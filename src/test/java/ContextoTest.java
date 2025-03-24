import Excepciones.ExcepcionContexto;
import lisp.*;
import lisp.contexto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ContextoTest {

    @Test
    public void testObtenerYEstablecer() throws ExcepcionContexto {
        contexto ctx = new contexto();
        simbolo sym = simbolo.inicializarSimbolo("X");
        numero num = numero.obtenerValor(42);
        ctx.establecer(sym, num);
        assertEquals(num, ctx.obtener(sym));
    }

    @Test
    public void testActualizar() throws ExcepcionContexto {
        contexto ctx = new contexto();
        simbolo sym = simbolo.inicializarSimbolo("X");
        numero num1 = numero.obtenerValor(42);
        numero num2 = numero.obtenerValor(43);
        ctx.establecer(sym, num1);
        ctx.actualizar(sym, num2);
        assertEquals(num2, ctx.obtener(sym));
    }

    @Test
    public void testExtender() throws ExcepcionContexto {
        contexto ctx = new contexto();
        simbolo param = simbolo.inicializarSimbolo("X");
        numero arg = numero.obtenerValor(42);
        ExpresionLisp parametros = new par(param, simbolo.NULO);
        ExpresionLisp argumentos = new par(arg, simbolo.NULO);
        contexto nuevoCtx = ctx.extender(parametros, argumentos);
        assertEquals(arg, nuevoCtx.obtener(param));
    }
}