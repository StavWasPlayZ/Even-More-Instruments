package com.cstav.evenmoreinstruments.item.partial;

import com.cstav.evenmoreinstruments.client.ModArmPose;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class WindInstrumentItem extends CreditableInstrumentItem {

    public WindInstrumentItem(OpenInstrumentPacketSender onOpenRequest, String credit) {
        super(onOpenRequest, credit);
    }
    public WindInstrumentItem(OpenInstrumentPacketSender onOpenRequest, Properties properties, String credit) {
        super(onOpenRequest, properties, credit);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {

            @Override
            public @Nullable HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
            return (entityLiving instanceof Player player)
                ? (InstrumentOpenProvider.isOpen(player) && InstrumentOpenProvider.isItem(player))
                    ? ModArmPose.PLAYING_WIND_INSTRUMENT
                    : null
                : null;
            }

        });
    }

}
