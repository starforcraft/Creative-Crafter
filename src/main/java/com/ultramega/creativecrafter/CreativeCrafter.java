package com.ultramega.creativecrafter;

import com.ultramega.creativecrafter.config.Config;
import com.ultramega.creativecrafter.registry.RegistryHandler;
import com.ultramega.creativecrafter.setup.ClientSetup;
import com.ultramega.creativecrafter.setup.CommonSetup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CreativeCrafter.MOD_ID)
public class CreativeCrafter {
    public static final String MOD_ID = "creativecrafter";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final int ROWS = 12;
    public static final int SIZE = ROWS * 9;

    public CreativeCrafter() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientSetup::new);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::onCommonSetup);

        RegistryHandler.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.common_config);
        Config.loadConfig(Config.common_config, FMLPaths.CONFIGDIR.get().resolve(CreativeCrafter.MOD_ID + "-common.toml").toString());
    }
}
