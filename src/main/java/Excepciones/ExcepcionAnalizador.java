package Excepciones;

public class ExcepcionAnalizador extends ExcepcionLisp {
    public ExcepcionAnalizador(String mensaje) {
        super(mensaje);
    }

    public ExcepcionAnalizador(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
