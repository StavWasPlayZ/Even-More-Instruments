package com.cstav.evenmoreinstruments.item;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.item.partial.instrument.CreditableInstrumentItem;
import com.cstav.evenmoreinstruments.util.CommonUtil;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * An instrument that requires the usage of a {@link InstrumentAccessoryItem}
 */
@EventBusSubscriber(modid = EMIMain.MODID, bus = Bus.FORGE)
public class AccessoryInstrumentItem extends CreditableInstrumentItem {
    private final InstrumentAccessoryItem accessory;

    public AccessoryInstrumentItem(OpenInstrumentPacketSender onOpenRequest, InstrumentAccessoryItem accessory, String credit) {
        super(onOpenRequest, credit);
        this.accessory = accessory;
    }
    public AccessoryInstrumentItem(OpenInstrumentPacketSender onOpenRequest, Properties properties, InstrumentAccessoryItem accessory,
                                   String credit) {
        super(onOpenRequest, properties, credit);
        this.accessory = accessory;
    }

    public InstrumentAccessoryItem getAccessoryItem() {
        return accessory;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        final ItemStack handItem = pPlayer.getItemInHand(pUsedHand);

        if (!pPlayer.getItemInHand(CommonUtil.getOffhand(pUsedHand)).is(getAccessoryItem())) {
            if (!pLevel.isClientSide) {
                pPlayer.displayClientMessage(
                    Component.translatable(
                        "item.evenmoreinstruments.instrument.accessory.not_present",
                        getAccessoryItem().getName(new ItemStack(getAccessoryItem()))
                    ),
                    true
                );
            }

            return InteractionResultHolder.fail(handItem);
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public void onAccessoryUsed(final InstrumentPlayedEvent<?> event, final ItemStack accessory) {
        if (!accessory.isDamageableItem())
            return;
        // Shouldn't be the case, but always best to check:
        if (event.level().isClientSide)
            return;

        //TODO entities in general (fix following)
        if (!event.isByPlayer())
            return;
        final Player player = (Player) event.entityInfo().get().entity;

        accessory.hurtAndBreak(
            hurtAccessoryBy(event, accessory),
            player,
            (player.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof AccessoryInstrumentItem)
                ? EquipmentSlot.MAINHAND
                : EquipmentSlot.OFFHAND
        );
    }

    public int hurtAccessoryBy(final InstrumentPlayedEvent<?> event, final ItemStack accessory) {
        return 1;
    }


    // Call AccessoryInstrumentItem#onAccessoryUsed
    @SubscribeEvent
    public static void onInstrumentPlayedEvent(final InstrumentPlayedEvent<?> event) {
        if (event.level().isClientSide)
            return;

        //TODO make for entities in general.
        // Use getSlot.
        if (!event.isByPlayer())
            return;

        final InstrumentPlayedEvent<?>.EntityInfo entityInfo = event.entityInfo().get();
        final Player player = (Player) entityInfo.entity;

        if (!entityInfo.isItemInstrument())
            return;

        final Item instruemntItem = player.getItemInHand(entityInfo.hand.get()).getItem();
        if (!(instruemntItem instanceof AccessoryInstrumentItem aiItem))
            return;

        final ItemStack offhandStack = player.getItemInHand(CommonUtil.getOffhand(entityInfo.hand.get()));
        if (!(offhandStack.getItem() instanceof InstrumentAccessoryItem))
            return;

        if (offhandStack.is(aiItem.getAccessoryItem())) {
            aiItem.onAccessoryUsed(event, offhandStack);
        }
    }
}
