package lisp;

import Excepciones.ExcepcionLisp;
import Excepciones.ExcepcionSimbolo;

import java.io.*;

/**
 * Analizador para expresiones LISP.
 */
public class analizador {
    private final Tokenizador tokenizador;

    public analizador(InputStream entrada) {
        this.tokenizador = new Tokenizador(new InputStreamReader(entrada));
    }

    public analizador(Reader lector) {
        this.tokenizador = new Tokenizador(lector);
    }

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
            return null; // Fin de entrada
        }

        return analizarToken(token);
    }

    private ExpresionLisp analizarToken(String token) throws ExcepcionLisp {
        return switch (token) {
            case "(" -> analizarLista();
            case ")" -> throw new ExcepcionLisp("Paréntesis de cierre inesperado");
            case "'" ->
                // Abreviatura de cita: 'x => (CITAR x)
                    new par(simbolo.CITAR, new par(analizar(), simbolo.NULO));
            case "\"" ->
                // Analizar cadena
                    analizarCadena();
            default -> analizarAtomo(token);
        };
    }

    private ExpresionLisp analizarCadena() throws ExcepcionLisp {
        StringBuilder sb = new StringBuilder();
        int c;
        boolean escape = false;

        try {
            while ((c = tokenizador.lectorRaw().read()) != -1) {
                char ch = (char) c;

                if (escape) {
                    // Manejar caracteres de escape
                    switch (ch) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case 'r': sb.append('\r'); break;
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        default: sb.append(ch);
                    }
                    escape = false;
                } else if (ch == '\\') {
                    escape = true;
                } else if (ch == '"') {
                    // Fin de cadena
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

    private ExpresionLisp analizarLista() throws ExcepcionLisp {
        String token = tokenizador.siguienteToken();
        if (token == null) {
            throw new ExcepcionLisp("Fin de entrada inesperado, falta un paréntesis de cierre");
        }

        if (token.equals(")")) {
            return simbolo.NULO; // Lista vacía
        }

        ExpresionLisp primero = analizarToken(token);

        token = tokenizador.siguienteToken();
        if (token == null) {
            throw new ExcepcionLisp("Fin de entrada inesperado, falta un paréntesis de cierre");
        }

        if (token.equals(".")) {
            // Notación de par punteado
            ExpresionLisp resto = analizar();
            token = tokenizador.siguienteToken();
            if (!token.equals(")")) {
                throw new ExcepcionLisp("Se esperaba un paréntesis de cierre después del par punteado");
            }
            return new par(primero, resto);
        } else {
            // Lista regular
            tokenizador.devolver(token);
            ExpresionLisp resto = analizarLista();
            return new par(primero, resto);
        }
    }

    private ExpresionLisp analizarAtomo(String token) throws ExcepcionLisp {
        // Intenta analizar como número
        try {
            long valor = Long.parseLong(token);
            return numero.obtenerValor(valor);
        } catch (NumberFormatException e) {
            // Si no es un número, es un símbolo
            try {
                return simbolo.internamente(token);
            } catch (ExcepcionSimbolo e2) {
                throw new ExcepcionLisp("Error al crear símbolo: " + e2.getMessage());
            }
        }
    }

    /**
     * Tokenizador para expresiones LISP.
     */
    private static class Tokenizador {
        private final BufferedReader lector;
        private String tokenDevuelto = null;

        public Tokenizador(Reader lector) {
            this.lector = lector instanceof BufferedReader ?
                    (BufferedReader) lector :
                    new BufferedReader(lector);
        }

        /**
         * Devuelve el siguiente token de la entrada.
         *
         * @return El siguiente token, o null al final de la entrada
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
                if (c == -1) {
                    return null; // Fin de entrada
                }

                char ch = (char) c;

                // Maneja tokens de un solo carácter
                switch (ch) {
                    case '(':
                    case ')':
                    case '\'':
                    case '.', '"':
                        return String.valueOf(ch);
                    case ';':
                        // Omitir comentario
                        lector.readLine();
                        return siguienteToken();
                }

                // Maneja tokens de múltiples caracteres (símbolos y números)
                StringBuilder sb = new StringBuilder();
                sb.append(ch);

                lector.mark(1);
                c = lector.read();
                while (c != -1 && !esDelimitador((char) c)) {
                    sb.append((char) c);
                    lector.mark(1);
                    c = lector.read();
                }

                if (c != -1) {
                    lector.reset(); // Devuelve el delimitador
                }

                return sb.toString();
            } catch (IOException e) {
                throw new ExcepcionLisp("Error de E/S: " + e.getMessage());
            }
        }

        /**
         * Devuelve el lector subyacente para operaciones de lectura directa.
         */
        public Reader lectorRaw() {
            return lector;
        }

        /**
         * Devuelve un token para ser retornado en la próxima llamada a siguienteToken().
         */
        public void devolver(String token) {
            tokenDevuelto = token;
        }

        private void saltarEspaciosEnBlanco() throws IOException {
            lector.mark(1);
            int c = lector.read();
            while (c != -1 && Character.isWhitespace((char) c)) {
                lector.mark(1);
                c = lector.read();
            }
            if (c != -1) {
                lector.reset();
            }
        }

        private boolean esDelimitador(char c) {
            return Character.isWhitespace(c) || c == '(' || c == ')' || c == '\'' || c == ';' || c == '"';
        }
    }
}