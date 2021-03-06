#include "p18F4520.inc"
    
    CONFIG  OSC = HS
    CONFIG  PWRT = OFF
    CONFIG  WDT = OFF
    CONFIG  PBADEN = OFF
    CONFIG  LVP = OFF
    
TCOUNT	equ	0x000
        
	org	0x00000
	BRA	MAIN
	
	org	0x00008
	BTFSC	INTCON,TMR0IF
	RCALL	T0_ISR
	BTFSC	INTCON,INT0IF
	RCALL	SW0_ISR
	BTFSC	INTCON3,INT2IF
	RCALL	SW1_ISR
	RETFIE
	
	org	0x00100
MAIN	RCALL	MINIT
	BRA	$
	
MINIT	BCF	TRISA,RA1
	BSF	PORTA,RA1
	CLRF	TRISD
	MOVLW	b'10101010'
	MOVWF	PORTD
	MOVLW	b'10000111'
	MOVWF	T0CON	
	MOVLW	b'10110000'
	MOVWF	INTCON
	MOVLW	b'00010000'
	MOVWF	INTCON3
	RCALL	T0RES
	RETURN
	
T0RES	BCF	INTCON,TMR0IF
	MOVLW	0xF6
	MOVWF	TMR0H
	MOVLW	0x3D
	MOVWF	TMR0L
	INCF	TCOUNT
	BTFSC	TCOUNT,2
	RCALL	LIGHTS
	RETURN

LIGHTS	CLRF	TCOUNT
	COMF	PORTD
	RETURN
	
T0_ISR	RCALL	T0RES
	RETURN
	
SW0_ISR	MOVLW	b'10101010'
	MOVWF	PORTD
	BCF	INTCON,INT0IF
	RETURN
	
SW1_ISR	MOVLW	b'11000011'
	MOVWF	PORTD
	BCF	INTCON3,INT2IF
	RETURN
	
	end