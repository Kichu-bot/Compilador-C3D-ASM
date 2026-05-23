.MODEL SMALL
JUMPS
.STACK 100h

.DATA
    numero     DW ?
    t1         DW ?
    divi_par   DW ?
    t2         DW ?
    mult_par   DW ?
    t3         DW ?
    residuo_par DW ?
    es_par     DW ?
    resultado_fact DW ?
    temp       DW ?
    t4         DW ?
    t5         DW ?
    es_primo   DW ?
    t6         DW ?
    limite     DW ?
    i          DW ?
    t7         DW ?
    divi_primo DW ?
    t8         DW ?
    mult_primo DW ?
    t9         DW ?
    residuo_val DW ?
    t10        DW ?
    opcion     DW ?
    n          DW ?
    t11        DW ?
    fact       DW ?
    t12        DW ?
    primo      DW ?
    t13        DW ?
    par        DW ?
    MSG00      DB '===================================', 0Dh, 0Ah, '$'
    MSG01      DB '   ANALIZADOR MATEMATICO  ', 0Dh, 0Ah, '$'
    MSG02      DB '1. Calcular Factorial', 0Dh, 0Ah, '$'
    MSG03      DB '2. Verificar si es Primo', 0Dh, 0Ah, '$'
    MSG04      DB '3. Saber si es Par o Impar', 0Dh, 0Ah, '$'
    MSG05      DB '0. Salir', 0Dh, 0Ah, '$'
    MSG06      DB 'Elija una opcion: ', 0Dh, 0Ah, '$'
    MSG07      DB 'Ingrese numero para factorial: ', 0Dh, 0Ah, '$'
    MSG08      DB 'El factorial es: ', 0Dh, 0Ah, '$'
    MSG09      DB 'Ingrese numero a verificar: ', 0Dh, 0Ah, '$'
    MSG10      DB '-> El numero SI es primo.', 0Dh, 0Ah, '$'
    MSG11      DB '-> El numero NO es primo.', 0Dh, 0Ah, '$'
    MSG12      DB 'Ingrese numero: ', 0Dh, 0Ah, '$'
    MSG13      DB '-> El numero es PAR.', 0Dh, 0Ah, '$'
    MSG14      DB '-> El numero es IMPAR.', 0Dh, 0Ah, '$'
    MSG15      DB 'Apagando sistema matricial. Adios!', 0Dh, 0Ah, '$'
    MSG16      DB '-> Opcion invalida. Intente de nuevo.', 0Dh, 0Ah, '$'
    NEWLINE_CHARS DB 0Dh, 0Ah, '$'
    PROMPT_SCAN   DB 'Ingrese un numero (y presione Enter): $'

.CODE
MAIN PROC
    MOV AX, @DATA
    MOV DS, AX
    JMP principal

verificarPar:
    POP DX  ; Extraer temporalmente el IP (Direccion de Retorno)
    POP AX  ; Extraer el parametro enviado en el CALL
    MOV numero, AX ; Guardarlo en la variable local
    PUSH DX ; Devolver el IP a la cima de la pila para el RET
    MOV AX, numero
    MOV BX, 2
    XOR DX, DX
    DIV BX
    MOV t1, AX
    MOV AX, t1
    MOV divi_par, AX
    MOV AX, divi_par
    MOV BX, 2
    MUL BX
    MOV t2, AX
    MOV AX, t2
    MOV mult_par, AX
    MOV AX, numero
    MOV BX, mult_par
    SUB AX, BX
    MOV t3, AX
    MOV AX, t3
    MOV residuo_par, AX
    MOV es_par, 0
    MOV AX, residuo_par
    CMP AX, 0
    JE L2
    JMP L3
L2:
    MOV es_par, 1
    JMP L1
L3:
L1:
    MOV AX, es_par
    RET
calcularFactorial:
    POP DX  ; Extraer temporalmente el IP (Direccion de Retorno)
    POP AX  ; Extraer el parametro enviado en el CALL
    MOV numero, AX ; Guardarlo en la variable local
    PUSH DX ; Devolver el IP a la cima de la pila para el RET
    MOV resultado_fact, 1
    MOV AX, numero
    MOV temp, AX
