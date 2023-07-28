package com.cstav.evenmoreinstruments.block.blockentity;

import com.cstav.genshinstrument.block.partial.InstrumentBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ModInstrumentBlockEntity extends InstrumentBlockEntity {

    public ModInstrumentBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.INSTRUMENT.get(), pPos, pBlockState);
    }
    
}
