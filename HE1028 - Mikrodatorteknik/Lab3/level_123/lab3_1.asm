#include "p18F4520.inc"    
    CONFIG  OSC = HS
    CONFIG  PWRT = OFF
    CONFIG  WDT = OFF
    CONFIG  PBADEN = OFF
    CONFIG  LVP = OFF
LCD_CTRL    equ	PORTB
LCD_DATA    equ	PORTD
RS	    equ	RB3
EN	    equ	RB1
BLINKING    equ	0x000      
	org	0x00000
	BRA	MAIN	
	org	0x00020
MAIN	RCALL	MINIT	
	MOVLW	0x80
	RCALL	ECOMWR	
	MOVLW	upper(_NAME)
	MOVWF	TBLPTRU
	MOVLW	high(_NAME)
	MOVWF	TBLPTRH
	MOVLW	low(_NAME)
	MOVWF	TBLPTRL
	RCALL	ERDL
	
	MOVLW	0xC0
	RCALL	ECOMWR	
	MOVLW	upper(_TITLE)
	MOVWF	TBLPTRU
	MOVLW	high(_TITLE)
	MOVWF	TBLPTRH
	MOVLW	low(_TITLE)
	MOVWF	TBLPTRL
	RCALL	ERDL	
	
LMLOOP	MOVLW	0x8F
	RCALL	ECOMWR
	SETF	TRISB
	CLRF	WREG
	BTFSC	PORTB,RB0
	BSF	WREG,0
	BTFSC	PORTB,RB2
	BSF	WREG,1
	BTFSC	PORTB,RB4
	BSF	WREG,2
	CLRF	TRISB
	
	MOVF	WREG
	BTFSC	STATUS,Z
	RCALL	EBLINK
	RCALL	EDATAWR
	RCALL	ED320MS
	BRA	LMLOOP
	
EBLINK	BTG	BLINKING,0
	BTFSC	BLINKING,0
	RETLW	0xFE
	RETLW	0x01
	
MINIT	CLRF	TRISB
	CLRF	TRISD
	CLRF	BLINKING	
	BCF	LCD_CTRL,EN
	RCALL	ED320MS

	MOVLW	0x48
	RCALL	ECOMWR	
	MOVLW	upper(_IDCG1)
	MOVWF	TBLPTRU
	MOVLW	high(_IDCG1)
	MOVWF	TBLPTRH
	MOVLW	low(_IDCG1)
	MOVWF	TBLPTRL
	RCALL	ERDL
	
	MOVLW	0x50
	RCALL	ECOMWR	
	MOVLW	upper(_IDCG2)
	MOVWF	TBLPTRU
	MOVLW	high(_IDCG2)
	MOVWF	TBLPTRH
	MOVLW	low(_IDCG2)
	MOVWF	TBLPTRL
	RCALL	ERDL
	
	MOVLW	0x58
	RCALL	ECOMWR	
	MOVLW	upper(_IDCG3)
	MOVWF	TBLPTRU
	MOVLW	high(_IDCG3)
	MOVWF	TBLPTRH
	MOVLW	low(_IDCG3)
	MOVWF	TBLPTRL
	RCALL	ERDL
	
	MOVLW	0x60
	RCALL	ECOMWR	
	MOVLW	upper(_IDCG4)
	MOVWF	TBLPTRU
	MOVLW	high(_IDCG4)
	MOVWF	TBLPTRH
	MOVLW	low(_IDCG4)
	MOVWF	TBLPTRL
	RCALL	ERDL
	
	MOVLW	0x68
	RCALL	ECOMWR	
	MOVLW	upper(_IDCG5)
	MOVWF	TBLPTRU
	MOVLW	high(_IDCG5)
	MOVWF	TBLPTRH
	MOVLW	low(_IDCG5)
	MOVWF	TBLPTRL
	RCALL	ERDL
	
	MOVLW	0x70
	RCALL	ECOMWR	
	MOVLW	upper(_IDCG6)
	MOVWF	TBLPTRU
	MOVLW	high(_IDCG6)
	MOVWF	TBLPTRH
	MOVLW	low(_IDCG6)
	MOVWF	TBLPTRL
	RCALL	ERDL
	
	MOVLW	0x78
	RCALL	ECOMWR	
	MOVLW	upper(_IDCG7)
	MOVWF	TBLPTRU
	MOVLW	high(_IDCG7)
	MOVWF	TBLPTRH
	MOVLW	low(_IDCG7)
	MOVWF	TBLPTRL
	RCALL	ERDL
	
	MOVLW	upper(_ICOM)
	MOVWF	TBLPTRU
	MOVLW	high(_ICOM)
	MOVWF	TBLPTRH
	MOVLW	low(_ICOM)
	MOVWF	TBLPTRL
	RCALL	ERCL
	RETURN
	
ERCL	TBLRD*+
	MOVF	TABLAT,W
	IORLW	0x00
	BTFSC	STATUS,Z
	RETURN
	RCALL	ECOMWR
	RCALL	ED10MS
	BRA	ERCL	
ERDL	TBLRD*+
	MOVF	TABLAT,W
	IORLW	0x00
	BTFSC	STATUS,Z
	RETURN
	RCALL	EDATAWR
	RCALL	ED10MS
	BRA	ERDL
	
ECOMWR	MOVWF	LCD_DATA
	BCF	LCD_CTRL,RS
	BSF	LCD_CTRL,EN
	RCALL	ED10MS
	BCF	LCD_CTRL,EN
	RETURN	
EDATAWR	MOVWF	LCD_DATA
	BSF	LCD_CTRL,RS
	BSF	LCD_CTRL,EN
	RCALL	ED10MS
	BCF	LCD_CTRL,EN
	RETURN
	
EDSHORT	MOVLW	0xFF
	MOVWF	0x0F0
LDLOOP0	DECFSZ	0x0F0
	BRA	LDLOOP0
	RETURN	
ED10MS  MOVLW	0x20
	MOVWF	0x0F1
LDLOOP1	RCALL	EDSHORT
	DECFSZ	0x0F1
	BRA	LDLOOP1
	RETURN
ED80MS	MOVLW	0xFF
	MOVWF	0x0F2
LDLOOP2	RCALL	EDSHORT
	DECFSZ	0x0F2
	BRA	LDLOOP2
	RETURN	
ED320MS	MOVLW	0x04
	MOVWF	0x0F3
LDLOOP3	RCALL	ED80MS
	DECFSZ	0x0F3
	BRA	LDLOOP3
	RCALL	ED10MS
	RETURN
	
	org 0x500
_IDCG1	DB  0x0E,0x11,0x11,0x11,0x11,0x11,0xFF,0x80,0
_IDCG2	DB  0x0E,0x11,0x11,0x11,0x11,0xFF,0xFF,0x80,0
_IDCG3	DB  0x0E,0x11,0x11,0x11,0xFF,0xFF,0xFF,0x80,0
_IDCG4	DB  0x0E,0x11,0x11,0xFF,0xFF,0xFF,0xFF,0x80,0
_IDCG5	DB  0x0E,0x11,0xFF,0xFF,0xFF,0xFF,0xFF,0x80,0
_IDCG6	DB  0x0E,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0x80,0
_IDCG7	DB  0x0E,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0x80,0
_ICOM	DB  0x38,0x0E,0x01,0x06,0x84,0x0C,0x80,0
_NAME	DB  "Emil Karlsson",0
_TITLE	DB  "Fattig student",0
	end