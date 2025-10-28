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

public class TabelaDeDecisaoRegrasDeNegocioTest {

    private CompraService compraService;
    private CarrinhoDeCompras carrinho;
    private Cliente cliente;
    private List<ItemCompra> itemCompras;

    @BeforeEach
    void setUp() {
        compraService = new CompraService(null, null, null, null);
        carrinho = new CarrinhoDeCompras();
        itemCompras = new ArrayList<>();
        cliente = new Cliente(1L, "Cliente", Regiao.SUDESTE, TipoCliente.BRONZE);
    }

    @Test
    @DisplayName("R6: Quando quantidade < 3 itens do mesmo tipo, sem desconto por tipo")
    void calcularCustoTotal_QuandoMenosDe3ItensMesmoTipo_NaoAplicaDesconto() {
        // Arrange
        var produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("1.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);

        itemCompras.add(new ItemCompra(1L, produto, 2L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("200.00");
    }

    @Test
    @DisplayName("R7: Quando 3-4 itens do mesmo tipo, aplica 5% de desconto")
    void calcularCustoTotal_Quando3A4ItensMesmoTipo_Aplica5PorcentoDesconto() {
        // Arrange
        var produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("1.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);

        itemCompras.add(new ItemCompra(1L, produto, 3L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("285.00");
    }

    @Test
    @DisplayName("R8: Quando 5-7 itens do mesmo tipo, aplica 10% de desconto")
    void calcularCustoTotal_Quando5A7ItensMesmoTipo_Aplica10PorcentoDesconto() {
        // Arrange
        var produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("1.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);

        itemCompras.add(new ItemCompra(1L, produto, 5L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("450.00");
    }

    @Test
    @DisplayName("R9: Quando 8 ou mais itens do mesmo tipo, aplica 15% de desconto")
    void calcularCustoTotal_Quando8OuMaisItensMesmoTipo_Aplica15PorcentoDesconto() {
        // Arrange
        var produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("1.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);

        itemCompras.add(new ItemCompra(1L, produto, 8L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("628.00"); // 800 - 15% - 10%
    }

    @Test
    @DisplayName("R10: Quando subtotal <= 500, não aplica desconto por valor")
    void calcularCustoTotal_QuandoSubtotalMenorIgual500_NaoAplicaDescontoPorValor() {
        // Arrange
        var produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("1.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);

        itemCompras.add(new ItemCompra(1L, produto, 2L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("200.00");
    }

    @Test
    @DisplayName("R11: Quando 500 < subtotal <= 1000, aplica 10% de desconto")
    void calcularCustoTotal_QuandoSubtotalEntre500E1000_Aplica10PorcentoDesconto() {
        // Arrange
        var produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("1.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);

        itemCompras.add(new ItemCompra(1L, produto, 6L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("504.00");
    }

    @Test
    @DisplayName("R12: Quando subtotal > 1000, aplica 20% de desconto")
    void calcularCustoTotal_QuandoSubtotalMaior1000_Aplica20PorcentoDesconto() {
        // Arrange
        var produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("1.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);

        itemCompras.add(new ItemCompra(1L, produto, 11L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("771.00");
    }
}
