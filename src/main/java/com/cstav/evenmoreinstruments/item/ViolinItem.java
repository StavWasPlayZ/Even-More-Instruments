package com.cstav.evenmoreinstruments.item;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.packet.instrument.util.InstrumentPacketUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

class ViolinItem extends AccessoryInstrumentItem {
    public ViolinItem() {
        super((player) -> InstrumentPacketUtil.sendOpenPacket(
                player, EMIMain.loc("violin")
            ),
            (InstrumentAccessoryItem) ModItems.VIOLIN_BOW.get(),
            "Philharmonia"
        );
    }

    @Override
    public int hurtAccessoryBy(final InstrumentPlayedEvent<?> event, final ItemStack accessory) {
        // If we did a long press, deal damage by 2.
        final boolean playedLong = Arrays.stream(ModSounds.VIOLIN_FULL_NOTE)
            .anyMatch((sound) -> sound.equals(event.sound()));

        return super.hurtAccessoryBy(event, accessory) * (playedLong ? 2 : 1);
    }
}
