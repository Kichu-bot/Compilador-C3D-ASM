// ==========================================
// FUNCIÓN 1: Factorial 
// ==========================================
num calcularFactorial(num numero) {
    num resultado_fact = 1;
    num temp = numero;
    
    mientras (temp > 1) {
        resultado_fact = resultado_fact * temp;
        temp--;
    }
    retornar resultado_fact;
}

// ==========================================
// FUNCIÓN 2: Primo
// ==========================================
num verificarPrimo(num numero) {
    num es_primo = 1;
    
    si (numero <= 1) {
        es_primo = 0;
    } final {
        num limite = numero / 2;
        para (num i = 2; i <= limite; i++) {
            
            // Simulación del módulo
            num divi_primo = numero / i;
            num mult_primo = divi_primo * i;
            num residuo_val = numero - mult_primo;
            
            si (residuo_val == 0) {
                es_primo = 0;
            }
        }
    }
    retornar es_primo;
}

// ==========================================
// FUNCIÓN 3: Paridad
// ==========================================
num verificarPar(num numero) {
    
    // Simulación del módulo 
    num divi_par = numero / 2;
    num mult_par = divi_par * 2;
    num residuo_par = numero - mult_par;
    
    num es_par = 0;
    si (residuo_par == 0) {
        es_par = 1;
    }
    retornar es_par;
}

// ==========================================
// BLOQUE PRINCIPAL
// ==========================================
inicio principal() {
    num opcion = 1;
    num n = 0;
    
    mientras (opcion != 0) {
        escribirLinea("===================================");
        escribirLinea("   ANALIZADOR MATEMATICO  ");
        escribirLinea("===================================");
        escribirLinea("1. Calcular Factorial");
        escribirLinea("2. Verificar si es Primo");
        escribirLinea("3. Saber si es Par o Impar");
        escribirLinea("0. Salir");
        escribirLinea("===================================");
        escribir("Elija una opcion: ");
        leer(opcion);

        elegir (opcion) {
            caso 1:
                escribir("Ingrese numero para factorial: ");
                leer(n);
                num fact = calcularFactorial(n); 
                escribir("El factorial es: ");
                escribirLinea(fact);
                salir;
                
            caso 2:
                escribir("Ingrese numero a verificar: ");
                leer(n);
                num primo = verificarPrimo(n);
                si (primo == 1) {
                    escribirLinea("-> El numero SI es primo.");
                } final {
                    escribirLinea("-> El numero NO es primo.");
                }
                salir;
                
            caso 3:
                escribir("Ingrese numero: ");
                leer(n);
                num par = verificarPar(n);
                si (par == 1) {
                    escribirLinea("-> El numero es PAR.");
                } final {
                    escribirLinea("-> El numero es IMPAR.");
                }
                salir;
                
            caso 0:
                escribirLinea("Apagando sistema matricial. Adios!");
                salir;
                
            defecto:
                escribirLinea("-> Opcion invalida. Intente de nuevo.");
                salir;
        }
    }
    retornar;
}