package ecommerce.external.fake;

import org.springframework.stereotype.Service;

import ecommerce.dto.PagamentoDTO;
import ecommerce.external.IPagamentoExternal;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class PagamentoSimulado implements IPagamentoExternal
{
    private final AtomicLong transacaoCounter = new AtomicLong(1000);

    private volatile boolean autorizarPadrao = true;

    @Override
    public PagamentoDTO autorizarPagamento(Long clienteId, Double custoTotal)
    {
        if (!autorizarPadrao) {
            return new PagamentoDTO(false, null);
        }

        if (custoTotal == null || custoTotal <= 0.0) {
            return new PagamentoDTO(false, null);
        }

        Long id = transacaoCounter.getAndIncrement();
        return new PagamentoDTO(true, id);
    }

    @Override
    public void cancelarPagamento(Long clienteId, Long pagamentoTransacaoId)
    {
        // simula cancelamento com retorno vazio
    }

    public void setAutorizarPadrao(boolean autorizarPadrao) {
        this.autorizarPadrao = autorizarPadrao;
    }

    public void resetTransacaoCounter(long inicio) {
        transacaoCounter.set(inicio);
    }
}
