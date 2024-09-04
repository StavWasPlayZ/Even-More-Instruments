package com.cstav.evenmoreinstruments.block.util;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@FunctionalInterface
public interface LooperInteractionRunnableNI {
    InteractionResult run(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                          LooperBlockEntity lbe, BlockHitResult pHitResult);
}
