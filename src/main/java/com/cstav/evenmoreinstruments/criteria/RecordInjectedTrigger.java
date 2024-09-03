package com.cstav.evenmoreinstruments.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class RecordInjectedTrigger extends SimpleCriterionTrigger<RecordInjectedTrigger.TriggerInstance> {
    public void trigger(final ServerPlayer player, final ItemStack instrument) {
        super.trigger(player, (triggerInstance) ->
            triggerInstance.matches(instrument)
        );
    }

    @Override
    public Codec<RecordInjectedTrigger.TriggerInstance> codec() {
        return RecordInjectedTrigger.TriggerInstance.CODEC;
    }


    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<RecordInjectedTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create((triggerInstance) ->
            triggerInstance.group(
                ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player")
                    .forGetter(RecordInjectedTrigger.TriggerInstance::player),
                ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "record")
                    .forGetter(RecordInjectedTrigger.TriggerInstance::item)
            ).apply(triggerInstance, RecordInjectedTrigger.TriggerInstance::new)
        );

        public boolean matches(final ItemStack record) {
            return item.isEmpty() || item.get().matches(record);
        }
    }

}