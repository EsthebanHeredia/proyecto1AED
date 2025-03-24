package lisp;

import Excepciones.ExcepcionSimbolo;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Representa un símbolo en el intérprete LISP.
 * Esta clase implementa el manejo de símbolos únicos mediante un sistema de tabla
 * de símbolos internos, asegurando que cada nombre de símbolo corresponda a una
 * única instancia.
 */
public class simbolo extends atomo {
    /** Tabla que almacena todos los símbolos internados */
    private static final Map<String, simbolo> tablaSimbolo = new ConcurrentHashMap<>();

    /**
     * Método privado para inicializar símbolos de manera segura.
     *
     * @param nombre el nombre del símbolo a inicializar
     * @return el símbolo inicializado
     */
    public static simbolo inicializarSimbolo(String nombre) {
        try {
            return internamente(nombre);
        } catch (ExcepcionSimbolo e) {
            throw new RuntimeException("Error al inicializar símbolo: " + nombre, e);
        }
    }

    /** Símbolos constantes del sistema */
    public static final simbolo NULO = inicializarSimbolo("NIL");
    public static final simbolo VERDADERO = inicializarSimbolo("T");
    public static final simbolo CITAR = inicializarSimbolo("QUOTE");
    public static final simbolo ASIGNAR = inicializarSimbolo("SET");
    public static final simbolo DEFUN = inicializarSimbolo("DEFUN");
    public static final simbolo CONDICIONAL = inicializarSimbolo("COND");

    /** Funciones estándar */
    public static final simbolo PRIMERO = inicializarSimbolo("CAR");
    public static final simbolo RESTO = inicializarSimbolo("CDR");
    public static final simbolo CONSTRUIR = inicializarSimbolo("CONS");
    public static final simbolo LISTA = inicializarSimbolo("LIST");
    public static final simbolo ES_ATOMO = inicializarSimbolo("ATOM");
    public static final simbolo ES_IGUAL_REF = inicializarSimbolo("EQ");
    public static final simbolo ES_IGUAL = inicializarSimbolo("EQUAL");
    public static final simbolo ES_LISTA = inicializarSimbolo("LIST?");
    public static final simbolo IMPRIMIR = inicializarSimbolo("PRINT");

    /** Operaciones con cadenas */
    public static final simbolo CONCATENAR = inicializarSimbolo("CONCAT");
    public static final simbolo LONGITUD_CADENA = inicializarSimbolo("LENGTH");

    /** Operadores aritméticos */
    public static final simbolo SUMA = inicializarSimbolo("+");
    public static final simbolo SUMAR = inicializarSimbolo("ADD");
    public static final simbolo RESTA = inicializarSimbolo("-");
    public static final simbolo RESTAR = inicializarSimbolo("SUB");
    public static final simbolo MULTIPLICA = inicializarSimbolo("*");
    public static final simbolo MULTIPLICAR = inicializarSimbolo("MUL");
    public static final simbolo DIVIDE = inicializarSimbolo("/");
    public static final simbolo DIVIDIR = inicializarSimbolo("DIV");

    /** Operadores de comparación */
    public static final simbolo MENOR = inicializarSimbolo("<");
    public static final simbolo MENOR_QUE = inicializarSimbolo("LESS");
    public static final simbolo MAYOR = inicializarSimbolo(">");
    public static final simbolo MAYOR_QUE = inicializarSimbolo("GREATER");
    public static final simbolo IGUAL = inicializarSimbolo("=");
    public static final simbolo ES_IGUAL_VALOR = inicializarSimbolo("EQUAL?");

    /** El nombre del símbolo */
    private final String nombre;

    /**
     * Constructor privado para crear nuevos símbolos.
     *
     * @param nombre el nombre del símbolo
     */
    private simbolo(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Interna un símbolo en la tabla de símbolos.
     *
     * @param nombre el nombre del símbolo a internar
     * @return el símbolo internado
     * @throws ExcepcionSimbolo si el nombre es nulo o vacío
     */
    public static simbolo internamente(String nombre) throws ExcepcionSimbolo {
        if (nombre == null || nombre.isEmpty()) {
            throw new ExcepcionSimbolo("El nombre del símbolo no puede ser nulo o vacío");
        }
        return tablaSimbolo.computeIfAbsent(nombre.toUpperCase(), simbolo::new);
    }

    /**
     * Obtiene el nombre del símbolo.
     *
     * @return el nombre del símbolo
     */
    public String obtenerNombre() {
        return nombre;
    }

    /**
     * Imprime el nombre del símbolo en el flujo de salida especificado.
     *
     * @param salida el flujo de salida donde se imprimirá el símbolo
     */
    @Override
    public void imprimir(PrintStream salida) {
        salida.print(nombre);
    }

    /**
     * Indica si esta expresión es un símbolo.
     *
     * @return true, ya que esta clase representa símbolos
     */
    @Override
    public boolean esSimbolo() {
        return true;
    }

    /**
     * Devuelve una representación en cadena del símbolo.
     *
     * @return el nombre del símbolo
     */
    @Override
    public String toString() {
        return nombre;
    }

    /**
     * Compara este símbolo con otro objeto para determinar la igualdad.
     * Los símbolos son iguales solo si son la misma instancia.
     *
     * @param obj el objeto a comparar con este símbolo
     * @return true si son el mismo símbolo
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Calcula el código hash de este símbolo.
     *
     * @return el código hash basado en el nombre del símbolo
     */
    @Override
    public int hashCode() {
        return nombre.hashCode();
    }
}
