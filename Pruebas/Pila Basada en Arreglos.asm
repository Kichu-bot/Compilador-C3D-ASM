.MODEL SMALL
JUMPS
.STACK 100h

.DATA
    opcion     DW ?
    tope       DW ?
    valor      DW ?
    indice     DW ?
    mem0       DW ?
    mem1       DW ?
    mem2       DW ?
    mem3       DW ?
    mem4       DW ?
    t1         DW ?
    t2         DW ?
    t3         DW ?
    i          DW ?
    t4         DW ?
    MSG00      DB '==============================', 0Dh, 0Ah, '$'
    MSG01      DB '  PILA BASADA EN ARRAY v2.0   ', 0Dh, 0Ah, '$'
    MSG02      DB '1. Push (Apilar)', 0Dh, 0Ah, '$'
    MSG03      DB '2. Pop (Desapilar)', 0Dh, 0Ah, '$'
    MSG04      DB '3. Peek (Ver solo la Cima)', 0Dh, 0Ah, '$'
    MSG05      DB '4. Mostrar toda la Memoria', 0Dh, 0Ah, '$'
    MSG06      DB '0. Salir', 0Dh, 0Ah, '$'
    MSG07      DB 'Elija una opcion: ', 0Dh, 0Ah, '$'
    MSG08      DB '-> ERROR: Pila LLENA (Stack Overflow)', 0Dh, 0Ah, '$'
    MSG09      DB 'Ingrese el numero a apilar: ', 0Dh, 0Ah, '$'
    MSG10      DB '-> Valor apilado correctamente.', 0Dh, 0Ah, '$'
    MSG11      DB '-> ERROR: Pila VACIA (Stack Underflow)', 0Dh, 0Ah, '$'
    MSG12      DB '-> Valor desapilado: ', 0Dh, 0Ah, '$'
    MSG13      DB '-> ERROR: La pila esta vacia.', 0Dh, 0Ah, '$'
    MSG14      DB '-> El valor en la CIMA es: ', 0Dh, 0Ah, '$'
    MSG15      DB '-> [ Pila Vacia ]', 0Dh, 0Ah, '$'
    MSG16      DB '--- MEMORIA ACTUAL ---', 0Dh, 0Ah, '$'
    MSG17      DB 'Indice [', 0Dh, 0Ah, '$'
    MSG18      DB '] : ', 0Dh, 0Ah, '$'
    MSG19      DB '----------------------', 0Dh, 0Ah, '$'
    MSG20      DB 'Apagando sistema...', 0Dh, 0Ah, '$'
    MSG21      DB '-> ERROR: Opcion invalida.', 0Dh, 0Ah, '$'
    MSG22      DB '', 0Dh, 0Ah, '$'
    NEWLINE_CHARS DB 0Dh, 0Ah, '$'
    PROMPT_SCAN   DB 'Ingrese un numero (y presione Enter): $'

.CODE
MAIN PROC
    MOV AX, @DATA
    MOV DS, AX
    MOV opcion, 1
    MOV tope, 0
    MOV valor, 0
    MOV indice, 0
    MOV mem0, 0
    MOV mem1, 0
    MOV mem2, 0
    MOV mem3, 0
    MOV mem4, 0
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
    JE L5
    MOV AX, opcion
    CMP AX, 2
    JE L6
    MOV AX, opcion
    CMP AX, 3
    JE L7
    MOV AX, opcion
    CMP AX, 4
    JE L8
    MOV AX, opcion
    CMP AX, 0
    JE L9
    JMP L10
L5:
    MOV AX, tope
    CMP AX, 5
    JE L12
    JMP L13
L12:
    LEA DX, MSG08
    MOV AH, 09h
    INT 21h
    JMP L11
L13:
    LEA DX, MSG09
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
    CMP AX, 0
    JE L15
    MOV AX, tope
    CMP AX, 1
    JE L16
    MOV AX, tope
    CMP AX, 2
    JE L17
    MOV AX, tope
    CMP AX, 3
    JE L18
    MOV AX, tope
    CMP AX, 4
    JE L19
    JMP L14
L15:
    MOV AX, valor
    MOV mem0, AX
    JMP L14
L16:
    MOV AX, valor
    MOV mem1, AX
    JMP L14
L17:
    MOV AX, valor
    MOV mem2, AX
    JMP L14
L18:
    MOV AX, valor
    MOV mem3, AX
    JMP L14
L19:
    MOV AX, valor
    MOV mem4, AX
    JMP L14
