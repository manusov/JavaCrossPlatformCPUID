;==============================================================================;
;                                                                              ;
;                          HARDWARE SHELL PROJECT.                             ;
;                                                                              ;
;                 Template for console debug. Linux 64 edition.                ;
;                                                                              ;
;               Customized for debug native library functionality              ; 
;                      before integration into Java CPUID.                     ; 
;                                                                              ;
;        Translation by Flat Assembler version 1.73.27 ( Jan 27, 2021 )        ;
;                         http://flatassembler.net/                            ;
;                                                                              ;
;       Edit by FASM Editor 2.0, use this editor for correct tabulations.      ;
;              https://fasmworld.ru/instrumenty/fasm-editor-2-0/               ;
;                                                                              ;
;==============================================================================;
;
; Get information from:
; ----------------------
; Ray Seyfarth PDF is main valid book.
; https://rayseyfarth.com/asm_1/ch12-system-calls.pdf
; http://man7.org/linux/man-pages/dir_section_2.html
; http://man7.org/linux/man-pages/man2/creat.2.html
; http://unix.superglobalmegacorp.com/Net2/newsrc/sys/mman.h.html
; http://syscalls.kernelgrok.com/
;
; Linux 64 calling convention notes.
; -----------------------------------
;
; 1) List of syscall numbers (some lists for multiple architectures, SELECT RIGHT BY SCROLL LISTS (x86_64 or x86_32):
; https://chromium.googlesource.com/chromiumos/docs/+/master/constants/syscalls.md#x86-32_bit
; https://android.googlesource.com/platform/prebuilts/gcc/linux-x86/host/x86_64-linux-glibc2.7-4.6/+/jb-dev/sysroot/usr/include/asm/unistd_64.h 
; Main API information source is man7.org.
;
; 2) Parameters order for system calls
;    (from parameter #7 used stack):
; stack  ; ...
; r9     ; 6th param
; r8     ; 5th param
; r10    ; 4th param
; rdx    ; 3rd param
; rsi    ; 2nd param
; rdi    ; 1st param
; eax    ; syscall_number
; syscall
; Return = rax.
;
; 3) Parameters order for non-system calls
;   (from parameter #7(integer) or #9(float) used stack: 
; stack        ; ...
; stack  xmm7  ; 8th param
; stack  xmm6  ; 7th param
; r9     xmm5  ; 6th param
; r8     xmm4  ; 5th param
; rcx    xmm3  ; 4th param
; rdx    xmm2  ; 3rd param
; rsi    xmm1  ; 2nd param
; rdi    xmm0  ; 1st param
; Return = rax.
;
; 4) Volatile registers:   RAX, RCX, RDX, RSI, RDI, R8-R11, ST(0)-ST(7), K0-K7, 
;                          XMM0-XMM15 / YMM0-YMM15 / ZMM0-ZMM31
; Non-volatile registers:  RBX, RBP, R12-R15
;
; 5) Note 32-bit operations (example: MOV EDX,3) also clears bits D[63-32] of
; target registers and can be used instead 64-operations (example: MOV RDX,3)
; for save space. This note actual not for Linux only, for x64 total.
;
;==============================================================================;

include 'service\connect_service_equ.inc'
include 'system\connect_system_equ.inc'

format ELF64 executable 3
segment readable executable
entry $

lea rbx,[Alias_Base]        ; RBX = Base for variables addressing.
xor eax,eax
mov ALIAS_REPORTNAME,rax    ; Clear report file name pointer, before first out. 
mov ALIAS_REPORTHANDLE,rax  ; Clear report file name handle, before first out.
;---------- Initializing console input handle. --------------------------------;
mov ALIAS_STDIN,DEVICE_STDIN
;---------- Initializing console output handle. -------------------------------;
mov ALIAS_STDOUT,DEVICE_STDOUT
;---------- Initializing console color. ---------------------------------------;
mov ALIAS_COLOR,COLOR_RESET_DEFAULT
;---------- Detect command line. ----------------------------------------------;
; Reserved
;---------- Title string. -----------------------------------------------------;
; Reserved
;---------- Initializing string-type options. ---------------------------------;
; Used for source and destination files by file I/O benchmark scenario.
; Reserved, not used for this application.
; See NIOBench debug samples for file name option support.
;---------- Load scenario file: INPUT.TXT. ------------------------------------;
lea rcx,[InputName]             ; Parm#1 = RCX = Pointer to scenario file name.
lea rdx,ALIAS_SCENARIOHANDLE    ; Parm#2 = RDX = Pointer to sc. file handle. 
lea r8,ALIAS_SCENARIOBASE       ; Parm#3 = R8  = Pointer to pointer to buffer.
lea r9,ALIAS_SCENARIOSIZE       ; Parm#4 = R9  = Pointer to pointer to size.
lea rax,[TEMP_BUFFER]           ; RAX = Buffer base, 64-bit address required.
mov [r8],rax                    ; Write buffer base address.
mov qword [r9],TEMP_BUFFER_SIZE ; Write buffer size limit.
call ReadScenario
;---------- Check loaded scenario file size. ---------------------------------;
; Detect error if loaded size = buffer size (means file size >= buffer size).
cmp ALIAS_SCENARIOSIZE, TEMP_BUFFER_SIZE
lea rcx,[MsgInputSize]       ; RCX = Base address for error message.
jae ErrorProgramSingleParm   ; Go error if size limit. 
;--- Interpreting input ( scenario ) file, update options values variables. ---;
lea rcx,[TEMP_BUFFER]        ; RCX = Pointer to buffer with scenario file.
mov rdx,ALIAS_SCENARIOSIZE
add rdx,rcx                ; RDX = Buffer limit, addr. of first not valid byte.
lea r8,[OpDesc]            ; R8 = Pointer to options descriptors list.
lea r9,ALIAS_ERROR_STATUS  ; R9 = Pointer to error status info.
call ParseScenario
;--- Check option " display = on|off " , clear output handle if " off ". ------;
xor edx,edx
cmp [Option_Display],dl    ; DL = 0.
jne @f
mov ALIAS_STDOUT,rdx       ; RDX = 0. 
@@:
;--- Check option " waitkey = on|off " , clear input handle if " off ". -------; 
cmp [Option_Waitkey],dl    ; DL = 0.
jne @f
mov ALIAS_STDIN,rdx        ; RDX = 0. 
@@:
;---------- Check parsing status, this must be after options interpreting. ----;
mov rcx,ALIAS_ERROR_P1     ; RCX = Pointer to first error description string.
mov rdx,ALIAS_ERROR_P2     ; RDX = Pointer to second error description string.
test rax,rax
jz ErrorProgramDualParm    ; Go if input scenario file parsing error.
;--- Start message, only after loading options, possible " display = off ". ---;
lea rcx,[StartMsg]         ; Parm#1 = RCX = Pointer to string for output.         
call ConsoleWriteReport    ; Output first message, output = display + file.
test rax,rax
jz ExitProgram             ; Silent exit if console write failed.
;---------- Initializing save output ( report ) file mechanism: OUTPUT.TXT. ---; 
cmp [Option_Report],0
je @f                      ; Go skip create report if option " report = off ".
lea rcx,[OutputName]       ; Parm#1 = RCX = Pointer to report file name. 
mov ALIAS_REPORTNAME,rcx
lea rdx,ALIAS_REPORTHANDLE ; Parm#2 = RDX = Pointer to report file handle. 
call CreateReport
@@:
;---------- Show list with options settings. ----------------------------------;
lea rcx,[OpDesc]           ; Parm#1 = RCX = Pointers to options descriptors.
lea rdx,[TEMP_BUFFER]      ; Parm#2 = RDX = Pointer to buffer for build text.
call ShowScenario

