inicio saludar() {
    escribirLinea("Iniciando programa de prueba...");
    retornar;
}

num sumar(num a, num b) {
    num resultado = a + b;
    retornar resultado;
}

inicio principal() {
    saludar();
    a = 10;
    b = 5;
    num total = sumar(x, y);

    escribir("El total es: ");
    escribirLinea(total);
}