;==============================================================================;
;                                                                              ;
;                        HARDWARE SHELL PROJECT.                               ;
;                                                                              ;
;                  Template for console debug. Win32 edition.                  ;
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

include 'win32a.inc'
include 'service\connect_service_equ.inc'
include 'system\connect_system_equ.inc'

format PE console
entry start
section '.text' code readable executable
start:

lea ebx,[Alias_Base]        ; EBX = Base for variables addressing.
xor eax,eax
mov ALIAS_REPORTNAME,eax    ; Clear report file name pointer, before first out. 
mov ALIAS_REPORTHANDLE,eax  ; Clear report file name handle, before first out.
;---------- Initializing console input handle. --------------------------------;
push STD_INPUT_HANDLE       ; Parm#1 = Handle ID = input device handle.       
call [GetStdHandle]         ; Initializing input device handle ( keyboard ).
test eax,eax
jz ExitProgram              ; Silent exit if get input handle failed.
mov ALIAS_STDIN,eax         ; Store input handle.
;---------- Initializing console output handle. -------------------------------;
push STD_OUTPUT_HANDLE      ; Parm#1 = Handle ID = output device handle.    
call [GetStdHandle]         ; Initializing output device handle ( display ).
test eax,eax
jz ExitProgram              ; Silent exit if get output handle failed.
mov ALIAS_STDOUT,eax        ; Store output handle.
;---------- Detect command line. ----------------------------------------------;
call [GetCommandLineA]      ; Get command line.
test eax,eax
jz ExitProgram              ; Silent exit if get command line failed.
mov ALIAS_COMMANDLINE,eax   ; Store pointer to command line.
;---------- Title string. -----------------------------------------------------;
push TitleString
call [SetConsoleTitle]      ; Title string for console output window up.
;---------- Get console screen buffer information. ----------------------------;
push ScreenInfo             ; Parm#2 = Pointer to destination buffer.
push dword ALIAS_STDOUT     ; Parm#1 = Output handle.
call [GetConsoleScreenBufferInfo]
test eax,eax                ; Silent exit if get information failed, 
jz ExitProgram              ; can replace this termination to non-color branch.
;---------- Initializing string-type options strings. -------------------------;
; Reserved. See NIOBench debug samples for file name option support.
;---------- Load scenario file: INPUT.TXT. ------------------------------------;
lea ecx,[InputName]              ; Parm#1 = ECX = Pointer to scenario file name.
lea edx,ALIAS_SCENARIOHANDLE     ; Parm#2 = EDX = Pointer to sc. file handle.
lea esi,ALIAS_SCENARIOBASE       ; Parm#3 = ESI = Pointer to pointer to buffer.
lea edi,ALIAS_SCENARIOSIZE       ; Parm#4 = EDI = Pointer to pointer to size.
mov dword [esi],TEMP_BUFFER      ; Write buffer base address.
mov dword [edi],TEMP_BUFFER_SIZE ; Write buffer size limit.
call ReadScenario
;---------- Check loaded scenario file size. ----------------------------------;
; Detect error if loaded size = buffer size (means file size >= buffer size).
cmp dword ALIAS_SCENARIOSIZE, TEMP_BUFFER_SIZE
lea ecx,[MsgInputSize]       ; ECX = Base address for error message.
jae ErrorProgramSingleParm   ; Go error if size limit. 
;--- Interpreting input ( scenario ) file, update options values variables. ---;
lea ecx,[TEMP_BUFFER]       ; ECX = Pointer to buffer with scenario file.
mov edx,ALIAS_SCENARIOSIZE
add edx,ecx                 ; EDX = Buffer limit, addr. of first not valid.
lea esi,[OpDesc]            ; ESI = Pointer to options descriptors list.
lea edi,ALIAS_ERROR_STATUS  ; EDI = Pointer to error status info.
call ParseScenario
;--- Check option " display = on|off " , clear output handle if " off ". ------;
xor edx,edx
cmp [Option_Display],dl     ; DL = 0.
jne @f
mov ALIAS_STDOUT,edx        ; EDX = 0.
@@:
;--- Check option " waitkey = on|off " , clear input handle if " off ". -------; 
cmp [Option_Waitkey],dl     ; DL = 0.
jne @f
mov ALIAS_STDIN,edx         ; EDX = 0.
@@:
;---------- Check parsing status, this must be after options interpreting. ----;
mov ecx,ALIAS_ERROR_P1      ; ECX = Pointer to first error description string.
mov edx,ALIAS_ERROR_P2      ; EDX = Pointer to second error description string.
test eax,eax
jz ErrorProgramDualParm     ; Go if input scenario file parsing error.
;--- Start message, only after loading options, possible " display = off ". ---;
lea ecx,[StartMsg]          ; ECX = Pointer to string for output.
call ConsoleWriteReport     ; Output first message, output = display + file.
test eax,eax
jz ExitProgram              ; Silent exit if console write failed.
;--- Initializing save output ( report ) file mechanism: OUTPUT.TXT. ----------; 
cmp [Option_Report],0
je @f                       ; Go skip create report if option " report = off ".
lea ecx,[OutputName]        ; ECX = Pointer to report file name.
lea edx,ALIAS_REPORTHANDLE  ; EDX = Pointer to report file handle.
mov ALIAS_REPORTNAME,ecx
call CreateReport
@@:
;---------- Show list with options settings. ----------------------------------;
lea ecx,[OpDesc]            ; ECX = Pointers to options descriptors.
lea edx,[TEMP_BUFFER]       ; EDX = Pointer to buffer for build text.
call ShowScenario

