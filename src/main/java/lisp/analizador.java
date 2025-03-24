package lisp;

import Excepciones.ExcepcionLisp;
import Excepciones.ExcepcionSimbolo;

import java.io.*;

/**
 * Analizador sintáctico para expresiones LISP.
 * Esta clase proporciona funcionalidad para analizar y convertir texto en expresiones LISP.
 */
public class analizador {
    /** El tokenizador que procesa la entrada */
    private final Tokenizador tokenizador;

    /**
     * Construye un nuevo analizador que lee de un flujo de entrada.
     *
     * @param entrada el flujo de entrada del cual leer las expresiones LISP
     */
    public analizador(InputStream entrada) {
        this.tokenizador = new Tokenizador(new InputStreamReader(entrada));
    }

    /**
     * Construye un nuevo analizador que lee de un lector.
     *
     * @param lector el lector del cual leer las expresiones LISP
     */
    public analizador(Reader lector) {
        this.tokenizador = new Tokenizador(lector);
    }

    /**
     * Construye un nuevo analizador que lee de un lector de cadenas.
     *
     * @param stringReader el lector de cadenas del cual leer las expresiones LISP
     */
    public analizador(StringReader stringReader) {
        this.tokenizador = new Tokenizador(stringReader);
    }

    /**
     * Analiza una expresión LISP desde la entrada.
     *
     * @return La expresión S analizada, o null al final de la entrada
     * @throws ExcepcionLisp si hay un error de sintaxis
     */
    public ExpresionLisp analizar() throws ExcepcionLisp {
        String token = tokenizador.siguienteToken();
        if (token == null) {
            return null;
        }
        return analizarToken(token);
    }

    /**
     * Analiza un token específico y lo convierte en una expresión LISP.
     *
     * @param token el token a analizar
     * @return la expresión LISP correspondiente al token
     * @throws ExcepcionLisp si hay un error de sintaxis
     */
    private ExpresionLisp analizarToken(String token) throws ExcepcionLisp {
        return switch (token) {
            case "(" -> analizarLista();
            case ")" -> throw new ExcepcionLisp("Paréntesis de cierre inesperado");
            case "'" -> new par(simbolo.CITAR, new par(analizar(), simbolo.NULO));
            case "\"" -> analizarCadena();
            default -> analizarAtomo(token);
        };
    }

    /**
     * Analiza una cadena de caracteres desde la entrada.
     *
     * @return la expresión LISP que representa la cadena
     * @throws ExcepcionLisp si hay un error al leer la cadena
     */
    private ExpresionLisp analizarCadena() throws ExcepcionLisp {
        StringBuilder sb = new StringBuilder();
        int c;
        boolean escape = false;

        try {
            while ((c = tokenizador.lectorRaw().read()) != -1) {
                char ch = (char) c;
                if (escape) {
                    switch (ch) {
                        case 'n' -> sb.append('\n');
                        case 't' -> sb.append('\t');
                        case 'r' -> sb.append('\r');
                        case '"' -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        default -> sb.append(ch);
                    }
                    escape = false;
                } else if (ch == '\\') {
                    escape = true;
                } else if (ch == '"') {
                    return new cadena(sb.toString());
                } else {
                    sb.append(ch);
                }
            }
            throw new ExcepcionLisp("Cadena sin cerrar");
        } catch (IOException e) {
            throw new ExcepcionLisp("Error al leer cadena: " + e.getMessage());
        }
    }

    /**
     * Analiza una lista de expresiones LISP.
     *
     * @return la expresión LISP que representa la lista
     * @throws ExcepcionLisp si hay un error de sintaxis
     */
    private ExpresionLisp analizarLista() throws ExcepcionLisp {
        String token = tokenizador.siguienteToken();
        if (token == null) {
            throw new ExcepcionLisp("Fin de entrada inesperado, falta un paréntesis de cierre");
        }

        if (token.equals(")")) {
            return simbolo.NULO;
        }

        ExpresionLisp primero = analizarToken(token);
        token = tokenizador.siguienteToken();
        if (token == null) {
            throw new ExcepcionLisp("Fin de entrada inesperado, falta un paréntesis de cierre");
        }

        if (token.equals(".")) {
            ExpresionLisp resto = analizar();
            token = tokenizador.siguienteToken();
            if (!token.equals(")")) {
                throw new ExcepcionLisp("Se esperaba un paréntesis de cierre después del par punteado");
            }
            return new par(primero, resto);
        } else {
            tokenizador.devolver(token);
            ExpresionLisp resto = analizarLista();
            return new par(primero, resto);
        }
    }

