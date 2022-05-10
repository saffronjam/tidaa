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
	    
COORD	    equ	0x030
COORD_R	    equ	0x031
NIBBLE	    equ 0x032
TMPADD	    equ	0x033
        
	org	0x00000
	GOTO	MAIN
	
	org	0x00008
	BTFSC	PIR1,RCIF
	CALL	ESRXISR,FAST
	RETFIE
	
	org 0x00020
	YLU	MULLW	0x04
	MOVF	PRODL,W
	ADDWF	PCL
	BSF	PORTD,RD0
	RETURN
	BSF	PORTD,RD1
	RETURN
	BSF	PORTD,RD2
	RETURN
	BSF	PORTD,RD3
	RETURN
	BSF	PORTD,RD4
	RETURN
	BSF	PORTD,RD5
	RETURN
	BSF	PORTD,RD6
	RETURN
	BSF	PORTD,RD7
	RETURN
	NOP
	RETURN
	
XLU	MULLW	0x04
	MOVF	PRODL,W	    
	ADDWF	PCL
	BSF	PORTA,RA1
	RETURN
	BSF	PORTA,RA2
	RETURN
	BSF	PORTA,RA3
	RETURN
	BSF	PORTA,RA5
	RETURN
	BSF	PORTE,RE0
	RETURN
	BSF	PORTE,RE1
	RETURN
	BSF	PORTE,RE2
	RETURN
	BSF	PORTC,RC0
	RETURN
	NOP
	RETURN	
	
MAIN	CALL	MINIT
	CALL	ESINIT
LMLOOP	CALL    EC8NXTQ     ; PORTD assigned to 8*8/7-seg!
        CALL    EC8NEXT     ; Activate next column...
	CALL    EKDOWNQ     ;    Scan associated kbd column!
	BTFSC	WREG,7
	GOTO	LMLOOP
	MOVWF	TMPADD
	
	CALL	ED500MS	
	MOVF	TMPADD,W
	CALL	ADDTOCH
	
	MOVF	COORD,W
	BTFSC	NIBBLE,1
	CALL	ESWRITE
		
	GOTO	LMLOOP
	GOTO	$
	
ADDTOCH	BTFSS	NIBBLE,0
	CALL	ROTATEW
	ADDWF	COORD
	BTG	NIBBLE,0
	BTFSS	NIBBLE,0
	BTG	NIBBLE,1
	RETURN	
	
ROTATEW	RLNCF	WREG
	RLNCF	WREG
	RLNCF	WREG
	RLNCF	WREG
	RETURN
	
MINIT	CLRF	COORD
	CLRF	NIBBLE
	CLRF	TMPADD
	
	CLRF	TRISA
	SETF	PORTA
	CLRF	PORTD
	CALL	EKINIT
	CALL	EC8INIT
	
	SETF	PORTB
	
	RETURN
	
WRTD	BCF	PORTA,RA1
	BCF	PORTA,RA2
	BCF	PORTA,RA3
	BCF	PORTA,RA5
	BCF	PORTE,RE0
	BCF	PORTE,RE1
	BCF	PORTE,RE2
	BCF	PORTC,RC0
	CLRF	PORTD
	MOVF	COORD_R,W
	ANDLW	0x0F
	CALL	YLU
	MOVF	COORD_R,W
	ANDLW	0xF0
	RRNCF	WREG
	RRNCF	WREG
	RRNCF	WREG
	RRNCF	WREG
	CALL	XLU
	RETURN
	
;***********************************************************
;*** Serial Toolbox 1.0 AC *********************************
;***********************************************************
ESRXISR	MOVFF	RCREG,COORD_R
	CALL	WRTD
	BTG	NIBBLE,1
	CLRF	COORD
	RETURN	FAST
	
ESINIT	MOVLW	0x07
	MOVWF	ADCON1
	MOVLW	B'10000010'
	MOVWF	TRISC
	CLRF	TRISA
	CLRF	TRISD
	BSF	PORTA,1
	
	MOVLW	B'10010000'
	MOVWF	RCSTA
	MOVLW	B'00100110'
	MOVWF	TXSTA
	MOVLW	D'65'
	MOVWF	SPBRG
	
	MOVLW	B'00100000'
	MOVWF	PIE1
	MOVLW	B'11000000'
	MOVWF	INTCON
	RETURN
	
ESWRITE	BTFSS	PIR1,TXIF
	GOTO	ESWRITE
	MOVWF	TXREG
	RETURN

;*** END Serial Toolbox ************************************	
	
	
;*** DELAY Package ************** 1.0 ****************** AND *
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
;*** End of DELAY Package *************************************
	
	
;***********************************************************
;*** COL toolbox 1.1 AC ************************************
;*** Needs RA1,2,3,5,RE0,1,2,RC0 ***************************
;*** RAM usage: 0x000 **************************************
;***********************************************************
IC8COL  equ     0x000
IC8TMP	equ	0x001

