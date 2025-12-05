package ecommerce.service.u3;

import ecommerce.entity.*;
import ecommerce.external.IEstoqueExternal;
import ecommerce.external.IPagamentoExternal;
import ecommerce.service.CarrinhoDeComprasService;
import ecommerce.service.ClienteService;
import ecommerce.service.CompraSimplificadaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CompraSimplificadaServiceTest {

    @InjectMocks
    private CompraSimplificadaService service;

    @Mock
    private CarrinhoDeComprasService carrinhoService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private IEstoqueExternal estoqueExternal;

    @Mock
    private IPagamentoExternal pagamentoExternal;

    private Cliente cliente;
    private CarrinhoDeCompras carrinho;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");
        cliente.setRegiao(Regiao.SUDESTE);
        cliente.setTipo(TipoCliente.BRONZE);

        carrinho = new CarrinhoDeCompras();
        carrinho.setId(1L);
        carrinho.setCliente(cliente);
        carrinho.setItens(new ArrayList<>());
    }

    @Test
    @DisplayName("Deve lançar exceção quando carrinho for nulo")
    void calcularCustoTotal_CarrinhoNulo_DeveLancarExcecao() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calcularCustoTotal(null, cliente)
        );
        assertEquals("Carrinho ou cliente não podem ser nulos.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente for nulo")
    void calcularCustoTotal_ClienteNulo_DeveLancarExcecao() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calcularCustoTotal(carrinho, null)
        );
        assertEquals("Carrinho ou cliente não podem ser nulos.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ambos forem nulos")
    void calcularCustoTotal_AmbosNulos_DeveLancarExcecao() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calcularCustoTotal(null, null)
        );
        assertEquals("Carrinho ou cliente não podem ser nulos.", exception.getMessage());
    }

    // Teste subtotal e desconto

    @Test
    @DisplayName("Sem desconto quando subtotal = 500.00")
    void calcularCustoTotal_SubtotalExato500_SemDesconto() {
        adicionarProduto(carrinho, 500.00, 1, 1.0, false);

        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("500.00"), custoTotal);
    }

    @Test
    @DisplayName("Desconto 10% quando subtotal = 500.01")
    void calcularCustoTotal_Subtotal500Ponto01_Desconto10Porcento() {
        adicionarProduto(carrinho, 500.01, 1, 1.0, false);

        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("450.01"), custoTotal);
    }

    @Test
    @DisplayName("Desconto 10% quando subtotal = 600.00")
    void calcularCustoTotal_Subtotal600_Desconto10Porcento() {
        adicionarProduto(carrinho, 600.00, 1, 1.0, false);

        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("540.00"), custoTotal);
    }

    @Test
    @DisplayName("Desconto 10% quando subtotal = 1000.00")
    void calcularCustoTotal_SubtotalExato1000_Desconto10Porcento() {
        adicionarProduto(carrinho, 1000.00, 1, 2.0, false);

        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("900.00"), custoTotal);
    }

    @Test
    @DisplayName("Desconto 20% quando subtotal = 1000.01")
    void calcularCustoTotal_Subtotal1000Ponto01_Desconto20Porcento() {
        adicionarProduto(carrinho, 1000.01, 1, 2.0, false);

        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("800.01"), custoTotal);
    }

    @Test
    @DisplayName("Desconto 20% quando subtotal = 1500.00")
    void calcularCustoTotal_Subtotal1500_Desconto20Porcento() {
        adicionarProduto(carrinho, 1500.00, 1, 2.0, false);

        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("1200.00"), custoTotal);
    }

    @Test
    @DisplayName("Sem desconto quando subtotal = 499.99")
    void calcularCustoTotal_Subtotal499_SemDesconto() {
        adicionarProduto(carrinho, 499.99, 1, 1.0, false);

        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);

        assertEquals(new BigDecimal("499.99"), custoTotal);
    }

    // Teste de frete

    @Test
    @DisplayName("Frete zero quando peso = 5kg exato sem frágeis")
    void calcularCustoTotal_Peso5kg_FreteZero() {
        adicionarProduto(carrinho, 100.00, 1, 5.0, false);
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("100.00"), custoTotal);
    }

    @Test
    @DisplayName("Frete apenas taxa frágil quando peso <= 5kg com frágil")
    void calcularCustoTotal_Peso5kgComFragil_ApenasFreteFragil() {
        adicionarProduto(carrinho, 100.00, 2, 2.0, true); // 4kg total, 2 itens
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("210.00"), custoTotal);
    }

    @Test
    @DisplayName("Taxa pequena (2.00/kg) quando peso = 5.01kg")
    void calcularCustoTotal_Peso5Ponto01kg_TaxaPequena() {
        adicionarProduto(carrinho, 100.00, 1, 5.01, false);
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("110.02"), custoTotal);
    }

    @Test
    @DisplayName("Taxa pequena quando peso = 10kg exato")
    void calcularCustoTotal_Peso10kg_TaxaPequena() {
        adicionarProduto(carrinho, 100.00, 1, 10.0, false);
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("120.00"), custoTotal);
    }

    @Test
    @DisplayName("Taxa média (4.00/kg) quando peso = 10.01kg")
    void calcularCustoTotal_Peso10Ponto01kg_TaxaMedia() {
        adicionarProduto(carrinho, 100.00, 1, 10.01, false);
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("140.04"), custoTotal);
    }

    @Test
    @DisplayName("Taxa média quando peso = 30kg")
    void calcularCustoTotal_Peso30kg_TaxaMedia() {
        adicionarProduto(carrinho, 100.00, 1, 30.0, false);
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("220.00"), custoTotal);
    }

    @Test
    @DisplayName("Taxa média quando peso = 50kg exato")
    void calcularCustoTotal_Peso50kg_TaxaMedia() {
        adicionarProduto(carrinho, 100.00, 1, 50.0, false);
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("300.00"), custoTotal);
    }

    @Test
    @DisplayName("Taxa grande (7.00/kg) quando peso = 50.01kg")
    void calcularCustoTotal_Peso50Ponto01kg_TaxaGrande() {
        adicionarProduto(carrinho, 100.00, 1, 50.01, false);
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("450.07"), custoTotal);
    }

    @Test
    @DisplayName("Taxa grande quando peso = 60kg")
    void calcularCustoTotal_Peso60kg_TaxaGrande() {
        adicionarProduto(carrinho, 100.00, 1, 60.0, false);
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("520.00"), custoTotal);
    }

    @Test
    @DisplayName("Taxa grande com frágeis")
    void calcularCustoTotal_Peso60kgComFrageis_TaxaGrandeMaisFragil() {
        adicionarProduto(carrinho, 100.00, 3, 20.0, true); // 60kg, 3 frágeis
        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("735.00"), custoTotal);
    }

    // Teste de limite inferior de peso

    @Test
    @DisplayName("Peso menor que 5kg sem frágil = frete zero")
    void calcularCustoTotal_Peso3kg_FreteZero() {
        adicionarProduto(carrinho, 100.00, 1, 3.0, false);

        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);

        assertEquals(new BigDecimal("100.00"), custoTotal);
    }

    @Test
    @DisplayName("Peso menor que 5kg com 1 frágil")
    void calcularCustoTotal_Peso3kgCom1Fragil_Apenas5Reais() {
        adicionarProduto(carrinho, 100.00, 1, 3.0, true);

        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("105.00"), custoTotal);
    }

    @Test
    @DisplayName("Peso zero com frágil")
    void calcularCustoTotal_PesoZeroComFragil_ApenasFreteFragil() {
        adicionarProduto(carrinho, 100.00, 1, 0.0, true);

        BigDecimal custoTotal = service.calcularCustoTotal(carrinho, cliente);
        assertEquals(new BigDecimal("105.00"), custoTotal);
    }

    @ParameterizedTest
    @CsvSource({
            "499.99, 0.00",
            "500.00, 0.00",
            "500.01, 50.00",
            "750.00, 75.00",
            "1000.00, 100.00",
            "1000.01, 200.00",
            "1500.00, 300.00",
            "2000.00, 400.00"
    })
    @DisplayName("Boundaries de desconto por subtotal")
    void descontoPorSubtotal_TodosBoundaries(double subtotalValor, double descontoEsperado) {
        BigDecimal subtotal = BigDecimal.valueOf(subtotalValor);
        BigDecimal desconto = service.descontoPorSubtotal(subtotal);

        assertEquals(
                BigDecimal.valueOf(descontoEsperado).setScale(2, RoundingMode.HALF_UP),
                desconto.setScale(2, RoundingMode.HALF_UP)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "0.01, false, 0.00",
            "4.99, false, 0.00",
            "5.00, false, 0.00",
            "5.01, false, 10.02",
            "10.00, false, 20.00",
            "10.01, false, 40.04",
            "50.00, false, 200.00",
            "50.01, false, 350.07",
            "100.00, false, 700.00",
            "5.00, true, 5.00",
            "10.00, true, 25.00",
            "50.01, true, 355.07"
    })
    @DisplayName("Boundaries de frete por peso")
    void valorFrete_TodosBoundaries(double peso, boolean fragil, double freteEsperado) {
        adicionarProduto(carrinho, 100.00, 1, peso, fragil);

        BigDecimal frete = service.valorFrete(carrinho);

        assertEquals(
                BigDecimal.valueOf(freteEsperado).setScale(2, RoundingMode.HALF_UP),
                frete.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private void adicionarProduto(CarrinhoDeCompras carrinho, double preco,
                                  int quantidade, double pesoFisico, boolean fragil) {
        long produtoId = carrinho.getItens().size() + 1;

        Produto produto = new Produto();
        produto.setId(produtoId);
        produto.setNome("Produto Teste " + produtoId);
        produto.setPreco(BigDecimal.valueOf(preco));
        produto.setPesoFisico(BigDecimal.valueOf(pesoFisico));
        produto.setFragil(fragil);
        produto.setTipo(TipoProduto.ELETRONICO);

        produto.setComprimento(BigDecimal.valueOf(10.0));
        produto.setLargura(BigDecimal.valueOf(10.0));
        produto.setAltura(BigDecimal.valueOf(10.0));

        ItemCompra item = new ItemCompra();
        item.setId(produtoId);
        item.setProduto(produto);
        item.setQuantidade((long) quantidade);

        carrinho.getItens().add(item);
    }
}
