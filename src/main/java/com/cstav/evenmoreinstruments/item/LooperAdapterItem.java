package com.cstav.evenmoreinstruments.item;

import com.cstav.evenmoreinstruments.Main;
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
        final BlockPos pos = pContext.getClickedPos();
        final Block block = pContext.getLevel().getBlockState(pContext.getClickedPos()).getBlock();

        final CompoundTag adapterTag = CommonUtil.getOrCreateElementTag(Main.modTag(pContext.getItemInHand()), "looperAdapter");
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
            return pair(adapterTag, blockPos, NbtUtils.readBlockPos(adapterTag.getCompound(LOOPER_POS_TAG)), player);

        adapterTag.put(BLOCK_INSTRUMENT_POS_TAG, NbtUtils.writeBlockPos(blockPos));
        player.displayClientMessage(
            Component.translatable("item.evenmoreinstruments.looper_adapter.select_looper").withStyle(ChatFormatting.GREEN)
        , true);
        return true;
    }
    private static boolean handleLooperBlock(BlockPos blockPos, CompoundTag adapterTag, Player player) {
        final BlockEntity be = player.getLevel().getBlockEntity(blockPos);
        if (!(be instanceof LooperBlockEntity lbe) || !LooperUtil.performChannelCheck(lbe, player))
            return false;

        if (adapterTag.contains(BLOCK_INSTRUMENT_POS_TAG, Tag.TAG_COMPOUND))
            return pair(adapterTag, NbtUtils.readBlockPos(adapterTag.getCompound(BLOCK_INSTRUMENT_POS_TAG)), blockPos, player);

        adapterTag.put(LOOPER_POS_TAG, NbtUtils.writeBlockPos(blockPos));
        player.displayClientMessage(
            Component.translatable("item.evenmoreinstruments.looper_adapter.select_instrument").withStyle(ChatFormatting.GREEN)
        , true);
        return true;
    }

    private static boolean pair(CompoundTag adapterTag, BlockPos instrumentBlockPos, BlockPos looperPos, Player player) {
        final Level level = player.getLevel();

        final BlockEntity be = level.getBlockEntity(looperPos),
            ibe = level.getBlockEntity(instrumentBlockPos);
        if (!(be instanceof LooperBlockEntity) || !(ibe instanceof InstrumentBlockEntity))
            return false;

            
        // Clear all compound keys after pairing
        for (final String key : adapterTag.getAllKeys())
            adapterTag.remove(key);

        return LooperUtil.performPair((LooperBlockEntity)be, () -> {

            final BlockState instrumentBlockState = level.getBlockState(instrumentBlockPos);
            final Block instrumentBlock = instrumentBlockState.getBlock();

            BlockPos otherBlockPos = null;
            if (instrumentBlock instanceof IDoubleBlock doubleBlock)
                otherBlockPos = doubleBlock.getOtherBlock(instrumentBlockState, instrumentBlockPos, level);

            LooperUtil.createLooperTag(ibe, looperPos);
            if (otherBlockPos != null)
                LooperUtil.createLooperTag(level.getBlockEntity(otherBlockPos), looperPos);

            ibe.setChanged();

            // Handle syncing data to client
            if (player instanceof ServerPlayer serverPlayer) {
                ModPacketHandler.sendToClient(new SyncModTagPacket(Main.modTag(ibe), instrumentBlockPos), serverPlayer);

                if (otherBlockPos != null)
                    ModPacketHandler.sendToClient(
                        new SyncModTagPacket(Main.modTag(ibe), otherBlockPos)
                    , serverPlayer);
            }

        }, player);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(
            Component.translatable("item.evenmoreinstruments.looper_adapter.description")
                .withStyle(ChatFormatting.GRAY)
        );
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    
}
