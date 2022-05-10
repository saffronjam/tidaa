#include "p18F4520.inc"
    
    CONFIG  OSC = HS
    CONFIG  PWRT = OFF
    CONFIG  WDT = OFF
    CONFIG  PBADEN = OFF
    CONFIG  LVP = OFF
        
COUNTER	equ	0x000
CTOP	equ	0x001
CBOT	equ	0x002
	
	org	0x00000
	BRA	MAIN
	
	org	0x00020
MAIN	RCALL	MINIT
LMLOOP0	BTFSC	PORTB,RB0   ;Waits for switch to be pulled back
	BRA	LMLOOP0
LMLOOP1	BTFSC	PORTB,RB5   ;Waits for switch to be pulled back
	BRA	LMLOOP1
	
LMLOOP2	BTFSS	PORTB,RB0   ;Waits for switch to be pulled
	BRA	LMLOOP2
	CLRF	COUNTER
LMCOUNT	RCALL	ED250MS	    ;Wait 0.25 seconds
	INCF	COUNTER	    ;Increase COUNTER
	MOVLW	0x14	    ;Since counter holds 0.25s we need to try to
	SUBWF	COUNTER,W   ;subtract 0x14 (4*5) to see if we reached 5 sec
	BZ	INCTOP	    ;If counter is 5 seconds, increase top
	BTFSS	PORTB,RB5   ;Check if switch is set
	BRA	LMCOUNT
	BRA	INCBOT	    ;Increase bot

INCTOP
LMLOOP3	BTFSS	PORTB,RB5   ;Waits for switch to be pulled
	BRA	LMLOOP3
	RLNCF	CTOP
	RCALL	ECUL
	BRA	LMLOOP0	
INCBOT	RLNCF	CBOT
	RCALL	ECUL
	BRA	LMLOOP0
	
	BRA	$
	
ECUL	MOVLW	0x00	    ;Ligts in bottom:
	BTFSC	CBOT,1	    ;If CBOT = '0000 0001' light up '0000 0001'
	ADDLW	0x01
	BTFSC	CBOT,2	    ;If CBOT = '0000 0010' light up '0000 0011'
	ADDLW	0x03
	BTFSC	CBOT,3	    ;If CBOT = '0000 0100' light up '0000 0111'
	ADDLW	0x07
	BTFSC	CBOT,4	    ;If CBOT = '0000 1000' light up '0000 1111'
	ADDLW	0x0F
	
	BTFSC	CTOP,1	    ;If CTOP = '0000 0001' light up '0001 0000'
	ADDLW	0x10
	BTFSC	CTOP,2	    ;If CTOP = '0000 0010' light up '0011 0000'
	ADDLW	0x30
	BTFSC	CTOP,3	    ;If CTOP = '0000 0100' light up '0111 0000'
	ADDLW	0x70
	BTFSC	CTOP,4	    ;If CTOP = '0000 1000' light up '1111 0000'
	ADDLW	0xF0
	
	MOVWF	PORTD
	RETURN
	
MINIT	CLRF	TRISA	    ;PORTA is OUTPUT
	SETF	TRISB	    ;PORTB is INPUT  <OPTIONAL LINE>
	CLRF	TRISD	    ;PORTD is OUTPUT
	CLRF	PORTA
	BSF	PORTA,RA1   ;Right-most is collumn always open
	CLRF	PORTD
	MOVLW	0x01	    ;Initiates CTOP and CBOT
	MOVWF	CTOP	    ;with 1 so the "Rotate Right"
	MOVWF	CBOT	    ;will work when increasing point
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