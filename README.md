# Ghostwriter

A proof of concept used to demonstrate [MC-253888](https://bugs.mojang.com/browse/MC-253888), originally reported by Sigurður Jón in [MC-253521](https://bugs.mojang.com/browse/MC-253521). **The exploit was patched in 1.19.1-pre5**, and chat preview was removed completely in 22w42a.

This exploit allows a malicious server to trick users into signing arbitrary server-controlled messages, which can then be used as valid evidence in reports.

## Usage

Run this mod on a server using Minecraft 1.19.1-pre2, then use `/gw <strategy>` to change the strategy the mod uses to trick users. The mod will then insert the string `(something nasty)` into the preview when conditions are met.

Use `/gw none` to disable preview manipulation.

### Strategies

#### `speed`

The chat preview for "l" and "lo" is normal, but "lol" is replaced with "(something nasty)".
The player can see the message, but since players often type short phrases like "lol" and "gg" quickly, the player may not notice before it's too late.

#### `sleight`

Inserts "(something nasty)" into the start of messages that are 150 of more characters long. If a player is focusing on the end of a message, they may not realize the beginning has been modified.

#### `shadow`

The same as `sleight`, but the preview text is black, making it harder to notice.

#### `sneak`

Inserts "(something nasty)" into the message as a **hover event**. The user must hover over the preview to detect that it has been modified.

#### `suppress`

Inserts "(something nasty)" into the message as **insertion text**. The user must **shift click** on the preview to detect that it has been modified.

#### `showcase`

This strategy mimics plugins that allow players to showcase their held item by typing `[item]` in a message. The held item's name will be changed to "(something nasty)".

#### Theoretical Strategies

There are other ways to trick players into signing messages than what's included in the mod. An example is to mimic plugins with "first to unscramble this word" challenges, then change the preview right when players are about to send the correct word.
