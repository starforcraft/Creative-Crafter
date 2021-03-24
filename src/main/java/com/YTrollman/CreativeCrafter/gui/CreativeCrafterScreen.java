package com.YTrollman.CreativeCrafter.gui;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.util.RenderUtils;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CreativeCrafterScreen extends BaseScreen<CreativeCrafterContainer>
{
    public CreativeCrafterScreen(CreativeCrafterContainer container, PlayerInventory inventory, ITextComponent title)
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
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
        bindTexture(CreativeCrafter.MOD_ID, "gui/creative_crafter.png");
        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight, 512, 512);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int i, int i1)
    {
        renderString(matrixStack, 7, 7, RenderUtils.shorten(title.getString(), 26));
        renderString(matrixStack, 7, 241, new TranslationTextComponent("container.inventory").getString());
    }
}
