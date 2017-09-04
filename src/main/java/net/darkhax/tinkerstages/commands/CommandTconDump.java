package net.darkhax.tinkerstages.commands;

import net.darkhax.bookshelf.command.Command;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;

public class CommandTconDump extends Command {

    @Override
    public String getName () {

        return "tcondump";
    }

    @Override
    public String getUsage (ICommandSender sender) {

        return "tcondump materials|modifiers";
    }

    @Override
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (args.length == 1) {

            if ("materials".equalsIgnoreCase(args[0])) {

                for (final Material material : TinkerRegistry.getAllMaterials()) {

                    sender.sendMessage(new TextComponentString("Name: " + material.getLocalizedName() + " Id: " + material.getIdentifier()));
                }
            }

            else if ("modifiers".equalsIgnoreCase(args[0])) {

                for (final IModifier modifier : TinkerRegistry.getAllModifiers()) {

                    sender.sendMessage(new TextComponentString("Name: " + modifier.getLocalizedName() + " Id: " + modifier.getIdentifier()));
                }
            }
        }

        else {

            sender.sendMessage(new TextComponentString(this.getUsage(sender)));
        }
    }

    @Override
    public int getRequiredPermissionLevel () {

        return 0;
    }
}
