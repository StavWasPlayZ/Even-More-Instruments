package com.cstav.evenmoreinstruments.client.gui.options;

import java.awt.Color;

import com.cstav.evenmoreinstruments.client.ModClientConfigs;
import com.cstav.evenmoreinstruments.client.gui.instrument.violin.ViolinScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.violin.ViolinSoundType;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.GridInstrumentOptionsScreen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.Component;

// Lazily copied from Zither
public class ViolinOptionsScreen extends GridInstrumentOptionsScreen {
    private static final String SOUND_TYPE_KEY = "button.evemoreinstruments.violin.soundType";
    private final static int SPACE_BEFORE = 30, SPACER_HEIGHT = 13;

    public ViolinOptionsScreen(AbstractGridInstrumentScreen screen) {
        super(screen);
    }
    
    private ViolinSoundType perferredSoundType = ModClientConfigs.VIOLIN_SOUND_TYPE.get();
    public ViolinSoundType getPerferredSoundType() {
        return perferredSoundType;
    }


    private int heightBefore;

    @Override
    protected void initOptionsGrid(GridLayout grid, RowHelper rowHelper) {
        super.initOptionsGrid(grid, rowHelper);
        
        rowHelper.addChild(SpacerElement.height(SPACER_HEIGHT), 2);
        grid.arrangeElements();
        heightBefore = grid.getHeight();

        final CycleButton<ViolinSoundType> soundTypeButton = CycleButton.<ViolinSoundType>builder((type) ->
            Component.translatable(SOUND_TYPE_KEY+"."+type.toString().toLowerCase())
        )
            .withValues(ViolinSoundType.values())
            .withInitialValue(getPerferredSoundType())
            .create(0, 0,
                getBigButtonWidth(), getButtonHeight()
            , Component.translatable(SOUND_TYPE_KEY), this::onSoundTypeChange);

        rowHelper.addChild(soundTypeButton, 2);
    }

    @Override
    public void render(GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(gui, pMouseX, pMouseY, pPartialTick);
        
        gui.drawCenteredString(font,
            Component.translatable("label.evenmoreinstruments.violin_options"),
            width/2, heightBefore + SPACE_BEFORE
        , Color.WHITE.getRGB());
    }

    private void onSoundTypeChange(final CycleButton<ViolinSoundType> btn, final ViolinSoundType soundType) {
        if ((instrumentScreen != null) && (instrumentScreen instanceof ViolinScreen))
            ((ViolinScreen)instrumentScreen).noteGrid.setNoteSounds(soundType.soundArr().get());

        queueToSave("violin_sound_type", () -> ModClientConfigs.VIOLIN_SOUND_TYPE.set(soundType));
    }

}
