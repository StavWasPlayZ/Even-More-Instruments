package com.cstav.evenmoreinstruments.server.command;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.item.ModItems;
import com.cstav.evenmoreinstruments.item.emirecord.RecordRepository;
import com.cstav.evenmoreinstruments.item.emirecord.WritableRecordItem;
import com.cstav.evenmoreinstruments.util.CommonUtil;
import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

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
            .then(Commands.literal("copy")
                .requires(CommandSourceStack::isPlayer)
                .executes(EMIRecordCommand::copyRecordToClipboard)
            )
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

    private static int copyRecordToClipboard(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        final Player player = stack.getSource().getPlayer();
        final Optional<ItemStack> record = CommonUtil.getItemInBothHands(player, ModItems.RECORD_WRITABLE.get());

        if (record.isEmpty())
            throw ERROR_NO_ITEM.create(player);

        clipboard.setClipboard(
            Minecraft.getInstance().getWindow().getWindow(),
            String.format(
                "/give @s %s{Channel:%s}",
                ModItems.RECORD_WRITABLE.getId(),
                record.get().getTagElement(WritableRecordItem.CHANNEL_TAG)
            )
        );

        stack.getSource().sendSuccess(
            () -> Component.translatable("commands.evenmoreinstruments.emirecord.clipboard_success"),
            true
        );
        return 1;
    }

    private static int loadRecordToHand(CommandContext<CommandSourceStack> stack) {
        stack.getSource().sendSuccess(() -> Component.literal("okie"), true);
        return 1;
    }
}
