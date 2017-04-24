;------------------------------------------------------------------------------;
; Part of PAL (Platform Abstraction Layer) for Windows IA32                    ;
; Main module for JNI DLL (Java Native Interface Dynamical Load Library)       ;
; Note Kernel Mode Support functions is reserved, yet not used at CPUID v0.5x. ;
;------------------------------------------------------------------------------;

include 'win32a.inc'
format PE GUI 4.0 DLL
entry DllMain

;---------- Code section -------------------------------------------------------
section '.text' code readable executable

DllMain:        ; This called by Operating System when load/unload DLL
mov eax,1       ; Return status to OS caller (actual when load)
ret
                ; This entry for debug native call mechanism,
checkPAL:       ; also differentiate between Win32 and Win32/WOW64
;--- Detect WOW64 ---
push ebx
push LibName
call [GetModuleHandle]
test eax,eax
jz @f
push FncName eax
call [GetProcAddress]
test eax,eax
jz @f
xchg ebx,eax
call [GetCurrentProcess]
push 0
mov ecx,esp
push ecx eax
call ebx
pop eax
@@:
pop ebx
;--- Send result ---
test eax,eax
mov eax,32
jz @f
inc eax
@@:
ret

;--- Entry point for PAL services, Java Native Interface (JNI) ---
; Parm#1 = [ESP+04] = JNI Environment  
; Parm#2 = [ESP+08] = JNI This Object reference (not used by this routine)
; Parm#3 = [ESP+12] = Object IPB array of qwords (long) reference or NULL
; Parm#4 = [ESP+16] = Object OPB array of qwords (long) reference or NULL
; Parm#5 = [ESP+20] = IPB size, qwords, or function code if IPB=NULL
; Parm#6 = [ESP+24] = OPB size, qwords, or reserved if OPB=NULL
; Return = EAX = JNI Status, 0=Error, 1=IA32 OK, 2=x64 OK
; Remember about 6*4=24 bytes must be removed from stack when return (RET 24),
; because required by IA32 calling convention.

entryPAL:
push ebx esi edi ebp                   ; Save non-volatile registers
xor eax,eax
push eax eax                           ; Reserve space for variables
mov ebp,esp                            ; EBP=Frame, parm #1 at [ebp+28]
xor esi,esi                            ; Pre-blank IPB pointer
xor edi,edi                            ; Pre-blank OPB pointer
;--- Check IPB presence ---
mov ecx,[ebp+36]                       ; ECX = IPB object
jecxz @f                               ; Go skip IPB extraction if IPB=null
mov ebx,[ebp+28]                       ; EBX = environment
mov eax,[ebx]                          ; EAX = Pointer to functions table
push ebp ecx ebx  
;--- Get IPB, parms: env, IPB Object, Pointer to flag ---
call dword [eax+188*4]                 ; JNI call [GetLongArrayElements]
test eax,eax
jz StatusRet                           ; Go skip if error = NULL pointer
xchg esi,eax                           ; ESI = Pointer to IPB
@@:
;--- Check OPB presence ---
mov ecx,[ebp+40]                       ; ECX = OPB object
jecxz @f                               ; Go skip IPB extraction if OPB=null
mov ebx,[ebp+28]                       ; EBX = environment
mov eax,[ebx]                          ; EAX = Pointer to functions table
lea edx,[ebp+4]
push edx ecx ebx  
;--- Get OPB, parms: env, OPB Object, Pointer to flag ---
call dword [eax+188*4]                 ; JNI call [GetLongArrayElements]
test eax,eax
jz StatusRet                           ; Go skip if error = NULL pointer
xchg edi,eax                           ; EDI = Pointer to OPB
@@:
;--- Target operation ---
test esi,esi
jz IPB_null
;--- Handling IPB present ---
xor eax,eax
mov ecx,[esi]
cmp ecx,iFunctionCount
jae @f
call dword [iFunctionSelector+ecx*4]
@@:
;--- Return point ---
ReleaseRet:
;--- Check IPB release requirement flag and IPB presence ---
cmp dword [ebp],0
je @f                                  ; Go skip if IPB release not required
mov ecx,[ebp+36]                       ; ECX = IPB object
jecxz @f                               ; Go skip IPB release if IPB=null
mov ebx,[ebp+28]                       ; EBX = environment
mov eax,[ebx]                          ; EAX = Pointer to functions table
push 0 esi ecx ebx  
;--- Release IPB, parms: env, obj, Pointer, Release mode --- 
call dword [eax+196*4]                 ; call [ReleaseLongArrayElements]
@@:
;--- Check OPB release requirement flag and OPB presence ---
cmp dword [ebp+4],0
je @f                                  ; Go skip if OPB release not required
mov ecx,[ebp+40]                       ; EDX = OPB object
jecxz @f                               ; Go skip OPB release if OPB=null
mov ebx,[ebp+28]                       ; EBX = environment
mov eax,[ebx]                          ; EAX = Pointer to functions table
push 0 edi ecx ebx  
;--- Release OPB, parms: env, obj, Pointer, Release mode --- 
call dword [eax+196*4]                 ; call [ReleaseLongArrayElements]
@@:
;--- Return with status = EAX ---
mov eax,1                              ; RAX=1 (true) means OK from Win32 DLL 
StatusRet:                             ; Entry point with RAX=0 (error)
pop ecx ecx ebp edi esi ebx            ; Restore non-volatile registers
ret 24                                 ; Return to Java JNI service caller 
 
