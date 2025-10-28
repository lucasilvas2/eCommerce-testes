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
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LimitesPrecoTest {

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
    @DisplayName("TC-VL26: Preço -0,01 (Limite 0,00 L-1) deve lançar exceção")
    void deveLancarExcecaoQuandoPrecoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> {
            produto = new Produto(1L, "Test", "Test Description", new BigDecimal("-0.01"),
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                    new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        }, "Deve lançar exceção para preço negativo");
    }

    @Test
    @DisplayName("TC-VL27: Preço 0,00 (Limite 0,00 L) deve calcular normalmente")
    void deveCalcularTotalQuandoPrecoZero() {
        produto = new Produto(1L, "Test", "Test Description", BigDecimal.ZERO,
                new BigDecimal("5.5"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor calculado normalmente para preço 0,00")
                .isEqualByComparingTo("23.00"); // Apenas taxa fixa do frete
    }

    @Test
    @DisplayName("TC-VL28: Preço 0,01 (Limite 0,00 L+1) deve calcular normalmente")
    void deveCalcularTotalQuandoPrecoPositivo() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("0.01"),
                new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor calculado normalmente para preço 0,01")
                .isEqualByComparingTo("0.01"); // 0.01
    }
}
