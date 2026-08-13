package com.github.whitemo.sprfix;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(SprFixMod.MOD_ID)
public class SprFixMod {
    public static final String MOD_ID = "sprfix";

    public SprFixMod() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            SprFixConfig.register();
            MinecraftForge.EVENT_BUS.register(this);
        }
    }
}
