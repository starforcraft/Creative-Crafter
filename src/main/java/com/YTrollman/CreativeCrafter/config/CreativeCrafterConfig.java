package com.YTrollman.CreativeCrafter.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CreativeCrafterConfig {

    public static ForgeConfigSpec.IntValue CREATIVE_CRAFTER_RF_CONSUME;
    public static ForgeConfigSpec.IntValue CREATIVE_CRAFTER_SPEED;

    public static void init(ForgeConfigSpec.Builder builder) {

        builder.comment("Creative Crafter Options");
            
            CREATIVE_CRAFTER_RF_CONSUME = builder
            		.comment("\nCreative Crafter RF Consume")
                    .defineInRange("creativecrafterrfconsume", 0, 0, 2147483647);
            CREATIVE_CRAFTER_SPEED = builder
            		.comment("\nCreative Crafter Speed \nIf this Value is too high and you try to craft like 10.000.000 items the Game will lag :)")
                    .defineInRange("creativecrafterspeed", 60000, 0, 2147483647);
    }
}
