import Excepciones.ExcepcionSimbolo;
import lisp.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimboloTest {
    @Test
    void testCreacionSimbolos() throws ExcepcionSimbolo {
        simbolo s1 = simbolo.internamente("TEST");
        simbolo s2 = simbolo.internamente("TEST");
        simbolo s3 = simbolo.internamente("OTRO");

        assertSame(s1, s2); // Should be same instance due to symbol interning
        assertNotSame(s1, s3);
        assertEquals("TEST", s1.obtenerNombre());
    }

    @Test
    void testSimbolosPreDefinidos() {
        assertNotNull(simbolo.VERDADERO);
        assertNotNull(simbolo.NULO);
        assertEquals("T", simbolo.VERDADERO.obtenerNombre());
        assertEquals("NIL", simbolo.NULO.obtenerNombre());
    }

    @Test
    void testSimboloInvalido() {
        assertThrows(ExcepcionSimbolo.class, () -> simbolo.internamente(null));
        assertThrows(ExcepcionSimbolo.class, () -> simbolo.internamente(""));
    }

    @Test
    void testConversionMayusculas() throws ExcepcionSimbolo {
        simbolo s1 = simbolo.internamente("test");
        simbolo s2 = simbolo.internamente("TEST");
        assertSame(s1, s2);
        assertEquals("TEST", s1.obtenerNombre());
    }
}