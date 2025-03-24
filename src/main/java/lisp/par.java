package lisp;

import Excepciones.ExcepcionLisp;

import java.io.PrintStream;

/**
 * Representa una celda cons (par) en LISP, que es el bloque básico de construcción para las listas.
 * Esta clase implementa la estructura de datos fundamental que permite crear listas enlazadas
 * y árboles de expresiones en LISP.
 */
public class par extends ExpresionLisp {
    /** El primer elemento del par */
    private final ExpresionLisp primero;

    /** El segundo elemento del par (resto de la lista) */
    private final ExpresionLisp resto;

    /**
     * Construye un nuevo par con los elementos especificados.
     *
     * @param primero el primer elemento del par
     * @param resto el segundo elemento del par (resto de la lista)
     */
    public par(ExpresionLisp primero, ExpresionLisp resto) {
        this.primero = primero;
        this.resto = resto;
    }

    /**
     * Obtiene el primer elemento del par.
     *
     * @return el primer elemento del par
     */
    @Override
    public ExpresionLisp primero() {
        return primero;
    }

    /**
     * Obtiene el resto del par.
     *
     * @return el segundo elemento del par (resto de la lista)
     */
    @Override
    public ExpresionLisp resto() {
        return resto;
    }

    /**
     * Crea una lista LISP a partir de los elementos proporcionados.
     *
     * @param elementos los elementos a incluir en la lista
     * @return una nueva lista LISP que contiene los elementos especificados
     */
    public static ExpresionLisp crearLista(ExpresionLisp... elementos) {
        ExpresionLisp resultado = simbolo.NULO;
        for (int i = elementos.length - 1; i >= 0; i--) {
            resultado = new par(elementos[i], resultado);
        }
        return resultado;
    }

    /**
     * Verifica si este par representa una lista LISP adecuada.
     * Una lista adecuada es aquella que termina con el símbolo NULO.
     *
     * @return true si este par representa una lista adecuada, false en caso contrario
     */
    public boolean esLista() {
        ExpresionLisp actual = this;
        while (!(actual.esAtomo())) {
            try {
                actual = actual.resto();
            } catch (ExcepcionLisp e) {
                return false;
            }
        }
        return actual == simbolo.NULO;
    }

    /**
     * Calcula la longitud de la lista representada por este par.
     *
     * @return la longitud de la lista, o -1 si no es una lista adecuada
     */
    public int longitud() {
        if (!esLista()) return -1;

        int longitud = 0;
        ExpresionLisp actual = this;
        while (actual != simbolo.NULO) {
            longitud++;
            try {
                actual = actual.resto();
            } catch (ExcepcionLisp e) {
                return -1;
            }
        }
        return longitud;
    }

    /**
     * Imprime la representación textual de este par en el flujo de salida especificado.
     * La representación sigue la sintaxis de LISP estándar para listas y pares punteados.
     *
     * @param salida el flujo de salida donde se imprimirá la representación
     */
    @Override
    public void imprimir(PrintStream salida) {
        salida.print("(");
        primero.imprimir(salida);

        ExpresionLisp restoLista = resto;
        while (!(restoLista.esAtomo()) && restoLista != simbolo.NULO) {
            salida.print(" ");
            try {
                restoLista.primero().imprimir(salida);
                restoLista = restoLista.resto();
            } catch (ExcepcionLisp e) {
                break;
            }
        }

        if (restoLista != simbolo.NULO) {
            salida.print(" . ");
            restoLista.imprimir(salida);
        }

        salida.print(")");
    }
}