package com.cstav.evenmoreinstruments.criteria;

import com.cstav.genshinstrument.mixins.required.CriterionRegisterInvoker;
import net.minecraft.advancements.CriterionTrigger;

public class ModCriteria {
    public static void load() {}

    // It doesn't account for namespaces, so will use evenmoreinstruments_ prefix instead
    public static final RecordInjectedTrigger RECORD_INJECTED_TRIGGER = register("evenmoreinstruments_record_injected", new RecordInjectedTrigger());


    private static <T extends CriterionTrigger<?>> T register(String id, T criterion) {
        return CriterionRegisterInvoker.callRegister(id, criterion);
    }
}
