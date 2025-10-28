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

public class LimitesPesoTotalTest {

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
    @DisplayName("TC-VL14: Peso Total 4,99 kg (Limite 5,00 kg L-1) deve resultar em frete isento")
    void deveCalcularFreteIsentoQuandoPesoTotalAbaixoLimite() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("4.99"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor sem frete para peso total 4,99 kg (abaixo do limite)")
                .isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("TC-VL15: Peso Total 5,00 kg (Limite 5,00 kg L) deve resultar em frete isento")
    void deveCalcularFreteIsentoQuandoPesoTotalNoLimite() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("5.00"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor sem frete para peso total 5,00 kg (no limite)")
                .isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("TC-VL16: Peso Total 5,01 kg (Limite 5,00 kg L+1) deve resultar em R$ 2,00/kg")
    void deveCalcularFreteDoisReaisPorKgQuandoPesoTotalAcimaLimiteMenor() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("5.01"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com frete R$ 2,00/kg para peso total 5,01 kg")
                .isEqualByComparingTo("122.02"); // 100 + (5.01 * 2) + 12 (taxa fixa)
    }

    @Test
    @DisplayName("TC-VL17: Peso Total 9,99 kg (Limite 10,00 kg L-1) deve resultar em R$ 2,00/kg")
    void deveCalcularFreteDoisReaisPorKgQuandoPesoTotalAbaixoLimiteMedio() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("9.99"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com frete R$ 2,00/kg para peso total 9,99 kg")
                .isEqualByComparingTo("131.98"); // 100 + (9.99 * 2) + 12
    }

    @Test
    @DisplayName("TC-VL18: Peso Total 10,00 kg (Limite 10,00 kg L) deve resultar em R$ 2,00/kg")
    void deveCalcularFreteDoisReaisPorKgQuandoPesoTotalNoLimiteMedio() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("10.00"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com frete R$ 2,00/kg para peso total 10,00 kg")
                .isEqualByComparingTo("132.00"); // 100 + (10 * 2) + 12
    }

    @Test
    @DisplayName("TC-VL19: Peso Total 10,01 kg (Limite 10,00 kg L+1) deve resultar em R$ 4,00/kg")
    void deveCalcularFreteQuatroReaisPorKgQuandoPesoTotalAcimaLimiteMedio() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("10.01"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com frete R$ 4,00/kg para peso total 10,01 kg")
                .isEqualByComparingTo("152.04"); // 100 + (10.01 * 4) + 12
    }

    @Test
    @DisplayName("TC-VL20: Peso Total 49,99 kg (Limite 50,00 kg L-1) deve resultar em R$ 4,00/kg")
    void deveCalcularFreteQuatroReaisPorKgQuandoPesoTotalAbaixoLimiteMaior() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("49.99"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com frete R$ 4,00/kg para peso total 49,99 kg")
                .isEqualByComparingTo("311.96"); // 100 + (49.99 * 4) + 12
    }

    @Test
    @DisplayName("TC-VL21: Peso Total 50,00 kg (Limite 50,00 kg L) deve resultar em R$ 4,00/kg")
    void deveCalcularFreteQuatroReaisPorKgQuandoPesoTotalNoLimiteMaior() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("50.00"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com frete R$ 4,00/kg para peso total 50,00 kg")
                .isEqualByComparingTo("312.00"); // 100 + (50 * 4) + 12
    }

    @Test
    @DisplayName("TC-VL22: Peso Total 50,01 kg (Limite 50,00 kg L+1) deve resultar em R$ 7,00/kg")
    void deveCalcularFreteSeteReaisPorKgQuandoPesoTotalAcimaLimiteMaior() {
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("50.01"), new BigDecimal("10.0"), new BigDecimal("10.0"),
                new BigDecimal("10.0"), false, TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Valor com frete R$ 7,00/kg para peso total 50,01 kg")
                .isEqualByComparingTo("462.07"); // 100 + (50.01 * 7) + 12
    }
}
