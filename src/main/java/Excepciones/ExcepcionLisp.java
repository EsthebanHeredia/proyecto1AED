package Excepciones;

/**
 * Base exception class for the LISP interpreter.
 * Provides a foundation for all custom exceptions in the interpreter,
 * extending the standard Java Exception class.
 */
public class ExcepcionLisp extends Exception {
    /**
     * Constructs a LISP exception with the specified error message.
     *
     * @param mensaje the error message detailing the cause of the exception
     */
    public ExcepcionLisp(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructs a LISP exception with the specified error message and cause.
     *
     * @param mensaje the error message detailing the cause of the exception
     * @param causa   the underlying cause of the exception
     */
    public ExcepcionLisp(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}