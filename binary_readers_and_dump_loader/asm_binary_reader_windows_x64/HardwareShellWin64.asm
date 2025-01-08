;==============================================================================;
;                                                                              ;
;                        HARDWARE SHELL PROJECT.                               ;
;                                                                              ;
;                  Template for console debug. Win64 edition.                  ;
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

include 'win64a.inc'
include 'service\connect_service_equ.inc'
include 'system\connect_system_equ.inc'

format PE64 console
entry start
section '.text' code readable executable
start:

sub rsp,8*5                 ; Make parameters shadow and stack alignment.
lea rbx,[Alias_Base]        ; RBX = Base for variables addressing.
xor eax,eax
mov ALIAS_REPORTNAME,rax    ; Clear report file name pointer, before first out. 
mov ALIAS_REPORTHANDLE,rax  ; Clear report file name handle, before first out.
;---------- Initializing console input handle. --------------------------------;
mov ecx,STD_INPUT_HANDLE    ; Parm#1 = RCX = Handle ID = input device handle.       
call [GetStdHandle]         ; Initializing input device handle ( keyboard ).
test rax,rax
jz ExitProgram              ; Silent exit if get input handle failed.
mov ALIAS_STDIN,rax         ; Store input handle.
;---------- Initializing console output handle. -------------------------------;
mov ecx,STD_OUTPUT_HANDLE   ; Parm#1 = RCX = Handle ID = output device handle.    
call [GetStdHandle]         ; Initializing output device handle ( display ).
test rax,rax
jz ExitProgram              ; Silent exit if get output handle failed.
mov ALIAS_STDOUT,rax        ; Store output handle.
;---------- Detect command line. ----------------------------------------------;
call [GetCommandLineA]      ; Get command line.
test rax,rax
jz ExitProgram              ; Silent exit if get command line failed.
mov ALIAS_COMMANDLINE,rax   ; Store pointer to command line.
;---------- Title string. -----------------------------------------------------;
lea rcx,[TitleString]
call [SetConsoleTitle]      ; Title string for console output window up.
;---------- Get console screen buffer information. ----------------------------;
mov rcx,ALIAS_STDOUT        ; Parm#1 = RCX = Output handle.
lea rdx,[ScreenInfo]        ; Parm#2 = RDX = Pointer to destination buffer. 
call [GetConsoleScreenBufferInfo]
test rax,rax                ; Silent exit if get information failed, 
jz ExitProgram              ; can replace this termination to non-color branch.
;---------- Initializing string-type options strings. -------------------------;
; Reserved. See NIOBench debug samples for file name option support.
;---------- Load scenario file: INPUT.TXT. ------------------------------------;
lea rcx,[InputName]              ; Parm#1 = RCX = Pointer to scenario file name.
lea rdx,ALIAS_SCENARIOHANDLE     ; Parm#2 = RDX = Pointer to sc. file handle. 
lea r8,ALIAS_SCENARIOBASE        ; Parm#3 = R8  = Pointer to pointer to buffer.
lea r9,ALIAS_SCENARIOSIZE        ; Parm#4 = R9  = Pointer to pointer to size.
lea rax,[TEMP_BUFFER]            ; RAX = Buffer base, 64-bit address required.
mov [r8],rax                     ; Write buffer base address.
mov qword [r9],TEMP_BUFFER_SIZE  ; Write buffer size limit.
call ReadScenario
;---------- Check loaded scenario file size. ----------------------------------; 
; Detect error if loaded size = buffer size (means file size >= buffer size).
cmp qword ALIAS_SCENARIOSIZE, TEMP_BUFFER_SIZE
lea rcx,[MsgInputSize]      ; RCX = Base address for error message.
jae ErrorProgramSingleParm  ; Go error if size limit. 
;--- Interpreting input ( scenario ) file, update options values variables. ---;
lea rcx,[TEMP_BUFFER]       ; RCX = Pointer to buffer with scenario file.
mov rdx,ALIAS_SCENARIOSIZE
add rdx,rcx                ; RDX = Buffer limit, addr. of first not valid byte.
lea r8,[OpDesc]            ; R8 = Pointer to options descriptors list.
lea r9,ALIAS_ERROR_STATUS  ; R9 = Pointer to error status info.
call ParseScenario
;--- Check option " display = on|off " , clear output handle if " off ". ------;
xor edx,edx
cmp [Option_Display],dl    ; Use DL = 0.
jne @f
mov ALIAS_STDOUT,rdx       ; Use RDX = 0. 
@@:
;--- Check option " waitkey = on|off " , clear input handle if " off ". -------; 
cmp [Option_Waitkey],dl    ; Use DL = 0.
jne @f
mov ALIAS_STDIN,rdx        ; Use RDX = 0. 
@@:
;---------- Check parsing status, this must be after options interpreting. ----;
mov rcx,ALIAS_ERROR_P1     ; RCX = Pointer to first error description string.
mov rdx,ALIAS_ERROR_P2     ; RDX = Pointer to second error description string.
test rax,rax
jz ErrorProgramDualParm    ; Go if input scenario file parsing error.
;--- Start message, only after loading options, possible " display = off " ----;
lea rcx,[StartMsg]         ; Parm#1 = RCX = Pointer to string for output.         
call ConsoleWriteReport    ; Output first message, output = display + file.
test rax,rax
jz ExitProgram             ; Silent exit if console write failed.
;--- Initializing save output ( report ) file mechanism: file OUTPUT.TXT. -----; 
cmp [Option_Report],0
je @f                      ; Go skip create report if option " report = off ".
lea rcx,[OutputName]       ; Parm#1 = RCX = Pointer to report file name. 
mov ALIAS_REPORTNAME,rcx
lea rdx,ALIAS_REPORTHANDLE  ; Parm#2 = RDX = Pointer to report file handle. 
call CreateReport
@@:
;---------- Show list with options settings. ----------------------------------;
lea rcx,[OpDesc]            ; Parm#1 = RCX = Pointers to options descriptors.
lea rdx,[TEMP_BUFFER]       ; Parm#2 = RDX = Pointer to buffer for build text.
call ShowScenario

