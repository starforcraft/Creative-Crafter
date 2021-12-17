package com.YTrollman.CreativeCrafter.gui.dataparameter;

import com.YTrollman.CreativeCrafter.gui.CreativeCrafterScreen;
import com.YTrollman.CreativeCrafter.gui.custombutton.CreativeCrafterModeSideButton;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationClientListener;
import com.refinedmods.refinedstorage.screen.BaseScreen;

public class CreativeCrafterTileDataParameterClientListener implements BlockEntitySynchronizationClientListener<Boolean>
{
    @Override
    public void onChanged(boolean initial, Boolean hasRoot)
    {
        if (!hasRoot)
        {
            BaseScreen.executeLater(CreativeCrafterScreen.class, (gui) ->
            {
                gui.addSideButton(new CreativeCrafterModeSideButton(gui));
            });
        }
    }
}
