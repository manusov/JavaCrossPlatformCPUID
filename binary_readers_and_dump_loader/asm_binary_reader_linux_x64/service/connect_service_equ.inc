;------------------------------------------------------------------------------;
;                                                                              ;
;          This directory contains helpers library for debug support.          ;
;             This file contains globally used source definitions.             ;
;            Console support library: global used data definitions.            ;
;                                                                              ;
;------------------------------------------------------------------------------;

;---------- Keyboard and display constants. -----------------------------------;
DEVICE_STDIN           EQU  2       ; Here means keyboard.
DEVICE_STDOUT          EQU  1       ; Here means display.
;---------- Linux API functions ( syscall numbers ). --------------------------; 
SYS_READ               EQU  0       ; Linux API functions ( syscall numbers ).
SYS_WRITE              EQU  1
SYS_OPEN               EQU  2
SYS_CLOSE              EQU  3
SYS_LSEEK              EQU  8
SYS_MMAP               EQU  9
SYS_MUNMAP             EQU  11
SYS_IOCTL              EQU  16
SYS_NANOSLEEP          EQU  35
SYS_GETPID             EQU  39
SYS_EXIT               EQU  60
SYS_UNLINK             EQU  87
SYS_GETTIME            EQU  228
SYS_GETRES             EQU  229
SYS_SETAFFINITY        EQU  203
SYS_GETAFFINITY        EQU  204
SYS_SETMEMPOLICY       EQU  238     ; Note alternative codes.
SYS_GETMEMPOLICY       EQU  239     ; Note alternative codes.
;---------- Constants equations. ----------------------------------------------; 
FILE_FLAGS_BLANK       EQU  00000042h
FILE_FLAG_TRUNCATE     EQU  00000200h
FILE_FLAG_O_DIRECT     EQU  00004000h
FILE_SHARE_MODE        EQU  -1     ; Note old value is 00000C00h required sudo.
;---------- Linux IOCTL definitions. ------------------------------------------;
TERMIOS_SIZE           EQU  36      ; Size of buffer for terminal control.
TCGETS                 EQU  5401h   ; IOCTL request, get data.
TCSETS                 EQU  5402h   ; IOCTL request, set data.
ECHO_FLAG              EQU  8       ; Bit mask for ECHO control bit.
ECHO_OFFSET            EQU  12      ; Address offset for ECHO control.
;---------- Application-defined buffers. --------------------------------------;
TEMP_BUFFER_ALIGNMENT  EQU  4096    ; Temporary buffer alignment = page.
TEMP_BUFFER_SIZE       EQU  32768   ; Temporary buffer size = 32 KB. 
IPB_CONST              EQU  4096    ; Input Parameters Block size, bytes.
OPB_CONST              EQU  4096    ; Output Parameters Block size, bytes.
;---------- Aliases for compact access to variables. --------------------------;
; Update this required if change variables layout at connect_var.inc.
ALIAS_STDIN           EQU  qword [rbx + 8*00]
ALIAS_STDOUT          EQU  qword [rbx + 8*01]
ALIAS_REPORTNAME      EQU  qword [rbx + 8*02]
ALIAS_REPORTHANDLE    EQU  qword [rbx + 8*03]
ALIAS_SCENARIOHANDLE  EQU  qword [rbx + 8*04] 
ALIAS_SCENARIOBASE    EQU  qword [rbx + 8*05]
ALIAS_SCENARIOSIZE    EQU  qword [rbx + 8*06] 
ALIAS_COMMANDLINE     EQU  qword [rbx + 8*07]
;--- This 3 variables must be continuous for return status from subroutines. --;
ALIAS_ERROR_STATUS    EQU  qword [rbx + 8*08]    
ALIAS_ERROR_P1        EQU  qword [rbx + 8*08]  ; Alias of previous.
ALIAS_ERROR_P2        EQU  qword [rbx + 8*09]
ALIAS_ERROR_C         EQU  qword [rbx + 8*10]
;---------- Console current color variable. -----------------------------------;
ALIAS_COLOR           EQU  dword [rbx + 8*11]

;---------- Color masks. ------------------------------------------------------;
CLEAR_FOREGROUND      EQU  0FFFFFFF0h
CLEAR_BACKGROUND      EQU  0FFFFFF0Fh
SELECT_FOREGROUND     EQU  00000000Fh
SELECT_BACKGROUND     EQU  0000000F0h
SELECT_BOTH           EQU  SELECT_FOREGROUND + SELECT_BACKGROUND
;--- Color values, note R,G,B reverse order relative MS Windows console. ------;
FOREGROUND_BLUE       EQU  04h
FOREGROUND_GREEN      EQU  02h
FOREGROUND_RED        EQU  01h
FOREGROUND_INTENSITY  EQU  08h
BACKGROUND_BLUE       EQU  040h
BACKGROUND_GREEN      EQU  020h
BACKGROUND_RED        EQU  010h
BACKGROUND_INTENSITY  EQU  080h
;---------- Special color control for reset to default. -----------------------;
COLOR_RESET_DEFAULT   EQU  080000000h

