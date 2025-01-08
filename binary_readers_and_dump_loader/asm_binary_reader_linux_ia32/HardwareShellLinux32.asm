;==============================================================================;
;                                                                              ;
;                          HARDWARE SHELL PROJECT.                             ;
;                                                                              ;
;                 Template for console debug. Linux 32 edition.                ;
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
; Linux 32 calling convention notes.
; -----------------------------------
;
; 1) List of syscall numbers (some lists for multiple architectures, SELECT RIGHT BY SCROLL LISTS (x86_64 or x86_32):
; https://chromium.googlesource.com/chromiumos/docs/+/master/constants/syscalls.md#x86-32_bit
; https://android.googlesource.com/platform/prebuilts/gcc/linux-x86/host/x86_64-linux-glibc2.7-4.6/+/jb-dev/sysroot/usr/include/asm/unistd_64.h 
; Main API information source is man7.org.
;
; 2) Function code = EAX.
; Input parameters = EBX, ECX, EDX, ESI, EDI, EBP.
; Output parameter = EAX.
;
; 3) Volatile registers:   EAX, ECX, EDX. 
; Non-volatile registers:  EBX, ESI, EDI, EBP.
;
;==============================================================================;

include 'service\connect_service_equ.inc'
include 'system\connect_system_equ.inc'

format ELF executable 3
segment readable executable
entry start
segment readable executable
start:

lea ebx,[Alias_Base]        ; EBX = Base for variables addressing.
xor eax,eax
mov ALIAS_REPORTNAME,eax    ; Clear report file name pointer, before first out. 
mov ALIAS_REPORTHANDLE,eax  ; Clear report file name handle, before first out.
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
lea ecx,[InputName]              ; Parm#1 = ECX = Pointer to scenario file name.
lea edx,ALIAS_SCENARIOHANDLE     ; Parm#2 = EDX = Pointer to sc. file handle. 
lea esi,ALIAS_SCENARIOBASE       ; Parm#3 = ESI = Pointer to pointer to buffer.
lea edi,ALIAS_SCENARIOSIZE       ; Parm#4 = EDI = Pointer to pointer to size.
lea eax,[TEMP_BUFFER]            ; EAX = Buffer base, 32-bit address required.
mov [esi],eax                    ; Write buffer base address.
mov dword [edi],TEMP_BUFFER_SIZE ; Write buffer size limit.
call ReadScenario
;---------- Check loaded scenario file size. ---------------------------------;
; Detect error if loaded size = buffer size (means file size >= buffer size).
cmp ALIAS_SCENARIOSIZE, TEMP_BUFFER_SIZE
lea ecx,[MsgInputSize]       ; ECX = Base address for error message.
jae ErrorProgramSingleParm   ; Go error if size limit. 
;--- Interpreting input ( scenario ) file, update options values variables. ---;
lea ecx,[TEMP_BUFFER]      ; ECX = Pointer to buffer with scenario file.
mov edx,ALIAS_SCENARIOSIZE
add edx,ecx                ; EDX = Buffer limit, addr. of first not valid byte.
lea esi,[OpDesc]           ; ESI = Pointer to options descriptors list.
lea edi,ALIAS_ERROR_STATUS ; EDI = Pointer to error status info.
call ParseScenario
;--- Check option " display = on|off " , clear output handle if " off ". ------;
xor edx,edx
cmp [Option_Display],dl    ; DL = 0.
jne @f
mov ALIAS_STDOUT,edx       ; EDX = 0. 
@@:
;--- Check option " waitkey = on|off " , clear input handle if " off ". -------; 
cmp [Option_Waitkey],dl    ; DL = 0.
jne @f
mov ALIAS_STDIN,edx        ; EDX = 0. 
@@:
;---------- Check parsing status, this must be after options interpreting. ----;
mov ecx,ALIAS_ERROR_P1     ; ECX = Pointer to first error description string.
mov edx,ALIAS_ERROR_P2     ; EDX = Pointer to second error description string.
test eax,eax
jz ErrorProgramDualParm    ; Go if input scenario file parsing error.
;--- Start message, only after loading options, possible " display = off ". ---;
lea ecx,[StartMsg]         ; Parm#1 = ECX = Pointer to string for output.         
call ConsoleWriteReport    ; Output first message, output = display + file.
test eax,eax
jz ExitProgram             ; Silent exit if console write failed.
;---------- Initializing save output ( report ) file mechanism: OUTPUT.TXT. ---; 
cmp [Option_Report],0
je @f                      ; Go skip create report if option " report = off ".
lea ecx,[OutputName]       ; Parm#1 = ECX = Pointer to report file name. 
mov ALIAS_REPORTNAME,ecx
lea edx,ALIAS_REPORTHANDLE ; Parm#2 = EDX = Pointer to report file handle. 
call CreateReport
@@:
;---------- Show list with options settings. ----------------------------------;
lea ecx,[OpDesc]           ; Parm#1 = ECX = Pointers to options descriptors.
lea edx,[TEMP_BUFFER]      ; Parm#2 = EDX = Pointer to buffer for build text.
call ShowScenario
;---------- Here starts target functionality under debug. ---------------------;

