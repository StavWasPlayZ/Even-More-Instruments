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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
                .filter((_record) -> _record.getNamespace().equals(EMIMain.MODID)),
            builder
        );

    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((player) ->
        Component.translatable("commands.enchant.failed.itemless", player)
    );
    private static final DynamicCommandExceptionType ERROR_RECORD_BURNED = new DynamicCommandExceptionType((player) ->
        Component.translatable("commands.evenmoreinstruments.emirecord.failed.record_burned", player)
    );
    private static final DynamicCommandExceptionType ERROR_RECORD_INVALID = new DynamicCommandExceptionType((id) ->
        Component.translatable("commands.evenmoreinstruments.emirecord.failed.record_invalid", id)
    );
    private static final DynamicCommandExceptionType ERROR_TOO_MANY = new DynamicCommandExceptionType((player) ->
        Component.translatable("commands.evenmoreinstruments.emirecord.failed.too_many", player)
    );

    private static final ClipboardManager clipboard = new ClipboardManager();


    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        pDispatcher.register(Commands.literal("emirecord")
            .then(Commands.literal("load")
                .then(Commands.argument("target", EntityArgument.player())
                    .then(Commands.argument("name", ResourceLocationArgument.id())
                        .suggests(SUGGEST_RECORDS)
                        .executes(EMIRecordCommand::loadRecordToHand)
                    )
                )
            )
            .then(Commands.literal("save")
                .requires((stack) -> stack.isPlayer() && stack.hasPermission(2))
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(EMIRecordCommand::saveRecord)
                )
            )
            .then(Commands.literal("remove")
                .requires((stack) -> stack.hasPermission(2))
                .then(Commands.argument("name", ResourceLocationArgument.id())
                    .suggests(SUGGEST_RECORDS)
                    .executes(EMIRecordCommand::removeRecord)
                )
            )
        );
    }

    private static int saveRecord(CommandContext<CommandSourceStack> stack) {
        stack.getSource().sendSuccess(() -> Component.literal("okie"), true);
        return 1;
    }

    private static int loadRecordToHand(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        final Player target = EntityArgument.getPlayer(stack,"target");
        final Optional<ItemStack> record = CommonUtil.getItemInBothHands(target, ModItems.RECORD_WRITABLE.get());

        if (record.isEmpty())
            throw ERROR_NO_ITEM.create(target);
        if (record.get().getCount() > 1)
            throw ERROR_TOO_MANY.create(target);

        if (((WritableRecordItem) record.get().getItem()).isBurned(record.get()))
            throw ERROR_RECORD_BURNED.create(target);


        final ResourceLocation recordName = stack.getArgument("name", ResourceLocation.class);
        final Optional<CompoundTag> recordChannel = RecordRepository.getRecord(recordName);

        if (recordChannel.isEmpty())
            throw ERROR_RECORD_INVALID.create(recordName);

        record.get().getOrCreateTag().put(WritableRecordItem.CHANNEL_TAG, recordChannel.get());

        stack.getSource().sendSuccess(() -> Component.translatable("commands.evenmoreinstruments.emirecord.record_loaded"), true);
        return 1;
    }

    private static int removeRecord(CommandContext<CommandSourceStack> stack) {
        stack.getSource().sendSuccess(() -> Component.literal("okie"), true);
        return 1;
    }
}
