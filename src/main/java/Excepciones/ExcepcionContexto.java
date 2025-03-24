package Excepciones;

/**
 * Clase de excepción personalizada para manejar errores relacionados con el contexto en el intérprete LISP.
 * Extiende ExcepcionLisp para mantener la jerarquía de excepciones del intérprete.
 */
public class ExcepcionContexto extends ExcepcionLisp {
    /**
     * Construye una excepción de contexto con el mensaje de error especificado.
     *
     * @param mensaje el mensaje de error que detalla la causa de la excepción
     */
    public ExcepcionContexto(String mensaje) {
        super(mensaje);
    }

    /**
     * Construye una excepción de contexto con el mensaje de error y la causa especificados.
     *
     * @param mensaje el mensaje de error que detalla la causa de la excepción
     * @param causa   la causa subyacente de la excepción
     */
    public ExcepcionContexto(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}