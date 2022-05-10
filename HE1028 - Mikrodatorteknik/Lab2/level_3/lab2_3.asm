#include "p18F4520.inc"
    
    CONFIG  OSC = HS
    CONFIG  PWRT = OFF
    CONFIG  WDT = OFF
    CONFIG  PBADEN = OFF
    CONFIG  LVP = OFF
        
PLAYER	equ	0x000
SEG1	equ	0x001
SEG2	equ	0x002
SEG3	equ	0x003
SEG4	equ	0x004
SEG5	equ	0x005
SEG6	equ	0x006
SEG7	equ	0x007
COUNTER	equ	0x008
LOST	equ	0x009
DELAY	equ	0x00A
DELAY_R	equ	0x00B
P_DIR	equ	0x00C

	org	0x00000
	BRA	MAIN
	
	org	0x00020
MAIN	RCALL	MINIT
LMLOOP	RCALL	EDISP		;Display all cars
	RCALL	ETRYC		;Test if player collided
	MOVF	LOST
	BNZ	EXIT
	RCALL	EDSHORT
	DECFSZ	DELAY		;Display = short delay and Logic = long delay
	BRA	LMLOOP
	MOVFF	DELAY_R, DELAY
	MOVLW	0x03		;Shortens reset-delay to make it harder!
	SUBWF	DELAY_R
	RCALL	EMOVPL		;Update player movement (Up/Down)
	RCALL	EROTGB		;Move all cars to the right
	RCALL	EINSNC		;Add a new car from left
	BRA	LMLOOP
EXIT	RCALL	EDISP		;Continues showing cars after losing ("Freeze")
	BRA	EXIT
	
MINIT	CLRF	TRISA
	CLRF	TRISE
	BCF	TRISC,0
	CLRF	TRISD
	CLRF	PORTA
	CLRF	PORTC
	CLRF	PORTE
	MOVLW	0x01
	MOVWF	PLAYER
	MOVLW	0x03
	MOVWF	SEG1
	MOVWF	SEG2
	MOVWF	SEG3
	MOVWF	SEG4
	MOVWF	SEG5
	MOVWF	SEG6
	MOVWF	SEG7
	SETF	DELAY
	SETF	DELAY_R
	MOVLW	0x0A
	RCALL	ERSEED		;Seeds the RNG
	CLRF	LOST
	RETURN
	
EMOVPL	CLRF	P_DIR		;Check what direction player is going
	BTFSC	PORTB,0		
	DECF	P_DIR		;If play is going BOTH up and DOWN,
	BTFSC	PORTB,5		;then the netto result will be 0, thus
	INCF	P_DIR		;staying in the same place
	BZ	OUT
	MOVLW	0x01
	SUBWF	P_DIR,W
	BZ	UP
	MOVLW	0x02
	SUBWF	PLAYER,W
	BZ	OUT
	INCF	PLAYER
	BRA	OUT
UP	MOVF	PLAYER
	BZ	OUT
	DECF	PLAYER
OUT	RETURN
	
EROWLU	MULLW	0x02	    ;Multiply map-value to make room for RETLW-opcode
	MOVF	PRODL,W	    ;Only needs the lower byte of the result since
	ADDWF	PCL	    ;collumn is in range: 0 <= col <= 3 
	RETLW	b'00000001' ;Maps position value to correct 7-seg display value
	RETLW	b'01000000'
	RETLW	b'00001000'
	RETLW	b'00000000'
	
ECOLLU	MULLW	0x04
	MOVF	PRODL,W
	ADDWF	PCL
	BSF	PORTC,RC0
	RETURN
	BSF	PORTE,RE2
	RETURN
	BSF	PORTE,RE1
	RETURN
	BSF	PORTE,RE0
	RETURN
	BSF	PORTA,RA5
	RETURN
	BSF	PORTA,RA3
	RETURN
	BSF	PORTA,RA2
	RETURN
	BSF	PORTA,RA1
	RETURN
	
ETRYC	MOVF	SEG1,W	    ;If: (player position value) == (seg position value) 
	SUBWF	PLAYER,W    ;then: lost = true
	BTFSC	STATUS,Z
	BSF	LOST,0
	RETURN	
	
EROTGB	LFSR	0,0x001		    ;Pointer to car1
	LFSR	1,0x002		    ;Pointer to car2
	MOVLW	0x06
	MOVWF	COUNTER
LLOOPI	MOVFF	POSTINC1,POSTINC0   ;Overwrites car1 with car2
	DECFSZ	COUNTER
	BRA	LLOOPI
	RETURN
	
EINSNC	RCALL	ERRAND		    ;New random value
	ANDLW	0x03		    ;Mask the 3 LSB
	MOVWF	SEG7		    ;Put it in the left most segment
	SUBLW	0x03
	BZ	EINSNC
	RETURN
	
EDISP	LFSR	0,0x000
	MOVLW	0x07
	MOVWF	COUNTER
LLOOPD	MOVF	COUNTER,W
	RCALL	ECOLLU
	RCALL	EDISPS
	MOVF	COUNTER,W
	CLRF	PORTA
	CLRF	PORTC
	CLRF	PORTE
	DECF	COUNTER
	BNN	LLOOPD
	RETURN
	
EDISPS	MOVF	POSTINC0,W
	RCALL	EROWLU
	MOVWF	PORTD
	RCALL	EDSHORT
	RETURN
	
EDSHORT	MOVLW	0xFF
	MOVWF	0x0F0
LDLOOP	DECFSZ	0x0F0
	BRA	LDLOOP
	RETURN
	
EDLONG	MOVLW	0xFF
	MOVWF	0x0F1
LDLOOPL	RCALL	EDSHORT
	DECFSZ	0x0F1
	BRA	LDLOOPL
	RETURN
	
	
	
	
;***********************************************************
;*** 16-bit Pseudo Random Gen. toolbox ********** 1.0 * AC *
;*** Out of a 16-b shift reg with taps at bit 3,12,14 & 15 *
;*** Needs - ***********************************************
;*** RAM 0x070..0x071 **************************************
;*** Dependensis - *****************************************
;***********************************************************
	
IRMSB	equ	0x070		; PRG MSB
IRLSB	equ	0x071		; PRG LSB
 
ERSEED	MOVWF	IRLSB		; Load seed to LSB...
	SETF	IRMSB		;         ...and preset MSB!
	RETURN
 
ERRAND	MOVF	IRMSB,w         ; if MSB +
        IORWF	IRLSB,w         ;    LSB is all zeros...
	BTFSC	STATUS,Z        ;	       ...preset MSB!
        COMF	IRMSB
	
        BTFSC	IRMSB,0x6	; if MSB.6...
        BTG	IRMSB,0x7       ;          ...toggle MSB.7
        BTFSC	IRMSB,0x4       ; if MSB.4...
        BTG	IRMSB,0x7       ;          ...toggle MSB.7
        BTFSC	IRLSB,0x3       ; if LSB.3...
        BTG	IRMSB,0x7       ;          ...toggle MSB.7
        RLCF	IRMSB		; Move MSB.7 into carry. 
	RRNCF	IRMSB		; Shift back MSB.
        RLCF	IRLSB           ; Rot C into LSB.0, LSB.7 to C. 
        RLCF	IRMSB           ; Rot C into MSB.0. 
	MOVF	IRLSB,W		; Return LSB
        RETURN	
;*** EOT 16-bit PRG ****************************************	
	end