package ecommerce.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import ecommerce.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecommerce.dto.CompraDTO;
import ecommerce.dto.DisponibilidadeDTO;
import ecommerce.dto.EstoqueBaixaDTO;
import ecommerce.dto.PagamentoDTO;
import ecommerce.external.IEstoqueExternal;
import ecommerce.external.IPagamentoExternal;
import jakarta.transaction.Transactional;

import static ecommerce.entity.Regiao.*;

@Service
public class CompraService
{

	private final CarrinhoDeComprasService carrinhoService;
	private final ClienteService clienteService;

	private final IEstoqueExternal estoqueExternal;
	private final IPagamentoExternal pagamentoExternal;
    private static final int DECIMAL_SCALE = 2;

	@Autowired
	public CompraService(CarrinhoDeComprasService carrinhoService, ClienteService clienteService,
			IEstoqueExternal estoqueExternal, IPagamentoExternal pagamentoExternal)
	{
		this.carrinhoService = carrinhoService;
		this.clienteService = clienteService;

		this.estoqueExternal = estoqueExternal;
		this.pagamentoExternal = pagamentoExternal;
	}

	@Transactional
	public CompraDTO finalizarCompra(Long carrinhoId, Long clienteId)
	{
		Cliente cliente = clienteService.buscarPorId(clienteId);
		CarrinhoDeCompras carrinho = carrinhoService.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente);

		List<Long> produtosIds = carrinho.getItens().stream().map(i -> i.getProduto().getId())
				.collect(Collectors.toList());
		List<Long> produtosQtds = carrinho.getItens().stream().map(i -> i.getQuantidade()).collect(Collectors.toList());

		DisponibilidadeDTO disponibilidade = estoqueExternal.verificarDisponibilidade(produtosIds, produtosQtds);

		if (!disponibilidade.disponivel())
		{
			throw new IllegalStateException("Itens fora de estoque.");
		}

		BigDecimal custoTotal = calcularCustoTotal(carrinho, cliente);

		PagamentoDTO pagamento = pagamentoExternal.autorizarPagamento(cliente.getId(), custoTotal.doubleValue());

		if (!pagamento.autorizado())
		{
			throw new IllegalStateException("Pagamento não autorizado.");
		}

		EstoqueBaixaDTO baixaDTO = estoqueExternal.darBaixa(produtosIds, produtosQtds);

		if (!baixaDTO.sucesso())
		{
			pagamentoExternal.cancelarPagamento(cliente.getId(), pagamento.transacaoId());
			throw new IllegalStateException("Erro ao dar baixa no estoque.");
		}

		CompraDTO compraDTO = new CompraDTO(true, pagamento.transacaoId(), "Compra finalizada com sucesso.");

