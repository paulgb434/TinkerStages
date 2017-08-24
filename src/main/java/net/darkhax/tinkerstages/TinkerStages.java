package net.darkhax.tinkerstages;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.gamestages.capabilities.PlayerDataHandler;
import net.darkhax.gamestages.capabilities.PlayerDataHandler.IStageData;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.DryingRecipe;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent.ToolCraftingEvent;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent.ToolModifyEvent;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent.ToolPartCraftingEvent;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent.ToolPartReplaceEvent;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.utils.TinkerUtil;

@Mod(modid = "tinkerstages", name = "Tinker Stages", version = "@VERSION@", dependencies = "required-after:bookshelf@[2.0.0.425,);required-after:gamestages@[1.0.19,);required-after:crafttweaker@[2.7.2.,)", acceptedMinecraftVersions = "[1.12,1.12.2)")
public class TinkerStages {

    public static final Set<DryingRecipe> recipes = new HashSet<>();

    /**
     * A set of stages for being able to craft tinker tools. At least one of these stages must
     * be unlocked for tinker tool crafting to be available.
     */
    public static Set<String> GENERAL_CRAFTING_STAGES = new HashSet<>();

    /**
     * A set of stages for being able to replace tinker tool parts. At least one of these
     * stages must be unlocked for part replacing to be available.
     */
    public static Set<String> GENERAL_PART_REPLACING_STAGES = new HashSet<>();

    /**
     * A set of stages for being able to create tinker tool parts. At least one of these stages
     * must be unlocked for part making to be available.
     */
    public static Set<String> GENERAL_PART_BUILDING_STAGES = new HashSet<>();

    /**
     * A set of stages for being able to apply tool modifiers. At least one of these stages
     * must be unlocked for applying modifiers to be available.
     */
    public static Set<String> GENERAL_MODIFIER_STAGES = new HashSet<>();

    /**
     * A map of possible stages needed to craft a specific tool type. Only one of the stages is
     * needed to craft that tool type.
     */
    public static Multimap<String, String> TOOL_CRAFTING_STAGES = HashMultimap.create();

    /**
     * A map of possible stages needed to use a specific tool material. Only one of the stages
     * is needed to use the material.
     */
    public static Multimap<String, String> TOOL_MATERIAL_STAGES = HashMultimap.create();

    /**
     * A map of possible stages needed to apply a specific tool modifier. Only one of the
     * stages is needed to apply the modifier.
     */
    public static Multimap<String, String> TOOL_MODIFIER_STAGES = HashMultimap.create();

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onToolCrafted (ToolCraftingEvent event) {

        final IStageData stageData = PlayerDataHandler.getStageData(event.getPlayer());
        final String itemId = StackUtils.getStackIdentifier(event.getItemStack());

        // Bad stage data
        if (stageData == null) {

            event.setCanceled("You have no GameStage data! This shouldn't be possible!");
            return;
        }

        // General crafting prevention
        if (!GENERAL_CRAFTING_STAGES.isEmpty() && Collections.disjoint(stageData.getUnlockedStages(), GENERAL_CRAFTING_STAGES)) {

            event.setCanceled("You can not craft any Tinkers Construct tools at this time. Further progression is needed.");
            return;
        }

        // Prevent specific tool crafting
        if (TOOL_CRAFTING_STAGES.containsKey(itemId) && Collections.disjoint(stageData.getUnlockedStages(), TOOL_CRAFTING_STAGES.get(itemId))) {

            event.setCanceled("You can not craft the " + event.getItemStack().getDisplayName() + " at this time. Further progression is needed.");
            return;
        }
    }

    @SubscribeEvent
    public void onToolPartSwap (ToolPartReplaceEvent event) {

        final IStageData stageData = PlayerDataHandler.getStageData(event.getPlayer());

        // Bad stage data
        if (stageData == null) {

            event.setCanceled("You have no GameStage data! This shouldn't be possible!");
            return;
        }

        // General replacing prevention
        if (!GENERAL_PART_REPLACING_STAGES.isEmpty() && Collections.disjoint(stageData.getUnlockedStages(), GENERAL_PART_REPLACING_STAGES)) {

            event.setCanceled("You can not swap any tool parts at this time. Further progression is needed.");
            return;
        }

        // Specific material prevention
        for (final ItemStack part : event.getToolParts()) {

            if (!part.isEmpty()) {

                final Material material = TinkerUtil.getMaterialFromStack(part);

                if (TOOL_MATERIAL_STAGES.containsKey(material.identifier) && Collections.disjoint(stageData.getUnlockedStages(), TOOL_MATERIAL_STAGES.get(material.identifier))) {

                    event.setCanceled("You can not use the " + part.getDisplayName() + " for tools yet. Further progression is needed.");
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void onToolPartCrafted (ToolPartCraftingEvent event) {

        final IStageData stageData = PlayerDataHandler.getStageData(event.getPlayer());
        final Material material = TinkerUtil.getMaterialFromStack(event.getItemStack());

        // Bad stage data
        if (stageData == null) {

            event.setCanceled("You have no GameStage data! This shouldn't be possible!");
            return;
        }

        // General part building prevention
        if (!GENERAL_PART_BUILDING_STAGES.isEmpty() && Collections.disjoint(stageData.getUnlockedStages(), GENERAL_PART_BUILDING_STAGES)) {

            event.setCanceled("You can not build any tool parts at this time. Further progression is needed.");
            return;
        }

        // Specific material prevention
        if (TOOL_MATERIAL_STAGES.containsKey(material.identifier) && Collections.disjoint(stageData.getUnlockedStages(), TOOL_MATERIAL_STAGES.get(material.identifier))) {

            event.setCanceled("You can not make parts from " + material.getLocalizedName() + " yet. Further progression is needed.");
            return;
        }
    }

    @SubscribeEvent
    public void onToolModified (ToolModifyEvent event) {

        final IStageData stageData = PlayerDataHandler.getStageData(event.getPlayer());

        // Bad stage data
        if (stageData == null) {

            event.setCanceled("You have no GameStage data! This shouldn't be possible!");
            return;
        }

        // General modifier prevention
        if (!GENERAL_MODIFIER_STAGES.isEmpty() && Collections.disjoint(stageData.getUnlockedStages(), GENERAL_MODIFIER_STAGES)) {

            event.setCanceled("You can not apply any tool modifiers at this time. Further progression is needed.");
            return;
        }

        // Prevent specific tool crafting
        for (final IModifier modifier : event.getModifiers()) {

            if (TOOL_MODIFIER_STAGES.containsKey(modifier.getIdentifier()) && Collections.disjoint(stageData.getUnlockedStages(), TOOL_MODIFIER_STAGES.get(modifier.getIdentifier()))) {

                event.setCanceled("You can not apply the " + modifier.getLocalizedName() + " modifier at this time. Further progression is needed.");
                return;
            }
        }
    }
}
