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

public class TabelaDeDecisaoRegrasDePesoTest {

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
    @DisplayName("R13: Quando peso <= 5kg, frete é isento")
    void calcularCustoTotal_QuandoPesoMenorIgual5kg_FreteIsento() {
        // Arrange
        var produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("2.5"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);

        itemCompras.add(new ItemCompra(1L, produto, 2L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("200.00"); // sem frete
    }

    @Test
    @DisplayName("R14: Quando 5kg < peso <= 10kg, frete R$2/kg + taxa fixa")
    void calcularCustoTotal_QuandoPesoEntre5e10kg_FreteBasico() {
        // Arrange
        var produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("3.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);

        itemCompras.add(new ItemCompra(1L, produto, 3L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("315.00");
    }

    @Test
    @DisplayName("R15: Quando 10kg < peso <= 50kg, frete R$4/kg + taxa fixa")
    void calcularCustoTotal_QuandoPesoEntre10e50kg_FreteMedio() {
        // Arrange
        var produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("5.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);

        itemCompras.add(new ItemCompra(1L, produto, 3L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("357.00");
    }

    @Test
    @DisplayName("R16: Quando peso > 50kg, frete R$7/kg + taxa fixa")
    void calcularCustoTotal_QuandoPesoMaior50kg_FreteAlto() {
        // Arrange
        var produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("17.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);

        itemCompras.add(new ItemCompra(1L, produto, 3L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("654.00");
    }

    @Test
    @DisplayName("R17: Quando item é frágil, adiciona taxa de R$5 por item")
    void calcularCustoTotal_QuandoItemFragil_AdicionaTaxaEspecial() {
        // Arrange
        var produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("2.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), true, TipoProduto.ELETRONICO);

        itemCompras.add(new ItemCompra(1L, produto, 2L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("210.00");
    }

    @Test
    @DisplayName("R18: Quando região é diferente do Sudeste, aplica multiplicador regional")
    void calcularCustoTotal_QuandoRegiaoNorte_AplicaMultiplicadorRegional() {
        // Arrange
        cliente = new Cliente(1L, "Cliente", Regiao.NORTE, TipoCliente.BRONZE);
        var produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("3.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);

        itemCompras.add(new ItemCompra(1L, produto, 3L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("320.40");
    }
}