#include "p18F4520.inc"
    
    CONFIG  OSC = HS
    CONFIG  PWRT = OFF
    CONFIG  WDT = OFF
    CONFIG  PBADEN = OFF
    CONFIG  LVP = OFF
        
SEG0	equ	0x020
SEG1	equ	0x021
SEG2	equ	0x022
SEG3	equ	0x023
COUNTER	equ	0x004
DELAY	equ	0x005
	org	0x00000
	BRA	MAIN
	
	org	0x00020
MAIN	RCALL	MINIT
LMLOOP	RCALL	EINC32	    ;Increases 32-bit number
	RCALL	EDLONG	    ;Lights updates every 0.3 ms
	BRA	LMLOOP
	BRA	$
	
EDISP	LFSR	0,0x023	    ;Starts displaying right-most segment-display
	MOVLW	0x03	    ;Ammount of segment-displays
	MOVWF	COUNTER	    
LLOOP	MOVF	COUNTER,W   ;Maps the current collumn to correct PORTA
	RCALL	ECOLLU	    ;using a "Look-up table"
	MOVWF	PORTA
	MOVFF	POSTDEC0,PORTD
	RCALL	EDSHORT
	DECF	COUNTER
	BNN	LLOOP
	RETURN
	
EINC32	LFSR	1,0x020	    ;Starts increasing the right-most segment-register
	MOVLW	0x04	    ;Ammount of segment-registers
	MOVWF	COUNTER
LLOOPI	INCF	POSTINC1    ;Increase what FSR is pointing to
	BTFSS	STATUS,C    ;If carry, clear it and excute same
	BNZ	EXIT	    ;If there was no carry -> Break
	DECFSZ	COUNTER
	BRA	LLOOPI
EXIT	RETURN
	
ECOLLU	MULLW	0x02	    ;Multiply map-value to make room for RETLW-opcode
	MOVF	PRODL,W	    ;Only needs the lower byte of the result since
	ADDWF	PCL	    ;collumn is in range: 0 <= col <= 3 
	RETLW	b'00000010'
	RETLW	b'00000100'
	RETLW	b'00001000'
	RETLW	b'00100000'
	
MINIT	CLRF	TRISA
	CLRF	TRISD
	CLRF	SEG0
	CLRF	SEG1
	CLRF	SEG2
	CLRF	SEG3
	CLRF	COUNTER
	SETF	DELAY
	RETURN
	
EDLONG	MOVLW	0x030
	MOVWF	0x0F1	
LDLOOPL RCALL	EDISP
	RCALL	EDSHORT
	DECFSZ	0x0F1
	BRA	LDLOOPL
	
EDSHORT	MOVLW	0xFF
	MOVWF	0x0F0
LDLOOP	DECFSZ	0x0F0
	BRA	LDLOOP
	RETURN
	
	end