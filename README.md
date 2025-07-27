# MusicJS

----
MusicJS is a KubeJS plugin that allows modpack developers to modify the vanilla playback of Minecraft music.

It operates on a single **client-side** event, `MusicEvents.chooseMusic`.

> IMPORTANT:
> 
> Due to how Minecraft's music manager works, this event runs on *every tick*.
> Do not perform any intensive computation here!

An example that replaces all in-game music with non-biome-specific tracks:

```js
let $Musics = Java.loadClass('net.minecraft.sounds.Musics');
MusicEvents.chooseMusic(event => {
    if (event.playingMusic()) { return; } // Allow interrupting music to go through
    event.remove(m => m.event.get() != 'music.menu'); // Remove everything other than the menu music
    if (event.player != null) { // If we're in game...
        event.add(100, $Musics.GAME); // ...add the generic music
    }
});
```

By default, the weight provided for the vanilla-selected music is set to 100. You can adjust this with the `modifyWeights` method:

```js
let $Musics = Java.loadClass('net.minecraft.sounds.Musics');
MusicEvents.chooseMusic(event => {
    if (event.playingMusic()) { return; } // Avoid any work while music is playing
    if (event.player == null) { return; } // Don't mess with menus

    event.modifyWeights((m, w) => {
        if (m.event.get().location.toString().includes('music.nether')) {
            return w; // Leave nether biome music weight alone
        }
        return 1; // Reduce the weight of any other biome music to make it rare
    });
    event.add(100, $Musics.GAME); // Add non-biome-specific music in at a high weight
});
```

A small helper method is provided to construct `Music` objects:

```js
let pigstep = Music.of(
    'music_disc.pigstep', // sound event
    20 * 60 * 60 * 5, // minDelay
    20 * 60 * 60 * 10, // maxDelay
    false, // replaceCurrentMusic
);
```

Finally, this mod exposes the currently-playing music track:

```js
let $Minecraft = Java.loadClass("net.minecraft.client.Minecraft");
ClientEvents.leftDebugInfo(event => {
    event.lines.push(`Currently playing: ${$Minecraft.getInstance().musicManager.musicjs$getCurrentMusic()?.location ?? "[nothing]"}`)
})
```

## Adding Custom Songs

As an example, let's say we want to (rarely, as an easter egg) play a song `deep_stone_lullaby.ogg` when it's nighttime and we're in the overworld.

First, we need to add it to our `assets/kubejs/sounds/music/game` folder. Then create or edit a `assets/kubejs/sounds.json` file
to register it as a sound:

```json
{
  "music.kubejs.overworld.night": {
    "sounds": [
      {
        "name": "music/game/deep_stone_lullaby",
        "stream": true,
        "volume": 0.4
      }
    ]
  }
}
```

*Then*, in a startup script, we need to register the actual sound event:

```js
// startup_scripts/registry/sound_events.js

StartupEvents.registry('sound_event', event => {
  let music = [
    'overworld.night',
  ]
  music.forEach(key => {
    event.create(`music.kubejs.${key}`);
  });
})
```

Now that we have a sound event, we can tell MusicJS to play it under certain conditions:

```js
// client_scripts/music.js

MusicEvents.chooseMusic((event) => {
  if (event.playingMusic) {
    return;
  } // Avoid any work while music is playing
  if (event.player == null) {
    return;
  } // Don't mess with menus

  if (event.player.level.dimension == "minecraft:overworld") { // If we're in the overworld...
    if (event.player.level.night) { // ...and it's nighttime...
      event.add(
        1, // ...low (~1%) chance of playing our easter egg music instead of vanilla music.
        Music.of(
          "kubejs:music.kubejs.overworld.night",
          20 * 60 * 60 * 5,  // min delay: 5 minutes
          20 * 60 * 60 * 10, // max delay: 10 minutes
          false              // shouldn't interrupt currently-playing music
        )
      );
    }
  }
}
```

You can add or remove whatever sound events you like. For example, you might remove all other songs
from the pool and add in an interrupting boss fight track with zero delay when a wither is nearby.