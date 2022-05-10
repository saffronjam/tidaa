#include "p18F4520.inc"

  CONFIG  OSC = HS              ; Oscillator Selection bits 
  CONFIG  PWRT = OFF            ; Power-up Timer Enable bit 
  CONFIG  WDT = OFF             ; Watchdog Timer Enable bit 
  CONFIG  PBADEN = OFF          ; PORTB A/D Enable bit 
  CONFIG  LVP = OFF             ; Single-Supply ICSP Enable bit

        org 0x00000
PORST   GOTO MAIN
	
	org 0x00008
	BTFSC	PIR1,RCIF
	CALL	ESRXISR,FAST
	RETFIE
	
        org 0x00020
	
MODE	equ	0x050
COORD_S	equ	0x051
COORD_R	equ	0x052
BCD	equ	0x053
	
MAPY	MULLW	0x4
	MOVFF	PRODL, WREG
	ADDWF	PCL
	BSF	PORTA, RA1
	RETURN
	BSF	PORTA, RA2
	RETURN
	BSF	PORTA, RA3
	RETURN
	BSF	PORTA, RA5
	RETURN
	BSF	PORTE, RE0
	RETURN
	BSF	PORTE, RE1
	RETURN
	BSF	PORTE, RE2
	RETURN
	BSF	PORTC, RC0
	RETURN
	
MAPX	MULLW	0x4
	MOVFF	PRODL, WREG
	ADDWF	PCL
	BSF	PORTD, RD7
	RETURN
	BSF	PORTD, RD6
	RETURN
	BSF	PORTD, RD5
	RETURN
	BSF	PORTD, RD4
	RETURN
	BSF	PORTD, RD3
	RETURN
	BSF	PORTD, RD2
	RETURN
	BSF	PORTD, RD1
	RETURN
	BSF	PORTD, RD0
	RETURN
	
	
;***********************************************************
;*** Serial Toolbox 1.0 AC *****************************
;***********************************************************
ESRXISR	MOVFF	RCREG,COORD_R
	CLRF	RCREG
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

;*** END Serial Toolbox ********************************	
	
	
;***********************************************************
;*** COL toolbox 1.1 AC ********************************
;*** Needs RA1,2,3,5,RE0,1,2,RC0 ***********************
;*** RAM usage: 0x000 **********************************
;***********************************************************
IC8COL  equ     0x000
IC8TMP	equ	0x001

EC8INIT BCF     TRISA,RA1       ; RA1 Out: Col 1
        BCF     TRISA,RA2       ; RA2 Out: Col 2
        BCF     TRISA,RA3       ; RA3 Out: Col 3
        BCF     TRISA,RA5       ; RA5 Out: Col 4
        CLRF    IC8COL          ; Next Col is Col 1.
        CALL    EC8FREE         ; Make sure Col X are off!
        RETURN

EC8COLQ MOVF    IC8COL,W        ; Return current Col!
        RETURN

EC8NXTQ INCF    IC8COL,W        ; Return next Col!
        BTFSC   WREG,3          ; Wrap-around?
        CLRF    WREG            ; Yes, Adjust!
        RETURN

EC8FREE BCF     PORTA,RA1       ; Turn off any Col X on!
        BCF     PORTA,RA2
        BCF     PORTA,RA3
        BCF     PORTA,RA5
        BCF     PORTE,RE0
        BCF     PORTE,RE1
        BCF     PORTE,RE2
        BCF     PORTC,RC0
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
;*** END COL toolbox ***********************************


;***********************************************************
;*** KEY toolbox 1.1 AC **** (Monkey safe) *********
;*** Needs RB0,2,4,5 ***********************************
;*** RAM usage: 0x003..5 *******************************
;*** Returns 0xFF = No key, or 0x00..0F = key pressed **
;*** Dependensis: EC8COLQ ******************************
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
;*** END KEY toolbox ***********************************

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

MAIN    CALL    EC8INIT     ; Init COL manager!
        CALL    EKINIT      ; Init KEY manager!
	CALL	ESINIT
	CALL	INIT
	
XLOOP   CLRF	PORTD
	CALL    EC8NXTQ     ; PORTD assigned to 8*8/7-seg!
        CALL    EC8NEXT     ; Activate next column...a
        CALL    EKDOWNQ     ;    Scan associated kbd column!
        BTFSS   WREG,7      ;    Skip if no key pressed?
	CALL	PUSHKEY
	MOVF	COORD_R,W
	SUBLW	0xFF
	BZ	XLOOP
	
	CLRF	PORTD
	BCF	PORTC,RC0
	BCF	PORTE,RE2
	BCF	PORTE,RE1
	BCF	PORTE,RE0
	BCF	PORTA,RA5
	BCF	PORTA,RA3
	BCF	PORTA,RA2
	BCF	PORTA,RA1
	
	MOVFF	COORD_R,WREG
	ANDLW	b'11110000'
	SWAPF	WREG
	CALL	MAPY
	
	MOVFF	COORD_R,WREG
	ANDLW	b'00001111'
	CALL	MAPX
	
	BRA	$
	
PUSHKEY	CPFSGT	BCD
	RETURN
	
	TSTFSZ	MODE
	BRA	SEC_KEY
	
	ANDLW	0x0F
	SWAPF	WREG
	ADDWF	COORD_S
	
	SETF	MODE
	CALL	ED500MS
	RETURN
	
SEC_KEY	ADDWF	COORD_S
	MOVF	COORD_S,W
	CALL	ESWRITE
	CALL	ED500MS
	
	CLRF	MODE
	CLRF	COORD_S
	RETURN
	
INIT	CLRF	MODE
	CLRF	COORD_S
	SETF	COORD_R
	MOVLW	0x09
	MOVWF	BCD
	BCF	TRISE,RE0
	BCF	TRISE,RE1
	BCF	TRISE,RE2
	RETURN
		
        END