    /**
     * Analiza un átomo (número o símbolo) desde un token.
     *
     * @param token el token a analizar
     * @return la expresión LISP que representa el átomo
     * @throws ExcepcionLisp si hay un error al crear el átomo
     */
    private ExpresionLisp analizarAtomo(String token) throws ExcepcionLisp {
        try {
            long valor = Long.parseLong(token);
            return numero.obtenerValor(valor);
        } catch (NumberFormatException e) {
            try {
                return simbolo.internamente(token);
            } catch (ExcepcionSimbolo e2) {
                throw new ExcepcionLisp("Error al crear símbolo: " + e2.getMessage());
            }
        }
    }

    /**
     * Clase interna que maneja la tokenización de la entrada.
     */
    private static class Tokenizador {
        /** El lector que proporciona la entrada */
        private final BufferedReader lector;
        /** Token guardado para devolución */
        private String tokenDevuelto = null;

        /**
         * Construye un nuevo tokenizador.
         *
         * @param lector el lector de entrada a utilizar
         */
        public Tokenizador(Reader lector) {
            this.lector = lector instanceof BufferedReader ?
                    (BufferedReader) lector :
                    new BufferedReader(lector);
        }

        /**
         * Obtiene el siguiente token de la entrada.
         *
         * @return el siguiente token, o null si se alcanza el final de la entrada
         * @throws ExcepcionLisp si hay un error de E/S
         */
        public String siguienteToken() throws ExcepcionLisp {
            if (tokenDevuelto != null) {
                String token = tokenDevuelto;
                tokenDevuelto = null;
                return token;
            }

            try {
                saltarEspaciosEnBlanco();
                int c = lector.read();
                if (c == -1) return null;

                char ch = (char) c;
                if (ch == '(' || ch == ')' || ch == '\'' || ch == '.' || ch == '"') {
                    return String.valueOf(ch);
                }
                if (ch == ';') {
                    lector.readLine();
                    return siguienteToken();
                }

                StringBuilder sb = new StringBuilder();
                sb.append(ch);

                lector.mark(1);
                c = lector.read();
                while (c != -1 && !esDelimitador((char) c)) {
                    sb.append((char) c);
                    lector.mark(1);
                    c = lector.read();
                }

                if (c != -1) lector.reset();
                return sb.toString();
            } catch (IOException e) {
                throw new ExcepcionLisp("Error de E/S: " + e.getMessage());
            }
        }

        /**
         * Proporciona acceso al lector subyacente.
         *
         * @return el lector subyacente
         */
        public Reader lectorRaw() {
            return lector;
        }

        /**
         * Devuelve un token para ser procesado en la siguiente llamada.
         *
         * @param token el token a devolver
         */
        public void devolver(String token) {
            tokenDevuelto = token;
        }

        /**
         * Avanza el lector hasta encontrar un carácter que no sea espacio en blanco.
         *
         * @throws IOException si ocurre un error de E/S
         */
        private void saltarEspaciosEnBlanco() throws IOException {
            lector.mark(1);
            int c = lector.read();
            while (c != -1 && Character.isWhitespace((char) c)) {
                lector.mark(1);
                c = lector.read();
            }
            if (c != -1) lector.reset();
        }

        /**
         * Verifica si un carácter es un delimitador en la sintaxis LISP.
         *
         * @param c el carácter a verificar
         * @return true si el carácter es un delimitador
         */
        private boolean esDelimitador(char c) {
            return Character.isWhitespace(c) || c == '(' || c == ')' || c == '\'' || c == ';' || c == '"';
        }
    }
}