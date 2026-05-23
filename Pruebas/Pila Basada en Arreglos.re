inicio principal() {
    num opcion = 1;
    num tope = 0;
    num valor = 0;
    num indice = 0;

    // Nuestro Arreglo Simulado de tamano 5 (Memoria)
    num mem0 = 0;
    num mem1 = 0;
    num mem2 = 0;
    num mem3 = 0;
    num mem4 = 0;

    mientras (opcion > 0) {
        escribirLinea("==============================");
        escribirLinea("  PILA BASADA EN ARRAY v2.0   ");
        escribirLinea("==============================");
        escribirLinea("1. Push (Apilar)");
        escribirLinea("2. Pop (Desapilar)");
        escribirLinea("3. Peek (Ver solo la Cima)");
        escribirLinea("4. Mostrar toda la Memoria");
        escribirLinea("0. Salir");
        escribirLinea("==============================");
        
        escribir("Elija una opcion: ");
        leer(opcion);

        elegir (opcion) {
            caso 1:
                si (tope == 5) {
                    escribirLinea("-> ERROR: Pila LLENA (Stack Overflow)");
                } final {
                    escribir("Ingrese el numero a apilar: ");
                    leer(valor);
                    
                    // MMU: Enrutador de ESCRITURA en el arreglo
                    elegir (tope) {
                        caso 0: mem0 = valor; salir;
                        caso 1: mem1 = valor; salir;
                        caso 2: mem2 = valor; salir;
                        caso 3: mem3 = valor; salir;
                        caso 4: mem4 = valor; salir;
                    }
                    
                    tope++; // Subimos el tope DESPUES de guardar
                    escribirLinea("-> Valor apilado correctamente.");
                }
                salir;

            caso 2:
                si (tope == 0) {
                    escribirLinea("-> ERROR: Pila VACIA (Stack Underflow)");
                } final {
                    tope--; // Bajamos el tope ANTES de extraer
                    
                    // MMU: Enrutador de LECTURA del arreglo
                    elegir (tope) {
                        caso 0: valor = mem0; salir;
                        caso 1: valor = mem1; salir;
                        caso 2: valor = mem2; salir;
                        caso 3: valor = mem3; salir;
                        caso 4: valor = mem4; salir;
                    }
                    
                    escribir("-> Valor desapilado: ");
                    escribirLinea(valor);
                }
                salir;

            caso 3:
                si (tope == 0) {
                    escribirLinea("-> ERROR: La pila esta vacia.");
                } final {
                    // La cima siempre esta un espacio abajo del tope actual
                    indice = tope - 1; 
                    
                    elegir (indice) {
                        caso 0: valor = mem0; salir;
                        caso 1: valor = mem1; salir;
                        caso 2: valor = mem2; salir;
                        caso 3: valor = mem3; salir;
                        caso 4: valor = mem4; salir;
                    }
                    escribir("-> El valor en la CIMA es: ");
                    escribirLinea(valor);
                }
                salir;

            caso 4:
                si (tope == 0) {
                    escribirLinea("-> [ Pila Vacia ]");
                } final {
                    escribirLinea("--- MEMORIA ACTUAL ---");
                    
                    // Magia pura: Ciclo FOR que lee nuestro arreglo simulado
                    para (num i = 0; i < tope; i++) {
                        elegir (i) {
                            caso 0: valor = mem0; salir;
                            caso 1: valor = mem1; salir;
                            caso 2: valor = mem2; salir;
                            caso 3: valor = mem3; salir;
                            caso 4: valor = mem4; salir;
                        }
                        escribir("Indice [");
                        escribir(i);
                        escribir("] : ");
                        escribirLinea(valor);
                    }
                    escribirLinea("----------------------");
                }
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