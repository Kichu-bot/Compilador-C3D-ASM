.MODEL SMALL
JUMPS
.STACK 100h

.DATA
    opcion     DW ?
    tope       DW ?
    valor      DW ?
    pila1      DW ?
    pila2      DW ?
    pila3      DW ?
    t1         DW ?
    t2         DW ?
    MSG00      DB '==============================', 0Dh, 0Ah, '$'
    MSG01      DB '       PILA (STACK) v1.0      ', 0Dh, 0Ah, '$'
    MSG02      DB '1. Push (Apilar)', 0Dh, 0Ah, '$'
    MSG03      DB '2. Pop (Desapilar)', 0Dh, 0Ah, '$'
    MSG04      DB '3. Mostrar Pila', 0Dh, 0Ah, '$'
    MSG05      DB '0. Salir', 0Dh, 0Ah, '$'
    MSG06      DB 'Elija una opcion: ', 0Dh, 0Ah, '$'
    MSG07      DB '-> ERROR: Pila LLENA (Overflow)', 0Dh, 0Ah, '$'
    MSG08      DB 'Ingrese el numero a apilar: ', 0Dh, 0Ah, '$'
    MSG09      DB '-> Valor apilado correctamente.', 0Dh, 0Ah, '$'
    MSG10      DB '-> ERROR: Pila VACIA (Underflow)', 0Dh, 0Ah, '$'
    MSG11      DB '-> Valor desapilado: ', 0Dh, 0Ah, '$'
    MSG12      DB '--- ESTADO ACTUAL DE LA PILA ---', 0Dh, 0Ah, '$'
    MSG13      DB '[ Pila Vacia ]', 0Dh, 0Ah, '$'
    MSG14      DB 'Posicion 3: ', 0Dh, 0Ah, '$'
    MSG15      DB 'Posicion 2: ', 0Dh, 0Ah, '$'
    MSG16      DB 'Posicion 1: ', 0Dh, 0Ah, '$'
    MSG17      DB '--------------------------------', 0Dh, 0Ah, '$'
    MSG18      DB 'Apagando sistema...', 0Dh, 0Ah, '$'
    MSG19      DB '-> ERROR: Opcion invalida.', 0Dh, 0Ah, '$'
    MSG20      DB '', 0Dh, 0Ah, '$'
    NEWLINE_CHARS DB 0Dh, 0Ah, '$'
    PROMPT_SCAN   DB 'Ingrese un numero (y presione Enter): $'

.CODE
MAIN PROC
    MOV AX, @DATA
    MOV DS, AX
    MOV opcion, 1
    MOV tope, 0
    MOV valor, 0
    MOV pila1, 0
    MOV pila2, 0
    MOV pila3, 0
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
    JE L5
    MOV AX, opcion
    CMP AX, 2
    JE L6
    MOV AX, opcion
    CMP AX, 3
    JE L7
    MOV AX, opcion
    CMP AX, 0
    JE L8
    JMP L9
L5:
    MOV AX, tope
    CMP AX, 3
    JE L11
    JMP L12
L11:
    LEA DX, MSG07
    MOV AH, 09h
    INT 21h
    JMP L10
L12:
    LEA DX, MSG08
    MOV AH, 09h
    INT 21h
    LEA DX, PROMPT_SCAN
    MOV AH, 09h
    INT 21h
    CALL SCAN_NUM_LOCAL
    MOV valor, AX
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    MOV AX, tope
    MOV BX, 1
    ADD AX, BX
    MOV t1, AX
    MOV AX, t1
    MOV tope, AX
    MOV AX, tope
    CMP AX, 1
    JE L14
    MOV AX, tope
    CMP AX, 2
    JE L15
    MOV AX, tope
    CMP AX, 3
    JE L16
    JMP L13
L14:
    MOV AX, valor
    MOV pila1, AX
    JMP L13
L15:
    MOV AX, valor
    MOV pila2, AX
    JMP L13
L16:
    MOV AX, valor
    MOV pila3, AX
    JMP L13
L13:
    LEA DX, MSG09
    MOV AH, 09h
    INT 21h
L10:
    JMP L4
L6:
    MOV AX, tope
    CMP AX, 0
    JE L18
    JMP L19
L18:
    LEA DX, MSG10
    MOV AH, 09h
    INT 21h
    JMP L17
L19:
    MOV AX, tope
    CMP AX, 3
    JE L21
    MOV AX, tope
    CMP AX, 2
    JE L22
    MOV AX, tope
    CMP AX, 1
    JE L23
    JMP L20
L21:
    MOV AX, pila3
    MOV valor, AX
    MOV pila3, 0
    JMP L20
L22:
    MOV AX, pila2
    MOV valor, AX
    MOV pila2, 0
    JMP L20
L23:
    MOV AX, pila1
    MOV valor, AX
    MOV pila1, 0
    JMP L20
L20:
    MOV AX, tope
    MOV BX, 1
    SUB AX, BX
    MOV t2, AX
    MOV AX, t2
    MOV tope, AX
    LEA DX, MSG11
    MOV AH, 09h
    INT 21h
    MOV AX, valor
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
L17:
    JMP L4
L7:
    LEA DX, MSG12
    MOV AH, 09h
    INT 21h
    MOV AX, tope
    CMP AX, 0
    JE L25
    JMP L26
L25:
    LEA DX, MSG13
    MOV AH, 09h
    INT 21h
    JMP L24
L26:
    MOV AX, tope
    CMP AX, 3
    JGE L28
    JMP L29
L28:
    LEA DX, MSG14
    MOV AH, 09h
    INT 21h
    MOV AX, pila3
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    JMP L27
L29:
    MOV AX, tope
    CMP AX, 2
    JGE L30
    JMP L31
L30:
    LEA DX, MSG15
    MOV AH, 09h
    INT 21h
    MOV AX, pila2
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    JMP L27
L31:
    MOV AX, tope
    CMP AX, 1
    JGE L32
    JMP L33
L32:
    LEA DX, MSG16
    MOV AH, 09h
    INT 21h
    MOV AX, pila1
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
    JMP L27
L33:
L27:
L24:
    LEA DX, MSG17
    MOV AH, 09h
    INT 21h
    JMP L4
L8:
    LEA DX, MSG18
    MOV AH, 09h
    INT 21h
    JMP L4
L9:
    LEA DX, MSG19
    MOV AH, 09h
    INT 21h
    JMP L4
L4:
    LEA DX, MSG20
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
