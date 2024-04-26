package com.cstav.evenmoreinstruments.server.command;

/**
 * @deprecated I tried ig - server compatibility issues, too bothersome to
 * deal with atm
 */
@Deprecated
public class EMIRecordCommand {
//    private static final SuggestionProvider<CommandSourceStack> SUGGEST_RECORDS = (stack, builder) ->
//        SharedSuggestionProvider.suggestResource(
//            RecordRepository.listGenRecords(stack.getSource().getLevel()),
//            builder
//        );
//
//    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((player) ->
//        Component.translatable("commands.enchant.failed.itemless", player)
//    );
//    private static final DynamicCommandExceptionType ERROR_RECORD_BURNED = new DynamicCommandExceptionType((player) ->
//        Component.translatable("commands.evenmoreinstruments.emirecord.failed.record_burned", player)
//    );
//    private static final DynamicCommandExceptionType ERROR_RECORD_EMPTY = new DynamicCommandExceptionType((player) ->
//        Component.translatable("commands.evenmoreinstruments.emirecord.failed.record_empty", player)
//    );
//    private static final DynamicCommandExceptionType ERROR_RECORD_INVALID = new DynamicCommandExceptionType((id) ->
//        Component.translatable("commands.evenmoreinstruments.emirecord.failed.record_invalid", id)
//    );
//    private static final DynamicCommandExceptionType ERROR_TOO_MANY = new DynamicCommandExceptionType((player) ->
//        Component.translatable("commands.evenmoreinstruments.emirecord.failed.too_many", player)
//    );
//    private static final DynamicCommandExceptionType ERROR_INVALID_NAME = new DynamicCommandExceptionType((id) ->
//        Component.translatable("commands.evenmoreinstruments.emirecord.failed.invalid_name", EMIMain.MODID, id)
//    );
//
//
//    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
//        pDispatcher.register(Commands.literal("emirecord")
//            .then(Commands.literal("load")
//                .then(Commands.argument("target", EntityArgument.player())
//                    .then(Commands.argument("name", ResourceLocationArgument.id())
//                        .suggests(SUGGEST_RECORDS)
//                        .executes(EMIRecordCommand::loadRecordToHand)
//                    )
//                )
//            )
//            .then(Commands.literal("save")
//                .requires((stack) -> stack.isPlayer() && stack.hasPermission(2))
//                .then(Commands.argument("name", ResourceLocationArgument.id())
//                    .executes(EMIRecordCommand::saveRecord)
//                )
//            )
//            .then(Commands.literal("remove")
//                .requires((stack) -> stack.hasPermission(2))
//                .then(Commands.argument("name", ResourceLocationArgument.id())
//                    .suggests(SUGGEST_RECORDS)
//                    .executes(EMIRecordCommand::removeRecord)
//                )
//            )
//        );
//    }
//
//    private static int saveRecord(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
//        final ResourceLocation saveLoc = ResourceLocationArgument.getId(stack, "name");
//        if (saveLoc.getNamespace().equals(EMIMain.MODID))
//            throw ERROR_INVALID_NAME.create(saveLoc);
//
//
//        final Player target = stack.getSource().getPlayer();
//        final Optional<ItemStack> record = CommonUtil.getItemInBothHands(target, ModItems.RECORD_WRITABLE.get());
//
//        if (record.isEmpty())
//            throw ERROR_NO_ITEM.create(target.getDisplayName());
//
//        if (!((WritableRecordItem) record.get().getItem()).isBurned(record.get()))
//            throw ERROR_RECORD_EMPTY.create(target.getDisplayName());
//
//
//        final boolean opSucceed = RecordRepository.saveRecord(
//            target.level(), saveLoc,
//            record.get().getTagElement(WritableRecordItem.CHANNEL_TAG)
//        );
//
//        if (!opSucceed) {
//            throw new SimpleCommandExceptionType(Component.translatable("command.failed")).create();
//        }
//
//        stack.getSource().sendSuccess(() -> Component.translatable("commands.evenmoreinstruments.emirecord.success.save"), true);
//        return 1;
//    }
//
//    private static int loadRecordToHand(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
//        final Player target = EntityArgument.getPlayer(stack,"target");
//        final Optional<ItemStack> record = CommonUtil.getItemInBothHands(target, ModItems.RECORD_WRITABLE.get());
//
//        if (record.isEmpty())
//            throw ERROR_NO_ITEM.create(target.getDisplayName());
//        if (record.get().getCount() > 1)
//            throw ERROR_TOO_MANY.create(target.getDisplayName());
//
//        if (((WritableRecordItem) record.get().getItem()).isBurned(record.get()))
//            throw ERROR_RECORD_BURNED.create(target.getDisplayName());
//
//
//        final ResourceLocation recordName = stack.getArgument("name", ResourceLocation.class);
//        final Optional<CompoundTag> recordChannel = RecordRepository.getRecord(recordName);
//
//        if (recordChannel.isEmpty())
//            throw ERROR_RECORD_INVALID.create(recordName);
//
//        record.get().getOrCreateTag().putString(BurnedRecordItem.BURNED_MEDIA_TAG, recordName.toString());
//
//        stack.getSource().sendSuccess(() -> Component.translatable("commands.evenmoreinstruments.emirecord.success.record_loaded"), true);
//        return 1;
//    }
//
//    private static int removeRecord(CommandContext<CommandSourceStack> stack) {
//        stack.getSource().sendSuccess(() -> Component.literal("okie"), true);
//        return 1;
//    }
}
