#include "p18F4520.inc"
    
    CONFIG  OSC = HS
    CONFIG  PWRT = OFF
    CONFIG  WDT = OFF
    CONFIG  PBADEN = OFF
    CONFIG  LVP = OFF
        
SEG0	equ	0x000
SEG1	equ	0x001
SEG2	equ	0x002
SEG3	equ	0x003
COUNTER	equ	0x004
COL	equ	0x005
DELAY	equ	0x006
	org	0x00000
	BRA	MAIN
	
	org	0x00020
MAIN	RCALL	MINIT
LMLOOP	DCFSNZ	DELAY	    ;An extra delay for increasing number (every 80 ms)
	RCALL	EINC32	    ;Increases 32-bit number
	RCALL	EDISP	    ;Updates display
	RCALL	EDSHORT	    ;Lights updates every 0.3 ms
	BRA	LMLOOP
	BRA	$
	
EDISP	LFSR	0,0x000	    ;Starts displaying right-most segment-display
	MOVLW	0x04	    ;Ammount of segment-displays
	MOVWF	COUNTER	    
	CLRF	COL	    ;Starts at collumn 0
LLOOP	MOVF	COL,W	    ;Maps the current collumn to correct PORTA
	RCALL	ECOLLU	    ;using a "Look-up table"
	MOVWF	PORTA
	INCF	COL	    
	MOVF	POSTINC0,W  ;Check which value the current segement-register
	RCALL	EROWLU	    ;have and the maps it to a value represting the
	MOVWF	PORTD	    ;number on the segement-display
	RCALL	EDSHORT
	DECFSZ	COUNTER
	BRA	LLOOP
	RETURN
	
EINC32	MOVLW	0x2F	    ;Resets the delay for increasing the 32-bit number
	MOVWF	DELAY
	LFSR	1,0x000	    ;Starts increasing the right-most segment-register
	MOVLW	0x04	    ;Ammount of segment-registers
	MOVWF	COUNTER
LLOOPI	INCF	INDF1	    ;Increase what FSR is pointing to
	MOVLW	0x0A	    ;If register == 10, clear it and excute same
	SUBWF	INDF1,W	    ;instructions on the next segment-register
	BNZ	EXIT	    ;If there was no carry -> Break
	CLRF	POSTINC1
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
	
EROWLU	MULLW	0x02	    ;Multiply map-value to make room for RETLW-opcode
	MOVF	PRODL,W	    ;Only needs the lower byte of the result since
	ADDWF	PCL	    ;collumn is in range: 0 <= col <= 3 
	RETLW	b'00111111'
	RETLW	b'00000110'
	RETLW	b'01011011'
	RETLW	b'01001111'
	RETLW	b'01100110'
	RETLW	b'01101101'
	RETLW	b'01111101'
	RETLW	b'00000111'
	RETLW	b'01111111'
	RETLW	b'01101111'
	
MINIT	CLRF	TRISA
	CLRF	TRISD
	CLRF	SEG0
	CLRF	SEG1
	CLRF	SEG2
	CLRF	SEG3
	CLRF	COUNTER
	SETF	DELAY
	RETURN
	
EDSHORT	MOVLW	0xFF
	MOVWF	0x0F0
LDLOOP	DECFSZ	0x0F0
	BRA	LDLOOP
	RETURN
	
	end