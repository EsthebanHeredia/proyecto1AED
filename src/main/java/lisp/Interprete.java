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
 * Esta clase implementa un intérprete básico para un subconjunto del lenguaje LISP,
 * proporcionando funcionalidad para evaluar expresiones y mantener un entorno global.
 */
public class Interprete {
    /** El analizador sintáctico para procesar expresiones LISP */
    private final analizador analizador;
    /** La salida donde se mostrarán los resultados */
    private final PrintStream salida;
    /** El contexto global que mantiene las variables y funciones definidas */
    private final contexto contextoGlobal;

    /**
     * Constructor del intérprete que usa la entrada y salida estándar.
     *
     * @throws ExcepcionLisp si hay un error al inicializar el intérprete
     */
    public Interprete() throws ExcepcionLisp {
        this(System.in, System.out);
    }

    /**
     * Constructor del intérprete con entrada y salida personalizadas.
     *
     * @param entrada La fuente de entrada para las expresiones
     * @param salida La salida donde mostrar resultados
     * @throws ExcepcionAtomo si hay un error con el manejo de átomos
     * @throws ExcepcionLisp si hay un error al inicializar el intérprete
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
     * @throws ExcepcionAtomo si hay un error con el manejo de átomos
     * @throws ExcepcionContexto si hay un error con el contexto
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

    private ExpresionLisp aplicar(ExpresionLisp funcion, List<ExpresionLisp> args) throws ExcepcionLisp, ExcepcionAtomo, ExcepcionContexto {
        if (funcion instanceof Funcion func) {
            // Función definida por el usuario
            return func.aplicar(args, this);
        } else if (funcion.esSimbolo()) {
            // Función incorporada
            simbolo op = (simbolo) funcion;

            if (op == simbolo.PRIMERO) {
                verificarCantidadArgumentos(args, 1);
                return args.get(0).primero();
            } else if (op == simbolo.RESTO) {
                verificarCantidadArgumentos(args, 1);
                return args.get(0).resto();
            } else if (op == simbolo.CONSTRUIR) {
                verificarCantidadArgumentos(args, 2);
                return new par(args.get(0), args.get(1));
            } else if (op == simbolo.LISTA) {
                // Convierte lista de args a una lista LISP adecuada
                ExpresionLisp resultado = simbolo.NULO;
                for (int i = args.size() - 1; i >= 0; i--) {
                    resultado = new par(args.get(i), resultado);
                }
                return resultado;
            } else if (op == simbolo.ES_IGUAL_REF) {
                verificarCantidadArgumentos(args, 2);
                return args.get(0) == args.get(1) ? simbolo.VERDADERO : simbolo.NULO;
            } else if (op == simbolo.ES_IGUAL || op == simbolo.IGUAL || op == simbolo.ES_IGUAL_VALOR) {
                verificarCantidadArgumentos(args, 2);
                return esIgual(args.get(0), args.get(1)) ? simbolo.VERDADERO : simbolo.NULO;
            } else if (op == simbolo.ES_ATOMO) {
                verificarCantidadArgumentos(args, 1);
                return args.get(0).esAtomo() ? simbolo.VERDADERO : simbolo.NULO;
            } else if (op == simbolo.ES_LISTA) {
                verificarCantidadArgumentos(args, 1);
                ExpresionLisp arg = args.get(0);

                // NULO is a list
                if (arg == simbolo.NULO) {
                    return simbolo.VERDADERO;
                }

                // Only pairs can be lists
                if (arg.esAtomo()) {
                    return simbolo.NULO;
                }

                // Check if it's a proper list
                return ((par) arg).esLista() ? simbolo.VERDADERO : simbolo.NULO;
            } else if (op == simbolo.CONCATENAR) {
                StringBuilder resultado = new StringBuilder();
                for (ExpresionLisp arg : args) {
                    if (arg.esCadena()) {
                        resultado.append(((cadena) arg).obtenerValor());
                    } else {
                        StringBuilder temp = new StringBuilder();
                        PrintStream ps = new PrintStream(System.out) {
                            @Override
                            public void print(String s) {
                                temp.append(s);
                            }
                            @Override
                            public void print(long l) {
                                temp.append(l);
                            }
                        };
                        arg.imprimir(ps);
                        resultado.append(temp);
                    }
                }
                return new cadena(resultado.toString());
            } else if (op == simbolo.LONGITUD_CADENA) {
                verificarCantidadArgumentos(args, 1);
                if (!args.get(0).esCadena()) {
                    throw new ExcepcionLisp("LONGITUD_CADENA requiere un argumento de tipo cadena");
                }
                return numero.obtenerValor(((cadena) args.get(0)).obtenerValor().length());
            } else if (op == simbolo.SUMA || op == simbolo.SUMAR) {
                long resultado = 0;
                for (ExpresionLisp arg : args) {
                    if (!arg.esNumero()) {
                        throw new ExcepcionLisp("+ requiere argumentos numéricos");
                    }
                    resultado += ((numero) arg).obtenerValor();
                }
                return numero.obtenerValor(resultado);
            } else if (op == simbolo.RESTA || op == simbolo.RESTAR) {
                if (args.size() == 0) {
                    throw new ExcepcionLisp("- requiere al menos un argumento");
                }

                if (!args.get(0).esNumero()) {
                    throw new ExcepcionLisp("- requiere argumentos numéricos");
                }

                if (args.size() == 1) {
                    // Menos unario
                    return numero.obtenerValor(-((numero) args.get(0)).obtenerValor());
                }

                // Menos binario
                long resultado = ((numero) args.get(0)).obtenerValor();
                for (int i = 1; i < args.size(); i++) {
                    if (!args.get(i).esNumero()) {
                        throw new ExcepcionLisp("- requiere argumentos numéricos");
                    }
                    resultado -= ((numero) args.get(i)).obtenerValor();
                }
                return numero.obtenerValor(resultado);
            } else if (op == simbolo.MULTIPLICA || op == simbolo.MULTIPLICAR) {
                long resultado = 1;
                for (ExpresionLisp arg : args) {
                    if (!arg.esNumero()) {
                        throw new ExcepcionLisp("* requiere argumentos numéricos");
                    }
                    resultado *= ((numero) arg).obtenerValor();
                }
                return numero.obtenerValor(resultado);
            } else if (op == simbolo.DIVIDE || op == simbolo.DIVIDIR) {
                if (args.size() == 0) {
                    throw new ExcepcionLisp("/ requiere al menos un argumento");
                }

                if (!args.get(0).esNumero()) {
                    throw new ExcepcionLisp("/ requiere argumentos numéricos");
                }

                if (args.size() == 1) {
                    // Inversión
                    long valor = ((numero) args.get(0)).obtenerValor();
                    if (valor == 0) {
                        throw new ExcepcionLisp("División por cero");
                    }
                    return numero.obtenerValor(1 / valor);
                }

                // División normal
                long resultado = ((numero) args.get(0)).obtenerValor();
                for (int i = 1; i < args.size(); i++) {
                    if (!args.get(i).esNumero()) {
                        throw new ExcepcionLisp("/ requiere argumentos numéricos");
                    }
                    long divisor = ((numero) args.get(i)).obtenerValor();
                    if (divisor == 0) {
                        throw new ExcepcionLisp("División por cero");
                    }
                    resultado /= divisor;
                }
                return numero.obtenerValor(resultado);
            } else if (op == simbolo.MENOR || op == simbolo.MENOR_QUE) {
                verificarCantidadArgumentos(args, 2);
                if (!args.get(0).esNumero() || !args.get(1).esNumero()) {
                    throw new ExcepcionLisp("< requiere argumentos numéricos");
                }
                long a = ((numero) args.get(0)).obtenerValor();
                long b = ((numero) args.get(1)).obtenerValor();
                return a < b ? simbolo.VERDADERO : simbolo.NULO;
            } else if (op == simbolo.MAYOR || op == simbolo.MAYOR_QUE) {
                verificarCantidadArgumentos(args, 2);
                if (!args.get(0).esNumero() || !args.get(1).esNumero()) {
                    throw new ExcepcionLisp("> requiere argumentos numéricos");
                }
                long a = ((numero) args.get(0)).obtenerValor();
                long b = ((numero) args.get(1)).obtenerValor();
                return a > b ? simbolo.VERDADERO : simbolo.NULO;
            } else if (op == simbolo.IMPRIMIR) {
                for (ExpresionLisp arg : args) {
                    arg.imprimir(salida);
                    salida.print(" ");
                }
                salida.println();
                return simbolo.NULO;
            } else {
                throw new ExcepcionLisp("Función desconocida: " + op.obtenerNombre());
            }
        } else {
            throw new ExcepcionLisp("No se puede aplicar: " + funcion);
        }
    }
    private contexto crearContextoGlobal() {
        contexto ctx = new contexto();

        // Definir constantes
        ctx.establecer(simbolo.NULO, simbolo.NULO);
        ctx.establecer(simbolo.VERDADERO, simbolo.VERDADERO);

        // Definir funciones incorporadas
        ctx.establecer(simbolo.PRIMERO, simbolo.PRIMERO);
        ctx.establecer(simbolo.RESTO, simbolo.RESTO);
        ctx.establecer(simbolo.CONSTRUIR, simbolo.CONSTRUIR);
        ctx.establecer(simbolo.LISTA, simbolo.LISTA);
        ctx.establecer(simbolo.ES_ATOMO, simbolo.ES_ATOMO);
        ctx.establecer(simbolo.ES_IGUAL_REF, simbolo.ES_IGUAL_REF);
        ctx.establecer(simbolo.ES_IGUAL, simbolo.ES_IGUAL);
        ctx.establecer(simbolo.ES_LISTA, simbolo.ES_LISTA);
        ctx.establecer(simbolo.IMPRIMIR, simbolo.IMPRIMIR);
        ctx.establecer(simbolo.DEFUN, simbolo.DEFUN);  // Changed from DEFINIR_FUNCION to DEFUN

        // Definir operaciones con cadenas
        ctx.establecer(simbolo.CONCATENAR, simbolo.CONCATENAR);
        ctx.establecer(simbolo.LONGITUD_CADENA, simbolo.LONGITUD_CADENA);

        // Definir operadores aritméticos - both forms
        ctx.establecer(simbolo.SUMA, simbolo.SUMA);
        ctx.establecer(simbolo.SUMAR, simbolo.SUMAR);
        ctx.establecer(simbolo.RESTA, simbolo.RESTA);
        ctx.establecer(simbolo.RESTAR, simbolo.RESTAR);
        ctx.establecer(simbolo.MULTIPLICA, simbolo.MULTIPLICA);
        ctx.establecer(simbolo.MULTIPLICAR, simbolo.MULTIPLICAR);
        ctx.establecer(simbolo.DIVIDE, simbolo.DIVIDE);
        ctx.establecer(simbolo.DIVIDIR, simbolo.DIVIDIR);

        // Definir operadores de comparación - both forms
        ctx.establecer(simbolo.MENOR, simbolo.MENOR);
        ctx.establecer(simbolo.MENOR_QUE, simbolo.MENOR_QUE);
        ctx.establecer(simbolo.MAYOR, simbolo.MAYOR);
        ctx.establecer(simbolo.MAYOR_QUE, simbolo.MAYOR_QUE);
        ctx.establecer(simbolo.IGUAL, simbolo.ES_IGUAL);
        ctx.establecer(simbolo.ES_IGUAL_VALOR, simbolo.ES_IGUAL_VALOR);

        return ctx;
    }

    /**
     * Ejecuta el intérprete en un bucle leer-evaluar-imprimir (REPL).
     */

public void repl() {
        salida.println("Intérprete LISP");
        salida.println("Escribe expresiones LISP para evaluar, Ctrl+D para salir");

        while (true) {
            try {
                salida.print("> ");
                salida.flush();

                ExpresionLisp expr = analizador.analizar();
                if (expr == null) {
                    break; // Fin de entrada
                }

                ExpresionLisp resultado = evaluar(expr, contextoGlobal);

                // Only print the result if it's not from an IMPRIMIR operation
                if (!(expr.primero() instanceof simbolo &&
                        expr.primero() == simbolo.IMPRIMIR)) {
                    salida.print("=> ");
                    resultado.imprimir(salida);
                    salida.println();
                }
            } catch (ExcepcionLisp e) {
                salida.println("Error: " + e.getMessage());
            }
        }

        salida.println("¡Adiós!");
    }

