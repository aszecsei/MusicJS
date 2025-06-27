package com.aszecsei.musicjs.mixins;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(MusicManager.class)
public interface MusicManagerMixin {
    @Accessor("currentMusic")
    public SoundInstance musicjs$getCurrentMusic();
}
