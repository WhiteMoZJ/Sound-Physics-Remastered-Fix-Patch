package com.github.whitemo.sprfix;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;

public class SprFixConfig {

    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_SOUND_PATTERNS;

    private static final List<String> DEFAULT_EXCLUDED = List.of(
        "step", "walk", "land", "swim", "splash", "fly", "pf"
    );

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Sound Physics Remastered Fix Patch Settings");
        builder.push("general");
        ENABLED = builder
            .comment("Enable player sound height correction. Set to false to disable the mod.")
            .define("enabled", true);
        EXCLUDED_SOUND_PATTERNS = builder
            .comment("Sound path patterns to exclude from height correction. These sounds will remain at feet level.")
            .defineList("excluded_sound_patterns", DEFAULT_EXCLUDED, o -> o instanceof String);
        builder.pop();
        CLIENT_SPEC = builder.build();
    }

    @SuppressWarnings("removal")
    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
    }
}