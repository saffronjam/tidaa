#include "p18F4520.inc"
    
    CONFIG  OSC = HS
    CONFIG  PWRT = OFF
    CONFIG  WDT = OFF
    CONFIG  PBADEN = OFF
    CONFIG  LVP = OFF
        
	org 0x00000
	BRA	MAIN
	
	org 0x00020
MAIN	RCALL	MINIT	    ;Initiate TRISA/B
LMLOOP	RCALL	EWRE	    ;(WR)ite (E) on board
	BRA	LMLOOP
	BRA	$
	
MINIT	CLRF    TRISA	    ;TRISA is OUTPUT
	CLRF    TRISD	    ;TRISB is OUTPUT
	RETURN
	
EWRE	MOVLW	0x54	    ;Writes
	MOVWF	PORTD	    ;3
	SETF	PORTA	    ;Lines
	RCALL	EDSHORT	    ;Delay to trick the eye
	MOVLW	0x7C	    ;Writes the
	MOVWF	PORTD	    ;BACK of
	MOVLW	0x20	    ;Letter
	MOVWF	PORTA	    ;E
	RCALL	EDSHORT	    ;Delay to trick the eye

	
EDSHORT	MOVLW	0xFF	    ;Short delay
	MOVWF	0x000
LDLOOP	DECFSZ	0x000
	BRA	LDLOOP
	RETURN
	
	end