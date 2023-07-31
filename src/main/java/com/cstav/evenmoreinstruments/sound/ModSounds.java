package com.cstav.evenmoreinstruments.sound;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.NoteSoundRegistrer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Main.MODID);
    public static void register(final IEventBus bus) {
        SOUNDS.register(bus);
    }

    public static final NoteSound[]
        KEYBOARD = NoteSoundRegistrer.createInstrumentNotes(SOUNDS, loc("keyboard"), true),

        VIOLIN_FULL_NOTE = NoteSoundRegistrer.createInstrumentNotes(SOUNDS, loc("violin_full")),
        VIOLIN_HALF_NOTE = NoteSoundRegistrer.createInstrumentNotes(SOUNDS, loc("violin_half")),

        TROMBONE = NoteSoundRegistrer.createInstrumentNotes(SOUNDS, loc("trombone"))
    ;


    private static ResourceLocation loc(final String id) {
        return new ResourceLocation(Main.MODID, id);
    }

}
