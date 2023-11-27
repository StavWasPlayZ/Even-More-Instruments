package com.cstav.evenmoreinstruments.block;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.block.blockentity.ModBlockEntities;
import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.LooperPlayStatePacket;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.genshinstrument.item.InstrumentItem;

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
    

    public LooperBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(PLAYING, false)
            .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PLAYING, FACING);
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


    public BlockState setPlaying(boolean isPlaying, Level level, BlockState blockState, BlockPos blockPos) {
        final BlockState newState = blockState.setValue(PLAYING, isPlaying);

        // should always be serverside but whatever
        if (!level.isClientSide)
            level.players().forEach((player) ->
                ModPacketHandler.sendToClient(new LooperPlayStatePacket(isPlaying, blockPos), (ServerPlayer)player)
            );

        return newState;
    }
    public BlockState cyclePlaying(Level level, BlockState blockState, BlockPos blockPos) {
        return setPlaying(!blockState.getValue(PLAYING), level, blockState, blockPos);
    }


    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {
        if (pLevel.isClientSide)
            return InteractionResult.CONSUME;

        final BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof LooperBlockEntity lbe))
            return InteractionResult.FAIL;

        final boolean hasChannel = lbe.hasFootage();
        final ItemStack itemStack = pPlayer.getItemInHand(pHand);
        

        // Handle pairing
        if (itemStack.getItem() instanceof InstrumentItem) {

            if (LooperUtil.performPair(lbe, () -> LooperUtil.createLooperTag(itemStack, pPos), pPlayer))
                return InteractionResult.SUCCESS;

        }


        //TODO: Add a GUI for the looper and trigger it for display here
        // Then make it so that only by holding shift can you pause and play
        // since you'll be able to do that there anyways
        if (hasChannel) {
            pLevel.setBlock(pPos, cyclePlaying(pLevel, pState, pPos), 3);
            return InteractionResult.SUCCESS;
        }
        else {
            pPlayer.displayClientMessage(
                Component.translatable("evenmoreinstruments.looper.no_footage")
            , true);
            return InteractionResult.CONSUME_PARTIAL;
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