;--- Special fast case, no Input Parameters Block ---
IPB_null:
xor eax,eax
mov ecx,[ebp+44]
cmp ecx,FunctionCount
jae @f
call dword [FunctionSelector+ecx*4]
@@:
jmp ReleaseRet

;---------- Open Service / Install Kernel Mode Driver -------------------------;
; Input:   EDI = Pointer to OPB (Output Parameters Block)                      ;
; Output:  EAX = JNI Status: 0=Error, 1=Win32 JNI OK                           ;
;                set externally from this subroutine                           ;
;          BYTE OPB[0] = Install KMD code, 0=OK, otherwise step number         ;
;------------------------------------------------------------------------------; 
KmdOpen:
push ebx esi edi ebp
;---
; VirtualAlloc - reserve region of pages
; Parm#1 = Block starting address, 0 = address selected by function
; Parm#2 = Block size, MAX_PATH=260, reserve space for path
; Parm#3 = Allocation type, MEM_COMMIT
; Parm#4 = Protection type, PAGE_READWRITE (but not execute)
; Return = EAX = Pointer to allocated memory block, 0=Error
;---
push PAGE_READWRITE MEM_COMMIT MAX_PATH 0 
call [VirtualAlloc]
test eax,eax         ; Address=0 means error
mov bl,1             ; BL=1, step number for error reporting
jz .CloseContext     ; Go if error
mov [DrvPath],eax    ; Save address of region
;---
; GetFullPathName - get path string
; Parm#1 = DrvFileName, pointer to file name source string
; Parm#2 = Buffer Length = MAX_PATH
; Parm#3 = Buffer Pointer, destination string = allocated address
; Parm#4 = File part, pointer for return path final file component address
; Return = EAX = Returned string length, 0=Error
;---
push DrvFile eax MAX_PATH DrvFilename
call [GetFullPathName]
test eax,eax
mov bl,2
jz .CloseContext  
;---
; OpenSCManager - Open Service Control Manager
; Parm#1 = Machine Name, 0 means local machine
; Parm#2 = Database Name, 0 means opened by default
; Parm#3 = Desired access, set SC_MANAGER_ALL_ACCESS
; Return = EAX = SCM Handle, 0=Error
;---
push SC_MANAGER_ALL_ACCESS 0 0 
call [OpenSCManager]
test eax,eax         ; Handle=0 means error
mov bl,3
jz .CloseContext     ; Go if error
mov [Manager],eax    ; Save handle
;---
; OpenService - Opens an existing service
; Parm#1 = Database handle
; Parm#2 = Pointer to service name
; Parm#3 = Desired access, set SERVICE_ALL_ACCESS
; Return = EAX = Service Handle, 0=Error
;---        
push SERVICE_ALL_ACCESS Drvname eax
call [OpenService]
test eax,eax         ; Handle=0 means error 
jnz @F               ; Go skip create if no errors
;---
; CreateService - Create the service object and add to database
; Parm#1 = SC_HANDLE, handle to the service control manager database
; Parm#2 = Service Name
; Parm#3 = User interface display name
; Parm#4 = Desired access = SERVICE_ALL_ACCESS
; Parm#5 = Service type = SERVICE_KERNEL_DRIVER
; Parm#6 = Start Type = SERVICE_DEMAND_START
; Parm#7 = Error Control = SERVICE_ERROR_NORMAL
; Parm#8 = Binary Path Name = [DrvPath]
; Parm#9 = Load Order Group, 0=None
; Parm#10 = Tag in the Load Order Group, 0=None
; Parm#11 = Load Orderign Groups dependences, 0=None
; Parm#12 = Service Start Name, 0=None
; Parm#13 = Password, 0=None 
; Return = EAX = Service Handle, 0=Error
;---
push 0 0 0 0 0 [DrvPath] \
     SERVICE_ERROR_NORMAL SERVICE_DEMAND_START \
     SERVICE_KERNEL_DRIVER SERVICE_ALL_ACCESS \
     Drvname Drvname [Manager]
call [CreateService]
test eax,eax           ; Handle=0 means error
mov bl,4
jz .CloseContext       ; Go if error
@@:
mov [Service],eax      ; Save service handle
;---
; QueryServiceStatus - query service status
; Parm#1 = Service Handle
; Parm#2 = Pointer to SERVICE_STATUS structure, require service info
; Return = EAX = Status, nonzero if OK, 0=Error
;--- 
push Status eax
call [QueryServiceStatus] 
test eax,eax           ; Return value = 0 means error
mov bl,5
jz .CloseContext       ; Go if error
cmp [Status + SERVICE_STATUS.dwCurrentState], SERVICE_RUNNING
je @F                  ; Go skip run if already running
;---
; StartService - service run
; Parm#1 = Service Handle
; Parm#2 = Number of Service Arguments, 0 means none
; Parm 3 = Service Arguments strings
; Return = EAX = Status, nonzero if OK, 0=Error
;---
push Vectors 0 [Service]
call [StartService] 
test eax,eax
;---
; jnz @F                    ; Go skip error visual if run ok
;---
; jmp .CloseContext
;---
mov bl,6
jz .CloseContext 
;---
; CreateFile - use create file operation for initializing service
; Parm#1 = File Name, at this context Device Name
; Parm#2 = Desired Access, requested GENERIC_WRITE + GENERIC_READ
; Parm#3 = Share Mode, requested FILE_SHARE_READ + FILE_SHARE_WRITE
; Parm#4 = Security attribute, 0 means none
; Parm#5 = Creation Disposition, for devices (non-files) use OPEN_EXISTING
; Parm#6 = Flags and Attributes, 0 means none
; Parm#7 = Template File, 0 means none
; Return = EAX = Device Handle, 0=Error
;---
@@:
push 0 0 OPEN_EXISTING 0 \ 
     FILE_SHARE_READ or FILE_SHARE_WRITE \
     GENERIC_WRITE or GENERIC_READ \
     DrvDevice  
