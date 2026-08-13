package com.github.whitemo.sprfix.mixin;

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

import java.util.Set;

@Mixin(AbstractSoundInstance.class)
public abstract class AbstractSoundInstanceMixin {

    private static final Set<String> EXCLUDED_SOUND_PATTERNS = Set.of(
        "step", "walk", "land", "swim", "splash", "fly"
    );

    @Inject(method = "m_7780_", at = @At("RETURN"), cancellable = true, remap = false)
    private void addPlayerSoundHeightOffset(CallbackInfoReturnable<Double> cir) {
        SoundInstance self = (SoundInstance) this;

        if (self.getSource() != SoundSource.PLAYERS) {
            return;
        }

        String soundPath = self.getLocation().getPath();
        for (String pattern : EXCLUDED_SOUND_PATTERNS) {
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

        // 在玩家列表中查找距离声音位置最近的玩家，使用 originalY 避免递归
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