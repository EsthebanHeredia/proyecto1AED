package Excepciones;

/**
 * Clase de excepción personalizada para manejar errores del analizador en el intérprete LISP.
 * Extiende ExcepcionLisp para mantener la jerarquía de excepciones del intérprete.
 */
public class ExcepcionAnalizador extends ExcepcionLisp {
    /**
     * Construye una excepción del analizador con el mensaje de error especificado.
     *
     * @param mensaje el mensaje de error que detalla la causa de la excepción
     */
    public ExcepcionAnalizador(String mensaje) {
        super(mensaje);
    }

    /**
     * Construye una excepción del analizador con el mensaje de error y la causa especificados.
     *
     * @param mensaje el mensaje de error que detalla la causa de la excepción
     * @param causa   la causa subyacente de la excepción
     */
    public ExcepcionAnalizador(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}