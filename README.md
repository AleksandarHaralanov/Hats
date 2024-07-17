# Hats
Allows blocks to be worn as hats.

<b>Download latest release [here](https://github.com/AleksandarHaralanov/Hats/releases/latest).</b>
- Compiled on Adoptium JDK 8 with [Poseidon](https://github.com/RhysB/Project-Poseidon), fork of CB1060.

## Features
- Wear the block in hand as a hat. [(ID 1 - 96)](https://imgur.com/RIVgSD7)
  - Implements checks for various conditions such as: No block held in hand, item cannot be worn as a hat, held block is already worn as a hat, swap of new and old hat, and dropping the old hat on the ground when a new one is equipped with a full inventory.
  - Enforces checks to ensure only one unit of a block is worn in the helmet slot, preventing players from using it as an additional inventory slot.
- It checks for a newer version of the plugin on the GitHub repository and displays a download link in the console if an update is available.

## Usage
By default, only OPs have permission.

Use PermissionsEx or similar plugins to grant groups the permission, enabling the command.
- Commands:
  - `/hat` - **Requires permission** - Wear the block in hand as a hat.
  - `/hat [v | ver | version]` - **Doesn't require permission** - Prints author, version, and a link to this repository.
- Permission: `hats.wear`