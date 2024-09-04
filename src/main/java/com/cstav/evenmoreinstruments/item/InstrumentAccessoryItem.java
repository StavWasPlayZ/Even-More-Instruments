package com.cstav.evenmoreinstruments.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * An item acting for an {@link AccessoryInstrumentItem}
 */
public class InstrumentAccessoryItem extends Item {
    public static final int MAX_DURABILITY = 2048;

    public InstrumentAccessoryItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pMiningEntity) {
        pStack.hurtAndBreak(
            pState.getDestroySpeed(pLevel, pPos) == 0 ? 3 : 10,
            pMiningEntity,
            EquipmentSlot.MAINHAND
        );

        return false;
    }
}
