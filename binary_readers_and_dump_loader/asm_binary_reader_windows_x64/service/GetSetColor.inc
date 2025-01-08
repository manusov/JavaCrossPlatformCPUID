;---------- Get console color, saved at start. -----------------------------;
; Input / Output parameters and Volatile / Non volatile registers           ;
; compatible with Microsoft x64 calling convention.                         ;
;                                                                           ;
; INPUT:  None.                                                             ;
;                                                                           ;
; OUTPUT: EAX = Color code.                                                 ;
;---------------------------------------------------------------------------;
GetColor:
mov eax,[ScreenInfo.wAttributes]
ret
;---------- Set console color. ---------------------------------------------;
; Input / Output parameters and Volatile / Non volatile registers           ;
; compatible with Microsoft x64 calling convention.                         ;
;                                                                           ;
; INPUT:   ECX = New color code,                                            ;
;          use global variable [StdOut].                                    ;  
;                                                                           ;
; OUTPUT:  RAX = OS Status.                                                 ;
;---------------------------------------------------------------------------;
SetColor:
push rbp
mov rbp,rsp                    ; RBP = storage for RSP and pointer to frame.
and rsp,0FFFFFFFFFFFFFFF0h     ; Align stack.
;---------- Set console color. ------------------------------------------------;
mov edx,ecx                    ; EDX = Color for set.
mov rcx,[StdOut]               ; RCX = Handle for console output.
sub rsp,32                     ; Parameters shadow.
call [SetConsoleTextAttribute]
mov rsp,rbp   ; This for restore after alignment and also instead ADD RSP,32.
pop rbp
ret
;--- Set console foreground color, background color as saved at start. -----;
; Input / Output parameters and Volatile / Non volatile registers           ;
; compatible with Microsoft x64 calling convention.                         ;
;                                                                           ;
; INPUT:   ECX = New foreground color code,                                 ;
;          use global variable [StdOut].                                    ;
;                                                                           ;
; OUTPUT:  RAX = OS Status.                                                 ;
;---------------------------------------------------------------------------;
SetFgColor:
push rbp
mov rbp,rsp                 ; RBP = storage for RSP and pointer to frame.
and rsp,0FFFFFFFFFFFFFFF0h  ; Align stack for call WinAPI by convention.
; Set console color
call GetColor               ; Return EAX = default color.
and eax,CLEAR_FOREGROUND
and ecx,CLEAR_BACKGROUND
lea rdx,[rax + rcx]         ; EDX = Color for set.
mov rcx,[StdOut]            ; RCX = Handle for console output.
sub rsp,32                  ; Parameters shadow.
call [SetConsoleTextAttribute]
mov rsp,rbp   ; This for restore after alignment and also instead ADD RSP,32.
pop rbp
ret
