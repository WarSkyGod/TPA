# TPA
一个支持 **Folia** 的简易传送插件，支持 **Bukkit/Spigot/Paper/Folia**  
A simple teleport plug-in that supports **Folia**, supported **Bukkit/Spigot/Paper/Folia  

## 特点 - Feature
- 简单  
Simple
- 可配置  
Configurable
- 中/英文支持  
Chinese/English language supports
- 可自定义语言文件  
Customizable language files

## 命令 - Commands
- **/tpa <玩家名称 - PlayerName>**  
向玩家发送传送请求  
Send a teleport request to the player  
- **/tphere <玩家名称 - PlayerName>**  
请求玩家传送到你身边  
Ask the player to teleport to you  
- **/tpall**  
强制所有在线玩家传送到你身边  
Force all online players to teleport to you  
- **/tpaccept**  
接受传送请求（你可以点击聊天框里的 **[接受/tpaccept]** 来直接接受）  
Accept a teleport request (You can click on the **[Accept/tpaccept]** in the chat box to accept directly)
- **/tpdeny**  
拒绝传送请求（你可以点击聊天框里的 **[拒绝/tpdeny]** 来直接拒绝）  
Reject teleport request (You can directly reject by clicking on the **[Deny/tpdeny]** in the chat box)  
- **/warp <传送点 - Teleport point>**  
传送到传送点  
Teleport to the Teleport point  
- **/setwarp <传送点 - Teleport point>**  
设置传送点  
Set the teleport point  
- **/delwarp <传送点 - Teleport point>**  
删除传送点  
Delete the teleport point  
- **/home <家 - Home>**  
传送到家  
Teleport to the home  
- **/sethome <家 - Home>**  
设置家  
Set the home  
- **/delhome <家 - Home>**  
删除家  
Delete the home  
- **/spawn**  
传送到主城  
Teleport to the spawn  
- **/setspawn**  
设置主城  
Set the spawn   
- **/delspawn**  
删除主城  
Delete the spawn  
- **/back**  
传送到上一次的位置  
Teleport to the Last location  
- **/tpa reload**  
重新加载配置文件  
Reload the configuration file    

## 权限 - Permissions
- **tpa.admin**  
最高权限，可执行所有操作  
Highest authority to perform all operations  
- **tpa.reload**  
可使用/reload命令重新加载配置文件  
The configuration file can be reloaded using the /reload command  
- **tpa.version**  
拥有这个权限的玩家会收到插件更新通知  
Players with this permission will be notified of plugin updates  
- **tpa.setwarp**  
可使用 /setwarp 命令设置传送点  
The teleport point can be set using the /setwarp command  
- **tpa.delwarp**  
可使用 /delwarp 命令删除传送点  
The teleport point can be delete using the /delwarp command  
- **tpa.setspawn**  
可使用 /setspawn 命令设置主城  
The spawn can be set using the /setspawn command  
- **tpa.delspawn**  
可使用 /delspawn 命令删除主城  
The spawn can be delete using the /delspawn command
- **tpa.tpall**  
可使用 /tpall 命令强行将所有在线玩家传送到你的位置  
The /tpall command can be used to forcibly teleport all online players to your location  
- **tpa.tpa**  
可使用 /tpa 命令请求传送到指定玩家的位置（默认关闭权限检查）  
The /tpa command can be used to request teleportation to a specified player's location (permission checking is turned off by default)  
- **tpa.tphere**  
可使用 /tphere 命令请求指定玩家传送到你的位置（默认关闭权限检查）  
The /tpa command can be used to request that a specified player teleport to your location (permission checking is turned off by default)  
- **tpa.warp**  
可使用 /warp 命令传送到传送点（默认关闭权限检查）  
You can use the /warp command to teleport to a teleport point (permission checking is turned off by default)  
- **tpa.home**  
可使用 /home 命令传送到家，可使用 /sethome 设置家，可使用 /delhome 删除家（默认关闭权限检查）    
Home can be teleport to using the /home command, home can be set using /sethome, and home can be deleted using /delhome (permission checking is turned off by default)  
- **tpa.vip**  
- **tpa.svip**  
可在配置文件中设置拥有该权限的玩家最多可以设置多少个家（-1 为不限制）  
The maximum number of homes that can be set by a player with this permission can be set in the configuration file (-1 is unrestricted)  
- **tpa.spawan**   
可使用 /spawn 命令传送到主城（默认关闭权限检查）  
You can use the /spawn command to teleport to a spawn (permission checking is turned off by default)  
- **tpa.back**   
可使用 /back 命令传送到上一次的位置（默认关闭权限检查）  
You can use the /back command to teleport to a last location (permission checking is turned off by default)

## 感谢 - Thank
本插件使用了 [FoliaLib](https://github.com/handyplus/FoliaLib) 来做 **Folia** 兼容  
This plugin use the [FoliaLib](https://github.com/handyplus/FoliaLib) to do **Folia** compatible