    /**
     * Evalúa una expresión LISP de cadena.
     *
     * @param expr La expresión a evaluar como cadena
     * @return El resultado de evaluar la expresión
     * @throws ExcepcionLisp si hay un error durante la evaluación
     */
    public ExpresionLisp evaluar(String expr) throws ExcepcionLisp, ExcepcionAtomo, ExcepcionContexto {
        lisp.analizador analizadorTemp = new analizador(new StringReader(expr));
        ExpresionLisp s = analizadorTemp.analizar();
        if (s == null) {
            throw new ExcepcionLisp("Expresión vacía");
        }
        return evaluar(s, contextoGlobal);
    }

    /**
     * Clase interna para representar funciones definidas por el usuario.
     */
    private class Funcion extends ExpresionLisp {
        private final ExpresionLisp parametros;
        private final ExpresionLisp cuerpo;
        private final contexto cierreLexico;

        /**
         * Constructor de una función definida por el usuario.
         *
         * @param parametros los parámetros formales de la función
         * @param cuerpo el cuerpo de la función
         * @param cierreLexico el contexto donde se definió la función
         */
        public Funcion(ExpresionLisp parametros, ExpresionLisp cuerpo, contexto cierreLexico) {
            this.parametros = parametros;
            this.cuerpo = cuerpo;
            this.cierreLexico = cierreLexico;
        }

