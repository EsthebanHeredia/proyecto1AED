package lisp;

import Excepciones.ExcepcionAtomo;

/**
 * Clase abstracta que representa un átomo en el intérprete LISP.
 * Un átomo es una expresión LISP indivisible que puede ser un número o un símbolo.
 * Esta clase proporciona la implementación base para todos los tipos de átomos.
 */
public abstract class atomo extends ExpresionLisp {
    /**
     * Indica si esta expresión es un átomo.
     *
     * @return true, ya que esta clase representa átomos
     */
    @Override
    public boolean esAtomo() {
        return true;
    }

    /**
     * Operación no soportada para átomos. Los átomos no tienen primer elemento.
     *
     * @return nunca retorna un valor
     * @throws ExcepcionAtomo siempre, ya que no se puede obtener el primer elemento de un átomo
     */
    @Override
    public ExpresionLisp primero() throws ExcepcionAtomo {
        throw new ExcepcionAtomo("No se puede obtener el primer elemento de un átomo: " + this);
    }

    /**
     * Operación no soportada para átomos. Los átomos no tienen resto.
     *
     * @return nunca retorna un valor
     * @throws ExcepcionAtomo siempre, ya que no se puede obtener el resto de un átomo
     */
    @Override
    public ExpresionLisp resto() throws ExcepcionAtomo {
        throw new ExcepcionAtomo("No se puede obtener el resto de un átomo: " + this);
    }
}