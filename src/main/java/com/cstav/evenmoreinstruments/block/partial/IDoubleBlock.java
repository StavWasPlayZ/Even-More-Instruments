package com.cstav.evenmoreinstruments.block.partial;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IDoubleBlock {
    BlockPos getOtherBlock(final BlockState state, final BlockPos blockPos, final Level level);
}
