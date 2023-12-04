package com.cstav.evenmoreinstruments.item;

import com.cstav.evenmoreinstruments.block.KeyboardStandBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class KeyboardBlockItem extends BlockItem {

    public KeyboardBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        final Level level = pContext.getLevel();
        
        final BlockPos pos = pContext.getClickedPos();
        final BlockState bs = level.getBlockState(pos);
        

        if (bs.getBlock() instanceof KeyboardStandBlock) {
            if (bs.getValue(KeyboardStandBlock.HAS_KEYBOARD))
                return InteractionResult.FAIL;
            
            level.setBlock(pos, bs.setValue(KeyboardStandBlock.HAS_KEYBOARD, true), 3);
            pContext.getPlayer().getItemInHand(pContext.getHand()).shrink(1);
            return InteractionResult.SUCCESS;
        }
        
        return super.useOn(pContext);
    }
}
