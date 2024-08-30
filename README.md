# Hats
![Hats.png](assets/Hats.png)

## What's Hats?
**Hats** is a Minecraft plugin designed for servers running version b1.7.3.
It allows players to wear blocks in the helmet slot as hats and optionally emit light from these hats, depending on the configured blocks.
The plugin also includes sound and visual effects to enhance the experience of wearing and interacting with hats.

### Features
- Wear any block as a hat. ([Block IDs 1-96](assets/Items.png))
- Configure which hats will emit light around the player, visible to everyone.
- Modify the radius and intensity of the emitted light.
- Enjoy sound and visual effects when wearing hats or switching the light on/off.
- Ability to disable certain features.
- **Side features:**
  - **Condition checks:** No block held in hand, Item cannot be worn as a hat, Held block is already worn as a hat, Swap between new and old hat, Dropping the old hat on the ground when a new one is equipped with a full inventory.
  - Ensures only one unit of a block is worn in the helmet slot, preventing players from using it as an additional inventory slot.
  - Checks for a newer version of the plugin and provides a download link in the console if available.

### Download
Latest releases of **Hats** can be found here on [GitHub](https://github.com/AleksandarHaralanov/Hats/releases).<br>
Alternatively, you can also download through [Modrinth](https://modrinth.com/plugin/hats-b1.7.3/versions).

The plugin is fully open-source and transparent. If you'd like additional peace of mind, you're welcome to scan the `.jar` file using [VirusTotal](https://www.virustotal.com/gui/home/upload).

### Requirements
Your b1.7.3 server must be running one of the following APIs: CB1000-CB1092, [Project Poseidon](https://github.com/RhysB/Project-Poseidon) or [UberBukkit](https://github.com/Moresteck/Project-Poseidon-Uberbukkit).

### Usage
By default, only OPs have permission.

Use PermissionsEx or similar plugins to grant groups the permission, enabling the commands.

#### Commands:
  - `/hat` - `hats.wear` - Wear the block in hand as a hat.
  - `/hat effects` - `hats.wear` - Toggle personal effects.
  - `/hat light` - `hats.light` - Toggle personal hat light.
  - `/hat light view` - View hat light source blocks.
  - `/hat about` - See Hats' information.
  - `/hat settings <args...>` - `hats.settings` - Manage Hats' settings. (Staff):
    - `reload` - Reload Hats' configuration.
    - `toggle <hats | effects | light>` - Toggle features.
    - `alter <radius | level>` - Alter hat light behavior.
    - `<add | remove> <1-96>` - Modify hat light source blocks.

#### Permissions:
##### Single permissions:
  - `hats.wear` - Allows the player to wear blocks as hats.
  - `hats.light` - Allows specific hats to emit light and be toggled by the player when worn.
  - `hats.settings` - Allows the player to reload and change the config.
##### Wildcard permissions:
  - `hats.*` - Wildcard permission granting everything.
  - `hats.perks` - Grants `hats.wear` and `hats.light`.

#### Configuration
Automatically generates configuration files `config.yml` and `players.yml` located at `plugins/Hats/`.

##### Default `config.yml`:
```yaml
hats:
    toggle: true
    effects: true
    light:
        toggle: true
        radius: NARROW
        level: LOW
        source:
        - 10
        - 11
        - 50
        - 51
        - 52
        - 62
        - 89
        - 90
        - 91
```

##### Default `players.yml`:
```yaml
players:
  light-enabled: []
  effects-disabled: []
```

If you made changes to the configuration while the server is running, it is strongly recommended to use `/hat settings reload` to apply the changes instead of `/reload`.