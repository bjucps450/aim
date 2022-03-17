.data
	.comm _x, 8, 4
.text

.global _main
_main:
    push    %rbp
    movq    %rsp, %rbp
	pushq	$4
	pushq	$2
    popq    %r11
    popq    %r10
	addq	%r11, %r10
	pushq	%r10
	popq	_x(%rip)
    leave
    ret
