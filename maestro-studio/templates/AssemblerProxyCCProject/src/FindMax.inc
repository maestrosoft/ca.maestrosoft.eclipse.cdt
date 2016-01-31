;
; To prevent of having to write 2 different assembler code for either x86 and x86_amd64 the 
; assembler macro are used when syntax would be different for both ABI. Of course by using
; a unique assembler code for both ABI we cannot take advantage of using the 8 supplemental
; registers available with x86_amd64. But this is acceptable for most cases.
;
IFNDEF WIN64

x86_C           TEXTEQU <C>
x86_findMax_arg TEXTEQU <ptrBuffer:PTR WORD, uiValueCount:DWORD>

ELSE

x86_C           TEXTEQU <>		; Not needed : because x86_amd64 ABI specifies only one calling convention : __fastcall
ptrBuffer       TEXTEQU <rcx>	; The x86_amd64 ABI specifies to use the rcx register to pass the first argument
uiValueCount    TEXTEQU <edx>	; The x86_amd64 ABI specifies to use the rdx register to pass the second argument
x86_findMax_arg TEXTEQU <>		; Stack will be use beginning at the 5th argument. Empty : no argument on stack

ENDIF

findMax  PROTO x86_C x86_findMax_arg
cpuClock PROTO x86_C 

;
; By default the registers name convention of the x86_amd64 model is used. A very simple define is 
; used to rebrand the register name convention of the x86_amd64 model to the x86 model convention.
; Of course this introduce some limitation but this is acceptable for most use cases when the
; target assembler code is SIMD.
;
IFNDEF WIN64

rax EQU	eax
rbx EQU	ebx
rcx EQU	ecx
rdx EQU	edx
rsi EQU	esi
rdi EQU	edi
rpb EQU	ebp
rsp EQU	esp

ENDIF

