package com.cstav.evenmoreinstruments.block;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.block.blockentity.ModBlockEntities;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.genshinstrument.item.InstrumentItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
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
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {
        if (pLevel.isClientSide)
            return InteractionResult.CONSUME;

        final BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof LooperBlockEntity))
            return InteractionResult.FAIL;

        final LooperBlockEntity lbe = (LooperBlockEntity) pLevel.getBlockEntity(pPos);
        final boolean hasChannel = lbe.hasChannel();


        final ItemStack itemStack = pPlayer.getItemInHand(pHand);
        

        // Handle pairing
        if (!pPlayer.isShiftKeyDown() && (itemStack.getItem() instanceof InstrumentItem)) {

            return LooperUtil.performPair(lbe, () -> LooperUtil.createLooperTag(itemStack, pPos), pPlayer)
                ? InteractionResult.SUCCESS
                : InteractionResult.CONSUME_PARTIAL;

        }


        //TODO: Add a GUI for the looper and trigger it for display here
        // Then make it so that only by holding shift can you pause and play
        // since you'll be able to do that there anyways
        if (hasChannel) {
            pLevel.setBlock(pPos, pState.cycle(PLAYING), 3);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME_PARTIAL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
            BlockEntityType<T> pBlockEntityType) {
                
        return (!pLevel.isClientSide && pBlockEntityType == ModBlockEntities.LOOPER.get())
            ? (level, pos, state, be) -> ((LooperBlockEntity)(be)).tick(level, pos, state)
            : null;
    }
    

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LooperBlockEntity(pPos, pState);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }


    @Override
    public boolean triggerEvent(BlockState pState, Level pLevel, BlockPos pPos, int pId, int pParam) {
        if (pId != 69420/*nice*/)
            return false;

        // Copied from note block
        pLevel.addParticle(ParticleTypes.NOTE,
            (double)pPos.getX() + 0.5D, (double)pPos.getY() + 1.2D, (double)pPos.getZ() + 0.5D,
            (double)pParam / 24.0D, 0.0D, 0.0D
        );

        return true;
    }


    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PLAYING, FACING);
    }
}
