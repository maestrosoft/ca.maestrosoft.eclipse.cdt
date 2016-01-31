;
; Name        : $(baseName).asm
; Author      : $(author)
; Version     :
; Copyright   : $(copyright)
; Description : FindMax SIMD optimized procedure to find a maximum value of a set
;

; The assembler model defined for x86_amd64 is quite hardcoded and doesn't need all the
; bell and whistle of the x86 model.
;
IFNDEF WIN64

.686
.xmm
.model flat, c

ENDIF

INCLUDE FindMax.inc

.code
;
; Define the procedure findMax.
;
; Note : The ABI of C++ is different for x86 and x86_amd64. All arguments must be pushed on the
; stack for x86. While x86_amd64 is passing arguments with both registers and the stack. for
; more information: http://software.intel.com/en-us/articles/introduction-to-x64-assembly
;
; To resolve this discrepencies of calling convention between the 2 ABI the x86_findMax_arg
; macro is used and have different definition for both ABI.
;
; Remember that general registers eax, ecx, and edx are caller-saved or volatile according to the x86 ABI
; General registers rax, r10, r11, are volatile according to the x86_amd64 ABI
;
findMax PROC x86_C USES rbx, x86_findMax_arg
;
; This procedure expect the ptrBuffer to be aligned on a 16 bytes. Otherwise an exception will happen.
; The main loop processes 32 signed short values. Since it is likely that the number of value to be
; processed is not a multiplw of 32 a remainer must be computed that will be processed outside of the
; main loop.
;
;   Prologue or initialization of the processing loop
;
    mov     rax,  ptrBuffer         ; rax <-- Get the buffer pointer to the stored value
    mov     ebx,  uiValueCount      ; ebx <-- Numbers of value to be processed
    mov     ecx,  ebx               ; ecx <-- uiValueCount
    shr     ecx,  5                 ; ecx <-- uiNumLoop = uiValueCount / 32
    and     ebx,  1fh               ; ebx <-- uiRemainer = uiValueCount % 32

    pxor    xmm4, xmm4              ; xmm4 <-- all 0

mainLoop:
;
; This is the main processing loop. Since assembler code is much more difficult to read than
; C/C++ code it is suggested to be very liberal regarding comment.
;
    movdqa  xmm0, [rax]          ; xmm0 <-- val(7), val(6), ..., val(1), val(0)
    movdqa  xmm1, [rax+16]       ; xmm1 <-- val(15), val(14), ..., val(9), val(8)
    movdqa  xmm2, [rax+32]       ; xmm2 <-- val(23), val(22), ..., val(17), val(16)
    movdqa  xmm3, [rax+48]       ; xmm3 <-- val(31), val(30), ..., val(25), val(24)

    pmaxsw  xmm0, xmm1           ; xmm0 <-- max[val(15), val(7)], ..., max[val(8), val(0)]
    pmaxsw  xmm2, xmm3           ; xmm2 <-- max[val(31), val(23)], ..., max[val(24), val(16)]
    pmaxsw  xmm0, xmm2           ; xmm0 <-- max[val(31), val(23), val(15), val(7)], ..., max[val(24), val(16), max[val(8), val(0)]
    pmaxsw  xmm4, xmm0           ; xmm4 <-- max[val(n*8+7)], max[val(n*8+6)], ..., max[val(n*8+1)], max[val(n*8)]

    add     rax,  64             ; rax <-- ptrBuffer += 32
    sub     ecx,  1              ; ecx <-- if ( --uiNumLoop != 0 )
    jnz     mainLoop             ; then goto mainLoop
;
; Prologue or terminate the processing loop and return result of processing.
;
    movdqa  xmm1, xmm4           ; xmm1 <-- max[val(n*8+7)], max[val(n*8+6)], ..., max[val(n*8+1)], max[val(n*8)]
    psrldq  xmm4, 8              ; xmm4 <-- 0, 0, 0, 0, max[val(n*8+7)], max[val(n*8+6)], max[val(n*8+5)], max[val(n*8+4)]
    pmaxsw  xmm4, xmm1           ; xmm4 <-- x, x, x, x, max[val(n*8+7), val(n*8+3)], max[val(n*8+6), val(n*8+2)], max[val(n*8+5), val(n*8+1)], max[val(n*8+4), val(n*8+0)]

    movdqa  xmm1, xmm4           ; xmm1 <-- x, x, x, x, max[val(n*8+7), val(n*8+3)], max[val(n*8+6), val(n*8+2)], max[val(n*8+5), val(n*8+1)], max[val(n*8+4), val(n*8+0)]
    psrlq   xmm4, 32             ; xmm4 <-- x, x, x, x, x, x, max[val(n*8+7), val(n*8+3)], max[val(n*8+6), val(n*8+2)]
    pmaxsw  xmm4, xmm1           ; xmm4 <-- x, x, x, x, x, x, max[val(n*8+7), val(n*8+5), val(n*8+3), val(n*8+1)], max[val(n*8+6), val(n*8+4), val(n*8+2), val(n*8+0)]

    movdqa  xmm1, xmm4           ; xmm1 <-- x, x, x, x, x, x, max[val(n*8+7), val(n*8+5), val(n*8+3), val(n*8+1)], max[val(n*8+6), val(n*8+4), val(n*8+2), val(n*8+0)]
    psrlq   xmm4, 16             ; xmm4 <-- x, x, x, x, x, x, x, max[val(n*8+7), val(n*8+5), val(n*8+3), val(n*8+1)]
    pmaxsw  xmm4, xmm1           ; xmm4 <-- x, x, x, x, x, x, x, max[val(n*8+7), val(n*8+6), val(n*8+5), val(n*8+4), val(n*8+3), val(n*8+2), val(n*8+1), val(n*8+0)]
    movd    ecx,  xmm4           ; cx <-- max[val(n*8+7), val(n*8+6), val(n*8+5), val(n*8+4), val(n*8+3), val(n*8+2), val(n*8+1), val(n*8+0)]
    movsx   rcx,  cx             ; rcx <-- maxValue = max[val(n*8+7), val(n*8+6), val(n*8+5), val(n*8+4), val(n*8+3), val(n*8+2), val(n*8+1), val(n*8+0)]

    and     ebx, ebx             ; if ( uiNumLoop != 0 )
    jz      retmax               ; then goto retmax

remainLoop:

    movsx   rdx,  word ptr [rax] ; rdx <-- val(n)
    add     rax,  2              ; rax <-- ptrBuffer++
    cmp     rcx,  rdx            ; if( max{val(n-1),..., val(0)} < val(n) ) then
    cmovl   rcx,  rdx            ; rcx <-- maxValue = max{val(n),..., val(0)}
    sub     ebx,  1              ; if( --uiRemainer != 0 )
    jnz     remainLoop           ; then process remainer

retmax:

    mov     rax,  rcx            ; rax <-- returned value must be put in rax according to the ABI

    RET                          ; exit from procedure and return to caller
   
findMax ENDP

;
; This is a produre that is only 1 assembler instruction. It purpose is to read the current time as 
; cpu cycles and return the 64 bits value in register eax, edx (x86 ABI)
;
cpuClock PROC x86_C

    rdtsc

    RET

cpuClock ENDP

end