call [CreateFile]
cmp eax,INVALID_HANDLE_VALUE
mov bl,7
je .CloseContext      ; Go exit if error
;---
test eax,eax
je .CloseContext      ; Go exit if error 
;---
mov [Driver], eax     ; Save handle
;--- Set status if OK ---
mov bl,0              ; BL=0, means driver installation OK
;--- Exit ---
.CloseContext:
mov al,bl
pop ebp edi esi ebx
mov [edi],al          ; Write status or failed step number 
ret 

;---------- Close Service / Uninstall Kernel Mode Driver ----------------------;
; Input:   EDI = Pointer to OPB (Output Parameters Block)                      ;
; Output:  EAX = JNI Status: 0=Error, 1=Win32 JNI OK                           ;
;                set externally from this subroutine                           ;
;          BYTE OPB[0] = Uninstall KMD code, 0=OK, otherwise step number       ;
;------------------------------------------------------------------------------; 
KmdClose:
;---
; CloseHandle - close driver handle
; Parm#1 = Handle for close
; Return = EAX = Status, nonzero if OK, 0=Error
;---
push ebx esi edi ebp
mov bl,0                  ; BL = failed step number or 0=OK (yet unused)
mov edi,[Driver]
cmp edi,INVALID_HANDLE_VALUE
je @F
push edi
call [CloseHandle]
;----
; ControlService - control for driver and device,
; same for DeleteService, CloseServiceHandle
; Parm#1 = Handle of service
; Parm#2 = Control value = SERVICE_CONTROL_STOP
; Parm#3 = Pointer to Service Status structure
; Return = EAX = Status, nonzero if OK, 0=Error
;---
@@:
mov esi,[Service]
test esi,esi
je @F
push Status SERVICE_CONTROL_STOP esi 
call [ControlService]
;- Note eax=status ignored, eax=0 means error
push esi
call [DeleteService]
;- Same note eax=status ignored
push esi
call [CloseServiceHandle]
;- Same note eax=status ignored
@@:
;---
; Same operation for Manager
;---
mov edi,[Manager]
test edi,edi
jz @F
push edi
call [CloseServiceHandle]
@@:
mov edi,[DrvPath]       ; EDI = Base address
test edi,edi
jz @F
;---
; VirtualFree - free memory, allocated at program start
; Parm#1 = Address, pointer to base address of region
; Parm#2 = Size of the region
; Parm#3 = Type of memory free operation
; Return = EAX = Status, nonzero if OK, 0=Error
;---
push MEM_RELEASE 0 edi
call [VirtualFree]
@@:
;--- Exit ---
mov al,bl
pop ebp edi esi ebx
mov [edi],al
ret


;---------- Execute request to Kernel Mode Driver -----------------------------;
; Input:   ESI = Pointer to IPB (Input Parameters Block)                       ;
;                DWORD [ESI+00] = Function code, decoded externally            ;
;                DWORD [ESI+04] = Device request                               ;
;                QWORD [ESI+08] = Block base address (request specific)        ;
;                QWORD [ESI+16] = Block length (request specific)              ;
;          EDI = Pointer to OPB (Output Parameters Block)                      ;
; Output:  EAX = JNI Status: 0=Error, 1=Win32 JNI OK                           ;
;                set externally from this subroutine                           ;
;          BYTE OPB[0] = Execution KMD code, 0=OK, otherwise step number       ;
;          ... reserved 7 bytes                                                ;
;          OPB[8] = Output buffer                                              ;
;------------------------------------------------------------------------------; 
KmdTransaction:
;- JMP DEBUG1
;push ebx esi edi ebp
;---
; Built QUERY request description block and request target operation
; WriteFile - use write file operation for execute target operation - I/O
; Parm#1 = Handle of File or I/O Device
; Parm#2 = Pointer to Buffer, data write to file or transmit to I/O device
; Parm#3 = Number of Bytes to Write, boffer size
; Parm#4 = Pointer to returned: Number of Bytes Written
; Parm#5 = Pointer to OVERLAPPED structure, 0 means none
; Return = RAX = Status, nonzero if OK, 0=Error
;---
;--- DEBUG FRAGMENT (CODE IGNORED BY DEBUG SAMPLE SYS) read 8-bit port 0061h ---
;mov eax, RZ_DRIVER_QUERY_PORT_IN_BYTE
;mov dword [Query + RZDriverQuery.IOCODE],eax
;mov eax,0061h
;mov dword [Query + RZDriverQuery.paramA],eax
;push 0 Bytes sizeof.RZDriverQuery Query [Driver] 
;call [WriteFile] 
;mov eax, dword [Query + RZDriverQuery.RESULT]
;-- Here view EAX.[7-0] = Byte from port 0061h
;- INT3  ; *** DEBUG ***
;--- End of debug fragment ---
;pop ebp edi esi ebx
;and eax,0FFFFh
;mov dword [edi+0 + 8],eax
;mov dword [edi+4 + 8],0
;ret
;--- DEBUG VARIANT WITH CALL KMD64 FROM JNI32, MEANS JVM32 UNDER WIN64 ---------
;DEBUG1:

