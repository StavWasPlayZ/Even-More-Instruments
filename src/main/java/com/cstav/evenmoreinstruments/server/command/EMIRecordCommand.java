package com.cstav.evenmoreinstruments.server.command;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.item.ModItems;
import com.cstav.evenmoreinstruments.item.component.ModDataComponents;
import com.cstav.evenmoreinstruments.item.emirecord.RecordRepository;
import com.cstav.evenmoreinstruments.item.emirecord.WritableRecordItem;
import com.cstav.evenmoreinstruments.util.CommonUtil;
import com.mojang.brigadier.CommandDispatcher;
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
import net.minecraft.world.item.component.CustomData;

import java.io.IOException;
import java.util.Optional;

public class EMIRecordCommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_RECORDS = (stack, builder) ->
        SharedSuggestionProvider.suggestResource(
            RecordRepository.listRecords(false),
            builder
        );

    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((player) ->
        Component.translatable("commands.evenmoreinstruments.emirecord.failed.no_record", player)
    );
    private static final DynamicCommandExceptionType ERROR_RECORD_ALREADY_BURNED = new DynamicCommandExceptionType((player) ->
        Component.translatable("commands.evenmoreinstruments.emirecord.failed.record_already_burned", player)
    );
    private static final DynamicCommandExceptionType ERROR_RECORD_BURNED = new DynamicCommandExceptionType((player) ->
        Component.translatable("commands.evenmoreinstruments.emirecord.failed.record_burned", player)
    );
    private static final DynamicCommandExceptionType ERROR_RECORD_EMPTY = new DynamicCommandExceptionType((player) ->
        Component.translatable("commands.evenmoreinstruments.emirecord.failed.record_empty", player)
    );
    private static final DynamicCommandExceptionType ERROR_RECORD_INVALID = new DynamicCommandExceptionType((id) ->
        Component.translatable("commands.evenmoreinstruments.emirecord.failed.record_invalid", id)
    );
    private static final DynamicCommandExceptionType ERROR_TOO_MANY = new DynamicCommandExceptionType((player) ->
        Component.translatable("commands.evenmoreinstruments.emirecord.failed.too_many", player)
    );
    private static final DynamicCommandExceptionType ERROR_INVALID_NAMESPACE = new DynamicCommandExceptionType((id) ->
        Component.translatable("commands.evenmoreinstruments.emirecord.failed.invalid_namespace", EMIMain.MODID, id)
    );
    private static final DynamicCommandExceptionType ERROR_ILLEGAL_NAME = new DynamicCommandExceptionType((name) ->
        Component.translatable("commands.evenmoreinstruments.emirecord.failed.illegal_name", name)
    );


    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        pDispatcher.register(Commands.literal("emirecord")
            .then(Commands.literal("burn")
                .then(Commands.argument("record", ResourceLocationArgument.id())
                    .suggests(SUGGEST_RECORDS)
                    .executes((stack) -> loadRecordToHand(stack, stack.getSource().getPlayerOrException()))
                    .then(Commands.argument("target", EntityArgument.player())
                        .requires((stack) -> stack.hasPermission(2))
                        .executes((stack) -> loadRecordToHand(stack, EntityArgument.getPlayer(stack,"target")))
                    )
                )
            )
            .then(Commands.literal("save")
                .requires((stack) -> stack.hasPermission(2))
                .then(Commands.argument("record", ResourceLocationArgument.id())
                    .executes(EMIRecordCommand::saveRecord)
                )
            )
            .then(Commands.literal("remove")
                .requires((stack) -> stack.hasPermission(2))
                .then(Commands.argument("record", ResourceLocationArgument.id())
                    .suggests(SUGGEST_RECORDS)
                    .executes(EMIRecordCommand::removeRecord)
                )
            )
        );
    }

    private static int saveRecord(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        final Player target = stack.getSource().getPlayerOrException();

        final ResourceLocation saveLoc = ResourceLocationArgument.getId(stack, "record");

        if (saveLoc.getNamespace().equals(EMIMain.MODID)) {
            throw ERROR_INVALID_NAMESPACE.create(saveLoc);
        }
        // Periods are disallowed because we later strip away the files' extensions.
        // /\ The above is relevant to Fabric only, but will keep it in for the sake of parity.
        if (saveLoc.getPath().contains(".")) {
            throw ERROR_ILLEGAL_NAME.create(saveLoc);
        }

        final Optional<ItemStack> record = CommonUtil.getItemInBothHands(target, ModItems.RECORD_WRITABLE.get());

        if (record.isEmpty()) {
            throw ERROR_NO_ITEM.create(target.getDisplayName());
        }

        if (!((WritableRecordItem) record.get().getItem()).isBurned(record.get())) {
            throw ERROR_RECORD_EMPTY.create(target.getDisplayName());
        }


//        final CompoundTag channelTag = record.get().getTagElement(WritableRecordItem.CHANNEL_TAG);
        final CustomData channelTag = record.get().get(ModDataComponents.CHANNNEL.get());
        if (channelTag == null) {

            if (record.get().has(ModDataComponents.BURNED_MEDIA.get())) {
                throw ERROR_RECORD_BURNED.create(target.getDisplayName());
            }

            throw ERROR_RECORD_EMPTY.create(target.getDisplayName());
        }


        try {
            RecordRepository.saveRecord(saveLoc, channelTag.copyTag());
        } catch (IOException e) {
            EMIMain.LOGGER.error("Error encountered while saving record data", e);
            throw new RuntimeException(e);
        }

        stack.getSource().sendSuccess(() -> Component.translatable("commands.evenmoreinstruments.emirecord.success.record_saved"), true);
        return 1;
    }

    private static int loadRecordToHand(CommandContext<CommandSourceStack> stack, Player target) throws CommandSyntaxException {
        final Optional<ItemStack> record = CommonUtil.getItemInBothHands(target, ModItems.RECORD_WRITABLE.get());

        if (record.isEmpty()) {
            throw ERROR_NO_ITEM.create(target.getDisplayName());
        }
        if (record.get().getCount() > 1) {
            throw ERROR_TOO_MANY.create(target.getDisplayName());
        }

        if (((WritableRecordItem) record.get().getItem()).isBurned(record.get())) {
            throw ERROR_RECORD_ALREADY_BURNED.create(target.getDisplayName());
        }


        final ResourceLocation recordName = stack.getArgument("record", ResourceLocation.class);
        final Optional<CompoundTag> recordChannel = RecordRepository.getRecord(recordName);

        if (recordChannel.isEmpty()) {
            throw ERROR_RECORD_INVALID.create(recordName);
        }

        record.get().set(ModDataComponents.BURNED_MEDIA.get(), recordName);

        stack.getSource().sendSuccess(() -> Component.translatable("commands.evenmoreinstruments.emirecord.success.record_burned"), true);
        return 1;
    }

    private static int removeRecord(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        final ResourceLocation name = ResourceLocationArgument.getId(stack, "record");
        if (name.getNamespace().equals(EMIMain.MODID))
            throw ERROR_INVALID_NAMESPACE.create(name);

        try {
            RecordRepository.removeRecord(name);
        } catch (Exception e) {
            // just assume its i/o
            throw ERROR_RECORD_INVALID.create(name);
        }

        stack.getSource().sendSuccess(() -> Component.translatable("commands.evenmoreinstruments.emirecord.success.record_removed"), true);
        return 1;
    }
}
