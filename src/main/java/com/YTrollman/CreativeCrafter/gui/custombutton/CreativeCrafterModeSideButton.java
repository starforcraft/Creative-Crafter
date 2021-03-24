package com.YTrollman.CreativeCrafter.gui.custombutton;

import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainer;
import com.YTrollman.CreativeCrafter.tileentity.CreativeCrafterTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class CreativeCrafterModeSideButton extends SideButton
{
    public CreativeCrafterModeSideButton(BaseScreen<CreativeCrafterContainer> screen)
    {
        super(screen);
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y)
    {
        this.screen.blit(matrixStack, x, y, CreativeCrafterTileEntity.MODE.getValue() * 16, 0, 16, 16);
    }

    public void onPress() {
        TileDataManager.setParameter(CreativeCrafterTileEntity.MODE, CreativeCrafterTileEntity.MODE.getValue() + 1);
    }

    @Override
    public String getTooltip()
    {
        return I18n.get("sidebutton.refinedstorage.crafter_mode", new Object[0]) + "\n" + TextFormatting.GRAY + I18n.get("sidebutton.refinedstorage.crafter_mode." + CreativeCrafterTileEntity.MODE.getValue(), new Object[0]);
    }
}