;--- Prepare request parameters ---
push ebx esi edi ebp

mov eax,[esi+04]   ; EAX  = Driver request code
mov ebx,[esi+08]   ; EBX  = Source base address
mov ecx,[esi+16]   ; ECX = Length
lea edx,[edi+08]   ; EDX = Destination base address  

; REQUIRED OPTIMIZING, BASE-INDEX ADDRESSING

;--- Built and execute request ---
mov dword [Query + RZDriverQuery1.IOCODE]  , eax  ; 00000040h ; R0_DRIVER_MMIO
mov dword [Query + RZDriverQuery1.IODATA]  , 0
mov dword [Query + RZDriverQuery1.SRC]     , ebx  ; 0C0000h
mov dword [Query + RZDriverQuery1.SRC+4]   , 0
mov dword [Query + RZDriverQuery1.DST]     , edx  ; edi
mov dword [Query + RZDriverQuery1.DST+4]   , 0
mov dword [Query + RZDriverQuery1.BYTES]   , ecx  ; 4
mov dword [Query + RZDriverQuery1.BYTES+4] , 0
push 0  Bytes  sizeof.RZDriverQuery1  Query  [Driver]         ; PARMS # [5...1]
call [WriteFile] 
; mov eax, dword [Query + RZDriverQuery1.RESULT]
pop ebp edi esi ebx
;and eax,0FFFFh
;mov dword [edi+0],eax
;mov dword [edi+4],0
;---
test eax,eax
mov al,8
jz @f
mov al,0
@@:
mov byte [edi],al
;---
ret


;---------- Get Driver file name ----------------------------------------------;
; Parm#1 = [ESP+4] = Pointer to buffer for return file name string             ;
;          Porting fixed to ECX                                                ;
; Output = EAX = Status: 0=Error, Non-Zero=OK                                  ;
;          [buffer] = File name returned                                       ;
;------------------------------------------------------------------------------; 
KmdGetFile:
;--- Porting fixup ---
lea ecx,[edi+8]
;--- End of porting fixup ---
push ebx
lea edx,[DrvFilename]
mov ebx,1000
.L0:
mov al,[edx]
mov [ecx],al
inc edx
inc ecx
test al,al
jz .L1
dec ebx
jnz .L0
mov [ecx],bl
.L1:
pop ebx
ret


;---------- Set Driver file name ----------------------------------------------;
; Parm#1 = [ESP+4] = Pointer to buffer with file name string                   ;
;          Porting fixed to ECX                                                ;
; Output = RAX = Status: 0=Error, Non-Zero=OK                                  ;
;------------------------------------------------------------------------------; 
KmdSetFile:
;--- Porting fixup ---
lea ecx,[esi+8]
;--- End of porting fixup ---
push ebx
lea edx,[DrvFilename]
mov ebx,1000
.L0:
mov al,[ecx]
mov [edx],al
inc ecx
inc edx
test al,al
jz .L1
dec ebx
jnz .L0
mov [edx],bl
.L1:
pop ebx
ret

;---------- Get CPUID dump ----------------------------------------------------;
; Parm#1 = EDI = Pointer to buffer for status and dump data                    ;
; Output = EAX = Status: 0=Error, Non-Zero=OK, set external. at transit caller ;
;          Buffer DWORD[0] = Number of entries returned                        ;
;          Bytes [4-31] = Reserved for alignment                               ;
;          Bytes [32-16383] = Buffer, maximum (16384-32)/32 = 511 entries ret. ;  
;------------------------------------------------------------------------------;
GetCPUID:
push edi                 ; (+1)
cld
mov ecx,8
xor eax,eax
rep stosd
push edi                 ; Parm#1
call Internal_GetCPUID
pop edi                  ; (-1)
mov [edi],eax
ret

