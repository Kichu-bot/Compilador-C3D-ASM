inicio principal() {
    num z = 0;
    
    // --- CORRECTO ---
    para (num i = 0; i < 5; i++) {
        z += 1;
    }

    // --- ERRORES INTERIORES ---
    // 1. Falta el incremento (Error 244)
    para (num a = 0; a < 5; ) { }

    // 2. Falta punto y coma post-condición (Error 245)
    para (num b = 0; b < 5 b++) { }

    // 3. Falta la condición (Error 246)
    para (num c = 0; ; c++) { }

    // 4. Falta variable en incremento (Error 247)
    para (num d = 0; d < 5; ++) { }

    // --- ERRORES EXTERIORES ---
    // 5. Falta '(' (Error 240)
    para num x = 0; x < 5; x++) { }

    // 6. Falta ')' (Error 241)
    para (num y = 0; y < 5; y++ { }

    // 7. Falta '{' (Error 242)
    para (num w = 0; w < 5; w++) 
        z++;
    }

    // 8. Falta '}' (Error 243)
    para (num q = 0; q < 5; q++) {
        z--;
123 // Para error de }

}