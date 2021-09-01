package com.YTrollman.CreativeCrafter.gui.custombutton;

import com.YTrollman.CreativeCrafter.gui.CreativeCrafterScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SearchBoxModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;

public class CreativeCrafterGridSearchBoxModeSideButton extends SearchBoxModeSideButton
{
    public CreativeCrafterGridSearchBoxModeSideButton(CreativeCrafterScreen screen) {
        super(screen);
    }

    protected int getSearchBoxMode() {
        return ((CreativeCrafterScreen)this.screen).getGrid().getSearchBoxMode();
    }

    protected void setSearchBoxMode(int mode) {
        ((CreativeCrafterScreen)this.screen).getGrid().onSearchBoxModeChanged(mode);
        ((CreativeCrafterScreen)this.screen).getSearchField().setMode(mode);
    }
}

