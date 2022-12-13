#include "p18F4520.inc"
    
    CONFIG  OSC = HS
    CONFIG  PWRT = OFF
    CONFIG  WDT = OFF
    CONFIG  PBADEN = OFF
    CONFIG  LVP = OFF
    
LCD_CTRL    equ	PORTB	;Used for sending to display
LCD_DATA    equ	PORTD
RS	    equ	RB3
EN	    equ	RB1
	    
C_DATA	    equ 0x000	;Used in program
TCOUNT	    equ	0x001
C_INTL	    equ	0x002
C_INTU	    equ	0x003
COUNTER	    equ 0x004
SAVEDA	    equ	0x005
SAVEDD	    equ	0x006
	    
	org	0x00000
	BRA	MAIN
	
	org	0x00008
	BTFSC	INTCON,TMR0IF
	CALL	T0_ISR
	BTFSC	INTCON,INT0IF
	CALL	SW0_ISR
	BTFSC	INTCON3,INT2IF
	CALL	SW1_ISR
	RETFIE
	
	org	0x00100
MAIN	CALL	MINIT
MLOOP	CALL	SLIGHTS
	BRA	MLOOP
	
MINIT	CLRF	C_DATA		;Clears all the registers
	CLRF	TCOUNT
	MOVLW	A'0'
	MOVWF	C_INTL
	MOVWF	C_INTU
	CLRF	COUNTER
	CLRF	SAVEDA
	CLRF	SAVEDD
	
	BCF	TRISA,RA1	;First collumn is open
	BCF	PORTA,RA1
	CLRF	TRISB		;Off FIRSTLY for setting up the display
	CLRF	TRISD
	
	BCF	LCD_CTRL,EN	;Enables display
	CALL	ED320MS
	
	MOVLW	upper(_ICOM)	;Setup INIT-commands to display
	MOVWF	TBLPTRU
	MOVLW	high(_ICOM)
	MOVWF	TBLPTRH
	MOVLW	low(_ICOM)
	MOVWF	TBLPTRL
	CALL	ERCL
	
	MOVLW	upper(_IDATA)	;Setup "Avbrott:" to display
	MOVWF	TBLPTRU
	MOVLW	high(_IDATA)
	MOVWF	TBLPTRH
	MOVLW	low(_IDATA)
	MOVWF	TBLPTRL
	CALL	ERDL
	
	MOVLW	b'10101010'	;Init value for C_DATA (PORTD)
	MOVWF	C_DATA
	MOVLW	b'10000111'	;Enable Timer0, sets pre-scale = 256x
	MOVWF	T0CON	
	MOVLW	b'10110000'	;Enable interupts for global, timer0, sw0
	MOVWF	INTCON
	MOVLW	b'00010000'	;Enable interupts for sw2
	MOVWF	INTCON3
	RCALL	T0RES
	
	CALL	UPTINT
	
	SETF	PORTB		;On LATER to enable input from switches
	RETURN
	


SLIGHTS	MOVFF	C_DATA,PORTD	;Put data-register out on PORTD
	BSF	PORTA,RA1
	CALL	EDSHORT
	BCF	PORTA,RA1
	CLRF	PORTD
	RETURN
	
ILIGHTS	CLRF	TCOUNT		;Invert the data out on PORTD
	COMF	C_DATA
	RETURN
	
UPTINT	MOVLW	0x8A		;Updates "Interupt-count" to new number
	CALL	ECOMWR
	MOVF	C_INTL,W
	CALL	EDATAWR
	MOVLW	0x89
	CALL	ECOMWR
	MOVF	C_INTU,W
	CALL	EDATAWR
	RETURN
	
EINCINT	LFSR	1,0x002	    ;Starts increasing the right-most segment-register
	MOVLW	0x02	    ;Ammount of segment-registers
	MOVWF	COUNTER
LLOOPI	INCF	INDF1	    ;Increase what FSR is pointing to
	MOVLW	0x3A	    ;If register == A'9'+1, clear it and excute same
	SUBWF	INDF1,W	    ;instructions on the next segment-register
	BNZ	EXIT	    ;If there was no carry -> Break
	MOVLW	A'0'
	MOVWF	POSTINC1
	DECFSZ	COUNTER
	BRA	LLOOPI
