package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreeAddressToAsmConverter {

    private Set<String> variables;
    private List<String> stringLiteralsData;
    private static final int FIXED_POINT_SCALE = 100; // Para 2 decimales de precisión

    public ThreeAddressToAsmConverter() {
        this.variables = new LinkedHashSet<>();
        this.stringLiteralsData = new ArrayList<>();
    }

    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Determina si una variable debe ser tratada con punto fijo.
    // Esta es una simplificación. Idealmente, esta información proviene de la tabla de símbolos.
    private boolean isFixedPointVariable(String varName) {
        // Hardcodeado para el ejemplo: 'contadoResultado' y temporales que podrían heredar su "decimalidad".
        // O si el nombre de la variable sugiere que es decimal (ej. si se usa una convención).
        return varName.equals("contadoResultado") || varName.startsWith("t_fp_"); // Ejemplo de convención
    }


    private String getNumericValue(String str, String contextVarName) {
        try {
            double val = Double.parseDouble(str);
            // Escalar si el contexto de la variable es de punto fijo O si el literal es inherentemente decimal
            if (isFixedPointVariable(contextVarName) || (str.contains(".") && !str.endsWith("."))) { // Evitar "10." como decimal sin fracción
                return String.valueOf((int) Math.round(val * FIXED_POINT_SCALE)); // Usar Math.round para mejor precisión antes de castear
            } else {
                return String.valueOf((int) val); // Truncar para enteros
            }
        } catch (NumberFormatException e) {
            // Podría ser un identificador, no lo tratamos como numérico aquí si falla el parseo.
            // System.err.println("Advertencia: getNumericValue no pudo parsear '" + str + "' como número.");
            return "0"; // O un valor por defecto o lanzar error si se esperaba un número
        }
    }

    // Escala un literal numérico si es para una operación de punto fijo.
    private String getScaledNumericLiteral(String strLiteral) {
         try {
            double val = Double.parseDouble(strLiteral);
            // Si el literal contiene un punto, o si no lo contiene pero se usa en contexto FP, se escala.
            // Aquí asumimos que si se llama a esta función, el contexto ya es FP.
            return String.valueOf((int) Math.round(val * FIXED_POINT_SCALE));
        } catch (NumberFormatException e) {
            System.err.println("Advertencia: getScaledNumericLiteral no pudo parsear '" + strLiteral + "' como número.");
            return "0";
        }
    }


    public void convert(String filePath3D) throws IOException {
        if (!filePath3D.endsWith(".3D")) {
            throw new IllegalArgumentException("El archivo debe tener la extensión .3D");
        }
        String asmFilePath = filePath3D.substring(0, filePath3D.lastIndexOf('.')) + ".asm";

        StringBuilder asmCode = new StringBuilder();
        List<String> threeAddressLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath3D))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                threeAddressLines.add(line);
                collectData(line);
            }
        }

        asmCode.append(".MODEL SMALL\n");
        asmCode.append("JUMPS\n");
        asmCode.append(".STACK 100h\n\n");

        asmCode.append(".DATA\n");
        for (String var : this.variables) {
            asmCode.append(String.format("    %-10s DW ?\n", var));
        }
        for (int i = 0; i < this.stringLiteralsData.size(); i++) {
            String literal = this.stringLiteralsData.get(i);
            String safeLiteral = literal.replace("'", "''"); // Escapar comillas simples para literales ASM
            asmCode.append(String.format("    MSG%02d%-5s DB '%s', 0Dh, 0Ah, '$'\n", i, "", safeLiteral));
        }
        asmCode.append("    NEWLINE_CHARS DB 0Dh, 0Ah, '$'\n");
        asmCode.append("    PROMPT_SCAN   DB 'Ingrese un numero (y presione Enter): $'\n\n");


        asmCode.append(".CODE\n");
        asmCode.append("MAIN PROC\n");
        asmCode.append("    MOV AX, @DATA\n");
        asmCode.append("    MOV DS, AX\n");
        
        asmCode.append("    JMP principal\n\n");

        for (String line : threeAddressLines) {
            asmCode.append(translateLine(line, this.stringLiteralsData));
        }

        asmCode.append("    MOV AH, 4Ch\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("MAIN ENDP\n\n");

        appendPrintNumProcedure(asmCode);
        appendPrintNumFixedPointProcedure(asmCode);
        appendScanNumLocalProcedure(asmCode);

        asmCode.append("END MAIN\n");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(asmFilePath))) {
            writer.write(asmCode.toString());
        }
        System.out.println("Archivo ensamblador generado en: " + asmFilePath);
    }

    private void collectData(String line) {
        String processedLine = line;
        if (processedLine.endsWith(";")) {
            processedLine = processedLine.substring(0, processedLine.length() - 1);
        }

        // --- Ignorar param ---
        if (processedLine.toLowerCase().startsWith("param ")) {
            return;
        }
        // --- Declarar variables que se "reciben" como parámetros ---
        Pattern recibirPattern = Pattern.compile("^recibir\\s+([a-zA-Z_][a-zA-Z0-9_]*);?$", Pattern.CASE_INSENSITIVE);
        Matcher recibirMatcher = recibirPattern.matcher(processedLine);
        if (recibirMatcher.matches()) {
            this.variables.add(recibirMatcher.group(1).trim());
            return;
        }

        // --- Recolectar variables del retornar ---
        Pattern retornarPattern = Pattern.compile("^retornar\\s*([a-zA-Z_][a-zA-Z0-9_]*|[.0-9]+)?\\s*$", Pattern.CASE_INSENSITIVE);
        Matcher retMatcher = retornarPattern.matcher(processedLine);
        if (retMatcher.matches()) {
            String val = retMatcher.group(1);
            if (val != null && !isNumeric(val)) this.variables.add(val.trim());
            return;
        }

        Pattern assignPattern = Pattern.compile("^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+)$");
        Matcher assignMatcher = assignPattern.matcher(processedLine);
        if (assignMatcher.matches()) {
            this.variables.add(assignMatcher.group(1).trim());
            String rightSide = assignMatcher.group(2).trim();

            // --- Ignorar llamadas a función para no declararlas como variables ---
            if (rightSide.toLowerCase().startsWith("call ")) {
                return;
            }

            Pattern exprPattern = Pattern.compile("^\\s*([a-zA-Z_][a-zA-Z0-9_]*|[.0-9]+)\\s*([+\\-*/])\\s*([a-zA-Z_][a-zA-Z0-9_]*|[.0-9]+)\\s*$");
            Matcher exprMatcher = exprPattern.matcher(rightSide);
            if (exprMatcher.matches()) {
                if (!isNumeric(exprMatcher.group(1))) this.variables.add(exprMatcher.group(1).trim());
                if (!isNumeric(exprMatcher.group(3))) this.variables.add(exprMatcher.group(3).trim());
            } else {
                if (!isNumeric(rightSide) && !rightSide.startsWith("\"")) this.variables.add(rightSide.trim());
            }
        }

        Pattern ifPattern = Pattern.compile("^IF\\s+([a-zA-Z_][a-zA-Z0-9_]*|[.0-9]+)\\s*([><=!]=?|<>)\\s*([a-zA-Z_][a-zA-Z0-9_]*|[.0-9]+)\\s+goto\\s+L\\d+$", Pattern.CASE_INSENSITIVE);
        Matcher ifMatcher = ifPattern.matcher(processedLine);
        if (ifMatcher.matches()) {
            if (!isNumeric(ifMatcher.group(1))) this.variables.add(ifMatcher.group(1).trim());
            if (!isNumeric(ifMatcher.group(3))) this.variables.add(ifMatcher.group(3).trim());
        }

        Pattern mostrarVarPattern = Pattern.compile("^mostrar\\s+([a-zA-Z_][a-zA-Z0-9_]*)$", Pattern.CASE_INSENSITIVE);
        Matcher mostrarVarMatcher = mostrarVarPattern.matcher(processedLine);
        if(mostrarVarMatcher.matches()){
            this.variables.add(mostrarVarMatcher.group(1).trim());
        }

        Pattern mostrarStringPattern = Pattern.compile("^mostrar\\s+\"([^\"]*)\"$", Pattern.CASE_INSENSITIVE);
        Matcher mostrarStringMatcher = mostrarStringPattern.matcher(processedLine);
         if (mostrarStringMatcher.matches()) {
            String literal = mostrarStringMatcher.group(1);
            if (!this.stringLiteralsData.contains(literal)) {
                 this.stringLiteralsData.add(literal);
            }
        }

        Pattern leerVarPattern = Pattern.compile("^leer\\s+([a-zA-Z_][a-zA-Z0-9_]*)$", Pattern.CASE_INSENSITIVE);
        Matcher leerVarMatcher = leerVarPattern.matcher(processedLine);
        if (leerVarMatcher.matches()) {
            this.variables.add(leerVarMatcher.group(1).trim());
        }
    }

