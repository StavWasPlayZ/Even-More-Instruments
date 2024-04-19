package com.cstav.evenmoreinstruments.criteria;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class RecordInjectedTrigger extends SimpleCriterionTrigger<RecordInjectedTrigger.TriggerInstance> {

    @Override
    protected TriggerInstance createInstance(JsonObject pJson, Optional<ContextAwarePredicate> player, DeserializationContext pContext) {
        return new TriggerInstance(player, ItemPredicate.fromJson(pJson.get("record")));
    }

    public void trigger(final ServerPlayer player, final ItemStack record) {
        trigger(player, (triggerInstance) ->
            triggerInstance.matches(record)
        );
    }


    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final Optional<ItemPredicate> item;

        public TriggerInstance(Optional<ContextAwarePredicate> pPlayer, Optional<ItemPredicate> item) {
            super(pPlayer);
            this.item = item;
        }

        public boolean matches(ItemStack record) {
            return item.isEmpty() || item.get().matches(record);
        }
    }

}
