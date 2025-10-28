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

public class LimitesPesoFisicoCubicoTest {

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
    @DisplayName("TC-VL31: Peso Físico maior que Peso Cúbico deve usar Peso Físico")
    void deveUsarPesoFisicoQuandoMaiorQuePesoCubico() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("30.0"),
                new BigDecimal("30.0"),
                new BigDecimal("66.6"),
                false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Deve usar peso físico (10kg) para cálculo do frete")
                .isEqualByComparingTo("132.00");
    }

    @Test
    @DisplayName("TC-VL32: Peso Físico igual ao Peso Cúbico deve usar qualquer um dos pesos")
    void deveCalcularMesmoValorQuandoPesosFisicoECubicoIguais() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("50.0"),
                new BigDecimal("30.0"),
                new BigDecimal("40.0"),
                false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Deve calcular mesmo valor quando pesos são iguais (10kg)")
                .isEqualByComparingTo("132.00");
    }

    @Test
    @DisplayName("TC-VL33: Peso Cúbico maior que Peso Físico deve usar Peso Cúbico")
    void deveUsarPesoCubicoQuandoMaiorQuePesoFisico() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("9.99"),
                new BigDecimal("100.0"),
                new BigDecimal("30.0"),
                new BigDecimal("20.2"),
                false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Deve usar peso cúbico (10.01kg) para cálculo do frete")
                .isEqualByComparingTo("152.40");
    }
}