lea ecx,ALIAS_ERROR_STATUS ; ECX = Pointer to 3 qwords for status return.
call SHELL_Init
mov ecx,ALIAS_ERROR_P1     ; ECX = Pointer to first error description string.
mov edx,ALIAS_ERROR_P2     ; EDX = Pointer to second error description string.
mov esi,ALIAS_ERROR_C      ; ESI = OS API error code. 
test eax,eax
jz ErrorProgramTripleParm
call ScenarioRunner

;---------- Here ends target functionality under debug. -----------------------;

;---------- This for "Press any key..." not add to text report. ---------------;
lea ebx,[Alias_Base]        ; EBX = Restore base for variables addressing.
xor eax,eax
mov ALIAS_REPORTNAME,eax    ; Clear report file name pointer. 
mov ALIAS_REPORTHANDLE,eax  ; Clear report file name handle.

;---------- Restore original color. -------------------------------------------;
call GetColor               ; Return EAX = Original ( OS ) console color.
xchg ecx,eax
call SetColor               ; Set color by input ECX.
;--- Done message, write to console (optional) and report file (optional). ----;
lea ecx,[DoneMsgNoWait]     ; Parm#1 = ECX = Pointer to message.
cmp [Option_Waitkey],0
je  @f
lea ecx,[DoneMsgWait]       ; Parm#1 = ECX = Pointer to message.
@@:
call ConsoleWriteReport 

;---------- Wait key press. ---------------------------------------------------;
lea ecx,[TEMP_BUFFER]     ; Parm#1 = ECX = Pointer to buffer for char.
mov edx,TEMP_BUFFER_SIZE  ; Parm#2 = EDX = Buffer size limit.
call ConsoleReadAnyEvent  ; Console input.
lea ecx,[CrLf2]           ; Parm#1 = ECX = Pointer to 0Dh, 0Ah ( CR, LF ).
call ConsoleWriteReport   ; Console output, go to next line after press.
;---------- Exit application, this point used if no errors --------------------;
ExitProgram:              ; Common entry point for exit to OS.
xor ebx,ebx               ; Parm#1 = EBX = Exit code 0 ( no errors ).
mov eax,SYS_EXIT          ; EAX = Linux API function ( syscall number ).
int 80h                   ; No return from this call.
;---------- Error handling and exit application. ------------------------------;
ErrorProgramSingleParm:   ; Here valid Parm#1 = ECX = Pointer to string.
xor edx,edx               ; Parm#2 = EDX = Pointer to second string, not used. 
ErrorProgramDualParm:     ; Here used 2 params: ECX, EDX.
xor esi,esi               ; Parm#3 = ESI  = OS API error code, not used. 
ErrorProgramTripleParm:   ; Here used all 3 params: ECX, EDX, ESI.
xchg eax,esi              ; Now EAX = OS error code. 
lea edi,[TEMP_BUFFER]     ; Parm#4 = ESI = Pointer to work buffer.
call ShowError            ; Show error message.
mov ebx,1                 ; Parm#1 = EBX = Exit code 1 ( error detected ).
mov eax,SYS_EXIT          ; EAX = Linux API function ( syscall number ).
int 80h                   ; No return from this call.

include 'service\connect_service_code.inc'
include 'system\connect_system_code.inc'

segment readable writeable
include 'service\connect_service_const.inc'
include 'system\connect_system_const.inc'
include 'service\connect_service_var.inc'
include 'system\connect_system_var.inc'

