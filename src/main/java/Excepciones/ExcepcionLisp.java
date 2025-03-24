package Excepciones;

public class ExcepcionLisp extends Exception {
    public ExcepcionLisp(String mensaje) {
        super(mensaje);
    }

    public ExcepcionLisp(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
