package Excepciones;

public class ExcepcionPar extends ExcepcionLisp {
    public ExcepcionPar(String mensaje) {
        super(mensaje);
    }

    public ExcepcionPar(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}