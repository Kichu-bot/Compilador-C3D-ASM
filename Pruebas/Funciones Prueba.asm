.MODEL SMALL
JUMPS
.STACK 100h

.DATA
    t1         DW ?
    a          DW ?
    b          DW ?
    resultado  DW ?
    x          DW ?
    y          DW ?
    t2         DW ?
    total      DW ?
    i          DW ?
    t3         DW ?
    MSG00      DB 'Iniciando programa de prueba...', 0Dh, 0Ah, '$'
    MSG01      DB 'El total es: ', 0Dh, 0Ah, '$'
    MSG02      DB 'El total es mayor a 10', 0Dh, 0Ah, '$'
    MSG03      DB 'El total es 10 o menor', 0Dh, 0Ah, '$'
    NEWLINE_CHARS DB 0Dh, 0Ah, '$'
    PROMPT_SCAN   DB 'Ingrese un numero (y presione Enter): $'

.CODE
MAIN PROC
    MOV AX, @DATA
    MOV DS, AX
    JMP principal

saludar:
    LEA DX, MSG00
    MOV AH, 09h
    INT 21h
sumar:
    MOV AX, a
    MOV BX, b
    ADD AX, BX
    MOV t1, AX
    MOV AX, t1
    MOV resultado, AX
    MOV AX, resultado
    RET
principal:
    CALL saludar
    MOV x, 10
    MOV y, 5
    ; param x
    ; param y
    CALL sumar
    MOV t2, AX
    MOV AX, t2
    MOV total, AX
    LEA DX, MSG01
    MOV AH, 09h
    INT 21h
    MOV AX, total
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    MOV AX, total
    CMP AX, 10
    JG L2
    JMP L3
L2:
    LEA DX, MSG02
    MOV AH, 09h
    INT 21h
    JMP L1
L3:
    LEA DX, MSG03
    MOV AH, 09h
    INT 21h
L1:
    MOV i, 0
L4:
    MOV AX, i
    CMP AX, 3
    JL L5
    JMP L7
L5:
    MOV AX, i
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
L6:
    MOV AX, i
    MOV BX, 1
    ADD AX, BX
    MOV t3, AX
    MOV AX, t3
    MOV i, AX
    JMP L4
L7:
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
