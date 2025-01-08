;------------------------------------------------------------------------------;
;                                                                              ;
;          This directory contains helpers library for debug support.          ;
;            This file contains globally used source definitions.              ;
;                                                                              ;
;------------------------------------------------------------------------------;

;---------- Required 48 Kilobytes miscellaneous buffer. -----------------------;
TEMP_BUFFER_SIZE            EQU  48 * 1024
;---------- Definitions for color console support, color masks. ---------------;
CLEAR_FOREGROUND            EQU  0FFFFFFF0h
CLEAR_BACKGROUND            EQU  0FFFFFF0Fh
SELECT_FOREGROUND           EQU  00000000Fh
SELECT_BACKGROUND           EQU  0000000F0h
;---------- Color values. -----------------------------------------------------;
FOREGROUND_BLUE             EQU  01h
FOREGROUND_GREEN            EQU  02h
FOREGROUND_RED              EQU  04h
FOREGROUND_INTENSITY        EQU  08h
BACKGROUND_BLUE             EQU  010h
BACKGROUND_GREEN            EQU  020h
BACKGROUND_RED              EQU  040h
BACKGROUND_INTENSITY        EQU  080h
COMMON_LVB_LEADING_BYTE     EQU  0100h
COMMON_LVB_TRAILING_BYTE    EQU  0200h
COMMON_LVB_GRID_HORIZONTAL  EQU  0400h
COMMON_LVB_GRID_LVERTICAL   EQU  0800h
COMMON_LVB_GRID_RVERTICAL   EQU  01000h
COMMON_LVB_REVERSE_VIDEO    EQU  04000h
COMMON_LVB_UNDERSCORE       EQU  08000h
;---------- Console data structures definition, char coordinates. -------------;
struct COORD
x dw  ?
y dw  ?
ends
;---------- Rectangle corners coordinates. ------------------------------------;
struct SMALL_RECT
Left   dw  ?
Top    dw  ?
Right  dw  ?
Bottom dw  ?
ends
;---------- Console screen buffer information. --------------------------------;
struct CONSOLE_SCREEN_BUFFER_INFO
dwSize               COORD       ; Buffer size at chars rows and columns.
dwCursorPosition     COORD       ; Coordinates (position) of cursor at buffer.
wAttributes          dd  ?       ; Attributes of chars.
srWindow             SMALL_RECT  ; Coordinates up-left and down-right corner of buffers.
dwMaximumWindowSize  COORD       ; Maximum sizes of console window.
ends
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
DB  XEND   ; ID = 0 = Terminator for list of options descriptors.
}
;---------- ID = 1 = means option is list of keywords. ------------------------;
macro OPTION_KEYS  x1, x2, x3, x4
{
DB  XKEY   ; ID = 1 = means option is list of keywords.
DD  x1     ; Pointer to option long name string, 0-terminated.
DD  x2     ; Pointer to option value = byte. 
DD  x3     ; Pointer to option single word short name string, for detection.
DD  x4     ; Pointer to list of 0-terminated keywords, 0,0 means end of list. 
}
;---------- ID = 2 = means 32-bit unsigned value, interpreted as decimal.------;
macro OPTION_DECIMAL_32  x1, x2, x3
{
DB  XDEC   ; ID = 2 = means 32-bit unsigned value, interpreted as decimal.
DD  x1     ; Pointer to option long name string, 0-terminated.
DD  x2     ; Pointer to option value = dword.
DD  x3     ; Pointer to option single word short name string, for detection. 
}
;---------- ID = 3 = means 64-bit unsigned value, interpreted as hex. ---------;
macro OPTION_HEX_64  x1, x2, x3
{
DB  XHEX   ; ID = 3 = means 64-bit unsigned value, interpreted as hex.
DD  x1     ; Pointer to option long name string, 0-terminated.
DD  x2     ; Pointer to option value = qword.
DD  x3     ; Pointer to option single word short name string, for detection. 
}
;---------- ID = 3 = means 64-bit unsigned value, interpreted as hex. ---------;
macro OPTION_SIZE_64  x1, x2, x3
{
DB  XSIZE  ; ID = 3 = means 64-bit unsigned value, interpreted as hex.
DD  x1     ; Pointer to option long name string, 0-terminated.
DD  x2     ; Pointer to option value = qword.
DD  x3     ; Pointer to option single word short name string, for detection. 
}
;---------- ID = 5 = means pointer to pointer to string. ----------------------;
macro OPTION_STRING  x1, x2, x3
{
DB  XSTR   ; ID = 5 = means pointer to pointer to string.
DD  x1     ; Pointer to option long name string, 0-terminated.
DD  x2     ; Pointer to option value = pointer to string, 0-terminated.
DD  x3     ; Pointer to option single word short name string, for detection. 
}
;---------- Support strings formatting and options strings save. --------------;
OPTION_NAME_FORMAT    EQU  29   ; Formatted output left part before " = " size.  
PATH_BUFFER_SIZE      EQU  256  ; Limit for buffers with paths, include last 0.
;---------- Aliases for compact access to variables. --------------------------;
; Update this required if change variables layout at connect_var.inc.
ALIAS_STDIN           EQU  [ebx + 4*00]
ALIAS_STDOUT          EQU  [ebx + 4*01]
ALIAS_REPORTNAME      EQU  [ebx + 4*02]
ALIAS_REPORTHANDLE    EQU  [ebx + 4*03]
ALIAS_SCENARIOHANDLE  EQU  [ebx + 4*04] 
ALIAS_SCENARIOBASE    EQU  [ebx + 4*05]
ALIAS_SCENARIOSIZE    EQU  [ebx + 4*06] 
ALIAS_COMMANDLINE     EQU  [ebx + 4*07]
;--- This 3 variables must be continuous for return status from subroutines. --;
ALIAS_ERROR_STATUS    EQU  [ebx + 4*08]    
ALIAS_ERROR_P1        EQU  [ebx + 4*08]  ; Alias of previous.
ALIAS_ERROR_P2        EQU  [ebx + 4*09]
ALIAS_ERROR_C         EQU  [ebx + 4*10]
;---------- Registers and memory dump subroutines support. --------------------;
; Global used data definitions.
REGISTER_NAME_COLOR   EQU  FOREGROUND_RED + FOREGROUND_GREEN + FOREGROUND_INTENSITY
REGISTER_VALUE_COLOR  EQU  FOREGROUND_RED + FOREGROUND_GREEN + FOREGROUND_BLUE + FOREGROUND_INTENSITY
DUMP_ADDRESS_COLOR    EQU  FOREGROUND_GREEN + FOREGROUND_INTENSITY
DUMP_DATA_COLOR       EQU  FOREGROUND_RED + FOREGROUND_GREEN + FOREGROUND_BLUE + FOREGROUND_INTENSITY  
;---------- Constants for keyboard input check. -------------------------------;
BLANK_KEY             EQU  00h
ENTER_KEY             EQU  0Dh 

