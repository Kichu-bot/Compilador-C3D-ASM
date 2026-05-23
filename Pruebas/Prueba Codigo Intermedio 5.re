inicio principal() {
    num iteraciones = 4;
    num contador = 0;
    
    escribirLinea("--- INICIO DEL PROGRAMA ---");
    
    mientras (contador < iteraciones) {
        contador++;
        num opcion = contador;
        
        elegir (opcion) {
            caso 1:
                escribirLinea("Analizando caso 1");
                num a = 5;
                num b = 10;
                num calculo = a + (b * 2);
                
                si (calculo == 25) {
                    escribirLinea("El calculo es exactamente 25");
                } sino (calculo > 25) {
                    escribirLinea("El calculo es mayor");
                } final {
                    escribirLinea("El calculo es menor");
                }
                salir;
                
            caso 2:
                escribirLinea("Iniciando ciclo en caso 2");
                para (num i = 0; i < 2; i++) {
                    escribir(i);
                }
                salir;
                
            defecto:
                escribirLinea("Opcion por defecto alcanzada");
                salir;
        }
    }
    
    escribirLinea("--- FIN DEL PROGRAMA ---");
}