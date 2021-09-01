package com.YTrollman.CreativeCrafter;

import com.YTrollman.CreativeCrafter.blocks.CreativeCrafterBlockFactory;
import com.YTrollman.CreativeCrafter.config.Config;
import com.YTrollman.CreativeCrafter.init.ClientEventHandler;
import com.YTrollman.CreativeCrafter.registry.ModSetup;
import com.YTrollman.CreativeCrafter.registry.RegistryHandler;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("creativecrafter")
public class CreativeCrafter {
    public static final String MOD_ID = "creativecrafter";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public CreativeCrafter() {
        RegistryHandler.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.common_config);

        ModSetup modSetup = new ModSetup();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(modSetup::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandler::init);
        });

        Config.loadConfig(Config.common_config, FMLPaths.CONFIGDIR.get().resolve("creativecrafter-common.toml").toString());
    }

    public void setup(FMLCommonSetupEvent event) {
        API.instance().getGridManager().add(CreativeCrafterBlockFactory.ID, new CreativeCrafterBlockFactory());
    }
}
