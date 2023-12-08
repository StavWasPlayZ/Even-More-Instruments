package com.cstav.evenmoreinstruments.block;

import com.cstav.evenmoreinstruments.block.blockentity.ModInstrumentBlockEntity;
import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.ModOpenInstrumentPacket;
import com.cstav.evenmoreinstruments.util.CommonUtil;
import com.cstav.genshinstrument.block.partial.AbstractInstrumentBlock;
import com.cstav.genshinstrument.block.partial.InstrumentBlockEntity;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;

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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KeyboardBlock extends AbstractInstrumentBlock implements IDoubleBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<KeyboardBlock.KeyboardPart> PART = EnumProperty.create("part", KeyboardPart.class);

    public static final VoxelShape
        SHAPE_LEFT_SOUTH = Block.box(0.0D, 0.0D, 4.0D, 15.65D, 4.4D, 12.8D),
        SHAPE_RIGHT_SOUTH = Block.box(0.3D, 0.0D, 4D, 16.0D, 4.4D, 12.8D),

        SHAPE_LEFT_EAST = Block.box(3.5D, 0.0D, 0.0D, 12D, 4.4D, 15.65D),
        SHAPE_RIGHT_EAST = Block.box(3.5D, 0.0D, 0.3D, 12D, 4.4D, 16D)
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

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }


    @Override
    protected OpenInstrumentPacketSender instrumentPacketSender() {
        return (player) -> ModPacketHandler.sendToClient(new ModOpenInstrumentPacket("keyboard"), player);
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

        final BlockPos sidePos = pPos.relative(CommonUtil.getRight(pState.getValue(FACING)));

        pLevel.setBlock(sidePos,
            pState.setValue(PART, KeyboardPart.RIGHT)
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
        final BlockPos sidePos = pos.relative(CommonUtil.getLeft(direction));

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
    

    public static enum KeyboardPart implements StringRepresentable {
        LEFT, RIGHT;

        @Override
        public String getSerializedName() {
            return toString().toLowerCase();
        }
    }


    @Override
    public BlockPos getOtherBlock(final BlockState state, BlockPos blockPos, Level level) {
        final BlockPos sideBlock = blockPos.relative((state.getValue(PART) == KeyboardPart.LEFT)
            ? CommonUtil.getRight(state.getValue(FACING))
            : CommonUtil.getLeft(state.getValue(FACING))
        );

        return (!level.getBlockState(sideBlock).is(ModBlocks.KEYBOARD.get())) ? null : sideBlock;
    }

}