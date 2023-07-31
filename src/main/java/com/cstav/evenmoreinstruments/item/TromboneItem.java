package com.cstav.evenmoreinstruments.item;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.cstav.evenmoreinstruments.client.ModArmPose;
import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.ModOpenInstrumentPacket;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.item.InstrumentItem;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class TromboneItem extends InstrumentItem {

    public TromboneItem() {
        super((player, hand) -> ModPacketHandler.sendToClient(
            new ModOpenInstrumentPacket("trombone", hand), player
        ));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            
            @Override
            public @Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return (entityLiving instanceof Player player)
                    ? (InstrumentOpenProvider.isOpen(player) && InstrumentOpenProvider.isItem(player))
                        ? ModArmPose.PLAYING_TROMBONE
                        : null
                    : null;
            }

        });
    }
    
}