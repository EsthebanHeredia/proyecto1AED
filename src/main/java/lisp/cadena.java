package lisp;

import java.io.PrintStream;

/**
 * Representa un valor de cadena en el intérprete LISP.
 * Esta clase extiende atomo para manejar cadenas de texto como valores atómicos.
 */
public class cadena extends atomo {
    /** El valor de la cadena almacenada */
    private final String valor;

    /**
     * Construye un nuevo objeto cadena con el valor especificado.
     *
     * @param valor la cadena de texto a almacenar
     */
    public cadena(String valor) {
        this.valor = valor;
    }

    /**
     * Obtiene el valor de la cadena almacenada.
     *
     * @return el valor de la cadena como String
     */
    public String obtenerValor() {
        return valor;
    }

    /**
     * Indica si esta expresión es una cadena.
     *
     * @return true, ya que esta clase representa cadenas
     */
    @Override
    public boolean esCadena() {
        return true;
    }

    /**
     * Imprime la representación de la cadena en el flujo de salida especificado.
     * La cadena se imprime entre comillas dobles.
     *
     * @param salida el flujo de salida donde se imprimirá la cadena
     */
    @Override
    public void imprimir(PrintStream salida) {
        salida.print("\"" + valor + "\"");
    }

    /**
     * Compara esta cadena con otro objeto para determinar la igualdad.
     *
     * @param obj el objeto a comparar con esta cadena
     * @return true si el objeto es una cadena con el mismo valor
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof cadena)) return false;
        return valor.equals(((cadena) obj).valor);
    }

    /**
     * Calcula el código hash de esta cadena.
     *
     * @return el código hash basado en el valor de la cadena
     */
    @Override
    public int hashCode() {
        return valor.hashCode();
    }
}