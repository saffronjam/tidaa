#pragma  config  OSC = HS                       // Oscillator Selection bits (HS oscillator)
#pragma  config  PWRT = OFF                     // Power-up Timer Enable bit (PWRT disabled)
#pragma  config  WDT = OFF                      // Watchdog Timer Enable bit (WDT disabled)
#pragma  config  PBADEN = OFF                   // PORTB A/D Enable bit (digital I/O on Reset)
#pragma  config  LVP = OFF                      // Single-Supply ICSP Enable bit (disabled)

#include "FreeRTOS.h"

#define LED_X 8
#define LED_Y 8
#define BULLETS 10
#define eBULLETS 3

void delay();
char BinaryTo8BitDot(char binary);
void INIT();
void Render(char frameBuffer[LED_X]);
void Draw(char x, char y, char frameBuffer[LED_X]);

struct Bullet{
    char pos_x;
    char pos_y;
};

struct Player{
    char pos_x;
    char shootTimer;
    struct Bullet bInAir[BULLETS];
};

struct Enemy{
    char pos_x;
    char dir_x;
    char shootTimer;
    char isDead;
    struct Bullet bInAir[eBULLETS];
};

struct Wave{
    struct Enemy enemies[5];
    char nEnemies;
    char difficulty;
    char shootTimerMAX;
};


struct Player CreatePlayer()
{
    struct Player p_ret;
    p_ret.pos_x = 3;
    p_ret.shootTimer = 0;
    for(char i = 0; i < BULLETS; i++)
    {
        p_ret.bInAir[i] = (struct Bullet){0,0};
    }
    return p_ret;
}

void PlayerUpdate(struct Player* player)
{
    if(!PORTBbits.RB0 && player->pos_x > 0)
    {
        player->pos_x--;
    }
    if(!PORTBbits.RB2 && player->pos_x < LED_X - 1)
    {
        player->pos_x++;
    }
    
    //UPDATES BULLETS BEFORE SPAWNING THEM
    for(char i = 0; i < BULLETS; i++)
    {
        if(player->bInAir[i].pos_y != 0)
        {
            if(player->bInAir[i].pos_y < LED_Y - 1)
            {
                player->bInAir[i].pos_y++;
            }else{
                player->bInAir[i].pos_y = 0;
            }
        }
    }
    
    if(!PORTBbits.RB4 && player->shootTimer == 0)
    {
        char i = 0;
        for(; i < BULLETS; i++)
        {
            if(player->bInAir[i].pos_y == 0)
            {
                break;
            }
        }
        player->bInAir[i] = (struct Bullet){player->pos_x, 1};
        player->shootTimer = 3;
    }
    
    if(player->shootTimer != 0)
    {
        player->shootTimer--;
    }
}

void PlayerUpdateCollision(struct Player* player, struct Wave* wave)
{
    for(char i = 0; i < BULLETS; i++)
    {
        if(player->bInAir[i].pos_y != 0){
            for(char j = 0 ; j < wave->nEnemies; j++)
            {
                if(player->bInAir[i].pos_x == wave->enemies[j].pos_x &&
                   player->bInAir[i].pos_y == LED_Y - 1)
                {
                    wave->enemies[j].isDead = 1;
                }
                for(int k = 0; k < eBULLETS; k++)
                {
                    if(wave->enemies[j].bInAir[k].pos_y != LED_Y - 1 &&
                       player->bInAir[i].pos_x == wave->enemies[j].bInAir[k].pos_x &&
                       (player->bInAir[i].pos_y == wave->enemies[j].bInAir[k].pos_y ||
                       player->bInAir[i].pos_y - 1 == wave->enemies[j].bInAir[k].pos_y))
                    {
                        player->bInAir[i].pos_y = 0;
                        wave->enemies[j].bInAir[k].pos_y = LED_Y - 1;
                    }
                }
            }
        }
    }
}

void PlayerDraw(struct Player* player, char frameBuffer[LED_X])
{
    Draw(player->pos_x, 0, frameBuffer);
    for(char i = 0; i < BULLETS; i++)
    {
        if(player->bInAir[i].pos_y != 0)
        {
            Draw(player->bInAir[i].pos_x, player->bInAir[i].pos_y, frameBuffer);
        }
    }
}

