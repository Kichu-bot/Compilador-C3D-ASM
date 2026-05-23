inicio principal() {
    num opcion = 1;
    num tope = 0;
    num valor = 0;
    
    // Nuestra memoria simulada para la pila
    num pila1 = 0;
    num pila2 = 0;
    num pila3 = 0;

    mientras (opcion > 0) {
        escribirLinea("==============================");
        escribirLinea("       PILA (STACK) v1.0      ");
        escribirLinea("==============================");
        escribirLinea("1. Push (Apilar)");
        escribirLinea("2. Pop (Desapilar)");
        escribirLinea("3. Mostrar Pila");
        escribirLinea("0. Salir");
        escribirLinea("==============================");
        
        escribir("Elija una opcion: ");
        leer(opcion);

        elegir (opcion) {
            caso 1:
                si (tope == 3) {
                    escribirLinea("-> ERROR: Pila LLENA (Overflow)");
                } final {
                    escribir("Ingrese el numero a apilar: ");
                    leer(valor);
                    tope++;
                    
                    // Simulamos el guardado en el arreglo usando el tope
                    elegir (tope) {
                        caso 1: pila1 = valor; salir;
                        caso 2: pila2 = valor; salir;
                        caso 3: pila3 = valor; salir;
                    }
                    escribirLinea("-> Valor apilado correctamente.");
                }
                salir;

            caso 2:
                si (tope == 0) {
                    escribirLinea("-> ERROR: Pila VACIA (Underflow)");
                } final {
                    // Simulamos la lectura del arreglo usando el tope
                    elegir (tope) {
                        caso 3: valor = pila3; pila3 = 0; salir;
                        caso 2: valor = pila2; pila2 = 0; salir;
                        caso 1: valor = pila1; pila1 = 0; salir;
                    }
                    tope--;
                    escribir("-> Valor desapilado: ");
                    escribirLinea(valor);
                }
                salir;

            caso 3:
                escribirLinea("--- ESTADO ACTUAL DE LA PILA ---");
                si (tope == 0) {
                    escribirLinea("[ Pila Vacia ]");
                } final {
                    // Imprimimos desde el tope hacia abajo (LIFO)
                    si (tope >= 3) { escribir("Posicion 3: "); escribirLinea(pila3); }
                    si (tope >= 2) { escribir("Posicion 2: "); escribirLinea(pila2); }
                    si (tope >= 1) { escribir("Posicion 1: "); escribirLinea(pila1); }
                }
                escribirLinea("--------------------------------");
                salir;

            caso 0:
                escribirLinea("Apagando sistema...");
                salir;

            defecto:
                escribirLinea("-> ERROR: Opcion invalida.");
                salir;
        }
        escribirLinea(""); 
    }
}