package ecommerce.service;

import ecommerce.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParticoesEquivalenciaTest {
    @ParameterizedTest(name = "price={0}, qty={1} -> expected={2}")
    @CsvSource({
            "100.00, 3, 285.00",   // 5% discount case from existing tests
            "50.00, 5, 225.00",    // 10% discount
            "25.00, 8, 170.00",    // 15% discount
            "100.00, 1, 100.00",  // no discount
            "100.00, 5, 450.00",  // 10% discount
    })
    @DisplayName("Test Calcular Custo Total com Desconto por Quantidade de Itens - Partições de Equivalência")
    public void testCalcularCustoTotalQuantidadeDeItensParameterized(String unitPrice, long qty, String expected) {
        CompraService service = new CompraService(null, null, null, null);

        Cliente cliente = new Cliente(1L, "Cliente Teste", Regiao.NORDESTE, TipoCliente.OURO);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();

        Produto produto = new Produto(
                1L,
                "Produto Param",
                "Descricao",
                new BigDecimal(unitPrice),
                new BigDecimal("0.05"),
                new BigDecimal("1.0"),
                new BigDecimal("5.0"),
                new BigDecimal("3.0"),
                false,
                TipoProduto.ELETRONICO
        );

        ItemCompra item = new ItemCompra(1L, produto, qty);
        carrinho.setItens(Collections.singletonList(item));

        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertThat(custoTotal).as("Custo Total da Compra").isEqualByComparingTo(new BigDecimal(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "25.00, 1, ELETRONICO, 100.00, 2, ROUPA, 50.00, 3, LIVRO, 367.50",
            "150.00, 2, ELETRONICO, 75.00, 3, MOVEL, 100.00, 1, ALIMENTO, 551.25",
            "100.00, 3, LIVRO, 80.00, 2, ROUPA, 60.00, 5, ELETRONICO, 639.00",
    })
    @DisplayName("Test Calcular Custo Total com Desconto por Quantidade de Itens - Múltiplos Tipos de Itens")
    public void testCalcularCustoTotalComDescontoMultiplosTiposDeItenParameterized(
            String price1, long qty1, TipoProduto type1,
            String price2, long qty2, TipoProduto type2,
            String price3, long qty3, TipoProduto type3,
            String expected
    ) {
        CompraService service = new CompraService(null, null, null, null);

        Cliente cliente = new Cliente(1L, "Cliente Teste", Regiao.NORDESTE, TipoCliente.OURO);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();

        List<ItemCompra> itens = new ArrayList<>();

        Produto produto1 = new Produto(
                1L,
                "Produto 1",
                "Descricao",
                new BigDecimal(price1),
                new BigDecimal("6.0"),
                new BigDecimal("10.0"),
                new BigDecimal("5.0"),
                new BigDecimal("3.0"),
                false,
                type1
        );
        ItemCompra item1 = new ItemCompra(1L, produto1, qty1);
        itens.add(item1);

        Produto produto2 = new Produto(
                2L,
                "Produto 2",
                "Descricao",
                new BigDecimal(price2),
                new BigDecimal("15.0"),
                new BigDecimal("5.0"),
                new BigDecimal("2.0"),
                new BigDecimal("1.0"),
                false,
                type2
        );
        ItemCompra item2 = new ItemCompra(2L, produto2, qty2);
        itens.add(item2);

        Produto produto3 = new Produto(
                3L,
                "Produto 3",
                "Descricao",
                new BigDecimal(price3),
                new BigDecimal("51.0"),
                new BigDecimal("5.0"),
                new BigDecimal("2.0"),
                new BigDecimal("1.0"),
                false,
                type3
        );
        ItemCompra item3 = new ItemCompra(3L, produto3, qty3);
        itens.add(item3);

        carrinho.setItens(itens);
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertThat(custoTotal).as("Custo Total da Compra").isEqualByComparingTo(new BigDecimal(expected));
    }

    @ParameterizedTest(name = "price={0}, qty={1} -> expected={2}")
    @CsvSource({
            "200.00, 2, 400.00",
            "100.00, 6, 540.00",
            "525.00, 2, 840.00",
            "200.00, 10, 1480.00",
    })
    @DisplayName("Test Calcular Custo Total com Desconto por Subtotal - Partições de Equivalência")
    public void testCalcularCustoTotalDescontoSubtotalParameterized(String unitPrice, long qty, String expected) {
        CompraService service = new CompraService(null, null, null, null);

        Cliente cliente = new Cliente(1L, "Cliente Teste", Regiao.NORDESTE, TipoCliente.OURO);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();

        List<ItemCompra> itens = new ArrayList<>();
        for (int i = 0; i < qty; i++) {
            TipoProduto tipoProdutoRandom = switch (i) {
                case 0 -> TipoProduto.ELETRONICO;
                case 1 -> TipoProduto.ROUPA;
                case 2 -> TipoProduto.ALIMENTO;
                case 3 -> TipoProduto.LIVRO;
                default -> TipoProduto.MOVEL;
            };

            Produto produto = new Produto(
                    1L + i,
                    "Produto Param " + i,
                    "Descricao",
                    new BigDecimal(unitPrice),
                    new BigDecimal("2.0"),
                    new BigDecimal("1.0"),
                    new BigDecimal("5.0"),
                    new BigDecimal("3.0"),
                    false,
                    tipoProdutoRandom
            );

            ItemCompra item = new ItemCompra(1L + i, produto, 1L);
            itens.add(item);
        }

        carrinho.setItens(itens);
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertThat(custoTotal).as("Custo Total da Compra").isEqualByComparingTo(new BigDecimal(expected));
    }

    @ParameterizedTest(name = "price={0}, qty={1} -> expected={2}")
    @CsvSource({
            "200.00, 2, 1.00, 0.5, 1.0, 2.0, 400.00",
            "50.00, 2, 4, 0.3, 0.4, 0.5, 128.00",
            "10.00, 2, 15, 0.3, 0.4, 0.5, 152.00",
            "150.00, 1, 55, 1.0, 1.0, 1.0, 547.00",
            "100.00, 2, 0.5, 75.0, 80.0, 50.0, 912.00",
    })
    @DisplayName("Test Calcular Custo Total com Valor do Frete por Peso - Partições de Equivalência")
    public void testValorDoFretePesoParameterized(
            String unitPrice,
            long qty,
            BigDecimal pesoFisico,
            BigDecimal comprimento,
            BigDecimal largura,
            BigDecimal altura,
            String expected
    ) {
        CompraService service = new CompraService(null, null, null, null);

        Cliente cliente = new Cliente(1L, "Cliente Teste", Regiao.SUDESTE, TipoCliente.BRONZE);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();

        Produto produto = new Produto(
                1L,
                "Produto Param",
                "Descricao",
                new BigDecimal(unitPrice),
                pesoFisico,
                comprimento,
                largura,
                altura,
                false,
                TipoProduto.ELETRONICO
        );

        ItemCompra item = new ItemCompra(1L, produto, qty);
        carrinho.setItens(Collections.singletonList(item));
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertThat(custoTotal).as("Custo Total da Compra").isEqualByComparingTo(new BigDecimal(expected));
    }

    @ParameterizedTest(name = "price={0}, qty={1}, peso={2}, comprimento={3}, largura={4}, altura={5}, fragil={6} -> expected={7}")
    @CsvSource({
            "50.00, 3, 1.00, 0.5, 1.0, 2.0, true, 157.50",
            "50.00, 2, 4, 0.3, 0.4, 0.5, false, 128.00",
    })
    @DisplayName("Test Calcular Custo Total com Valor do Frete por Fragilidade - Partições de Equivalência")
    public void testValorDoFreteItensFragilidadeParameterized(
            String unitPrice,
            long qty,
            BigDecimal pesoFisico,
            BigDecimal comprimento,
            BigDecimal largura,
            BigDecimal altura,
            Boolean fragil,
            String expected
    ) {
        CompraService service = new CompraService(null, null, null, null);

        Cliente cliente = new Cliente(1L, "Cliente Teste", Regiao.SUDESTE, TipoCliente.BRONZE);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();

        Produto produto = new Produto(
                1L,
                "Produto Param",
                "Descricao",
                new BigDecimal(unitPrice),
                pesoFisico,
                comprimento,
                largura,
                altura,
                fragil,
                TipoProduto.ELETRONICO
        );

        ItemCompra item = new ItemCompra(1L, produto, qty);
        carrinho.setItens(Collections.singletonList(item));
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertThat(custoTotal).as("Custo Total da Compra").isEqualByComparingTo(new BigDecimal(expected));
    }

    @ParameterizedTest(name = "price={0}, qty={1}, peso={2}, comprimento={3}, largura={4}, altura={5}, regiao={6} -> expected={7}")
    @CsvSource({
            "50.00, 3, 1.00, 0.5, 1.0, 2.0, SUDESTE, 142.50",
            "50.00, 2, 4.00, 0.5, 1.0, 2.0, SUL, 128.80",
            "50.00, 2, 4.50, 0.5, 1.0, 2.0, NORDESTE, 131.80",
            "25.00, 1, 10.00, 0.5, 1.0, 2.0, CENTRO_OESTE, 61.00",
            "50.00, 2, 12.00, 0.5, 1.0, 2.0, NORTE, 236.80",
    })
    @DisplayName("Test Calcular Custo Total com Valor do Frete por Região - Partições de Equivalência")
    public void testValorFretePorRegiaoParameterized(
            String unitPrice,
            long qty,
            BigDecimal pesoFisico,
            BigDecimal comprimento,
            BigDecimal largura,
            BigDecimal altura,
            Regiao regiao,
            String expected
    ) {
        CompraService service = new CompraService(null, null, null, null);

        Cliente cliente = new Cliente(1L, "Cliente Teste", regiao, TipoCliente.BRONZE);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();

        Produto produto = new Produto(
                1L,
                "Produto Param",
                "Descricao",
                new BigDecimal(unitPrice),
                pesoFisico,
                comprimento,
                largura,
                altura,
                false,
                TipoProduto.ELETRONICO
        );

        ItemCompra item = new ItemCompra(1L, produto, qty);
        carrinho.setItens(Collections.singletonList(item));
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertThat(custoTotal).as("Custo Total da Compra").isEqualByComparingTo(new BigDecimal(expected));
    }

    @ParameterizedTest(name = "price={0}, qty={1}, peso={2}, comprimento={3}, largura={4}, altura={5}, tipoCliente={6} -> expected={7}")
    @CsvSource({
            "50.00, 2, 10.00, 0.5, 1.0, 2.0, OURO, 100.00",
            "50.00, 2, 10.00, 0.5, 1.0, 2.0, PRATA, 146.00",
            "50.00, 2, 10.00, 0.5, 1.0, 2.0, BRONZE, 192.00",
    })
    @DisplayName("Test Calcular Custo Total com Valor do Frete por Tipo de Cliente - Partições de Equivalência")
    public void testValorFretePorTipoClienteParameterized(
            String unitPrice,
            long qty,
            BigDecimal pesoFisico,
            BigDecimal comprimento,
            BigDecimal largura,
            BigDecimal altura,
            TipoCliente tipoCliente,
            String expected
    ) {
        CompraService service = new CompraService(null, null, null, null);

        Cliente cliente = new Cliente(1L, "Cliente Teste", Regiao.SUDESTE, tipoCliente);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();

        Produto produto = new Produto(
                1L,
                "Produto Param",
                "Descricao",
                new BigDecimal(unitPrice),
                pesoFisico,
                comprimento,
                largura,
                altura,
                false,
                TipoProduto.ELETRONICO
        );

        ItemCompra item = new ItemCompra(1L, produto, qty);
        carrinho.setItens(Collections.singletonList(item));
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertThat(custoTotal).as("Custo Total da Compra").isEqualByComparingTo(new BigDecimal(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "-1, Pedro, SUDESTE, BRONZE",
            "2, , NORTE, PRATA",
            "3, Maria, , OURO",
            ", Carlos, NORDESTE, BRONZE",
    })
    @DisplayName("Test Cliente com Valores Inválidos - Partições de Equivalência")
    public void testClienteValoresInvalidosParameterized(
            Long clienteId,
            String clienteNome,
            Regiao regiao,
            TipoCliente tipoCliente
    ) {

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            Cliente cliente = new Cliente(clienteId, clienteNome, regiao, tipoCliente);
        });
    }

    @ParameterizedTest
    @CsvSource({
            "-1, Produto Teste, Descricao, 100.00, 2.0, 10.0, 5.0, 3.0, false, ELETRONICO, 1, 1",
            ", Produto Teste, Descricao, 100.00, 2.0, 10.0, 5.0, 3.0, false, ELETRONICO, 1, 1",
            "2, , Descricao, 50.00, 1.0, 5.0, 2.0, 1.0, true, ROUPA, 1, 1",
            "3, Produto Teste, Descricao, -10.00, 1.0, 5.0, 2.0, 1.0, false, ELETRONICO, 1, 1",
            "4, Produto Teste, Descricao, 50.00, -2.0, 5.0, 2.0, 1.0, false, MOVEL, 1, 1",
            "5, Produto Teste, Descricao, 50.00, 2.0, -10.0, 2.0, 1.0, false, MOVEL, 1, 1",
            "6, Produto Teste, Descricao, 50.00, 2.0, 10.0, -5.0, 1.0, false, MOVEL, 1, 1",
            "7, Produto Teste, Descricao, 50.00, 2.0, 10.0, 5.0, -3.0, false, MOVEL, 1, 1",
            "8, Produto Teste, Descricao, 50.00, 2.0, 10.0, 5.0, 3.0, false, ELETRONICO, -1, 1",
            "9, Produto Teste, Descricao, 50.00, 2.0, 10.0, 5.0, 3.0, false, ELETRONICO, 1, -5",
            "10, Produto Teste, Descricao, 50.00, 2.0, 10.0, 5.0, 3.0, false, ELETRONICO, , 1",
            "11, Produto Teste, Descricao, 50.00, 2.0, 10.0, 5.0, 3.0, false, ELETRONICO, 1, ",
            "12, Produto Teste, Descricao, 50.00, 2.0, 10.0, 5.0, 3.0, false, ELETRONICO, 1, 0",

    })
    @DisplayName("Test Produto com Valores Inválidos - Partições de Equivalência")
    public void testProdutoValoresInvalidosParameterized(
            Long produtoId,
            String produtoNome,
            String produtoDescricao,
            BigDecimal produtoPreco,
            BigDecimal pesoFisico,
            BigDecimal comprimento,
            BigDecimal largura,
            BigDecimal altura,
            Boolean fragil,
            TipoProduto tipoProduto,
            Long itemCompraId,
            Long itemQuantidade
    ) {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            Produto produto = new Produto(
                    produtoId,
                    produtoNome,
                    produtoDescricao,
                    produtoPreco,
                    pesoFisico,
                    comprimento,
                    largura,
                    altura,
                    fragil,
                    tipoProduto
            );

            ItemCompra item = new ItemCompra(itemCompraId, produto, itemQuantidade);
        });
    }


    @Test
    @DisplayName("Test Cliente Nulo - Partições de Equivalência")
    public void testClientVazio() {
        CompraService service = new CompraService(null, null, null, null);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();

        Produto produto = new Produto(
                1L,
                "Produto Teste",
                "Descricao",
                new BigDecimal("50.00"),
                new BigDecimal("6.00"), // increase weight so pesoTotal > 5 and switch(regiao) is reached
                new BigDecimal("1.00"),
                new BigDecimal("0.50"),
                new BigDecimal("0.10"),
                false,
                TipoProduto.ELETRONICO
        );

        ItemCompra item = new ItemCompra(1L, produto, 1L);
        carrinho.setItens(Collections.singletonList(item));

        Cliente cliente = new Cliente();
        Assertions.assertThrowsExactly(NullPointerException.class, () -> {
            service.calcularCustoTotal(carrinho, cliente);
        });
    }

    @Test
    @DisplayName("Test Produto Nulo - Partições de Equivalência")
    public void testProdutoVazio() {
        CompraService service = new CompraService(null, null, null, null);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();

        Produto produto = new Produto();

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            ItemCompra item = new ItemCompra(1L, produto, 1L);
        });
    }

    @ParameterizedTest(name = "pesoFisico={0}, dimensoes={1}x{2}x{3} -> expected={4}")
    @CsvSource({
            // Peso físico > Peso cúbico
            "10.00, 1.0, 1.0, 1.0, 192.00",
            // Peso cúbico > Peso físico
            "1.00, 3.0, 3.0, 3.0, 100.00",
            // peso cúbico = peso físico
            "4.5, 30.0, 30.0, 30.0, 130.00"
    })
    @DisplayName("Test Calcular Custo Total - Peso Físico vs Peso Cúbico - Partições de Equivalência")
    public void testPesoFisicoVsPesoCubicoParameterized(
            BigDecimal pesoFisico,
            BigDecimal comprimento,
            BigDecimal largura,
            BigDecimal altura,
            String expected
    ) {
        CompraService service = new CompraService(null, null, null, null);

        Cliente cliente = new Cliente(1L, "Cliente Teste", Regiao.SUDESTE, TipoCliente.BRONZE);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();

        Produto produto = new Produto(
                1L,
                "Produto Teste",
                "Descricao",
                new BigDecimal("50.00"),
                pesoFisico,
                comprimento,
                largura,
                altura,
                false,
                TipoProduto.ELETRONICO
        );

        ItemCompra item = new ItemCompra(1L, produto, 2L);
        carrinho.setItens(Collections.singletonList(item));

        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertThat(custoTotal).as("Custo Total da Compra").isEqualByComparingTo(new BigDecimal(expected));
    }
}
