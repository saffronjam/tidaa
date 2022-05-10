#pragma  config  OSC = HS                       // Oscillator Selection bits (HS oscillator)
#pragma  config  PWRT = OFF                     // Power-up Timer Enable bit (PWRT disabled)
#pragma  config  WDT = OFF                      // Watchdog Timer Enable bit (WDT disabled)
#pragma  config  PBADEN = OFF                   // PORTB A/D Enable bit (digital I/O on Reset)
#pragma  config  LVP = OFF                      // Single-Supply ICSP Enable bit (disabled)

/* Scheduler include files. */
#include "FreeRTOS.h"
#include "task.h"

/* The period between executions of the check task before and after an error
has been discovered.  If an error has been discovered the check task runs
more frequently - increasing the LED flash rate. */
#define mainNO_ERROR_CHECK_PERIOD		( ( TickType_t ) 1000 / portTICK_PERIOD_MS )
#define mainERROR_CHECK_PERIOD			( ( TickType_t ) 100 / portTICK_PERIOD_MS )

/* Priority definitions for some of the tasks.  Other tasks just use the idle
priority. */
#define mainQUEUE_POLL_PRIORITY			( tskIDLE_PRIORITY + 2 )
#define mainCHECK_TASK_PRIORITY			( tskIDLE_PRIORITY + 3 )

/* The LED that is flashed by the check task. */
#define mainCHECK_TASK_LED				( 0 )

/* Constants required for the communications.  Only one character is ever
transmitted. */
#define mainCOMMS_QUEUE_LENGTH			( 5 )
#define mainNO_BLOCK					( ( TickType_t ) 0 )
#define mainBAUD_RATE					( ( unsigned long ) 9600 )

#define LED_X 8
#define LED_Y 8

void delay();
char BinaryTo8BitDot(char binary);
void INIT();
void Render(char frameBuffer[LED_X]);
void Draw(char x, char y, char frameBuffer[LED_X]);

#include <stdlib.h>

struct Player{
    char pos_y;
    char height;
    char jumpCount;
    char falling_speed;
    char hasLost;
};

struct Obstacles{
    char data[20];
    char n;
    char offset;
};

struct Player CreatePlayer()
{
    struct Player p_ret;
    p_ret.pos_y = 1;
    p_ret.height = 3;
    p_ret.jumpCount = 0;
    p_ret.falling_speed = 0;
    p_ret.hasLost = 0;
    return p_ret;
}

void PlayerUpdate(struct Player* player, char obstacles[])
{
    player->height = 3;
    if(player->jumpCount != 0)
    {
        player->pos_y++;
        player->jumpCount--;
    }else if(player->pos_y != 1){
        if(player->pos_y > 2){
            player->pos_y -= player->falling_speed;
        }else
        {
            player->pos_y--; 
        }
        if(player->pos_y < 1)
            player->pos_y = 1;
    }
}

void PlayerUpdateCollision(struct Player* player, struct Obstacles* obs)
{
    for(char i = 0; i < obs->n; i++)
    {
        char curr = obs->data[i];
        if(curr){
            short drawPos = i * 8 + LED_X - obs->offset;
            if(drawPos == 1){
                switch(curr)
                {
                    case 1: //small cactus
                        if(player->pos_y <= 1)
                        {
                            player->hasLost = 1;
                        }
                        break;
                    case 2: //big cactus
                        if(player->pos_y <= 1)
                        {
                            player->hasLost = 1;
                        }
                        break;
                    case 3: //jump over, bird
                        if(player->pos_y <= 2)
                        {
                            player->hasLost = 1;
                        }
                        break;
                    case 4: //duck, bird
                        if(player->pos_y >= 2 || player->height != 2)
                        {
                            player->hasLost = 1;
                        }
                        break;
                }
            }
        }
    }
}

void PlayerDraw(struct Player* player, char frameBuffer[LED_X])
{
    for(char i = 0; i < player->height;i++)
        Draw(1, player->pos_y + i, frameBuffer);
}

