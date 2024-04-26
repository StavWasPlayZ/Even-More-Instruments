package com.cstav.evenmoreinstruments.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Collections;

/*
copy give to clipboard in hand
save to datapack from hand [name]
load to hand [name]
 */
public class EMIRecordCommand {

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        pDispatcher.register(Commands.literal("emirecord")
            .then(Commands.literal("copy")
                .executes(EMIRecordCommand::copyRecordToClipboard)
            )
            .then(Commands.literal("load")
                .then(Commands.argument("name", StringArgumentType.word())
                    .suggests((stack, builder) -> SharedSuggestionProvider.suggest(getSavedRecords(), builder))
                    .executes(EMIRecordCommand::loadRecordToHand)
                )
            )
            .then(Commands.literal("save")
                .requires((stack) -> stack.hasPermission(2))
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(EMIRecordCommand::saveRecordToDatapack)
                )
            )
        );
    }

    private static Collection<String> getSavedRecords() {
        return Collections.singleton("lmao");
    }

    private static int saveRecordToDatapack(CommandContext<CommandSourceStack> stack) {
        stack.getSource().sendSuccess(() -> Component.literal("okie"), true);
        return 1;
    }

    private static int copyRecordToClipboard(CommandContext<CommandSourceStack> stack) {
        stack.getSource().sendSuccess(() -> Component.literal("okie"), true);
        return 1;
    }

    private static int loadRecordToHand(CommandContext<CommandSourceStack> stack) {
        stack.getSource().sendSuccess(() -> Component.literal("okie"), true);
        return 1;
    }
}
