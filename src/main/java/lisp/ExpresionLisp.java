package lisp;

import Excepciones.ExcepcionAtomo;

import java.io.PrintStream;

public abstract class ExpresionLisp {
    public abstract ExpresionLisp primero() throws ExcepcionAtomo;
    public abstract ExpresionLisp resto() throws ExcepcionAtomo;

    public boolean esAtomo() {
        return false;
    }

    public boolean esSimbolo() {
        return false;
    }

    public boolean esNumero() {
        return false;
    }

    public boolean esCadena() {
        return false;
    }

    public abstract void imprimir(PrintStream salida);

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