ENTRIES_LIMIT = 511    ; Maximum number of output buffer 16352 bytes = 511*32
;---------- Target subroutine -------------------------------------------------;
; INPUT:  Parameter#1 = [esp+4] = Pointer to output buffer
; OUTPUT: RAX = Number of output entries
;         Output buffer updated
;---
; Output buffer maximum size is 16352 bytes, 511 entries * 32 bytes
; Each entry is 32 bytes, 8 dwords:
; dword   offset in entry(hex)   comments
;--------------------------------------------------------------------------
;   0     00-03                  Information type tag, 0 for CPUID info                  
;   1     04-07                  CPUID function number
;   2     08-0B                  CPUID subfunction number
;   3     0C-0F                  CPUID pass number (as for function #2)
;   4     10-13                  Result EAX after CPUID
;   5     14-17                  Result EBX after CPUID
;   6     18-1B                  Result ECX after CPUID
;   7     1C-1F                  Result EDX after CPUID
;---
Internal_GetCPUID:
;---------- Initializing ------------------------------------------------------;

temp_r8   EQU  dword [ebp+00]  ; this for porting from x64 code
temp_r9   EQU  dword [ebp+04]
temp_r10  EQU  dword [ebp+08]
temp_ebp  EQU  dword [ebp+12]

cld
;--- Store registers ---
push ebx ebp esi edi
;--- Variables pool ---
xor eax,eax
push eax eax eax eax
mov ebp,esp
;--- Start ---
mov edi,[esp+32+4]
mov temp_ebp,0            ; xor ebp,ebp ; EBP = Global output entries counter
;---------- Check for ID bit writeable for "1" --------------------------------;
;mov ebx,21
;pushf                     ; In the 64-bit mode, push RFLAGS
;pop eax
;bts eax,ebx               ; Set EAX.21=1
;push eax
;popf                      ; Load RFLAGS with RFLAGS.21=1
;pushf                     ; Store RFLAGS
;pop eax                   ; Load RFLAGS to RAX
;btr eax,ebx               ; Check EAX.21=1, Set EAX.21=0
;jnc NoCpuId               ; Go error branch if cannot set EFLAGS.21=1
;---------- Check for ID bit writeable for "0" --------------------------------;
;push eax
;popf                      ; Load RFLAGS with RFLAGS.21=0
;pushf                     ; Store RFLAGS
;pop eax                   ; Load RFLAGS to RAX
;btr eax,ebx               ; Check EAX.21=0
;jc NoCpuId                ; Go if cannot set EFLAGS.21=0

call CheckCPUID            ; Return CF=Error flag, EAX=Maximum standard function
jc NoCpuId

;---------- Get standard CPUID results ----------------------------------------;
mov temp_r9,0             ; xor r9d,r9d  ; R9D  = standard functions start
;xor eax,eax
;cpuid
cmp eax,ENTRIES_LIMIT/2   ; EAX = maximum supported standard function number
ja ErrorCpuId             ; Go if invalid limit
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow at subfunction
;---------- Get virtual CPUID results -----------------------------------------;
mov temp_r9,40000000h     ; R9D = virtual functions start
mov eax,temp_r9           ; EAX = Function
xor ecx,ecx               ; ECX = Subfunction
cpuid
and eax,0FFFFFF00h
cmp eax,040000000h
jne NoVirtual             ; Skip virtual CPUID if not supported
mov eax,temp_r9           ; EAX = Limit, yet 1 function 40000000h
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow at subfunction
NoVirtual:
;---------- Get extended CPUID results ----------------------------------------;
mov temp_r9,80000000h     ; mov r9d,80000000h ; R9D  = extended functions start
mov eax,temp_r9           ; r9d
cpuid
cmp eax,80000000h + ENTRIES_LIMIT/2  ; EAX = maximum extended function number
ja ErrorCpuId                        ; Go if invalid limit
call SequenceCpuId
jc ErrorCpuId                        ; Exit if output buffer overflow
;---------- Return points -----------------------------------------------------;
; xchg eax,ebp            ; Normal exit point, return EAX = number of entries
mov eax,temp_ebp
;-

ExitCpuId:
add esp,16
pop edi esi ebp ebx
ret 4
NoCpuId:                  ; Exit for CPUID not supported, RAX=0  
xor eax,eax
jmp ExitCpuId
ErrorCpuId:               ; Exit for CPUID error, RAX=-1=FFFFFFFFFFFFFFFFh
mov eax,-1
jmp ExitCpuId 
;---------- Subroutine, sequence of CPUID functions ---------------------------;
; INPUT:  R9D = Start CPUID function number
;         EAX = Limit CPUID function number (inclusive)
;         RDI = Pointer to memory buffer
; OUTPUT: RDI = Modified by store CPUID input parms + output parms entry
;         Flags condition code: Carry (C) = means entries count limit
;---
SequenceCpuId:
mov temp_r10,eax      ; mov r10d,eax ; R10D = standard or extended functions limit 
CycleCpuId:
;--- Specific handling for functions with subfunctions ---
mov eax,temp_r9       ; r9d ; EAX = function number, input at R9D
cmp eax,04h
je Function04
cmp eax,07h
je Function07
cmp eax,0Bh
je Function0B
cmp eax,0Dh
je Function0D
cmp eax,0Fh
je Function0F
cmp eax,10h
je Function10
cmp eax,14h
je Function14
cmp eax,8000001Dh
je Function04
;--- Default handling for functions without subfunctions ---
xor esi,esi               ; ESI = sub-function number for CPUID
xor ecx,ecx               ; ECX = sub-function number for save entry 
call StoreCpuId
ja OverSubFunction
AfterSubFunction:         ; Return point after sub-function specific handler

;- inc r9d
;- cmp r9d,r10d
mov eax,temp_r9
inc eax
mov temp_r9,eax
cmp eax,temp_r10
;---

jbe CycleCpuId            ; Cycle for CPUID standard functions
ret
OverSubFunction:
stc
ret 
;---------- Subroutine, one CPUID function execution --------------------------;
; INPUT:  EAX = CPUID function number
;         R9D = EAX (R8-R15 emulated in memory, because port from x64)
;         ECX = CPUID subfunction number
;         ESI = ECX
;         RDI = Pointer to memory buffer
; OUTPUT: RDI = Modified by store CPUID input parms + output parms entry
;         Flags condition code: Above (A) = means entries count limit
;---
StoreCpuId:
cpuid
StoreCpuId_Entry:     ; Entry point for CPUID results (EAX,EBX,ECX,EDX) ready 
push eax
xor eax,eax
stosd                 ; Store tag dword[0] = Information type
mov eax,temp_r9       ; r9d
stosd                 ; Store argument dword [1] = CPUID function number 
mov eax,esi
stosd                 ; Store argument dword [2] = CPUID sub-function number
xor eax,eax
stosd                 ; Store argument dword [3] = CPUID pass number (see fn.2)
pop eax
stosd                 ; Store result dword [4] = output EAX 
xchg eax,ebx
stosd                 ; Store result dword [5] = output EBX
xchg eax,ecx
stosd                 ; Store result dword [6] = output ECX
xchg eax,edx
stosd                 ; Store result dword [7] = output EDX
inc temp_ebp          ; ebp ; Global counter +1
cmp temp_ebp,ENTRIES_LIMIT  ; ebp ; Limit for number of output entries
ret
;---------- CPUID function 04h = Deterministic cache parameters ---------------;
Function04:
xor esi,esi           ; ESI = Storage for sub-function number
.L0:
mov eax,temp_r9       ; r9d ; EAX = function number
mov ecx,esi           ; ECX = subfunction number
cpuid
test al,00011111b     ; Check for subfunction list end
jz AfterSubFunction   ; Go if reach first not valid subfunction
call StoreCpuId_Entry
ja OverSubFunction    ; Go if output buffer overflow
inc esi               ; Sunfunctions number +1
jmp .L0               ; Go repeat for next subfunction
;---------- CPUID function 07h = Structured extended feature flags ------------;   
Function07:
xor esi,esi           ; ESI = Storage for sub-function number
mov ecx,esi
mov eax,temp_r9       ; r9d ; EAX = function number (BUGGY DUPLICATED)
cpuid
mov temp_r8,eax       ; r8d,eax ; R8D = Maximal sub-function number
.L0:
mov eax,temp_r9       ; r9d
mov ecx,esi           ; ECX = Current sub-function number
call StoreCpuId
ja OverSubFunction    ; Go if output buffer overflow
inc esi               ; Sunfunctions number +1
cmp esi,temp_r8       ; r8d 
jbe .L0               ; Go cycle if next sub-function exist
jmp AfterSubFunction
;---------- CPUID function 0Bh = Extended topology enumeration ----------------;
Function0B:
xor esi,esi           ; ESI = Storage for sub-function number
.L0:
mov eax,temp_r9       ; r9d ; EAX = function number
mov ecx,esi           ; ECX = subfunction number
cpuid
test eax,eax          ; Check for subfunction list end
jz AfterSubFunction   ; Go if reach first not valid subfunction
call StoreCpuId_Entry
ja OverSubFunction    ; Go if output buffer overflow
inc esi               ; Sunfunctions number +1
jmp .L0               ; Go repeat for next subfunction
;---------- CPUID function 0Dh = Processor extended state enumeration ---------;
Function0D:
;---
;mov eax,temp_r9       ; r9d ; EAX = function number
;xor ecx,ecx           ; ECX = sub-function number
;cpuid
;mov temp_r8,0         ; xor r8d,r8d ; R8D = counter for subfunctions, this also make CF=0(NC)
;mov ecx,63            ; ECX = Maximum number of subfunctions
;.L0:                  ; Cycle for count number of ones in bitfield [63-01]
;rcl eax,1             ; Bitfield is EDX[31-0] + EAX[31-1]
;rcl edx,1
;jnc .L1
;inc r8d               ; R8D = Maximal sub-function number, count ones at bitmap
;.L1:
;loop .L0
;xor esi,esi           ; ESI = Storage for sub-function number
;.L2:
;mov eax,temp_r9       ; r9d
;mov ecx,esi           ; ECX = Sub-function number
;call StoreCpuId
;ja OverSubFunction    ; Go if output buffer overflow
;inc esi               ; Sunfunctions number +1
;cmp esi,temp_r8       ; r8d 
;jbe .L2               ; Go cycle if next sub-function exist
;---
mov eax,temp_r9       ; r9d ; EAX = function number
xor ecx,ecx           ; ECX = sub-function number
cpuid
xor esi,esi           ; ESI = Storage for sub-function number
.L2:
rcr edx,1
rcr eax,1
jnc .L3
push eax edx
mov eax,temp_r9       ; r9d
mov ecx,esi           ; ECX = Sub-function number
call StoreCpuId
pop edx eax
ja OverSubFunction    ; Go if output buffer overflow
.L3:
inc esi               ; Sunfunctions number +1
cmp esi,63            ;  
jbe .L2               ; Go cycle if next sub-function exist
;---
jmp AfterSubFunction 
;---------- CPUID function 0Fh = Platform QoS monitoring enumeration ----------;
Function0F:
;---------- CPUID function 10h = L3 cache QoS enforcement enumeration (same) --;
Function10:
xor esi,esi           ; ESI = sub-function number for CPUID
xor ecx,ecx           ; ECX = sub-function number for save entry 
push eax temp_r9      ; r9       
call StoreCpuId       ; Subfunction 0 of fixed list [0,1]
pop temp_r9 eax       ; r9
ja OverSubFunction    ; Go if output buffer overflow
mov esi,1
mov ecx,esi
call StoreCpuId       ; Subfunction 1 of fixed list [0,1]
ja OverSubFunction    ; Go if output buffer overflow
jmp AfterSubFunction
;---------- CPUID function 14h = Intel Processor Trace Enumeration ------------;
Function14:
xor esi,esi           ; ESI = Storage for sub-function number
mov ecx,esi
mov eax,temp_r9       ; r9d ; EAX = function number (BUGGY DUPLICATED)
cpuid
mov temp_r8,eax       ; r8d,eax ; R8D = Maximal sub-function number
.L0:
mov eax,temp_r9       ; r9d
mov ecx,esi           ; ECX = Current sub-function number
call StoreCpuId
ja OverSubFunction    ; Go if output buffer overflow
inc esi               ; Sunfunctions number +1
cmp esi,temp_r8       ; r8d 
jbe .L0               ; Go cycle if next sub-function exist
jmp AfterSubFunction


;------------------------------------------------------------------------;
; Measure CPU Clock frequency by Time Stamp Counter (TSC)                ;
;                                                                        ;
; INPUT:   EDI = Pointer to OPB (Output Parameters Block)                ;
;                                                                        ;
; OUTPUT:  QWORD OPB[00] = Frequency, Hz, 0 if measurement error         ;
;------------------------------------------------------------------------;
GetCPUCLK:
push eax ebx ecx edx
mov dword [edi+0],0
mov dword [edi+4],0
call CheckCPUID
;--- Make subroutine for optimization --- start ---
; Get INCLUDE from PowerInfo project.
;mov ebx,21
;pushf                     ; In the 32-bit mode, push EFLAGS
;pop eax
;bts eax,ebx               ; Set EAX.21=1
;push eax
;popf                      ; Load EFLAGS with EFLAGS.21=1
;pushf                     ; Store EFLAGS
;pop eax                   ; Load EFLAGS to EAX
;btr eax,ebx               ; Check EAX.21=1, Set EAX.21=0
;jnc .L0                   ; Go error branch if cannot set EFLAGS.21=1
;push eax
;popf                      ; Load EFLAGS with EFLAGS.21=0
;pushf                     ; Store EFLAGS
;pop eax                   ; Load EFLAGS to EAX
;btr eax,ebx               ; Check EAX.21=0
;jc .L0                    ; Go if cannot set EFLAGS.21=0
;xor eax,eax
;cpuid
;jmp .L1
;.L0:
;stc
;.L1:
;--- Make subroutine for optimization --- end ---
jc @f
cmp eax,1
jb @f
mov eax,1
cpuid
test dl,10h
jz @f
call MeasureCpuClk
jc @f
mov [edi+0],eax
mov [edi+4],edx
@@:
pop edx ecx ebx eax
ret


;------------------------------------------------------------------------;
; Get CPU context management state for context save-restore subsystem    ;
;                                                                        ;
; INPUT:   EDI = Pointer to OPB (Output Parameters Block)                ;
;                                                                        ;
; OUTPUT:  QWORD OPB[00] = CPU validation mask                           ;
;          QWORD OPB[08] = OS validation mask                            ;
;------------------------------------------------------------------------;
GetCPUCTX:
push eax ebx ecx edx
xor eax,eax
mov [edi+00],eax     ; Pre-clear output data
mov [edi+04],eax
mov [edi+08],eax
mov [edi+12],eax
call CheckCPUID
jc @f                ; Skip if CPUID not supported
xor eax,eax
cpuid
cmp eax,0Dh
jb @f                ; Skip if CPUID context declaration not supported
mov eax,1
cpuid
bt ecx,27
jnc @f               ; Skip if CPU context management not supported
mov eax,0Dh
xor ecx,ecx
cpuid
mov [edi+00],eax     ; QWORD OPB[00] = CPU validation mask
mov [edi+04],edx
xor ecx,ecx
xgetbv
mov [edi+08],eax     ; QWORD OPB[08] = OS validation mask 
mov [edi+12],edx
@@:
pop edx ecx ebx eax
ret


;------------------------------------------------------------------------;
; Measure CPU TSC (Time Stamp Counter) clock frequency, Hz               ;
;                                                                        ;
; INPUT:   None                                                          ;
;                                                                        ;
; OUTPUT:  CF flag = Status: 0(NC)=Measured OK, 1(C)=Measurement error	 ;
;          Output EAX, EBX, ECX, EDX valid only if CF=0(NC)              ;
;          EDX:EAX = TSC Frequency, Hz, F = Delta TSC per 1 second       ;
;------------------------------------------------------------------------;
MeasureCpuClk:
cld                             ; Clear direction, because STOS used
xor eax,eax
push esi edi ebp eax eax        ; Last EAX = Variable
;--- Start measure frequency, wait toggle ---
push ebp
call [GetSystemTimeAsFileTime]  ; Get current count
mov esi,[ebp]
@@:
push ebp
call [GetSystemTimeAsFileTime]  ; Get next count for wait 100 ns
cmp esi,[ebp]
je @b
;--- Start time point ---
mov esi,[ebp+0]
mov edi,[ebp+4]
add esi,10000000                
adc edi,0                       ; EDI:ESI = 10^7 * 100ns = 1 second
rdtsc
push eax edx                    ; Stack qword = 64-bit TSC at operation start
;--- Delay 1 second ---
@@:
push ebp
call [GetSystemTimeAsFileTime]  ; Get count for wait 1 second
cmp edi,[ebp+4]                 ; Compare high: target=edi and returned=[ebp+4] 
ja @b                           ; Go wait if target > returned, must 1 second
jb @f
cmp esi,[ebp+0]                 ; Compare low: target=esi and returned=[ebp+0] 
ja @b                           ; Go wait if target > returned, must 1 second
@@:
;--- Stop time point ---
rdtsc                           ; EDX:EAX = 64-bit TSC at operation end
pop ecx ebx
sub eax,ebx
sbb edx,ecx
;--- Exit ---
ExitCpuClk:
pop ebp ebp ebp edi esi          ; First POP EBP for RSP-8 only 
ret


;------------------------------------------------------------------------;
; Check CPUID instruction support.                                       ;
;                                                                        ;
; INPUT:   None                                                          ;
;                                                                        ;
; OUTPUT:  CF = Error flag,                                              ; 
;          0(NC) = Result in EAX valid, 1(C) = Result not valid          ;
;          EAX = Maximum supported standard function, if no errors       ;
;------------------------------------------------------------------------;
CheckCPUID:
mov ebx,21
pushf                     ; In the 32-bit mode, push EFLAGS
pop eax
bts eax,ebx               ; Set EAX.21=1
push eax
popf                      ; Load EFLAGS with EFLAGS.21=1
pushf                     ; Store EFLAGS
pop eax                   ; Load EFLAGS to EAX
btr eax,ebx               ; Check EAX.21=1, Set EAX.21=0
jnc .L0                   ; Go error branch if cannot set EFLAGS.21=1
push eax
popf                      ; Load EFLAGS with EFLAGS.21=0
pushf                     ; Store EFLAGS
pop eax                   ; Load EFLAGS to EAX
btr eax,ebx               ; Check EAX.21=0
jc .L0                    ; Go if cannot set EFLAGS.21=0
xor eax,eax
cpuid
ret
.L0:
stc
ret


;---------- Data section -------------------------------------------------------
section '.data' data readable writeable
;--- Functions pointers, for IPB absent ---
FunctionCount      =   6
FunctionSelector   DD  KmdOpen         ; Open service, load driver
                   DD  KmdClose        ; Close service, unload driver
                   DD  KmdGetFile      ; Get current path and file for KMD
                   DD  GetCPUID        ; Get CPUID dump
                   DD  GetCPUCLK       ; Measure CPU TSC Clock
                   DD  GetCPUCTX       ; Get CPU context management flags

;--- Functions pointers, for IPB present ---
iFunctionCount     =   2
iFunctionSelector  DD  KmdSetFile      ; Set current path and file for KMD
                   DD  KmdTransaction  ; Make service request

;--- Data for detect WOW64 ---
LibName  DB  'KERNEL32',0
FncName  DB  'IsWow64Process',0

;--- Header ---

MAX_PATH_SIZE = 1024 ; 260

SC_MANAGER_ALL_ACCESS = 0x000F003F
SERVICE_ALL_ACCESS    = 0x000F01FF
SERVICE_KERNEL_DRIVER = 0x00000001
SERVICE_DEMAND_START  = 0x00000003
SERVICE_ERROR_NORMAL  = 0x00000001
SERVICE_CONTROL_STOP  = 0x00000001
SERVICE_RUNNING       = 0x00000004

struct SERVICE_STATUS
dwServiceType             dd ?
dwCurrentState            dd ?
dwControlsAccepted        dd ?
dwWin32ExitCode           dd ?
dwServiceSpecificExitCode dd ?
dwCheckPoint              dd ?
dwWaitHint                dd ?
ends

RZ_DRIVER_QUERY_BUFFER_SIZE = 24
RZ_DRIVER_QUERY_PORT_IN_BYTE = 0x0020

struct RZDriverQuery
IOCODE dd ? ; user I/O code
IODATA dd ? ; user I/O data
PROC   dq ? ; procedure offset
paramA dq ? ; parameter A
paramB dq ? ; parameter B
RESULT dq ? ; result
Buffer db RZ_DRIVER_QUERY_BUFFER_SIZE dup (?)
ends

;--- Request code and options ---
R0_DRIVER_BUFFER_SIZE  = 24
R0_DRIVER_MMIO         = 00040h

struct RZDriverQuery1
IOCODE   dd  ?  ; user I/O code
IODATA   dd  ?  ; user I/O data
SRC      dq  ?  ; source address
DST      dq  ?  ; destination address
BYTES    dq  ?  ; memory size in bytes
RESULT   dq  ?  ; result
Buffer   db  R0_DRIVER_BUFFER_SIZE dup (?)
ends

;--- Driver data ---
Error       db 'ERROR',0
;Drvname    db 'RZ32',0
;DrvDevice  db '\\.\RZ32',0

; Unified for availability 64-bit KMD under JVM32-Win64
Drvname     DB  'ICR0',0            ; Driver service name
DrvDevice   DB  '\\.\ICR0',0        ; Device name

DrvPath     dd 0
Manager     dd 0
Service     dd 0
Driver      dd INVALID_HANDLE_VALUE

;--- Service request buffer ---
DrvFile dd ?
Vectors dd ?
Status  SERVICE_STATUS
Bytes   dd ?
Query   RZDriverQuery
Buffer  rb 1024
;--- File name ---
DrvFilename DB  'WIN32KMD.SYS',0
            DB  1000 DUP (?)        ; Space for set new path

;---------- Export section -----------------------------------------------------
section '.edata' export data readable
export 'WIN32JNI.dll' ,\
checkPAL  , 'Java_arch1_kernel_PAL_checkPAL', \
entryPAL  , 'Java_arch1_kernel_PAL_entryPAL'

;---------- Import section ----------------------------------------------------;
section '.idata' import data readable writeable
library kernel32 , 'KERNEL32.DLL' , advapi32 , 'ADVAPI32.DLL'
include 'api\kernel32.inc'
include 'api\advapi32.inc'

;---------- Relocations section ------------------------------------------------ 
data fixups
end data

