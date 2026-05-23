num calcularFactorial(num numero) {
    num resultado_fact = 1;
    num temp = numero;
    mientras (temp > 1) {
        resultado_fact = resultado_fact * temp;
        temp--;
    }
    retornar resultado_fact;
}

inicio principal() {
    num opcion = 0;
    num valor1 = 0;
    num valor2 = 0;
    num resultado = 0;

    mientras (opcion != 5) {
        escribir("Seleccione una opcion: ");
        leer(opcion);

        // ERROR SINTÁCTICO: Falta el paréntesis de apertura '('
        elegir opcion) { 
            
            caso 1:
                resultado = valor1 + valor2;
                escribirLinea(resultado);
                salir;
                resultado = 0; // ERROR SEMÁNTICO 129: Código inalcanzable (está después del salir)

            // ERROR SINTÁCTICO: Faltan los dos puntos ':' después del 2
            caso 2:
                
                // ERROR LÉXICO: Símbolo no reconocido @
                valor1 = 10 @ 5; 
                
                // ERROR SEMÁNTICO 128: División por cero estática
                resultado = valor1 / 0; 
                salir;

            caso 3:
                // ERROR SEMÁNTICO 125: Crisis de Identidad (Llamar a una variable normal como si fuera función)
                valor1(); 

                // ERROR SEMÁNTICO 122 y 126: Firmas inválidas (Se envían 2 parámetros en lugar de 1, y uno es texto)
                resultado = calcularFactorial(valor1, "texto_ilegal");
                salir;

            defecto:
                escribirLinea("Opcion Invalida");
                // ERROR SINTÁCTICO: Falta el punto y coma ';' al final de la instrucción
                salir
        }
    }
}
