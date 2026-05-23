inicio principal() {
    // =========================================================
    // PARTE 1: ESTRUCTURAS PERFECTAS (El compilador debe aceptarlas)
    // =========================================================
    
    // Declaraciones y asignaciones
    num variableNum = 10;
    texto variableTexto = "Hola Mundo";
    logico variableLogica = verdadero;
    flot variableFlotante = 3.14;
    
    variableNum = 20;
    variableNum++;
    variableNum += 5;

    // Lectura y Escritura
    escribir("Ingrese valor: ");
    leer(variableNum);
    escribirLinea("El valor es: " + variableNum);

    // Ciclo Para
    para (num i = 0; i < 3; i++) {
        variableNum++;
    }

    // Ciclo Mientras
    mientras (variableNum < 50) {
        variableNum++;
    }

    // Menú Elegir
    elegir (variableNum) {
        caso 1:
            escribirLinea("Uno");
            salir;
        defecto:
            escribirLinea("Otro");
            salir;
    }

    // Condicionales (Si, Sino, Final)
    si (variableNum > 10) {
        escribirLinea("Mayor");
    } sino (variableNum == 10) {
        escribirLinea("Igual");
    } final {
        escribirLinea("Menor");
    }

    escribirLinea("--- HASTA AQUI TODO PERFECTO ---");

    // =========================================================
    // PARTE 2: EL CAOS (Todos los errores forzados)
    // =========================================================

    // --- ERRORES BÁSICOS (Asumiendo que tienes reglas de falta de punto y coma) ---
    num rotaDeclaracion = 5
    rotaDeclaracion = 10
    escribirLinea("Falta punto y coma")
    leer(rotaDeclaracion)
    rotaDeclaracion++

    // --- ERRORES DEL CICLO PARA (240 - 247) ---
    para num i = 0; i < 1; i++) { } 
    para (num i = 0; i < 1; i++ { } 
    para (num i = 0; i < 1; i++) 
        escribirLinea("Falta llave");
    }
    para (num i = 0; i < 1; ) { } 
    para (num i = 0; i < 1 i++) { } 
    para (num i = 0; ; i++) { } 
    para (num i = 0; i < 1; ++) { } 

    // --- ERRORES DEL CICLO MIENTRAS (256 - 260) ---
    mientras variableNum < 10 ) { } 
    mientras ( variableNum < 10 { } 
    mientras (variableNum < 10) 
        variableNum++;
    }
    mientras () { } 

    // --- ERRORES DEL MENÚ ELEGIR (248 - 255) ---
    elegir variableNum ) { } 
    elegir ( variableNum { } 
    elegir () { } 
    elegir (variableNum) 
        caso 1: salir;
    }

    elegir (variableNum) {
        caso : 
            salir; 
        caso 2 
            salir;
        defecto 
            salir;
    }

    elegir (variableNum) {
        caso 3:
            salir 
        defecto:
            salir;
    }

    // --- ERRORES CONDICIONALES (261 - 272) ---
    si variableNum > 1 ) { } 
    si ( variableNum > 1 { } 
    si (variableNum > 1) 
        variableNum++;
    }
    si () { } 

    si (variableNum > 1) {
        variableNum++;
    } sino variableNum == 2 ) { } 

    si (variableNum > 1) {
        variableNum++;
    } sino ( variableNum == 2 { } 

    si (variableNum > 1) {
        variableNum++;
    } sino (variableNum == 2) 
        variableNum++;
    }

    si (variableNum > 1) {
        variableNum++;
    } sino () { } 

    si (variableNum > 1) {
        variableNum++;
    } final 
        variableNum++;
    }

    // =========================================================
    // PARTE 3: OBSTÁCULOS FINALES (Las llaves de cierre perdidas)
    // =========================================================
    
    para (num i = 0; i < 1; i++) {
        escribirLinea("Falta llave cierre PARA");
        12345 // (Error 243)
        
    mientras (variableNum < 10) { 
        variableNum++;
        12345 // (Error 259)
        
    si (variableNum > 1) {
        variableNum++;
        12345 // (Error 264)
        
    si (variableNum > 1) {
        variableNum++;
    } sino (variableNum == 2) {
        variableNum++;
        12345 // (Error 271)
        
    si (variableNum > 1) {
        variableNum++;
    } final {
        variableNum++;
        12345 // (Error 267)

    escribirLinea("FIN DE LA MEGAPRUEBA");
// Forzamos Error 80 no poniendo la llave final aqui:
