package com.cstav.evenmoreinstruments.sound;

import java.util.HashMap;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.NoteSoundRegistrer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Main.MODID);
    public static void register(final IEventBus bus) {
        SOUNDS.register(bus);
    }

    static {
        NOTEBLOCK_SOUNDS = new HashMap<>();
        registerNoteBlockSounds();
    }
    

    public static final NoteSound[]
        KEYBOARD = nsr(SOUNDS, loc("keyboard")).stereo().regsiterGrid(),

        VIOLIN_FULL_NOTE = nsr(SOUNDS, loc("violin_full")).regsiterGrid(),
        VIOLIN_HALF_NOTE = nsr(SOUNDS, loc("violin_half")).regsiterGrid(),

        TROMBONE = nsr(SOUNDS, loc("trombone")).regsiterGrid(),
        GUITAR = nsr(SOUNDS, loc("guitar")).regsiterGrid()
    ;


    private static final HashMap<NoteBlockInstrument, NoteSound> NOTEBLOCK_SOUNDS;
    public static NoteSound[] getNoteblockSounds(final NoteBlockInstrument instrumentType) {
        return new NoteSound[] {NOTEBLOCK_SOUNDS.get(instrumentType)};
    }

    private static void registerNoteBlockSounds() {
        final NoteSoundRegistrer registrer = nsr(SOUNDS, loc("note_block_instrument"));

        for (NoteBlockInstrument noteSound : NoteBlockInstrument.values()) {
            registrer.add(noteSound.getSoundEvent().get().getLocation());
            NOTEBLOCK_SOUNDS.put(noteSound, registrer.peek());
        }

        registrer.registerAll();
    }


    private static ResourceLocation loc(final String id) {
        return new ResourceLocation(Main.MODID, id);
    }
    /**
     * Shorthand for {@code new NoteSoundRegistrer(soundRegistrer, instrumentId)}
     */
    private static NoteSoundRegistrer nsr(DeferredRegister<SoundEvent> soundRegistrer, ResourceLocation instrumentId) {
        return new NoteSoundRegistrer(soundRegistrer, instrumentId);
    }

}
