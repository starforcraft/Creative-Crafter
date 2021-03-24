package com.YTrollman.CreativeCrafter.gui.dataparameter;

import com.YTrollman.CreativeCrafter.gui.CreativeCrafterScreen;
import com.YTrollman.CreativeCrafter.gui.custombutton.CreativeCrafterModeSideButton;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.data.TileDataParameterClientListener;

public class CreativeCrafterTileDataParameterClientListener implements TileDataParameterClientListener<Boolean>
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
