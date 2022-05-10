#pragma  config  OSC = HS                       // Oscillator Selection bits (HS oscillator)
#pragma  config  PWRT = OFF                     // Power-up Timer Enable bit (PWRT disabled)
#pragma  config  WDT = OFF                      // Watchdog Timer Enable bit (WDT disabled)
#pragma  config  PBADEN = OFF                   // PORTB A/D Enable bit (digital I/O on Reset)
#pragma  config  LVP = OFF                      // Single-Supply ICSP Enable bit (disabled)

#include "FreeRTOS.h"

#define LED_X 8
#define LED_Y 8

void delay();
char BinaryTo8BitDot(char binary);
void INIT();
void Render(char frameBuffer[LED_X]);
void Draw(char x, char y, char frameBuffer[LED_X]);

struct Player{
    
};


struct Player CreatePlayer()
{
    struct Player p_ret;
    return p_ret;
}

void PlayerUpdate(struct Player* player)
{
   
}


void PlayerDraw(struct Player* player, char frameBuffer[LED_X])
{
   
}


void main( void )
{
    char frameBuffer[LED_X] = {0};
    
    INIT();
    
    struct Player player = CreatePlayer();
    
    
    char shortCounter = 0;
    char longCounter = 0;
    
    while(1){
               
        Render(frameBuffer);
    }
}


void delay(){
    volatile char c = 0;
    for(int i = 0; i < 100; i++)
        c++;
    return;
}

char BinaryTo8BitDot(char binary)
{
    binary = binary & 0x07;
    switch(binary){
        case 0:
            return 1;
        case 1:
            return 2;
        case 2:
            return 4;
        case 3:
            return 8;
        case 4:
            return 16;
        case 5:
            return 32;
        case 6:
            return 64;
        case 7:
            return 128;
        default:
            return 0;
    }
}

void INIT()
{
    TRISD = 0x00;
    PORTD = 0x00;
    TRISA = 0xC1;
    TRISC = 0xFE;
    TRISE = 0xF8;
    TRISBbits.RB0 = 1;
    PORTAbits.RA1 = 1;
}

void Render(char frameBuffer[LED_X])
{
    PORTAbits.RA1 = 0;
    PORTCbits.RC0 = 1;
    PORTD = frameBuffer[0];
    delay();
    PORTCbits.RC0 = 0;
        
    PORTEbits.RE2 = 1;
    PORTD = frameBuffer[1];
    delay();
    PORTEbits.RE2 = 0;
    
    PORTEbits.RE1 = 1;
    PORTD = frameBuffer[2];
    delay();
    PORTEbits.RE1 = 0;    

    PORTEbits.RE0 = 1;
    PORTD = frameBuffer[3];
    delay();
    PORTEbits.RE0 = 0;

    PORTAbits.RA5 = 1;
    PORTD = frameBuffer[4];
    delay();
    PORTAbits.RA5 = 0;

    PORTAbits.RA3 = 1;
    PORTD = frameBuffer[5];
    delay();
    PORTAbits.RA3 = 0;

    PORTAbits.RA2 = 1;
    PORTD = frameBuffer[6];
    delay();
    PORTAbits.RA2 = 0;

    PORTAbits.RA1 = 1;
    PORTD = frameBuffer[7];
    delay();
    PORTAbits.RA1 = 0;
    
    for(int i = 0; i < LED_X;i++)
    {
        frameBuffer[i] = 0x00;
    }
    PORTD = 0x00;
    PORTAbits.RA1 = 1;
}

void Draw(char x, char y, char frameBuffer[LED_X]){
    frameBuffer[x] = frameBuffer[x] | BinaryTo8BitDot(y);
}