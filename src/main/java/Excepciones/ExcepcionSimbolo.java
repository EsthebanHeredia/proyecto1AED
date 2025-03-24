package Excepciones;

public class ExcepcionSimbolo extends ExcepcionLisp {
    public ExcepcionSimbolo(String mensaje) {
        super(mensaje);
    }

    public ExcepcionSimbolo(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}