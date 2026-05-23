inicio principal() {
    num limite = 10;
    num iterador = 0;
    num suma = 0;
    num valor = 7;

    mientras (iterador < limite) {
        suma = suma + (iterador * 2);
        iterador++;
    }

    escribir(suma);

       si (valor > 15) {
        escribirLinea("Mayor a 15");
    } sino (valor > 5) {
        escribirLinea("Mayor a 5");
    } final {
        escribirLinea("Menor o igual a 5");
    }
}
