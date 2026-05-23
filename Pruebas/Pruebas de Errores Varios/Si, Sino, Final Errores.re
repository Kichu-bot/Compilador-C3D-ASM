inicio principal() {
    num valor = 10;

    // 0. ESTRUCTURA PERFECTA
    si (valor > 15) {
        escribirLinea("Mayor a 15");
    } sino (valor > 5) {
        escribirLinea("Mayor a 5");
    } final {
        escribirLinea("Menor o igual a 5");
    }

    // ERRORES DEL 'SI'
    si valor > 5 ) { 
        escribirLinea("Error 261");
    }

    si ( valor > 5 { 
        escribirLinea("Error 262");
    }

    si (valor > 5) 
        escribirLinea("Error 263");
    }

    si () { 
        escribirLinea("Error 265");
    }

    // ERRORES DEL 'SINO' (ELSE IF)
    si (valor == 1) {
        escribirLinea("OK");
    } sino valor == 2 ) {
        escribirLinea("Error 268");
    }

    si (valor == 1) {
        escribirLinea("OK");
    } sino ( valor == 2 {
        escribirLinea("Error 269");
    }

    si (valor == 1) {
        escribirLinea("OK");
    } sino (valor == 2) 
        escribirLinea("Error 270");
    }

    // ERRORES DEL 'FINAL' (ELSE)
    si (valor > 5) {
        escribirLinea("OK");
    } final 
        escribirLinea("Error 266");
    }

    // ERRORES DE LLAVES FALTANTES (CON OBSTÁCULOS)
    si (valor > 5) {
        escribirLinea("Falta llave } en si");
        12345 // OBSTÁCULO Error 264

    si (valor == 1) {
        escribirLinea("OK");
    } sino (valor == 2) {
        escribirLinea("Falta llave } en sino");
        12345 // OBSTÁCULO Error 271

    si (valor > 5) {
        escribirLinea("OK");
    } final {
        escribirLinea("Falta llave en final");
        12345 // OBSTÁCULO Error 267
}