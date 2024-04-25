package com.cstav.evenmoreinstruments.item.emirecord;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BurnedRecordItem extends EMIRecordItem {
    public static final String BURNED_MEDIA_TAG = "BurnedMedia";

    private final ResourceLocation burnedMedia;
    private final Component title;
    private final @Nullable String info, arranger;

    public BurnedRecordItem(Properties properties, ResourceLocation burnedMedia,
                            @Nullable String info,
                            @Nullable String arranger,
                            Component title) {
        super(properties);
        this.burnedMedia = burnedMedia;
        this.info = info;
        this.arranger = arranger;
        this.title = title;
    }
    public BurnedRecordItem(Properties properties, ResourceLocation burnedMedia, @Nullable String info, @Nullable String arranger) {
        this(properties, burnedMedia, info, arranger, Component.translatable("item.evenmoreinstruments.burned_record"));
    }

    @Override
    public void onInsert(final ItemStack stack, final LooperBlockEntity lbe) {
        stack.getOrCreateTag().putString(BURNED_MEDIA_TAG, burnedMedia.toString());
    }


    @Override
    public Component getName(ItemStack pStack) {
        return title;
    }
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (info != null) {
            pTooltipComponents.add(
                Component.literal(info)
                    .withStyle(ChatFormatting.GRAY)
            );
        }
        if (arranger != null) {
            pTooltipComponents.add(
                Component.translatable("item.evenmoreinstruments.record.arranger", arranger)
                    .withStyle(ChatFormatting.GRAY)
                    .withStyle(ChatFormatting.ITALIC)
            );
        }

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
