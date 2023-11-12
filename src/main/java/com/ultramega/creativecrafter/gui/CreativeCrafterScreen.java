package com.ultramega.creativecrafter.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.util.RenderUtils;
import com.ultramega.creativecrafter.CreativeCrafter;
import com.ultramega.creativecrafter.container.CreativeCrafterContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CreativeCrafterScreen extends BaseScreen<CreativeCrafterContainerMenu> {
    public CreativeCrafterScreen(CreativeCrafterContainerMenu container, Inventory inventory, Component title) {
        super(container, 177, 335, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {

    }

    @Override
    public void tick(int x, int y) {

    }

    @Override
    public void renderBackground(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(CreativeCrafter.MOD_ID, "gui/creative_crafter.png");
        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight, 512, 512);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int i, int i1) {
        renderString(poseStack, 7, 7, RenderUtils.shorten(title.getString(), 26));
        renderString(poseStack, 7, 241, Component.translatable("container.inventory").getString());
    }
}
