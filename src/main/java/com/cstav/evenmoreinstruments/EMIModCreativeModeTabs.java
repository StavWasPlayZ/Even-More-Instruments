package com.cstav.evenmoreinstruments;

import com.cstav.evenmoreinstruments.item.ModItems;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class EMIModCreativeModeTabs {

    public static final CreativeModeTab instrumentAccessoryTab = new CreativeModeTab("evenmoreinstruments.instrument_accessories") {

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.LOOPER.get());
        }
        
    };

}
