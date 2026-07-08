package br.com.fiap.sout.catalogo.domain.exceptions;

public class VeiculoNaoEncontradoException extends RuntimeException {
    public VeiculoNaoEncontradoException(String message) {
        super(message);
    }
}
