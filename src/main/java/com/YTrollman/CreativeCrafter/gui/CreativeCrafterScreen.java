package com.YTrollman.CreativeCrafter.gui;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainerMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;

public class CreativeCrafterScreen extends BaseScreen<CreativeCrafterContainerMenu>
{
    public CreativeCrafterScreen(CreativeCrafterContainerMenu container, Inventory inventory, Component title)
    {
        super(container, 177, 335, inventory, title);
    }
    
    @Override
    public void onPostInit(int x, int y) {

    }

    @Override
    public void tick(int x, int y) {
    	
    }
    
    @Override
    public void renderBackground(PoseStack poseStack, int x, int y, int mouseX, int mouseY)
    {
        bindTexture(CreativeCrafter.MOD_ID, "gui/creative_crafter.png");
        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight, 512, 512);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int i, int i1)
    {
        renderString(poseStack, 7, 7, RenderUtils.shorten(title.getString(), 26));
        renderString(poseStack, 7, 241, new TranslatableComponent("container.inventory").getString());
    }
}