L14:
    MOV AX, tope
    MOV BX, 1
    ADD AX, BX
    MOV t1, AX
    MOV AX, t1
    MOV tope, AX
    LEA DX, MSG10
    MOV AH, 09h
    INT 21h
L11:
    JMP L4
L6:
    MOV AX, tope
    CMP AX, 0
    JE L21
    JMP L22
L21:
    LEA DX, MSG11
    MOV AH, 09h
    INT 21h
    JMP L20
L22:
    MOV AX, tope
    MOV BX, 1
    SUB AX, BX
    MOV t2, AX
    MOV AX, t2
    MOV tope, AX
    MOV AX, tope
    CMP AX, 0
    JE L24
    MOV AX, tope
    CMP AX, 1
    JE L25
    MOV AX, tope
    CMP AX, 2
    JE L26
    MOV AX, tope
    CMP AX, 3
    JE L27
    MOV AX, tope
    CMP AX, 4
    JE L28
    JMP L23
L24:
    MOV AX, mem0
    MOV valor, AX
    JMP L23
L25:
    MOV AX, mem1
    MOV valor, AX
    JMP L23
L26:
    MOV AX, mem2
    MOV valor, AX
    JMP L23
L27:
    MOV AX, mem3
    MOV valor, AX
    JMP L23
L28:
    MOV AX, mem4
    MOV valor, AX
    JMP L23
L23:
    LEA DX, MSG12
    MOV AH, 09h
    INT 21h
    MOV AX, valor
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
L20:
    JMP L4
L7:
    MOV AX, tope
    CMP AX, 0
    JE L30
    JMP L31
L30:
    LEA DX, MSG13
    MOV AH, 09h
    INT 21h
    JMP L29
L31:
    MOV AX, tope
    MOV BX, 1
    SUB AX, BX
    MOV t3, AX
    MOV AX, t3
    MOV indice, AX
    MOV AX, indice
    CMP AX, 0
    JE L33
    MOV AX, indice
    CMP AX, 1
    JE L34
    MOV AX, indice
    CMP AX, 2
    JE L35
    MOV AX, indice
    CMP AX, 3
    JE L36
    MOV AX, indice
    CMP AX, 4
    JE L37
    JMP L32
L33:
    MOV AX, mem0
    MOV valor, AX
    JMP L32
L34:
    MOV AX, mem1
    MOV valor, AX
    JMP L32
L35:
    MOV AX, mem2
    MOV valor, AX
    JMP L32
L36:
    MOV AX, mem3
    MOV valor, AX
    JMP L32
L37:
    MOV AX, mem4
    MOV valor, AX
    JMP L32
L32:
    LEA DX, MSG14
    MOV AH, 09h
    INT 21h
    MOV AX, valor
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
L29:
    JMP L4
L8:
    MOV AX, tope
    CMP AX, 0
    JE L39
    JMP L40
L39:
    LEA DX, MSG15
    MOV AH, 09h
    INT 21h
    JMP L38
L40:
    LEA DX, MSG16
    MOV AH, 09h
    INT 21h
    MOV i, 0
L41:
    MOV AX, i
    MOV BX, tope
    CMP AX, BX
    JL L42
    JMP L44
L42:
    MOV AX, i
    CMP AX, 0
    JE L46
    MOV AX, i
    CMP AX, 1
    JE L47
    MOV AX, i
    CMP AX, 2
    JE L48
    MOV AX, i
    CMP AX, 3
    JE L49
    MOV AX, i
    CMP AX, 4
    JE L50
    JMP L45
L46:
    MOV AX, mem0
    MOV valor, AX
    JMP L45
L47:
    MOV AX, mem1
    MOV valor, AX
    JMP L45
L48:
    MOV AX, mem2
    MOV valor, AX
    JMP L45
L49:
    MOV AX, mem3
    MOV valor, AX
    JMP L45
L50:
    MOV AX, mem4
    MOV valor, AX
    JMP L45
L45:
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
    MOV AX, valor
    CALL PRINT_NUM
    LEA DX, NEWLINE_CHARS
    MOV AH, 09h
    INT 21h
L43:
    MOV AX, i
    MOV BX, 1
    ADD AX, BX
    MOV t4, AX
    MOV AX, t4
    MOV i, AX
    JMP L41
L44:
    LEA DX, MSG19
    MOV AH, 09h
    INT 21h
L38:
    JMP L4
L9:
    LEA DX, MSG20
    MOV AH, 09h
    INT 21h
    JMP L4
L10:
    LEA DX, MSG21
    MOV AH, 09h
    INT 21h
    JMP L4
L4:
    LEA DX, MSG22
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
