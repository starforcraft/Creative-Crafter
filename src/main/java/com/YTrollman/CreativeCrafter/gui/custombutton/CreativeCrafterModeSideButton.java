package com.YTrollman.CreativeCrafter.gui.custombutton;

import com.YTrollman.CreativeCrafter.blockentity.CreativeCrafterBlockEntity;
import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainerMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class CreativeCrafterModeSideButton extends SideButton {
    public CreativeCrafterModeSideButton(BaseScreen<CreativeCrafterContainerMenu> screen) {
        super(screen);
    }

    @Override
    protected void renderButtonIcon(PoseStack poseStack, int x, int y) {
        this.screen.blit(poseStack, x, y, CreativeCrafterBlockEntity.MODE.getValue() * 16, 0, 16, 16);
    }

    public void onPress() {
        BlockEntitySynchronizationManager.setParameter(CreativeCrafterBlockEntity.MODE, CreativeCrafterBlockEntity.MODE.getValue() + 1);
    }

    @Override
    public String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.crafter_mode") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.crafter_mode." + CreativeCrafterBlockEntity.MODE.getValue());
    }
}
