package ecommerce.service.limites;

import ecommerce.entity.*;
import ecommerce.service.CompraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LimitesClienteRegiaoVaziaTest {

    private CompraService compraService;
    private CarrinhoDeCompras carrinho;
    private Cliente cliente;
    private Produto produto;
    private List<ItemCompra> itemCompras;

    @BeforeEach
    void setup() {
        compraService = new CompraService(null, null, null, null);
        carrinho = new CarrinhoDeCompras();
        itemCompras = new ArrayList<>();
        produto = new Produto(
                1L,
                "Test",
                "Test Description",
                new BigDecimal("100.00"),
                new BigDecimal("1.0"),
                new BigDecimal("10.0"),
                new BigDecimal("10.0"),
                new BigDecimal("10.0"),
                false,
                TipoProduto.ELETRONICO
        );
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);
    }

    @Test
    @DisplayName("TC-VL29: Cliente nulo deve lançar IllegalArgumentException")
    void deveLancarExcecaoQuandoClienteNulo() {
        cliente = null;

        assertThatThrownBy(() -> compraService.calcularCustoTotal(carrinho, cliente))
                .as("Deve lançar IllegalArgumentException quando cliente for nulo")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("TC-VL30: Região nula deve lançar IllegalArgumentException")
    void deveLancarExcecaoQuandoRegiaoNula() {
        assertThatThrownBy(() -> {
            new Cliente(1L, "Test", null, TipoCliente.BRONZE);
        })
                .as("Deve lançar IllegalArgumentException quando região for nula")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A região não pode ser nula.");
    }
}
