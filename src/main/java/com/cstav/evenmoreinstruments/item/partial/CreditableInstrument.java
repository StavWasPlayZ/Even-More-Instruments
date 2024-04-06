package com.cstav.evenmoreinstruments.item.partial;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public interface CreditableInstrument {
    String getCredit();

    default Component getCreditAsComponent() {
        return Component.literal(getCredit()).withStyle(ChatFormatting.GRAY);
    }
}
