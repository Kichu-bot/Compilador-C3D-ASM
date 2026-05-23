inicio principal() {
    // 1. Declaraciones y Asignaciones
    num contador = 0;
    num acumulador = 0;
    num opcion = 1;

    escribirLinea("--- INICIO DE LA MEGAPRUEBA ---");

    // 2. Condicionales Simples (si, sino, final)
    si (opcion == 1) {
        escribirLinea("El sistema inicio correctamente.");
    } sino (opcion == 2) {
        escribirLinea("Opcion alterna.");
    } final {
        escribirLinea("Error de inicio.");
    }

    // 3. Ciclo Mientras con un Elegir/Caso anidado
    mientras (contador < 3) {
        contador++;
        
        elegir (contador) {
            caso 1:
                escribirLinea("Primera vuelta del mientras");
                salir;
            caso 2:
                escribirLinea("Segunda vuelta del mientras");
                salir;
            defecto:
                escribirLinea("Tercera y ultima vuelta");
                salir;
        }
    }

    // 4. Ciclo Para anidado dentro de otro Para, con un Si anidado
    para (num i = 0; i < 2; i++) {
        escribirLinea("Bucle externo numero: " + i);
        
        para (num j = 0; j < 2; j++) {
            acumulador += 1;
            
            si (acumulador > 2) {
                escribirLinea("El acumulador ya supero la mitad");
            } final {
                escribirLinea("El acumulador sigue bajo");
            }
        }
    }

    // 5. Verificacion final de variables
    escribirLinea("Valor final del acumulador: " + acumulador);
    escribirLinea("--- FIN DE LA MEGAPRUEBA ---");
}
