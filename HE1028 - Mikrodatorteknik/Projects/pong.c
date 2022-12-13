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

struct Ball{
    float pos_x;
    float pos_y;
    float vel_x;
    float vel_y;
};

struct Player{
    unsigned char pos;
    unsigned char score;
    char direction;
    unsigned char id;
};

struct Ball CreateBall()
{
    struct Ball b_ret;
    b_ret.pos_x = 3.0f;
    b_ret.pos_y = 3.0f;
    b_ret.vel_x = 1.0f;
    b_ret.vel_y = 0.0f;
    return b_ret;
}

void BallUpdateMovement(struct Ball* ball)
{
   if(ball->pos_x > 0 && 
       ball->pos_x < LED_X - 1) //&&
       //ball->pos_y >= 0 && 
       //ball->pos_y <= LED_Y - 1)
    {
        ball->pos_x += ball->vel_x;
        ball->pos_y += ball->vel_y;
    }
}

void BallUpdateCollision(struct Ball* ball, struct Player* p0, struct Player* p1)
{
    const char next_x = ball->pos_x + ball->vel_x;
    const float next_y = ball->pos_y + ball->vel_y;
    
    char hitLeft  = next_x == 0         && next_y <= p0->pos + 1 && next_y >= p0->pos - 1;
    char hitRight = next_x == LED_X - 1 && next_y <= p1->pos + 1 && next_y >= p1->pos - 1;
    char hitRoof = next_y > LED_Y - 1;
    char hitFloor = next_y == -1;
    
    if(hitLeft)
    {
        if(p0->direction == 1){
            ball->vel_y += 1.0f;
            ball->vel_x = 1.0f;
        }else if(p0->direction == 2){
            ball->vel_y += -1.0f;
            ball->vel_x = 1.0f;
        }else if(p0->direction == 3){
            ball->vel_x *= -1.0f;
        }
    }
    if(hitRight)
    {
        if(p1->direction == 1){
            ball->vel_y += 1.0f;
            ball->vel_x = -1.0f;
        }else if(p1->direction == 2){
            ball->vel_y += -1.0f;
            ball->vel_x = -1.0f;
        }else if(p1->direction == 3){
            ball->vel_x *= -1.0f;
        }
    }
    if(hitRoof)
        ball->vel_y *= -1.0f;
    if(hitFloor)
        ball->vel_y *= -1.0f;
    
    if(ball->vel_y > 1.0f){
        ball->vel_y = 1.0f;
    }else if(ball->vel_y < -1.0f){
        ball->vel_y = -1.0f;
    }
    
}

void BallDraw(struct Ball* ball, char frameBuffer[LED_X])
{
    frameBuffer[(char)ball->pos_x] = frameBuffer[(char)ball->pos_x] | BinaryTo8BitDot((char)ball->pos_y);
}

struct Player CreatePlayer(char id)
{
    struct Player p_ret;
    p_ret.pos = 3;
    p_ret.direction = 0;
    p_ret.score = 0;
    p_ret.id = id;
    return p_ret;
}

void PlayerUpdate(struct Player* player)
{
    if(player->pos > 1 && player->direction == 2)
    {
        player->pos--;
    }
    else if(player->pos < LED_Y - 2 && player->direction == 1)
    {
        player->pos++;
    }else{
        player->direction = 3;
    }
}

void PlayerDraw(struct Player* player, char frameBuffer[LED_X])
{
    char top = player->pos + 1;
    char bot = player->pos - 1;
    char i = player->id * (LED_X - 1);
    frameBuffer[i] = frameBuffer[player->id * (LED_X - 1)] | BinaryTo8BitDot(top);
    frameBuffer[i] = frameBuffer[player->id * (LED_X - 1)] | BinaryTo8BitDot(player->pos);
    frameBuffer[i] = frameBuffer[player->id * (LED_X - 1)] | BinaryTo8BitDot(bot);
}

void INIT()
{
    TRISD = 0x00;
    PORTD = 0x00;
    TRISA = 0xC1;
    TRISC = 0xFE;
    TRISE = 0xF8;
    TRISBbits.RB0 = 1;
    TRISBbits.RB0 = 5;
    PORTAbits.RA1 = 1;
}

void Draw(char frameBuffer[LED_X])
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

void main( void )
{
    char frameBuffer[LED_X] = {0};
    
    INIT();
       
    struct Player p0 = CreatePlayer(0);
    struct Player p1 = CreatePlayer(1);
    struct Ball ball = CreateBall();
    
    char counterFast = 0;
    char counterSlow = 0;
    
    while(1){
        p0.direction = 0;
        if(PORTBbits.RB0 && PORTBbits.RB2)
        {
            p0.direction = 3;
        }
        else{
            if(PORTBbits.RB0)
            {
                p0.direction = 2;
            }else if(PORTBbits.RB2){
                p0.direction = 1;
            }
        }
        p1.direction = 0;
        if(PORTBbits.RB4 && PORTBbits.RB5)
        {
            p1.direction = 3;
        }
        else{
            if(PORTBbits.RB4)
            {
                p1.direction = 2;
            }else if(PORTBbits.RB5){
                p1.direction = 1;
            }
        }
        
        if(counterFast == 14){
            PlayerUpdate(&p0);
            PlayerUpdate(&p1);
            BallUpdateCollision(&ball, &p0, &p1);
            counterFast = 0;
        }else{
            counterFast++;
        }
        if(counterSlow == 20){
            BallUpdateMovement(&ball);
            counterSlow = 0;
        }
        else{
            counterSlow++;
        }
        
        
        PlayerDraw(&p0, frameBuffer);
        PlayerDraw(&p1, frameBuffer);
        BallDraw(&ball, frameBuffer);
        
        Draw(frameBuffer);
        
    }
    
}