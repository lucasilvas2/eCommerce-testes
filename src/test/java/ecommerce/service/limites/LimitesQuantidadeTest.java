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

public class LimitesQuantidadeTest {
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
    @DisplayName("TC-VL23: Quantidade -1 (Limite 0 L-1) deve lançar IllegalArgumentException")
    void deveLancarExcecaoQuandoQuantidadeNegativa() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new ItemCompra(1L, produto, -1L);
        });

        assertThat(exception.getMessage())
                .as("Mensagem de erro para quantidade negativa")
                .contains("Quantidade do item deve ser maior que zero.");
    }

    @Test
    @DisplayName("TC-VL24: Quantidade 0 (Limite 0 L) deve lançar IllegalArgumentException")
    void deveLancarExcecaoQuandoQuantidadeZero() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new ItemCompra(1L, produto, 0L);
        });

        assertThat(exception.getMessage())
                .as("Mensagem de erro para quantidade zero")
                .contains("Quantidade do item deve ser maior que zero.");
    }

    @Test
    @DisplayName("TC-VL25: Quantidade 1 (Limite 0 L+1) deve permitir cálculo normal")
    void deveCalcularTotalQuandoQuantidadeValida() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor calculado normalmente para quantidade 1")
                .isEqualByComparingTo("100.00");
    }
}