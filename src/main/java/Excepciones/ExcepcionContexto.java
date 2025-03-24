package Excepciones;

public class ExcepcionContexto extends ExcepcionLisp {
    public ExcepcionContexto(String mensaje) {
        super(mensaje);
    }

    public ExcepcionContexto(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}