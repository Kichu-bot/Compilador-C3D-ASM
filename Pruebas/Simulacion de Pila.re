inicio principal() {
    // 1. "Declaracion" de nuestro Arreglo simulado (Tamano 5)
    num valor = 0;
    
    num mem0 = 0;
    num mem1 = 0;
    num mem2 = 0;
    num mem3 = 0;
    num mem4 = 0;

    escribirLinea("==============================");
    escribirLinea("   SIMULADOR DE ARREGLOS v1   ");
    escribirLinea("==============================");
    escribirLinea("Llenando el arreglo con un ciclo...");

    // 2. ESCRITURA EN EL ARREGLO (Equivalente a arreglo[i] = valor)
    para (num i = 0; i <= 4; i++) {
        // Generamos un dato de prueba (0, 10, 20, 30, 40)
        valor = i * 10; 
        
        // MMU: Enrutador de Escritura
        elegir (i) {
            caso 0: mem0 = valor; salir;
            caso 1: mem1 = valor; salir;
            caso 2: mem2 = valor; salir;
            caso 3: mem3 = valor; salir;
            caso 4: mem4 = valor; salir;
        }
    }
    
    escribirLinea("Arreglo llenado con exito.");
    escribirLinea("Leyendo datos del arreglo...");
    escribirLinea("");

    // 3. LECTURA DEL ARREGLO (Equivalente a valor = arreglo[j])
    para (num j = 0; j <= 4; j++) {
        
        // MMU: Enrutador de Lectura
        elegir (j) {
            caso 0: valor = mem0; salir;
            caso 1: valor = mem1; salir;
            caso 2: valor = mem2; salir;
            caso 3: valor = mem3; salir;
            caso 4: valor = mem4; salir;
        }
        
        escribir("Valor en el indice ");
        escribir(j);
        escribir(": ");
        escribirLinea(valor);
    }
    
    escribirLinea("==============================");
}