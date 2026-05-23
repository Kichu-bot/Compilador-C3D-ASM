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
    num stop = 0;
    num resultado = 0;

    mientras (opcion != 5) {
        escribirLinea("==============================");
        escribirLinea("   CALCULADORA MULTIPROPOSITO ");
        escribirLinea("==============================");
        escribirLinea("1. Operacion Simple (Suma)");
        escribirLinea("2. Operacion Compleja (Division Segura)");
        escribirLinea("3. Ciclo Anidado (Tabla de Multiplicar)");
        escribirLinea("4. Operacion con Funcion (Factorial)");
        escribirLinea("5. Salir");
        escribirLinea("==============================");
        
        escribir("Seleccione una opcion: ");
        leer(opcion);

        // AQUI ESTA LA ESTRELLA DE LA RUBRICA: EL SWITCH (ELEGIR)
        elegir (opcion) {
            caso 1:
                // Operacion aritmetica simple
                escribir("Ingrese primer numero: ");
                leer(valor1);
                escribir("Ingrese segundo numero: ");
                leer(valor2);
                
                resultado = valor1 + valor2;
                
                escribir("El resultado de la suma es: ");
                escribirLinea(resultado);
                salir;

            caso 2:
                // If-Else anidado para control semantico/logico
                escribir("Ingrese el dividendo: ");
                leer(valor1);
                escribir("Ingrese el divisor (No puede ser cero): ");
                leer(valor2);
                
                si (valor2 == 0) {
                    escribirLinea("-> ERROR: No se puede dividir entre cero.");
                } final {
                    resultado = valor1 / valor2;
                    escribir("El resultado de la division es: ");
                    escribirLinea(resultado);
                }
                salir;

            caso 3:
                // Estructura PARA dentro de un CASO
                escribir("Ingrese un numero para ver su tabla: ");
                leer(valor1);
                escribirLinea("Generando tabla...");
                
                para (num i = 1; i <= 10; i++) {
                    resultado = valor1 * i;
                    
                    escribir(valor1);
                    escribir(" x ");
                    escribir(i);
                    escribir(" = ");
                    escribirLinea(resultado);
                }
                salir;

            caso 4:
                // Llamada a función, paso de parámetros y retorno
                escribir("Ingrese un numero para calcular su factorial: ");
                leer(valor1);
                
                // Llamada a la función externa
                resultado = calcularFactorial(valor1);
                
                escribir("El factorial es: ");
                escribirLinea(resultado);
                salir;

            caso 5:
                escribirLinea("Saliendo de la calculadora...");
                salir;

            defecto:
                // Manejo de errores exigido por la rubrica
                escribirLinea("-> ERROR: Opcion invalida. Intente de nuevo.");
                salir;
        }
        escribirLinea("");
    }
}
