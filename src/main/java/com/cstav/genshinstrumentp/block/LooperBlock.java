package com.cstav.genshinstrumentp.block;

import javax.annotation.Nullable;

import com.cstav.genshinstrument.item.InstrumentItem;
import com.cstav.genshinstrumentp.Main;
import com.cstav.genshinstrumentp.Util;
import com.cstav.genshinstrumentp.block.blockentity.LooperBlockEntity;
import com.cstav.genshinstrumentp.block.blockentity.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class LooperBlock extends Block implements EntityBlock {
    public static final String LOOPER_TAG = "looper",
        POS_TAG = "pos", RECORDING_TAG = "recording", CHANNEL_TAG = "channel";

    public static boolean hasLooperTag(final ItemStack instrument) {
        return Main.modTag(instrument).contains(LOOPER_TAG, CompoundTag.TAG_COMPOUND);
    }

    public static CompoundTag looperTag(final ItemStack instrument) {
        final CompoundTag tag = Main.modTag(instrument);
        return tag.contains(LOOPER_TAG, CompoundTag.TAG_COMPOUND)
            ? tag.getCompound(LOOPER_TAG)
            : Util.TAG_EMPTY;
    }
    public static void remLooperTag(final ItemStack instrument) {
        Main.modTag(instrument).remove(LOOPER_TAG);
    }

    public LooperBlock(Properties properties) {
        super(properties);
    }

    
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LooperBlockEntity(pPos, pState);
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
            BlockEntityType<T> pBlockEntityType) {
        return (pBlockEntityType == ModBlockEntities.LOOPER.get())
            ? (level, pos, state, be) -> ((LooperBlockEntity)(be)).tick(level, pos, state)
            : null;
    }



    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {
        final ItemStack itemStack = pPlayer.getItemInHand(pHand);
        
        if (pPlayer.isShiftKeyDown()) {
            final LooperBlockEntity lbe = pLevel.getBlockEntity(pPos, ModBlockEntities.LOOPER.get()).get();
            lbe.setPlaying(!lbe.isPlaying());
            lbe.setChanged();
            
            return InteractionResult.SUCCESS;
        }

        if ((itemStack.getItem() instanceof InstrumentItem) && !isSameBlock(itemStack, pPos)) {
            looperTag(itemStack).put(POS_TAG, NbtUtils.writeBlockPos(pPos));
            setRecording(itemStack, false);
            setChannel(itemStack, 0);

            return InteractionResult.SUCCESS;
        }

        //TODO: Add a GUI for the looper and trigger it for display here

        return InteractionResult.SUCCESS;
    }


    //TODO move all the below to instrument looper utils or smth

    /**
     * @param instrument The item to check its NBT against
     * @param pos The position of the block to check for {@code item}
     * @return Whether {@code item} is pointing to a looper, and if its position is equal to {@code pos}'s
     */
    private static boolean isSameBlock(final ItemStack instrument, final BlockPos pos) {
        try {
            return getLooperPos(instrument).equals(pos);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Nullable
    public static BlockPos getLooperPos(final ItemStack instrument) {
        final CompoundTag looperPosTag = looperTag(instrument).getCompound(POS_TAG);
        return (looperPosTag == null) ? null : NbtUtils.readBlockPos(looperPosTag);
    }
    
    public static void setRecording(final ItemStack instrument, final boolean recording) {
        looperTag(instrument).putBoolean(RECORDING_TAG, recording);
    }
    public static void setChannel(final ItemStack instrument, final int channel) {
        looperTag(instrument).putInt(CHANNEL_TAG, channel);
    }
    public static boolean isRecording(final ItemStack instrument) {
        return looperTag(instrument).getBoolean(RECORDING_TAG);
    }
    public static int getChannel(final ItemStack instrument) {
        return looperTag(instrument).getInt(CHANNEL_TAG);
    }
    
}
