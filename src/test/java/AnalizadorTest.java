import Excepciones.ExcepcionLisp;
import lisp.*;
import org.junit.jupiter.api.Test;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.*;

public class AnalizadorTest {

    @Test
    public void testAnalizarCadena() throws ExcepcionLisp {
        String input = "\"Hola Mundo\"";
        analizador analizador = new analizador(new StringReader(input));
        ExpresionLisp resultado = analizador.analizar();
        assertTrue(resultado instanceof cadena);
        assertEquals("Hola Mundo", ((cadena) resultado).obtenerValor());
    }

    @Test
    public void testAnalizarNumero() throws ExcepcionLisp {
        String input = "123";
        analizador analizador = new analizador(new StringReader(input));
        ExpresionLisp resultado = analizador.analizar();
        assertTrue(resultado instanceof numero);
        assertEquals(123, ((numero) resultado).obtenerValor());
    }

    @Test
    public void testAnalizarSimbolo() throws ExcepcionLisp {
        String input = "SIMBOLO";
        analizador analizador = new analizador(new StringReader(input));
        ExpresionLisp resultado = analizador.analizar();
        assertTrue(resultado instanceof simbolo);
        assertEquals("SIMBOLO", ((simbolo) resultado).obtenerNombre());
    }
}