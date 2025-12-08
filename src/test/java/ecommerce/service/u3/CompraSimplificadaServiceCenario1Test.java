package ecommerce.service.u3;

import ecommerce.dto.CompraDTO;
import ecommerce.dto.DisponibilidadeDTO;
import ecommerce.dto.EstoqueBaixaDTO;
import ecommerce.entity.*;
import ecommerce.external.fake.EstoqueSimulado;
import ecommerce.external.fake.PagamentoSimulado;
import ecommerce.service.CarrinhoDeComprasService;
import ecommerce.service.ClienteService;
import ecommerce.service.CompraSimplificadaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompraSimplificadaServiceCenario1Test {

    private CarrinhoDeComprasService carrinhoService;
    private ClienteService clienteService;

    private EstoqueSimulado estoqueSimulado;
    private PagamentoSimulado pagamentoSimulado;

    private CompraSimplificadaService service;

    private Cliente cliente;
    private CarrinhoDeCompras carrinho;

    @BeforeEach
    void setUp() {
        carrinhoService = mock(CarrinhoDeComprasService.class);
        clienteService = mock(ClienteService.class);

        EstoqueSimulado estoqueFake = new EstoqueSimulado() {
            @Override
            public EstoqueBaixaDTO darBaixa(List<Long> produtosIds, List<Long> produtosQuantidades) {
                return new EstoqueBaixaDTO(true);
            }

            @Override
            public DisponibilidadeDTO verificarDisponibilidade(List<Long> produtosIds, List<Long> produtosQuantidades) {
                return new DisponibilidadeDTO(true, List.of());
            }
        };
        estoqueSimulado = Mockito.spy(estoqueFake);

        pagamentoSimulado = Mockito.spy(new PagamentoSimulado());
        pagamentoSimulado.resetTransacaoCounter(2000);
        pagamentoSimulado.setAutorizarPadrao(true);

        service = new CompraSimplificadaService(carrinhoService, clienteService, estoqueSimulado, pagamentoSimulado);

        cliente = new Cliente();
        cliente.setId(10L);
        cliente.setNome("Cliente Teste");
        cliente.setRegiao(Regiao.SUDESTE);
        cliente.setTipo(TipoCliente.PRATA);

        carrinho = new CarrinhoDeCompras();
        carrinho.setId(100L);
        carrinho.setCliente(cliente);
        carrinho.setItens(new ArrayList<>());

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("P1");
        produto.setPreco(BigDecimal.valueOf(100.00));
        produto.setPesoFisico(BigDecimal.valueOf(1.0));
        produto.setFragil(false);
        produto.setTipo(TipoProduto.ELETRONICO);
        produto.setComprimento(BigDecimal.valueOf(10));
        produto.setLargura(BigDecimal.valueOf(10));
        produto.setAltura(BigDecimal.valueOf(10));

        ItemCompra item = new ItemCompra();
        item.setId(1L);
        item.setProduto(produto);
        item.setQuantidade(1L);

        carrinho.getItens().add(item);

        when(clienteService.buscarPorId(cliente.getId())).thenReturn(cliente);
        when(carrinhoService.buscarPorCarrinhoIdEClienteId(carrinho.getId(), cliente)).thenReturn(carrinho);
    }

    @Test
    @DisplayName("Finalizar compra com sucesso: valida fluxo completo e cálculo do custo total")
    void deveFinalizarCompraComSucessoValidandoFluxoCompletoECalculoCustoTotal() {
        CompraDTO resultado = service.finalizarCompra(carrinho.getId(), cliente.getId());

        assertNotNull(resultado);
        assertTrue(resultado.sucesso());
        assertEquals("Compra finalizada com sucesso.", resultado.mensagem());
        assertNotNull(resultado.transacaoPagamentoId());
        assertEquals(2000L, resultado.transacaoPagamentoId());

        // verifica chamada na ordem correta
        verify(clienteService, times(1)).buscarPorId(eq(cliente.getId()));
        verify(carrinhoService, times(1)).buscarPorCarrinhoIdEClienteId(eq(carrinho.getId()), eq(cliente));

        // verifica interação com estoqueSimulado.verificarDisponibilidade
        ArgumentCaptor<List> idsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List> qtdsCaptor = ArgumentCaptor.forClass(List.class);

        verify(estoqueSimulado, times(1)).verificarDisponibilidade(idsCaptor.capture(), qtdsCaptor.capture());
        List<Long> ids = idsCaptor.getValue();
        List<Long> qtds = qtdsCaptor.getValue();
        assertEquals(List.of(1L), ids);
        assertEquals(List.of(1L), qtds);

        // verifica valor calculado
        ArgumentCaptor<Double> valorCaptor = ArgumentCaptor.forClass(Double.class);
        verify(pagamentoSimulado, times(1)).autorizarPagamento(eq(cliente.getId()), valorCaptor.capture());
        assertEquals(100.0, valorCaptor.getValue(), 0.01, "Valor calculado incorreto");

        // darBaixa
        ArgumentCaptor<List> idsCaptor2 = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List> qtdsCaptor2 = ArgumentCaptor.forClass(List.class);
        verify(estoqueSimulado, times(1)).darBaixa(idsCaptor2.capture(), qtdsCaptor2.capture());
        assertEquals(List.of(1L), idsCaptor2.getValue());
        assertEquals(List.of(1L), qtdsCaptor2.getValue());

        verify(pagamentoSimulado, never()).cancelarPagamento(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Estoque indisponível: deve lançar exceção e não autorizar pagamento")
    void deveLancarExcecaoQuandoEstoqueIndisponivelENaoAutorizarPagamento() {
        EstoqueSimulado estoqueIndisponivel = new EstoqueSimulado() {
            @Override
            public EstoqueBaixaDTO darBaixa(List<Long> produtosIds, List<Long> produtosQuantidades) {
                return new EstoqueBaixaDTO(true);
            }

            @Override
            public DisponibilidadeDTO verificarDisponibilidade(List<Long> produtosIds, List<Long> produtosQuantidades) {
                return new DisponibilidadeDTO(false, List.of(1L));
            }
        };

        EstoqueSimulado spyEstoqueIndisponivel = Mockito.spy(estoqueIndisponivel);
        PagamentoSimulado spyPagamento = Mockito.spy(new PagamentoSimulado());
        spyPagamento.setAutorizarPadrao(true);

        CompraSimplificadaService svc = new CompraSimplificadaService(carrinhoService, clienteService, spyEstoqueIndisponivel, spyPagamento);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> svc.finalizarCompra(carrinho.getId(), cliente.getId()));
        assertEquals("Itens fora de estoque.", ex.getMessage());

        verify(clienteService, times(1)).buscarPorId(eq(cliente.getId()));
        verify(carrinhoService, times(1)).buscarPorCarrinhoIdEClienteId(eq(carrinho.getId()), eq(cliente));

        verify(spyPagamento, never()).autorizarPagamento(anyLong(), anyDouble());
        verify(spyEstoqueIndisponivel, times(1)).verificarDisponibilidade(anyList(), anyList());
        verify(spyEstoqueIndisponivel, never()).darBaixa(anyList(), anyList());
    }

    @Test
    @DisplayName("Pagamento não autorizado: deve lançar exceção e não dar baixa no estoque")
    void deveLancarExcecaoQuandoPagamentoNaoAutorizadoENaoDarBaixaEstoque() {
        EstoqueSimulado estoqueSpy = Mockito.spy(new EstoqueSimulado() {
            @Override
            public EstoqueBaixaDTO darBaixa(List<Long> produtosIds, List<Long> produtosQuantidades) {
                return new EstoqueBaixaDTO(true);
            }

            @Override
            public DisponibilidadeDTO verificarDisponibilidade(List<Long> produtosIds, List<Long> produtosQuantidades) {
                return new DisponibilidadeDTO(true, List.of());
            }
        });

        PagamentoSimulado pagamentoSpy = Mockito.spy(new PagamentoSimulado());
        pagamentoSpy.setAutorizarPadrao(false);

        CompraSimplificadaService svc = new CompraSimplificadaService(carrinhoService, clienteService, estoqueSpy, pagamentoSpy);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> svc.finalizarCompra(carrinho.getId(), cliente.getId()));
        assertEquals("Pagamento não autorizado.", ex.getMessage());

        verify(clienteService, times(1)).buscarPorId(eq(cliente.getId()));
        verify(carrinhoService, times(1)).buscarPorCarrinhoIdEClienteId(eq(carrinho.getId()), eq(cliente));

        verify(estoqueSpy, times(1)).verificarDisponibilidade(anyList(), anyList());
        verify(estoqueSpy, never()).darBaixa(anyList(), anyList());
        verify(pagamentoSpy, times(1)).autorizarPagamento(eq(cliente.getId()), anyDouble());
    }

    @Test
    @DisplayName("Falha na baixa de estoque: deve cancelar pagamento e lançar exceção")
    void deveCancelarPagamentoELancarExcecaoQuandoFalhaNaBaixaEstoque() {
        EstoqueSimulado estoqueFalha = new EstoqueSimulado() {
            @Override
            public EstoqueBaixaDTO darBaixa(List<Long> produtosIds, List<Long> produtosQuantidades) {
                return new EstoqueBaixaDTO(false);
            }

            @Override
            public DisponibilidadeDTO verificarDisponibilidade(List<Long> produtosIds, List<Long> produtosQuantidades) {
                return new DisponibilidadeDTO(true, List.of());
            }
        };

        EstoqueSimulado spyEstoqueFalha = Mockito.spy(estoqueFalha);
        PagamentoSimulado spyPagamento = Mockito.spy(new PagamentoSimulado());
        spyPagamento.resetTransacaoCounter(3000);
        spyPagamento.setAutorizarPadrao(true);

        CompraSimplificadaService svc = new CompraSimplificadaService(carrinhoService, clienteService, spyEstoqueFalha, spyPagamento);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> svc.finalizarCompra(carrinho.getId(), cliente.getId()));
        assertEquals("Erro ao dar baixa no estoque.", ex.getMessage());

        verify(clienteService, times(1)).buscarPorId(eq(cliente.getId()));
        verify(carrinhoService, times(1)).buscarPorCarrinhoIdEClienteId(eq(carrinho.getId()), eq(cliente));

        verify(spyPagamento, times(1)).cancelarPagamento(eq(cliente.getId()), eq(3000L));
        verify(spyPagamento, times(1)).autorizarPagamento(eq(cliente.getId()), anyDouble());
        verify(spyEstoqueFalha, times(1)).verificarDisponibilidade(anyList(), anyList());
        verify(spyEstoqueFalha, times(1)).darBaixa(anyList(), anyList());
    }
}
