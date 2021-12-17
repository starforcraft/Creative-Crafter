package com.YTrollman.CreativeCrafter.blocks;

import com.YTrollman.CreativeCrafter.blockentity.CreativeCrafterBlockEntity;
import com.YTrollman.CreativeCrafter.config.CreativeCrafterConfig;
import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainerMenu;
import com.YTrollman.CreativeCrafter.util.TooltipBuilder;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.block.BlockDirection;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.container.factory.BlockEntityMenuProvider;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new CreativeCrafterBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level levelIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.setPlacedBy(levelIn, pos, state, placer, stack);
        if (!levelIn.isClientSide)
        {
            BlockEntity tile = levelIn.getBlockEntity(pos);

            if (tile instanceof CreativeCrafterBlockEntity && stack.hasCustomHoverName()) {
                ((CreativeCrafterBlockEntity) tile).getNode().setDisplayName(stack.getHoverName());
                ((CreativeCrafterBlockEntity) tile).getNode().markDirty();
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (!levelIn.isClientSide)
        {
            return NetworkUtils.attempt(levelIn, pos, player, () -> NetworkHooks.openGui(
                    (ServerPlayer) player,
                    new BlockEntityMenuProvider<CreativeCrafterBlockEntity>(
                            ((CreativeCrafterBlockEntity) levelIn.getBlockEntity(pos)).getNode().getName(),
                            (tile, windowId, inventory, p) -> new CreativeCrafterContainerMenu(windowId, player, tile),
                            pos
                    ),
                    pos
            ), Permission.MODIFY, Permission.AUTOCRAFTING);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hasConnectedState()
    {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        if(Screen.hasShiftDown())
        {
            tooltip.addAll(new TooltipBuilder()
            .addTip(I18n.get("tooltip.creativecrafter.speed") + CreativeCrafterConfig.CREATIVE_CRAFTER_SPEED.get() + " blocks per tick", ChatFormatting.AQUA)
            .addTip(I18n.get("tooltip.creativecrafter.slots"), ChatFormatting.AQUA)
            .addTip(I18n.get("tooltip.creativecrafter.rfconsume") + CreativeCrafterConfig.CREATIVE_CRAFTER_RF_CONSUME.get() + " RF", ChatFormatting.AQUA)
            .build());
        }
        else
        {
            tooltip.add(new TranslatableComponent("tooltip.creativecrafter.hold_shift").withStyle(ChatFormatting.YELLOW));
        }
    }
}
