;------------------------------------------------------------------------------;
;                                                                              ;
;          This directory contains target functionality under debug.           ; 
;               This file connects include files with subroutines.             ; 
;                                                                              ;
;------------------------------------------------------------------------------;

;---------- Check options from INPUT.TXT and run requested handlers. -----;
;                                                                         ;
; INPUT:   None.                                                          ; 
; OUTPUT:  None.                                                          ;
;-------------------------------------------------------------------------;
ScenarioRunner:
push esi edi
cld
lea esi,[Handlers_Pointers]  ; ESI = Pointer to procedures pointers array.
lea edi,[Handlers_Map]       ; EDI = Pointer to bitmap: enabled / disabled.
.optionsCycle:
lodsd
test eax,eax
jz .optionsDone         ; Go exit if end of list: indicated by pointer = 0. 
cmp byte [edi],0
je .optionSkip          ; Go skip if this option disabled.
call eax
.optionSkip:
inc edi
jmp .optionsCycle
.optionsDone:
pop edi esi
ret

;------------------------------------------------------------------------------;
;                                Handlers.                                     ;
;------------------------------------------------------------------------------;

include 'DumpGPR32.inc'
include 'DumpSelectors.inc'
include 'DumpFPU.inc'
include 'DumpMMX.inc'
include 'DumpSSE.inc'
include 'DumpAVX256.inc'
include 'DumpAVX512.inc'
include 'DumpPredicates.inc'
include 'DumpMemory.inc'

include 'UnitWoW64.inc'
include 'UnitCPUID.inc'
include 'UnitSubFnc.inc'
include 'UnitAffCPUID.inc'
include 'UnitAffSubFnc.inc'
include 'UnitOsContext.inc'
include 'UnitTSC.inc'
include 'UnitAffinity.inc'
include 'UnitTopology.inc'
include 'UnitExTopology.inc'

include 'UnitInit.inc'
