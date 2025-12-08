package ecommerce.service.u3;

import ecommerce.dto.CompraDTO;
import ecommerce.dto.DisponibilidadeDTO;
import ecommerce.dto.EstoqueBaixaDTO;
import ecommerce.dto.PagamentoDTO;
import ecommerce.entity.*;
import ecommerce.external.IEstoqueExternal;
import ecommerce.external.IPagamentoExternal;
import ecommerce.repository.ClienteRepository;
import ecommerce.repository.CarrinhoDeComprasRepository;
import ecommerce.service.CarrinhoDeComprasService;
import ecommerce.service.ClienteService;
import ecommerce.service.CompraSimplificadaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompraSimplificadaServiceCenario2Test {
    //fakes de repositórios
    private FakeClienteRepository clienteRepository;
    private FakeCarrinhoDeComprasRepository carrinhoRepository;

    private ClienteService clienteService;
    private CarrinhoDeComprasService carrinhoService;

    // mocks dos serviços externos
    private IEstoqueExternal estoqueExternal;
    private IPagamentoExternal pagamentoExternal;

    private CompraSimplificadaService service;

    private Cliente cliente;
    private CarrinhoDeCompras carrinho;

    @BeforeEach
    void setUp() {
        clienteRepository = new FakeClienteRepository();
        carrinhoRepository = new FakeCarrinhoDeComprasRepository();

        clienteService = new ClienteService(clienteRepository);
        carrinhoService = new CarrinhoDeComprasService(carrinhoRepository);

        estoqueExternal = mock(IEstoqueExternal.class);
        pagamentoExternal = mock(IPagamentoExternal.class);

        service = new CompraSimplificadaService(
                carrinhoService,
                clienteService,
                estoqueExternal,
                pagamentoExternal
        );

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

        ItemCompra item = new ItemCompra();
        item.setId(1L);
        item.setProduto(produto);
        item.setQuantidade(1L);

        carrinho.getItens().add(item);

        clienteRepository.salvar(cliente);
        carrinhoRepository.salvar(carrinho);
    }

    @Test
    @DisplayName("Finalizar compra com sucesso: valida fluxo completo e cálculo do custo total")
    void deveFinalizarCompraComSucesso() {
        var ids = List.of(1L);
        var qts = List.of(1L);

        when(estoqueExternal.verificarDisponibilidade(ids, qts))
                .thenReturn(new DisponibilidadeDTO(true, List.of()));

        when(pagamentoExternal.autorizarPagamento(eq(cliente.getId()), anyDouble()))
                .thenReturn(new PagamentoDTO(true, 2000L));

        when(estoqueExternal.darBaixa(ids, qts))
                .thenReturn(new EstoqueBaixaDTO(true));

        CompraDTO resultado = service.finalizarCompra(carrinho.getId(), cliente.getId());

        assertNotNull(resultado);
        assertTrue(resultado.sucesso());
        assertEquals("Compra finalizada com sucesso.", resultado.mensagem());
        assertEquals(2000L, resultado.transacaoPagamentoId());

        verify(estoqueExternal, times(1)).verificarDisponibilidade(ids, qts);
        verify(pagamentoExternal, times(1))
                .autorizarPagamento(eq(cliente.getId()), anyDouble());
        verify(estoqueExternal, times(1)).darBaixa(ids, qts);
        verify(pagamentoExternal, never())
                .cancelarPagamento(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Estoque indisponível: deve lançar exceção e não autorizar pagamento")
    void deveLancarExcecaoQuandoEstoqueIndisponivel() {
        var ids = List.of(1L);
        var qts = List.of(1L);

        when(estoqueExternal.verificarDisponibilidade(ids, qts))
                .thenReturn(new DisponibilidadeDTO(false, List.of(1L)));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.finalizarCompra(carrinho.getId(), cliente.getId())
        );

        assertEquals("Itens fora de estoque.", ex.getMessage());

        verify(estoqueExternal, times(1)).verificarDisponibilidade(ids, qts);
        verify(pagamentoExternal, never())
                .autorizarPagamento(anyLong(), anyDouble());
        verify(estoqueExternal, never()).darBaixa(anyList(), anyList());
        verify(pagamentoExternal, never())
                .cancelarPagamento(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Pagamento não autorizado: deve lançar exceção e não dar baixa no estoque")
    void deveLancarExcecaoQuandoPagamentoNaoAutorizado() {
        var ids = List.of(1L);
        var qts = List.of(1L);

        when(estoqueExternal.verificarDisponibilidade(ids, qts))
                .thenReturn(new DisponibilidadeDTO(true, List.of()));

        when(pagamentoExternal.autorizarPagamento(eq(cliente.getId()), anyDouble()))
                .thenReturn(new PagamentoDTO(false, 0L));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.finalizarCompra(carrinho.getId(), cliente.getId())
        );

        assertEquals("Pagamento não autorizado.", ex.getMessage());

        verify(estoqueExternal, times(1)).verificarDisponibilidade(ids, qts);
        verify(pagamentoExternal, times(1))
                .autorizarPagamento(eq(cliente.getId()), anyDouble());
        verify(estoqueExternal, never()).darBaixa(anyList(), anyList());
        verify(pagamentoExternal, never())
                .cancelarPagamento(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Falha na baixa de estoque: cancela pagamento e lança exceção")
    void deveCancelarPagamentoQuandoFalhaNaBaixaDeEstoque() {
        var ids = List.of(1L);
        var qts = List.of(1L);

        when(estoqueExternal.verificarDisponibilidade(ids, qts))
                .thenReturn(new DisponibilidadeDTO(true, List.of()));

        when(pagamentoExternal.autorizarPagamento(eq(cliente.getId()), anyDouble()))
                .thenReturn(new PagamentoDTO(true, 3000L));

        when(estoqueExternal.darBaixa(ids, qts))
                .thenReturn(new EstoqueBaixaDTO(false));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.finalizarCompra(carrinho.getId(), cliente.getId())
        );

        assertEquals("Erro ao dar baixa no estoque.", ex.getMessage());

        verify(estoqueExternal, times(1)).verificarDisponibilidade(ids, qts);
        verify(pagamentoExternal, times(1))
                .autorizarPagamento(eq(cliente.getId()), anyDouble());
        verify(estoqueExternal, times(1)).darBaixa(ids, qts);
        verify(pagamentoExternal, times(1))
                .cancelarPagamento(eq(cliente.getId()), eq(3000L));
    }

    static class FakeClienteRepository implements ClienteRepository {
        private final Map<Long, Cliente> store = new HashMap<>();

        public void salvar(Cliente c) {
            store.put(c.getId(), c);
        }

        @Override
        public Optional<Cliente> findById(Long id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public boolean existsById(Long aLong) {
            return false;
        }

        @Override
        public void flush() {

        }

        @Override
        public <S extends Cliente> S saveAndFlush(S entity) {
            return null;
        }

        @Override
        public <S extends Cliente> List<S> saveAllAndFlush(Iterable<S> entities) {
            return List.of();
        }

        @Override
        public void deleteAllInBatch(Iterable<Cliente> entities) {

        }

        @Override
        public void deleteAllByIdInBatch(Iterable<Long> longs) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public Cliente getOne(Long aLong) {
            return null;
        }

        @Override
        public Cliente getById(Long aLong) {
            return null;
        }

        @Override
        public Cliente getReferenceById(Long aLong) {
            return null;
        }

        @Override
        public <S extends Cliente> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends Cliente> List<S> findAll(Example<S> example) {
            return List.of();
        }

        @Override
        public <S extends Cliente> List<S> findAll(Example<S> example, Sort sort) {
            return List.of();
        }

        @Override
        public <S extends Cliente> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Cliente> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends Cliente> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public <S extends Cliente, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }

        @Override
        public <S extends Cliente> S save(S entity) {
            return null;
        }

        @Override
        public <S extends Cliente> List<S> saveAll(Iterable<S> entities) {
            return List.of();
        }

        @Override
        public List<Cliente> findAll() {
            return List.of();
        }

        @Override
        public List<Cliente> findAllById(Iterable<Long> longs) {
            return List.of();
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(Long aLong) {

        }

        @Override
        public void delete(Cliente entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends Long> longs) {

        }

        @Override
        public void deleteAll(Iterable<? extends Cliente> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public List<Cliente> findAll(Sort sort) {
            return List.of();
        }

        @Override
        public Page<Cliente> findAll(Pageable pageable) {
            return null;
        }

    }

    static class FakeCarrinhoDeComprasRepository implements CarrinhoDeComprasRepository {
        private final Map<Long, CarrinhoDeCompras> store = new HashMap<>();

        public void salvar(CarrinhoDeCompras c) {
            store.put(c.getId(), c);
        }

        @Override
        public Optional<CarrinhoDeCompras> findByIdAndCliente(Long id, Cliente cliente) {
            CarrinhoDeCompras carrinho = store.get(id);
            if (carrinho != null && carrinho.getCliente().getId().equals(cliente.getId())) {
                return Optional.of(carrinho);
            }
            return Optional.empty();
        }

        @Override
        public void flush() {

        }

        @Override
        public <S extends CarrinhoDeCompras> S saveAndFlush(S entity) {
            return null;
        }

        @Override
        public <S extends CarrinhoDeCompras> List<S> saveAllAndFlush(Iterable<S> entities) {
            return List.of();
        }

        @Override
        public void deleteAllInBatch(Iterable<CarrinhoDeCompras> entities) {

        }

        @Override
        public void deleteAllByIdInBatch(Iterable<Long> longs) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public CarrinhoDeCompras getOne(Long aLong) {
            return null;
        }

        @Override
        public CarrinhoDeCompras getById(Long aLong) {
            return null;
        }

        @Override
        public CarrinhoDeCompras getReferenceById(Long aLong) {
            return null;
        }

        @Override
        public <S extends CarrinhoDeCompras> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends CarrinhoDeCompras> List<S> findAll(Example<S> example) {
            return List.of();
        }

        @Override
        public <S extends CarrinhoDeCompras> List<S> findAll(Example<S> example, Sort sort) {
            return List.of();
        }

        @Override
        public <S extends CarrinhoDeCompras> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends CarrinhoDeCompras> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends CarrinhoDeCompras> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public <S extends CarrinhoDeCompras, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }

        @Override
        public <S extends CarrinhoDeCompras> S save(S entity) {
            return null;
        }

        @Override
        public <S extends CarrinhoDeCompras> List<S> saveAll(Iterable<S> entities) {
            return List.of();
        }

        @Override
        public Optional<CarrinhoDeCompras> findById(Long aLong) {
            return Optional.empty();
        }

        @Override
        public boolean existsById(Long aLong) {
            return false;
        }

        @Override
        public List<CarrinhoDeCompras> findAll() {
            return List.of();
        }

        @Override
        public List<CarrinhoDeCompras> findAllById(Iterable<Long> longs) {
            return List.of();
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(Long aLong) {

        }

        @Override
        public void delete(CarrinhoDeCompras entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends Long> longs) {

        }

        @Override
        public void deleteAll(Iterable<? extends CarrinhoDeCompras> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public List<CarrinhoDeCompras> findAll(Sort sort) {
            return List.of();
        }

        @Override
        public Page<CarrinhoDeCompras> findAll(Pageable pageable) {
            return null;
        }

    }
}
