package com.ultramega.creativecrafter.gui.dataparameter;

import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationClientListener;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.ultramega.creativecrafter.gui.CreativeCrafterScreen;
import com.ultramega.creativecrafter.gui.custombutton.CreativeCrafterModeSideButton;

public class CreativeCrafterTileDataParameterClientListener implements BlockEntitySynchronizationClientListener<Boolean> {
    @Override
    public void onChanged(boolean initial, Boolean hasRoot) {
        if (Boolean.FALSE.equals(hasRoot)) {
            BaseScreen.executeLater(CreativeCrafterScreen.class, (gui) -> gui.addSideButton(new CreativeCrafterModeSideButton(gui)));
        }
    }
}
