package com.ultramega.creativecrafter.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CreativeCrafterConfig {
    public static ForgeConfigSpec.IntValue CREATIVE_CRAFTER_RF_CONSUME;

    public static void init(ForgeConfigSpec.Builder common) {
        common.push("Creative Crafter Options");
            CREATIVE_CRAFTER_RF_CONSUME = common
                    .comment("\nThe energy used by the Creative Crafter")
                    .defineInRange("creativeCrafterEnergyUsage", 0, 0, Integer.MAX_VALUE);
        common.pop();

        common.build();
    }
}
