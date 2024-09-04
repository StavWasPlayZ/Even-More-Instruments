package com.cstav.evenmoreinstruments.item.partial.instrument;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreditableBlockInstrumentItem extends BlockItem implements CreditableInstrument {
    private final String credit;

    public CreditableBlockInstrumentItem(Block pBlock, Properties pProperties, String credit) {
        super(pBlock, pProperties);
        this.credit = credit;
    }

    @Override
    public @Nullable String getCredit() {
        return credit;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        creditHoverText(pTooltipComponents);
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }
}
