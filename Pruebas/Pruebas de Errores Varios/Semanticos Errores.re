// ==========================================================
// ERROR 123: Función con tipo, pero SIN instrucción 'retornar'
// ==========================================================
num funcionSinRetorno(num a) {
    num b = a + 1;
}

// ==========================================================
// ERROR 120: Función 'inicio' (void) intentando retornar un valor
// ==========================================================
inicio funcionVoidConRetorno() {
    retornar 10;
}

// ==========================================================
// ERROR 121: Función con tipo retornando vacío
// ==========================================================
texto funcionRetornoVacio() {
    retornar;
}

// Función auxiliar válida para probar las llamadas más adelante
num sumarMatematica(num a, num b) {
    retornar a + b;
}

// ==========================================================
// ERROR 124: Falta el punto de entrada 'inicio principal()'
// (Hemos llamado a esta función 'no_principal' intencionalmente)
// ==========================================================
inicio no_principal() {

    // ==========================================================
    // ERROR 130: 'salir' (break) huérfano fuera de un ciclo o elegir
    // ==========================================================
    salir;

    // ==========================================================
    // ERROR 129: Código inalcanzable (está después de un salir/retornar)
    // ==========================================================
    num inalcanzable = 0;

    // ==========================================================
    // ERROR 82: Declaración múltiple de la misma variable
    // ==========================================================
    num variableRepetida = 1;
    num variableRepetida = 2;

    // ==========================================================
    // ERROR 84: Asignar valor a una variable que NO existe
    // ==========================================================
    variableFantasma = 100;

    // ==========================================================
    // ERROR 85: Usar variables que NO existen en distintas estructuras
    // ==========================================================
    mientras (fantasmaBucle < 10) { }
    elegir (fantasmaSwitch) { }
    escribirLinea(fantasmaImpresion);

    // ==========================================================
    // ERRORES DE TIPO EN DECLARACIONES (Errores 100 al 108)
    // Tu compilador no permite mezclar peras con manzanas.
    // ==========================================================
    num err100 = "texto";       // Error 100: num <- texto
    num err101 = 3.14;          // Error 101: num <- decimal (Pérdida de precisión)
    num err102 = verdadero;     // Error 102: num <- lógico

    flot err103 = "texto";      // Error 103: flot <- texto
    flot err104 = falso;        // Error 104: flot <- lógico

    texto err105 = 50;          // Error 105: texto <- entero
    texto err106 = verdadero;   // Error 106: texto <- lógico

    logico err107 = "falso";    // Error 107: logico <- texto (Debe ser la palabra reservada falso, no cadena)
    logico err108 = 10;         // Error 108: logico <- numero

    // ==========================================================
    // ERROR 112: Operadores matemáticos (+, -, *, /) en variables de texto o lógicas
    // ==========================================================
    texto miCadena = "hola";
    miCadena += " mundo";

    // ==========================================================
    // ERROR 113: Iterador del bucle 'para' NO es numérico
    // ==========================================================
    para (miCadena = "a"; miCadena != "b"; miCadena++) {
        escribirLinea("Esto no deberia funcionar");
    }

    // ==========================================================
    // ERROR 125: Crisis de Identidad (Variable llamada como función)
    // ==========================================================
    num numeroEnganoso = 5;
    numeroEnganoso();

    // ==========================================================
    // ERROR 122: Cantidad de parámetros incorrecta en llamada a función
    // (sumarMatematica exige 2 parámetros, le mandamos 1)
    // ==========================================================
    sumarMatematica(10); 

    // ==========================================================
    // ERROR 126: Tipo de argumento incorrecto en llamada a función
    // (sumarMatematica exige 'num, num', le mandamos 'texto, logico')
    // ==========================================================
    sumarMatematica("diez", verdadero);

    retornar;
}