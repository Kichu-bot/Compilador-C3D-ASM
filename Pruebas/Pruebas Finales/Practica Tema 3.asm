.MODEL SMALL
JUMPS
.STACK 100h

.DATA
    numero     DW ?
    resultado_fact DW ?
    temp       DW ?
    t1         DW ?
    t2         DW ?
    opcion     DW ?
    valor1     DW ?
    valor2     DW ?
    stop       DW ?
    resultado  DW ?
    t3         DW ?
    t4         DW ?
    i          DW ?
    t5         DW ?
    t6         DW ?
    t7         DW ?
    MSG00      DB '==============================', 0Dh, 0Ah, '$'
    MSG01      DB '   CALCULADORA MULTIPROPOSITO ', 0Dh, 0Ah, '$'
    MSG02      DB '1. Operacion Simple (Suma)', 0Dh, 0Ah, '$'
    MSG03      DB '2. Operacion Compleja (Division Segura)', 0Dh, 0Ah, '$'
    MSG04      DB '3. Ciclo Anidado (Tabla de Multiplicar)', 0Dh, 0Ah, '$'
    MSG05      DB '4. Operacion con Funcion (Factorial)', 0Dh, 0Ah, '$'
    MSG06      DB '5. Salir', 0Dh, 0Ah, '$'
    MSG07      DB 'Seleccione una opcion: ', 0Dh, 0Ah, '$'
    MSG08      DB 'Ingrese primer numero: ', 0Dh, 0Ah, '$'
    MSG09      DB 'Ingrese segundo numero: ', 0Dh, 0Ah, '$'
    MSG10      DB 'El resultado de la suma es: ', 0Dh, 0Ah, '$'
    MSG11      DB 'Ingrese el dividendo: ', 0Dh, 0Ah, '$'
    MSG12      DB 'Ingrese el divisor (No puede ser cero): ', 0Dh, 0Ah, '$'
    MSG13      DB '-> ERROR: No se puede dividir entre cero.', 0Dh, 0Ah, '$'
    MSG14      DB 'El resultado de la division es: ', 0Dh, 0Ah, '$'
    MSG15      DB 'Ingrese un numero para ver su tabla: ', 0Dh, 0Ah, '$'
    MSG16      DB 'Generando tabla...', 0Dh, 0Ah, '$'
    MSG17      DB ' x ', 0Dh, 0Ah, '$'
    MSG18      DB ' = ', 0Dh, 0Ah, '$'
    MSG19      DB 'Ingrese un numero para calcular su factorial: ', 0Dh, 0Ah, '$'
    MSG20      DB 'El factorial es: ', 0Dh, 0Ah, '$'
    MSG21      DB 'Saliendo de la calculadora...', 0Dh, 0Ah, '$'
    MSG22      DB '-> ERROR: Opcion invalida. Intente de nuevo.', 0Dh, 0Ah, '$'
    MSG23      DB '', 0Dh, 0Ah, '$'
    NEWLINE_CHARS DB 0Dh, 0Ah, '$'
    PROMPT_SCAN   DB 'Ingrese un numero (y presione Enter): $'

.CODE
MAIN PROC
    MOV AX, @DATA
    MOV DS, AX
    JMP principal

calcularFactorial:
    POP DX  ; Extraer temporalmente el IP (Direccion de Retorno)
    POP AX  ; Extraer el parametro enviado en el CALL
    MOV numero, AX ; Guardarlo en la variable local
    PUSH DX ; Devolver el IP a la cima de la pila para el RET
    MOV resultado_fact, 1
    MOV AX, numero
    MOV temp, AX
L1:
    MOV AX, temp
    CMP AX, 1
    JG L2
    JMP L3
L2:
    MOV AX, resultado_fact
    MOV BX, temp
    MUL BX
    MOV t1, AX
    MOV AX, t1
    MOV resultado_fact, AX
    MOV AX, temp
    MOV BX, 1
    SUB AX, BX
    MOV t2, AX
    MOV AX, t2
    MOV temp, AX
    JMP L1
L3:
    MOV AX, resultado_fact
    RET
