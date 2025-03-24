package lisp;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Representa un valor numérico en el intérprete LISP.
 * Esta clase implementa un sistema de caché para números frecuentemente utilizados
 * y extiende la clase atomo para manejar valores numéricos.
 */
public class numero extends atomo {
    /** Caché de números para reutilización de instancias */
    private static final Map<Long, numero> cacheNumeros = new ConcurrentHashMap<>();

    /** Instancia constante que representa el número cero */
    public static final numero CERO = obtenerValor(0);

    /** Instancia constante que representa el número uno */
    public static final numero UNO = obtenerValor(1);

    /** El valor numérico almacenado */
    private final long valor;

    /**
     * Constructor privado para crear nuevas instancias de número.
     * El acceso a nuevas instancias debe hacerse a través del método obtenerValor().
     *
     * @param valor el valor numérico a almacenar
     */
    private numero(long valor) {
        this.valor = valor;
    }

    /**
     * Obtiene una instancia de número para el valor especificado.
     * Utiliza un sistema de caché para reutilizar instancias de números comunes.
     *
     * @param valor el valor numérico deseado
     * @return una instancia de número que representa el valor dado
     */
    public static numero obtenerValor(long valor) {
        return cacheNumeros.computeIfAbsent(valor, numero::new);
    }

    /**
     * Obtiene el valor numérico almacenado.
     *
     * @return el valor numérico como long
     */
    public long obtenerValor() {
        return valor;
    }

    /**
     * Indica si esta expresión es un número.
     *
     * @return true, ya que esta clase representa números
     */
    @Override
    public boolean esNumero() {
        return true;
    }

    /**
     * Imprime el valor numérico en el flujo de salida especificado.
     *
     * @param salida el flujo de salida donde se imprimirá el número
     */
    @Override
    public void imprimir(PrintStream salida) {
        salida.print(valor);
    }

    /**
     * Compara este número con otro objeto para determinar la igualdad.
     *
     * @param obj el objeto a comparar con este número
     * @return true si el objeto es un número con el mismo valor
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof numero)) return false;
        return valor == ((numero) obj).valor;
    }

    /**
     * Calcula el código hash de este número.
     *
     * @return el código hash basado en el valor numérico
     */
    @Override
    public int hashCode() {
        return Long.hashCode(valor);
    }
}
