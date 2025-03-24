package lisp;



import java.io.PrintStream;

/**
 * Representa un valor de cadena en LISP.
 */
public class
cadena extends atomo {
    private final String valor;

    public cadena(String valor) {
        this.valor = valor;
    }

    /**
     * Obtiene el valor de esta cadena.
     */
    public String obtenerValor() {
        return valor;
    }

    @Override
    public boolean esCadena() {
        return true;
    }

    @Override
    public void imprimir(PrintStream salida) {
        salida.print("\"" + valor + "\"");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof cadena)) return false;
        return valor.equals(((cadena) obj).valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }
}
