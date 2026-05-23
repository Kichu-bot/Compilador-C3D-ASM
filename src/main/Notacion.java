/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;
import java.util.ArrayList;
import java.util.Stack;
import java.util.HashMap;
import java.util.Map;

public class Notacion {

    /**
     * Convierte una cadena que contiene una expresión en notación infija
     * en un ArrayList de tokens (cada token es un String).
     * Esta versión robusta maneja números, identificadores y operadores.
     *
     * @param expr La expresión en notación infija.
     * @return ArrayList<String> con cada token de la expresión.
     */
    public static ArrayList<String> obtenerArrayListInfija(String expr) {
        ArrayList<String> tokens = new ArrayList<>();
        int i = 0;
        int len = expr.length();

        while (i < len) {
            char c = expr.charAt(i);

            // Omitir espacios en blanco
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }
            
            // Manejar el signo '-' unario (si es parte de un número)
            if (c == '-') {
                boolean unary = false;
                if (tokens.isEmpty()) {
                    unary = true;
                } else {
                    String last = tokens.get(tokens.size() - 1);
                    // Si el token anterior es un operador o "(", consideramos el '-' como unario.
                    if (last.equals("+") || last.equals("-") || last.equals("*") ||
                        last.equals("/") || last.equals("^") || last.equals("(") ||
                        last.equals("mod") || last.equals("div")) {
                        unary = true;
                    }
                }
                if (unary && (i + 1 < len) && (Character.isDigit(expr.charAt(i + 1)) || expr.charAt(i + 1) == '.')) {
                    StringBuilder sb = new StringBuilder();
                    sb.append('-');
                    i++; // saltar el signo
                    while (i < len && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                        sb.append(expr.charAt(i));
                        i++;
                    }
                    tokens.add(sb.toString());
                    continue;
                } else {
                    tokens.add("-");
                    i++;
                    continue;
                }
            }
            
            // Si es dígito o punto, se acumulan los caracteres para formar un número
            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < len && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    sb.append(expr.charAt(i));
                    i++;
                }
                tokens.add(sb.toString());
                continue;
            }
            
            // Si es una letra, se acumulan para formar un identificador o función (p.ej. sin, cos, mod, div, variable, etc.)
            if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                while (i < len && (Character.isLetterOrDigit(expr.charAt(i)) || expr.charAt(i) == '_')) {
                    sb.append(expr.charAt(i));
                    i++;
                }
                tokens.add(sb.toString());
                continue;
            }
            
            // Si es otro carácter, se toma como operador o símbolo individual (por ejemplo, +, -, *, /, (, ), etc.)
            tokens.add(String.valueOf(c));
            i++;
        }
        
        return tokens;
    }

    /**
     * Convierte una lista de tokens en notación infija a notación posfija usando el algoritmo de Shunting-yard.
     *
     * @param infija ArrayList de tokens en notación infija.
     * @return ArrayList<String> con los tokens en notación posfija.
     */
    public static ArrayList<String> infijaPostfija(ArrayList<String> infija) {
        ArrayList<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        Map<String, Integer> precedence = new HashMap<>();
        // Definimos la precedencia de los operadores.
        precedence.put("+", 1);
        precedence.put("-", 1);
        precedence.put("*", 2);
        precedence.put("/", 2);
        precedence.put("mod", 2);
        precedence.put("div", 2);
        precedence.put("^", 3);

        for (String token : infija) {
            // Si el token no es un operador conocido y no es un paréntesis, se considera operando.
            if (!precedence.containsKey(token) && !token.equals("(") && !token.equals(")")) {
                output.add(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                // Sacamos operadores de la pila hasta encontrar "(".
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (!stack.isEmpty() && stack.peek().equals("(")) {
                    stack.pop();
                }
            } else {
                // Es un operador; sacamos de la pila los operadores de mayor o igual precedencia.
                while (!stack.isEmpty() && !stack.peek().equals("(") &&
                        precedence.get(stack.peek()) >= precedence.get(token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
        }
        
        // Sacamos los operadores restantes de la pila.
        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }
        
        return output;
    }
    
    // Método main para pruebas
    public static void main(String[] args) {
        // Ejemplos de expresiones complejas:
        String[] expresiones = {
            "1 + 2",
            "hola = 1 + 2;",
            "x = -3.5 + sin(45) * mod(10, 3)",
            "result = (a + b) * (c - d) / 2"
        };
        
        for (String expr : expresiones) {
            System.out.println("Expresión: " + expr);
            ArrayList<String> tokensInfija = obtenerArrayListInfija(expr);
            System.out.println("Tokens infija: " + tokensInfija);
            
            // Para la conversión a posfija, normalmente se debe aplicar solo a la parte aritmética.
            // Por ejemplo, si se tiene "hola = 1 + 2;", se debería extraer "1 + 2"
            // Para simplificar, aquí convertimos la expresión completa.
            ArrayList<String> tokensPostfija = infijaPostfija(tokensInfija);
            System.out.println("Tokens postfija: " + tokensPostfija);
            System.out.println("-----");
        }
    }
}
