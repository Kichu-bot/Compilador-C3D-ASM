inicio funcionInutil() {
    retornar;
}

inicio principal() {
    num x = 10 / 0;             // Error 128: División por cero
    num y = funcionInutil();    // Error 127: Asignación de void
    
    salir;
    escribirLinea("Fantasma");  // Error 129: Código inalcanzable
}
h