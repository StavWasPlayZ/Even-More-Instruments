package com.cstav.genshinstrumentp.block;

import com.cstav.genshinstrument.item.InstrumentItem;
import com.cstav.genshinstrumentp.block.blockentity.LooperBlockEntity;
import com.cstav.genshinstrumentp.block.blockentity.ModBlockEntities;
import com.cstav.genshinstrumentp.util.LooperUtil;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class LooperBlock extends Block implements EntityBlock {

    public static final BooleanProperty PLAYING = BooleanProperty.create("playing");

    public LooperBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PLAYING, false));
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
            pLevel.setBlock(pPos, pState.cycle(PLAYING), 3);
            
            return InteractionResult.SUCCESS;
        }

        if ((itemStack.getItem() instanceof InstrumentItem) && !LooperUtil.isSameBlock(itemStack, pPos)) {
            LooperUtil.createLooperTag(itemStack, pPos);
            return InteractionResult.SUCCESS;
        }

        //TODO: Add a GUI for the looper and trigger it for display here

        return InteractionResult.SUCCESS;
    }
    

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PLAYING);
    }
}
