package com.cstav.evenmoreinstruments.mixins.required;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.level.GameRules;

@Mixin(GameRules.IntegerValue.class)
public interface GameRuleIntegerInvoker {
    
    @Invoker("create")
    public static GameRules.Type<GameRules.IntegerValue> invokeCreate(int pDefaultValue) {
        throw new AssertionError();
    }

}