		return compraDTO;
	} 

	public BigDecimal calcularCustoTotal(CarrinhoDeCompras carrinho, Cliente cliente)
	{
		BigDecimal subtotal = subtotal(carrinho);

        BigDecimal descontoItemsMesmoTipo = descontoPorItemsDoMesmoTipo(carrinho);

        BigDecimal descontoSubtotalotal = descontoPorSubtotal(subtotal);

        BigDecimal subtotalComDesconto = descontoItemsMesmoTipo.subtract(descontoSubtotalotal);
        BigDecimal valorFrete = valorFrete(carrinho, cliente.getRegiao());

        BigDecimal descontoFreteTipoCliente = descontoFretePorTipoCliente(cliente.getTipo());

        BigDecimal valorFreteComDesconto = valorFrete.subtract(
                valorFrete.multiply(descontoFreteTipoCliente)
        );

        return subtotalComDesconto
                .add(valorFreteComDesconto)
                .setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
	}


    public BigDecimal subtotal(CarrinhoDeCompras carrinho) {
        return carrinho.getItens().stream()
                .map(item -> item.getProduto().getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal descontoPorItemsDoMesmoTipo(CarrinhoDeCompras carrinho) {
        List<List<ItemCompra>> listaDeItensAgrupadosPorTipo = carrinho.getItens().stream()
                .collect(Collectors.groupingBy(item -> item.getProduto().getTipo()))
                .values()
                .stream()
                .filter(lista -> !lista.isEmpty())
                .toList();

        final BigDecimal[] subtotal = {BigDecimal.ZERO};
        listaDeItensAgrupadosPorTipo.forEach(itens -> {
            //quantidade de items do mesmo tipo
            int quantidadeItems = itens.stream()
                    .mapToInt(item -> item.getQuantidade().intValue())
                    .sum();
            BigDecimal subtotalItemsMesmoTipo = itens.stream()
                    .map(item -> item.getProduto().getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal desconto = BigDecimal.ZERO;
            if(quantidadeItems == 3 || quantidadeItems == 4) {
                desconto = BigDecimal.valueOf(0.05);
            } else if(quantidadeItems >= 5 && quantidadeItems <= 7) {
                desconto = BigDecimal.valueOf(0.10);
            }
            else if(quantidadeItems >= 8) {
                desconto = BigDecimal.valueOf(0.15);
            }

            //soma do subtotal com o desconto aplicado
            BigDecimal subtotalComDesconto = subtotalItemsMesmoTipo.subtract(
                    subtotalItemsMesmoTipo.multiply(desconto)
            );
            subtotal[0] = subtotal[0].add(subtotalComDesconto);

        });

        return subtotal[0];
    }

    public BigDecimal descontoPorSubtotal(BigDecimal subtotal) {
        BigDecimal desconto = BigDecimal.ZERO;
        if(subtotal.compareTo(BigDecimal.valueOf(1000)) > 0 ) {
            desconto = BigDecimal.valueOf(0.20);
        } else if(subtotal.compareTo(BigDecimal.valueOf(500)) > 0 ) {
            desconto = BigDecimal.valueOf(0.10);
        }

        return subtotal.multiply(desconto);
    }

    public BigDecimal valorFrete(CarrinhoDeCompras carrinho, Regiao regiao) {
        BigDecimal valorFrete;
        BigDecimal taxaFixa = new BigDecimal("12.00");

        BigDecimal pesoTotal = carrinho.getItens().stream()
                .map(item -> item.getProduto().pesoTributavel().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int quantidadeItensFragil = carrinho.getItens().stream()
                .filter(item -> item.getProduto().isFragil())
                .mapToInt(item -> item.getQuantidade().intValue())
                .sum();
        BigDecimal taxaFragil = BigDecimal.valueOf(5.00).multiply(BigDecimal.valueOf(quantidadeItensFragil));

        if (pesoTotal.compareTo(BigDecimal.valueOf(5)) <= 0) {
            return BigDecimal.ZERO.add(taxaFragil);
        }

        BigDecimal baseRate;
        if (pesoTotal.compareTo(BigDecimal.valueOf(5)) > 0 && pesoTotal.compareTo(BigDecimal.valueOf(10)) <= 0) {
            baseRate = BigDecimal.valueOf(2.00);
        } else if (pesoTotal.compareTo(BigDecimal.valueOf(10)) > 0 && pesoTotal.compareTo(BigDecimal.valueOf(50)) <= 0) {
            baseRate = BigDecimal.valueOf(4.00);
        } else {
            // fallback for > 50 (adjust as needed)
            baseRate = BigDecimal.valueOf(7.00);
        }

        BigDecimal regionMultiplier = switch (regiao) {
            case SUDESTE -> BigDecimal.valueOf(1.0);
            case SUL -> BigDecimal.valueOf(1.05);
            case NORDESTE -> BigDecimal.valueOf(1.10);
            case CENTRO_OESTE -> BigDecimal.valueOf(1.20);
            case NORTE -> BigDecimal.valueOf(1.30);
        };

        return pesoTotal.multiply(baseRate).multiply(regionMultiplier).add(taxaFixa).add(taxaFragil);
    }



    public BigDecimal descontoFretePorTipoCliente(TipoCliente tipoCliente) {
        BigDecimal desconto = BigDecimal.ZERO;

        switch (tipoCliente) {
            case BRONZE -> desconto = BigDecimal.valueOf(0.0);
            case PRATA -> desconto = BigDecimal.valueOf(0.5);
            case OURO -> desconto = BigDecimal.valueOf(1.0);
        }

        return desconto;
    }
}
