package com.ultramega.creativecrafter.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CreativeCrafterConfig {
    public static ForgeConfigSpec.IntValue CREATIVE_CRAFTER_RF_CONSUME;

    public static void init(ForgeConfigSpec.Builder common) {
        common.push("Creative Crafter Options");
        CREATIVE_CRAFTER_RF_CONSUME = common
                .comment("\nCreative Crafter RF Consume")
                .defineInRange("creativecrafterrfconsume", 0, 0, 2147483647);
        common.pop();

        common.build();
    }
}
