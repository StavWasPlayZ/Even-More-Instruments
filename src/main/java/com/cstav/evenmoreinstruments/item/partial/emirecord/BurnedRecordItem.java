package com.cstav.evenmoreinstruments.item.partial.emirecord;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BurnedRecordItem extends EMIRecordItem {
    private final ResourceLocation burnedMedia;
    private final Component title;
    private final String info;

    public BurnedRecordItem(final Properties properties, final ResourceLocation burnedMedia, final String info, final Component title) {
        super(properties.stacksTo(1));
        this.burnedMedia = burnedMedia;
        this.info = info;
        this.title = title;
    }
    public BurnedRecordItem(final Properties properties, final ResourceLocation burnedMedia, final Component title) {
        this(properties, burnedMedia, null, title);
    }
    public BurnedRecordItem(final Properties properties, final ResourceLocation burnedMedia, final String info) {
        this(properties, burnedMedia, info, Component.translatable("item.evenmoreinstruments.record"));
    }

    @Override
    public void onInsert(final ItemStack stack, final LooperBlockEntity lbe) {
        stack.getOrCreateTag().putString("burned_media", burnedMedia.toString());
    }


    @Override
    public Component getName(ItemStack pStack) {
        return title;
    }
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (info != null)
            pTooltipComponents.add(Component.literal(info).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
