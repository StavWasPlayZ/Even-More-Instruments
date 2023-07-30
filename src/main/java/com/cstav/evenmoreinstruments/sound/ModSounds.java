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
        VIOLIN = NoteSoundRegistrer.createInstrumentNotes(SOUNDS, loc("violin"))
    ;


    private static ResourceLocation loc(final String id) {
        return new ResourceLocation(Main.MODID, id);
    }

}
