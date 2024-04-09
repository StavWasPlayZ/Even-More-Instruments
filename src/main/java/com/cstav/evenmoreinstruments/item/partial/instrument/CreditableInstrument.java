package com.cstav.evenmoreinstruments.item.partial.instrument;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public interface CreditableInstrument {
    String getCredit();

    default Component getCreditAsComponent() {
        return Component.literal(getCredit()).withStyle(ChatFormatting.GRAY);
    }
}
