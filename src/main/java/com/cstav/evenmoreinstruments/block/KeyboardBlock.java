package com.cstav.evenmoreinstruments.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KeyboardBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<KeyboardBlock.KeyboardPart> PART = EnumProperty.create("part", KeyboardPart.class);

    private static final VoxelShape
        SHAPE_LEFT_SOUTH = Block.box(0.0D, 0.0D, 4.0D, 15.65D, 4.4D, 12.8D),
        SHAPE_RIGHT_SOUTH = Block.box(0.3D, 0.0D, 4D, 16.0D, 4.4D, 12.8D),

        SHAPE_LEFT_EAST = Block.box(3.5D, 0.0D, 0.0D, 12D, 4.4D, 15.65D),
        SHAPE_RIGHT_EAST = Block.box(3.5D, 0.0D, 0.3D, 12D, 4.4D, 16D);
    ;


    public KeyboardBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(PART, KeyboardPart.LEFT)
        );
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        final Direction facing = pState.getValue(FACING);
        final boolean southOrNorth = (facing == Direction.SOUTH) || (facing == Direction.NORTH);

        return (pState.getValue(PART) == KeyboardPart.LEFT)
            ? (southOrNorth ? SHAPE_LEFT_SOUTH : SHAPE_LEFT_EAST)
            : (southOrNorth ? SHAPE_RIGHT_SOUTH : SHAPE_RIGHT_EAST);
    }


    //idk how to do maths
    private static final Direction[] DIRECTIONS = {
        Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };
    private static Direction getOffset(final Direction direction, final int offset) {
        for (int i = 0; i < DIRECTIONS.length; i++)
            if (DIRECTIONS[i] == direction)
                return DIRECTIONS[pyWrap(i + offset, DIRECTIONS.length) % DIRECTIONS.length];

        throw new IllegalStateException("How did we get here?");
    }
    private static Direction getLeft(final Direction direction) {
        return getOffset(direction, 1);
    }
    private static Direction getRight(final Direction direction) {
        return getOffset(direction, -1);
    }
    //TODO: Move to CommonUtil and make public in genshinstrument
    private static int pyWrap(int index, int arrLength) {
        while (index < 0) {
           index += arrLength;
        }
  
        return index;
    }


    // Handle 2 blocks
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (pLevel.isClientSide)
            return;

        pLevel.setBlock(
            pPos.relative(getRight(pState.getValue(FACING))),
            pState.setValue(PART, KeyboardPart.RIGHT)
        , 3);
        pLevel.blockUpdated(pPos, Blocks.AIR);
        pState.updateNeighbourShapes(pLevel, pPos, 3);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pLevel.isClientSide)
            return;

        final BlockPos sideBlock = pPos.relative((pState.getValue(PART) == KeyboardPart.LEFT)
            ? getRight(pState.getValue(FACING))
            : getLeft(pState.getValue(FACING))
        );
        if (!(pLevel.getBlockState(sideBlock).getBlock() instanceof KeyboardBlock))
            return;

        pLevel.setBlock(sideBlock,
            Blocks.AIR.defaultBlockState()
        , 3);
        pLevel.blockUpdated(pPos, Blocks.AIR);
        pState.updateNeighbourShapes(pLevel, pPos, 3);
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, PART);
    }
    

    public static enum KeyboardPart implements StringRepresentable {
        LEFT, RIGHT;

        @Override
        public String getSerializedName() {
            return toString().toLowerCase();
        }
    }
}