import Excepciones.ExcepcionLisp;
import lisp.*;
import org.junit.jupiter.api.Test;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.*;

class AnalizadorTest {
    @Test
    void testAnalizarCadena() throws ExcepcionLisp {
        String input = "\"Hola Mundo\"";
        analizador analizador = new analizador(new StringReader(input));
        ExpresionLisp resultado = analizador.analizar();
        assertTrue(resultado instanceof cadena);
        assertEquals("Hola Mundo", ((cadena) resultado).obtenerValor());
    }

    @Test
    void testAnalizarCadenaVacia() throws ExcepcionLisp {
        String input = "\"\"";
        analizador analizador = new analizador(new StringReader(input));
        ExpresionLisp resultado = analizador.analizar();
        assertTrue(resultado instanceof cadena);
        assertEquals("", ((cadena) resultado).obtenerValor());
    }

    @Test
    void testAnalizarNumeros() throws ExcepcionLisp {
        // Positive numbers
        assertEquals(123, ((numero)new analizador(new StringReader("123")).analizar()).obtenerValor());
        // Negative numbers
        assertEquals(-123, ((numero)new analizador(new StringReader("-123")).analizar()).obtenerValor());
        // Zero
        assertEquals(0, ((numero)new analizador(new StringReader("0")).analizar()).obtenerValor());
    }

    @Test
    void testAnalizarSimbolos() throws ExcepcionLisp {
        // Regular symbols
        assertEquals("SIMBOLO", ((simbolo)new analizador(new StringReader("SIMBOLO")).analizar()).obtenerNombre());
        // Predefined symbols
        assertSame(simbolo.VERDADERO, new analizador(new StringReader("T")).analizar());
        assertSame(simbolo.NULO, new analizador(new StringReader("NIL")).analizar());
    }

    @Test
    void testAnalizarListas() throws ExcepcionLisp {
        // Empty list
        String input = "()";
        analizador analizador = new analizador(new StringReader(input));
        assertEquals(simbolo.NULO, analizador.analizar());

        // Simple list
        input = "(1 2 3)";
        analizador = new analizador(new StringReader(input));
        ExpresionLisp resultado = analizador.analizar();
        assertTrue(resultado instanceof par);
        assertTrue(((par)resultado).esLista());

        // Nested list
        input = "(1 (2 3) 4)";
        analizador = new analizador(new StringReader(input));
        resultado = analizador.analizar();
        assertTrue(resultado instanceof par);
        assertTrue(((par)resultado).esLista());
    }

    @Test
    void testAnalizarExpresionesComplejas() throws ExcepcionLisp {
        // Function call
        String input = "(+ 1 2)";
        analizador analizador = new analizador(new StringReader(input));
        ExpresionLisp resultado = analizador.analizar();
        assertTrue(resultado instanceof par);

        // Quoted expression
        input = "'(1 2 3)";
        analizador = new analizador(new StringReader(input));
        resultado = analizador.analizar();
        assertTrue(resultado instanceof par);

        // Mixed expression
        input = "(CONS 1 '(2 3))";
        analizador = new analizador(new StringReader(input));
        resultado = analizador.analizar();
        assertTrue(resultado instanceof par);
    }

}