principal:
    MOV opcion, 0
    MOV valor1, 0
    MOV valor2, 0
    MOV stop, 0
    MOV resultado, 0
L4:
    MOV AX, opcion
    CMP AX, 5
    JNE L5
    JMP L6
L5:
    LEA DX, MSG00
    MOV AH, 09h
    INT 21h
    LEA DX, MSG01
    MOV AH, 09h
    INT 21h
    LEA DX, MSG00
    MOV AH, 09h
    INT 21h
    LEA DX, MSG02
    MOV AH, 09h
    INT 21h
    LEA DX, MSG03
    MOV AH, 09h
    INT 21h
    LEA DX, MSG04
    MOV AH, 09h
    INT 21h
    LEA DX, MSG05
    MOV AH, 09h
    INT 21h
    LEA DX, MSG06
    MOV AH, 09h
    INT 21h
    LEA DX, MSG00
    MOV AH, 09h
    INT 21h
    LEA DX, MSG07
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV opcion, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    MOV AX, opcion
    CMP AX, 1
    JE L8
    MOV AX, opcion
    CMP AX, 2
    JE L9
    MOV AX, opcion
    CMP AX, 3
    JE L10
    MOV AX, opcion
    CMP AX, 4
    JE L11
    MOV AX, opcion
    CMP AX, 5
    JE L12
    JMP L13
L8:
    LEA DX, MSG08
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV valor1, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    LEA DX, MSG09
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV valor2, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    MOV AX, valor1
    MOV BX, valor2
    ADD AX, BX
    MOV t3, AX
    MOV AX, t3
    MOV resultado, AX
    LEA DX, MSG10
    MOV AH, 09h
    INT 21h
    MOV AX, resultado
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    JMP L7
L9:
    LEA DX, MSG11
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV valor1, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    LEA DX, MSG12
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV valor2, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    MOV AX, valor2
    CMP AX, 0
    JE L15
    JMP L16
L15:
    LEA DX, MSG13
    MOV AH, 09h
    INT 21h
    JMP L14
L16:
    MOV AX, valor1
    MOV BX, valor2
    XOR DX, DX
    DIV BX
    MOV t4, AX
    MOV AX, t4
    MOV resultado, AX
    LEA DX, MSG14
    MOV AH, 09h
    INT 21h
    MOV AX, resultado
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
L14:
    JMP L7
L10:
    LEA DX, MSG15
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV valor1, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    LEA DX, MSG16
    MOV AH, 09h
    INT 21h
    MOV i, 1
L17:
    MOV AX, i
    CMP AX, 10
    JLE L18
    JMP L20
L18:
    MOV AX, valor1
    MOV BX, i
    MUL BX
    MOV t5, AX
    MOV AX, t5
    MOV resultado, AX
    MOV AX, valor1
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    LEA DX, MSG17
    MOV AH, 09h
    INT 21h
    MOV AX, i
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    LEA DX, MSG18
    MOV AH, 09h
    INT 21h
    MOV AX, resultado
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
L19:
    MOV AX, i
    MOV BX, 1
    ADD AX, BX
    MOV t6, AX
    MOV AX, t6
    MOV i, AX
    JMP L17
L20:
    JMP L7
L11:
    LEA DX, MSG19
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV valor1, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    PUSH valor1
    CALL calcularFactorial
    MOV t7, AX
    MOV AX, t7
    MOV resultado, AX
    LEA DX, MSG20
    MOV AH, 09h
    INT 21h
    MOV AX, resultado
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    JMP L7
L12:
    LEA DX, MSG21
    MOV AH, 09h
    INT 21h
    JMP L7
L13:
    LEA DX, MSG22
    MOV AH, 09h
    INT 21h
    JMP L7
L7:
    LEA DX, MSG23
    MOV AH, 09h
    INT 21h
    JMP L4
L6:
    MOV AH, 4Ch
    INT 21h
MAIN ENDP

PRINT_NUM PROC
    PUSH AX
    PUSH BX
    PUSH CX
    PUSH DX
    CMP AX, 0
    JNS @@PRINT_NUM_POSITIVE
    PUSH AX
    MOV DL, '-'
    MOV AH, 02h
    INT 21h
    POP AX
    NEG AX
