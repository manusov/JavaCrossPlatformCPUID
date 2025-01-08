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
; OUTPUT:  RAX = OS API Status.                         ;
;          Buffer updated.                              ;
;          Local subroutine, corrupt registers.         ;
;          volatile by Linux convention.                ;
;-------------------------------------------------------;
GetTermios:
mov esi,TCGETS
EntryTermios:
push rbp
mov rbp,rsp
and rsp,0FFFFFFFFFFFFFFF0h  ; Stack alignment by calling convention.
mov rdi,[StdIn]
lea rdx,[TermiosBuffer]
mov eax,SYS_IOCTL
syscall
leave                       ; Remove stack alignment.
ret

;--- Set data from terminal control buffer by IOCTL. ---;
;                                                       ;
; INPUT:   None, but [StdIn] variable used.             ;
;                                                       ;
; OUTPUT:  RAX = OS API Status.                         ;
;          Buffer updated.                              ;
;          Local subroutine, corrupt registers.         ;
;          volatile by Linux convention.                ;
;-------------------------------------------------------;
SetTermios:
mov esi,TCSETS
jmp EntryTermios

;--- Disable terminal echo. ---------------------------;
;                                                      ;
; INPUT:   None, but [StdIn] variable used.            ;
;                                                      ;
; OUTPUT:  RAX = OS API Status.                        ;
;          ECX = Dword value, contains ECHO bit.       ;
;          Buffer updated.                             ;
;          Local subroutine, corrupt registers.        ;
;          volatile by Linux convention.               ;
;------------------------------------------------------;
DisableEcho:
call GetTermios
lea rdx,[TermiosBuffer + ECHO_OFFSET]
mov ecx,[rdx]
and dword [rdx],NOT ECHO_FLAG
push rcx
call SetTermios
pop rcx
ret

;--- Restore terminal echo. ----------------------------;
;                                                       ;
; INPUT:   ECX = Dword value, contains ECHO bit.        ;
;          [StdIn] variable used.                       ;
;                                                       ;
; OUTPUT:  RAX = OS API Status.                         ;
;          Buffer updated.                              ;
;          Local subroutine, corrupt registers.         ;
;          volatile by Linux convention.                ;
;-------------------------------------------------------;
RestoreEcho:
push rcx
call GetTermios
pop rcx
lea rdx,[TermiosBuffer + ECHO_OFFSET]
mov [rdx],ecx
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

