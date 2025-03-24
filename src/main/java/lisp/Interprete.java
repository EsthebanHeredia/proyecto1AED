package lisp;

import Excepciones.ExcepcionAtomo;
import Excepciones.ExcepcionContexto;
import Excepciones.ExcepcionLisp;

import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Intérprete de LISP simple.
 */
public class Interprete {
    private final analizador analizador;
    private final PrintStream salida;
    private final contexto contextoGlobal;

    /**
     * Constructor del intérprete.
     */
    public Interprete() throws ExcepcionLisp {
        this(System.in, System.out);
    }

    /**
     * Constructor del intérprete con entrada y salida personalizadas.
     *
     * @param entrada La fuente de entrada
     * @param salida La salida donde mostrar resultados
     */
    public Interprete(java.io.InputStream entrada, PrintStream salida) throws ExcepcionAtomo, ExcepcionLisp {
        this.analizador = new analizador(entrada);
        this.salida = salida;
        this.contextoGlobal = crearContextoGlobal();
    }

    /**
     * Evalúa una expresión LISP en un contexto dado.
     *
     * @param expr La expresión a evaluar
     * @param ctx El contexto de evaluación
     * @return El resultado de evaluar la expresión
     * @throws ExcepcionLisp si hay un error durante la evaluación
     */
    public ExpresionLisp evaluar(ExpresionLisp expr, contexto ctx) throws ExcepcionLisp, ExcepcionAtomo, ExcepcionContexto {
        // Autoevaluación para tipos atómicos
        if (expr.esAtomo()) {
            if (expr.esNumero() || expr.esCadena()) {
                return expr; // Números y cadenas se evalúan a sí mismos
            }
            if (expr.esSimbolo()) {
                simbolo sym = (simbolo) expr;
                ExpresionLisp valor = ctx.buscar(sym);
                if (valor == null) {
                    throw new ExcepcionLisp("Símbolo no definido: " + sym.obtenerNombre());
                }
                return valor;
            }
            throw new ExcepcionLisp("No se puede evaluar: " + expr);
        }

        // Evaluación de listas
        if (expr.primero() instanceof simbolo op) {
            // Formas especiales
            if (op == simbolo.CITAR) {
                // (CITAR expr) => expr sin evaluarla
                ExpresionLisp args = expr.resto();
                if (args == simbolo.NULO || args.resto() != simbolo.NULO) {
                    throw new ExcepcionLisp("CITAR requiere exactamente un argumento");
                }
                return args.primero();
            } else if (op == simbolo.ASIGNAR) {
                // (ASIGNAR var valor) => asigna valor a var y devuelve valor
                ExpresionLisp args = expr.resto();
                if (args == simbolo.NULO || args.resto() == simbolo.NULO || args.resto().resto() != simbolo.NULO) {
                    throw new ExcepcionLisp("ASIGNAR requiere exactamente dos argumentos");
                }
                if (!(args.primero() instanceof simbolo)) {
                    throw new ExcepcionLisp("Primer argumento de ASIGNAR debe ser un símbolo");
                }
                simbolo var = (simbolo) args.primero();
                ExpresionLisp valor = evaluar(args.resto().primero(), ctx);
                ctx.establecer(var, valor);
                return valor;
            } else if (op == simbolo.DEFUN) {
                // (DEFUN (nombre param1 param2...) cuerpo)
                ExpresionLisp args = expr.resto();
                if (args == simbolo.NULO || args.resto() == simbolo.NULO || args.resto().resto() != simbolo.NULO) {
                    throw new ExcepcionLisp("DEFUN requiere exactamente dos argumentos");
                }

                ExpresionLisp cabecera = args.primero();
                ExpresionLisp cuerpo = args.resto().primero();

                if (cabecera == simbolo.NULO || !(cabecera.primero() instanceof simbolo)) {
                    throw new ExcepcionLisp("Cabecera de función inválida");
                }

                simbolo nombreFuncion = (simbolo) cabecera.primero();
                ExpresionLisp parametros = cabecera.resto();

                // Crear la función
                Funcion funcion = new Funcion(parametros, cuerpo, ctx);
                ctx.establecer(nombreFuncion, funcion);

                return nombreFuncion;
            } else if (op == simbolo.CONDICIONAL) {
                // (CONDICIONAL (condición1 resultado1) ... (condiciónN resultadoN))
                ExpresionLisp condiciones = expr.resto();
                while (condiciones != simbolo.NULO) {
                    ExpresionLisp clausula = condiciones.primero();
                    if (clausula == simbolo.NULO || clausula.resto() == simbolo.NULO) {
                        throw new ExcepcionLisp("Cláusula condicional inválida");
                    }

                    ExpresionLisp condicion = clausula.primero();
                    ExpresionLisp resultado = clausula.resto().primero();

                    // Para la cláusula VERDADERO, evaluar directamente el resultado
                    if (condicion == simbolo.VERDADERO) {
                        return evaluar(resultado, ctx);
                    }

                    // Evaluar la condición
                    ExpresionLisp evaluacionCondicion = evaluar(condicion, ctx);
                    if (evaluacionCondicion != simbolo.NULO) {
                        return evaluar(resultado, ctx);
                    }

                    condiciones = condiciones.resto();
                }

                // No se cumplió ninguna condición
                return simbolo.NULO;
            }
        }

        // Aplicación de función
        List<ExpresionLisp> args = new ArrayList<>();
        ExpresionLisp funcion = evaluar(expr.primero(), ctx);
        ExpresionLisp listaArgs = expr.resto();

        // Evaluar todos los argumentos
        while (listaArgs != simbolo.NULO) {
            args.add(evaluar(listaArgs.primero(), ctx));
            listaArgs = listaArgs.resto();
        }

        // Aplicar la función a los argumentos
        return aplicar(funcion, args);
    }

    private void verificarCantidadArgumentos(List<ExpresionLisp> args, int esperados) throws ExcepcionLisp {
        if (args.size() != esperados) {
            throw new ExcepcionLisp("Se esperaban " + esperados + " argumentos, pero se recibieron " + args.size());
        }
    }

    private boolean esIgual(ExpresionLisp a, ExpresionLisp b) throws ExcepcionLisp, ExcepcionAtomo {
        if (a == b) {
            return true; // Misma referencia
        }

        if (a.esNumero() && b.esNumero()) {
            return ((numero) a).obtenerValor() == ((numero) b).obtenerValor();
        }

        if (a.esCadena() && b.esCadena()) {
            return ((cadena) a).obtenerValor().equals(((cadena) b).obtenerValor());
        }

        if (a.esSimbolo() && b.esSimbolo()) {
            return a == b; // Los símbolos son internados
        }

        if (!a.esAtomo() && !b.esAtomo()) {
            // Para listas, comprobamos si todos los elementos son iguales
            return esIgual(a.primero(), b.primero()) && esIgual(a.resto(), b.resto());
        }

        return false; // Tipos diferentes
    }