package com.github.whitemo.sprfix.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(AbstractSoundInstance.class)
public abstract class AbstractSoundInstanceMixin {

    // 玩家发声高度偏移：原版玩家站立眼高约1.62格，对应嘴部位置
    private static final double HEIGHT_OFFSET = 1.62D;

    // 脚步声、落地声、游泳声等移动音效应保留在脚部位置，不进行偏移
    private static final Set<String> EXCLUDED_SOUND_PATTERNS = Set.of(
        "step", "walk", "land", "swim", "splash", "fly"
    );

    @Inject(method = "m_7780_", at = @At("RETURN"), cancellable = true, remap = false)
    private void addPlayerSoundHeightOffset(CallbackInfoReturnable<Double> cir) {
        SoundInstance self = (SoundInstance) this;

        if (self.getSource() != SoundSource.PLAYERS) {
            return;
        }

        // 仅处理本地玩家的声音，远程玩家的声音已由服务端 EntityMixin 修正
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        double dx = self.getX() - player.getX();
        double dz = self.getZ() - player.getZ();
        if (dx * dx + dz * dz > 0.25D) {
            return; // 声音位置与本地玩家不符，跳过
        }

        String soundPath = self.getLocation().getPath();
        for (String pattern : EXCLUDED_SOUND_PATTERNS) {
            if (soundPath.contains(pattern)) {
                return;
            }
        }

        // 趴下（爬行）时玩家头部贴近地面，无需进行高度偏移
        if (player.getPose() == Pose.SWIMMING || player.getPose() == Pose.SITTING) {
            return;
        }
        double originalY = cir.getReturnValue();
        double newY = originalY + HEIGHT_OFFSET;

        if (player.getPose() == Pose.CROUCHING) newY -= 0.5;
        if (player.getPose() == Pose.SITTING) newY -= 1.0;

        cir.setReturnValue(newY);
    }
}