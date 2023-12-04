package com.cstav.evenmoreinstruments.mixins.optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cstav.evenmoreinstruments.block.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.Level;

@Mixin(Parrot.class)
public abstract class ParrotLooperDanceInjector extends Entity {

    public ParrotLooperDanceInjector(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Shadow
    private BlockPos jukebox;
    @Shadow
    private boolean partyParrot;

    @Unique
    private boolean isLooper;
    @Unique
    private BlockPos jukeboxBefore;
    @Unique
    private boolean partiedBefore;


    @Inject(at = @At(value = "HEAD"), method = "aiStep()V")
    private void aiStepHead(final CallbackInfo info) {
        jukeboxBefore = jukebox;
        isLooper = (jukebox != null) && level().getBlockState(jukebox).is(ModBlocks.LOOPER.get());

        partiedBefore = partyParrot;
    }

    @Inject(at = @At(value = "TAIL"), method = "aiStep()V")
    private void aiStepTail(final CallbackInfo info) {
        if (!isLooper)
            return;
        // If the parrot is not dancing to the looper, it means it has stopped playing
        if (!partiedBefore)
            jukebox = null;
            
        else if (jukeboxBefore.closerToCenterThan(position(), 3.46)) {
            partyParrot = true;
            jukebox = jukeboxBefore;
        }
    }

    
    
}
