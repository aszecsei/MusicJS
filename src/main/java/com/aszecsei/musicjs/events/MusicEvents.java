package com.aszecsei.musicjs.events;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface MusicEvents {
    EventGroup GROUP = EventGroup.of("MusicEvents");

    EventHandler CHOOSE_MUSIC = MusicEvents.GROUP.client("choose_music", () -> ChooseMusicEvent.class);
}
