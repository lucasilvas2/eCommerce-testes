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
    @ParameterizedTest(name = "TC-P{index}: Desconto por quantidade - preço={0}, qtd={1} -> esperado={2}")
    @CsvSource({
            "100.00, 1, 100.00",   // TC-P1: Sem desconto (<3 itens)
            "100.00, 3, 285.00",   // TC-P2: Desconto 5% (3-4 itens)
            "50.00, 5, 225.00",    // TC-P3: Desconto 10% (5-7 itens)
            "25.00, 8, 170.00",    // TC-P4: Desconto 15% (≥8 itens)
            "100.00, 5, 450.00",   // TC-P3: Desconto 10% (5-7 itens) - valor diferente
    })
    @DisplayName("Partições de Equivalência: Desconto por Quantidade de Itens")
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
        assertThat(custoTotal).as("Custo Total da Compra")
                .as("Custo Total da Compra")
                .isEqualByComparingTo(new BigDecimal(expected));
    }

    @ParameterizedTest(name = "TC-P{index}: Múltiplos tipos - Item1(R${0},qtd={1},{2}), Item2(R${3},qtd={4},{5}), Item3(R${6},qtd={7},{8})")
    @CsvSource({
            "25.00, 1, ELETRONICO, 100.00, 2, ROUPA, 50.00, 3, LIVRO, 367.50",     // TC-P5: Múltiplos tipos com descontos variados
            "150.00, 2, ELETRONICO, 75.00, 3, MOVEL, 100.00, 1, ALIMENTO, 551.25", // TC-P6: Múltiplos tipos/preços
            "100.00, 3, LIVRO, 80.00, 2, ROUPA, 60.00, 5, ELETRONICO, 639.00",     // TC-P5: Variação com diferentes combinações
    })
    @DisplayName("Partições de Equivalência (P1,P2,P3,P4): Desconto por Múltiplos Tipos de Itens")
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

    @ParameterizedTest(name = "TC-P{index}: Subtotal - preço=R${0}, qtd={1} -> esperado=R${2}")
    @CsvSource({
            "200.00, 2, 400.00",   // TC-P5: Subtotal ≤ R$500,00 (0% desconto)
            "100.00, 6, 540.00",   // TC-P6: R$500,00 < Subtotal ≤ R$1000,00 (10% desconto)
            "525.00, 2, 840.00",   // TC-P7: Subtotal > R$1000,00 (20% desconto)
            "200.00, 10, 1480.00", // TC-P7: Subtotal > R$1000,00 (outro caso)
    })
    @DisplayName("Partições de Equivalência (P5-P7): Desconto por Faixa de Subtotal")
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

    @ParameterizedTest(name = "TC-P{index}: Frete por Peso - preço=R${0}, qtd={1}, peso={2}kg")
    @CsvSource({
            "200.00, 2, 1.00, 0.5, 1.0, 2.0, 400.00",  // TC-P8: 0 ≤ peso ≤ 5 (R$0,00/kg)
            "50.00, 2, 4.00, 0.3, 0.4, 0.5, 128.00",   // TC-P8: 0 ≤ peso ≤ 5 (outro caso)
            "10.00, 2, 15.0, 0.3, 0.4, 0.5, 152.00",   // TC-P10: 10 < peso ≤ 50 (R$4,00/kg)
            "150.00, 1, 55.0, 1.0, 1.0, 1.0, 547.00",  // TC-P11: peso > 50 (R$7,00/kg)
            "100.00, 2, 0.50, 75.0, 80.0, 50.0, 912.00" // TC-P21/P22: Teste peso físico vs cúbico
    })
    @DisplayName("Partições de Equivalência (P8-P11,P21-P23): Cálculo do Frete por Peso")
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

    @ParameterizedTest(name = "TC-P{index}: Taxa de Fragilidade - R${0}, qtd={1}, {6} -> R${7}")
    @CsvSource({
            "50.00, 3, 1.00, 0.5, 1.0, 2.0, true, 157.50",  // TC-P14: Item frágil (R$5,00 por item)
            "50.00, 2, 4.00, 0.3, 0.4, 0.5, false, 128.00", // TC-P15: Item não frágil (R$0,00)
    })
    @DisplayName("Partições de Equivalência (P14-P15): Taxa de Manuseio por Fragilidade")
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

    @ParameterizedTest(name = "TC-P{index}: Multiplicador Regional - {6} (x{7})")
    @CsvSource({
            "50.00, 3, 12.00, 0.5, 1.0, 2.0, SUDESTE, 298.50",     // TC-P16: Multiplicador Sudeste (1.00)
            "50.00, 2, 12.00, 0.5, 1.0, 2.0, SUL, 212.80",         // TC-P17: Multiplicador Sul (1.05)
            "50.00, 2, 12.00, 0.5, 1.0, 2.0, NORDESTE, 217.60",    // TC-P18: Multiplicador Nordeste (1.10)
            "25.00, 1, 12.00, 0.5, 1.0, 2.0, CENTRO_OESTE, 94.60", // TC-P19: Multiplicador Centro-Oeste (1.20)
            "50.00, 2, 12.00, 0.5, 1.0, 2.0, NORTE, 236.80",       // TC-P20: Multiplicador Norte (1.30)
    })
    @DisplayName("Partições de Equivalência (P16-P20): Multiplicador Regional de Frete")
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

    @ParameterizedTest(name = "TC-P{index}: Desconto por Nível - Cliente {6}")
    @CsvSource({
            "50.00, 2, 10.00, 0.5, 1.0, 2.0, OURO, 100.00",   // TC-P24: Cliente Ouro (100% desconto)
            "50.00, 2, 10.00, 0.5, 1.0, 2.0, PRATA, 146.00",  // TC-P25: Cliente Prata (50% desconto)
            "50.00, 2, 10.00, 0.5, 1.0, 2.0, BRONZE, 192.00", // TC-P26: Cliente Bronze (0% desconto)
    })
    @DisplayName("Partições de Equivalência (P24-P26): Desconto no Frete por Nível do Cliente")
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

    @ParameterizedTest(name = "TC-P{index}: Validação - ID={0}, Nome={1}, Região={2}, Tipo={3}")
    @CsvSource({
            "-1, Pedro, SUDESTE, BRONZE",    // TC-P27/P28: ID negativo (inválido)
            "2, , NORTE, PRATA",             // TC-P31/P32: Nome nulo (inválido)
            "3, Maria, , OURO",              // TC-P31: Região nula (inválida)
            ", Carlos, NORDESTE, BRONZE",     // TC-P31: ID nulo (inválido)
    })
    @DisplayName("Partições de Equivalência (P27-P33): Validação de Dados do Cliente")
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
    @DisplayName("Partições de Equivalência (P31,P32): Cliente Nulo - Validação de Objeto Requerido")
    public void testClientVazio() {
        CompraService service = new CompraService(null, null, null, null);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();

        Produto produto = new Produto(
                1L,
                "Produto Teste",
                "Descricao",
                new BigDecimal("50.00"),
                new BigDecimal("6.00"),
                new BigDecimal("1.00"),
                new BigDecimal("0.50"),
                new BigDecimal("0.10"),
                false,
                TipoProduto.ELETRONICO
        );

        ItemCompra item = new ItemCompra(1L, produto, 1L);
        carrinho.setItens(Collections.singletonList(item));

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            service.calcularCustoTotal(carrinho, null);
        });
    }

    @Test
    @DisplayName("Partições de Equivalência (P33): Carrinho Nulo - Validação de Objeto Requerido")
    public void testCarrinhoVazio() {
        CompraService service = new CompraService(null, null, null, null);

        Cliente cliente = new Cliente(1L, "Cliente Teste", Regiao.SUDESTE, TipoCliente.BRONZE);

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            service.calcularCustoTotal(null, cliente);
        });
    }

    @Test
    @DisplayName("Partições de Equivalência (P31,P32): Produto Nulo - Validação de Objeto Requerido")
    public void testProdutoVazio() {
        CompraService service = new CompraService(null, null, null, null);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();

        Produto produto = new Produto();

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            ItemCompra item = new ItemCompra(1L, produto, 1L);
        });
    }

    @ParameterizedTest(name = "TC-P{index}: Peso - físico={0}kg, dimensões={1}x{2}x{3}cm -> esperado=R${4}")
    @CsvSource({
            "10.00, 1.0, 1.0, 1.0, 192.00",   // TC-P21: Peso físico maior que peso cúbico
            "1.00, 3.0, 3.0, 3.0, 100.00",    // TC-P22: Peso cúbico maior que peso físico
            "4.5, 30.0, 30.0, 30.0, 130.00"   // TC-P23: Peso físico igual ao peso cúbico
    })
    @DisplayName("Partições de Equivalência (P21-P23): Cálculo do Peso para Frete")
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
