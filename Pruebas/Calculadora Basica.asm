.MODEL SMALL
JUMPS
.STACK 100h

.DATA
    opcion     DW ?
    num1       DW ?
    num2       DW ?
    resultado  DW ?
    t1         DW ?
    t2         DW ?
    MSG00      DB '==============================', 0Dh, 0Ah, '$'
    MSG01      DB '    CALCULADORA OPTIMIZADA    ', 0Dh, 0Ah, '$'
    MSG02      DB '1. Sumar', 0Dh, 0Ah, '$'
    MSG03      DB '2. Restar', 0Dh, 0Ah, '$'
    MSG04      DB '0. Salir del programa', 0Dh, 0Ah, '$'
    MSG05      DB 'Elija una opcion: ', 0Dh, 0Ah, '$'
    MSG06      DB 'Ingrese el primer numero: ', 0Dh, 0Ah, '$'
    MSG07      DB 'Ingrese el segundo numero: ', 0Dh, 0Ah, '$'
    MSG08      DB '-> La suma es: ', 0Dh, 0Ah, '$'
    MSG09      DB '-> La resta es: ', 0Dh, 0Ah, '$'
    MSG10      DB 'Saliendo de la calculadora. Adios!', 0Dh, 0Ah, '$'
    MSG11      DB '-> ERROR: Opcion no valida.', 0Dh, 0Ah, '$'
    MSG12      DB '', 0Dh, 0Ah, '$'
    NEWLINE_CHARS DB 0Dh, 0Ah, '$'
    PROMPT_SCAN   DB 'Ingrese un numero (y presione Enter): $'

.CODE
MAIN PROC
    MOV AX, @DATA
    MOV DS, AX
    MOV opcion, 1
    MOV num1, 0
    MOV num2, 0
    MOV resultado, 0
L1:
    MOV AX, opcion
    CMP AX, 0
    JG L2
    JMP L3
L2:
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
    LEA DX, MSG00
    MOV AH, 09h
    INT 21h
    LEA DX, MSG05
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
    JE L5
    MOV AX, opcion
    CMP AX, 2
    JE L6
    MOV AX, opcion
    CMP AX, 0
    JE L7
    JMP L8
L5:
    LEA DX, MSG06
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV num1, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    LEA DX, MSG07
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV num2, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    MOV AX, num1
    MOV BX, num2
    ADD AX, BX
    MOV t1, AX
    MOV AX, t1
    MOV resultado, AX
    LEA DX, MSG08
    MOV AH, 09h
    INT 21h
    MOV AX, resultado
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    JMP L4
L6:
    LEA DX, MSG06
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV num1, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    LEA DX, MSG07
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV num2, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    MOV AX, num1
    MOV BX, num2
    SUB AX, BX
    MOV t2, AX
    MOV AX, t2
    MOV resultado, AX
    LEA DX, MSG09
    MOV AH, 09h
    INT 21h
    MOV AX, resultado
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    JMP L4
L7:
    LEA DX, MSG10
    MOV AH, 09h
    INT 21h
    JMP L4
L8:
    LEA DX, MSG11
    MOV AH, 09h
    INT 21h
    JMP L4
L4:
    LEA DX, MSG12
    MOV AH, 09h
    INT 21h
    JMP L1
L3:
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