EC8INIT BCF     TRISA,RA1       ; RA1 Out: Col 1
        BCF     TRISA,RA2       ; RA2 Out: Col 2
        BCF     TRISA,RA3       ; RA3 Out: Col 3
        BCF     TRISA,RA5       ; RA5 Out: Col 4
        BCF     TRISE,RE0       ; RE0 Out: Col 5
        BCF     TRISE,RE1       ; RE1 Out: Col 6
        BCF     TRISE,RE2       ; RE2 Out: Col 7
        BCF     TRISC,RC0       ; RC0 Out: Col 8
        CLRF    IC8COL          ; Next Col is Col 1.
        RETURN

EC8COLQ MOVF    IC8COL,W        ; Return current Col!
        RETURN

EC8NXTQ INCF    IC8COL,W        ; Return next Col!
        BTFSC   WREG,3          ; Wrap-around?
        CLRF    WREG            ; Yes, Adjust!
        RETURN

EC8NEXT CALL    EC8NXTQ         ; Advance to next Col!
        MOVWF   IC8COL

        RLNCF   WREG            ; Case next column is...
        RLNCF   WREG
	MOVWF	IC8TMP		; Temp. store offset
	MOVLW	high(LC8CASE)	; Middle 8 bit of jmp tbl
	MOVWF	PCLATH
	MOVLW	low(LC8CASE)	; Low 8 bit of jmp tbl...
	ADDWF	IC8TMP,W	; ...add offset...
	BTFSC	STATUS,C	; ...if carry
	INCF	PCLATH		; ......adjust PCH+1
        MOVWF   PCL             ; ...jump!
LC8CASE BSF     PORTA,RA1       ; ...1
        RETURN
        BSF     PORTA,RA2       ; ...2
        RETURN
        BSF     PORTA,RA3       ; ...3
        RETURN
        BSF     PORTA,RA5       ; ...4
        RETURN
        BSF     PORTE,RE0       ; ...5
        RETURN
        BSF     PORTE,RE1       ; ...6
        RETURN
        BSF     PORTE,RE2       ; ...7
        RETURN
        BSF     PORTC,RC0       ; ...8
        RETURN
;*** END COL toolbox ***************************************
		
	
;***********************************************************
;*** KEY toolbox 1.1 AC ******** (Monkey safe) *************
;*** Needs RB0,2,4,5 ***************************************
;*** RAM usage: 0x003..5 ***********************************
;*** Returns 0xFF = No key, or 0x00..0F = key pressed ******
;*** Dependensis: EC8COLQ **********************************
;***********************************************************
IKTEMP  equ 0x003
IKPREV  equ 0x004
IKTIME  equ 0x005

EKINIT  BSF     TRISB,RB0       ; Configure kbd sense lines!
        BSF     TRISB,RB2
        BSF     TRISB,RB4
        BSF     TRISB,RB5
        SETF    IKPREV          ; No key pressed.
	MOVLW	0x01		; Accept "any"...
        MOVWF   IKTIME		; ...key as a new key!
        RETURN

EKDOWNQ CALL	EC8COLQ		; Every 8:th time...
	DECFSZ	WREG
	GOTO	LKDOWNQ
	DCFSNZ	IKTIME		; ...dec repeat counter,
	INCF	IKTIME		;          but only to one!
LKDOWNQ	SETF    WREG            ; Key pressed? (Assume not!)
        BTFSS   PORTB,RB0
        CLRF    WREG
        BTFSS   PORTB,RB2
        MOVLW   0x01
        BTFSS   PORTB,RB4
        MOVLW   0x02
        BTFSS   PORTB,RB5
        MOVLW   0x03
        BTFSC   WREG,7
        RETURN                  ; No!  return!
	
        MOVWF   IKTEMP          ; Yes, Which...
        CALL    EC8COLQ         ;    ...column...
        RLNCF   WREG
        RLNCF   WREG
        IORWF   IKTEMP          ;    ...add row...
        MOVF    IKPREV,W
        XORWF   IKTEMP,W        ;    ...same???...
        BNZ     LKNEW

        MOVF    IKTEMP,W        ;    ...yes, repeat?
        DECFSZ  IKTIME
LKSAFE  SETF    WREG            ;       ...no  return 0xFF!
        RETURN                  ;       ...yes else prev.k!

LKNEW   DECFSZ  IKTIME, W       ;    ...no! Monky check?
	GOTO	LKSAFE		;	    Too early, monky!
	MOVF	IKTEMP, W	;	    New key...
        MOVWF   IKPREV          ;           ...rememeber it!
        CLRF    IKTIME          ;           reset repeat cnt
        RETURN
;*** END KEY toolbox ***************************************
	

	
	
        end