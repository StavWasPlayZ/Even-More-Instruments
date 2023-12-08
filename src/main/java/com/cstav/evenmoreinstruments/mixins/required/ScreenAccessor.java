package com.cstav.evenmoreinstruments.mixins.required;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface ScreenAccessor {

    @Invoker
    public void invokeRemoveWidget(GuiEventListener listener);

}
