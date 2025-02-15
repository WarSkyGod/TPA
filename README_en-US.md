
---
# TPA
[简体中文](https://github.com/WarSkyGod/TPA)  
A simple teleportation plugin that supports **Folia**, compatible with **Bukkit/Spigot/Paper/Folia**.

## Features

- **Simple**: Easy to use and configure.
- **Configurable**: Supports custom configuration files.
- **Multilingual Support**: Built-in multiple language files.
- **Customizable Language Files**: Allows users to customize language files.

## Commands

### Teleportation
- **/tpa <player name>**  
  Send a teleportation request to a player.
- **/tphere <player name>**  
  Request a player to teleport to your location.
- **/tpall [player/warp/spawn] [player name/warp name]**  
  Force all online players to teleport to the target location (if no parameters are provided, defaults to teleporting to the user's location).
- **/tplogout <player name>**  
  Teleport to the last logout location of the specified player.
- **/tpaccept**  
  Accept a teleportation request (you can click **[Accept]** in the chat to directly accept).
- **/tpdeny**  
  Deny a teleportation request (you can click **[Deny]** in the chat to directly deny, or click **[Deny and Block]** to deny the request and block the player).
- **/denys [add/remove] [player name]**  
  Manage the player's blacklist.

### Warp
- **/warp <warp name>**  
  Teleport to a warp point.
- **/setwarp <warp name>**  
  Set a warp point.
- **/delwarp <warp name>**  
  Delete a warp point.

### Home
- **/home <home name>**  
  Teleport to a home location.
- **/homes**  
  List all your home locations.
- **/sethome <home name>**  
  Set a home location.
- **/setdefaulthome <home name>**  
  Set the default home location.
- **/delhome <home name>**  
  Delete a home location.

### Spawn
- **/spawn**  
  Teleport to the spawn point.
- **/setspawn**  
  Set the spawn point.
- **/delspawn**  
  Delete the spawn point.

### Others
- **/back**  
  Teleport to the previous location.
- **/rtp**  
  Random teleportation.
- **/tpa version**  
  Check for plugin updates.
- **/tpa setlang <clear/language>**  
  Set the client display language.
- **/tpa reload**  
  Reload the configuration file.

## Permissions

### Admin
- **tpa.admin**  
  Highest permission, allows execution of all commands.
- **tpa.tpall**  
  Allows using the `/tpall` command to force all online players to teleport to your location.
- **tpa.tplogout**  
  Allows using the `/tplogout` command to teleport to a player's last logout location.
- **tpa.reload**  
  Allows using the `/reload` command to reload the configuration file.
- **tpa.version**  
  Players with this permission will receive plugin update notifications and can use `/tpa version` to check for updates.
- **tpa.nodelay**  
  Players with this permission are not restricted by command cooldowns.

### Teleportation
- **tpa.tpa**  
  Allows using the `/tpa` command to request teleportation to a specified player (permission check is disabled by default).
- **tpa.tphere**  
  Allows using the `/tphere` command to request a specified player to teleport to your location (permission check is disabled by default).
- **tpa.rtp**  
  Allows using the `/rtp` command for random teleportation (permission check is disabled by default).

### Blacklist
- **tpa.denys**  
  Allows using the `/denys` command to manage the blacklist.

### Spawn
- **tpa.spawn**  
  Allows using the `/spawn` command to teleport to the spawn point (permission check is disabled by default).
- **tpa.setspawn**  
  Allows using the `/setspawn` command to set the spawn point.
- **tpa.delspawn**  
  Allows using the `/delspawn` command to delete the spawn point.

### Warp
- **tpa.warp**  
  Allows using the `/warp` command to teleport to a warp point (permission check is disabled by default).
- **tpa.setwarp**  
  Allows using the `/setwarp` command to set a warp point.
- **tpa.delwarp**  
  Allows using the `/delwarp` command to delete a warp point.

### Home
- **tpa.home**  
  Allows using the `/home`, `/homes`, `/sethome`, `/setdefaulthome`, and `/delhome` commands (permission check is disabled by default).

### VIP
- **tpa.vip**
- **tpa.vip+**
- **tpa.mvp**
- **tpa.mvp+**
- **tpa.mvp++**  
  Allows configuring the maximum number of homes a player with this permission can set (-1 for unlimited) and command cooldowns in the configuration file.

### Others
- **tpa.back**  
  Allows using the `/back` command to teleport to the previous location (permission check is disabled by default).

## About Configuration Migration

Version 3.2.0 added automatic migration for old configuration files, so there is no need to worry.  
However, for safety reasons, the old configuration files will be automatically backed up to the `plugins/TPA/backup/old version number` directory. If the migration fails, please manually attempt to migrate the configuration files.

## Acknowledgments

This plugin uses [FoliaLib](https://github.com/handyplus/FoliaLib) for **Folia** compatibility.

### Code Contributors
- [fanlepian1](https://github.com/fanlepian1)
- [Apleax](https://github.com/Apleax)
- [LFWQSP2641](https://github.com/LFWQSP2641)
- [sky-3311](https://github.com/sky-3311)

### Bug Reporters
- [fanlepian1](https://github.com/fanlepian1)
- [Apleax](https://github.com/Apleax)
- [luckeist](https://github.com/luckeist)
- [DuckCattyCotton](https://github.com/DuckCattyCotton)
- [Ry4nnnnn](https://github.com/Ry4nnnnn)
- [sky-3311](https://github.com/sky-3311)

### Feature Suggesters
- [Apleax](https://github.com/Apleax)

### AI Models
- [DeepSeek](https://www.deepseek.com/)

--- 
