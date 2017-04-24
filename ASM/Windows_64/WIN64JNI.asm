;------------------------------------------------------------------------------;
; Part of PAL (Platform Abstraction Layer) for Windows x64                     ;
; JNI DLL (Java Native Interface Dynamical Load Library)                       ;
; Note Kernel Mode Support functions is reserved, yet not used at CPUID v0.5x. ; 
;------------------------------------------------------------------------------;

include 'win64a.inc'
format PE64 GUI 4.0 DLL
entry DllMain

;---------- Code section ------------------------------------------------------;
section '.text' code readable executable

DllMain:        ; This called by Operating System when load/unload DLL
mov eax,1       ; Return status to OS caller (actual when load)
ret

checkPAL:       ; This simple entry for debug native call mechanism
mov eax,64
ret

;--- Entry point for PAL services, Java Native Interface (JNI) ---
; Parm#1 = RCX = JNI Environment  
; Parm#2 = RDX = JNI This Object reference (not used by this routine)
; Parm#3 = R8  = Object IPB array of qwords (long) reference or NULL
; Parm#4 = R9  = Object OPB array of qwords (long) reference or NULL
; Parm#5 = [RSP+40] = IPB size, qwords, or function code if IPB=NULL
; Parm#6 = [RSP+48] = OPB size, qwords, or reserved if OPB=NULL
; Return = RAX = JNI Status: 0=Error, 1=IA32 OK, 2=x64 OK
;---

