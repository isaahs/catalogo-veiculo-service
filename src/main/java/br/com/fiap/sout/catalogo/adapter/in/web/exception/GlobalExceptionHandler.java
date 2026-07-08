package br.com.fiap.sout.catalogo.adapter.in.web.exception;

import br.com.fiap.sout.catalogo.domain.exceptions.PlacaAlteradaException;
import br.com.fiap.sout.catalogo.domain.exceptions.VeiculoNaoEncontradoException;
import br.com.fiap.sout.catalogo.domain.exceptions.VeiculoVendidoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VeiculoNaoEncontradoException.class)
    public ProblemDetail handleVeiculoNaoEncontrado(VeiculoNaoEncontradoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(VeiculoVendidoException.class)
    public ProblemDetail handleVeiculoVendido(VeiculoVendidoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Erro de validação dos dados enviados.");
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(PlacaAlteradaException.class)
    public ProblemDetail handlePlacaAlterada(PlacaAlteradaException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Erro inesperado", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado.");
    }
}
