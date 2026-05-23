package main;

import compilerTools.Token;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

public class Generador3D {

    public int contadorTemp = 0;
    public int contadorEtiq = 0;
    public PrintStream out = System.out;
    
    // Estos métodos deberían estar en tu clase que convierte de .re a .3D,
// o en una clase helper de la que tengas una instancia.

// Campos miembro para el estado del parser de expresiones:
private List<Token> tokensDeLaExpresionActual; // Tokens solo de la expresión que se está parseando
private int       indiceEnExpresionActual;   // Puntero al token actual DENTRO de tokensDeLaExpresionActual
private Generador3D generador3D_ref;       // Referencia a tu generador de 3AC

    public void crearArchivoCodigo3D(String rutaArchivo) {
        String intermedio = rutaArchivo;
        intermedio = intermedio.replace(".re", ".3D");
        try {
            this.out = new PrintStream(new FileOutputStream(intermedio));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gc(String operacion, String arg1, String arg2, String resultado) {
        switch (operacion) {
            case "+":
            case "-":
            case "*":
            case "/":
                out.println(resultado + "=" + arg1 + operacion + arg2 + ";");
                break;

            case "mod":
            case "div":
                out.println(resultado + "=" + arg1 + " " + operacion + " " + arg2 + ";");
                break;

            case "=":
            case ":=":
            case "ASIGNACION":
                out.println(resultado + "=" + arg1 + ";");
                break;

            case "GOTO":
                out.println("goto " + resultado + ";");
                break;

            case "LABEL":
                out.println(resultado + ":");
                break;

            case "mostrar":
            case "leer":
                out.println(operacion + " " + resultado + ";");
                break;

            case "IF":
                out.println(operacion + " " + arg1 + " goto " + resultado);
                break;

            default:
                System.err.println("Error en la generación de código");
        }
    }

    public String nuevaTemp() {
        contadorTemp++;
        return "t" + contadorTemp;
    }

    public String nuevaEtiq() {
        contadorEtiq++;
        return "L" + contadorEtiq; // Genera etiquetas con el prefijo "L" y el número secuencial
    }

    public void iniciaizaTempEtiq() {
        contadorTemp = 0;
        contadorEtiq = 0;
    }

    public void cerrarArchivoCodigo3D() {
        out.close();
    }
    


}
