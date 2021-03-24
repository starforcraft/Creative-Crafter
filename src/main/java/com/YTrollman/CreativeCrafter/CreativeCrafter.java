package com.YTrollman.CreativeCrafter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.YTrollman.CreativeCrafter.config.Config;
import com.YTrollman.CreativeCrafter.init.ClientEventHandler;
import com.YTrollman.CreativeCrafter.registry.ModSetup;
import com.YTrollman.CreativeCrafter.registry.RegistryHandler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod("creativecrafter")
public class CreativeCrafter {
    public static final String MOD_ID = "creativecrafter";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    
    public CreativeCrafter() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModSetup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandler::init);
        });
        
        MinecraftForge.EVENT_BUS.register(this);
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.client_config);
        
        RegistryHandler.init();
        
        Config.loadConfig(Config.client_config, FMLPaths.CONFIGDIR.get().resolve("creativecrafter-client.toml").toString());
        
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    	
    }

    private void doClientStuff(final FMLClientSetupEvent event) 
    {
    	
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) 
    {
    	
    }
}