struct Enemy CreateEnemy()
{
    struct Enemy e_ret;
    e_ret.pos_x = 0;
    e_ret.dir_x = 0;
    e_ret.shootTimer = 0;
    e_ret.isDead = 1;
    for(char i = 0; i < eBULLETS;i++)
    {
        e_ret.bInAir[i] = (struct Bullet){0,LED_Y - 1};
    }
    return e_ret;
}

void EnemyUpdate(struct Enemy* enemy, struct Wave* wave)
{
    for(char i = 0; i < eBULLETS; i++)
    {
        if(enemy->bInAir[i].pos_y != LED_Y - 1)
        {
            if(enemy->bInAir[i].pos_y > 0)
            {
                enemy->bInAir[i].pos_y--;
            }else{
                enemy->bInAir[i].pos_y = LED_Y - 1;
            }
        }
    }
    
    if(!enemy->isDead)
    {
        if(enemy->shootTimer == 0)
        {
            char i = 0;
            for(; i < eBULLETS; i++)
            {
                if(enemy->bInAir[i].pos_y == LED_Y - 1)
                {
                    break;
                }
            }
            enemy->bInAir[i] = (struct Bullet){enemy->pos_x, LED_X - 2};
            enemy->shootTimer = wave->shootTimerMAX;
        }

        if(enemy->shootTimer != 0)
        {
            enemy->shootTimer--;
        }
    }
}

void EnemyDraw(struct Enemy* enemy, char frameBuffer[])
{
    if(enemy->dir_x != 0)
    {
        if(!enemy->isDead)
        {
            Draw(enemy->pos_x, LED_Y -1, frameBuffer);
        }
        for(char i = 0; i < eBULLETS; i++)
        {
            if(enemy->bInAir[i].pos_y != LED_Y - 1)
            {
                Draw(enemy->bInAir[i].pos_x, enemy->bInAir[i].pos_y, frameBuffer);
            }
        }
    }
}

struct Wave CreateWave(char difficulty)
{
    struct Wave w_ret;
    
    for(char i = 0; i < 5; i++)
    {
        w_ret.enemies[i] = CreateEnemy();
    }
    
    w_ret.difficulty = difficulty;
    switch(w_ret.difficulty)
    {
        case 1:
            w_ret.nEnemies = 1;
            for(char i = 0; i < w_ret.nEnemies;i++)
            {
                w_ret.enemies[i].pos_x = (i + 1) * 3;
                w_ret.enemies[i].dir_x = 1;
                w_ret.enemies[i].isDead = 0;
                w_ret.shootTimerMAX = 7;
            }
            break;
        case 2:
            w_ret.nEnemies = 2;
            for(char i = 0; i < w_ret.nEnemies;i++)
            {
                w_ret.enemies[i].pos_x = (i + 1) * 2;
                w_ret.enemies[i].dir_x = 1 + (i % 2);
                w_ret.enemies[i].shootTimer = i * 2;
                w_ret.enemies[i].isDead = 0;
                w_ret.shootTimerMAX = 7;
            }
            break;
        case 3:
            w_ret.nEnemies = 3;
            for(char i = 0; i < w_ret.nEnemies;i++)
            {
                w_ret.enemies[i].pos_x = 1 + i * 2;
                w_ret.enemies[i].dir_x = 1;
                w_ret.enemies[i].isDead = 0;
                w_ret.shootTimerMAX = 7;
                
            }
            break;
        case 4:
            w_ret.nEnemies = 4;
            for(char i = 0; i < w_ret.nEnemies;i++)
            {
                w_ret.enemies[i].pos_x = i * 2;
                w_ret.enemies[i].dir_x = 1;
                w_ret.enemies[i].isDead = 0;
                w_ret.shootTimerMAX = 7;
            }
            break;
        case 5:
            w_ret.nEnemies = 4;
            for(char i = 0; i < w_ret.nEnemies;i++)
            {
                w_ret.enemies[i].pos_x = i * 2;
                w_ret.enemies[i].dir_x = 1;
                w_ret.enemies[i].isDead = 0;
                w_ret.shootTimerMAX = 5;
            }
            break;
        default:
            break;
    }
    return w_ret;
}