char PlayerCanJump(struct Player* player)
{
    return player->pos_y == 1 && player->jumpCount == 0;
}

void PlayerJump(struct Player* player)
{
    if(PlayerCanJump(player))
    {
        player->jumpCount = 3;
        player->falling_speed = 1;
    }
}

void PlayerDuck(struct Player* player)
{
    player->falling_speed = 2;
    player->height = 2;
    player->jumpCount = 0;
}

struct Obstacles CreateObstacles()
{
    struct Obstacles o_ret;
    o_ret.n = 20;
    for(char i = 0; i < o_ret.n; i++)
        o_ret.data[i] = 0;
    o_ret.offset = 0;
    return o_ret;
}

void ObstaclesUpdate(struct Obstacles* obs)
{
    obs->offset++;
    if(obs->offset == 160)
    {
        obs->offset = 0;
    }
}

void ObstaclesDraw(struct Obstacles* obs, char frameBuffer[])
{
    for(char i = 0; i < obs->n; i++)
    {
        char curr = obs->data[i];
        if(curr){
            short drawPos = i * 8 + LED_X - obs->offset;
            if(drawPos >= 0 && drawPos < LED_X){
                if(curr == 1)
                {
                    Draw(drawPos, 1, frameBuffer);
                }else if(curr == 2)
                {
                    Draw(drawPos, 1, frameBuffer);
                    Draw(drawPos + 1, 1, frameBuffer);
                }else if(curr == 3)
                {
                    Draw(drawPos, 2, frameBuffer);
                    Draw(drawPos + 1, 2, frameBuffer);
                }else if(curr == 4)
                {
                    Draw(drawPos, 3, frameBuffer);
                    Draw(drawPos + 1, 3, frameBuffer);
                }
            }
        }
    }
}

void ObstaclesGenerateData(struct Obstacles* obs)
{
    obs->data[0] = 1;
    obs->data[1] = 1;
    obs->data[2] = 2;
    obs->data[3] = 3;
    obs->data[4] = 4;
    obs->data[5] = 3;
    obs->data[6] = 2;
    obs->data[7] = 4;
    obs->data[8] = 3;
    obs->data[9] = 1;
    obs->data[10] = 3;
    obs->data[11] = 4;
    obs->data[12] = 2;
    obs->data[13] = 3;
    obs->data[14] = 2;
    obs->data[15] = 1;
    obs->data[16] = 1;
    obs->data[17] = 4;
    obs->data[18] = 3;
    obs->data[19] = 3;
}

void main( void )
{
    char frameBuffer[LED_X] = {0};
    char obstacles[10] = {1,0,0,0,2,0,0,0,1,1};
    
    INIT();
    
    struct Player player = CreatePlayer();
    struct Obstacles obs = CreateObstacles();
    
    ObstaclesGenerateData(&obs);
    
    char shortCounter = 0;
    char longCounter = 0;
    char uniqueCounter = 0;
    char uniqueCounterMAX = 15;
    
    while(1){
        if(!player.hasLost)
        {
            if(shortCounter > 10)
            {
                PlayerUpdate(&player, obstacles);
                shortCounter = 0;
            }else{
                shortCounter++;
            }

            if(uniqueCounter > uniqueCounterMAX)
            {
                ObstaclesUpdate(&obs);
                uniqueCounter = 0;
            }else{
                uniqueCounter++;
            }

            if(longCounter > 60)
            {
                if(uniqueCounterMAX > 8){
                    uniqueCounterMAX--;
                }
                longCounter = 0;
            }else{
                longCounter++;
            }
            
            PlayerUpdateCollision(&player, &obs);


            if(!PORTBbits.RB0){
                PlayerJump(&player);
            }
            if(!PORTBbits.RB2){
                PlayerDuck(&player);
            }
        }
        
        
        PlayerDraw(&player, frameBuffer);
        ObstaclesDraw(&obs, frameBuffer);
        
        for(char i = 0; i < LED_X; i++)
        {
            Draw(i, 0, frameBuffer);
        }
        
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