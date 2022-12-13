#include "Plants.h"
#include <stdio.h>
void CreatePlant(Plant *plant, Graphics *gfx, PlantEnum plantEnum, SDL_Rect tile, Uint32 TickPlaced, int zIndex)
{

    ConstructDrawable(&plant->GrownItems.d,DT_Plant, gfx, SS_ITEM, tile, tile, zIndex);
    ConstructDrawable(&plant->SeedItems.d,DT_Plant, gfx, SS_ITEM, tile, tile, zIndex);
    ConstructDrawable(&plant->TextureMap, DT_Plant, gfx, SS_PLANT, tile, tile, zIndex);
    SDL_Rect r;
    switch (plantEnum)
    {
    case ParsnipType:
        r.x = 0;
        r.y = 0;
        r.w = 16;
        r.h = 32;

        r.y += 10;
        r.h = 20;

        plant->GrownItems.d.srcrect.x = 0;
        plant->GrownItems.d.srcrect.y = 16;
        plant->GrownItems.d.srcrect.w = 16;
        plant->GrownItems.d.srcrect.h = 16;

        plant->SeedItems.d.srcrect.x = 16 * 16;
        plant->SeedItems.d.srcrect.y = 19 * 16;
        plant->SeedItems.d.srcrect.w = 16;
        plant->SeedItems.d.srcrect.h = 16;

        plant->TextureMap.destrect.y -= 15;
        plant->TextureMap.destrect.h += 15;
        CreatePlantType(plant, "Parsnip", r, 6, 60 * 60);

        break;

    case CauliflowerType:
        r.x = 0;
        r.y = 32;
        r.w = 16;
        r.h = 32;

        r.h -= 16;
        r.y += 16;

        plant->GrownItems.d.srcrect.x = 22*16;
        plant->GrownItems.d.srcrect.y = 7*16;
        plant->GrownItems.d.srcrect.w = 16;
        plant->GrownItems.d.srcrect.h = 16;

        plant->SeedItems.d.srcrect.x = 18 * 16;
        plant->SeedItems.d.srcrect.y = 19 * 16;
        plant->SeedItems.d.srcrect.w = 16;
        plant->SeedItems.d.srcrect.h = 16;
        CreatePlantType(plant, "Cauliflower", r, 7, 60 * 60);
    break;

    case GarlicType:
        r.x = 0;
        r.y = 64;
        r.w = 16;
        r.h = 32;

        r.y += 10;

        //22, 1
        plant->GrownItems.d.srcrect.x = 8*16;
        plant->GrownItems.d.srcrect.y = 10*16;
        plant->GrownItems.d.srcrect.w = 16;
        plant->GrownItems.d.srcrect.h = 16;

        plant->SeedItems.d.srcrect.x = 20 * 16;
        plant->SeedItems.d.srcrect.y = 19 * 16;
        plant->SeedItems.d.srcrect.w = 16;
        plant->SeedItems.d.srcrect.h = 16;
        CreatePlantType(plant, "Garlic", r, 6, 60 * 60);

    break;

    case RhubarbType:
        r.x = 0;
        r.y = 96;
        r.w = 16;
        r.h = 32;

        plant->GrownItems.d.srcrect.x = 12*16;
        plant->GrownItems.d.srcrect.y = 10*16;
        plant->GrownItems.d.srcrect.w = 16;
        plant->GrownItems.d.srcrect.h = 16;

        plant->SeedItems.d.srcrect.x = 22 * 16;
        plant->SeedItems.d.srcrect.y = 19 * 16;
        plant->SeedItems.d.srcrect.w = 16;
        plant->SeedItems.d.srcrect.h = 16;

        plant->TextureMap.destrect.y -= 15;
        plant->TextureMap.destrect.h += 15;
        CreatePlantType(plant, "Rhubarb", r, 7, 60 * 60);
    break;

    case WheatType:
        r.x = 7 * 16;
        r.y = 5 * 32;
        r.w = 16;
        r.h = 32;

        plant->GrownItems.d.srcrect.x = 22*16;
        plant->GrownItems.d.srcrect.y = 10*16;
        plant->GrownItems.d.srcrect.w = 16;
        plant->GrownItems.d.srcrect.h = 16;

        plant->SeedItems.d.srcrect.x = 3 * 16;
        plant->SeedItems.d.srcrect.y = 20 * 16;
        plant->SeedItems.d.srcrect.w = 16;
        plant->SeedItems.d.srcrect.h = 16;
        CreatePlantType(plant, "Wheat", r, 7, 60 * 60);

        break;
    case PumpkinType:
        r.x = 0 * 16;
        r.y = 9 * 32;
        r.w = 16;
        r.h = 32;

        r.y += 8;
        r.h -= 8;

        plant->GrownItems.d.srcrect.x = 13*16;
        plant->GrownItems.d.srcrect.y = 11*16;
        plant->GrownItems.d.srcrect.w = 16;
        plant->GrownItems.d.srcrect.h = 16;

        plant->SeedItems.d.srcrect.x = 10*16;
        plant->SeedItems.d.srcrect.y = 20*16;
        plant->SeedItems.d.srcrect.w = 16;
        plant->SeedItems.d.srcrect.h = 16;
        CreatePlantType(plant, "Pumpkin", r, 7, 60 * 60);
        break;

    case CornType:
        r.x = 8 * 16;
        r.y = 7 * 32;
        r.w = 16;
        r.h = 32;

        plant->GrownItems.d.srcrect.x = 6*16;
        plant->GrownItems.d.srcrect.y = 11*16;
        plant->GrownItems.d.srcrect.w = 16;
        plant->GrownItems.d.srcrect.h = 16;

        plant->SeedItems.d.srcrect.x = 7 * 16;
        plant->SeedItems.d.srcrect.y = 20 * 16;
        plant->SeedItems.d.srcrect.w = 16;
        plant->SeedItems.d.srcrect.h = 16;

        plant->TextureMap.destrect.y -= 15;
        plant->TextureMap.destrect.h += 10;
        CreatePlantType(plant, "Corn", r, 8, 60 * 60);
        plant->TickToRegrow = 60 * 60;
        plant->HasHarvestableBerries = 1;
        break;

    case TomatoType:
        r.x = 0;
        r.y = 96 + 32;
        r.w = 16;
        r.h = 32;
        plant->GrownItems.d.srcrect.x = 16*16;
        plant->GrownItems.d.srcrect.y = 10*16;
        plant->GrownItems.d.srcrect.w = 16;
        plant->GrownItems.d.srcrect.h = 16;

        plant->SeedItems.d.srcrect.x = 0 * 16;
        plant->SeedItems.d.srcrect.y = 20 * 16;
        plant->SeedItems.d.srcrect.w = 16;
        plant->SeedItems.d.srcrect.h = 16;
        CreatePlantType(plant, "Tomato", r, 8, 60 * 60);
        plant->TickToRegrow = 60 * 60;
        plant->HasHarvestableBerries = 1;
    break;

    case CoffeeBeanType://20 0
        r.x = 0;
        r.y = 20 * 32;
        r.w = 16;
        r.h = 32;
        plant->GrownItems.d.srcrect.x = 1*16;
        plant->GrownItems.d.srcrect.y = 18*16;
        plant->GrownItems.d.srcrect.w = 16;
        plant->GrownItems.d.srcrect.h = 16;

        plant->SeedItems.d.srcrect.x = 1*16;
        plant->SeedItems.d.srcrect.y = 18*16;
        plant->SeedItems.d.srcrect.w = 16;
        plant->SeedItems.d.srcrect.h = 16;

        plant->TextureMap.destrect.y -= 15;
        plant->TextureMap.destrect.h += 15;
        CreatePlantType(plant, "Coffee Bean", r, 8, 60 * 60);
        plant->TickToRegrow = 60 * 60;
        plant->HasHarvestableBerries = 1;

        break;
    case StrawberryType://18 0
        r.x = 0;
        r.y = 18 * 32;
        r.w = 16;
        r.h = 32;

        r.y += 10;
        r.h -= 10;
        plant->GrownItems.d.srcrect.x = 16*16;
        plant->GrownItems.d.srcrect.y = 16*16;
        plant->GrownItems.d.srcrect.w = 16;
        plant->GrownItems.d.srcrect.h = 16;

        plant->SeedItems.d.srcrect.x = 16*16;
        plant->SeedItems.d.srcrect.y = 20*16;
        plant->SeedItems.d.srcrect.w = 16;
        plant->SeedItems.d.srcrect.h = 16;

        plant->TextureMap.destrect.y -= 15;
        plant->TextureMap.destrect.h += 15;
        CreatePlantType(plant, "Strawberry", r, 8, 60 * 60);
        plant->TickToRegrow = 60 * 60;
        plant->HasHarvestableBerries = 1;

        break;

    default:
        break;
    }
    plant->TickSinceLastHarvested = plant->TickToRegrow;
    plant->TickPlaced = TickPlaced;
}
void CreatePlantType(Plant *plant, char name[], SDL_Rect base, int length, int diffTime){
    //TMP
    plant->GrownItems.SellValue = 20;
    plant->SeedItems.Cost = 1;
    //TMP
    plant->nPlantStages = length - 1;
    plant->nToUpdate = 0;
    SDL_Rect r = base;
    strcpy(plant->Name, name);
    strcpy(plant->GrownItems.Name, name);
    strcpy(plant->SeedItems.Name, name);
    strcat(plant->SeedItems.Name, " Seed");
    for (int i = 0; i < length; i++)
    {
        r.x += 16;
        plant->plantStages[i].srcrect = r;
        plant->plantStages[i].GrowTick = diffTime * i;
    }
}
void UpdatePlant(Plant *plant, Uint32 Tick)
{
    
    plant->TickSinceLastHarvested = Tick - plant->TickAtHarvestation;
    Uint32 calcTick = Tick - plant->TickPlaced;

    if (plant->plantStages[plant->nToUpdate].GrowTick <= calcTick)
    {
        
        if (!plant->HasHarvestableBerries){
            if (plant->plantStages[plant->nToUpdate].GrowTick <= Tick - plant->TickPlaced && plant->nPlantStages - 1 >= plant->nToUpdate){
                plant->nToUpdate++;
            }
        }
        else{
            if (plant->plantStages[plant->nToUpdate].GrowTick <= Tick - plant->TickPlaced && plant->nPlantStages - 2 >= plant->nToUpdate){ //initial growth
                plant->nToUpdate++;
            }
        }
    }
    if (plant->HasHarvestableBerries){
        if (plant->nPlantStages == plant->nToUpdate && plant->TickSinceLastHarvested >= plant->TickToRegrow){
            plant->nToUpdate--;
        }
    }
    plant->TextureMap.srcrect = plant->plantStages[plant->nToUpdate - 1].srcrect;
}
Item SeedToItem(Graphics *gfx, PlantEnum plant, int amount){
    Plant p;
    SDL_Rect rect={0, 0, 32, 32};
    CreatePlant(&p, gfx, plant, rect, SDL_GetTicks(), 1000);
    p.SeedItems.amount = amount;
    p.SeedItems.exists = 1;
    return p.SeedItems;
}