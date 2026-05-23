// Prueba 1: Falta el tipo de retorno (Debería lanzar Error 273)
sumar(num a, num b) {
    retornar a + b;
}

// Prueba 2: Falta el nombre de la función (Debería lanzar Error 274)
num (num x) {
    retornar x;
}

// Prueba 3: Falta el paréntesis de apertura (Debería lanzar Error 275)
inicio funcionSinAbrir num a) {
    escribirLinea("Falta abrir");
}

// Prueba 4: Falta la llave de apertura (Debería lanzar Error 277)
inicio funcionSinLlave()
    escribirLinea("Falta llave");
}

inicio principal() {
    num a = 5;
    
    // Prueba 5: Llamada sin punto y coma (Debería lanzar Error 279)
    sumar(a, a)

    // Prueba 6: Llamada sin paréntesis de apertura (Debería lanzar Error 280)
    sumar a, a);

    // Prueba 7: Llamada sin paréntesis de cierre (Debería lanzar Error 281)
    sumar(a, a;
}