package ecommerce.service.limites;

import ecommerce.entity.*;
import ecommerce.service.CompraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LimitesSubtotalTest {

    private CompraService compraService;
    private CarrinhoDeCompras carrinho;
    private Cliente cliente;
    private Produto produto;
    private List<ItemCompra> itemCompras;

    @BeforeEach
    void setup() {
        compraService = new CompraService(null, null, null, null);
        cliente = new Cliente(1L, "Test", Regiao.SUDESTE, TipoCliente.BRONZE);
        carrinho = new CarrinhoDeCompras();
        itemCompras = new ArrayList<>();
    }

    @Test
    @DisplayName("TC-VL8: Subtotal R$ 499,99 (Limite R$ 500,00 L-1) deve resultar em 0% desconto")
    void deveCalcularSemDescontoQuandoSubtotalAbaixoLimite() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("499.99"),
                new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor sem desconto para subtotal R$ 499,99 (abaixo do limite)")
                .isEqualByComparingTo("499.99");
    }

    @Test
    @DisplayName("TC-VL9: Subtotal R$ 500,00 (Limite R$ 500,00 L) deve resultar em 0% desconto")
    void deveCalcularSemDescontoQuandoSubtotalNoLimite() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("500.00"),
                new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor sem desconto para subtotal R$ 500,00 (no limite)")
                .isEqualByComparingTo("500.00");
    }

    @Test
    @DisplayName("TC-VL10: Subtotal R$ 500,01 (Limite R$ 500,00 L+1) deve resultar em 10% desconto")
    void deveCalcularComDescontoDezPorcentoQuandoSubtotalAcimaLimite() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("500.01"),
                new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com 10% de desconto para subtotal R$ 500,01 (acima do limite)")
                .isEqualByComparingTo("450.01");
    }

    @Test
    @DisplayName("TC-VL11: Subtotal R$ 999,99 (Limite R$ 1000,00 L-1) deve resultar em 10% desconto")
    void deveCalcularComDescontoDezPorcentoQuandoSubtotalAbaixoLimiteMaior() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("999.99"),
                new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com 10% de desconto para subtotal R$ 999,99 (abaixo do limite maior)")
                .isEqualByComparingTo("899.99");
    }

    @Test
    @DisplayName("TC-VL12: Subtotal R$ 1000,00 (Limite R$ 1000,00 L) deve resultar em 10% desconto")
    void deveCalcularComDescontoDezPorcentoQuandoSubtotalNoLimiteMaior() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("1000.00"),
                new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com 10% de desconto para subtotal R$ 1000,00 (no limite maior)")
                .isEqualByComparingTo("900.00");
    }

    @Test
    @DisplayName("TC-VL13: Subtotal R$ 1000,01 (Limite R$ 1000,00 L+1) deve resultar em 20% desconto")
    void deveCalcularComDescontoVintePorcentoQuandoSubtotalAcimaLimiteMaior() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("1000.01"),
                new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com 20% de desconto para subtotal R$ 1000,01 (acima do limite maior)")
                .isEqualByComparingTo("800.01");
    }
}
