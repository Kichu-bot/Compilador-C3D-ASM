.MODEL SMALL
JUMPS
.STACK 100h

.DATA
    valor      DW ?
    mem0       DW ?
    mem1       DW ?
    mem2       DW ?
    mem3       DW ?
    mem4       DW ?
    i          DW ?
    t1         DW ?
    t2         DW ?
    j          DW ?
    t3         DW ?
    MSG00      DB '==============================', 0Dh, 0Ah, '$'
    MSG01      DB '   SIMULADOR DE ARREGLOS v1   ', 0Dh, 0Ah, '$'
    MSG02      DB 'Llenando el arreglo con un ciclo...', 0Dh, 0Ah, '$'
    MSG03      DB 'Arreglo llenado con exito.', 0Dh, 0Ah, '$'
    MSG04      DB 'Leyendo datos del arreglo...', 0Dh, 0Ah, '$'
    MSG05      DB '', 0Dh, 0Ah, '$'
    MSG06      DB 'Valor en el indice ', 0Dh, 0Ah, '$'
    MSG07      DB ': ', 0Dh, 0Ah, '$'
    NEWLINE_CHARS DB 0Dh, 0Ah, '$'
    PROMPT_SCAN   DB 'Ingrese un numero (y presione Enter): $'

.CODE
MAIN PROC
    MOV AX, @DATA
    MOV DS, AX
    MOV valor, 0
    MOV mem0, 0
    MOV mem1, 0
    MOV mem2, 0
    MOV mem3, 0
    MOV mem4, 0
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
    MOV i, 0
L1:
    MOV AX, i
    CMP AX, 4
    JLE L2
    JMP L4
L2:
    MOV AX, i
    MOV BX, 10
    MUL BX
    MOV t1, AX
    MOV AX, t1
    MOV valor, AX
    MOV AX, i
    CMP AX, 0
    JE L6
    MOV AX, i
    CMP AX, 1
    JE L7
    MOV AX, i
    CMP AX, 2
    JE L8
    MOV AX, i
    CMP AX, 3
    JE L9
    MOV AX, i
    CMP AX, 4
    JE L10
    JMP L5
L6:
    MOV AX, valor
    MOV mem0, AX
    JMP L5
L7:
    MOV AX, valor
    MOV mem1, AX
    JMP L5
L8:
    MOV AX, valor
    MOV mem2, AX
    JMP L5
L9:
    MOV AX, valor
    MOV mem3, AX
    JMP L5
L10:
    MOV AX, valor
    MOV mem4, AX
    JMP L5
L5:
L3:
    MOV AX, i
    MOV BX, 1
    ADD AX, BX
    MOV t2, AX
    MOV AX, t2
    MOV i, AX
    JMP L1
L4:
    LEA DX, MSG03
    MOV AH, 09h
    INT 21h
    LEA DX, MSG04
    MOV AH, 09h
    INT 21h
    LEA DX, MSG05
    MOV AH, 09h
    INT 21h
    MOV j, 0
L11:
    MOV AX, j
    CMP AX, 4
    JLE L12
    JMP L14
L12:
    MOV AX, j
    CMP AX, 0
    JE L16
    MOV AX, j
    CMP AX, 1
    JE L17
    MOV AX, j
    CMP AX, 2
    JE L18
    MOV AX, j
    CMP AX, 3
    JE L19
    MOV AX, j
    CMP AX, 4
    JE L20
    JMP L15
L16:
    MOV AX, mem0
    MOV valor, AX
    JMP L15
L17:
    MOV AX, mem1
    MOV valor, AX
    JMP L15
L18:
    MOV AX, mem2
    MOV valor, AX
    JMP L15
L19:
    MOV AX, mem3
    MOV valor, AX
    JMP L15
L20:
    MOV AX, mem4
    MOV valor, AX
    JMP L15
L15:
    LEA DX, MSG06
    MOV AH, 09h
    INT 21h
    MOV AX, j
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    LEA DX, MSG07
    MOV AH, 09h
    INT 21h
    MOV AX, valor
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
L13:
    MOV AX, j
    MOV BX, 1
    ADD AX, BX
    MOV t3, AX
    MOV AX, t3
    MOV j, AX
    JMP L11
L14:
    LEA DX, MSG00
    MOV AH, 09h
    INT 21h
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
