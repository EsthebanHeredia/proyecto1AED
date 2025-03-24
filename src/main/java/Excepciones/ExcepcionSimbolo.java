package Excepciones;

/**
 * Clase de excepción personalizada para manejar errores relacionados con símbolos en el intérprete LISP.
 * Extiende ExcepcionLisp para mantener la jerarquía de excepciones del intérprete.
 */
public class ExcepcionSimbolo extends ExcepcionLisp {
    /**
     * Construye una excepción de símbolo con el mensaje de error especificado.
     *
     * @param mensaje el mensaje de error que detalla la causa de la excepción
     */
    public ExcepcionSimbolo(String mensaje) {
        super(mensaje);
    }

    /**
     * Construye una excepción de símbolo con el mensaje de error y la causa especificados.
     *
     * @param mensaje el mensaje de error que detalla la causa de la excepción
     * @param causa   la causa subyacente de la excepción
     */
    public ExcepcionSimbolo(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}