;---------- Here starts target functionality under debug. ---------------------;

lea rcx,ALIAS_ERROR_STATUS ; RCX = Pointer to 3 qwords for status return.
call SHELL_Init
mov rcx,ALIAS_ERROR_P1     ; RCX = Pointer to first error description string.
mov rdx,ALIAS_ERROR_P2     ; RDX = Pointer to second error description string.
mov r8,ALIAS_ERROR_C       ; R8  = WinAPI error code. 
test rax,rax
jz ErrorProgramTripleParm
call ScenarioRunner

;---------- Here ends target functionality under debug. -----------------------;

lea rbx,[Alias_Base]       ; RBX = Restore base for variables addressing.
;---------- This for "Press any key..." not add to text report. ---------------;
xor eax,eax
mov ALIAS_REPORTNAME,rax   ; Clear report file name pointer 
mov ALIAS_REPORTHANDLE,rax ; Clear report file name handle
;---------- Restore original color. -------------------------------------------;
call GetColor              ; Return EAX = Original ( OS ) console color.
xchg ecx,eax
call SetColor              ; Set color by input ECX.
;--- Done message, write to console (optional) and report file (optional). ----;
lea rcx,[DoneMsgNoWait]    ; Parm#1 = RCX = Pointer to message.
cmp [Option_Waitkey],0
je  @f
lea rcx,[DoneMsgWait]      ; Parm#1 = RCX = Pointer to message.
@@:
call ConsoleWriteReport 
;---------- Wait for any key press. -------------------------------------------;
lea rcx,[TEMP_BUFFER]      ; Parm#1 = RCX = Pointer to buffer for INPUT_RECORD.
call ConsoleReadAnyEvent   ; Console input.
;---------- Go to next line after wait key. -----------------------------------;
lea rcx,[CrLf2]            ; Parm#1 = RCX = Pointer to 0Dh, 0Ah ( CR, LF ).
call ConsoleWriteReport    ; Console output.

;---------- Exit application, this branch used if no errors. ------------------;
ExitProgram:             ; Common entry point for exit to OS.
xor ecx,ecx              ; Parm#1 = RCX = Exit code = 0 (no errors).
call [ExitProcess]       ; No return from this function.

;---------- Error handling and exit application. ------------------------------;
ErrorProgramSingleParm:  ; Here valid Parm#1 = RCX = Pointer to string.
xor edx,edx              ; Parm#2 = RDX = Pointer to second string, not used. 
ErrorProgramDualParm:    ; Here used 2 params: RCX, RDX.
xor r8,r8                ; Parm#3 = R8  = WinAPI error code, not used. 
ErrorProgramTripleParm:  ; Here used all 3 params: RCX, RDX, R8.
lea r9,[TEMP_BUFFER]     ; Parm#4 = R9 = Pointer to work buffer.
call ShowError           ; Show error message.
mov ecx,1                ; Parm#1 = RCX = Exit code = 1 (error detected).
call [ExitProcess]       ; No return from this function.

;------------------------------------------------------------------------------;
;                            Connected modules.                                ;
;------------------------------------------------------------------------------;

include 'service\connect_service_code.inc'
include 'system\connect_system_code.inc'

section '.data' data readable writeable
include 'service\connect_service_const.inc'
include 'system\connect_system_const.inc'
include 'service\connect_service_var.inc'
include 'system\connect_system_var.inc'

section '.idata' import data readable writeable
library kernel32, 'KERNEL32.DLL', user32, 'USER32.DLL', gdi32, 'GDI32.DLL'
include 'api\kernel32.inc'  ; Win API, OS standard functions.
include 'api\user32.inc'    ; Win API, user interface.
include 'api\gdi32.inc'     ; Win API, graphics. 
