package com.github.whitemo.sprfix.mixin;


import com.github.whitemo.sprfix.SprFixConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSoundInstance.class)
public abstract class AbstractSoundInstanceMixin {

    @Inject(method = "m_7780_", at = @At("RETURN"), cancellable = true, remap = false)
    private void addPlayerSoundHeightOffset(CallbackInfoReturnable<Double> cir) {
        if (!SprFixConfig.ENABLED.get()) {
            return;
        }

        SoundInstance self = (SoundInstance) this;

        if (self.getSource() != SoundSource.PLAYERS) {
            return;
        }

        String namespace = self.getLocation().getNamespace();
        for (String ns : SprFixConfig.EXCLUDED_MOD_NAMESPACES.get()) {
            if (namespace.equals(ns)) {
                return;
            }
        }

        String soundPath = self.getLocation().getPath();
        for (String pattern : SprFixConfig.EXCLUDED_SOUND_PATTERNS.get()) {
            if (soundPath.contains(pattern)) {
                return;
            }
        }

        LocalPlayer localPlayer = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if (localPlayer == null || level == null) {
            return;
        }

        double originalY = cir.getReturnValue();

        Player closest = null;
        double bestDist = 2.0D;
        for (Player player : level.players()) {
            double dist = player.distanceToSqr(self.getX(), originalY, self.getZ());
            if (dist < bestDist) {
                bestDist = dist;
                closest = player;
            }
        }

        double newY = closest != null ? closest.getEyeY() : originalY + 1.62D;
        cir.setReturnValue(newY);
    }
}