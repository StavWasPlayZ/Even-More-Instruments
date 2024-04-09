package com.cstav.evenmoreinstruments.item.partial.instrument;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface CreditableInstrument {
    @Nullable String getCredit();

    default Component getCreditAsComponent() {
        return (getCredit() == null) ? Component.empty() : Component.literal(getCredit()).withStyle(ChatFormatting.GRAY);
    }
    default boolean hasCredit() {
        return getCredit() != null;
    }
}
