package com.ultramega.creativecrafter.gui;

import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.util.RenderUtils;
import com.ultramega.creativecrafter.CreativeCrafter;
import com.ultramega.creativecrafter.container.CreativeCrafterContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CreativeCrafterScreen extends BaseScreen<CreativeCrafterContainerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CreativeCrafter.MOD_ID, "textures/gui/creative_crafter.png");

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
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 512, 512);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int i, int i1) {
        renderString(graphics, 7, 7, RenderUtils.shorten(title.getString(), 26));
        renderString(graphics, 7, 241, I18n.get("container.inventory"));
    }
}
