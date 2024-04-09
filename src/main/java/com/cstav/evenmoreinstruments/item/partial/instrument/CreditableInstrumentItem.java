package com.cstav.evenmoreinstruments.item.partial.instrument;

import com.cstav.genshinstrument.item.InstrumentItem;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An instrument item that derives sounds from a 3rd-party.
 */
public class CreditableInstrumentItem extends InstrumentItem implements CreditableInstrument {
    private final String credit;
    public CreditableInstrumentItem(OpenInstrumentPacketSender onOpenRequest, String credit) {
        super(onOpenRequest);
        this.credit = credit;
    }
    public CreditableInstrumentItem(OpenInstrumentPacketSender onOpenRequest, Properties properties, String credit) {
        super(onOpenRequest, properties);
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
