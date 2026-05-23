inicio principal() {
    num valor = 10;
    escribirLinea("--- INICIANDO EL CAOS ---");

    // ==========================================
    // 1. DESTRUYENDO EL CICLO 'PARA' 
    // ==========================================
    // (Falta el punto y coma despues de la condicion)
    para (num i = 0; i < 5 i++) { 
        escribirLinea("Falta punto y coma");
    }
    
    // (Falta la condicion en medio)
    para (num j = 0; ; j++) {
        escribirLinea("Falta condicion");
    }

    // ==========================================
    // 2. DESTRUYENDO EL CICLO 'MIENTRAS'
    // ==========================================
    // (Falta llave de apertura)
    mientras (valor < 20) 
        escribirLinea("Falta llave de apertura");
    }

    // (Condicion vacia)
    mientras () {
        escribirLinea("Falta condicion");
    }

    // ==========================================
    // 3. DESTRUYENDO EL MENÚ 'ELEGIR'
    // ==========================================
    // (Falta el parentesis de apertura y llaves)
    elegir valor ) {
        
        // (Falta el valor del caso)
        caso :
            escribirLinea("Falta valor a evaluar");
            salir;
        
        // (Faltan los dos puntos despues del 2)
        caso 2
            escribirLinea("Faltan dos puntos");
            // (Falta el punto y coma en el salir)
            salir
            
        defecto:
            escribirLinea("Todo mal");
            salir;
    }

    // ==========================================
    // 4. DESTRUYENDO LOS CONDICIONALES
    // ==========================================
    // (Falta parentesis de apertura)
    si valor > 5 ) {
        escribirLinea("Falta parentesis");
        
    // (Falta llave de apertura en el sino)
    } sino (valor == 10) 
        escribirLinea("Falta llave en sino");
        
    // (Falta llave de apertura en el final)
    } final 
        escribirLinea("Falta llave en final");
    }

    // ==========================================
    // 5. EL ERROR DEL LADRÓN DE LLAVES
    // ==========================================
    si (valor == 10) {
        escribirLinea("A este si le falta su llave de cierre");
        
        12345 // OBSTACULO para atrapar la falta de llave de cierre

    escribirLinea("--- FIN DEL CAOS ---");
// Provocamos el Error 80 (falta llave final de principal) ignorando el cierre aqui abajo