entryPAL:
push rbx rsi rdi rbp r12 r13 r14 r15   ; Save non-volatile registers
mov rbp,rsp                            ; Save RSP because stack alignment
xor eax,eax
push rax rax                           ; Storage for variable
mov rbx,rcx                            ; RBX = Environment
mov r12,r8                             ; R12 = Object: Input Parm. Block
mov r13,r9                             ; R13 = Object: Output Parm. Block 
mov r14,[rbp+64+8+32+0]                ; R14 = Length of IPB (parm#5)
mov r15,[rbp+64+8+32+8]                ; R15 = Length of OPB (parm#6)
and rsp,0FFFFFFFFFFFFFFF0h             ; Stack alignment by calling convention
sub rsp,32                             ; Parm. shadow by calling convention
xor esi,esi                            ; Pre-blank IPB pointer
xor edi,edi                            ; Pre-blank OPB pointer
;--- Check IPB presence ---
test r12,r12
jz @f                                  ; Go skip IPB extraction if IPB=null
mov rdx,r12
lea r8,[rbp-8]
mov rax,[rbx]                          ; RAX = Pointer to functions table
;--- Get IPB, parms: RCX=env, RDX=IPB Object, R8=Pointer to flag ---
call qword [rax+188*8]                 ; JNI call [GetLongArrayElements]
test rax,rax
jz StatusRet                           ; Go skip if error = NULL pointer
xchg rsi,rax                           ; RSI = Pointer to IPB
@@:
;--- Check OPB presence ---
test r13,r13
jz @f                                  ; Go skip IPB extraction if OPB=null
mov rcx,rbx
mov rdx,r13
lea r8,[rbp-16]
mov rax,[rbx]                          ; RAX = Pointer to functions table
;--- Get OPB, parms: RCX=env, RDX=OPB Object, R8=Pointer to flag ---
call qword [rax+188*8]                 ; JNI call [GetLongArrayElements]
test rax,rax
jz StatusRet                           ; Go skip if error = NULL pointer
xchg rdi,rax                           ; RSI = Pointer to OPB
@@: 
;--- Target operation ---
test rsi,rsi
jz IPB_null                            ; Go special case, IPB size = function
;--- Handling IPB present ---
xor eax,eax
mov r10d,[rsi]                         ; DWORD IPB[0] = Function selector 
cmp r10d,iFunctionCount
jae @f
call qword [iFunctionSelector+r10*8]
@@:
;--- Return point ---
ReleaseRet:
;--- Check IPB release requirement flag and IPB presence ---
cmp qword [rbp-8],0
je @f                                  ; Go skip if IPB release not required
test r12,r12
jz @f                                  ; Go skip IPB extraction if IPB=null
mov rcx,rbx
mov rdx,r12
mov r8,rsi
xor r9d,r9d
mov rax,[rbx]                          ; RAX = Pointer to functions table
;--- Release IPB, parms: RCX=env, RDX=obj, R8=Pointer, R9=Release mode --- 
call qword [rax+196*8]                 ; call [ReleaseLongArrayElements]
@@:
;--- Check OPB release requirement flag and OPB presence ---
cmp qword [rbp-16],0
je @f                                  ; Go skip if OPB release not required
test r13,r13
jz @f                                  ; Go skip OPB extraction if OPB=null
mov rcx,rbx
mov rdx,r13
mov r8,rdi
xor r9d,r9d
mov rax,[rbx]                          ; RAX = Pointer to functions table
;--- Release OPB, parms: RCX=env, RDX=obj, R8=Pointer, R9=Release mode --- 
call qword [rax+196*8]                 ; call [ReleaseLongArrayElements]
@@:
;--- Return with status = RAX ---
mov eax,2                              ; RAX=2 (true) means OK from Win64 DLL 
StatusRet:                             ; Entry point with RAX=0 (error)
mov rsp,rbp                            ; Restore RSP after alignment
pop r15 r14 r13 r12 rbp rdi rsi rbx    ; Restore non-volatile registers
ret                                    ; Return to Java JNI service caller 
;--- Special fast case, no Input Parameters Block ---
IPB_null:
xor eax,eax
cmp r14,FunctionCount                  ; QWORD R14 = Function selector 
jae @f
call qword [FunctionSelector+r14*8]
@@:
jmp ReleaseRet


;---------- Open Service / Install Kernel Mode Driver -------------------------;
; Input:   RDI = Pointer to OPB (Output Parameters Block)                      ;
; Output:  RAX = JNI Status: 0=Error, 2=Win64 JNI OK                           ;
;                set externally from this subroutine                           ;
;          BYTE OPB[0] = Install KMD code, 0=OK, otherwise step number         ;
;------------------------------------------------------------------------------; 
KmdOpen:
push rbx rsi rdi rbp r15
mov r15,rsp
sub rsp,32
;--- (01h) --- VirtualAlloc - reserve region of pages ---
; Parm#1 = RCX = Block starting address, 0 = address selected by function
; Parm#2 = RDX = Block size, MAX_PATH_SIZE=260, reserve space for path
; Parm#3 = R8  = Allocation type, MEM_COMMIT
; Parm#4 = R9  = Protection type, PAGE_READWRITE (but not execute)
; Return = RAX = Pointer to allocated memory block, 0=Error
;---
xor ecx,ecx
mov edx,MAX_PATH_SIZE
mov r8d,MEM_COMMIT
mov r9d,PAGE_READWRITE
call [VirtualAlloc] 
test rax,rax                            ; RAX = 0 means error
mov bl,1                                ; BL = Step = 1
jz CloseContext                         ; Go if memory allocation error  
mov [PathPointer],rax                   ; Store pointer to memory bufer
;--- (02h) --- GetFullPathName - get path string ---
; Parm#1 = RCX = Pointer to file name source string
; Parm#2 = RDX = Buffer Length = MAX_PATH_SIZE
; Parm#3 = R8  = Buffer Pointer, destination string = allocated address
; Parm#4 = R9  = File part, pointer for return path final file component address
; Return = RAX = Returned string length, 0=Error
;---
lea rcx,[FileName]
mov edx,MAX_PATH_SIZE
xchg r8,rax
lea r9,[NamePart]
call [GetFullPathName]
test rax,rax                            ; RAX = 0 means error
mov bl,2                                ; BL = Step = 2
jz CloseContext                         ; Go if error  
;--- (03h) --- OpenSCManager - Open Service Control Manager
; Parm#1 = RCX = Machine Name, 0 means local machine
; Parm#2 = RDX = Database Name, 0 means opened by default
; Parm#3 = R8  = Desired access, set SC_MANAGER_ALL_ACCESS
; Return = RAX = SCM Handle, 0=Error
;---
xor ecx,ecx
xor edx,edx
mov r8d,SC_MANAGER_ALL_ACCESS        
call [OpenSCManager]
test rax,rax                            ; RAX = 0 means error
mov bl,3                                ; BL = Step = 3
jz CloseContext                         ; Go if error  
mov [ScmHandle],rax                     ; Store SCM handle
;--- (04h) --- OpenService - Opens an existing service ---
; Parm#1 = RCX = Database handle
; Parm#2 = RDX = Pointer to service name
; Parm#3 = R8  = Desired access, set SERVICE_ALL_ACCESS
; Return = RAX = Service Handle, 0=Error
;---        
xchg rcx,rax
lea rdx,[ServiceName]
mov r8d,SERVICE_ALL_ACCESS
call [OpenService]
test rax,rax                            ; RAX = 0 means error = not exist
jnz @f                                  ; Skip Create Service if already exist  
;--- (continue 04h) --- CreateService ---
; Create the service object and add to database
; Parm#1  = RCX = SC_HANDLE, handle to the service control manager database
; Parm#2  = RDX = Service Name
; Parm#3  = R8  = User interface display name
; Parm#4  = R9  = Desired access = SERVICE_ALL_ACCESS
; Parm#5  = [RSP+040] = Service type = SERVICE_KERNEL_DRIVER
; Parm#6  = [RSP+048] = Start Type = SERVICE_DEMAND_START
; Parm#7  = [RSP+056] = Error Control = SERVICE_ERROR_NORMAL
; Parm#8  = [RSP+064] = Binary Path Name = [DrvPath]
; Parm#9  = [RSP+072] = Load Order Group, 0=None
; Parm#10 = [RSP+080] = Tag in the Load Order Group, 0=None
; Parm#11 = [RSP+088] = Load Orderign Groups dependences, 0=None
; Parm#12 = [RSP+096] = Service Start Name, 0=None
; Parm#13 = [RSP+104] = Password, 0=None
; Return = RAX = Service Handle, 0=Error 
;---
mov rcx,[ScmHandle]
lea rdx,[ServiceName]
mov r8,rdx
mov r9d,SERVICE_ALL_ACCESS
xor eax,eax
push rax rax rax rax rax rax            ; plus 1 push for stack alignment 
push qword [PathPointer]
push SERVICE_ERROR_NORMAL
push SERVICE_DEMAND_START
push SERVICE_KERNEL_DRIVER
sub rsp,32                              ; Parameters shadow = 32 bytes
call [CreateService]
add rsp,8+32+72
test rax,rax                            ; RAX = 0 means error
mov bl,4                                ; BL = Step = 4
jz CloseContext                         ; Go if error  
@@:
mov [ServiceHandle],rax
;--- (05h) --- QueryServiceStatus - query service status ---
; Parm#1 = RCX = Service Handle
; Parm#2 = RDX = Pointer to SERVICE_STATUS structure, require service info
; Return = RAX = Status, nonzero if OK, 0=Error
;--- 
xchg rcx,rax
lea rdi,[ServiceStatus]
mov rdx,rdi
call [QueryServiceStatus]
test rax,rax                            ; RAX = 0 means error
mov bl,5                                ; BL = Step = 5
jz CloseContext                         ; Go if error  
cmp [rdi + SERVICE_STATUS.dwCurrentState], SERVICE_RUNNING
je @f
;--- (06h) --- StartService - service run ---
; Parm#1 = RCX = Service Handle
; Parm#2 = RDX = Number of Service Arguments, 0 means none
; Parm#3 = R8  = Service Arguments strings
; Return = RAX = Status, nonzero if OK, 0=Error 
;---
mov rcx,[ServiceHandle]
xor edx,edx
;lea r8,[ServiceParms]
xor r8d,r8d
call [StartService]
test rax,rax                            ; RAX = 0 means error
mov bl,6                                ; BL = Step = 6
jz CloseContext                         ; Go if error  
@@:
;--- (07h) --- CreateFile - use create file op. for initializing service ---
; Parm#1 = RCX = File Name, at this context Device Name
; Parm#2 = RDX = Desired Access, requested GENERIC_WRITE + GENERIC_READ
; Parm#3 = R8  = Share Mode, requested FILE_SHARE_READ + FILE_SHARE_WRITE
; Parm#4 = R9  = Security attribute, 0 means none
; Parm#5 = [RSP+040] = Creation Disposition, 
;                      for devices (non-files) use OPEN_EXISTING
; Parm#6 = [RSP+048] = Flags and Attributes, 0 means none
; Parm#7 = [RSP+056] = Template File, 0 means none
; Return = RAX = Device Handle, 0=Error
;---
lea rcx,[DeviceName]
mov edx,GENERIC_WRITE or GENERIC_READ
mov r8d,FILE_SHARE_READ or FILE_SHARE_WRITE
xor r9d,r9d
xor eax,eax
push rax rax rax                        ; Plus one push for stack alignment
push OPEN_EXISTING
sub rsp,32                              ; Parameters shadow = 32 bytes
call [CreateFile]
add rsp,8+24+32
test rax,rax                            ; RAX = 0 means error
mov bl,7                                ; BL = Step = 7
jz CloseContext                         ; Go if error  
@@:
mov [DeviceHandle],rax
;--- Exit if OK ---                     ; Context closed fully if open error
mov bl,0                                ; Status=OK
CloseContext:                           ; FIX DOUBLE-CLOSE BUG
mov rsp,r15
mov al,bl
pop r15 rbp rdi rsi rbx
mov [rdi],al                            ; BYTE OPB[0] = KMD install status = OK
ret


;---------- Close Service / Uninstall Kernel Mode Driver ----------------------;
; Input:   RDI = Pointer to OPB (Output Parameters Block)                      ;
; Output:  RAX = JNI Status: 0=Error, 2=Win64 JNI OK                           ;
;                set externally from this subroutine                           ;
;          BYTE OPB[0] = Uninstall KMD code, 0=OK, otherwise step number       ;
;------------------------------------------------------------------------------; 
KmdClose:
push rbx rsi rdi rbp r15
mov r15,rsp
sub rsp,32
mov bl,0            ; BL=0 means no errors, for close function branch
;--- CloseHandle - close driver handle ---
; Parm#1 = RCX = Handle for close
; Return = RAX = Status, nonzero if OK, 0=Error
;---
CloseContext_1:
mov rcx,[DeviceHandle]
jrcxz @f
call [CloseHandle]
@@:
;--- ControlService - control for driver and device ---
; first parameter - same for DeleteService, CloseServiceHandle
; Parm#1 = RCX = Handle of service
; Parm#2 = RDX = Control value = SERVICE_CONTROL_STOP
; Parm#3 = R8  = Pointer to Service Status structure
; Return = RAX = Status, nonzero if OK, 0=Error
;---
mov rsi,[ServiceHandle]
test rsi,rsi
jz @f
mov edi,SERVICE_CONTROL_STOP
lea rbp,[ServiceStatus]
mov rcx,rsi
mov edx,edi
mov r8,rbp
call [ControlService]
mov rcx,rsi
call [DeleteService]
mov rcx,rsi
call [CloseServiceHandle]
@@:
;--- Close manager handle ---
; Parm#1 = RCX = Handle for close
; Return = RAX = Status, nonzero if OK, 0=Error
;---
mov rcx,[ScmHandle]
jrcxz @f
call [CloseServiceHandle]
@@:
;---
; VirtualFree - free memory, allocated at program start
; Parm#1 = Address, pointer to base address of region
; Parm#2 = Size of the region
; Parm#3 = Type of memory free operation
;---
mov rcx,[PathPointer]
jrcxz @f
; mov edx,MAX_PATH_SIZE
xor edx,edx
mov r8d,MEM_RELEASE
call [VirtualFree]
@@:
;--- Get RAX = error code 
; RESERVED FOR FUTURE IMPLEMENT
; test bl,bl
; jz @f              ; Skip get error if no errors
; call [GetLastError]
; @@:
;--- Exit if ERROR ---
mov rsp,r15
mov al,bl
pop r15 rbp rdi rsi rbx
mov [rdi],al
ret 


;---------- Execute request to Kernel Mode Driver -----------------------------;
; Input:   RSI = Pointer to IPB (Input Parameters Block)                       ;
;                DWORD [RSI+00] = Function code, decoded externally            ;
;                DWORD [RSI+04] = Device request                               ;
;                QWORD [RSI+08] = Block base address (request specific)        ;
;                QWORD [RSI+16] = Block length (request specific)              ;
;          RDI = Pointer to OPB (Output Parameters Block)                      ;
; Output:  RAX = JNI Status: 0=Error, 2=Win64 JNI OK                           ;
;                set externally from this subroutine                           ;
;          BYTE OPB[0] = Execution KMD code, 0=OK, otherwise step number       ;
;          ... reserved 7 bytes                                                ;
;          OPB[8] = Output buffer                                              ;
;------------------------------------------------------------------------------; 
KmdTransaction:
;--- Prepare request parameters ---
mov r8d,[rsi+04]   ; R8  = Driver request code
mov r9, [rsi+08]   ; R9  = Source base address
mov r10,[rsi+16]   ; R10 = Length
lea r11,[rdi+08]   ; R11 = Destination base address  
;--- Built and execute request ---
push rbx rsi rdi rbp r15
mov r15,rsp
sub rsp,32
;--- (08h) --- Built QUERY request desc. block and request target operation ---
; WriteFile - use write file operation for execute target operation - I/O
; Parm#1 = RCX = Handle of File or I/O Device
; Parm#2 = RDX = Pointer to Buffer, data write to file or transmit to I/O device
; Parm#3 = R8  = Number of Bytes to Write, buffer size
; Parm#4 = R9  = Pointer to returned: Number of Bytes Written
; Parm#5 = [RSP+040] = Pointer to OVERLAPPED structure, 0 means none
; Return = RAX = Status, nonzero if OK, 0=Error
;---
lea rsi,[Target_IPB]
lea rdi,[Target_OPB]
lea rdx,[DriverQuery]
xor eax,eax
push rax rax                            ; Plus one push for stack alignment
sub rsp,32                              ; Parameters shadow = 32 bytes
mov rax, R0_DRIVER_MMIO
mov qword [rdx + DriverRequestPacket.IOCODE],   rax        ; Selected function
mov qword [rdx + DriverIoRequestPacket.SRC],    r9  ; r8   ; PhysicalAddress
mov qword [rdx + DriverIoRequestPacket.BYTES],  r10 ; 4    ; NumberOfBytes
mov qword [rdx + DriverIoRequestPacket.DST],    r11 ; rdi  ; Destination
mov dword [rdx + DriverIoRequestPacket.IODATA], 0          ; 0 means unmap
mov rcx,[DeviceHandle]
mov r8d,sizeof.DriverRequestPacket
lea r9,[ReturnBytes]
call [WriteFile]
add rsp,8+8+32
test rax,rax                            ; RAX = 0 means error
mov bl,8                                ; BL = Step = 8
jz CloseContext                         ; Go if error  
;--- Exit if READ OK ---
mov rsp,r15
pop r15 rbp rdi rsi rbx
;--- Write status ---
mov byte [rdi],0
ret


;---------- Get Driver file name ----------------------------------------------;
; Parm#1 = RCX = Pointer to buffer for return file name string                 ;
; Output = RAX = Status: 0=Error, Non-Zero=OK, set external. at transit caller ;
;          [buffer] = File name returned                                       ;
;------------------------------------------------------------------------------; 
KmdGetFile:
;--- Porting fixup ---
lea rcx,[rdi+8]
;--- End of porting fixup ---
lea rdx,[FileName]
mov r8d,1000
.L0:
mov al,[rdx]
mov [rcx],al
inc rdx
inc rcx
test al,al
jz .L1
dec r8d
jnz .L0
mov [rcx],r8b
.L1:
ret


;---------- Set Driver file name ----------------------------------------------;
; Parm#1 = RCX = Pointer to buffer with file name string                       ;
; Output = RAX = Status: 0=Error, Non-Zero=OK, set external. at transit caller ;
;------------------------------------------------------------------------------; 
KmdSetFile:
;--- Porting fixup ---
lea rcx,[rsi+8]
;--- End of porting fixup ---
lea rdx,[FileName]
mov r8d,1000
.L0:
mov al,[rcx]
mov [rdx],al
inc rcx
inc rdx
test al,al
jz .L1
dec r8d
jnz .L0
mov [rdx],r8b
.L1:
ret

;---------- Get CPUID dump ----------------------------------------------------;
; Parm#1 = RDI = Pointer to buffer for status and dump data                    ;
; Output = RAX = Status: 0=Error, Non-Zero=OK, set external. at transit caller ;
;          Buffer DWORD[0] = Number of entries returned                        ;
;          Bytes [4-31] = Reserved for alignment                               ;
;          Bytes [32-16383] = Buffer, maximum (16384-32)/32 = 511 entries ret. ;  
;------------------------------------------------------------------------------;
GetCPUID:
push rdi
cld
mov ecx,8
xor eax,eax
rep stosd
mov rcx,rdi
call Internal_GetCPUID
pop rdi
mov [rdi],eax
ret

ENTRIES_LIMIT = 511    ; Maximum number of output buffer 16352 bytes = 511*32
;---------- Target subroutine -------------------------------------------------;
; INPUT:  Parameter#1 = RCX = Pointer to output buffer
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
cld
push rbx rbp rsi rdi
mov rdi,rcx
xor ebp,ebp               ; EBP = Global output entries counter
;---------- Check for ID bit writeable for "1" --------------------------------;
;mov ebx,21
;pushf                     ; In the 64-bit mode, push RFLAGS
;pop rax
;bts eax,ebx               ; Set EAX.21=1
;push rax
;popf                      ; Load RFLAGS with RFLAGS.21=1
;pushf                     ; Store RFLAGS
;pop rax                   ; Load RFLAGS to RAX
;btr eax,ebx               ; Check EAX.21=1, Set EAX.21=0
;jnc NoCpuId               ; Go error branch if cannot set EFLAGS.21=1
;---------- Check for ID bit writeable for "0" --------------------------------;
;push rax
;popf                      ; Load RFLAGS with RFLAGS.21=0
;pushf                     ; Store RFLAGS
;pop rax                   ; Load RFLAGS to RAX
;btr eax,ebx               ; Check EAX.21=0
;jc NoCpuId                ; Go if cannot set EFLAGS.21=0
;---------- Get standard CPUID results ----------------------------------------;

call CheckCPUID           ; Return CF=Error flag, EAX=Maximum standard function
jc NoCpuId

xor r9d,r9d               ; R9D  = standard functions start
;xor eax,eax
;cpuid
cmp eax,ENTRIES_LIMIT/2   ; EAX = maximum supported standard function number
ja ErrorCpuId             ; Go if invalid limit
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow at subfunction
;---------- Get virtual CPUID results -----------------------------------------;
mov r9d,40000000h         ; R9D = virtual functions start
mov eax,r9d               ; EAX = Function
xor ecx,ecx               ; ECX = Subfunction
cpuid
and eax,0FFFFFF00h
cmp eax,040000000h
jne NoVirtual             ; Skip virtual CPUID if not supported
mov eax,r9d               ; EAX = Limit, yet 1 function 40000000h
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow at subfunction
NoVirtual:
;---------- Get extended CPUID results ----------------------------------------;
mov r9d,80000000h         ; R9D  = extended functions start
mov eax,r9d
cpuid
cmp eax,80000000h + ENTRIES_LIMIT/2  ; EAX = maximum extended function number
ja ErrorCpuId                        ; Go if invalid limit
call SequenceCpuId
jc ErrorCpuId                        ; Exit if output buffer overflow
;---------- Return points -----------------------------------------------------;
xchg eax,ebp              ; Normal exit point, return RAX = number of entries
ExitCpuId:
pop rdi rsi rbp rbx
ret
NoCpuId:                  ; Exit for CPUID not supported, RAX=0  
xor eax,eax
jmp ExitCpuId
ErrorCpuId:               ; Exit for CPUID error, RAX=-1=FFFFFFFFFFFFFFFFh
mov rax,-1
jmp ExitCpuId 
;---------- Subroutine, sequence of CPUID functions ---------------------------;
; INPUT:  R9D = Start CPUID function number
;         EAX = Limit CPUID function number (inclusive)
;         RDI = Pointer to memory buffer
; OUTPUT: RDI = Modified by store CPUID input parms + output parms entry
;         Flags condition code: Carry (C) = means entries count limit
;---
SequenceCpuId:
mov r10d,eax              ; R10D = standard or extended functions limit 
CycleCpuId:
;--- Specific handling for functions with subfunctions ---
mov eax,r9d           ; EAX = function number, input at R9D
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
inc r9d
cmp r9d,r10d
jbe CycleCpuId            ; Cycle for CPUID standard functions
ret
OverSubFunction:
stc
ret 
;---------- Subroutine, one CPUID function execution --------------------------;
; INPUT:  EAX = CPUID function number
;         R9D = EAX
;         ECX = CPUID subfunction number
;         ESI = ECX
;         RDI = Pointer to memory buffer
; OUTPUT: RDI = Modified by store CPUID input parms + output parms entry
;         Flags condition code: Above (A) = means entries count limit
;---
StoreCpuId:
cpuid
StoreCpuId_Entry:     ; Entry point for CPUID results (EAX,EBX,ECX,EDX) ready 
push rax
xor eax,eax
stosd                 ; Store tag dword[0] = Information type
mov eax,r9d
stosd                 ; Store argument dword [1] = CPUID function number 
mov eax,esi
stosd                 ; Store argument dword [2] = CPUID sub-function number
xor eax,eax
stosd                 ; Store argument dword [3] = CPUID pass number (see fn.2)
pop rax
stosd                 ; Store result dword [4] = output EAX 
xchg eax,ebx
stosd                 ; Store result dword [5] = output EBX
xchg eax,ecx
stosd                 ; Store result dword [6] = output ECX
xchg eax,edx
stosd                 ; Store result dword [7] = output EDX
inc ebp               ; Global counter +1
cmp ebp,ENTRIES_LIMIT ; Limit for number of output entries
ret
;---------- CPUID function 04h = Deterministic cache parameters ---------------;
Function04:
xor esi,esi           ; ESI = Storage for sub-function number
.L0:
mov eax,r9d           ; EAX = function number
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
mov eax,r9d           ; EAX = function number (BUGGY DUPLICATED)
cpuid
mov r8d,eax           ; R8D = Maximal sub-function number
.L0:
mov eax,r9d
mov ecx,esi           ; ECX = Current sub-function number
call StoreCpuId
ja OverSubFunction    ; Go if output buffer overflow
inc esi               ; Sunfunctions number +1
cmp esi,r8d           ; 
jbe .L0               ; Go cycle if next sub-function exist
jmp AfterSubFunction
;---------- CPUID function 0Bh = Extended topology enumeration ----------------;
Function0B:
xor esi,esi           ; ESI = Storage for sub-function number
.L0:
mov eax,r9d           ; EAX = function number
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
;mov eax,r9d           ; EAX = function number
;xor ecx,ecx           ; ECX = sub-function number
;cpuid
;xor r8d,r8d           ; R8D = counter for subfunctions, this also make CF=0(NC)
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
;mov eax,r9d
;mov ecx,esi           ; ECX = Sub-function number
;call StoreCpuId
;ja OverSubFunction    ; Go if output buffer overflow
;inc esi               ; Sunfunctions number +1
;cmp esi,r8d           ; 
;jbe .L2               ; Go cycle if next sub-function exist
;---
mov eax,r9d           ; EAX = function number
xor ecx,ecx           ; ECX = sub-function number
cpuid
shl rdx,32
lea r8,[rdx+rax]
xor esi,esi           ; ESI = Storage for sub-function number
.L2:
shr r8,1
jnc .L3
mov eax,r9d
mov ecx,esi           ; ECX = Sub-function number
call StoreCpuId
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
push rax r9       
call StoreCpuId       ; Subfunction 0 of fixed list [0,1]
pop r9 rax
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
mov eax,r9d           ; EAX = function number (BUGGY DUPLICATED)
cpuid
mov r8d,eax           ; R8D = Maximal sub-function number
.L0:
mov eax,r9d
mov ecx,esi           ; ECX = Current sub-function number
call StoreCpuId
ja OverSubFunction    ; Go if output buffer overflow
inc esi               ; Sunfunctions number +1
cmp esi,r8d           ; 
jbe .L0               ; Go cycle if next sub-function exist
jmp AfterSubFunction


;------------------------------------------------------------------------;
; Measure CPU Clock frequency by Time Stamp Counter (TSC)                ;
;                                                                        ;
; INPUT:   RDI = Pointer to OPB (Output Parameters Block)                ;
;                                                                        ;
; OUTPUT:  QWORD OPB[00] = Frequency, Hz, 0 if measurement error         ;
;------------------------------------------------------------------------;
GetCPUCLK:
push rax rbx rcx rdx
mov qword [rdi],0
call CheckCPUID
;--- Make subroutine for optimization --- start ---
; Get INCLUDE from PowerInfo project.
;mov ebx,21
;pushf                     ; In the 64-bit mode, push RFLAGS
;pop rax
;bts eax,ebx               ; Set EAX.21=1
;push rax
;popf                      ; Load RFLAGS with RFLAGS.21=1
;pushf                     ; Store RFLAGS
;pop rax                   ; Load RFLAGS to RAX
;btr eax,ebx               ; Check EAX.21=1, Set EAX.21=0
;jnc .L0                   ; Go error branch if cannot set EFLAGS.21=1
;push rax
;popf                      ; Load RFLAGS with RFLAGS.21=0
;pushf                     ; Store RFLAGS
;pop rax                   ; Load RFLAGS to RAX
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
mov [rdi],rax
@@:
pop rdx rcx rbx rax
ret


;------------------------------------------------------------------------;
; Get CPU context management state for context save-restore subsystem    ;
;                                                                        ;
; INPUT:   RDI = Pointer to OPB (Output Parameters Block)                ;
;                                                                        ;
; OUTPUT:  QWORD OPB[00] = CPU validation mask                           ;
;          QWORD OPB[08] = OS validation mask                            ;
;------------------------------------------------------------------------;
GetCPUCTX:
push rax rbx rcx rdx
xor eax,eax
mov [rdi+00],rax     ; Pre-clear output data
mov [rdi+08],rax
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
mov [rdi+00],eax     ; QWORD OPB[00] = CPU validation mask
mov [rdi+04],edx
xor ecx,ecx
xgetbv
mov [rdi+08],eax     ; QWORD OPB[08] = OS validation mask 
mov [rdi+12],edx
@@:
pop rdx rcx rbx rax
ret


;------------------------------------------------------------------------;
; Measure CPU TSC (Time Stamp Counter) clock frequency, Hz               ;
;                                                                        ;
; INPUT:   None                                                          ;
;                                                                        ;
; OUTPUT:  CF flag = Status: 0(NC)=Measured OK, 1(C)=Measurement error	 ;
;          Output RAX,RDX valid only if CF=0(NC)                         ;
;          RAX = TSC Frequency, Hz, F = Delta TSC per 1 second           ;
;------------------------------------------------------------------------;
MeasureCpuClk:
cld                         ; Clear direction, because STOSQ used
push rbx rcx rdx rsi rbp r8 r9 r10 r11 rax	 ; R8-R11 because API, RAX = Var
mov rbp,rsp                 ; RBP used for restore RSP and addressing variables
and rsp,0FFFFFFFFFFFFFFF0h  ; Align stack (16)
sub rsp,32                  ; Make parameters shadow
;--- Start measure frequency, wait toggle ---
mov rcx,rbp
call [GetSystemTimeAsFileTime]  ; Get current count
mov rsi,[rbp]
@@:
mov rcx,rbp
call [GetSystemTimeAsFileTime]  ; Get next count for wait 100 ns
cmp rsi,[rbp]
je @b
;--- Start time point ---
mov rsi,[rbp]
add rsi,10000000                ; RSI = 10^7 * 100ns = 1 second
rdtsc
shl rdx,32
lea rbx,[rax+rdx]               ; RBX = 64-bit TSC at operation start
;--- Delay 1 second ---
@@:
mov rcx,rbp
call [GetSystemTimeAsFileTime]  ; Get count for wait 1 second
cmp rsi,[rbp]                   ; Compare target=rsi and returned=[rbp] 
ja @b                           ; Go wait if target > returned, must 1 second
;--- Stop time point ---
rdtsc
shl rdx,32
or rax,rdx                      ; RAX = 64-bit TSC at operation end
sub rax,rbx                     ; RAX = Delta TSC = frequency (1 second)
;--- Restore RSP, pop extra registers, exit ---
ExitCpuClk:
mov rsp,rbp                            ; Restore RSP after alignment and shadow
pop rbx r11 r10 r9 r8 rbp rsi rdx rcx rbx  ; First POP RBX for RSP-8 only 
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
pushf                     ; In the 64-bit mode, push RFLAGS
pop rax
bts eax,ebx               ; Set EAX.21=1
push rax
popf                      ; Load RFLAGS with RFLAGS.21=1
pushf                     ; Store RFLAGS
pop rax                   ; Load RFLAGS to RAX
btr eax,ebx               ; Check EAX.21=1, Set EAX.21=0
jnc .L0                   ; Go error branch if cannot set EFLAGS.21=1
push rax
popf                      ; Load RFLAGS with RFLAGS.21=0
pushf                     ; Store RFLAGS
pop rax                   ; Load RFLAGS to RAX
btr eax,ebx               ; Check EAX.21=0
jc .L0                    ; Go if cannot set EFLAGS.21=0
xor eax,eax
cpuid                     ; CPUID function 0, here used output EAX = max. std.
ret
.L0:                      ; This point for errors handling
stc
ret


;---------- Data section ------------------------------------------------------;
section '.data' data readable writeable
;--- Functions pointers, for IPB absent ---
FunctionCount      =   6
FunctionSelector   DQ  KmdOpen          ; Open service, load driver
                   DQ  KmdClose         ; Close service, unload driver
                   DQ  KmdGetFile       ; Get current path and file for KMD
                   DQ  GetCPUID         ; Get CPUID dump
                   DQ  GetCPUCLK        ; Measure CPU TSC frequency
                   DQ  GetCPUCTX        ; Get CPU context management flags

;--- Functions pointers, for IPB present ---
iFunctionCount     =   2
iFunctionSelector  DQ  KmdSetFile       ; Set current path and file for KMD
                   DQ  KmdTransaction   ; Make service request

;--- Header equations ---

MAX_PATH_SIZE = 1024 ; 260

SC_MANAGER_ALL_ACCESS = 0000F003Fh   ; Desired access mode for Open SCM
SERVICE_ALL_ACCESS    = 0000F01FFh   ; Desired access mode for Open Service
SERVICE_ERROR_NORMAL  = 000000001h   ; Options for create service
SERVICE_DEMAND_START  = 000000003h
SERVICE_KERNEL_DRIVER = 000000001h
SERVICE_RUNNING       = 000000004h
SERVICE_CONTROL_STOP  = 000000001h

;--- Service status structure ---
struct SERVICE_STATUS
dwServiceType              dd  ?
dwCurrentState             dd  ?
dwControlsAccepted         dd  ?
dwWin32ExitCode            dd  ?
dwServiceSpecificExitCode  dd  ?
dwCheckPoint               dd  ?
dwWaitHint                 dd  ?
ends

;--- Request code and options ---
R0_DRIVER_BUFFER_SIZE  = 24
R0_DRIVER_MMIO         = 00040h

;--- Structure for driver query ---
; base
struct DriverRequestPacket
IOCODE   dd  ?   ; user I/O code
IODATA   dd  ?   ; user I/O data
PROC     dq  ?   ; procedure offset
paramA   dq  ?   ; parameter A
paramB   dq  ?   ; parameter B
RESULT   dq  ?   ; result
Buffer   db  R0_DRIVER_BUFFER_SIZE dup (?)
ends

;--- Structure for driver query ---
; I/O, memory map
struct DriverIoRequestPacket
IOCODE   dd  ?  ; user I/O code
IODATA   dd  ?  ; user I/O data
SRC      dq  ?  ; source address
DST      dq  ?  ; destination address
BYTES    dq  ?  ; memory size in bytes
RESULT   dq  ?  ; result
Buffer   db  R0_DRIVER_BUFFER_SIZE dup (?)
ends

;--- Objects Names ---
;ServiceName   DB  'R064',0            ; Driver service name
;ServiceParms  DQ  0                   ; Pointers to parameters strings, 0=None
;DeviceName    DB  '\\.\R064',0        ; Device name

; Unified for availability 64-bit KMD under JVM32-Win64
ServiceName    DB  'ICR0',0            ; Driver service name
DeviceName     DB  '\\.\ICR0',0        ; Device name

;--- Variables ---
; Must be pre-blanked for validity check
PathPointer    DQ  0          ; Pointer to driver path built, free memory
FileHandle     DQ  0          ; Driver file handle
NamePart       DQ  0          ; Pointer to file name - part of path
ScmHandle      DQ  0          ; SCM (Service Control Manager) handle 
ServiceHandle  DQ  0          ; Service handle
DeviceHandle   DQ  0          ; Created device handle
ReturnBytes    DQ  0          ; Number of bytes, returned by driver
;--- Request packet structure ---
DriverQuery    DriverRequestPacket
;--- Input-Output blocks ---
Target_IPB     DB 64 DUP (?)  ; Driver request Input Parameters Block - IPB
Target_OPB     DB 64 DUP (?)  ; Driver request Output Parameters Block - OPB
;--- File name ---
FileName       DB  'WIN64KMD.SYS',0    ; Driver file name
               DB  1000 DUP (?)        ; Space for set new path
;--- Status structure ---
ServiceStatus  SERVICE_STATUS

;---------- Export section ----------------------------------------------------;
section '.edata' export data readable
export 'WIN64JNI.dll' ,\
checkPAL  , 'Java_arch1_kernel_PAL_checkPAL', \
entryPAL  , 'Java_arch1_kernel_PAL_entryPAL'

;---------- Import section ----------------------------------------------------;
section '.idata' import data readable writeable
library kernel32 , 'KERNEL32.DLL' , advapi32 , 'ADVAPI32.DLL'
include 'api\kernel32.inc'
include 'api\advapi32.inc'

;---------- Relocations section -----------------------------------------------; 
data fixups
end data

