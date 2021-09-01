package com.YTrollman.CreativeCrafter.gui.custombutton;

import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.integration.inventorytweaks.InventoryTweaksIntegration;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class CreativeCrafterGridSortingTypeSideButton extends SideButton
{
    private final IGrid grid;

    public CreativeCrafterGridSortingTypeSideButton(BaseScreen<CreativeCrafterContainer> screen, IGrid grid) {
        super(screen);
        this.grid = grid;
    }

    public String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.grid.sorting.type") + "\n" + TextFormatting.GRAY + I18n.get("sidebutton.refinedstorage.grid.sorting.type." + this.grid.getSortingType());
    }

    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        if (this.grid.getSortingType() == 4) {
            this.screen.blit(matrixStack, x, y, 48, 48, 16, 16);
        } else {
            this.screen.blit(matrixStack, x, y, this.grid.getSortingType() * 16, 32, 16, 16);
        }

    }

    public void onPress() {
        int type = this.grid.getSortingType();
        if (type == 0) {
            type = 1;
        } else if (type == 1) {
            if (this.grid.getGridType() == GridType.FLUID) {
                type = 4;
            } else {
                type = 2;
            }
        } else if (type == 2) {
            type = 4;
        } else if (type == 4) {
            if (this.grid.getGridType() != GridType.FLUID && InventoryTweaksIntegration.isLoaded()) {
                type = 3;
            } else {
                type = 0;
            }
        } else if (type == 3) {
            type = 0;
        }

        this.grid.onSortingTypeChanged(type);
    }
}
