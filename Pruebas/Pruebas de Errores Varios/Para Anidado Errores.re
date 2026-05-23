inicio principal() {
    num iteraciones = 0;

    // 1. ANIDADO PERFECTO (Debe compilar limpio)
    para (num i = 0; i < 3; i++) {
        para (num j = 0; j < 2; j++) {
            iteraciones++;
        }
    }
    escribirLinea("Total iteraciones: " + iteraciones);

    // 2. ERROR EN EL FOR INTERNO 
    // (Falta la llave de cierre del for interno)
    para (num x = 0; x < 2; x++) {
        para (num y = 0; y < 2; y++) {
            escribirLinea("Falta llave interna");
            
        12345 // OBSTÁCULO para forzar el Error 243 en el interno
    }

    // 3. ERROR EN EL FOR EXTERNO 
    // (Falta la condición de evaluación)
    para (num a = 0; ; a++) {
        para (num b = 0; b < 2; b++) {
            escribirLinea("El interno esta bien, el externo no");
        }
    }
}