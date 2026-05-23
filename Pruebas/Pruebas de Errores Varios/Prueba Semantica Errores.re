inicio principal() {
    // ==========================================
    // PARTE 1: EL CAMINO FELIZ (Debe dar 0 Errores)
    // ==========================================
    num edad = 20;
    num dinero = 100;
    texto estado = "Activo";
    logico tienePase = verdadero;

    // 1. SI simple con múltiples condiciones (AND lógico 'yy')
    si (edad >= 18 yy dinero >= 50 yy tienePase == verdadero) {
        escribirLinea("Puedes entrar al evento");
    }

    // 2. SI con FINAL (If - Else)
    si (dinero < 200) {
        escribirLinea("No te alcanza para el VIP");
    } final {
        escribirLinea("Pase VIP comprado");
    }

    // 3. SI con SINO y FINAL (If - ElseIf - Else)
    si (estado == "Inactivo") {
        escribirLinea("Cuenta inactiva");
    } sino (estado == "Pausado") {
        escribirLinea("Cuenta pausada");
    } final {
        escribirLinea("Cuenta activa");
    }

    // 4. SI Anidados a profundidad
    si (edad >= 18) {
        si (estado == "Activo") {
            si (dinero > 0) {
                escribirLinea("Usuario valido y con fondos");
            }
        } final {
            escribirLinea("Usuario mayor pero sin cuenta activa");
        }
    }

    // ==========================================
    // PARTE 2: PRUEBAS DE FUEGO (Errores Semánticos)
    // Quita las barras '//' de una en una para probar
    // ==========================================

    // PRUEBA A: Variable fantasma en un SI simple (Debe dar Error 85)
     si (vida == 0) { escribirLinea("Game Over"); }

    // PRUEBA B: Variable fantasma en un SINO / ElseIf (Debe dar Error 85)
     si (edad == 10) { } sino (magia == 5) { }

    // PRUEBA C: Variable fantasma en un SI muy anidado (Debe dar Error 85)
     si (edad > 10) {
         si (estado == "Activo") {
            si (nivelFantasma == 1) { escribirLinea("Error profundo"); }
          }
     }
}
