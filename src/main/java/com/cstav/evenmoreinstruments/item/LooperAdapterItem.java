package com.cstav.evenmoreinstruments.item;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.block.IDoubleBlock;
import com.cstav.evenmoreinstruments.block.LooperBlock;
import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.SyncModTagPacket;
import com.cstav.evenmoreinstruments.util.CommonUtil;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.genshinstrument.block.partial.AbstractInstrumentBlock;
import com.cstav.genshinstrument.block.partial.InstrumentBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class LooperAdapterItem extends Item {
    private static final String BLOCK_INSTRUMENT_POS_TAG = "instrument_block",
        LOOPER_POS_TAG = "looper";

    public LooperAdapterItem(Properties pProperties) {
        super(pProperties);
    }
    

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide)
            return InteractionResult.CONSUME_PARTIAL;

        final BlockPos pos = pContext.getClickedPos();
        final Block block = pContext.getLevel().getBlockState(pContext.getClickedPos()).getBlock();

        final CompoundTag adapterTag = CommonUtil.getOrCreateElementTag(EMIMain.modTag(pContext.getItemInHand()), "looperAdapter");
        final Player player = pContext.getPlayer();

        boolean pairSucceed;
        if (block instanceof AbstractInstrumentBlock)
            pairSucceed = handleInstrumentBlock(pos, adapterTag, player);
        else if (block instanceof LooperBlock)
            pairSucceed = handleLooperBlock(pos, adapterTag, player);
        else
            return InteractionResult.FAIL;

        return pairSucceed ? InteractionResult.SUCCESS : InteractionResult.CONSUME_PARTIAL;
    }

    private static boolean handleInstrumentBlock(BlockPos blockPos, CompoundTag adapterTag, Player player) {
        if (adapterTag.contains(LOOPER_POS_TAG, Tag.TAG_COMPOUND))
            return pairLooperToInstrument(adapterTag, NbtUtils.readBlockPos(adapterTag.getCompound(LOOPER_POS_TAG)), blockPos, player);

        adapterTag.put(BLOCK_INSTRUMENT_POS_TAG, NbtUtils.writeBlockPos(blockPos));
        player.displayClientMessage(
            Component.translatable("item.evenmoreinstruments.looper_adapter.looper.select")
        , true);
        return true;
    }
    private static boolean handleLooperBlock(BlockPos blockPos, CompoundTag adapterTag, Player player) {
        final BlockEntity be = player.getLevel().getBlockEntity(blockPos);
        if (!(be instanceof LooperBlockEntity lbe))
            return false;

        if (!lbe.isRecordIn()) {
            player.displayClientMessage(
                Component.translatable("evenmoreinstruments.looper.no_record")
                    .withStyle(ChatFormatting.RED),
                true
            );
            return false;
        }

        if (adapterTag.contains(BLOCK_INSTRUMENT_POS_TAG, Tag.TAG_COMPOUND))
            return pairLooperToInstrument(adapterTag, blockPos, NbtUtils.readBlockPos(adapterTag.getCompound(BLOCK_INSTRUMENT_POS_TAG)), player);
        if (adapterTag.contains(LOOPER_POS_TAG, Tag.TAG_COMPOUND))
            return syncLoopers(adapterTag, NbtUtils.readBlockPos(adapterTag.getCompound(LOOPER_POS_TAG)), blockPos, player);

        adapterTag.put(LOOPER_POS_TAG, NbtUtils.writeBlockPos(blockPos));
        player.displayClientMessage(
            Component.translatable("item.evenmoreinstruments.looper_adapter.instrument.select").withStyle(ChatFormatting.GREEN),
            true
        );
        return true;
    }


    private static boolean pairLooperToInstrument(CompoundTag adapterTag, InstrumentBlockEntity ibe, LooperBlockEntity lbe, Player player) {
        // Clear all compound keys after pairing
        for (final String key : adapterTag.getAllKeys())
            adapterTag.remove(key);

        return LooperUtil.performPair(lbe, () -> {

            final BlockState instrumentBlockState = ibe.getBlockState();
            final Block instrumentBlock = instrumentBlockState.getBlock();

            final BlockPos instrumentBlockPos = ibe.getBlockPos(),
                looperBlockPos = lbe.getBlockPos();

            // Linked blocks (like the Keyboard) should too have the tag:
            BlockPos otherBlockPos = null;
            if (instrumentBlock instanceof IDoubleBlock doubleBlock)
                otherBlockPos = doubleBlock.getOtherBlock(instrumentBlockState, instrumentBlockPos, player.getLevel());

            LooperUtil.createLooperTag(ibe, looperBlockPos);
            if (otherBlockPos != null)
                LooperUtil.createLooperTag(player.getLevel().getBlockEntity(otherBlockPos), looperBlockPos);

            ibe.setChanged();

            // Handle syncing data to client
            if (player instanceof ServerPlayer serverPlayer) {
                ModPacketHandler.sendToClient(new SyncModTagPacket(EMIMain.modTag(ibe), instrumentBlockPos), serverPlayer);
                if (otherBlockPos != null)
                    ModPacketHandler.sendToClient(new SyncModTagPacket(EMIMain.modTag(ibe), otherBlockPos), serverPlayer);
            }

        }, player);
    }
    private static boolean pairLooperToInstrument(CompoundTag adapterTag, BlockPos looperPos, BlockPos instrumentPos, Player player) {
        final Level level = player.getLevel();

        final BlockEntity lbe = level.getBlockEntity(looperPos),
            ibe = level.getBlockEntity(instrumentPos);
        if (!(lbe instanceof LooperBlockEntity) || !(ibe instanceof InstrumentBlockEntity))
            return false;

        return pairLooperToInstrument(adapterTag, (InstrumentBlockEntity)ibe, (LooperBlockEntity)lbe, player);
    }

    /**
     * Syncs the 2 loopers by setting their repeat ticks to be of the lower looper.
     */
    private static boolean syncLoopers(CompoundTag adapterTag, LooperBlockEntity lbe1, LooperBlockEntity lbe2, Player player) {
        if (lbe1.getBlockPos().equals(lbe2.getBlockPos()))
            return false;

        // Clear all compound keys after pairing
        for (final String key : adapterTag.getAllKeys())
            adapterTag.remove(key);

        if (!lbe1.hasFootage() || !lbe1.hasFootage()) {
            player.displayClientMessage(
                Component.translatable("evenmoreinstruments.record.no_footage").withStyle(ChatFormatting.RED),
                true
            );
            return true;
        }

        lbe2.setRepeatTick(lbe1.getRepeatTick());
        lbe2.setTicks(lbe1.getTicks());

        player.displayClientMessage(
            Component.translatable("item.evenmoreinstruments.looper_adapter.instrument.success_pair").withStyle(ChatFormatting.GREEN),
            true
        );
        return true;
    }
    private static boolean syncLoopers(CompoundTag adapterTag, BlockPos looper1Pos, BlockPos looper2Pos, Player player) {
        if (looper2Pos.equals(looper1Pos))
            return false;

        final Level level = player.getLevel();

        BlockEntity lbe1 = level.getBlockEntity(looper1Pos),
            lbe2 = level.getBlockEntity(looper2Pos);

        if (!(lbe1 instanceof LooperBlockEntity) || !(lbe2 instanceof LooperBlockEntity))
            return false;

        return syncLoopers(adapterTag, (LooperBlockEntity)lbe1, (LooperBlockEntity)lbe2, player);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        if (!Screen.hasShiftDown()) {
            tooltipComponents.add(
                Component.translatable("item.shift.hint.show")
                    .withStyle(ChatFormatting.YELLOW)
            );
            return;
        } else {
            tooltipComponents.add(
                Component.translatable("item.shift.hint.hide")
                    .withStyle(ChatFormatting.YELLOW)
            );
        }

        tooltipComponents.add(
            Component.translatable("item.evenmoreinstruments.looper_adapter.instrument.description")
                .withStyle(ChatFormatting.GRAY)
        );
        tooltipComponents.add(
            Component.translatable("item.evenmoreinstruments.looper_adapter.looper.description")
                .withStyle(ChatFormatting.GRAY)
        );

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    
}
