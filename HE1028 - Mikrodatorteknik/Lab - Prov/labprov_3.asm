#include "p18F4520.inc"
    
    CONFIG  OSC = HS
    CONFIG  PWRT = OFF
    CONFIG  WDT = OFF
    CONFIG  PBADEN = OFF
    CONFIG  LVP = OFF
    
WORM	equ	0x050
TIMER	equ	0x051
        
	org	0x00000
	BRA	MAIN
	
	org	0x00008
	BTFSC	INTCON,TMR0IF
	CALL	INTT0,FAST
	RETFIE
	
	org	0x00020
MAIN	RCALL	MINIT
LMLOOP	CLRF	TIMER
	BTFSC	PORTB,RB0
	INCF	TIMER
	BTFSC	PORTB,RB2
	INCF	TIMER
	BTFSC	PORTB,RB4
	INCF	TIMER
	BTFSC	PORTB,RB5
	INCF	TIMER
	
	MOVF	TIMER,W
	CALL	TIMERLU
	MOVWF	TIMER
	MOVLW	b'11111000'
	ANDWF	T0CON
	MOVF	TIMER,W
	ADDWF	T0CON
	
	BRA	LMLOOP
	BRA	$
	
TIMERLU	MULLW	0x02
	MOVF	PRODL,W	    
	ADDWF	PCL
	RETLW	b'00000111'
	RETLW	b'00000110'
	RETLW	b'00000100'
	RETLW	b'00000010'
	RETLW	b'00000000'
	
UPTWORM	MOVFF	WORM,PORTD
	RLNCF	WORM
	BTFSC	WORM,6
	CALL	RESWORM
	RETURN
	
RESWORM	CLRF	WORM
	INCF	WORM
	RETURN
	
INTT0	BCF	INTCON,TMR0IF	;Reset timer to 1 ms (runs 4x with 256 pre-scaler ---> 1024 ms == 1 second)
	CALL	UPTWORM
	RETURN	FAST
	
MINIT	CALL	RESWORM
	BCF	TRISA,1
	BCF	TRISA,RA1
	CLRF	TRISD
	BSF	PORTA,1
	
	MOVLW	b'10000111'	;Enable Timer0, sets pre-scale = 256x
	MOVWF	T0CON
	MOVLW	b'10100000'	;Enable interupts for global, sw0
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