;---------- Registers and memory dump subroutines support. --------------------;
; Global used data definitions.
REGISTER_NAME_COLOR   EQU  FOREGROUND_RED + FOREGROUND_GREEN + FOREGROUND_INTENSITY
REGISTER_VALUE_COLOR  EQU  FOREGROUND_RED + FOREGROUND_GREEN + FOREGROUND_BLUE + FOREGROUND_INTENSITY
DUMP_ADDRESS_COLOR    EQU  FOREGROUND_GREEN + FOREGROUND_INTENSITY
DUMP_DATA_COLOR       EQU  FOREGROUND_RED + FOREGROUND_GREEN + FOREGROUND_BLUE + FOREGROUND_INTENSITY

;---------- Definitions for configuration file support. -----------------------;
; Options descriptors, types of descriptors. 
XEND    EQU  0
XKEY    EQU  1 
XDEC    EQU  2
XHEX    EQU  3
XSIZE   EQU  4
XSTR    EQU  5
XLAST   EQU  5
;---------- Offsets of descriptors fields. ------------------------------------;
X0      EQU  0
X1      EQU  1
X2      EQU  5
X3      EQU  9
X4      EQU  13
;---------- Addends for addressing descriptors sequence. ----------------------;
XBIG    EQU  17
XSMALL  EQU  13
XDELTA  EQU  XBIG - XSMALL
;---------- ID = 0 = Terminator for list of options descriptors. --------------;
macro OPTION_END
{
DB  XEND         ; ID = 0 = Terminator for list of options descriptors
}
;---------- ID = 1 = means option is list of keywords. ------------------------;
macro OPTION_KEYS  x1, x2, x3, x4
{
DB  XKEY         ; ID = 1 = means option is list of keywords.
DD  x1 - OpDesc  ; Pointer to option long name string, 0-terminated.
DD  x2 - OpDesc  ; Pointer to option value = byte. 
DD  x3 - OpDesc  ; Pointer to option single word short name string, for detection.
DD  x4 - OpDesc  ; Pointer to list of 0-terminated keywords, 0,0 means end of list.
}
;---------- ID = 2 = means 32-bit unsigned value, interpreted as decimal. -----;
macro OPTION_DECIMAL_32  x1, x2, x3
{
DB  XDEC         ; ID = 2 = means 32-bit unsigned value, interpreted as decimal.
DD  x1 - OpDesc  ; Pointer to option long name string, 0-terminated.
DD  x2 - OpDesc  ; Pointer to option value = dword.
DD  x3 - OpDesc  ; Pointer to option single word short name string, for detection. 
}
;---------- ID = 3 = means 64-bit unsigned value, interpreted as hex. ---------;
macro OPTION_HEX_64  x1, x2, x3
{
DB  XHEX         ; ID = 3 = means 64-bit unsigned value, interpreted as hex.
DD  x1 - OpDesc  ; Pointer to option long name string, 0-terminated.
DD  x2 - OpDesc  ; Pointer to option value = qword.
DD  x3 - OpDesc  ; Pointer to option single word short name string, for detection. 
}
;---------- ID = 3 = means 64-bit unsigned value, interpreted as hex. ---------;
macro OPTION_SIZE_64  x1, x2, x3
{
DB  XSIZE        ; ID = 3 = means 64-bit unsigned value, interpreted as hex.
DD  x1 - OpDesc  ; Pointer to option long name string, 0-terminated.
DD  x2 - OpDesc  ; Pointer to option value = qword.
DD  x3 - OpDesc  ; Pointer to option single word short name string, for detection. 
}
;---------- ID = 5 = means pointer to pointer to string. ----------------------;
macro OPTION_STRING  x1, x2, x3
{
DB  XSTR         ; ID = 5 = means pointer to pointer to string.
DD  x1 - OpDesc  ; Pointer to option long name string, 0-terminated.
DD  x2 - OpDesc  ; Pointer to option value = pointer to string, 0-terminated.
DD  x3 - OpDesc  ; Pointer to option single word short name string, for detection. 
}
;---------- Support strings formatting and options strings save. --------------;
OPTION_NAME_FORMAT    EQU  29   ; Formatted output left part before " = " size.  
PATH_BUFFER_SIZE      EQU  256  ; Limit for buffers with paths, include last 0.
SEQUENCE_BUFFER_SIZE  EQU  16   ; Buffer size for color control escape sequence.


