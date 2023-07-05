package com.ultramega.creativecrafter.registry;

import com.ultramega.creativecrafter.CreativeCrafter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreativeCrafter.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB_CREATIVECRAFTER = TABS.register(CreativeCrafter.MOD_ID, () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.creativecrafter")).icon(() -> new ItemStack(ModItems.CREATIVE_CRAFTER.get())).displayItems((featureFlags, output) -> {
        output.accept(new ItemStack(ModItems.CREATIVE_CRAFTER.get()));
    }).build());
}