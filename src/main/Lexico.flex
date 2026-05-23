// Parte 1: Package and imports, copiados a Scanner.java

package main;

import java_cup.runtime.*; // Aquí viene el tipo Symbol
import compilerTools.Token;

%% // Parte 2: Options y macros (se definen cosas para usar en la siguiente sección de reglas)

%class Scanner
%type Token
%line
// %cup
%ignorecase // Solo para no distinguir entre mayúsculas y minúsculas
%column
%state STRING

// Código copiado a Scanner.java
%{
  private Token token(String lexema, String componenteLexico, int line, int column) {
    return new Token(lexema, componenteLexico, line + 1, column + 1);
  }
%}

    // Macros (regex)

    //  Un salto de línea es un \r, \n o \r\n dependiendo del SO  
    TerminadorLinea = \r|\n|\r\n
    Espacio     = {TerminadorLinea} | [ \t\f]
    Digitos = [0-9] // Digitos de mi compilador
    Letra   = [a-zA-Z] // Es bueno tener una macro para letra también por claridad

    Caracter = [^\r\n] //Todo lo que no sea \r\n
    Comentario = {ComentarioBloque} | {ComentarioLinea} | {ComentarioDocumentar}
    ContenidoComentario       = ( [^*] | \*+ [^/*] )* /* lo que puede contener un comentario */

    ComentarioBloque   = "/*" [^*] ~"*/" | "/*" "*"+ "/" 
    ComentarioLinea     = "//" {Caracter}* {TerminadorLinea}?
    ComentarioDocumentar = "/**" {ContenidoComentario} "*"+ "/"

    NumeroEntero = ([+|-][1-9]{Digitos}*) | ({Digitos}+)
    PuntoFlotante = ([+|-]?{Digitos}+"."{Digitos}+) | ([+|-]?"."{Digitos}+)
    Identificador = ({Letra}|"_")({Letra}|{Digitos}|"_")*
    Cadena = "\""{Caracter}*"\""


%% // Parte 3: Reglas/keywords, usando las definiciones anteriores

  // ************************ Palabras reservadas ************************
      
      // Palabras reservadas atómicas
      // "var" {return token (yytext(),"VAR",yyline,yycolumn);}
      "inicio" {return token (yytext(),"INICIO",yyline,yycolumn);} // Corregido de NULO a INICIO según el segundo código
      "retornar" {return token (yytext(),"RETORNAR",yyline,yycolumn);}
      "verdadero" {return token (yytext(),"VERDADERO",yyline,yycolumn);}
      "falso" {return token (yytext(),"FALSO",yyline,yycolumn);}

      // Tipos de datos
      "num" {return token (yytext(),"NUM",yyline,yycolumn);}
      "flot" {return token(yytext(),"FLOT",yyline,yycolumn);}
      "texto" {return token (yytext(),"TEXTO",yyline,yycolumn);}
      // "letra" {return token (yytext(),"LETRA",yyline,yycolumn);}
      "logico" {return token (yytext(),"LOGICO",yyline,yycolumn);}
      // "dato" {return token (yytext(),"DATO",yyline,yycolumn);}

      // Instrucciones de Entrada / Salida
      "escribir" {return token (yytext(),"ESCRIBIR",yyline,yycolumn);}
      "escribirLinea" {return token (yytext(),"ESCRIBIR_LINEA",yyline,yycolumn);}
      "leer" {return token (yytext(),"LEER",yyline,yycolumn);}

      // Sentencias condicionales, selectivas e iterativas
      "si" {return token(yytext(),"SI",yyline,yycolumn);}
      "sino" {return token(yytext(),"SINO",yyline,yycolumn);}
      "final" {return token(yytext(),"FINAL",yyline,yycolumn);}
      "elegir" {return token(yytext(),"ELEGIR",yyline,yycolumn);}
      "caso" {return token(yytext(),"CASO",yyline,yycolumn);}
      "defecto" {return token(yytext(),"DEFECTO",yyline,yycolumn);}
      "salir" {return token(yytext(),"SALIR",yyline,yycolumn);}
      "mientras" {return token(yytext(),"MIENTRAS",yyline,yycolumn);}
      "para" {return token(yytext(),"PARA",yyline,yycolumn);}

// ************************ Fin palabras reservadas ************************

  // Signos de puntuación
  "," {return token(yytext(),"COMA",yyline,yycolumn);}
  ":" {return token(yytext(),"DOSPUNTOS",yyline,yycolumn);}
  ";" {return token(yytext(),"FIN_LINEA",yyline,yycolumn);} // Alineado con FinLinea del segundo código

  // Signos de agrupación
  "(" {return token(yytext(),"PARENTESIS_APERTURA",yyline,yycolumn);}
  ")" {return token(yytext(),"PARENTESIS_CIERRE",yyline,yycolumn);}
  "[" {return token(yytext(),"CORCHETE_APERTURA",yyline,yycolumn);}
  "]" {return token(yytext(),"CORCHETE_CIERRE",yyline,yycolumn);}
  "{" {return token(yytext(),"LLAVE_APERTURA",yyline,yycolumn);}
  "}" {return token(yytext(),"LLAVE_CIERRE",yyline,yycolumn);}

  // Operadores relacionales
  "<" {return token(yytext(),"MENOR_QUE",yyline,yycolumn);}
  "<=" {return token(yytext(),"MENOR_IGUAL",yyline,yycolumn);}
  "==" {return token(yytext(),"IGUAL_A",yyline,yycolumn);}
  ">=" {return token(yytext(),"MAYOR_IGUAL",yyline,yycolumn);}
  ">" {return token(yytext(),"MAYOR_QUE",yyline,yycolumn);}
  "!=" {return token(yytext(),"DIFERENTE_DE",yyline,yycolumn);}

  // Operadores lógicos
  "yy" {return token(yytext(),"Y",yyline,yycolumn);}
  "oo" {return token(yytext(),"O",yyline,yycolumn);}
  "!" {return token(yytext(),"NO",yyline,yycolumn);}

  // Operadores aritméticos
  "+" {return token(yytext(),"SUMA",yyline,yycolumn);}
  "-" {return token(yytext(),"RESTA",yyline,yycolumn);}
  "*" {return token(yytext(),"MULTIPLICACION",yyline,yycolumn);}
  "/" {return token(yytext(),"DIVISION",yyline,yycolumn);}
  "%" {return token(yytext(),"MODULO",yyline,yycolumn);}
  
  // Operadores de asignación
  "=" {return token(yytext(),"OPERADORASIGNACION",yyline,yycolumn);}
  "+=" {return token(yytext(),"ASIGNACIONSUMA",yyline,yycolumn);}
  "-=" {return token(yytext(),"ASIGNACIONRESTA",yyline,yycolumn);}
  "*=" {return token(yytext(),"ASIGNACIONMULTIPLICACION",yyline,yycolumn);}
  "/=" {return token(yytext(),"ASIGNACIONDIVISION",yyline,yycolumn);}

  // Operadores unarios
  "++" {return token(yytext(),"INCREMENTO",yyline,yycolumn);}
  "--" {return token(yytext(),"DECREMENTO",yyline,yycolumn);}

  // Macros y Expresiones Regulares
  {PuntoFlotante} {return token(yytext(),"NUMERODECIMAL",yyline,yycolumn);}
  {NumeroEntero} {return token(yytext(),"NUMEROENTERO",yyline,yycolumn);}
  {Identificador} {return token(yytext(),"IDENTIFICADOR",yyline,yycolumn);}
  {Cadena} {return token(yytext(),"CADENA",yyline,yycolumn);}

  // Ya estaban en el codigo
  {Comentario}     {/* Ignorar */} // los comentarios solo se ignorarán
  {Espacio}  {/* Ignorar */} // el espacio en blanco solo se ignorará

  // Cualquier otra cosa
  .      {return token(yytext(),"ERROR",yyline,yycolumn);}