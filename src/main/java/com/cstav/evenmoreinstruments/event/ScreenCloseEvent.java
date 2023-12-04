package com.cstav.evenmoreinstruments.event;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.eventbus.api.Event;

public class ScreenCloseEvent extends Event {

    public final Screen screen;
    public ScreenCloseEvent(final Screen screen) {
        this.screen = screen;
    }

}
