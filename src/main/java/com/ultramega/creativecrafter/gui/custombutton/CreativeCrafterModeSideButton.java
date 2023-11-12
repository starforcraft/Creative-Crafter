package com.ultramega.creativecrafter.gui.custombutton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.ultramega.creativecrafter.blockentity.CreativeCrafterBlockEntity;
import com.ultramega.creativecrafter.container.CreativeCrafterContainerMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class CreativeCrafterModeSideButton extends SideButton {
    public CreativeCrafterModeSideButton(BaseScreen<CreativeCrafterContainerMenu> screen) {
        super(screen);
    }

    @Override
    public String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.crafter_mode") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.crafter_mode." + CreativeCrafterBlockEntity.MODE.getValue());
    }

    @Override
    protected void renderButtonIcon(PoseStack poseStack, int x, int y) {
        this.screen.blit(poseStack, x, y, CreativeCrafterBlockEntity.MODE.getValue() * 16, 0, 16, 16);
    }

    public void onPress() {
        BlockEntitySynchronizationManager.setParameter(CreativeCrafterBlockEntity.MODE, CreativeCrafterBlockEntity.MODE.getValue() + 1);
    }
}
