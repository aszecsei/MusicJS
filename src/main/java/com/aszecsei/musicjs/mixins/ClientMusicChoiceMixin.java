package com.aszecsei.musicjs.mixins;

import com.aszecsei.musicjs.Config;
import com.aszecsei.musicjs.MusicJS;
import com.aszecsei.musicjs.events.ChooseMusicEvent;
import com.aszecsei.musicjs.events.MusicEvents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(Minecraft.class)
public class ClientMusicChoiceMixin {
    @Shadow @Nullable public Screen screen;

    @Shadow @Nullable public LocalPlayer player;

    @Shadow @Final public Gui gui;
    @Shadow @Final private MusicManager musicManager;

    @Unique
    boolean musicjs$failedMusicChoice = false;

    @ModifyReturnValue(method = "getSituationalMusic", at = @At("RETURN"))
    private Music musicjs$musicChoice(Music original)
    {
        List<WeightedEntry.Wrapper<Music>> vanillaWantsToPlay = new ArrayList<>();
        Holder<Biome> holder = null;

        if (this.player != null) {
            holder = this.player.level().getBiome(this.player.blockPosition());
        }

        vanillaWantsToPlay.add(WeightedEntry.wrap(original, 100));

        ChooseMusicEvent event = new ChooseMusicEvent(vanillaWantsToPlay, holder);
        MusicEvents.CHOOSE_MUSIC.post(ScriptType.CLIENT, event);

        SimpleWeightedRandomList.Builder<Music> opts = new SimpleWeightedRandomList.Builder<>();
        for (WeightedEntry.Wrapper<Music> w : event.getTracks()) {
            opts.add(w.getData(), w.getWeight().asInt());
        }
        final SimpleWeightedRandomList<Music> tracks = opts.build();

        final Optional<Music> selectedMusic = tracks.getRandomValue(MusicJS.rand);
        if (selectedMusic.isEmpty()) {
            if (!musicjs$failedMusicChoice && Config.enableLogging) {
                MusicJS.LOGGER.warn("Empty music selection! Defaulting to Vanilla behavior.");
            }
            musicjs$failedMusicChoice = true;
            return original;
        }
        musicjs$failedMusicChoice = false;
        return selectedMusic.get();
    }
}
