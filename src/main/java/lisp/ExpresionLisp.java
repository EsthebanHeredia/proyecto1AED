package lisp;

import Excepciones.ExcepcionAtomo;

import java.io.PrintStream;

/**
 * Clase abstracta base para todas las expresiones en el intérprete LISP.
 * Define la interfaz común que todas las expresiones LISP deben implementar.
 */
public abstract class ExpresionLisp {
    /**
     * Obtiene el primer elemento de una expresión LISP.
     *
     * @return el primer elemento de la expresión
     * @throws ExcepcionAtomo si la expresión es un átomo y no tiene primer elemento
     */
    public abstract ExpresionLisp primero() throws ExcepcionAtomo;

    /**
     * Obtiene el resto de elementos de una expresión LISP.
     *
     * @return el resto de la expresión
     * @throws ExcepcionAtomo si la expresión es un átomo y no tiene resto
     */
    public abstract ExpresionLisp resto() throws ExcepcionAtomo;

    /**
     * Verifica si esta expresión es un átomo.
     *
     * @return true si la expresión es un átomo, false en caso contrario
     */
    public boolean esAtomo() {
        return false;
    }

    /**
     * Verifica si esta expresión es un símbolo.
     *
     * @return true si la expresión es un símbolo, false en caso contrario
     */
    public boolean esSimbolo() {
        return false;
    }

    /**
     * Verifica si esta expresión es un número.
     *
     * @return true si la expresión es un número, false en caso contrario
     */
    public boolean esNumero() {
        return false;
    }

    /**
     * Verifica si esta expresión es una cadena.
     *
     * @return true si la expresión es una cadena, false en caso contrario
     */
    public boolean esCadena() {
        return false;
    }

    /**
     * Imprime la representación textual de la expresión en el flujo de salida especificado.
     *
     * @param salida el flujo de salida donde se imprimirá la expresión
     */
    public abstract void imprimir(PrintStream salida);

    /**
     * Devuelve una representación en cadena de la expresión.
     *
     * @return la representación en cadena de la expresión
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        PrintStream ps = new PrintStream(System.out) {
            @Override
            public void print(String s) {
                sb.append(s);
            }

            @Override
            public void print(long l) {
                sb.append(l);
            }
        };
        imprimir(ps);
        return sb.toString();
    }
}