package Excepciones;

/**
 * Clase de excepción personalizada para manejar errores relacionados con pares en el intérprete LISP.
 * Extiende ExcepcionLisp para mantener la jerarquía de excepciones del intérprete.
 */
public class ExcepcionPar extends ExcepcionLisp {
    /**
     * Construye una excepción de par con el mensaje de error especificado.
     *
     * @param mensaje el mensaje de error que detalla la causa de la excepción
     */
    public ExcepcionPar(String mensaje) {
        super(mensaje);
    }

    /**
     * Construye una excepción de par con el mensaje de error y la causa especificados.
     *
     * @param mensaje el mensaje de error que detalla la causa de la excepción
     * @param causa   la causa subyacente de la excepción
     */
    public ExcepcionPar(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}