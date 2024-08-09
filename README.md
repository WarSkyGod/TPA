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
设置传送点（需要 **tpa.setwarp** 权限）  
Set the teleport point (The **tpa.setwarp** permission is required)
- **/back**  
传送到上一次的位置（需要 **tpa.back** 权限）  
Teleport to the Last location (The **tpa.back** permission is required)
- **/tpa reload**  
重新加载配置文件（需要 **tpa.reload** 权限）
Reload the configuration file (The **tpa.reload** permission is required)

## 注意 - WARN
从1.2及以下版本更新到1.3请删除配置文件和语言文件，重新生成后修改  
To update from version 1.2 and below to 1.3, please delete the configuration file and language file, and modify them after regeneration. 

## 感谢 - Thank
本插件使用了 [FoliaLib](https://github.com/handyplus/FoliaLib) 来做 **Folia** 兼容  
This plugin use the [FoliaLib](https://github.com/handyplus/FoliaLib) to do **Folia** compatible
