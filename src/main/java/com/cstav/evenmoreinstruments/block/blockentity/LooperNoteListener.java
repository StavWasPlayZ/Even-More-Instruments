package com.cstav.evenmoreinstruments.block.blockentity;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.genshinstrument.event.HeldNoteSoundPlayedEvent;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.event.NoteSoundPlayedEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.Optional;

import static com.cstav.evenmoreinstruments.item.emirecord.EMIRecordItem.INSTRUMENT_ID_TAG;

/**
 * Listens to instrument played events
 * and writes it to a matching looper.
 */
@EventBusSubscriber(bus = Bus.FORGE, modid = EMIMain.MODID)
public class LooperNoteListener {

    @SubscribeEvent
    public static void onNoteSoundPlayed(final NoteSoundPlayedEvent event) {
        getMatchingLooper(event).ifPresent((looperBE) ->
            looperBE.writeNote(event.sound(), event.soundMeta(), looperBE.getTicks())
        );
    }

    @SubscribeEvent
    public static void onHeldNoteSoundPlayed(final HeldNoteSoundPlayedEvent event) {
        getMatchingLooper(event).ifPresent((looperBE) ->
            looperBE.writeHeldNote(event.sound(), event.phase, event.soundMeta(), looperBE.getTicks())
        );
    }


    /**
     * @return The looper matching the provided event
     */
    private static Optional<LooperBlockEntity> getMatchingLooper(final InstrumentPlayedEvent<?> event) {
        // Only get player events
        if (!event.isByPlayer())
            return Optional.empty();

        final Player player = (Player) event.entityInfo().get().entity;

        if (event.level().isClientSide || !LooperUtil.isRecording(player))
            return Optional.empty();


        final Level level = player.level();

        final LooperBlockEntity looperBE = LooperUtil.getFromEvent(event);
        if (looperBE == null || looperBE.isCapped(level))
            return Optional.empty();

        // Omit if record is not writable
        if (!looperBE.isWritable())
            return Optional.empty();


        if (looperBE.isLocked()) {
            if (!looperBE.isRecording() || !looperBE.isAllowedToRecord(player))
                return Optional.empty();
        } else {
            looperBE.setLockedBy(player);
            looperBE.setRecording(true);
            looperBE.getChannel().putString(INSTRUMENT_ID_TAG, event.soundMeta().instrumentId().toString());
        }

        return Optional.of(looperBE);
    }

}
