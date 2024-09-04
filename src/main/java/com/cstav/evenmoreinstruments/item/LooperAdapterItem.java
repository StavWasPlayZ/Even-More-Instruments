package com.cstav.evenmoreinstruments.item;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.block.LooperBlock;
import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.block.partial.IDoubleBlock;
import com.cstav.evenmoreinstruments.item.component.ModDataComponents;
import com.cstav.evenmoreinstruments.networking.EMIPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.SyncModTagPacket;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.genshinstrument.block.partial.AbstractInstrumentBlock;
import com.cstav.genshinstrument.block.partial.InstrumentBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
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

        final ItemStack adapterItem = pContext.getItemInHand();
        final Player player = pContext.getPlayer();

        boolean pairSucceed;
        if (block instanceof AbstractInstrumentBlock)
            pairSucceed = handleInstrumentBlock(pos, adapterItem, player);
        else if (block instanceof LooperBlock)
            pairSucceed = handleLooperBlock(pos, adapterItem, player);
        else
            return InteractionResult.FAIL;

        return pairSucceed ? InteractionResult.SUCCESS : InteractionResult.CONSUME_PARTIAL;
    }

    private static boolean handleInstrumentBlock(BlockPos blockPos, ItemStack adapterItem, Player player) {
        if (adapterItem.has(ModDataComponents.LOOPER_POS.get()))
            return pairLooperToInstrument(adapterItem, adapterItem.get(ModDataComponents.LOOPER_POS.get()), blockPos, player);

        adapterItem.set(ModDataComponents.BLOCK_INSTRUMENT_POS.get(), blockPos);
        player.displayClientMessage(
            Component.translatable("item.evenmoreinstruments.looper_adapter.looper.select")
        , true);
        return true;
    }
    private static boolean handleLooperBlock(BlockPos blockPos, ItemStack adapterItem, Player player) {
        final BlockEntity be = player.level().getBlockEntity(blockPos);
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

        if (adapterItem.has(ModDataComponents.BLOCK_INSTRUMENT_POS.get()))
            return pairLooperToInstrument(adapterItem, blockPos, adapterItem.get(ModDataComponents.BLOCK_INSTRUMENT_POS.get()), player);
        if (adapterItem.has(ModDataComponents.LOOPER_POS.get()))
            return syncLoopers(adapterItem, adapterItem.get(ModDataComponents.LOOPER_POS.get()), blockPos, player);

        adapterItem.set(ModDataComponents.LOOPER_POS.get(), blockPos);
        player.displayClientMessage(
            Component.translatable("item.evenmoreinstruments.looper_adapter.instrument.select").withStyle(ChatFormatting.GREEN),
            true
        );
        return true;
    }


    private static boolean pairLooperToInstrument(ItemStack adapterItem, InstrumentBlockEntity ibe, LooperBlockEntity lbe, Player player) {
        clearComponents(adapterItem);

        return LooperUtil.performPair(lbe, () -> {

            final BlockState instrumentBlockState = ibe.getBlockState();
            final Block instrumentBlock = instrumentBlockState.getBlock();

            final BlockPos instrumentBlockPos = ibe.getBlockPos(),
                looperBlockPos = lbe.getBlockPos();

            // Linked blocks (like the Keyboard) should too have the tag:
            BlockPos otherBlockPos = null;
            if (instrumentBlock instanceof IDoubleBlock doubleBlock)
                otherBlockPos = doubleBlock.getOtherBlock(instrumentBlockState, instrumentBlockPos, player.level());

            LooperUtil.createLooperTag(ibe, looperBlockPos);
            if (otherBlockPos != null)
                LooperUtil.createLooperTag(player.level().getBlockEntity(otherBlockPos), looperBlockPos);

            ibe.setChanged();

            // Handle syncing data to client
            if (player instanceof ServerPlayer serverPlayer) {
                EMIPacketHandler.sendToClient(new SyncModTagPacket(EMIMain.modTag(ibe), instrumentBlockPos), serverPlayer);
                if (otherBlockPos != null)
                    EMIPacketHandler.sendToClient(new SyncModTagPacket(EMIMain.modTag(ibe), otherBlockPos), serverPlayer);
            }

        }, player);
    }
    private static boolean pairLooperToInstrument(ItemStack adapterItem, BlockPos looperPos, BlockPos instrumentPos, Player player) {
        final Level level = player.level();

        final BlockEntity lbe = level.getBlockEntity(looperPos),
            ibe = level.getBlockEntity(instrumentPos);
        if (!(lbe instanceof LooperBlockEntity) || !(ibe instanceof InstrumentBlockEntity))
            return false;

        return pairLooperToInstrument(adapterItem, (InstrumentBlockEntity)ibe, (LooperBlockEntity)lbe, player);
    }

    /**
     * Syncs the 2 loopers by setting their repeat ticks to be of the lower looper.
     */
    private static boolean syncLoopers(ItemStack adapterItem, LooperBlockEntity lbe1, LooperBlockEntity lbe2, Player player) {
        if (lbe1.getBlockPos().equals(lbe2.getBlockPos()))
            return false;

        clearComponents(adapterItem);

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
    private static boolean syncLoopers(ItemStack adapterItem, BlockPos looper1Pos, BlockPos looper2Pos, Player player) {
        if (looper2Pos.equals(looper1Pos))
            return false;

        final Level level = player.level();

        BlockEntity lbe1 = level.getBlockEntity(looper1Pos),
            lbe2 = level.getBlockEntity(looper2Pos);

        if (!(lbe1 instanceof LooperBlockEntity) || !(lbe2 instanceof LooperBlockEntity))
            return false;

        return syncLoopers(adapterItem, (LooperBlockEntity)lbe1, (LooperBlockEntity)lbe2, player);
    }


    public static void clearComponents(final ItemStack adapterItem) {
        adapterItem.remove(ModDataComponents.LOOPER_POS.get());
        adapterItem.remove(ModDataComponents.BLOCK_INSTRUMENT_POS.get());
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> tooltipComponents, TooltipFlag pTooltipFlag) {
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
        super.appendHoverText(pStack, pContext, tooltipComponents, pTooltipFlag);
    }
    
}
