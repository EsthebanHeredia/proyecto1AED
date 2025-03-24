package Excepciones;

public class ExcepcionAtomo extends ExcepcionLisp {
    public ExcepcionAtomo(String mensaje) {
        super(mensaje);
    }

    public ExcepcionAtomo(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}