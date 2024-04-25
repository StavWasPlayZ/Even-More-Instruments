package com.cstav.evenmoreinstruments.block.util;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@FunctionalInterface
public interface LooperInteractionRunnable {
    InteractionResult run(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                          LooperBlockEntity lbe, ItemStack heldStack, BlockHitResult pHit);
}
