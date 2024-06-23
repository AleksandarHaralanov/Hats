# Hats
Allows blocks to be worn as hats.

<b>Download latest release [here](https://github.com/AleksandarHaralanov/Hats/releases/latest).</b>
- Compiled on Adoptium JDK 8 with [Poseidon](https://github.com/RhysB/Project-Poseidon), fork of CB1060.

## Features
- Wear a block as a hat. [(ID 1 - 96)](https://imgur.com/RIVgSD7)
- Implements precise checks for various conditions including no item held, hat already worn, swap of new and old hat, and dropping the old hat on the ground when a new one is equipped with a full inventory.
- Enforces checks to ensure only one unit of X block is worn in the helmet slot, preventing players from using it as an additional inventory slot.

## Usage
By default, only OPs have permissions.<br>Use PermissionsEx or similar plugins to grant groups the permission, enabling the command.
- Command: ```/hat```
- Permission: ```hats.wear```