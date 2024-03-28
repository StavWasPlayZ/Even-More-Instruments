package com.cstav.evenmoreinstruments.block;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.block.blockentity.ModBlockEntities;
import com.cstav.evenmoreinstruments.item.partial.emirecord.EMIRecordItem;
import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.LooperPlayStatePacket;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.genshinstrument.item.InstrumentItem;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class LooperBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    //TODO: Redstone should trigger this
    public static final BooleanProperty PLAYING = BooleanProperty.create("playing");
    public static final BooleanProperty RECORD_IN = BooleanProperty.create("record_in");


    public LooperBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(PLAYING, false)
            .setValue(RECORD_IN, false)
            .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PLAYING, FACING, RECORD_IN);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LooperBlockEntity(pPos, pState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
            BlockEntityType<T> pBlockEntityType) {
                
        return (!pLevel.isClientSide && pBlockEntityType == ModBlockEntities.LOOPER.get())
            ? (level, pos, state, be) -> ((LooperBlockEntity)(be)).tick(level, pos, state)
            : null;
    }

    public static BlockState cyclePlaying(LooperBlockEntity lbe, BlockState state) {
        return lbe.setPlaying(!state.getValue(PLAYING), state);
    }


    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {
        if (pLevel.isClientSide)
            return InteractionResult.CONSUME;

        final BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof LooperBlockEntity lbe))
            return InteractionResult.FAIL;

        final ItemStack itemStack = pPlayer.getItemInHand(pHand);

        // Check for a record's presence
        boolean recordInjected = false;
        BlockState newState = pState;

        if (itemStack.getItem() instanceof EMIRecordItem recordItem) {
            if (!pState.getValue(RECORD_IN)) {
                newState = newState.setValue(RECORD_IN, true);
            } else {
                lbe.popRecord();
            }

            lbe.setRecordData(recordItem.toLooperData(itemStack));
            lbe.setChanged();

            if (!pPlayer.isCreative())
                itemStack.shrink(1);
            recordInjected = true;
        } else if (!pState.getValue(RECORD_IN)) {
            pPlayer.displayClientMessage(
                Component.translatable("evenmoreinstruments.looper.no_record").withStyle(ChatFormatting.RED)
                , true);
            return InteractionResult.FAIL;
        }
        

        // Handle pairing
        if (itemStack.getItem() instanceof InstrumentItem) {

            if (LooperUtil.performPair(lbe, () -> LooperUtil.createLooperTag(itemStack, pPos), pPlayer))
                return InteractionResult.SUCCESS;

        }

        // Ejecting the record
        if (pState.getValue(RECORD_IN) && (pPlayer.isShiftKeyDown() || !lbe.hasFootage())) {
            pLevel.setBlockAndUpdate(pPos, cyclePlaying(lbe, pState.setValue(RECORD_IN, false)));
            lbe.popRecord();
            return InteractionResult.SUCCESS;
        }


        //TODO: Add a GUI for the looper and trigger it for display here
        // Then make it so that only by holding shift can you pause and play
        // since you'll be able to do that there anyways
        if (lbe.hasFootage()) {
            pLevel.setBlockAndUpdate(pPos, cyclePlaying(lbe, newState));
            return InteractionResult.SUCCESS;
        }
        else {
            pPlayer.displayClientMessage(
                Component.translatable("evenmoreinstruments.looper.no_footage")
            , true);

            if (newState != pState)
                pLevel.setBlockAndUpdate(pPos, newState);
            return recordInjected ? InteractionResult.SUCCESS : InteractionResult.CONSUME_PARTIAL;
        }

    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock,
            BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (pLevel.isClientSide)
            return;
                
        if (pLevel.hasNeighborSignal(pPos) && (pLevel.getBlockEntity(pPos) instanceof LooperBlockEntity lbe))
            lbe.setTicks(lbe.getRepeatTick() + 1);
    }


    @Override
    public boolean triggerEvent(BlockState pState, Level pLevel, BlockPos pPos, int pId, int pParam) {
        if (pId != 42)
            return false;

        // Copied from note block
        pLevel.addParticle(ParticleTypes.NOTE,
            (double)pPos.getX() + 0.5D, (double)pPos.getY() + 1.2D, (double)pPos.getZ() + 0.5D,
            (double)pParam / 24.0D, 0.0D, 0.0D
        );

        return true;
    }

}
