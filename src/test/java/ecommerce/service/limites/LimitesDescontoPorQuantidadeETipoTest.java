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

public class LimitesDescontoPorQuantidadeETipoTest {

    private CompraService compraService;
    private CarrinhoDeCompras carrinho;
    private Cliente cliente;
    private Produto produto;
    private List<ItemCompra> itemCompras;

    @BeforeEach
    void setup() {
        compraService = new CompraService(null, null, null, null);
        cliente = new Cliente(
                1L,
                "Test",
                Regiao.SUDESTE,
                TipoCliente.BRONZE
        );
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
        carrinho = new CarrinhoDeCompras();
        itemCompras = new ArrayList<>();
    }


    @Test
    @DisplayName("TC-VL1: Quantidade 2 (Limite 3 L-1) deve resultar em 0% desconto")
    void deveCalcularSemDescontoQuandoQuantidadeDois() {
        ItemCompra item = new ItemCompra(1L, produto, 2L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor sem desconto para 2 itens (abaixo do limite 3)")
                .isEqualByComparingTo("200.00");
    }

    @Test
    @DisplayName("TC-VL2: Quantidade 3 (Limite 3 L) deve resultar em 5% desconto")
    void deveCalcularComDescontoPequenoQuandoQuantidadeTres() {
        ItemCompra item = new ItemCompra(1L, produto, 3L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com 5% de desconto para 3 itens (no limite)")
                .isEqualByComparingTo("285.00");
    }

    @Test
    @DisplayName("TC-VL3: Quantidade 4 (Limite 4 L) deve resultar em 5% desconto")
    void deveCalcularComDescontoPequenoQuandoQuantidadeQuatro() {
        ItemCompra item = new ItemCompra(1L, produto, 4L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com 5% de desconto para 4 itens (no limite)")
                .isEqualByComparingTo("380.00");
    }

    @Test
    @DisplayName("TC-VL4: Quantidade 5 (Limite 5 L) deve resultar em 10% desconto")
    void deveCalcularComDescontoMedioQuandoQuantidadeCinco() {
        ItemCompra item = new ItemCompra(1L, produto, 5L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com 10% de desconto para 5 itens (no limite)")
                .isEqualByComparingTo("450.00");
    }

    @Test
    @DisplayName("TC-VL5: Quantidade 7 (Limite 7 L) deve resultar em 10% desconto")
    void deveCalcularComDescontoMedioQuandoQuantidadeSete() {
        ItemCompra item = new ItemCompra(1L, produto, 7L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com 10% de desconto para 7 itens (no limite)")
                .isEqualByComparingTo("586.00");

    }

    @Test
    @DisplayName("TC-VL6: Quantidade 8 (Limite 8 L) deve resultar em 15% desconto")
    void deveCalcularComDescontoGrandeQuandoQuantidadeOito() {
        ItemCompra item = new ItemCompra(1L, produto, 8L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com 15% de desconto para 8 itens (no limite)")
                .isEqualByComparingTo("628.00");
    }

    @Test
    @DisplayName("TC-VL7: Quantidade 9 (Limite 8 L+1) deve resultar em 15% desconto")
    void deveCalcularComDescontoGrandeQuandoQuantidadeNove() {
        ItemCompra item = new ItemCompra(1L, produto, 9L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com 15% de desconto para 9 itens (acima do limite)")
                .isEqualByComparingTo("705.00");
    }
}
