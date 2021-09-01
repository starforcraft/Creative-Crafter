package com.YTrollman.CreativeCrafter.gui.custombutton;

import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class CreativeCrafterGridSortingDirectionSideButton extends SideButton
{
    private final IGrid grid;

    public CreativeCrafterGridSortingDirectionSideButton(BaseScreen<CreativeCrafterContainer> screen, IGrid grid) {
        super(screen);
        this.grid = grid;
    }

    public String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.grid.sorting.direction") + "\n" + TextFormatting.GRAY + I18n.get("sidebutton.refinedstorage.grid.sorting.direction." + this.grid.getSortingDirection());
    }

    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        this.screen.blit(matrixStack, x, y, this.grid.getSortingDirection() * 16, 16, 16, 16);
    }

    public void onPress() {
        int dir = this.grid.getSortingDirection();
        if (dir == 0) {
            dir = 1;
        } else if (dir == 1) {
            dir = 0;
        }

        this.grid.onSortingDirectionChanged(dir);
    }
}
