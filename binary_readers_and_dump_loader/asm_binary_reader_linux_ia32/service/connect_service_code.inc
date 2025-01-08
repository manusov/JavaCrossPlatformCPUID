;------------------------------------------------------------------------------;
;                                                                              ;
;          This directory contains helpers library for debug support.          ; 
;               This file connects include files with subroutines.             ; 
;                                                                              ;
;------------------------------------------------------------------------------;

;--- Get data to terminal control buffer by IOCTL. -----;
;                                                       ;
; INPUT:   None, but [StdIn] variable used.             ;
;                                                       ;
; OUTPUT:  EAX = OS API Status.                         ;
;          Buffer updated.                              ;
;          Local subroutine, corrupt registers.         ;
;          volatile by Linux convention.                ;
;-------------------------------------------------------;
GetTermios:
mov ecx,TCGETS
EntryTermios:
mov ebx,[StdIn]
lea edx,[TermiosBuffer]
mov eax,SYS_IOCTL
int 80h
ret

;--- Set data from terminal control buffer by IOCTL. ---;
;                                                       ;
; INPUT:   None, but [StdIn] variable used.             ;
;                                                       ;
; OUTPUT:  EAX = OS API Status.                         ;
;          Buffer updated.                              ;
;          Local subroutine, corrupt registers.         ;
;          volatile by Linux convention.                ;
;-------------------------------------------------------;
SetTermios:
mov ecx,TCSETS
jmp EntryTermios

;--- Disable terminal echo. ---------------------------;
;                                                      ;
; INPUT:   None, but [StdIn] variable used.            ;
;                                                      ;
; OUTPUT:  EAX = OS API Status.                        ;
;          ECX = Dword value, contains ECHO bit.       ;
;          Buffer updated.                             ;
;          Local subroutine, corrupt registers.        ;
;          volatile by Linux convention.               ;
;------------------------------------------------------;
DisableEcho:
call GetTermios
lea edx,[TermiosBuffer + ECHO_OFFSET]
mov ecx,[edx]
and dword [edx],NOT ECHO_FLAG
push ecx
call SetTermios
pop ecx
ret

;--- Restore terminal echo. ----------------------------;
;                                                       ;
; INPUT:   ECX = Dword value, contains ECHO bit.        ;
;          [StdIn] variable used.                       ;
;                                                       ;
; OUTPUT:  EAX = OS API Status.                         ;
;          Buffer updated.                              ;
;          Local subroutine, corrupt registers.         ;
;          volatile by Linux convention.                ;
;-------------------------------------------------------;
RestoreEcho:
push ecx
call GetTermios
pop ecx
lea edx,[TermiosBuffer + ECHO_OFFSET]
mov [edx],ecx
call SetTermios
ret

include 'StringWrite.inc'
include 'HexPrint.inc'
include 'DecimalPrint32.inc'
include 'DoublePrint.inc'
include 'SizePrint64.inc'
include 'GetSetColor.inc'
include 'ConsoleWrite.inc'
include 'CreateReport.inc'
include 'ReadScenario.inc'
include 'ParseScenario.inc'
include 'ShowScenario.inc'
include 'ShowError.inc'
include 'DecodeError.inc'
include 'ConsoleReadChar.inc'
include 'ConsoleReadAnyEvent.inc'

