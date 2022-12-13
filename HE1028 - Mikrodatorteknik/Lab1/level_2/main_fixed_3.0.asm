#include "p18F4520.inc"
    
    CONFIG  OSC = HS
    CONFIG  PWRT = OFF
    CONFIG  WDT = OFF
    CONFIG  PBADEN = OFF
    CONFIG  LVP = OFF
        
ITER	equ	0x000
CTOP	equ	0x001
CBOT	equ	0x002
	org	0x00000
	BRA	MAIN
	org	0x00020
MAIN	RCALL	MINIT
RESTART	MOVFF	CBOT,PORTD  ;Updates lights according to CBOT and CTOP
	MOVF	CTOP,W
	ADDWF	PORTD
LMLOOP0	BTFSS	PORTB,RB0   ;Waits for switches to be pulled back
	BTFSC	PORTB,RB5
	BRA	LMLOOP0
LMLOOP1	BTFSS	PORTB,RB0   ;Waits for switch to be pulled to start counter
	BRA	LMLOOP1
	MOVLW	0x14
	MOVWF	ITER
LMCOUNT	RCALL	ED250MS	    ;Wait 0.25 seconds
	BTFSC	PORTB,RB5   ;Check if switch is set
	BRA	INCBOT
	DECFSZ	ITER
	BRA	LMCOUNT
INCTOP	BTFSS	PORTB,RB5   ;Increases CTOP if counter managed whole loop
	BRA	INCTOP
	RLNCF	CTOP
	BSF	CTOP,4
	BRA	RESTART	
INCBOT	RLNCF	CBOT	    ;Increases CBOT if counter was interupted
	BSF	CBOT,0
	BRA	RESTART
	
MINIT	CLRF	TRISA	    ;PORTA is OUTPUT
	CLRF	TRISD	    ;PORTD is OUTPUT
	BSF	PORTA,RA1   ;Right-most is collumn always open
	CLRF	CTOP
	CLRF	CBOT
	RETURN
	
ED250MS	MOVLW	0x03
	MOVWF	0x0F0
LDL0	CLRF	0x0F1
LDL1	CLRF	0x0F2
LDL2	DECFSZ	0x0F2
	BRA	LDL2
	DECFSZ	0x0F1
	BRA	LDL1
	DECFSZ	0x0F0
	BRA	LDL0
	RETURN
	
	end