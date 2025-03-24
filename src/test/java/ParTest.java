import Excepciones.ExcepcionLisp;
import Excepciones.ExcepcionSimbolo;
import lisp.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ParTest {
    @Test
    void testCreacionPar() throws ExcepcionLisp {
        Interprete interprete = new Interprete();
        ExpresionLisp primero = interprete.evaluar("1");
        ExpresionLisp resto = interprete.evaluar("2");
        par p = new par(primero, resto);

        assertEquals(primero, p.primero());
        assertEquals(resto, p.resto());
    }

    @Test
    void testLongitudLista() throws ExcepcionLisp, ExcepcionSimbolo {
        ExpresionLisp lista = par.crearLista(
            simbolo.internamente("1"),
            simbolo.internamente("2"),
            simbolo.internamente("3")
        );

        assertTrue(lista instanceof par);
        assertEquals(3, ((par)lista).longitud());
    }

    @Test
    void testEsLista() throws ExcepcionLisp, ExcepcionSimbolo {
        // Lista propia (termina en NIL)
        ExpresionLisp listaPropia = par.crearLista(
            simbolo.internamente("1")
        );
        assertTrue(((par)listaPropia).esLista());

        // Par impropio (no termina en NIL)
        par parImpropio = new par(
            simbolo.internamente("1"),
            simbolo.internamente("2")
        );
        assertFalse(parImpropio.esLista());
    }

    @Test
    void testListaVacia() {
        par lista = new par(simbolo.NULO, simbolo.NULO);
        assertTrue(lista.esLista());
        assertEquals(1, lista.longitud());
    }
}