        /**
         * Aplica la función a los argumentos dados.
         *
         * @param args los argumentos de la función
         * @param interprete el intérprete que ejecuta la función
         * @return el resultado de evaluar la función
         * @throws ExcepcionLisp si hay un error durante la evaluación
         * @throws ExcepcionAtomo si hay un error con el manejo de átomos
         * @throws ExcepcionContexto si hay un error con el contexto
         */
        public ExpresionLisp aplicar(List<ExpresionLisp> args, Interprete interprete) throws ExcepcionLisp, ExcepcionAtomo, ExcepcionContexto {
            // Convierte la lista de args a una lista LISP adecuada
            ExpresionLisp listaArgs = simbolo.NULO;
            for (int i = args.size() - 1; i >= 0; i--) {
                listaArgs = new par(args.get(i), listaArgs);
            }

            // Crea un nuevo contexto extendido con los parámetros enlazados a los argumentos
            contexto nuevoContexto = cierreLexico.extender(parametros, listaArgs);

            // Evalúa el cuerpo de la función en el nuevo contexto
            return interprete.evaluar(cuerpo, nuevoContexto);
        }

        @Override
        public ExpresionLisp primero() throws ExcepcionAtomo {
            throw new ExcepcionAtomo("No se puede obtener el primer elemento de una función");
        }


        @Override
        public ExpresionLisp resto() throws ExcepcionAtomo {
            throw new ExcepcionAtomo("No se puede obtener el resto de una función");
        }

        @Override
        public void imprimir(PrintStream salida) {
            salida.print("#<FUNCION>");
        }
    }

    /**
     * Método principal que inicia el intérprete.
     *
     * @param args argumentos de la línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        try {
            Interprete interprete = new Interprete();
            interprete.repl();
        } catch (ExcepcionLisp e) {
            System.err.println("Error al iniciar el intérprete: " + e.getMessage());
        }
    }
}
