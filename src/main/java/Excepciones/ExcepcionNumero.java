package Excepciones;

public class ExcepcionNumero extends ExcepcionLisp {
    public ExcepcionNumero(String mensaje) {
        super(mensaje);
    }

    public ExcepcionNumero(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}