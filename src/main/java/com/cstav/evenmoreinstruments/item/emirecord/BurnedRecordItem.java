package com.cstav.evenmoreinstruments.item.emirecord;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.item.component.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BurnedRecordItem extends EMIRecordItem {
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
        stack.set(ModDataComponents.BURNED_MEDIA.get(), burnedMedia);
    }


    @Override
    public Component getName(ItemStack pStack) {
        return title;
    }
    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
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

        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }
}