@@PRINT_NUM_POSITIVE:
    XOR CX, CX
    MOV BX, 10
@@PRINT_NUM_LOOP1:
    XOR DX, DX
    DIV BX
    PUSH DX
    INC CX
    CMP AX, 0
    JNE @@PRINT_NUM_LOOP1
@@PRINT_NUM_LOOP2:
    POP DX
    ADD DL, '0'
    MOV AH, 02h
    INT 21h
    LOOP @@PRINT_NUM_LOOP2
    POP DX
    POP CX
    POP BX
    POP AX
    RET
PRINT_NUM ENDP

PRINT_NUM_FIXED_POINT PROC
    PUSH AX
    PUSH BX
    PUSH CX
    PUSH DX
    CMP AX, 0
    JGE @@PF_POSITIVE
    PUSH AX
    MOV DL, '-'
    MOV AH, 02h
    INT 21h
    POP AX
    NEG AX
@@PF_POSITIVE:
    MOV BX, 100
    XOR DX, DX
    DIV BX
    PUSH DX
    CALL PRINT_NUM
    MOV DL, '.'
    MOV AH, 02h
    INT 21h
    POP AX
    MOV BL, 10
    XOR DX, DX
    DIV BL
    PUSH AX
    MOV DL, AL
    ADD DL, '0'
    MOV AH, 02h
    INT 21h
    POP AX
    MOV DL, AH
    ADD DL, '0'
    MOV AH, 02h
    INT 21h
    POP DX
    POP CX
    POP BX
    POP AX
    RET
PRINT_NUM_FIXED_POINT ENDP

SCAN_NUM_LOCAL PROC
    PUSH BX
    PUSH CX
    PUSH DX
    PUSH SI
    PUSH DI
    XOR BX, BX
    XOR CX, CX
    MOV SI, 0
    MOV AH, 01h
    INT 21h
    CMP AL, '-'
    JNE @@SL_CHECK_PLUS_SCAN
    MOV CX, 1
    MOV AH, 01h
    INT 21h
    JMP @@SL_CONVERSION_LOOP_SCAN
@@SL_CHECK_PLUS_SCAN:
    CMP AL, '+'
    JNE @@SL_CONVERSION_LOOP_SCAN
    MOV AH, 01h
    INT 21h
@@SL_CONVERSION_LOOP_SCAN:
    CMP AL, 0DH
    JE @@SL_FINALIZE_NUMBER_SCAN
    CMP AL, '0'
    JL @@SL_INVALID_CHAR_SCAN
    CMP AL, '9'
    JG @@SL_INVALID_CHAR_SCAN
    INC SI
    SUB AL, '0'
    MOV AH, 0
    PUSH AX
    MOV AX, BX
    MOV DI, 10
    MUL DI
    CMP DX, 0
    JNE @@SL_HANDLE_OVERFLOW_SCAN
    MOV BX, AX
    POP AX
    ADD BX, AX
    JC @@SL_HANDLE_OVERFLOW_SCAN
    MOV AH, 01h
    INT 21h
    JMP @@SL_CONVERSION_LOOP_SCAN
@@SL_INVALID_CHAR_SCAN:
    MOV AH, 02h
    MOV DL, 0Dh
    INT 21h
    MOV DL, 0Ah
    INT 21h
@@SL_FINALIZE_NUMBER_SCAN:
    CMP SI, 0
    JNE @@SL_APPLY_SIGN_SCAN
    XOR BX, BX
@@SL_APPLY_SIGN_SCAN:
    CMP CX, 1
    JNE @@SL_MOVE_TO_AX_SCAN
    NEG BX
@@SL_MOVE_TO_AX_SCAN:
    MOV AX, BX
    JMP @@SL_EXIT_SCAN
@@SL_HANDLE_OVERFLOW_SCAN:
    MOV AX, 0
@@SL_EXIT_SCAN:
    POP DI
    POP SI
    POP DX
    POP CX
    POP BX
    RET
SCAN_NUM_LOCAL ENDP

END MAIN
