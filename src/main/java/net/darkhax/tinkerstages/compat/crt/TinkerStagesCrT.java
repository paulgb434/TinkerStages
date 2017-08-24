package net.darkhax.tinkerstages.compat.crt;

import crafttweaker.annotations.ZenRegister;
import net.darkhax.tinkerstages.TinkerStages;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.TinkerStages")
public class TinkerStagesCrT {

    @ZenMethod
    public static void addGeneralCraftingStage (String stage) {

        TinkerStages.GENERAL_CRAFTING_STAGES.add(stage);
    }

    @ZenMethod
    public static void addGeneralPartReplacingStage (String stage) {

        TinkerStages.GENERAL_PART_REPLACING_STAGES.add(stage);
    }

    @ZenMethod
    public static void addGeneralPartBuildingStage (String stage) {

        TinkerStages.GENERAL_PART_BUILDING_STAGES.add(stage);
    }

    @ZenMethod
    public static void addGeneralModifierStage (String stage) {

        TinkerStages.GENERAL_MODIFIER_STAGES.add(stage);
    }

    @ZenMethod
    public static void addToolTypeStage (String stage, String toolType) {

        TinkerStages.TOOL_CRAFTING_STAGES.put(toolType, stage);
    }
    
    @ZenMethod
    public static void addMaterialStage (String stage, String material) {
        
        TinkerStages.TOOL_MATERIAL_STAGES.put(material, stage);
    }
    
    @ZenMethod
    public static void addModifierStage (String stage, String modifier) {
        
        TinkerStages.TOOL_MODIFIER_STAGES.put(modifier, stage);
    }
}