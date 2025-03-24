package lisp;

import Excepciones.ExcepcionAtomo;

public abstract class atomo extends ExpresionLisp {
    @Override
    public boolean esAtomo() {
        return true;
    }

    @Override
    public ExpresionLisp primero() throws ExcepcionAtomo {
        throw new ExcepcionAtomo("No se puede obtener el primer elemento de un átomo: " + this);
    }

    @Override
    public ExpresionLisp resto() throws ExcepcionAtomo {
        throw new ExcepcionAtomo("No se puede obtener el resto de un átomo: " + this);
    }
}