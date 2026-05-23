package main;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GeneradorASM {

    private int contadorTemp = 0;
    private int contadorEtiq = 0;
    private ArrayList codigoASM_TMP, declaracionesASM_TMP;
    private PrintStream codigoASM = System.out;

    public GeneradorASM() {
        codigoASM_TMP = new ArrayList();
        declaracionesASM_TMP = new ArrayList();
    }

    public void inicializarTempEtiq() {
        contadorTemp = 0;
        contadorEtiq = 0;

    }

    public void convertirDeclaracionesASM(HashMap<String, Simbolo> identificadores) {
        // declaracionesASM_TMP.add(""); // Puedes añadir un comentario inicial si quieres
        for (Simbolo simb : identificadores.values()) {
            String identNombre = simb.getIdent().trim(); // Es bueno limpiar el nombre
            String tipoLower = simb.getTipo().toLowerCase();
            String valorOriginal = simb.getValor();

            // System.out.println("Procesando para ASM: " + identNombre + ", Tipo: " + tipoLower + ", Valor: " + valorOriginal);
            switch (tipoLower) {
                case "string":
                    String valorStr;
                    if (valorOriginal == null || valorOriginal.isEmpty()) {
                        // Cadena vacía o no inicializada, para imprimir con INT 21h/09h necesita al menos el '$'
                        valorStr = "$";
                    } else {
                        // Quita comillas dobles si vienen del literal del lenguaje fuente
                        if (valorOriginal.startsWith("\"") && valorOriginal.endsWith("\"") && valorOriginal.length() >= 2) {
                            valorStr = valorOriginal.substring(1, valorOriginal.length() - 1);
                        } else {
                            valorStr = valorOriginal;
                        }
                        // Reemplaza comillas simples internas si es necesario o escapa caracteres especiales (más avanzado)
                        // Por ahora, asumimos que las cadenas no contienen comillas simples.
                        valorStr = "'" + valorStr + "','$"; // Agrega comillas simples y el terminador $
                    }
                    declaracionesASM_TMP.add("    " + identNombre + " db " + valorStr + " ; Cadena");
                    break;

                case "numero": // O como llames a tu tipo numérico principal
                case "integer":
                case "entero":
                    String valorNum;
                    if (valorOriginal == null || valorOriginal.trim().isEmpty() || valorOriginal.trim().equals("?")) {
                        valorNum = "?"; // Variable no inicializada
                    } else {
                        valorNum = valorOriginal.trim();
                        // Aquí podrías añadir una validación para asegurar que valorNum es realmente un número
                        try {
                            Integer.parseInt(valorNum); // Solo para validar si es un entero válido
                        } catch (NumberFormatException e) {
                            System.err.println("ADVERTENCIA: El valor '" + valorNum + "' para la variable numérica '" + identNombre + "' no es un entero válido. Se declarará como no inicializada '?'.");
                            valorNum = "?";
                        }
                    }
                    declaracionesASM_TMP.add("    " + identNombre + " dw " + valorNum + " ; Numero (16-bit)");
                    break;

                // Podrías añadir más tipos si tu lenguaje los soporta, ej:
                // case "byte": // Para números de 8 bits
                //    // Lógica similar a "numero" pero usando "db"
                //    declaracionesASM_TMP.add("    " + identNombre + " db " + valorNum8bit + " ; Byte (8-bit)");
                //    break;
                default:
                    System.err.println("ADVERTENCIA: Tipo de dato '" + simb.getTipo() + "' para el identificador '" + identNombre + "' no manejado en convertirDeclaracionesASM.");
                    declaracionesASM_TMP.add("    " + identNombre + " dw ? ; Tipo desconocido, reservando palabra"); //Fallback o error
                    break;
            }
        }
    }

    public void crearArchivoCodigoASM(String rutaArchivo) {
        String objeto = rutaArchivo;
        objeto = objeto.replace(".re", ".ASM");
        try {
            this.codigoASM = new PrintStream(new FileOutputStream(objeto));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // En guardarFinalASM()
    public void guardarFinalASM() {
        codigoASM.println("");
        codigoASM.println("    ; Terminar el programa");
        codigoASM.println("    mov ah, 4Ch");
        codigoASM.println("    int 21h");
        codigoASM.println("MAIN ENDP"); // Final del procedimiento principal
        codigoASM.println("END MAIN");  // Final del archivo, MAIN es el punto de entrada
    }

    public void guardarEncabezadoASM() {
        codigoASM.println(".model small");
        codigoASM.println(".stack 100h");
        codigoASM.println(".data");
    }

    public void guardarSegmentoDatos() {
        codigoASM.println("");
        codigoASM.println(".code");
        codigoASM.println("mov ax,@data");
        codigoASM.println("mov ds,ax");
    }

    public void guardarDeclaracionesASM() {
        for (Object declaraciones : declaracionesASM_TMP) {
            codigoASM.println(declaraciones.toString());
        }
    }

    public void guardarCuerpoASM() {
        for (Object declaraciones : codigoASM_TMP) {
            codigoASM.println(declaraciones.toString());
        }
    }
    // En guardarEncabezadoASM() o una nueva función que llames después

    public void guardarInicioDelCodigo() { // Cambia el nombre para más claridad
        codigoASM.println("");
        codigoASM.println(".CODE");
        codigoASM.println("MAIN PROC"); // Define el inicio del procedimiento principal
        codigoASM.println("    ; Inicializar Segmento de Datos");
        codigoASM.println("    mov ax, @DATA");
        codigoASM.println("    mov ds, ax");
        codigoASM.println("    mov es, ax ; A menudo útil también para operaciones de cadena");
        codigoASM.println(""); // Línea en blanco para separar
    }



    public void CuerpoTemporalASM(String operacion, String operando1, String operando2, String resultado) {
        String etiqTemp1, etiqTemp2;
        codigoASM_TMP.add("");
        switch (operacion.toUpperCase()) {
            case "MOSTRAR_CADENA":
                imprimirCadena(resultado);
                break;
            //case "WRITE_NUMERO":imprimirNumero(resultado);
            //  break;
            }
    }

    public String nuevaTemp() {
        contadorTemp++;
        return "t" + contadorTemp;
    }

    public void imprimirCadena(String texto) {
        codigoASM_TMP.add("mov ah,09");
        codigoASM_TMP.add("lea dx," + texto);
        codigoASM_TMP.add("int 21h");
    }

    public void cerrarArchivoCodigoASM() {
        codigoASM.close();
        declaracionesASM_TMP.clear();
        codigoASM_TMP.clear();
    }
}
