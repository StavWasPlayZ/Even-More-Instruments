package com.cstav.evenmoreinstruments.item.partial.instrument;

import com.cstav.genshinstrument.item.InstrumentItem;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    public @Nullable String getCredit() {
        return credit;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        creditHoverText(pTooltipComponents);
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }

}
