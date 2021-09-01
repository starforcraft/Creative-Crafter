package com.YTrollman.CreativeCrafter.gui.custombutton;

import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class CreativeCrafterGridViewTypeSideButton extends SideButton {
    private final IGrid grid;

    public CreativeCrafterGridViewTypeSideButton(BaseScreen<CreativeCrafterContainer> screen, IGrid grid) {
        super(screen);
        this.grid = grid;
    }

    public String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.grid.view_type") + "\n" + TextFormatting.GRAY + I18n.get("sidebutton.refinedstorage.grid.view_type." + this.grid.getViewType());
    }

    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        this.screen.blit(matrixStack, x, y, (this.grid.getViewType() - (this.grid.getViewType() >= 3 ? 3 : 0)) * 16, 112, 16, 16);
    }

    public void onPress() {
        int type = this.grid.getViewType();
        if (type == 0) {
            type = 1;
        } else if (type == 1) {
            type = 2;
        } else if (type == 2) {
            type = 0;
        }

        this.grid.onViewTypeChanged(type);
    }
}
