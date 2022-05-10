#include "p18F4520.inc"

  CONFIG  OSC = HS              ; Oscillator Selection bits 
  CONFIG  PWRT = OFF            ; Power-up Timer Enable bit 
  CONFIG  WDT = OFF             ; Watchdog Timer Enable bit 
  CONFIG  PBADEN = OFF          ; PORTB A/D Enable bit 
  CONFIG  LVP = OFF             ; Single-Supply ICSP Enable bit

        org 0x000000
PORST   GOTO MAIN

        org 0x000020
	
EMPCHAR	MULLW	0x02
	MOVF	PRODL,W	    
	ADDWF	PCL
	RETLW	b'00111111' ;0
	RETLW	b'00000110' ;1
	RETLW	b'01011011' ;2
	RETLW	b'01001111' ;3
	RETLW	b'01100110' ;4
	RETLW	b'01101101' ;5
	RETLW	b'01111101' ;6
	RETLW	b'00000111' ;7
	RETLW	b'01111111' ;8
	RETLW	b'01101111' ;9
	RETLW	b'01110111' ;A
	RETLW	b'01111100' ;B
	RETLW	b'00111001' ;C
	RETLW	b'01011110' ;D
	RETLW	b'01111001' ;E
	RETLW	b'01110001' ;F
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
;*** END COL toolbox ***************************************

;***********************************************************
;*** 8*8 LED Matrix toolbox 1.0 AC *************************
;*** Needs FSR0 & PDx **************************************
;*** RAM 0x010..0x017 **************************************
;***********************************************************
S88DISP EQU     0x010           ;Shared 8 Byte LED buffer.

;*** External E88INIT * Initalize HW/SW ********************
E88INIT CLRF    TRISD
        LFSR    0,S88DISP       ;FSR0 ptr to LED buffer.
        CLRF    S88DISP+0
        CLRF    S88DISP+1
        CLRF    S88DISP+2
        CLRF    S88DISP+3
        CLRF    S88DISP+4
        CLRF    S88DISP+5
        CLRF    S88DISP+6
        CLRF    S88DISP+7
        RETURN

;*** External E88VIEW * Update the matrix ******************
E88VIEW MOVF	PLUSW0,W    ; Present right column data!
	CALL	EMPCHAR
	MOVWF	PORTD
        RETURN
;*** END 8*8 LED Matrix toolbox ****************************

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

MAIN    CALL    EC8INIT     ; Init COL manager!
        CALL    EKINIT      ; Init KEY manager!
        CALL    E88INIT     ; Init LED manager!
        LFSR    1,S88DISP   ; LED Matrix data area!
	
XLOOP   CALL    EC8NXTQ     ; PORTD assigned to 8*8/7-seg!
        CALL    E88VIEW     ; Tell 8*8LED M to present data.

        CALL    EC8NEXT     ; Activate next column...
        CALL    EKDOWNQ     ;    Scan associated kbd column!
        BTFSS   WREG,7      ;    Skip if no key pressed?
        MOVWF   INDF1	    ;    Present key at next free spot!
        BTFSC   FSR1L,3     ;    Skip if not wrap-around!
        LFSR    1,S88DISP   ;    Wrap-around reset!
        CALL    ED300US     ;    Let it glow for a while...
MCONT   CALL    EC8FREE     ; ...Deaktivate current column!

                            ; PORTD free, if needed (LCD)!
        GOTO    XLOOP	    ; Again...forever!	
			
        END