package ecommerce.service.tabelaDeDecisao;

import ecommerce.entity.*;
import ecommerce.service.CompraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TabelaDeDecisaoRegrasDeValidacaoTest {

    private CompraService compraService;
    private CarrinhoDeCompras carrinho;
    private Cliente cliente;
    private Produto produto;
    private List<ItemCompra> itemCompras;

    @BeforeEach
    void setUp() {
        compraService = new CompraService(null, null, null, null);
        carrinho = new CarrinhoDeCompras();
        itemCompras = new ArrayList<>();
    }

    @Test
    @DisplayName("R1: Deve lançar exceção quando preço for negativo")
    void calcularCustoTotal_QuandoPrecoNegativo_DeveLancarExcecao() {
        cliente = new Cliente(1L, "Cliente", Regiao.SUDESTE, TipoCliente.BRONZE);


        assertThatThrownBy(() -> {
            produto = new Produto(
                    1L,
                    "Produto",
                    "Produto de teste",
                    new BigDecimal("-10.00"),
                    new BigDecimal("1.0"),
                    new BigDecimal("10"),
                    new BigDecimal("10"),
                    new BigDecimal("10"),
                    false,
                    TipoProduto.ELETRONICO
            );
        })
                .as("Deve lançar exceção para preço negativo")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O preço não pode ser negativo.");
    }

    @Test
    @DisplayName("R2: Deve lançar exceção quando quantidade for menor ou igual a zero")
    void calcularCustoTotal_QuandoQuantidadeInvalida_DeveLancarExcecao() {
        // Arrange
        cliente = new Cliente(1L, "Cliente", Regiao.SUDESTE, TipoCliente.BRONZE);
        produto = new Produto(
                1L,
                "Produto",
                "Produto de teste",
                new BigDecimal("10.00"),
                new BigDecimal("1.0"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                false,
                TipoProduto.ELETRONICO
        );

        // Act & Assert
        assertThatThrownBy(() -> {
            ItemCompra item = new ItemCompra(1L, produto, 0L);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade do item deve ser maior que zero.");
    }

    @Test
    @DisplayName("R3: Deve lançar exceção quando cliente for nulo")
    void calcularCustoTotal_QuandoClienteNulo_DeveLancarExcecao() {
        cliente = null;
        // Arrange
        produto = new Produto(
                1L,
                "Produto",
                "Produto de teste",
                new BigDecimal("10.00"),
                new BigDecimal("1.0"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                false,
                TipoProduto.ELETRONICO
        );

        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        // Act & Assert
        assertThatThrownBy(() -> compraService.calcularCustoTotal(carrinho, cliente))
                .as("Deve lançar exceção quando cliente for nulo")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Carrinho ou cliente não podem ser nulos.");
    }

    @Test
    @DisplayName("R4: Deve lançar exceção quando região for nula")
    void calcularCustoTotal_QuandoRegiaoNula_DeveLancarExcecao() {
        // Act & Assert
        assertThatThrownBy(() -> {
            cliente = new Cliente(1L, "Cliente", null, TipoCliente.BRONZE);
        })
                .as("Deve lançar exceção quando região do cliente for nula")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A região não pode ser nula.");
    }

    @Test
    @DisplayName("R5: Caso válido - Todos os parâmetros corretos")
    void calcularCustoTotal_QuandoParametrosValidos_DeveCalcularCorretamente() {
        // Arrange
        cliente = new Cliente(1L, "Cliente", Regiao.SUDESTE, TipoCliente.BRONZE);
        produto = new Produto(
                1L,
                "Produto",
                "Produto de teste",
                new BigDecimal("10.00"),
                new BigDecimal("1.0"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                false,
                TipoProduto.ELETRONICO
        );

        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.compareTo(BigDecimal.ZERO)).isGreaterThan(0);
    }
}