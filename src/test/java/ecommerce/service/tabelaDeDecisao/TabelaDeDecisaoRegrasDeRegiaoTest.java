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

    public class TabelaDeDecisaoRegrasDeRegiaoTest {

        private CompraService compraService;
        private CarrinhoDeCompras carrinho;
        private Cliente cliente;
        private List<ItemCompra> itemCompras;
        private Produto produto;

        @BeforeEach
        void setUp() {
            compraService = new CompraService(null, null, null, null);
            carrinho = new CarrinhoDeCompras();
            itemCompras = new ArrayList<>();

            produto = new Produto(1L, "Produto", "Produto teste",
                    new BigDecimal("100.00"), new BigDecimal("6.0"),
                    new BigDecimal("10"), new BigDecimal("10"),
                    new BigDecimal("10"), false, TipoProduto.ELETRONICO);
        }

        @Test
        @DisplayName("R19: Quando região é Sudeste, aplica multiplicador 1.0")
        void calcularCustoTotal_QuandoRegiaoSudeste_AplicaMultiplicador1() {
            // Arrange
            cliente = new Cliente(1L, "Cliente", Regiao.SUDESTE, TipoCliente.BRONZE);
            itemCompras.add(new ItemCompra(1L, produto, 1L));
            carrinho.setItens(itemCompras);

            // Act
            BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

            // Assert
            assertThat(resultado).isEqualByComparingTo("124.00");
        }

        @Test
        @DisplayName("R20: Quando região é Sul, aplica multiplicador 1.05")
        void calcularCustoTotal_QuandoRegiaoSul_AplicaMultiplicador1_05() {
            // Arrange
            cliente = new Cliente(1L, "Cliente", Regiao.SUL, TipoCliente.BRONZE);
            itemCompras.add(new ItemCompra(1L, produto, 1L));
            carrinho.setItens(itemCompras);

            // Act
            BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

            // Assert
            assertThat(resultado).isEqualByComparingTo("124.60");
        }

        @Test
        @DisplayName("R21: Quando região é Nordeste, aplica multiplicador 1.10")
        void calcularCustoTotal_QuandoRegiaoNordeste_AplicaMultiplicador1_10() {
            // Arrange
            cliente = new Cliente(1L, "Cliente", Regiao.NORDESTE, TipoCliente.BRONZE);
            itemCompras.add(new ItemCompra(1L, produto, 1L));
            carrinho.setItens(itemCompras);

            // Act
            BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

            // Assert
            assertThat(resultado).isEqualByComparingTo("125.20");
        }

        @Test
        @DisplayName("R22: Quando região é Centro-Oeste, aplica multiplicador 1.20")
        void calcularCustoTotal_QuandoRegiaoCentroOeste_AplicaMultiplicador1_20() {
            // Arrange
            cliente = new Cliente(1L, "Cliente", Regiao.CENTRO_OESTE, TipoCliente.BRONZE);
            itemCompras.add(new ItemCompra(1L, produto, 1L));
            carrinho.setItens(itemCompras);

            // Act
            BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

            // Assert
            assertThat(resultado).isEqualByComparingTo("126.40");
        }

        @Test
        @DisplayName("R23: Quando região é Norte, aplica multiplicador 1.30")
        void calcularCustoTotal_QuandoRegiaoNorte_AplicaMultiplicador1_30() {
            // Arrange
            cliente = new Cliente(1L, "Cliente", Regiao.NORTE, TipoCliente.BRONZE);
            itemCompras.add(new ItemCompra(1L, produto, 1L));
            carrinho.setItens(itemCompras);

            // Act
            BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

            // Assert
            assertThat(resultado).isEqualByComparingTo("127.60");
        }

        @Test
        @DisplayName("R24: Quando cliente é Ouro, frete é zero independente da região")
        void calcularCustoTotal_QuandoClienteOuro_FreteZeroIndependenteDaRegiao() {
            // Arrange
            cliente = new Cliente(1L, "Cliente", Regiao.NORTE, TipoCliente.OURO);
            itemCompras.add(new ItemCompra(1L, produto, 1L));
            carrinho.setItens(itemCompras);

            // Act
            BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

            // Assert
            assertThat(resultado).isEqualByComparingTo("100.00");
        }

        @Test
        @DisplayName("R25: Quando cliente é Prata, aplica 50% desconto no frete após multiplicador regional")
        void calcularCustoTotal_QuandoClientePrata_Aplica50PorcentoDescontoNoFrete() {
            // Arrange
            cliente = new Cliente(1L, "Cliente", Regiao.NORTE, TipoCliente.PRATA);
            itemCompras.add(new ItemCompra(1L, produto, 1L));
            carrinho.setItens(itemCompras);

            // Act
            BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

            // Assert
            assertThat(resultado).isEqualByComparingTo("113.80");
        }
    }