L4:
    MOV AX, temp
    CMP AX, 1
    JG L5
    JMP L6
L5:
    MOV AX, resultado_fact
    MOV BX, temp
    MUL BX
    MOV t4, AX
    MOV AX, t4
    MOV resultado_fact, AX
    MOV AX, temp
    MOV BX, 1
    SUB AX, BX
    MOV t5, AX
    MOV AX, t5
    MOV temp, AX
    JMP L4
L6:
    MOV AX, resultado_fact
    RET
verificarPrimo:
    POP DX  ; Extraer temporalmente el IP (Direccion de Retorno)
    POP AX  ; Extraer el parametro enviado en el CALL
    MOV numero, AX ; Guardarlo en la variable local
    PUSH DX ; Devolver el IP a la cima de la pila para el RET
    MOV es_primo, 1
    MOV AX, numero
    CMP AX, 1
    JLE L8
    JMP L9
L8:
    MOV es_primo, 0
    JMP L7
L9:
    MOV AX, numero
    MOV BX, 2
    XOR DX, DX
    DIV BX
    MOV t6, AX
    MOV AX, t6
    MOV limite, AX
    MOV i, 2
L10:
    MOV AX, i
    MOV BX, limite
    CMP AX, BX
    JLE L11
    JMP L13
L11:
    MOV AX, numero
    MOV BX, i
    XOR DX, DX
    DIV BX
    MOV t7, AX
    MOV AX, t7
    MOV divi_primo, AX
    MOV AX, divi_primo
    MOV BX, i
    MUL BX
    MOV t8, AX
    MOV AX, t8
    MOV mult_primo, AX
    MOV AX, numero
    MOV BX, mult_primo
    SUB AX, BX
    MOV t9, AX
    MOV AX, t9
    MOV residuo_val, AX
    MOV AX, residuo_val
    CMP AX, 0
    JE L15
    JMP L16
L15:
    MOV es_primo, 0
    JMP L14
L16:
L14:
L12:
    MOV AX, i
    MOV BX, 1
    ADD AX, BX
    MOV t10, AX
    MOV AX, t10
    MOV i, AX
    JMP L10
L13:
L7:
    MOV AX, es_primo
    RET
principal:
    MOV opcion, 1
    MOV n, 0
L17:
    MOV AX, opcion
    CMP AX, 0
    JNE L18
    JMP L19
L18:
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
    LEA DX, MSG00
    MOV AH, 09h
    INT 21h
    LEA DX, MSG06
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
    JE L21
    MOV AX, opcion
    CMP AX, 2
    JE L22
    MOV AX, opcion
    CMP AX, 3
    JE L23
    MOV AX, opcion
    CMP AX, 0
    JE L24
    JMP L25
L21:
    LEA DX, MSG07
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV n, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    PUSH n
    CALL calcularFactorial
    MOV t11, AX
    MOV AX, t11
    MOV fact, AX
    LEA DX, MSG08
    MOV AH, 09h
    INT 21h
    MOV AX, fact
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    JMP L20
L22:
    LEA DX, MSG09
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV n, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    PUSH n
    CALL verificarPrimo
    MOV t12, AX
    MOV AX, t12
    MOV primo, AX
    MOV AX, primo
    CMP AX, 1
    JE L27
    JMP L28
L27:
    LEA DX, MSG10
    MOV AH, 09h
    INT 21h
    JMP L26
L28:
    LEA DX, MSG11
    MOV AH, 09h
    INT 21h
L26:
    JMP L20
L23:
    LEA DX, MSG12
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV n, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    PUSH n
    CALL verificarPar
    MOV t13, AX
    MOV AX, t13
    MOV par, AX
    MOV AX, par
    CMP AX, 1
    JE L30
    JMP L31
L30:
    LEA DX, MSG13
    MOV AH, 09h
    INT 21h
    JMP L29
L31:
    LEA DX, MSG14
    MOV AH, 09h
    INT 21h
L29:
    JMP L20
L24:
    LEA DX, MSG15
    MOV AH, 09h
    INT 21h
    JMP L20
L25:
    LEA DX, MSG16
    MOV AH, 09h
    INT 21h
    JMP L20
L20:
    JMP L17
L19:
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
