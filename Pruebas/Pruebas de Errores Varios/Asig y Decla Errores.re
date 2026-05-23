inicio principal() {
    // --- CORRECTAS ---
    num puntos = 100;
    puntos = 50;

    // ==========================================
    // ERRORES ASIGNACIÓN
    // ==========================================
    // 1. Falta punto y coma (Error 226)
    puntos = 20
    // 2. Falta el valor (Error 227)
    puntos = ;
    
    // 3. Falta la variable (Error 228)
    = 10;

    // ==========================================
    // ERRORES DECLARACIÓN
    // ==========================================
    // 4. Falta punto y coma (Error 229)
    texto nombre = "Heroe"
    
    // 5. Falta el nombre (Error 230)
    texto = "Villano";
    
    // 6. Falta el valor tras el igual (Error 231)
    logico bandera = ;
}
