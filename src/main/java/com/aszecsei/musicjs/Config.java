package com.aszecsei.musicjs;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MusicJS.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ENABLE_LOGGING = BUILDER
            .comment("Whether to log the music choice process")
            .define("enableLogging", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean enableLogging;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        enableLogging = ENABLE_LOGGING.get();
    }
}
