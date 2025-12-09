package ecommerce.service;

import ecommerce.dto.CompraDTO;
import ecommerce.dto.DisponibilidadeDTO;
import ecommerce.dto.EstoqueBaixaDTO;
import ecommerce.dto.PagamentoDTO;
import ecommerce.entity.CarrinhoDeCompras;
import ecommerce.entity.Cliente;
import ecommerce.entity.Regiao;
import ecommerce.entity.TipoCliente;
import ecommerce.external.IEstoqueExternal;
import ecommerce.external.IPagamentoExternal;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompraSimplificadaService
{

	private final CarrinhoDeComprasService carrinhoService;
	private final ClienteService clienteService;

	private final IEstoqueExternal estoqueExternal;
	private final IPagamentoExternal pagamentoExternal;
    private static final int ESCALA_DECIMAL = 2;

    // Descontos por subtotal
    private static final BigDecimal LIMITE_SUBTOTAL_MEDIO = BigDecimal.valueOf(500);
    private static final BigDecimal LIMITE_SUBTOTAL_GRANDE = BigDecimal.valueOf(1000);
    private static final BigDecimal DESCONTO_SUBTOTAL_MEDIO = BigDecimal.valueOf(0.10);
    private static final BigDecimal DESCONTO_SUBTOTAL_GRANDE = BigDecimal.valueOf(0.20);

    // Constantes de frete
    private static final BigDecimal TAXA_ITEM_FRAGIL = BigDecimal.valueOf(5.00);
    private static final BigDecimal LIMITE_PESO_PEQUENO = BigDecimal.valueOf(5);
    private static final BigDecimal LIMITE_PESO_MEDIO = BigDecimal.valueOf(10);
    private static final BigDecimal LIMITE_PESO_GRANDE = BigDecimal.valueOf(50);
    private static final BigDecimal TAXA_PESO_PEQUENO = BigDecimal.valueOf(2.00);
    private static final BigDecimal TAXA_PESO_MEDIO = BigDecimal.valueOf(4.00);
    private static final BigDecimal TAXA_PESO_GRANDE = BigDecimal.valueOf(7.00);


	@Autowired
	public CompraSimplificadaService(CarrinhoDeComprasService carrinhoService, ClienteService clienteService,
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

        BigDecimal descontoSubtotal = descontoPorSubtotal(subtotal);

        BigDecimal subtotalComDesconto = subtotal.subtract(descontoSubtotal);
        BigDecimal valorFrete = valorFrete(carrinho);

        return subtotalComDesconto
                .add(valorFrete)
                .setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP);
	}


    public BigDecimal subtotal(CarrinhoDeCompras carrinho) {
        return carrinho.getItens().stream()
                .map(item -> item.getProduto().getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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

    public BigDecimal valorFrete(CarrinhoDeCompras carrinho) {
        BigDecimal pesoTotal = carrinho.getItens().stream()
                .map(item -> item.getProduto().pesoTributavel().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxaFragil = TAXA_ITEM_FRAGIL.multiply(
                BigDecimal.valueOf(carrinho.getItens().stream()
                        .filter(item -> item.getProduto().isFragil())
                        .mapToInt(item -> item.getQuantidade().intValue())
                        .sum())
        );



        BigDecimal baseRate;
        if (pesoTotal.compareTo(LIMITE_PESO_PEQUENO) <= 0) {
            return taxaFragil;
        } else if (pesoTotal.compareTo(LIMITE_PESO_MEDIO) <= 0) {
            baseRate = TAXA_PESO_PEQUENO;
        } else if (pesoTotal.compareTo(LIMITE_PESO_GRANDE) <= 0) {
            baseRate = TAXA_PESO_MEDIO;
        } else {
            baseRate = TAXA_PESO_GRANDE;
        }


        return pesoTotal.multiply(baseRate).add(taxaFragil);
    }

}
