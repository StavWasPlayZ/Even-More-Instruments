package com.cstav.evenmoreinstruments.item.partial.instrument;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An instrument item that derives sounds from a 3rd-party.
 */
public interface CreditableInstrument {
    @Nullable String getCredit();

    default Component getCreditAsComponent() {
        return (getCredit() == null)
            ? new TextComponent("")
            : new TextComponent(getCredit()).withStyle(ChatFormatting.GRAY);
    }
    default boolean hasCredit() {
        return getCredit() != null;
    }

    default void creditHoverText(final List<Component> pTooltipComponents) {
        if (hasCredit())
            pTooltipComponents.add(getCreditAsComponent());
    }
}
