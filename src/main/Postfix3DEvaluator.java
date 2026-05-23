package main;

import java.util.ArrayList;
import java.util.Stack;

public class Postfix3DEvaluator {

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-")
                || token.equals("*") || token.equals("/")
                || token.equals("mod") || token.equals("div")
                || token.equals("^");
    }

    /**
     * Sobrecarga: devuelve un temporal y usa destino nulo.
     */
    public String postfija3D(ArrayList<String> postfija, Generador3D gen) {
        return postfija3D(postfija, gen, null);
    }

    /**
     * Evalúa postfija y genera 3D.
     *
     * @param postfija lista postfija
     * @param gen Generador3D
     * @param dest destino final (o null)
     * @return temporal o dest donde quedó el resultado
     */
    public String postfija3D(ArrayList<String> postfija, Generador3D gen, String dest) {
        Stack<String> stack = new Stack<>();
        for (String tok : postfija) {
            if (isOperator(tok)) {
                String b = stack.pop();
                String a = stack.pop();
                boolean ultimaOp = stack.isEmpty() && dest != null;
                // Solo inyectar dest si es última op _y_ ambos operandos _no_ son temporales
                boolean ambosNoTemps = !a.matches("t\\d+") && !b.matches("t\\d+");
                String res = (ultimaOp && ambosNoTemps) ? dest : gen.nuevaTemp();
                gen.gc(tok, a, b, res);
                stack.push(res);
            } else {
                stack.push(tok);
            }
        }
        return stack.pop();
    }
}
