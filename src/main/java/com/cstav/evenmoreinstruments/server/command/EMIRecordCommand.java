package com.cstav.evenmoreinstruments.server.command;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.item.emirecord.RecordRepository;
import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;

/*
copy give to clipboard in hand
save to datapack from hand [name]
load to hand [name]
 */
public class EMIRecordCommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_RECORDS = (stack, builder) ->
        SharedSuggestionProvider.suggestResource(
            RecordRepository.records().stream()
                // Filter only non-built-ins
                .filter((_record) -> !_record.getNamespace().equals(EMIMain.MODID)),
            builder
        );

    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((player) ->
        Component.translatable("commands.enchant.failed.itemless", player));

    private static final ClipboardManager clipboard = new ClipboardManager();


    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        pDispatcher.register(Commands.literal("emirecord")
            .then(Commands.literal("load")
                .then(Commands.argument("name", ResourceLocationArgument.id())
                    .suggests(SUGGEST_RECORDS)
                    .executes(EMIRecordCommand::loadRecordToHand)
                )
            )
            .then(Commands.literal("save")
                .requires((stack) -> stack.hasPermission(2))
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(EMIRecordCommand::saveRecordToDatapack)
                )
            )
            .then(Commands.literal("remove")
                .requires((stack) -> stack.hasPermission(2))
                .then(Commands.argument("name", ResourceLocationArgument.id())
                    .suggests(SUGGEST_RECORDS)
                    .executes(EMIRecordCommand::saveRecordToDatapack)
                )
            )
        );
    }

    private static int saveRecordToDatapack(CommandContext<CommandSourceStack> stack) {
        stack.getSource().sendSuccess(() -> Component.literal("okie"), true);
        return 1;
    }

    private static int loadRecordToHand(CommandContext<CommandSourceStack> stack) {
        stack.getSource().sendSuccess(() -> Component.literal("okie"), true);
        return 1;
    }
}
