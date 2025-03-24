import Excepciones.ExcepcionLisp;
import lisp.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class InterpreteTest {
    private Interprete interprete;
    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;

    @BeforeEach
    void setUp() throws ExcepcionLisp {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
        interprete = new Interprete(System.in, printStream);
    }

    @Test
    void testEvaluarAtomosBasicos() throws ExcepcionLisp {
        assertEquals(42, ((numero)interprete.evaluar("42")).obtenerValor());
        assertEquals("test", ((cadena)interprete.evaluar("\"test\"")).obtenerValor());
        assertEquals(simbolo.VERDADERO, interprete.evaluar("T"));
        assertEquals(simbolo.NULO, interprete.evaluar("NIL"));
    }

    @Test
    void testOperacionesAritmeticasAvanzadas() throws ExcepcionLisp {
        // Multiple arguments
        assertEquals(10, ((numero)interprete.evaluar("(+ 1 2 3 4)")).obtenerValor());
        assertEquals(24, ((numero)interprete.evaluar("(* 2 3 4)")).obtenerValor());

        // Nested operations
        assertEquals(14, ((numero)interprete.evaluar("(+ (* 2 3) (- 10 2))")).obtenerValor());

        // Unary operations
        assertEquals(-5, ((numero)interprete.evaluar("(- 5)")).obtenerValor());
    }


    @Test
    void testFuncionesRecursivas() throws ExcepcionLisp {
        // Define factorial function
        interprete.evaluar("(DEFUN (factorial n) (COND ((= n 0) 1) (T (* n (factorial (- n 1))))))");
        assertEquals(120, ((numero)interprete.evaluar("(factorial 5)")).obtenerValor());

        // Define fibonacci function
        interprete.evaluar("(DEFUN (fib n) (COND ((< n 2) n) (T (+ (fib (- n 1)) (fib (- n 2))))))");
        assertEquals(5, ((numero)interprete.evaluar("(fib 5)")).obtenerValor());
    }

    @Test
    void testManipulacionListas() throws ExcepcionLisp {
        // List construction
        ExpresionLisp lista = interprete.evaluar("(CONS 1 (CONS 2 (CONS 3 NIL)))");
        assertTrue(lista instanceof par);
        assertEquals(3, ((par)lista).longitud());

        // List predicates
        assertEquals(simbolo.VERDADERO, interprete.evaluar("(LIST? (LIST 1 2 3))"));
        assertEquals(simbolo.NULO, interprete.evaluar("(LIST? 42)"));
    }



    @Test
    void testQuote() throws ExcepcionLisp {
        // Quote symbol
        ExpresionLisp quoted = interprete.evaluar("'abc");
        assertTrue(quoted instanceof simbolo);

        // Quote list
        quoted = interprete.evaluar("'(1 2 3)");
        assertTrue(quoted instanceof par);

        // Nested quotes
        quoted = interprete.evaluar("'(a '(b c))");
        assertTrue(quoted instanceof par);
    }


}