#include "p18F4520.inc"
    
    CONFIG  OSC = HS
    CONFIG  PWRT = OFF
    CONFIG  WDT = OFF
    CONFIG  PBADEN = OFF
    CONFIG  LVP = OFF
    
WORM	equ	0x050
        
	org	0x00000
	BRA	MAIN
	
	org	0x00008
	BTFSC	PORTB,RB0
	CALL	INTSW0,FAST
	RETFIE
	
	org	0x00020
MAIN	RCALL	MINIT
LMLOOP	MOVFF	WORM,PORTD
	RLNCF	WORM
	BTFSC	WORM,6
	CALL	RESWORM
	
	RCALL	ED500MS
	BRA	LMLOOP
	BRA	$
	
RESWORM	CLRF	WORM
	INCF	WORM
	RETURN
	
INTSW0	BCF	INTCON,INT0IF	;Reset interupt-flag
	CALL	ED500MS
	CALL	ED500MS
	CALL	ED500MS
	CALL	ED500MS
	CALL	ED500MS
	CALL	ED500MS	
	RETURN	FAST
	
MINIT	CALL	RESWORM
	BCF	TRISA,1
	BSF	TRISB,0
	CLRF	TRISD
	BSF	PORTA,1
	
	MOVLW	b'10010000'	;Enable interupts for global, sw0
	MOVWF	INTCON
	RETURN

;*** DELAY Package ********** 1.0 ************** AND *
IDSHORT equ     0x07D           ; Delay short counter
IDLONG  equ     0x07E           ; Delay long counter
IDVERYL equ     0x07F           ; Delay very long counter

ED300US MOVLW   0x00
        MOVWF   IDSHORT
LNSLOOP DECFSZ  IDSHORT
        GOTO    LNSLOOP
        RETURN

ED10MS  CLRF    IDSHORT         ; Reset delay counters.
        MOVLW   0x20
        MOVWF   IDLONG
LSLOOP  DECFSZ  IDSHORT         ; Count down...
        GOTO    LSLOOP          ; ...until zero!
        DECFSZ	IDLONG          ; Count down again...
        GOTO	LSLOOP          ; ...until zero!
        RETURN	

ED500MS MOVLW	0x32            ; Reset delay counters.
        MOVWF	IDVERYL
LDLOOP  CALL	ED10MS          ; Wait
        DECFSZ  IDVERYL         ; Count down...
        GOTO    LDLOOP          ; ...until zero!
        RETURN
;*** End of DELAY Package *********************************
	
	end