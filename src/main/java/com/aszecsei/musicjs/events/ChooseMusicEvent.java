package com.aszecsei.musicjs.events;

import com.aszecsei.musicjs.mixins.MusicManagerMixin;
import dev.latvian.mods.kubejs.client.ClientEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class ChooseMusicEvent extends ClientEventJS {
    private final List<WeightedEntry.Wrapper<Music>> _tracks;
    private final Holder<Biome> _biome;

    @FunctionalInterface
    public interface WeightModificationCallback {
        int updateWeight(Music track, int weight);
    }

    public ChooseMusicEvent(List<WeightedEntry.Wrapper<Music>> vanillaWantsToPlay, Holder<Biome> biome) {
        _tracks = vanillaWantsToPlay;
        _biome = biome;
    }
    public List<WeightedEntry.Wrapper<Music>> getTracks() {
        return _tracks;
    }
    public Holder<Biome> getBiome() {
        return _biome;
    }

    @Info("Set the weights for each piece of music that can be played")
    public void modifyWeights(WeightModificationCallback callback) {
        for (int i = 0; i < _tracks.size(); i++) {
            WeightedEntry.Wrapper<Music> track = _tracks.get(i);
            _tracks.set(i, WeightedEntry.wrap(track.getData(), callback.updateWeight(track.getData(), track.getWeight().asInt())));
        }
    }

    @Info("Prevent any music that matches the given predicate from playing")
    public void remove(Predicate<Music> predicate) {
        _tracks.removeIf(x -> predicate.test(x.getData()));
    }

    @Info("Add music with a weighted chance of being selected")
    public void add(int weight, Music... musics) {
        for (Music music : musics) {
            _tracks.add(WeightedEntry.wrap(music, weight));
        }
    }

    @Info("Get the current active music instance")
    public SoundInstance currentMusic() {
        return ((MusicManagerMixin)Minecraft.getInstance().getMusicManager()).musicjs$getCurrentMusic();
    }

    @Info("Whether or not music is currently playing")
    public boolean isPlayingMusic() {
        return Minecraft.getInstance().getSoundManager().isActive(currentMusic());
    }
}
