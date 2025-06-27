package com.aszecsei.musicjs;

import com.aszecsei.musicjs.events.MusicEvents;
import com.aszecsei.musicjs.util.bindings.MusicBinding;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class MusicJSPlugin extends KubeJSPlugin {

    @Override
    public void init() {

    }

    @Override
    public void registerEvents() {
        MusicEvents.GROUP.register();
    }

    @Override
    public void afterInit() {

    }

    @Override
    public void registerBindings(BindingsEvent bindings) {
        bindings.add("Music", MusicBinding.class);
    }
}
