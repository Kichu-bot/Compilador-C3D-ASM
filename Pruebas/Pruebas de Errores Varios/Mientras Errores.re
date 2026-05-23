inicio principal() {
    num contador = 0;

    // 0. ESTRUCTURA PERFECTA
    mientras (contador < 5) {
        contador++;
    }

    // ERRORES DEL CICLO MIENTRAS
    
    // 1. Falta '(' (Error 256)
    mientras contador < 5 ) { 
        contador++;
    }

    // 2. Falta ')' (Error 257)
    mientras ( contador < 5 { 
        contador++;
    }

    // 3. Falta '{' (Error 258)
    mientras (contador < 5) 
        contador++;
    }

    // 4. Condición vacía (Error 260)
    mientras () { 
        contador++;
    }

    // 5. Falta '}' (Error 259)
    mientras (contador < 5) {
        contador++;
        
        12345 // OBSTÁCULO para que no se robe la llave de principal()
}