package com.YTrollman.CreativeCrafter.blocks;

import com.YTrollman.CreativeCrafter.config.CreativeCrafterConfig;
import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainer;
import com.YTrollman.CreativeCrafter.tileentity.CreativeCrafterTileEntity;
import com.YTrollman.CreativeCrafter.util.TooltipBuilder;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.block.BlockDirection;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class CreativeCrafterBlock extends NetworkNodeBlock
{
    public CreativeCrafterBlock()
    {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    @Override
    public BlockDirection getDirection()
    {
        return BlockDirection.ANY_FACE_PLAYER;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new CreativeCrafterTileEntity();
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (!worldIn.isClientSide)
        {
            TileEntity tile = worldIn.getBlockEntity(pos);

            if (tile instanceof CreativeCrafterTileEntity && stack.hasCustomHoverName()) {
                ((CreativeCrafterTileEntity) tile).getNode().setDisplayName(stack.getHoverName());
                ((CreativeCrafterTileEntity) tile).getNode().markDirty();
            }
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (!worldIn.isClientSide)
        {
            return NetworkUtils.attempt(worldIn, pos, player, () -> NetworkHooks.openGui(
                    (ServerPlayerEntity) player,
                    new PositionalTileContainerProvider<CreativeCrafterTileEntity>(
                            ((CreativeCrafterTileEntity) worldIn.getBlockEntity(pos)).getNode().getName(),
                            (tile, windowId, inventory, p) -> new CreativeCrafterContainer(tile.getNode(), windowId, player, tile),
                            pos
                    ),
                    pos
            ), Permission.MODIFY, Permission.AUTOCRAFTING);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasConnectedState()
    {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if(Screen.hasShiftDown())
        {
            tooltip.addAll(new TooltipBuilder()
            .addTip(I18n.get("tooltip.creativecrafter.speed") + CreativeCrafterConfig.CREATIVE_CRAFTER_SPEED.get() + " blocks per tick", TextFormatting.AQUA)
            .addTip(I18n.get("tooltip.creativecrafter.slots"), TextFormatting.AQUA)
            .addTip(I18n.get("tooltip.creativecrafter.rfconsume") + CreativeCrafterConfig.CREATIVE_CRAFTER_RF_CONSUME.get() + " RF", TextFormatting.AQUA)
            .build());
        }
        else
        {
            tooltip.add(new TranslationTextComponent("tooltip.creativecrafter.hold_shift").withStyle(TextFormatting.YELLOW));
        }
    }
}
