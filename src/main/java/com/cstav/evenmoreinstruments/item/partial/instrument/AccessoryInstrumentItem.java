package com.cstav.evenmoreinstruments.item.partial.instrument;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.util.CommonUtil;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import com.cstav.genshinstrument.util.ServerUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfig.Common;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.function.Supplier;

@EventBusSubscriber(modid = Main.MODID, bus = Bus.FORGE)
public class AccessoryInstrumentItem extends CreditableInstrumentItem {
    private final Supplier<Item> accessorySupplier;

    public AccessoryInstrumentItem(OpenInstrumentPacketSender onOpenRequest, Supplier<Item> accessorySupplier, String credit) {
        super(onOpenRequest, credit);
        this.accessorySupplier = accessorySupplier;
    }
    public AccessoryInstrumentItem(OpenInstrumentPacketSender onOpenRequest, Properties properties, Supplier<Item> accessorySupplier,
                                   String credit) {
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

    public void onAccessoryUsed(final InstrumentPlayedEvent.ByPlayer event, final ItemStack accessory) {
        if (!accessory.isDamageableItem())
            return;

        accessory.hurtAndBreak(
            hurtInstrumentBy(event, accessory),
            event.player,
            (player) -> {
                player.broadcastBreakEvent(player.getUsedItemHand());
                ServerUtil.setInstrumentClosed(player);
            }
        );
    }

    public int hurtInstrumentBy(final InstrumentPlayedEvent.ByPlayer event, final ItemStack accessory) {
        return 1;
    }


    // Call AccessoryInstrumentItem#onAccessoryUsed
    @SubscribeEvent
    public static void onInstrumentPlayedEvent(final InstrumentPlayedEvent.ByPlayer event) {
        if (event.isClientSide)
            return;

        if (!event.isItemInstrument())
            return;

        final Item instruemntItem = event.itemInstrument.get().getItem();
        if (!(instruemntItem instanceof AccessoryInstrumentItem aiItem))
            return;

        //TODO replace with event.usedHand
        final ItemStack offhandStack = event.player.getItemInHand(CommonUtil.getOffhand(InstrumentOpenProvider.getHand(event.player)));

        if (offhandStack.is(aiItem.getAccessoryItem())) {
            aiItem.onAccessoryUsed(event, offhandStack);
        }
    }
}
