package com.aszecsei.musicjs.util.bindings;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;

@SuppressWarnings("unused")
@Info("Various music-related helper methods")
public class MusicBinding {
    @Info("Returns a Music class, with the specified SoundEvent, minimum delay, maximum delay, and whether or not it should override already-playing music tracks")
    public static Music of(SoundEvent sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
        return new Music(Holder.direct(sound), minDelay, maxDelay, replaceCurrentMusic);
    }
}
