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

public class TabelaDeDecisaoRegrasDeClienteEFragilidadeTest {

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
    @DisplayName("R21: Cliente Bronze não tem desconto no frete")
    void calcularCustoTotal_QuandoClienteBronze_NaoTemDescontoNoFrete() {
        // Arrange
        cliente.setTipo(TipoCliente.BRONZE);
        Produto produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("6.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);
        itemCompras.add(new ItemCompra(1L, produto, 1L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("124.00");
    }

    @Test
    @DisplayName("R22: Cliente Prata tem 50% de desconto no frete")
    void calcularCustoTotal_QuandoClientePrata_Tem50PorcentoDescontoNoFrete() {
        // Arrange
        cliente.setTipo(TipoCliente.PRATA);
        Produto produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("6.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);
        itemCompras.add(new ItemCompra(1L, produto, 1L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("112.00");
    }

    @Test
    @DisplayName("R23: Cliente Ouro tem 100% de desconto no frete")
    void calcularCustoTotal_QuandoClienteOuro_Tem100PorcentoDescontoNoFrete() {
        // Arrange
        cliente.setTipo(TipoCliente.OURO);
        Produto produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("6.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);
        itemCompras.add(new ItemCompra(1L, produto, 1L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("R24: Produto não frágil não aplica taxa adicional")
    void calcularCustoTotal_QuandoProdutoNaoFragil_NaoAplicaTaxaAdicional() {
        // Arrange
        Produto produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("6.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), false, TipoProduto.ELETRONICO);
        itemCompras.add(new ItemCompra(1L, produto, 1L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isEqualByComparingTo("124.00");
    }

    @Test
    @DisplayName("R25: Produto frágil aplica taxa adicional")
    void calcularCustoTotal_QuandoProdutoFragil_AplicaTaxaAdicional() {
        // Arrange
        Produto produto = new Produto(1L, "Produto", "Produto teste",
                new BigDecimal("100.00"), new BigDecimal("6.0"),
                new BigDecimal("10"), new BigDecimal("10"),
                new BigDecimal("10"), true, TipoProduto.ELETRONICO);
        itemCompras.add(new ItemCompra(1L, produto, 1L));
        carrinho.setItens(itemCompras);

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertThat(resultado).isGreaterThan(new BigDecimal("124.00"));
    }

}
