package com.cstav.evenmoreinstruments.block.partial;

import com.cstav.evenmoreinstruments.block.blockentity.ModInstrumentBlockEntity;
import com.cstav.genshinstrument.block.partial.AbstractInstrumentBlock;
import com.cstav.genshinstrument.block.partial.InstrumentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class DoubleInstrumentBlock extends AbstractInstrumentBlock implements IDoubleBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<BlockPart> PART = EnumProperty.create("part", BlockPart.class);


    public DoubleInstrumentBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(PART, BlockPart.LEFT)
        );
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InstrumentBlockEntity newBlockEntity(BlockPos arg0, BlockState arg1) {
        return new ModInstrumentBlockEntity(arg0, arg1);
    }


    // Handle 2 blocks
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (pLevel.isClientSide)
            return;

        final BlockPos sidePos = pPos.relative(pState.getValue(FACING).getCounterClockWise());

        pLevel.setBlock(sidePos,
            pState.setValue(PART, BlockPart.RIGHT)
            , 3);
        pLevel.blockUpdated(pPos, Blocks.AIR);
        pLevel.blockUpdated(sidePos, Blocks.AIR);
        pState.updateNeighbourShapes(pLevel, pPos, 3);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pLevel.isClientSide)
            return;

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);


        final BlockPos sideBlock = getOtherBlock(pState, pPos, pLevel);
        if (sideBlock == null)
            return;

        pLevel.setBlock(sideBlock,
            Blocks.AIR.defaultBlockState()
            , 1|2|4);
        pLevel.blockUpdated(pPos, Blocks.AIR);
        pState.updateNeighbourShapes(pLevel, pPos, 1|2|4);
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        final Direction direction = pContext.getHorizontalDirection();
        final BlockPos pos = pContext.getClickedPos();
        final BlockPos sidePos = pos.relative(direction.getClockWise());

        final Level level = pContext.getLevel();

        return (
            level.getBlockState(sidePos).canBeReplaced(pContext) && level.getWorldBorder().isWithinBounds(sidePos)
                && (level.getBlockState(pos.below(1)).canOcclude() || level.getBlockState(sidePos.below(1)).canOcclude())
        )
            ? defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite())
            : null;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, PART);
    }


    public static enum BlockPart implements StringRepresentable {
        LEFT, RIGHT;

        @Override
        public String getSerializedName() {
            return toString().toLowerCase();
        }
    }


    @Override
    public BlockPos getOtherBlock(final BlockState state, BlockPos blockPos, Level level) {
        final BlockPos sideBlock = blockPos.relative((state.getValue(PART) == BlockPart.LEFT)
            ? state.getValue(FACING).getCounterClockWise()
            : state.getValue(FACING).getClockWise()
        );

        return (!level.getBlockState(sideBlock).is(state.getBlock())) ? null : sideBlock;
    }

}
