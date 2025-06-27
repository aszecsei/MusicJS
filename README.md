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