inicio principal() {
    num opcion = 2;

    // 0. ESTRUCTURA PERFECTA
    elegir (opcion) {
        caso 1:
            escribirLinea("Uno");
            salir;
        caso 2:
            escribirLinea("Dos");
            salir;
        defecto:
            escribirLinea("Otro");
            salir;
    }

    // ERRORES DE CABECERA (ELEGIR)
    // 1. Falta '(' (Error 248)
    elegir opcion ) { }

    // 2. Falta ')' (Error 249)
    elegir ( opcion { }

    // 3. Variable vacía (Error 250)
    elegir () { }

    // 4. Falta '{' (Error 251)
    elegir (opcion) 
        caso 1: 
            salir;
    }

    // ERRORES INTERNOS (CASO, DEFECTO, SALIR)
    elegir (opcion) {
        // 5. Falta valor en caso (Error 252)
        caso :
            salir;
        
        // 6. Faltan dos puntos en caso (Error 253)
        caso 3
            escribirLinea("Tres");
            salir;

        // 7. Faltan dos puntos en defecto (Error 254)
        defecto
            salir;
    }

    elegir (opcion) {
        caso 4:
            // 8. Falta punto y coma en salir (Error 255)
            salir
        defecto:
            salir;
    }
}