;---------- Here starts target functionality under debug. ---------------------;

lea rcx,ALIAS_ERROR_STATUS ; RCX = Pointer to 3 qwords for status return.
call SHELL_Init
mov rcx,ALIAS_ERROR_P1     ; RCX = Pointer to first error description string.
mov rdx,ALIAS_ERROR_P2     ; RDX = Pointer to second error description string.
mov r8,ALIAS_ERROR_C       ; R8  = OS API error code. 
test rax,rax
jz ErrorProgramTripleParm
call ScenarioRunner

;---------- Here ends target functionality under debug. -----------------------;

;---------- This for "Press any key..." not add to text report. ---------------;
lea rbx,[Alias_Base]        ; RBX = Restore base for variables addressing.
xor eax,eax
mov ALIAS_REPORTNAME,rax    ; Clear report file name pointer. 
mov ALIAS_REPORTHANDLE,rax  ; Clear report file name handle.
;---------- Restore original color. -------------------------------------------;
call GetColor               ; Return EAX = Original ( OS ) console color.
xchg ecx,eax
call SetColor               ; Set color by input ECX.
;--- Done message, write to console (optional) and report file (optional). ----;
lea rcx,[DoneMsgNoWait]     ; Parm#1 = RCX = Pointer to message.
cmp [Option_Waitkey],0
je  @f
lea rcx,[DoneMsgWait]       ; Parm#1 = RCX = Pointer to message.
@@:
call ConsoleWriteReport 

;---------- Wait key press. ---------------------------------------------------;
lea rcx,[TEMP_BUFFER]     ; Parm#1 = RCX = Pointer to buffer for char.
mov edx,TEMP_BUFFER_SIZE  ; Parm#2 = EDX = Buffer size limit.
call ConsoleReadAnyEvent  ; Console input.
lea rcx,[CrLf2]           ; Parm#1 = RCX = Pointer to 0Dh, 0Ah ( CR, LF ).
call ConsoleWriteReport   ; Console output, go to next line after press.

;---------- Exit application, this point used if no errors --------------------;
ExitProgram:              ; Common entry point for exit to OS.
xor edi,edi               ; Parm#1 = EDI = Exit code 0 ( no errors ).
mov eax,SYS_EXIT          ; EAX = Linux API function ( syscall number ).
syscall                   ; No return from this call.

;---------- Error handling and exit application. ------------------------------;
ErrorProgramSingleParm:   ; Here valid Parm#1 = RCX = Pointer to string.
xor edx,edx               ; Parm#2 = RDX = Pointer to second string, not used. 
ErrorProgramDualParm:     ; Here used 2 params: RCX, RDX.
xor r8,r8                 ; Parm#3 = R8  = OS API error code, not used. 
ErrorProgramTripleParm:   ; Here used all 3 params: RCX, RDX, R8.
lea r9,[TEMP_BUFFER]      ; Parm#4 = R9 = Pointer to work buffer.
call ShowError            ; Show error message.
mov edi,1                 ; Parm#1 = EDI = Exit code 1 ( error detected ).
mov eax,SYS_EXIT          ; EAX = Linux API function ( syscall number ).
syscall                   ; No return from this call.

include 'service\connect_service_code.inc'
include 'system\connect_system_code.inc'

segment readable writeable
include 'service\connect_service_const.inc'
include 'system\connect_system_const.inc'
include 'service\connect_service_var.inc'
include 'system\connect_system_var.inc'

