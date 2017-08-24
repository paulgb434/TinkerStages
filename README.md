# Dimension Stages [![](http://cf.way2muchnoise.eu/275343.svg)](https://minecraft.curseforge.com/projects/tinkerstages) [![](http://cf.way2muchnoise.eu/versions/275343.svg)](https://minecraft.curseforge.com/projects/tinkerstages)

This mod hooks in to the GameStage API, and allows various aspects of [Tinkers Construct](https://minecraft.curseforge.com/projects/tinkers-construct) to be put into progression stages.

[![Nodecraft](https://i.imgur.com/sz9PUmK.png)](https://nodecraft.com/r/darkhax)    
This project is sponsored by Nodecraft. Use code [Darkhax](https://nodecraft.com/r/darkhax) for 30% off your first month of service!

## Description
This mod is an addon for the GameStage API. This allows for access to aspects of Tinkers Construct to be put into custom progression systems. You should check out the GameStage API mod's description for more info. To give a brief run down, stages are parts of the progression system set up by the modpack or server. Stages are given to players through a command, which is typically ran by a questing mod, advancement, or even a Command Block. 

## Setup
This mod uses [CraftTweaker](https://minecraft.curseforge.com/projects/crafttweaker) for configuration.

### General Restrictions
These are restrictions that apply to entire systems rather than specific situations. These ZenScript methods have one input, which is the stage you want to restrict the system to. You can add multiple stages for the same system, and the player will have access to the system if they have at least one of those stages. 

- **Tool Crafting** - Prevents the player from crafting tools in the Tinker tool station unless they have the stage. `mods.TinkerStages.addGeneralCraftingStage(String stage);`
- **Part Replacing** - Prevents the player from replacing tool parts at the tool station, unless they have the stage. `mods.TinkerStages.addGeneralPartReplacingStage(String stage);`
- **Part Building** - Prevents the player from building parts in the part builder, unless they have the stage. `mods.TinkerStages.addGeneralPartBuildingStage(String stage);`
- **Applying Modifiers** - Prevents the player from applying tool modifiers, unless they have the stage. `mods.TinkerStages.addGeneralModifierStage(String stage);`

### Specific Restrictions
These restrictions apply to a specific situation. They have varying inputs, but are all very straight forward. 

- **Tool Type** - Prevents a specific tool type from being crafted at the tool station. For example, you can restrict the crafting of hammers until a specific stage. `mods.TinkerStages.addToolTypeStage(String stage, String toolId);`
- **Material Stage** - Prevents a material from being used by the player. Including crafting, part building, and using the tool. `mods.TinkerStages.addMaterialStage(String stage, String material);`
- **Modifier Stage** - Prevents a specific modifier from being applied to a tool or used. `mods.TinkerStages.addModifierStage(String stage, String modifier);`

### Example Script

```apache
//GENERAL RESTRICTIONS
//Prevents all tools unless the stage is unlocked.
mods.TinkerStages.addGeneralCraftingStage("one");

//Prevents all tool swapping unless the stage is unlcoked.
mods.TinkerStages.addGeneralPartReplacingStage("one");

//Prvents all part building unless the stage is unlocked.
mods.TinkerStages.addGeneralPartBuildingStage("one");

//Prevents applying any tool modifiers unless the stage is unlocked.
mods.TinkerStages.addGeneralModifierStage("one");


//SPECIFIC RESTRICTIONS
//Prevents crafting pickaxes unless the stage is unlocked.
mods.TinkerStages.addToolTypeStage("two", "tconstruct:pickaxe");

//Prevents the material from being used. 
mods.TinkerStages.addMaterialStage("two", "stone");

//Prevents the modifier from being applied.
mods.TinkerStages.addModifierStage("two", "mending_moss");
```