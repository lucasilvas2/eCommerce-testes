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

public class LimiteTipoClienteTest {

    private CompraService compraService;
    private CarrinhoDeCompras carrinho;
    private Cliente cliente;
    private Produto produto;
    private List<ItemCompra> itemCompras;

    @BeforeEach
    void setup() {
        compraService = new CompraService(null, null, null, null);
        carrinho = new CarrinhoDeCompras();
        itemCompras = new ArrayList<>();
        // Produto com peso que gera frete de R$100,00
        produto = new Produto(1L, "Test", "Test Description", new BigDecimal("100.00"),
                new BigDecimal("22.00"),
                new BigDecimal("10.0"),
                new BigDecimal("10.0"),
                new BigDecimal("10.0"),
                false,
                TipoProduto.ELETRONICO);
        ItemCompra item = new ItemCompra(1L, produto, 1L);
        itemCompras.add(item);
        carrinho.setItens(itemCompras);
    }

    @Test
    @DisplayName("TC-VL34: Cliente OURO deve ter 100% de desconto no frete")
    void deveCalcularFreteZeroQuandoClienteOuro() {
        cliente = new Cliente(1L, "Test", Regiao.SUDESTE, TipoCliente.OURO);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Cliente OURO deve pagar apenas o valor dos produtos")
                .isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("TC-VL35: Cliente PRATA deve ter 50% de desconto no frete")
    void deveCalcularFretePelaMedadeQuandoClientePrata() {
        cliente = new Cliente(1L, "Test", Regiao.SUDESTE, TipoCliente.PRATA);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Cliente PRATA deve pagar metade do frete")
                .isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("TC-VL36: Cliente BRONZE deve pagar frete integral")
    void deveCalcularFreteIntegralQuandoClienteBronze() {
        cliente = new Cliente(1L, "Test", Regiao.SUDESTE, TipoCliente.BRONZE);

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertThat(custoTotal)
                .as("Cliente BRONZE deve pagar frete integral")
                .isEqualByComparingTo("200.00");
    }
}