;---------- See MSDN info about this constants and structures. ----------------; 

DISABLE_ECHO_ALL      EQU  0F9h
KEY_EVENT             EQU  1

;typedef struct _INPUT_RECORD {
;    WORD EventType;
;    union {
;        KEY_EVENT_RECORD KeyEvent;
;        MOUSE_EVENT_RECORD MouseEvent;
;        WINDOW_BUFFER_SIZE_RECORD WindowBufferSizeEvent;
;        MENU_EVENT_RECORD MenuEvent;
;        FOCUS_EVENT_RECORD FocusEvent;
;    } Event;
;} INPUT_RECORD, *PINPUT_RECORD;
;
;
;typedef struct _KEY_EVENT_RECORD {
;    BOOL bKeyDown;
;    WORD wRepeatCount;
;    WORD wVirtualKeyCode;
;    WORD wVirtualScanCode;
;    union {
;        WCHAR UnicodeChar;
;        CHAR   AsciiChar;
;    } uChar;
;    DWORD dwControlKeyState;
;} KEY_EVENT_RECORD, *PKEY_EVENT_RECORD;

struct INPUT_RECORD_KEY_EVENT
EventType         dw  ?
Reserved1         dw  ?
bKeyDown          dd  ?
wRepeatCount      dw  ?
wVirtualKeyCode   dw  ?
wVirtualScanCode  dw  ?
UnicodeChar       dw  ?
dwControlKeyState dd  ?
ends
