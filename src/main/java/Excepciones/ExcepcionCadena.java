package Excepciones;

public class ExcepcionCadena extends ExcepcionLisp {
    public ExcepcionCadena(String mensaje) {
        super(mensaje);
    }

    public ExcepcionCadena(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}