void WaveUpdate(struct Wave* wave)
{
    for(char i = 0; i < wave->nEnemies; i++)
    {
        switch(wave->difficulty)
        {
            case 1:
                if(wave->enemies[i].pos_x > 0 && wave->enemies[i].dir_x == 1)
                {
                    wave->enemies[i].pos_x--;
                }else if(wave->enemies[i].pos_x == 0 && wave->enemies[i].dir_x == 1)
                {
                    wave->enemies[i].dir_x = 2;
                }
                else if(wave->enemies[i].pos_x < LED_X - 1 && wave->enemies[i].dir_x == 2)
                {
                    wave->enemies[i].pos_x++;
                }else if(wave->enemies[i].pos_x == LED_X - 1 && wave->enemies[i].dir_x == 2)
                {
                    wave->enemies[i].dir_x = 1;
                }
                break;
            case 2:
                if(wave->enemies[i].pos_x > 0 && wave->enemies[i].dir_x == 1)
                {
                    wave->enemies[i].pos_x--;
                }else if(wave->enemies[i].pos_x == 0 && wave->enemies[i].dir_x == 1)
                {
                    wave->enemies[i].dir_x = 2;
                }
                else if(wave->enemies[i].pos_x < LED_X - 1 && wave->enemies[i].dir_x == 2)
                {
                    wave->enemies[i].pos_x++;
                }else if(wave->enemies[i].pos_x == LED_X - 1 && wave->enemies[i].dir_x == 2)
                {
                    wave->enemies[i].dir_x = 1;
                }
                break;
            case 3:
                if(wave->enemies[i].dir_x == 1)
                {
                    wave->enemies[i].pos_x++;
                    wave->enemies[i].dir_x = 2;
                }else if(wave->enemies[i].dir_x == 2)
                {
                    wave->enemies[i].pos_x--;
                    wave->enemies[i].dir_x = 1;
                }
                break;
            case 4:
                if(wave->enemies[i].dir_x == 1)
                {
                    wave->enemies[i].pos_x++;
                    wave->enemies[i].dir_x = 2;
                }else if(wave->enemies[i].dir_x == 2)
                {
                    wave->enemies[i].pos_x--;
                    wave->enemies[i].dir_x = 1;
                }
                break;
            case 5:
                if(wave->enemies[i].pos_x > 0 && wave->enemies[i].dir_x == 1)
                {
                    wave->enemies[i].pos_x--;
                }else if(wave->enemies[i].pos_x == 0 && wave->enemies[i].dir_x == 1)
                {
                    wave->enemies[i].dir_x = 2;
                }
                else if(wave->enemies[i].pos_x < LED_X - 1 && wave->enemies[i].dir_x == 2)
                {
                    wave->enemies[i].pos_x++;
                }else if(wave->enemies[i].pos_x == LED_X - 1 && wave->enemies[i].dir_x == 2)
                {
                    wave->enemies[i].dir_x = 1;
                }
            default:
                break;
        }
        EnemyUpdate(&wave->enemies[i], wave);
    }
}

void WaveDraw(struct Wave* wave, char frameBuffer[LED_X])
{
    for(char i = 0; i < wave->nEnemies; i++)
    {
        EnemyDraw(&wave->enemies[i], frameBuffer);
    }
}

void main( void )
{
    char frameBuffer[LED_X] = {0};
    
    INIT();
    
    struct Player player = CreatePlayer();
    struct Wave wave = CreateWave(1);
    
    
    char shortCounter = 0;
    char longCounter = 0;
    
    char currentDifficulty = 1;
    
    while(1){
        char allDead = 1;
        for(char i = 0; i < wave.nEnemies; i++)
        {
            if(!wave.enemies[i].isDead)
                allDead = 0;
        }
        if(allDead)
        {
            wave = CreateWave(++currentDifficulty);
        }
        if(shortCounter > 10)
        {
            PlayerUpdate(&player);
            WaveUpdate(&wave);
            shortCounter = 0;
        }else
        {
            shortCounter++;
        }
        PlayerUpdateCollision(&player, &wave);
        
        PlayerDraw(&player, frameBuffer);
        WaveDraw(&wave, frameBuffer);
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