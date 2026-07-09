package br.com.fiap.sout.catalogo.domain.exceptions;

public class ServicoVendasIndisponivelException extends RuntimeException {
    public ServicoVendasIndisponivelException(String message, Throwable cause) {
        super(message, cause);
    }
}
