package com.cstav.evenmoreinstruments.item.partial.instrument;

import com.cstav.evenmoreinstruments.util.CommonUtil;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class AccessoryInstrumentItem extends CreditableInstrumentItem {
    private final Supplier<Item> accessorySupplier;

    public AccessoryInstrumentItem(OpenInstrumentPacketSender onOpenRequest, Supplier<Item> accessorySupplier, String credit) {
        super(onOpenRequest, credit);
        this.accessorySupplier = accessorySupplier;
    }
    public AccessoryInstrumentItem(OpenInstrumentPacketSender onOpenRequest, Properties properties, Supplier<Item> accessorySupplier, String credit) {
        super(onOpenRequest, properties, credit);
        this.accessorySupplier = accessorySupplier;
    }

    public Item getAccessoryItem() {
        return accessorySupplier.get();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        final ItemStack handItem = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide)
            return InteractionResultHolder.success(handItem);

        if (!pPlayer.getItemInHand(CommonUtil.getOffhand(pUsedHand)).is(getAccessoryItem())) {
            pPlayer.displayClientMessage(
                Component.translatable(
                    "item.evenmoreinstruments.instrument.accessory.not_present",
                    getAccessoryItem().getName(new ItemStack(getAccessoryItem()))
                ),
                true
            );

            return InteractionResultHolder.fail(handItem);
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
