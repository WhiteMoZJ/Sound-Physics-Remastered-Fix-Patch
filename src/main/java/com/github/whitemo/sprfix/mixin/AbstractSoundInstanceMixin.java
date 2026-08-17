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

    @Inject(method = "m_7772_", at = @At("HEAD"), cancellable = true, remap = false)
    private void fixSoundX(CallbackInfoReturnable<Double> cir) {
        Player player = resolvePlayer();
        if (player == null) return;
        cir.setReturnValue(player.getX());
    }

    @Inject(method = "m_7780_", at = @At("HEAD"), cancellable = true, remap = false)
    private void fixSoundY(CallbackInfoReturnable<Double> cir) {
        Player player = resolvePlayer();
        if (player == null) return;
        cir.setReturnValue(player.getEyeY());
    }

    @Inject(method = "m_7778_", at = @At("HEAD"), cancellable = true, remap = false)
    private void fixSoundZ(CallbackInfoReturnable<Double> cir) {
        Player player = resolvePlayer();
        if (player == null) return;
        cir.setReturnValue(player.getZ());
    }

    private Player resolvePlayer() {
        if (!SprFixConfig.ENABLED.get()) {
            return null;
        }

        SoundInstance self = (SoundInstance) this;

        if (self.getSource() != SoundSource.PLAYERS) {
            return null;
        }

        String namespace = self.getLocation().getNamespace();
        for (String ns : SprFixConfig.EXCLUDED_MOD_NAMESPACES.get()) {
            if (namespace.equals(ns)) {
                return null;
            }
        }

        String soundPath = self.getLocation().getPath();
        for (String pattern : SprFixConfig.EXCLUDED_SOUND_PATTERNS.get()) {
            if (soundPath.contains(pattern)) {
                return null;
            }
        }

        LocalPlayer localPlayer = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if (localPlayer == null || level == null) {
            return null;
        }

        AbstractSoundInstanceAccessor acc = (AbstractSoundInstanceAccessor) this;
        double rawX = acc.getRawX();
        double rawY = acc.getRawY();
        double rawZ = acc.getRawZ();

        Player closest = null;
        double bestDist = 2.0D;
        for (Player player : level.players()) {
            double dist = player.distanceToSqr(rawX, rawY, rawZ);
            if (dist < bestDist) {
                bestDist = dist;
                closest = player;
            }
        }

        return closest;
    }
}