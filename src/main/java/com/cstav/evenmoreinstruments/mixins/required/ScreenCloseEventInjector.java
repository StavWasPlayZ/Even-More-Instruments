package com.cstav.evenmoreinstruments.mixins.required;

import com.cstav.evenmoreinstruments.event.ScreenCloseEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenCloseEventInjector {

    @Inject(at = @At("TAIL"), method = "onClose")
    public void onCloseInjector(final CallbackInfo info) {
        MinecraftForge.EVENT_BUS.post(new ScreenCloseEvent((Screen)((Object)this)));
    }

}
