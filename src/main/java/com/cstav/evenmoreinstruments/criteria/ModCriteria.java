package com.cstav.evenmoreinstruments.criteria;

import static net.minecraft.advancements.CriteriaTriggers.register;

public class ModCriteria {
    public static void load() {}

    public static final RecordInjectedTrigger RECORD_INJECTED_TRIGGER = register(new RecordInjectedTrigger());

}
