package com.cstav.evenmoreinstruments.gamerule;

import com.cstav.evenmoreinstruments.EMIMain;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.Category;

public abstract class ModGameRules {

    public static void load() {}

    public static final GameRules.Key<GameRules.IntegerValue>
        RULE_LOOPER_MAX_NOTES = GameRules.register(EMIMain.MODID+"_looperMaxNotes", Category.MISC, GameRules.IntegerValue.create(255))
    ;
    
}
