package com.cstav.evenmoreinstruments.item.partial;

import com.cstav.genshinstrument.item.InstrumentItem;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An instrument item that derives sounds from a 3rd-party.
 */
public class CreditableBlockInstrumentItem extends BlockItem implements CreditableInstrument {
    private final String credit;

    public CreditableBlockInstrumentItem(Block pBlock, Properties pProperties, String credit) {
        super(pBlock, pProperties);
        this.credit = credit;
    }

    @Override
    public String getCredit() {
        return credit;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(getCreditAsComponent());
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
