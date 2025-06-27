package com.aszecsei.musicjs.mixins;

import com.aszecsei.musicjs.Config;
import com.aszecsei.musicjs.MusicJS;
import com.aszecsei.musicjs.events.ChooseMusicEvent;
import com.aszecsei.musicjs.events.MusicEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void musicjs$musicChoice(final CallbackInfoReturnable<Music> cir)
    {
        List<WeightedEntry.Wrapper<Music>> vanillaWantsToPlay = new ArrayList<>();
        Holder<Biome> holder = null;

        Music music = Optionull.map(this.screen, Screen::getBackgroundMusic);
        if (music != null) {
            cir.setReturnValue(music);
            return;
        }

        // Standard vanilla behavior, but added to weighted random list
        if (this.player == null) {
            vanillaWantsToPlay.add(WeightedEntry.wrap(Musics.MENU, 100));
        } else {
            holder = this.player.level().getBiome(this.player.blockPosition());
            if (this.player.level().dimension() == Level.END) {
                vanillaWantsToPlay.add(WeightedEntry.wrap(this.gui.getBossOverlay().shouldPlayMusic() ? Musics.END_BOSS : Musics.END, 100));
            } else {
                if (!this.musicManager.isPlayingMusic(Musics.UNDER_WATER) && (!this.player.isUnderWater() || !holder.is(BiomeTags.PLAYS_UNDERWATER_MUSIC))) {
                    vanillaWantsToPlay.add(WeightedEntry.wrap(this.player.level().dimension() != Level.NETHER && this.player.getAbilities().instabuild && this.player.getAbilities().mayfly ? Musics.CREATIVE : holder.value().getBackgroundMusic().orElse(Musics.GAME), 100));
                } else {
                    vanillaWantsToPlay.add(WeightedEntry.wrap(Musics.UNDER_WATER, 100));
                }
            }
        }

        ChooseMusicEvent event = new ChooseMusicEvent(vanillaWantsToPlay, holder);
        MusicEvents.CHOOSE_MUSIC.post(ScriptType.CLIENT, event);

        SimpleWeightedRandomList.Builder<Music> opts = new SimpleWeightedRandomList.Builder<>();
        for (WeightedEntry.Wrapper<Music> w : event.getTracks()) {
            opts.add(w.getData(), w.getWeight().asInt());
        }
        final SimpleWeightedRandomList<Music> tracks = opts.build();
        if (tracks.isEmpty()) {
            return;
        }

        final Optional<Music> selectedMusic = tracks.getRandomValue(MusicJS.rand);
        selectedMusic.ifPresentOrElse(cir::setReturnValue, () -> cir.setReturnValue(Musics.MENU));
    }
}