;---------- Here starts target functionality under debug. ---------------------;

lea ecx,ALIAS_ERROR_STATUS ; RCX = Pointer to 3 qwords for status return.
call SHELL_Init
mov ecx,ALIAS_ERROR_P1     ; ECX = Pointer to first error description string.
mov edx,ALIAS_ERROR_P2     ; EDX = Pointer to second error description string.
test eax,eax               ; Check status, EAX = 0 means error.
mov eax,ALIAS_ERROR_C      ; EAX = WinAPI error code. 
jz ErrorProgramTripleParm
call ScenarioRunner

;---------- Here ends target functionality under debug. -----------------------;

lea ebx,[Alias_Base]         ; EBX = Restore base for variables addressing.
;---------- This for "Press any key..." not add to text report. ---------------;
xor eax,eax
mov ALIAS_REPORTNAME,eax     ; Clear report file name pointer.
mov ALIAS_REPORTHANDLE,eax   ; Clear report file name handle.
;---------- Restore original color. -------------------------------------------;
call GetColor              ; Return EAX = Original ( OS ) console color.
xchg ecx,eax
call SetColor              ; Set color by input ECX.
;--- Done message, write to console (optional) and report file (optional). ----;
lea ecx,[DoneMsgNoWait]    ; ECX = Pointer to message 1.
cmp [Option_Waitkey],0
je  @f
lea ecx,[DoneMsgWait]      ; ECX = Pointer to message 2.
@@:
call ConsoleWriteReport 
;---------- Wait for any key press. -------------------------------------------;
lea ecx,[TEMP_BUFFER]      ; ECX = Pointer to buffer for char.
call ConsoleReadAnyEvent   ; Console input.
;---------- Go to next line after wait key. -----------------------------------;
lea ecx,[CrLf2]            ; ECX = Pointer to 0Dh, 0Ah ( CR, LF ).
call ConsoleWriteReport    ; Console output.

;---------- Exit application, this branch used if no errors. ------------------;
ExitProgram:               ; Common entry point for exit to OS.
push 0                     ; Parm#1 = Exit code = 0 (no errors).
call [ExitProcess]         ; No return from this function.

;---------- Error handling and exit application. ------------------------------;
ErrorProgramSingleParm:    ; Here valid Parm#1 = ECX = Pointer to first string
xor edx,edx                ; Parm#2 = EDX = Pointer to second string, not used 
ErrorProgramDualParm:      ; Here used 2 params: ECX, EDX
xor eax,eax                ; Parm#3 = EAX  = WinAPI error code, not used 
ErrorProgramTripleParm:    ; Here used all 3 params: ECX, EDX, EAX
lea edi,[TEMP_BUFFER]      ; Parm#4 = Pointer to work buffer
call ShowError             ; Show error message
push 1                     ; Parm#1 = Exit code = 1 (error detected)
call [ExitProcess]         ; No return from this function

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
