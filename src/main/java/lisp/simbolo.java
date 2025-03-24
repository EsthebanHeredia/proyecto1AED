package lisp;

import Excepciones.ExcepcionSimbolo;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class simbolo extends atomo {
    private static final Map<String, simbolo> tablaSimbolo = new ConcurrentHashMap<>();

    // Private method to initialize symbols safely
    public static simbolo inicializarSimbolo(String nombre) {
        try {
            return internamente(nombre);
        } catch (ExcepcionSimbolo e) {
            throw new RuntimeException("Error al inicializar símbolo: " + nombre, e);
        }
    }

    // Constants
    public static final simbolo NULO = inicializarSimbolo("NIL");
    public static final simbolo VERDADERO = inicializarSimbolo("T");
    public static final simbolo CITAR = inicializarSimbolo("QUOTE");
    public static final simbolo ASIGNAR = inicializarSimbolo("SET");
    public static final simbolo DEFUN = inicializarSimbolo("DEFUN");
    public static final simbolo CONDICIONAL = inicializarSimbolo("COND");

    // Standard functions
    public static final simbolo PRIMERO = inicializarSimbolo("CAR");
    public static final simbolo RESTO = inicializarSimbolo("CDR");
    public static final simbolo CONSTRUIR = inicializarSimbolo("CONS");
    public static final simbolo LISTA = inicializarSimbolo("LIST");
    public static final simbolo ES_ATOMO = inicializarSimbolo("ATOM");
    public static final simbolo ES_IGUAL_REF = inicializarSimbolo("EQ");
    public static final simbolo ES_IGUAL = inicializarSimbolo("EQUAL");
    public static final simbolo ES_LISTA = inicializarSimbolo("LIST?");
    public static final simbolo IMPRIMIR = inicializarSimbolo("PRINT");

    // String operations
    public static final simbolo CONCATENAR = inicializarSimbolo("CONCAT");
    public static final simbolo LONGITUD_CADENA = inicializarSimbolo("LENGTH");

    // Arithmetic operators
    public static final simbolo SUMA = inicializarSimbolo("+");
    public static final simbolo SUMAR = inicializarSimbolo("ADD");
    public static final simbolo RESTA = inicializarSimbolo("-");
    public static final simbolo RESTAR = inicializarSimbolo("SUB");
    public static final simbolo MULTIPLICA = inicializarSimbolo("*");
    public static final simbolo MULTIPLICAR = inicializarSimbolo("MUL");
    public static final simbolo DIVIDE = inicializarSimbolo("/");
    public static final simbolo DIVIDIR = inicializarSimbolo("DIV");

    // Comparison operators
    public static final simbolo MENOR = inicializarSimbolo("<");
    public static final simbolo MENOR_QUE = inicializarSimbolo("LESS");
    public static final simbolo MAYOR = inicializarSimbolo(">");
    public static final simbolo MAYOR_QUE = inicializarSimbolo("GREATER");
    public static final simbolo IGUAL = inicializarSimbolo("=");
    public static final simbolo ES_IGUAL_VALOR = inicializarSimbolo("EQUAL?");

    private final String nombre;

    private simbolo(String nombre) {
        this.nombre = nombre;
    }

    public static simbolo internamente(String nombre) throws ExcepcionSimbolo {
        if (nombre == null || nombre.isEmpty()) {
            throw new ExcepcionSimbolo("El nombre del símbolo no puede ser nulo o vacío");
        }
        return tablaSimbolo.computeIfAbsent(nombre.toUpperCase(), simbolo::new);
    }

    public String obtenerNombre() {
        return nombre;
    }

    @Override
    public void imprimir(PrintStream salida) {
        salida.print(nombre);
    }

    @Override
    public boolean esSimbolo() {
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return nombre.hashCode();
    }
}
