#ifndef GRAPHICS_H
#define GRAPHICS_H
#include "../SDL2/SDL.h"

// #define WINDOW_WIDTH 1280
// #define WINDOW_HEIGHT 750

#define TILE_WIDTH 32
#define TILE_HEIGHT 32

#define TILE_Z_INDEX_MIN -100
#define TILE_Z_INDEX_MAX 100

typedef enum SpriteSheet
{
    SS_TILEMAP_SPRING,
    SS_TILEMAP_SUMMER,
    SS_TILEMAP_FALL,
    SS_TILEMAP_WINTER,
    SS_PLAYER,
    SS_GUI,
    SS_PLANT,
    SS_ITEM,
    SS_FONT,
    SS_SHADER,
    SS_TRANSPARENT,
    SS_NONE,
    SS_Doge,
    SS_Cow,
    SS_Chiken,
    SS_Pig
} SpriteSheet;

typedef struct Graphics
{
    SDL_Window *win;
    int wWidth;
    int wHeight;
    int wFullscreen;
    SDL_Renderer *rend;
    SDL_Texture **textures;
} Graphics;

void ConstructGraphics(Graphics *gfx);

void DrawFilledRectangle(SDL_Renderer *r, const SDL_Rect *rect);
void DrawHollowRectangle(SDL_Renderer *r, const SDL_Rect *rect);
void DestroyGraphics(Graphics *gfx);
void BeginFrame(Graphics *gfx);
void EndFrame(Graphics *gfx);

#endif