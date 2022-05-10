#include "p18F4520.inc"
    
    CONFIG  OSC = HS
    CONFIG  PWRT = OFF
    CONFIG  WDT = OFF
    CONFIG  PBADEN = OFF
    CONFIG  LVP = OFF
    
				;EPUL	- External Potentiometer Update Lights
    
PRES	equ	0x000		;PRES = (P)otentiometer (RES)ult
        
	org	0x00000
	BRA	MAIN
	
	org	0x00020
MAIN	RCALL	MINIT		;Initializes values: ADC,TRIS,PORT etc.
LMLOOPO	BSF	ADCON0,GO	;Starts the ADC
LMLOOPI	BTFSC	ADCON0,DONE	;Continues until ADC is DONE
				;DONE and GO points to same bit in ADCON0
				;0->ADC is not converting
				;1->ADC is converting
	BRA	LMLOOPI		
	MOVFF	ADRESH,PRES	;Moves the result of ADC to PRES
	RCALL	EPUL		;Updates the lights accordingly
	BRA	LMLOOPO		;Back to top!
	BRA	$
	
EPUL	MOVF	PRES,W		;Masks the value
	ANDLW	0xE0		;of ADC result (PRES)
	ANDWF	PRES		;to 3 MSB
	
	MOVLW	0x80		;The flag-reg for the lights 
	MOVWF	0x001		;0010 0000 -> Light: LED5
				;0000 0100 -> Light: LED3 etc.
	MOVLW	0xE0		;Start value for testing with PRES
	MOVWF	0x002
LPLOOP	MOVFF	PRES,0x003	;Moves PRES into temp-reg to not corrupt it
	MOVF	0x002,W
	SUBWF	0x003,F		;Tries to subtract
	BZ	LPANS		;and if result is zero -> Branch to LPANS
	RRNCF	0x001		;Otherwise, bit-shift flag value for lights
	MOVLW	0x20		;Next to subract is (previous - 0x20) 
	SUBWF	0x002		;because we only use the 3 MSB
	BNZ	LPLOOP		;Back to top
	
LPANS	CLRF	PORTD		;Shuts all the lights off
	BTFSC	0x001,0		;If flag[0] -> Light: LED 0
	BSF	PORTD,0
	BTFSC	0x001,1		;If flag[1] -> Light: LED 1
	BSF	PORTD,1
	BTFSC	0x001,2		;If flag[2] -> Light: LED 2
	BSF	PORTD,2
	BTFSC	0x001,3		;If flag[3] -> Light: LED 3
	BSF	PORTD,3
	BTFSC	0x001,4		;If flag[4] -> Light: LED 4
	BSF	PORTD,4
	BTFSC	0x001,5		;If flag[5] -> Light: LED 5
	BSF	PORTD,5
	BTFSC	0x001,6		;If flag[6] -> Light: LED 6
	BSF	PORTD,6
	BTFSC	0x001,7		;If flag[7] -> Light: LED 7
	BSF	PORTD,7
	RETURN	
	
MINIT	BSF	TRISA,AN0	;ADC has INPUT
	BCF	TRISA,RA1	;First collumn is OUTPUT
	CLRF	TRISD		;All rows are OUTPUTS
	MOVLW	0x81		;Sets correct flags for the ADC:
	MOVWF	ADCON0		;Flags chosen: (1000 0001)
				; - Converstion time: FOSC/2
				; - Channel: AN0
				; - ADON: True (turns on ADC)
	
	RETURN
	
	end