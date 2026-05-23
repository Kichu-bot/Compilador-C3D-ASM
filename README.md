# [Compiladore] - Compilador Autónomo x86

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Assembly](https://img.shields.io/badge/Assembly_x86-000000?style=for-the-badge&logo=assembly&logoColor=white)
![Status](https://img.shields.io/badge/Status-Completado-success?style=for-the-badge)

Un compilador completo desarrollado desde cero en Java. Este proyecto abarca todo el ciclo de vida de compilación: desde la lectura del código fuente personalizado,
pasando por un riguroso análisis sintáctico y semántico, hasta la generación de código intermedio (.3D) optimizado y su traducción final a código de máquina (Ensamblador x86)
ejecutable.

---

## Características Principales

### 1. Front-End (Análisis)
* **Analizador Léxico:** Reconocimiento de tokens, palabras reservadas, operadores y control de errores léxicos.
* **Analizador Sintáctico Robusto:** Implementación de recuperación de errores Bottom-Up (Panic Mode). El compilador aísla instrucciones mal formadas (ej. falta de `;` o de
    llaves) sin detener abruptamente el proceso, reportando la línea exacta del fallo.
* **Analizador Semántico Estricto:** * Sistema de tipado fuerte (prevención de pérdida de precisión e incompatibilidad de tipos).
  * Control de ámbito (scope) y prevención de variables duplicadas o no declaradas.
  * Validación de firmas de funciones (cantidad y tipo de parámetros, obligatoriedad de retorno).
  * Detección de código inalcanzable (Dead Code) e instrucciones huérfanas.

### 2. Middle-End (Código Intermedio y Optimización)
* **Generación C3D:** Traducción de estructuras de alto nivel (ciclos, selecciones múltiples, anidamientos) a Código de 3 Direcciones.

### 3. Back-End (Generación de ASM)
* **Traducción x86:** Conversión autónoma de archivos `.3D` a lenguaje Ensamblador (`.asm`).
* **Gestión de Memoria Dinámica:** Uso real de la Pila de Ejecución (`PUSH` / `POP`) para el paso de parámetros en llamadas a funciones, emulando la arquitectura de compiladores modernos.

---

## Requisitos del Sistema

* **Java JDK:** 24 o superior.
* **IDE Recomendado:** Apache NetBeans / IntelliJ IDEA.
* **Emulador ASM:** [emu8086]([https://emu8086-microprocessor-emulator.en.softonic.com/](https://sourceforge.net/projects/dosbox/)) para ejecutar los archivos de salida.
* **Visual Studio MASM/TASM [https://marketplace.visualstudio.com/items?itemName=xsro.masm-tasm]

---

## Instalación y Uso

1. Clona este repositorio:
   ```bash
   git clone https://github.com/Kichu-bot/Compilador-C3D-ASM.git
   
2. Abre el proyecto en tu IDE (NetBeans/IntelliJ) y compílalo.

3. Ejecuta la aplicación principal.

4. Carga un archivo de prueba con extensión .re (puedes encontrar ejemplos en la carpeta /pruebas).

5. Observa la consola de salida para el análisis.

6. Utiliza el botón de Compilacio para generar el archivo ejecutable final.

---

## Capturas de Pantalla

### 1. Compilación Exitosa
Demostración del compilador procesando una prueba sin errores.
![Compilación Exitosa](screenshots/Interfaz.png)

### 2. Recuperación de Errores
El analizador sintáctico y semántico atrapando errores en tiempo real sin detener la ejecución del IDE.
![Manejo de Errores](screenshots/Errores.png)

### 3. Generacion C3D
El conversor de codigo intermedio generando el archivo 3D.
![Generacion de C3D](screenshots/C3D.png)

### 4. Ejecucion ASM
Probando La Ejecucion del ASM generado por nuestro conversor C3D a ASM.
![Ejecucion de ASM](screenshots/Ejecucion.png)
