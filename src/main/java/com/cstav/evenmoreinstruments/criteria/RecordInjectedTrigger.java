package com.cstav.evenmoreinstruments.criteria;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.advancements.critereon.EntityPredicate.Composite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RecordInjectedTrigger extends SimpleCriterionTrigger<RecordInjectedTrigger.TriggerInstance> {
    // It doesn't account for namespaces, so will use genshinstrument_ prefix instead
    public static final ResourceLocation ID = new ResourceLocation("evenmoreinstruments_record_injected");


    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject pJson, Composite pPlayer, DeserializationContext pContext) {
        return new TriggerInstance(pPlayer, ItemPredicate.fromJson(pJson.get("record")));
    }

    public void trigger(final ServerPlayer player, final ItemStack record) {
        trigger(player, (triggerInstance) ->
            triggerInstance.matches(record)
        );
    }


    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;

        public TriggerInstance(Composite pPlayer, ItemPredicate item) {
            super(ID, pPlayer);
            this.item = item;
        }

        public boolean matches(final ItemStack record) {
            return item.matches(record);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext pConditions) {
            return super.serializeToJson(pConditions);
        }
    }

}
