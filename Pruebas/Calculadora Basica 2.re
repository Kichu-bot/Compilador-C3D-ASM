inicio principal() {
    num opcion = 1;
    num num1 = 0;
    num num2 = 0;
    num resultado = 0;

    mientras (opcion > 0) {
        escribirLinea("==============================");
        escribirLinea("    CALCULADORA EXPANDIDA     ");
        escribirLinea("==============================");
        escribirLinea("1. Sumar");
        escribirLinea("2. Restar");
        escribirLinea("3. Multiplicar");
        escribirLinea("4. Dividir");
        escribirLinea("0. Salir del programa");
        escribirLinea("==============================");
        
        escribir("Elija una opcion: ");
        leer(opcion);

        elegir (opcion) {
            caso 1:
                escribir("Ingrese el primer numero: ");
                leer(num1);
                escribir("Ingrese el segundo numero: ");
                leer(num2);
                resultado = num1 + num2;
                escribir("-> La suma es: ");
                escribirLinea(resultado);
                salir;

            caso 2:
                escribir("Ingrese el primer numero: ");
                leer(num1);
                escribir("Ingrese el segundo numero: ");
                leer(num2);
                resultado = num1 - num2;
                escribir("-> La resta es: ");
                escribirLinea(resultado);
                salir;

            caso 3:
                escribir("Ingrese el primer numero: ");
                leer(num1);
                escribir("Ingrese el segundo numero: ");
                leer(num2);
                resultado = num1 * num2;
                escribir("-> La multiplicacion es: ");
                escribirLinea(resultado);
                salir;

            caso 4:
                escribir("Ingrese el dividendo: ");
                leer(num1);
                escribir("Ingrese el divisor (no use cero): ");
                leer(num2);
                
                // NOTA: Para no forzar la gramática con condicionales anidados,
                // confiaremos en que el usuario no escriba 0 por ahora.
                resultado = num1 / num2;
                
                escribir("-> La division es: ");
                escribirLinea(resultado);
                salir;

            caso 0:
                escribirLinea("Saliendo de la calculadora. Adios!");
                salir;

            defecto:
                escribirLinea("-> ERROR: Opcion no valida.");
                salir;
        }
        
        escribirLinea(""); 
    }
}