private String translateLine(String line, List<String> stringLiteralsView) {
        StringBuilder sb = new StringBuilder();
        String processedLine = line.trim();

        if (processedLine.endsWith(";")) {
            processedLine = processedLine.substring(0, processedLine.length() - 1).trim();
        }

        // --- Aceptar cualquier nombre como etiqueta (ej. sumar: o L1:) ---
        Pattern labelPattern = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*):$");
        Matcher labelMatcher = labelPattern.matcher(processedLine);
        if (labelMatcher.matches()) {
            sb.append(labelMatcher.group(1)).append(":\n");
            return sb.toString();
        }
        
        // --- Retornar (RET en ensamblador) ---
        Pattern retornarPattern = Pattern.compile("^retornar\\s*([a-zA-Z_][a-zA-Z0-9_]*|[.0-9]+)?\\s*$", Pattern.CASE_INSENSITIVE);
        Matcher retornarMatcher = retornarPattern.matcher(processedLine);
        if (retornarMatcher.matches()) {
            String val = retornarMatcher.group(1);
            if (val != null) {
                if (isNumeric(val)) sb.append(String.format("    MOV AX, %s\n", getNumericValue(val, "")));
                else sb.append(String.format("    MOV AX, %s\n", val));
            }
            sb.append("    RET\n"); // Instrucción vital para salir de la función
            return sb.toString();
        }

        // --- Enviar parámetro a la Pila (PUSH) ---
        Pattern paramPattern = Pattern.compile("^param\\s+([a-zA-Z_][a-zA-Z0-9_]*|[.0-9]+);?$", Pattern.CASE_INSENSITIVE);
        Matcher paramMatcher = paramPattern.matcher(processedLine);
        if (paramMatcher.matches()) {
            String var = paramMatcher.group(1);
            if (isNumeric(var)) {
                sb.append(String.format("    MOV AX, %s\n", getNumericValue(var, "")));
                sb.append("    PUSH AX\n"); // Guardar en la pila
            } else {
                sb.append(String.format("    PUSH %s\n", var));
            }
            return sb.toString();
        }

        // --- Recibir parámetro (El truco maestro del POP) ---
        Pattern recibirPattern = Pattern.compile("^recibir\\s+([a-zA-Z_][a-zA-Z0-9_]*);?$", Pattern.CASE_INSENSITIVE);
        Matcher recibirMatcher = recibirPattern.matcher(processedLine);
        if (recibirMatcher.matches()) {
            String var = recibirMatcher.group(1);
            sb.append("    POP DX  ; Extraer temporalmente el IP (Direccion de Retorno)\n");
            sb.append("    POP AX  ; Extraer el parametro enviado en el CALL\n");
            sb.append(String.format("    MOV %s, AX ; Guardarlo en la variable local\n", var));
            sb.append("    PUSH DX ; Devolver el IP a la cima de la pila para el RET\n");
            return sb.toString();
        }

        // ---  Llamada a función con asignación (t1 = call sumar, 2) ---
        Pattern callAssignPattern = Pattern.compile("^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*call\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*,\\s*\\d+\\s*$");
        Matcher callAssignMatcher = callAssignPattern.matcher(processedLine);
        if (callAssignMatcher.matches()) {
            String dest = callAssignMatcher.group(1);
            String func = callAssignMatcher.group(2);
            sb.append(String.format("    CALL %s\n", func));
            sb.append(String.format("    MOV %s, AX\n", dest)); // Las funciones guardan su resultado en AX
            return sb.toString();
        }

        // --- Llamada a función simple (call saludar, 0) ---
        Pattern callPattern = Pattern.compile("^call\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*,\\s*\\d+$", Pattern.CASE_INSENSITIVE);
        Matcher callMatcher = callPattern.matcher(processedLine);
        if (callMatcher.matches()) {
            String func = callMatcher.group(1);
            sb.append(String.format("    CALL %s\n", func));
            return sb.toString();
        }

        Pattern assignSimplePattern = Pattern.compile("^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([a-zA-Z_][a-zA-Z0-9_]*|[.0-9]+)\\s*$");
        Matcher assignSimpleMatcher = assignSimplePattern.matcher(processedLine);
        if (assignSimpleMatcher.matches()) {
            String dest = assignSimpleMatcher.group(1).trim();
            String source = assignSimpleMatcher.group(2).trim();
            if (isNumeric(source)) {
                sb.append(String.format("    MOV %s, %s\n", dest, getNumericValue(source, dest)));
            } else {
                sb.append(String.format("    MOV AX, %s\n", source));
                sb.append(String.format("    MOV %s, AX\n", dest));
            }
            return sb.toString();
        }

        Pattern assignExprPattern = Pattern.compile("^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([a-zA-Z_][a-zA-Z0-9_]*|[.0-9]+)\\s*([+\\-*/])\\s*([a-zA-Z_][a-zA-Z0-9_]*|[.0-9]+)\\s*$");
        Matcher assignExprMatcher = assignExprPattern.matcher(processedLine);
        if (assignExprMatcher.matches()) {
            String dest = assignExprMatcher.group(1).trim();
            String op1_str = assignExprMatcher.group(2).trim();
            String operator = assignExprMatcher.group(3).trim();
            String op2_str = assignExprMatcher.group(4).trim();

            boolean isFPOperation = isFixedPointVariable(dest) ||
                                    (isNumeric(op1_str) && op1_str.contains(".")) || isFixedPointVariable(op1_str) ||
                                    (isNumeric(op2_str) && op2_str.contains(".")) || isFixedPointVariable(op2_str);

            if (isNumeric(op1_str)) {
                sb.append(String.format("    MOV AX, %s\n", isFPOperation ? getScaledNumericLiteral(op1_str) : getNumericValue(op1_str, "")));
            } else {
                sb.append(String.format("    MOV AX, %s\n", op1_str));
            }

            if (isNumeric(op2_str)) {
                 sb.append(String.format("    MOV BX, %s\n", isFPOperation ? getScaledNumericLiteral(op2_str) : getNumericValue(op2_str, "")));
            } else {
                sb.append(String.format("    MOV BX, %s\n", op2_str));
            }

            switch (operator) {
                case "+": sb.append("    ADD AX, BX\n"); break;
                case "-": sb.append("    SUB AX, BX\n"); break;
                case "*":
                    sb.append("    MUL BX\n");
                    if (isFPOperation) {
                        boolean op1IsFP = (isNumeric(op1_str) && op1_str.contains(".")) || isFixedPointVariable(op1_str);
                        boolean op2IsFP = (isNumeric(op2_str) && op2_str.contains(".")) || isFixedPointVariable(op2_str);
                        if (op1IsFP && op2IsFP) {
                            sb.append("    MOV CX, ").append(FIXED_POINT_SCALE).append("\n");
                            sb.append("    DIV CX\n");
                        }
                    }
                    break;
                case "/":
                    sb.append("    XOR DX, DX\n");
                    if (isFPOperation) {
                        boolean op1IsFP = (isNumeric(op1_str) && op1_str.contains(".")) || isFixedPointVariable(op1_str);
                        boolean op2IsFP = (isNumeric(op2_str) && op2_str.contains(".")) || isFixedPointVariable(op2_str);
                        if(op1IsFP && !op2IsFP){ // (a*S) / b_int -> resultado ya escalado
                             sb.append("    DIV BX\n");
                        } else if (op1IsFP && op2IsFP) { // (a*S) / (b*S) -> a/b (no escalado), re-escalar
                             sb.append("    PUSH BX\n"); // Guardar divisor b*S
                             sb.append("    MOV CX, ").append(FIXED_POINT_SCALE).append("\n");
                             sb.append("    MUL CX\n");     // AX = (a*S)*S = a*S^2 (DX:AX)
                             sb.append("    POP BX\n");     // Recuperar divisor b*S
                             sb.append("    DIV BX\n");     // AX = (a*S^2)/(b*S) = (a/b)*S
                        } else if (!op1IsFP && op2IsFP) { // a_int / (b*S) -> (a/b)/S (desescalado), re-escalar por S^2
                             // Esto es más complejo y menos común.
                             // (a * S^2) / (b*S) = (a/b)*S
                             sb.append("    PUSH BX\n"); // Guardar b*S
                             sb.append("    MOV BX, ").append(FIXED_POINT_SCALE * FIXED_POINT_SCALE).append("\n");
                             sb.append("    MUL BX\n"); // AX = a * S^2 (DX:AX)
                             sb.append("    POP BX\n"); // Recuperar b*S
                             sb.append("    DIV BX\n"); // AX = (a*S^2)/(b*S) = (a/b)*S
                        }
                         else { // Ambos enteros, división normal
                            sb.append("    DIV BX\n");
                        }
                    } else {
                         sb.append("    DIV BX\n");
                    }
                    break;
            }
            sb.append(String.format("    MOV %s, AX\n", dest));
            return sb.toString();
        }

        // SOPORTE PARA EXPRESIONES COMPUESTAS (yy / AND) 

        Pattern ifCompoundPattern = Pattern.compile("^IF\\s+([a-zA-Z_0-9.]+)\\s*([><=!]=?|<>)\\s*([a-zA-Z_0-9.]+)\\s*yy\\s*([a-zA-Z_0-9.]+)\\s*([><=!]=?|<>)\\s*([a-zA-Z_0-9.]+)\\s+goto\\s+(L\\d+)$", Pattern.CASE_INSENSITIVE);
        Matcher ifCompoundMatcher = ifCompoundPattern.matcher(processedLine);
        if (ifCompoundMatcher.matches()) {
            String op1 = ifCompoundMatcher.group(1).trim();
            String comp1 = ifCompoundMatcher.group(2).trim();
            String op2 = ifCompoundMatcher.group(3).trim();
            String op3 = ifCompoundMatcher.group(4).trim();
            String comp2 = ifCompoundMatcher.group(5).trim();
            String op4 = ifCompoundMatcher.group(6).trim();
            String label = ifCompoundMatcher.group(7).trim();

            // Etiqueta única para no chocar con otros IFs compuestos
            String uniqueId = String.valueOf(System.nanoTime()).substring(8); 

            // Determinar instrucciones de salto
            String jump1 = "", jump2 = "";
            switch (comp1) { case "==": jump1="JE"; break; case ">": jump1="JG"; break; case "<": jump1="JL"; break; case ">=": jump1="JGE"; break; case "<=": jump1="JLE"; break; case "!=": case "<>": jump1="JNE"; break; }
            switch (comp2) { case "==": jump2="JE"; break; case ">": jump2="JG"; break; case "<": jump2="JL"; break; case ">=": jump2="JGE"; break; case "<=": jump2="JLE"; break; case "!=": case "<>": jump2="JNE"; break; }

            // PARTE 1 DE LA CONDICIÓN
            if (isNumeric(op1)) sb.append(String.format("    MOV AX, %s\n", getNumericValue(op1, "")));
            else sb.append(String.format("    MOV AX, %s\n", op1));

            if (isNumeric(op2)) sb.append(String.format("    CMP AX, %s\n", getNumericValue(op2, "")));
            else { sb.append(String.format("    MOV BX, %s\n", op2)); sb.append("    CMP AX, BX\n"); }

            sb.append(String.format("    %s @@EVAL_P2_%s\n", jump1, uniqueId));
            sb.append(String.format("    JMP @@SKIP_%s\n", uniqueId));

            // PARTE 2 DE LA CONDICIÓN
            sb.append(String.format("@@EVAL_P2_%s:\n", uniqueId));
            if (isNumeric(op3)) sb.append(String.format("    MOV AX, %s\n", getNumericValue(op3, "")));
            else sb.append(String.format("    MOV AX, %s\n", op3));

            if (isNumeric(op4)) sb.append(String.format("    CMP AX, %s\n", getNumericValue(op4, "")));
            else { sb.append(String.format("    MOV BX, %s\n", op4)); sb.append("    CMP AX, BX\n"); }

            sb.append(String.format("    %s %s\n", jump2, label));
            
            sb.append(String.format("@@SKIP_%s:\n", uniqueId));

            return sb.toString();
        }
        
        Pattern ifGotoPattern = Pattern.compile("^IF\\s+([a-zA-Z_][a-zA-Z0-9_]*|[.0-9]+)\\s*([><=!]=?|<>)\\s*([a-zA-Z_][a-zA-Z0-9_]*|[.0-9]+)\\s+goto\\s+(L\\d+)$", Pattern.CASE_INSENSITIVE);
        Matcher ifGotoMatcher = ifGotoPattern.matcher(processedLine);
        if (ifGotoMatcher.matches()) {
            String op1_str = ifGotoMatcher.group(1).trim();
            String comparator = ifGotoMatcher.group(2).trim();
            String op2_str = ifGotoMatcher.group(3).trim();
            String label = ifGotoMatcher.group(4).trim();

            boolean op1IsFPContext = isFixedPointVariable(op1_str) || (isNumeric(op1_str) && op1_str.contains("."));
            boolean op2IsFPContext = isFixedPointVariable(op2_str) || (isNumeric(op2_str) && op2_str.contains("."));
            boolean isComparisonFP = op1IsFPContext || op2IsFPContext;


            if (isNumeric(op1_str)) {
                sb.append(String.format("    MOV AX, %s\n", isComparisonFP ? getScaledNumericLiteral(op1_str) : getNumericValue(op1_str, "")));
            } else {
                sb.append(String.format("    MOV AX, %s\n", op1_str));
            }

            if (isNumeric(op2_str)) {
                sb.append(String.format("    CMP AX, %s\n", isComparisonFP ? getScaledNumericLiteral(op2_str) : getNumericValue(op2_str, "")));
            } else {
                sb.append(String.format("    MOV BX, %s\n", op2_str));
                sb.append("    CMP AX, BX\n");
            }

            String jumpInstruction = "";
            switch (comparator) {
                case "==": jumpInstruction = "JE"; break;
                case ">":  jumpInstruction = "JG"; break;
                case "<":  jumpInstruction = "JL"; break;
                case ">=": jumpInstruction = "JGE"; break;
                case "<=": jumpInstruction = "JLE"; break;
                case "!=": case "<>": jumpInstruction = "JNE"; break;
            }
            if (!jumpInstruction.isEmpty()) {
                sb.append(String.format("    %s %s\n", jumpInstruction, label));
            }
            return sb.toString();
        }

        Pattern gotoPattern = Pattern.compile("^goto\\s+(L\\d+)$", Pattern.CASE_INSENSITIVE);
        Matcher gotoMatcher = gotoPattern.matcher(processedLine);
        if (gotoMatcher.matches()) {
            sb.append(String.format("    JMP %s\n", gotoMatcher.group(1)));
            return sb.toString();
        }

        Pattern mostrarStringPattern = Pattern.compile("^mostrar\\s+\"([^\"]*)\"$", Pattern.CASE_INSENSITIVE);
        Matcher mostrarStringMatcher = mostrarStringPattern.matcher(processedLine);
        if (mostrarStringMatcher.matches()) {
            String strContent = mostrarStringMatcher.group(1);
            int strIndex = stringLiteralsView.indexOf(strContent);
            if (strIndex != -1) {
                sb.append(String.format("    LEA DX, MSG%02d\n", strIndex));
                sb.append("    MOV AH, 09h\n");
                sb.append("    INT 21h\n");
            }
            return sb.toString();
        }

        Pattern mostrarVarPattern = Pattern.compile("^mostrar\\s+([a-zA-Z_][a-zA-Z0-9_]*)$", Pattern.CASE_INSENSITIVE);
        Matcher mostrarVarMatcher = mostrarVarPattern.matcher(processedLine);
        if (mostrarVarMatcher.matches()) {
            String varName = mostrarVarMatcher.group(1).trim();
            sb.append(String.format("    MOV AX, %s\n", varName));
            if (isFixedPointVariable(varName)) {
                sb.append("    CALL PRINT_NUM_FIXED_POINT\n");
            } else {
                sb.append("    CALL PRINT_NUM\n");
            }
            sb.append("    LEA DX, NEWLINE_CHARS\n");
            sb.append("    MOV AH, 09h\n");
            sb.append("    INT 21h\n");
            return sb.toString();
        }

        Pattern leerVarPattern = Pattern.compile("^leer\\s+([a-zA-Z_][a-zA-Z0-9_]*)$", Pattern.CASE_INSENSITIVE);
        Matcher leerVarMatcher = leerVarPattern.matcher(processedLine);
        if (leerVarMatcher.matches()) {
            String varName = leerVarMatcher.group(1).trim();
            sb.append("    LEA DX, PROMPT_SCAN\n");
            sb.append("    MOV AH, 09h\n");
            sb.append("    INT 21h\n");
            sb.append("    CALL SCAN_NUM_LOCAL\n");
            sb.append(String.format("    MOV %s, AX\n", varName));
            // Si la variable leída es de punto fijo, necesitarías escalar AX después de SCAN_NUM_LOCAL
            // if (isFixedPointVariable(varName)) {
            //     sb.append("    MOV CX, ").append(FIXED_POINT_SCALE).append("\n");
            //     sb.append("    IMUL CX\n"); // AX = AX * SCALE (DX:AX)
            //     sb.append(String.format("    MOV %s, AX\n", varName));
            // }
            sb.append("    LEA DX, NEWLINE_CHARS\n");
            sb.append("    MOV AH, 09h\n");
            sb.append("    INT 21h\n");
            return sb.toString();
        }
        
        Pattern haltPattern = Pattern.compile("^HALT$", Pattern.CASE_INSENSITIVE);
        if (haltPattern.matcher(processedLine).matches()) {
            // La finalización del programa ya está manejada al final de MAIN PROC
            // No se necesita generar nada extra aquí a menos que sea un HALT específico
            return ""; // No genera nada para HALT ya que es implícito
        }


        return ""; // No genera nada si no se pudo traducir
    }

    private void appendPrintNumProcedure(StringBuilder asmCode) {
        asmCode.append("PRINT_NUM PROC\n");
        asmCode.append("    PUSH AX\n");
        asmCode.append("    PUSH BX\n");
        asmCode.append("    PUSH CX\n");
        asmCode.append("    PUSH DX\n");
        asmCode.append("    CMP AX, 0\n");
        asmCode.append("    JNS @@PRINT_NUM_POSITIVE\n");
        asmCode.append("    PUSH AX\n");
        asmCode.append("    MOV DL, '-'\n");
        asmCode.append("    MOV AH, 02h\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("    POP AX\n");
        asmCode.append("    NEG AX\n");
        asmCode.append("@@PRINT_NUM_POSITIVE:\n");
        asmCode.append("    XOR CX, CX\n");
        asmCode.append("    MOV BX, 10\n");
        asmCode.append("@@PRINT_NUM_LOOP1:\n");
        asmCode.append("    XOR DX, DX\n");
        asmCode.append("    DIV BX\n");
        asmCode.append("    PUSH DX\n");
        asmCode.append("    INC CX\n");
        asmCode.append("    CMP AX, 0\n");
        asmCode.append("    JNE @@PRINT_NUM_LOOP1\n");
        asmCode.append("@@PRINT_NUM_LOOP2:\n");
        asmCode.append("    POP DX\n");
        asmCode.append("    ADD DL, '0'\n");
        asmCode.append("    MOV AH, 02h\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("    LOOP @@PRINT_NUM_LOOP2\n");
        asmCode.append("    POP DX\n");
        asmCode.append("    POP CX\n");
        asmCode.append("    POP BX\n");
        asmCode.append("    POP AX\n");
        asmCode.append("    RET\n");
        asmCode.append("PRINT_NUM ENDP\n\n");
    }

    private void appendPrintNumFixedPointProcedure(StringBuilder asmCode) {
        asmCode.append("PRINT_NUM_FIXED_POINT PROC\n");
        asmCode.append("    PUSH AX\n");
        asmCode.append("    PUSH BX\n");
        asmCode.append("    PUSH CX\n");
        asmCode.append("    PUSH DX\n");
        asmCode.append("    CMP AX, 0\n");
        asmCode.append("    JGE @@PF_POSITIVE\n");
        asmCode.append("    PUSH AX\n");
        asmCode.append("    MOV DL, '-'\n");
        asmCode.append("    MOV AH, 02h\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("    POP AX\n");
        asmCode.append("    NEG AX\n");
        asmCode.append("@@PF_POSITIVE:\n");
        asmCode.append("    MOV BX, ").append(FIXED_POINT_SCALE).append("\n");
        asmCode.append("    XOR DX, DX\n");
        asmCode.append("    DIV BX\n");
        asmCode.append("    PUSH DX\n");
        asmCode.append("    CALL PRINT_NUM\n");
        asmCode.append("    MOV DL, '.'\n");
        asmCode.append("    MOV AH, 02h\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("    POP AX\n");
        asmCode.append("    MOV BL, 10\n");
        asmCode.append("    XOR DX, DX\n"); // DX debe ser 0 para DIV BL, AH no se usa como parte alta
        asmCode.append("    DIV BL\n");     // AL = (ParteFrac / 10), AH = (ParteFrac % 10)
        asmCode.append("    PUSH AX\n");
        asmCode.append("    MOV DL, AL\n");
        asmCode.append("    ADD DL, '0'\n");
        asmCode.append("    MOV AH, 02h\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("    POP AX\n");
        asmCode.append("    MOV DL, AH\n");
        asmCode.append("    ADD DL, '0'\n");
        asmCode.append("    MOV AH, 02h\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("    POP DX\n");
        asmCode.append("    POP CX\n");
        asmCode.append("    POP BX\n");
        asmCode.append("    POP AX\n");
        asmCode.append("    RET\n");
        asmCode.append("PRINT_NUM_FIXED_POINT ENDP\n\n");
    }

    private void appendScanNumLocalProcedure(StringBuilder asmCode) {
        asmCode.append("SCAN_NUM_LOCAL PROC\n");
        asmCode.append("    PUSH BX\n");
        asmCode.append("    PUSH CX\n");
        asmCode.append("    PUSH DX\n");
        asmCode.append("    PUSH SI\n");
        asmCode.append("    PUSH DI\n");
        asmCode.append("    XOR BX, BX\n");
        asmCode.append("    XOR CX, CX\n");
        asmCode.append("    MOV SI, 0\n");
        asmCode.append("    MOV AH, 01h\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("    CMP AL, '-'\n");
        asmCode.append("    JNE @@SL_CHECK_PLUS_SCAN\n");
        asmCode.append("    MOV CX, 1\n");
        asmCode.append("    MOV AH, 01h\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("    JMP @@SL_CONVERSION_LOOP_SCAN\n");
        asmCode.append("@@SL_CHECK_PLUS_SCAN:\n");
        asmCode.append("    CMP AL, '+'\n");
        asmCode.append("    JNE @@SL_CONVERSION_LOOP_SCAN\n");
        asmCode.append("    MOV AH, 01h\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("@@SL_CONVERSION_LOOP_SCAN:\n");
        asmCode.append("    CMP AL, 0DH\n");
        asmCode.append("    JE @@SL_FINALIZE_NUMBER_SCAN\n");
        asmCode.append("    CMP AL, '0'\n");
        asmCode.append("    JL @@SL_INVALID_CHAR_SCAN\n");
        asmCode.append("    CMP AL, '9'\n");
        asmCode.append("    JG @@SL_INVALID_CHAR_SCAN\n");
        asmCode.append("    INC SI\n");
        asmCode.append("    SUB AL, '0'\n");
        asmCode.append("    MOV AH, 0\n");
        asmCode.append("    PUSH AX\n");
        asmCode.append("    MOV AX, BX\n");
        asmCode.append("    MOV DI, 10\n");
        asmCode.append("    MUL DI\n");
        asmCode.append("    CMP DX, 0\n");
        asmCode.append("    JNE @@SL_HANDLE_OVERFLOW_SCAN\n");
        asmCode.append("    MOV BX, AX\n");
        asmCode.append("    POP AX\n");
        asmCode.append("    ADD BX, AX\n");
        asmCode.append("    JC @@SL_HANDLE_OVERFLOW_SCAN\n");
        asmCode.append("    MOV AH, 01h\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("    JMP @@SL_CONVERSION_LOOP_SCAN\n");
        asmCode.append("@@SL_INVALID_CHAR_SCAN:\n");
        asmCode.append("    MOV AH, 02h\n");
        asmCode.append("    MOV DL, 0Dh\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("    MOV DL, 0Ah\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("@@SL_FINALIZE_NUMBER_SCAN:\n");
        asmCode.append("    CMP SI, 0\n");
        asmCode.append("    JNE @@SL_APPLY_SIGN_SCAN\n");
        asmCode.append("    XOR BX, BX\n");
        asmCode.append("@@SL_APPLY_SIGN_SCAN:\n");
        asmCode.append("    CMP CX, 1\n");
        asmCode.append("    JNE @@SL_MOVE_TO_AX_SCAN\n");
        asmCode.append("    NEG BX\n");
        asmCode.append("@@SL_MOVE_TO_AX_SCAN:\n");
        asmCode.append("    MOV AX, BX\n");
        asmCode.append("    JMP @@SL_EXIT_SCAN\n");
        asmCode.append("@@SL_HANDLE_OVERFLOW_SCAN:\n");
        asmCode.append("    MOV AX, 0\n");
        asmCode.append("@@SL_EXIT_SCAN:\n");
        asmCode.append("    POP DI\n");
        asmCode.append("    POP SI\n");
        asmCode.append("    POP DX\n");
        asmCode.append("    POP CX\n");
        asmCode.append("    POP BX\n");
        asmCode.append("    RET\n");
        asmCode.append("SCAN_NUM_LOCAL ENDP\n\n");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso: java ThreeAddressToAsmConverter <ruta_al_archivo.3D>");
            String testFile = "test_fp.3D";
            createSample3DFileForFixedPointTest(testFile);
            System.out.println("Archivo de prueba '" + testFile + "' creado. Ejecuta de nuevo con '" + testFile + "' como argumento.");
            return;
        }

        String filePath = args[0];
        ThreeAddressToAsmConverter converter = new ThreeAddressToAsmConverter();
        try {
            converter.convert(filePath);
        } catch (IOException e) {
            System.err.println("Error durante la conversión: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void createSample3DFileForFixedPointTest(String filename) {
        // Ejemplo para probar el punto fijo
        String content =
            "entero_var=10;\n" +
            "contadoResultado=0.0;\n" + // Variable que usaremos con punto fijo
            "t_fp_temp1 = contadoResultado + 2.55;\n" + // Suma FP
            "contadoResultado = t_fp_temp1;\n" +
            "mostrar contadoResultado;\n" + // Debería mostrar 2.55
            "t_fp_temp2 = contadoResultado * 2.0;\n" + // Multiplicación FP. 2.55 * 2.0 = 5.10
            "contadoResultado = t_fp_temp2;\n" +
            "mostrar contadoResultado;\n" + // Debería mostrar 5.10
            "t_fp_temp3 = entero_var + 0.5;\n" + // Entero + FP literal -> resultado FP
            "mostrar t_fp_temp3;\n" + // Debería mostrar 10.50
            "HALT;\n"; // Añadir un HALT para C3D

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("No se pudo crear el archivo de prueba: " + e.getMessage());
        }
    }
}