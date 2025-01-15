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
- **/tpall [player/warp/spawn] [玩家名称 - PlayerName/传送点名称 - Teleport point]**  
强制将所有在线玩家传送到目标位置（如果不加参数默认传送到使用者身边）  
Forces all online players to teleport to the target location (defaults to the user if not parameterized)  
- **/tplogout <玩家名称 - PlayerName>**  
传送到该玩家最后一次下线的位置  
Teleport to the location where the player was last offline  
- **/tpaccept**  
接受传送请求（你可以点击聊天框里的 **[接受]** 来直接接受）  
Accept a teleport request (You can click on the **[Accept]** in the chat box to accept directly)
- **/tpdeny**  
拒绝传送请求（你可以点击聊天框里的 **[拒绝]** 来直接拒绝，点击聊天框中的 **[拒绝并拉黑]** 将拒绝请求并且拉黑该玩家）  
Reject teleport request (You can click **[Deny]** in the chat box to reject it outright, and **[Deny and add blacklist]** in the chat box to reject the request and blackmail the player.)  
- **/denys [add/remove] [玩家名称 - PlayerName]**  
列出玩家的黑名单列表  
Getting a blacklist of players  
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
- **/homes**
列出你设置的家  
List the homes you have set up
- **/sethome <家 - Home>**  
设置家  
Set the home  
- **/setdefaulthome <家 - Home>**  
设置默认的家  
Setting the default home  
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
- **tpa.denys**  
可使用 /denys 管理黑名单  
Blacklists can be managed using /denys  
- **tpa.tplogout**
可使用 /tplogout 传送到玩家最后的下线位置  
Can be transported to the player's last offline location using /tplogout  
- **tpa.warp**  
可使用 /warp 命令传送到传送点（默认关闭权限检查）  
You can use the /warp command to teleport to a teleport point (permission checking is turned off by default)  
- **tpa.home**  
可使用 /home 命令传送到家，可使用 /homes 列出所有可用的家，可使用 /sethome 设置家，可使用 /setdefaulthome 设置默认的家，可使用 /delhome 删除家（默认关闭权限检查）    
  You can use the /home command to transfer to a home, you can use /homes to list all available homes, you can use /sethome to set a home, you can use /setdefaulthome to set a default home, and you can use /delhome to delete a home (permission checking is turned off by default)  
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

## 关于配置迁移问题 - Questions about configuration migration
这几天爆肝了这么多的新内容，脑子稍微有点不够用了，写的进度卡在最后这个配置文件迁移问题上了  
所有老配置文件统一保存在 /plugins/TPA/backup/版本号/ 里面，想找老配置文件可在这里找到  

config.yml 插件会自动迁移，不需要管  
spawn.yml 打开文件并删除 "==: org.bukkit.Location" 保存在/plugins/TPA/spawn.yml  
warp.yml 同上，删除每个传送点里的 "==: org.bukkit.Location" 保存在/plugins/TPA/warp.yml  
home.yml 这个文件稍微有点麻烦，需要删掉 "==: org.bukkit.Location" 并且保存在/plugins/TPA/playerdata/玩家的uuid.yml （例如 e3a43876-367f-39a9-9b20-539a9409c9a4.yml）  
玩家登录服务器后会自动生成该文件  
格式大概如下：  

player_name: WarSkyGod // 玩家的名字  
lang: zh_CN // 玩家的语言（1.12及以上版本可以自动读取客户端语言，但还是建议填一个值）  
home_amount: 2 // 有几个家这里就写几个  
default_home: cs // 如果有家就随便填一个家的名字，使用 /home 会自动传送这个  
homes: // 没有家的话整个列表都可不填  
 cs:  
  world: world  
  x: 156.14841374540512  
  y: 79.0  
  z: -99.84139845197157  
  pitch: 24.89987  
  yaw: -169.87944  
 cs2:  
  world: world  
  x: 156.5698360255829  
  y: 78.0  
  z: -102.21630072737173  
  pitch: 24.89987  
  yaw: -169.87944  
last_location: // 上一次的位置，对应 back 命令 以及原来的 last_location.yml 文件  
 world: world  
 x: 156.9422094722107  
 y: 78.0  
 z: -104.33149265095616  
 pitch: 24.89987  
 yaw: -169.87944  


## 感谢 - Thank
本插件使用了 [FoliaLib](https://github.com/handyplus/FoliaLib) 来做 **Folia** 兼容  
This plugin use the [FoliaLib](https://github.com/handyplus/FoliaLib) to do **Folia** compatible
