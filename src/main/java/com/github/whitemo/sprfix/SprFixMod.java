package com.github.whitemo.sprfix;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(SprFixMod.MOD_ID)
public class SprFixMod {
    public static final String MOD_ID = "sprfix";

    public SprFixMod() {
        // 仅客户端生效，声音逻辑全在客户端
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }
}
