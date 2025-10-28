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
    private static final int ESCALA_DECIMAL = 2;

    // Descontos por quantidade de itens do mesmo tipo
    private static final int QTD_MINIMA_DESCONTO_PEQUENO = 3;
    private static final int QTD_MAXIMA_DESCONTO_PEQUENO = 4;
    private static final int QTD_MINIMA_DESCONTO_MEDIO = 5;
    private static final int QTD_MAXIMA_DESCONTO_MEDIO = 7;
    private static final int QTD_MINIMA_DESCONTO_GRANDE = 8;

    // Percentuais de desconto por quantidade
    private static final BigDecimal DESCONTO_PEQUENO = BigDecimal.valueOf(0.05);
    private static final BigDecimal DESCONTO_MEDIO = BigDecimal.valueOf(0.10);
    private static final BigDecimal DESCONTO_GRANDE = BigDecimal.valueOf(0.15);

    // Descontos por subtotal
    private static final BigDecimal LIMITE_SUBTOTAL_MEDIO = BigDecimal.valueOf(500);
    private static final BigDecimal LIMITE_SUBTOTAL_GRANDE = BigDecimal.valueOf(1000);
    private static final BigDecimal DESCONTO_SUBTOTAL_MEDIO = BigDecimal.valueOf(0.10);
    private static final BigDecimal DESCONTO_SUBTOTAL_GRANDE = BigDecimal.valueOf(0.20);

    // Constantes de frete
    private static final BigDecimal TAXA_FIXA_FRETE = BigDecimal.valueOf(12.00);
    private static final BigDecimal TAXA_ITEM_FRAGIL = BigDecimal.valueOf(5.00);
    private static final BigDecimal LIMITE_PESO_PEQUENO = BigDecimal.valueOf(5);
    private static final BigDecimal LIMITE_PESO_MEDIO = BigDecimal.valueOf(10);
    private static final BigDecimal LIMITE_PESO_GRANDE = BigDecimal.valueOf(50);
    private static final BigDecimal TAXA_PESO_PEQUENO = BigDecimal.valueOf(2.00);
    private static final BigDecimal TAXA_PESO_MEDIO = BigDecimal.valueOf(4.00);
    private static final BigDecimal TAXA_PESO_GRANDE = BigDecimal.valueOf(7.00);

    // Multiplicadores por região
    private static final BigDecimal MULT_REGIAO_SUDESTE = BigDecimal.valueOf(1.0);
    private static final BigDecimal MULT_REGIAO_SUL = BigDecimal.valueOf(1.05);
    private static final BigDecimal MULT_REGIAO_NORDESTE = BigDecimal.valueOf(1.10);
    private static final BigDecimal MULT_REGIAO_CENTRO_OESTE = BigDecimal.valueOf(1.20);
    private static final BigDecimal MULT_REGIAO_NORTE = BigDecimal.valueOf(1.30);

    // Descontos por tipo de cliente
    private static final BigDecimal DESCONTO_CLIENTE_BRONZE = BigDecimal.valueOf(0.0);
    private static final BigDecimal DESCONTO_CLIENTE_PRATA = BigDecimal.valueOf(0.5);
    private static final BigDecimal DESCONTO_CLIENTE_OURO = BigDecimal.valueOf(1.0);

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
        if (carrinho == null || cliente == null){
            throw new IllegalArgumentException("Carrinho ou cliente não podem ser nulos.");
        }

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
                .setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP);
	}


    public BigDecimal subtotal(CarrinhoDeCompras carrinho) {
        return carrinho.getItens().stream()
                .map(item -> item.getProduto().getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal descontoPorItemsDoMesmoTipo(CarrinhoDeCompras carrinho) {
        return carrinho.getItens().stream()
                .collect(Collectors.groupingBy(item -> item.getProduto().getTipo()))
                .values().stream()
                .map(itens -> {
                    int quantidadeItems = itens.stream()
                            .mapToInt(item -> item.getQuantidade().intValue())
                            .sum();
                    BigDecimal subtotal = itens.stream()
                            .map(item -> item.getProduto().getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal porcentagemDesconto = retornaPorcentagemDescontoPorItemDoMesmoTipo(quantidadeItems);

                    return subtotal.subtract(subtotal.multiply(porcentagemDesconto));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal retornaPorcentagemDescontoPorItemDoMesmoTipo(int quantidadeItems) {
        if (quantidadeItems >= QTD_MINIMA_DESCONTO_PEQUENO && quantidadeItems <= QTD_MAXIMA_DESCONTO_PEQUENO) {
            return DESCONTO_PEQUENO;
        }
        if (quantidadeItems >= QTD_MINIMA_DESCONTO_MEDIO && quantidadeItems <= QTD_MAXIMA_DESCONTO_MEDIO) {
            return DESCONTO_MEDIO;
        }
        if (quantidadeItems >= QTD_MINIMA_DESCONTO_GRANDE) {
            return DESCONTO_GRANDE;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal descontoPorSubtotal(BigDecimal subtotal) {
        BigDecimal desconto = BigDecimal.ZERO;
        if(subtotal.compareTo(LIMITE_SUBTOTAL_GRANDE) > 0 ) {
            desconto = DESCONTO_SUBTOTAL_GRANDE;
        } else if(subtotal.compareTo(LIMITE_SUBTOTAL_MEDIO) > 0 ) {
            desconto = DESCONTO_SUBTOTAL_MEDIO;
        }

        return subtotal.multiply(desconto);
    }

    public BigDecimal valorFrete(CarrinhoDeCompras carrinho, Regiao regiao) {
        BigDecimal pesoTotal = carrinho.getItens().stream()
                .map(item -> item.getProduto().pesoTributavel().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxaFragil = TAXA_ITEM_FRAGIL.multiply(
                BigDecimal.valueOf(carrinho.getItens().stream()
                        .filter(item -> item.getProduto().isFragil())
                        .mapToInt(item -> item.getQuantidade().intValue())
                        .sum())
        );

        if (pesoTotal.compareTo(LIMITE_PESO_PEQUENO) <= 0) {
            return taxaFragil;
        }

        BigDecimal baseRate;
        if (pesoTotal.compareTo(LIMITE_PESO_PEQUENO) > 0 && pesoTotal.compareTo(LIMITE_PESO_MEDIO) <= 0) {
            baseRate = TAXA_PESO_PEQUENO;
        } else if (pesoTotal.compareTo(LIMITE_PESO_MEDIO) > 0 && pesoTotal.compareTo(LIMITE_PESO_GRANDE) <= 0) {
            baseRate = TAXA_PESO_MEDIO;
        } else {
            baseRate = TAXA_PESO_GRANDE;
        }

        BigDecimal regionMultiplier = switch (regiao) {
            case SUDESTE -> MULT_REGIAO_SUDESTE;
            case SUL -> MULT_REGIAO_SUL;
            case NORDESTE -> MULT_REGIAO_NORDESTE;
            case CENTRO_OESTE -> MULT_REGIAO_CENTRO_OESTE;
            case NORTE -> MULT_REGIAO_NORTE;
        };

        return pesoTotal.multiply(baseRate).multiply(regionMultiplier).add(TAXA_FIXA_FRETE).add(taxaFragil);
    }

    public BigDecimal descontoFretePorTipoCliente(TipoCliente tipoCliente) {
        return switch (tipoCliente) {
            case BRONZE -> DESCONTO_CLIENTE_BRONZE;
            case PRATA -> DESCONTO_CLIENTE_PRATA;
            case OURO -> DESCONTO_CLIENTE_OURO;
        };
    }
}
