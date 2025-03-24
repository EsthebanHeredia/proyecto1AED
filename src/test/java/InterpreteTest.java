import Excepciones.ExcepcionLisp;
import lisp.*;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

public class InterpreteTest {

    @Test
    public void testEvaluarNumero() throws ExcepcionLisp {
        Interprete interprete = new Interprete();
        ExpresionLisp resultado = interprete.evaluar("123");
        assertTrue(resultado instanceof numero);
        assertEquals(123, ((numero) resultado).obtenerValor());
    }

    @Test
    public void testEvaluarCadena() throws ExcepcionLisp {
        Interprete interprete = new Interprete();
        ExpresionLisp resultado = interprete.evaluar("\"Hola Mundo\"");
        assertTrue(resultado instanceof cadena);
        assertEquals("Hola Mundo", ((cadena) resultado).obtenerValor());
    }

    @Test
    public void testEvaluarSimbolo() throws ExcepcionLisp {
        Interprete interprete = new Interprete();
        ExpresionLisp resultado = interprete.evaluar("T");
        assertTrue(resultado instanceof simbolo);
        assertEquals(simbolo.VERDADERO, resultado);
    }

    @Test
    public void testImprimir() throws ExcepcionLisp {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Interprete interprete = new Interprete(System.in, new PrintStream(salida));
        interprete.evaluar("(PRINT \"Hola Mundo\")");
        assertTrue(salida.toString().contains("Hola Mundo"));
    }

    @Test
    public void testEvaluarLista() throws ExcepcionLisp {
        Interprete interprete = new Interprete();
        ExpresionLisp resultado = interprete.evaluar("(LIST 1 2 3)");
        assertTrue(resultado instanceof par);
        assertEquals(3, ((par) resultado).longitud());
    }

    @Test
    public void testEvaluarCondicional() throws ExcepcionLisp {
        Interprete interprete = new Interprete();
        ExpresionLisp resultado = interprete.evaluar("(COND ((> 3 2) 'MAYOR) ((< 3 2) 'MENOR))");
        assertTrue(resultado instanceof simbolo);
        assertEquals("MAYOR", ((simbolo) resultado).obtenerNombre());
    }

    @Test
    public void testEvaluarConcatenarCadenas() throws ExcepcionLisp {
        Interprete interprete = new Interprete();
        ExpresionLisp resultado = interprete.evaluar("(CONCAT \"Hola\" \" \" \"Mundo\")");
        assertTrue(resultado instanceof cadena);
        assertEquals("Hola Mundo", ((cadena) resultado).obtenerValor());
    }

}