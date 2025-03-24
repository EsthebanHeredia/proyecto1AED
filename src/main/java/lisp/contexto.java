package lisp;

import Excepciones.ExcepcionAtomo;
import Excepciones.ExcepcionContexto;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa un contexto de ejecución en LISP que almacena asociaciones entre símbolos y valores.
 * Implementa el sistema de ámbitos léxicos.
 */
public class contexto {
    private final Map<simbolo, ExpresionLisp> enlaces;
    private final contexto padre;

    /**
     * Crea un contexto global (sin padre).
     */
    public contexto() {
        this(null);
    }

    /**
     * Crea un contexto hijo del contexto padre proporcionado.
     *
     * @param padre El contexto padre, o null si es un contexto global
     */
    public contexto(contexto padre) {
        this.enlaces = new HashMap<>();
        this.padre = padre;
    }

    /**
     * Obtiene el valor de un símbolo en este contexto o sus ancestros.
     *
     * @param simbolo El símbolo a buscar
     * @return El valor asociado al símbolo
     * @throws ExcepcionContexto Si el símbolo no está definido en ningún contexto
     */
    public ExpresionLisp obtener(simbolo simbolo) throws ExcepcionContexto {
        if (simbolo == null) {
            throw new ExcepcionContexto("No se puede obtener un símbolo nulo");
        }

        if (enlaces.containsKey(simbolo)) {
            return enlaces.get(simbolo);
        } else if (padre != null) {
            return padre.obtener(simbolo);
        } else {
            throw new ExcepcionContexto("Símbolo no definido: " + simbolo.obtenerNombre());
        }
    }

    /**
     * Busca el valor de un símbolo sin lanzar una excepción si no existe.
     *
     * @param simbolo El símbolo a buscar
     * @return El valor asociado o null si no está definido
     */
    public ExpresionLisp buscar(simbolo simbolo) {
        if (simbolo == null) {
            return null;
        }

        if (enlaces.containsKey(simbolo)) {
            return enlaces.get(simbolo);
        } else if (padre != null) {
            return padre.buscar(simbolo);
        } else {
            return null;
        }
    }

    /**
     * Establece un valor para un símbolo en este contexto.
     *
     * @param simbolo El símbolo a definir
     * @param valor El valor a asociar
     */
    public void establecer(simbolo simbolo, ExpresionLisp valor) {
        if (simbolo == null) {
            throw new IllegalArgumentException("No se puede establecer un símbolo nulo");
        }
        enlaces.put(simbolo, valor);
    }

    /**
     * Actualiza el valor de un símbolo existente en este contexto o sus ancestros.
     *
     * @param simbolo El símbolo a actualizar
     * @param valor El nuevo valor
     * @throws ExcepcionContexto Si el símbolo no está definido en ningún contexto
     */
    public void actualizar(simbolo simbolo, ExpresionLisp valor) throws ExcepcionContexto {
        if (simbolo == null) {
            throw new ExcepcionContexto("No se puede actualizar un símbolo nulo");
        }

        if (enlaces.containsKey(simbolo)) {
            enlaces.put(simbolo, valor);
        } else if (padre != null) {
            padre.actualizar(simbolo, valor);
        } else {
            throw new ExcepcionContexto("Símbolo no definido: " + simbolo.obtenerNombre());
        }
    }

    /**
     * Extiende este contexto con asociaciones entre parámetros y argumentos.
     * Usado para crear el ámbito de una llamada a función.
     *
     * @param parametros Lista de parámetros formales
     * @param argumentos Lista de argumentos actuales
     * @return Un nuevo contexto con las asociaciones de parámetros
     * @throws ExcepcionContexto Si hay un error en la asociación
     */
    public contexto extender(ExpresionLisp parametros, ExpresionLisp argumentos) throws ExcepcionContexto {
        contexto nuevoContexto = new contexto(this);

        ExpresionLisp parametroActual = parametros;
        ExpresionLisp argumentoActual = argumentos;

        try {
            while (parametroActual != simbolo.NULO && argumentoActual != simbolo.NULO) {
                if (parametroActual.esAtomo()) {
                    if (!parametroActual.esSimbolo()) {
                        throw new ExcepcionContexto("Parámetro no es un símbolo: " + parametroActual);
                    }
                    nuevoContexto.establecer((simbolo) parametroActual, argumentoActual);
                    break;
                }

                if (!parametroActual.primero().esSimbolo()) {
                    throw new ExcepcionContexto("Parámetro no es un símbolo: " + parametroActual.primero());
                }

                nuevoContexto.establecer((simbolo) parametroActual.primero(), argumentoActual.primero());
                parametroActual = parametroActual.resto();
                argumentoActual = argumentoActual.resto();
            }
        } catch (ExcepcionAtomo e) {
            throw new ExcepcionContexto("Error al extender el contexto: " + e.getMessage(), e);
        }

        if (parametroActual != simbolo.NULO && !parametroActual.esAtomo()) {
            throw new ExcepcionContexto("Faltan argumentos");
        }
        if (argumentoActual != simbolo.NULO && parametroActual == simbolo.NULO) {
            throw new ExcepcionContexto("Demasiados argumentos");
        }

        return nuevoContexto;
    }
}