EXIT	RETURN
	
ERCL	TBLRD*+		    ;Reads a full line of commands and sends to display
	MOVF	TABLAT,W
	IORLW	0x00
	BTFSC	STATUS,Z
	RETURN
	CALL	ECOMWR
	BRA	ERCL	
ERDL	TBLRD*+		    ;Reads a full line of data and sends to display
	MOVF	TABLAT,W
	IORLW	0x00
	BTFSC	STATUS,Z
	RETURN
	CALL	EDATAWR
	BRA	ERDL
	
ECOMWR	MOVWF	LCD_DATA    ;Sends a command to the display
	CLRF	TRISB
	BCF	LCD_CTRL,RS
	BSF	LCD_CTRL,EN
	CALL	EDSHORT
	BCF	LCD_CTRL,EN
	SETF	TRISB
	RETURN	
EDATAWR	MOVWF	LCD_DATA    ;Sends one byte of data to display
	CLRF	TRISB
	BSF	LCD_CTRL,RS
	BSF	LCD_CTRL,EN
	CALL	EDSHORT
	BCF	LCD_CTRL,EN
	SETF	TRISB
	RETURN

EDSHORT	MOVLW	0xFF
	MOVWF	0x0F0
LDLOOP0	DECFSZ	0x0F0
	BRA	LDLOOP0
	RETURN	
ED10MS  MOVLW	0x20
	MOVWF	0x0F1
LDLOOP1 CALL	EDSHORT
	DECFSZ	0x0F1
	BRA	LDLOOP1
	RETURN
ED80MS	MOVLW	0xFF
	MOVWF	0x0F2
LDLOOP2	CALL	EDSHORT
	DECFSZ	0x0F2
	BRA	LDLOOP2
	RETURN	
ED320MS	MOVLW	0x04
	MOVWF	0x0F3
LDLOOP3	CALL	ED80MS
	DECFSZ	0x0F3
	BRA	LDLOOP3
	CALL	ED10MS
	RETURN	
	
T0RES	BCF	INTCON,TMR0IF	;Reset timer to 1 ms (runs 4x with 256 pre-scaler ---> 1024 ms == 1 second)
	MOVLW	0xF6
	MOVWF	TMR0H
	MOVLW	0x3D
	MOVWF	TMR0L
	INCF	TCOUNT
	BTFSC	TCOUNT,2
	CALL	ILIGHTS
	RETURN
	
T0_ISR	CALL	T0RES		;Timer0 interupt-handler
	RETURN	
SW0_ISR BCF	INTCON,7
	MOVLW	b'10101010'	;Switch0 interupt-handler
	MOVWF	C_DATA		;Change to new data
	CALL	EINCINT		;Increase interupt-number on display
	MOVFF	PORTA,SAVEDA	;Saves PORTA and PORTD to later be recovered
	MOVFF	PORTD,SAVEDD	;after messing with the display
	CLRF	PORTA
	CLRF	PORTD	
	CALL	UPTINT		;Put new number on display
	MOVFF	SAVEDA,PORTA	;Recovers PORTA and PORTD
	MOVFF	SAVEDD,PORTD
	BCF	INTCON,INT0IF	;Reset interupt-flag
	RETURN
SW1_ISR	BCF	INTCON,7
	MOVLW	b'11000011'	;Switch1 interupt-handler
	MOVWF	C_DATA		;Change to new data
	CALL	EINCINT		;Increase interupt-number on display
	MOVFF	PORTA,SAVEDA	;Saves PORTA and PORTD to later be recovered
	MOVFF	PORTD,SAVEDD	;after messing with the display
	CLRF	PORTA
	CLRF	PORTD
	CALL	UPTINT		;Put new number on display
	MOVFF	SAVEDA,PORTA	;Recovers PORTA and PORTD
	MOVFF	SAVEDD,PORTD
	BCF	INTCON3,INT2IF	;Reset interupt-flag
	RETURN
	
	org	0x01000
_ICOM	DB  0x38,0x0E,0x01,0x06,0x84,0x0C,0x80,0
_IDATA	DB  "Avbrott: ",0
	
	end