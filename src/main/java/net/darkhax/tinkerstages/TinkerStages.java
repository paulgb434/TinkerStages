package net.darkhax.tinkerstages;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.GameStages;
import net.darkhax.gamestages.data.IStageData;
import net.darkhax.tinkerstages.commands.CommandTconDump;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent.ToolCraftingEvent;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent.ToolModifyEvent;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent.ToolPartCraftingEvent;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent.ToolPartReplaceEvent;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

@Mod(modid = "tinkerstages", name = "Tinker Stages", version = "@VERSION@", dependencies = "required-after:tconstruct;required-after:bookshelf;required-after:gamestages@[2.0.89,);required-after:crafttweaker", certificateFingerprint = "@FINGERPRINT@")
public class TinkerStages {
    
    /**
     * The logger for the mod.
     */
    public static final LoggingHelper LOG = new LoggingHelper("Tinker Stages");
    
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
        GameStages.COMMAND.addSubcommand(new CommandTconDump());
    }
    
    @SubscribeEvent
    public void onToolCrafted (ToolCraftingEvent event) {
        
        final String itemId = StackUtils.getStackIdentifier(event.getItemStack());
        
        // General crafting prevention
        if (!GENERAL_CRAFTING_STAGES.isEmpty() && !GameStageHelper.hasAnyOf(event.getPlayer(), GENERAL_CRAFTING_STAGES)) {
            
            event.setCanceled("You can not craft any Tinkers Construct tools at this time. Further progression is needed.");
            return;
        }
        
        // Prevent specific tool crafting
        if (TOOL_CRAFTING_STAGES.containsKey(itemId) && !GameStageHelper.hasAnyOf(event.getPlayer(), TOOL_CRAFTING_STAGES.get(itemId))) {
            
            event.setCanceled("You can not craft the " + event.getItemStack().getDisplayName() + " at this time. Further progression is needed.");
            return;
        }
        
        // Specific material prevention
        for (final ItemStack part : event.getToolParts()) {
            
            if (!part.isEmpty()) {
                
                final Material material = TinkerUtil.getMaterialFromStack(part);
                
                if (TOOL_MATERIAL_STAGES.containsKey(material.identifier) && !GameStageHelper.hasAnyOf(event.getPlayer(), TOOL_MATERIAL_STAGES.get(material.identifier))) {
                    
                    event.setCanceled("You can not use the " + part.getDisplayName() + " in tools yet. Further progression is needed for " + material.getLocalizedName() + " tools.");
                    return;
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onToolPartSwap (ToolPartReplaceEvent event) {
        
        // General replacing prevention
        if (!GENERAL_PART_REPLACING_STAGES.isEmpty() && !GameStageHelper.hasAnyOf(event.getPlayer(), GENERAL_PART_REPLACING_STAGES)) {
            
            event.setCanceled("You can not swap any tool parts at this time. Further progression is needed.");
            return;
        }
        
        // Specific material prevention
        for (final ItemStack part : event.getToolParts()) {
            
            if (!part.isEmpty()) {
                
                final Material material = TinkerUtil.getMaterialFromStack(part);
                
                if (TOOL_MATERIAL_STAGES.containsKey(material.identifier) && !GameStageHelper.hasAnyOf(event.getPlayer(), TOOL_MATERIAL_STAGES.get(material.identifier))) {
                    
                    event.setCanceled("You can not use the " + part.getDisplayName() + " for tools yet. Further progression is needed.");
                    return;
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onToolPartCrafted (ToolPartCraftingEvent event) {
        
        final Material material = TinkerUtil.getMaterialFromStack(event.getItemStack());
        
        // General part building prevention
        if (!GENERAL_PART_BUILDING_STAGES.isEmpty() && !GameStageHelper.hasAnyOf(event.getPlayer(), GENERAL_PART_BUILDING_STAGES)) {
            
            event.setCanceled("You can not build any tool parts at this time. Further progression is needed.");
            return;
        }
        
        // Specific material prevention
        if (TOOL_MATERIAL_STAGES.containsKey(material.identifier) && !GameStageHelper.hasAnyOf(event.getPlayer(), TOOL_MATERIAL_STAGES.get(material.identifier))) {
            
            event.setCanceled("You can not make parts from " + material.getLocalizedName() + " yet. Further progression is needed.");
            return;
        }
    }
    
    @SubscribeEvent
    public void onToolModified (ToolModifyEvent event) {
          
	    boolean hasOtherModifier = false;
        // Prevent specific modifier
        for (final IModifier modifier : event.getModifiers()) {
            
            if (TOOL_MODIFIER_STAGES.containsKey(modifier.getIdentifier()) && !GameStageHelper.hasAnyOf(event.getPlayer(), TOOL_MODIFIER_STAGES.get(modifier.getIdentifier()))) {
                
		boolean hasOtherModifier = true;
                event.setCanceled("You can not apply the " + modifier.getLocalizedName() + " modifier at this time. Further progression is needed.");
                return;
            } else if (!GENERAL_MODIFIER_STAGES.isEmpty() && !GameStageHelper.hasAnyOf(event.getPlayer(), GENERAL_MODIFIER_STAGES) && hasOtherModifier = false) {
            
            event.setCanceled("You can not apply any tool modifiers at this time. Further progression is needed.");
            return;
     	   }
        }
    }
    
    @SubscribeEvent
    public void onLivingUpdate (LivingUpdateEvent event) {
        
        if (event.getEntityLiving() instanceof EntityPlayer && !event.getEntityLiving().world.isRemote) {
            
            final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            final IStageData stageData = GameStageHelper.getPlayerData(player);
            final ItemStack stack = player.getHeldItemMainhand();
            
            final String itemId = StackUtils.getStackIdentifier(stack);
            
            if (stageData != null && !stack.isEmpty()) {
                
                // Prevent specific tool
                if (TOOL_CRAFTING_STAGES.containsKey(itemId) && !GameStageHelper.hasAnyOf(player, stageData, TOOL_CRAFTING_STAGES.get(itemId))) {
                    
                    player.sendMessage(new TextComponentString("You dropped the " + stack.getDisplayName() + "! Further progression is needed to wield this type of tool."));
                    player.dropItem(true);
                    return;
                }
                
                // Specific material prevention
                for (final Material material : TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack))) {
                    
                    if (TOOL_MATERIAL_STAGES.containsKey(material.identifier) && !GameStageHelper.hasAnyOf(player, stageData, TOOL_MATERIAL_STAGES.get(material.identifier))) {
                        
                        player.sendMessage(new TextComponentString("You dropped the " + stack.getDisplayName() + "! Further progression is needed to wield " + material.getLocalizedName() + " tools."));
                        player.dropItem(true);
                        return;
                    }
                }
                
                // Specific modifier prevention
                for (final IModifier modifier : TinkerUtil.getModifiers(stack)) {
                    
                    if (TOOL_MODIFIER_STAGES.containsKey(modifier.getIdentifier()) && !GameStageHelper.hasAnyOf(player, stageData, TOOL_MODIFIER_STAGES.get(modifier.getIdentifier()))) {
                        
                        player.sendMessage(new TextComponentString("You dropped the " + stack.getDisplayName() + "! Further progression is needed to use the " + modifier.getLocalizedName() + " modifier."));
                        player.dropItem(true);
                        return;
                    }
                }
            }
        }
    }
}
