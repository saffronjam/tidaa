#include "p18F4520.inc"
    
    CONFIG  OSC = HS
    CONFIG  PWRT = OFF
    CONFIG  WDT = OFF
    CONFIG  PBADEN = OFF
    CONFIG  LVP = OFF
        
			    ;ECTIMER	- External Counter Timer
			    ;ECUL	- External Counter Update Ligths
    
OVER5S	equ	0x000	    ;Holds flag for if timer > 5 seconds
CTOP	equ	0x001	    ;Counter for TOP part
CBOT	equ	0x002	    ;Counter for BOT part
	
	org	0x00000
PORST	BRA	MAIN
	
	org	0x00020
MAIN	RCALL	MINIT	    ;Initializes values
LMLOOPM	BTFSS	PORTB,RB0   ;Waits for switch to be pulled
	BRA	LMLOOPM	    
	RCALL	ECTIMER	    ;Starts timer
LMLOOPI	BTFSC	PORTB,RB0   ;Waits for switch to be pulled back
	BRA	LMLOOPI	    
	MOVF	OVER5S	    ;Updates flags for OVER5S
	BZ	INCB	    ;Increases bottom if timer < 5
	BTFSS	CTOP,4	    ;Only increases if less than 4 points
	RLNCF	CTOP	    ;Rotates bit to increase point (See SUBROUTINE ECUL)
	RCALL	ECUL	    ;Updates the lights accordingly
	CLRF	OVER5S	    ;Clears OVER5S for a new test
	BRA	LMLOOPM	    ;Goes back to the start
INCB	BTFSS	CBOT,4	    ;Only increases if less than 4 points
	RLNCF	CBOT	    ;Rotates bit to increase point (See SUBROUTINE ECUL)
	RCALL	ECUL	    ;Updates the lights accordingly	    
	CLRF	OVER5S	    ;Clears OVER5S for a new test
	BRA	LMLOOPM	    ;Goes back to the start
	BRA	$	    ;Plan B to halt the program!
		
ECTIMER	MOVLW	0x27	    ;Loads nested loop timer:
	MOVWF	0x0F0	    ;0x27 * 0xFF * 0xFF = 2.5 million loops 
LCT0	SETF	0x0F1	    ;Roughly 2 instructions per loop
LCT1	SETF	0x0F2	    ;5 million instructions
LCT2	BTFSS	PORTB,RB0   ;Continue as long as switch is not pulled back
	BZ	LCOVER	    ;JUMP OUT if switch is suddenly pulled back
	DECFSZ	0x0F2
	BRA	LCT2
	DECFSZ	0x0F1
	BRA	LCT1
	DECFSZ	0x0F0
	BRA	LCT0
	BSF	OVER5S,0    ;Timer was not interupted -> OVER5S = true
LCOVER	RETURN
	
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
	CLRF	OVER5S
	MOVLW	0x01	    ;Initiates CTOP and CBOT
	MOVWF	CTOP	    ;with 1 so the "Rotate Right"
	MOVWF	CBOT	    ;will work when increasing point
	RETURN
	
	end