package com.ultramega.creativecrafter.blocks;

import com.ultramega.creativecrafter.blockentity.CreativeCrafterBlockEntity;
import com.ultramega.creativecrafter.config.CreativeCrafterConfig;
import com.ultramega.creativecrafter.container.CreativeCrafterContainerMenu;
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

public class CreativeCrafterBlock extends NetworkNodeBlock {
    public CreativeCrafterBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeCrafterBlockEntity(pos, state);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.creativecrafter.slots").withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("tooltip.creativecrafter.rfconsume", CreativeCrafterConfig.CREATIVE_CRAFTER_RF_CONSUME.get()).withStyle(ChatFormatting.AQUA));
        } else {
            tooltip.add(Component.translatable("tooltip.creativecrafter.hold_shift").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public void setPlacedBy(Level levelIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(levelIn, pos, state, placer, stack);
        if(!levelIn.isClientSide) {
            BlockEntity tile = levelIn.getBlockEntity(pos);

            if(tile instanceof CreativeCrafterBlockEntity && stack.hasCustomHoverName()) {
                ((CreativeCrafterBlockEntity) tile).getNode().setDisplayName(stack.getHoverName());
                ((CreativeCrafterBlockEntity) tile).getNode().markDirty();
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(!levelIn.isClientSide) {
            return NetworkUtils.attempt(levelIn, pos, player, () -> NetworkHooks.openScreen(
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
    public BlockDirection getDirection() {
        return BlockDirection.ANY_FACE_PLAYER;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
