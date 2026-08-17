package com.github.whitemo.sprfix.mixin;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSoundInstance.class)
public interface AbstractSoundInstanceAccessor {

    @Accessor("f_119575_")
    double getRawX();

    @Accessor("f_119576_")
    double getRawY();

    @Accessor("f_119577_")
    double getRawZ();
}