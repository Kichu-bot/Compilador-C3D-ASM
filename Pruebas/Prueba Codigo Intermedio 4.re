inicio principal() {
    num limite = 10;
    num iterador = 0;
    num suma = 0;
    num valor = 7;
    num iteraciones = 0;
    num opcion = 0;

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

    para (num i = 0; i < 3; i++) {
        para (num j = 0; j < 2; j++) {
            iteraciones++;
        }
    }

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
}
