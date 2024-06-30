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
- **/restp <领地传送点 - Teleport point>**  
传送到领地传送点（**Folia** 版的领地插件传送有问题，所以我做了这个指令，不需要的话可以无视这个）  
Teleport to the Resident Teleport point (**Folia** version of the Residence plugin teleport issues, so I made this instruction, Ignore it if you don't need i)
- **/restpset <领地传送点 - Teleport point>**  
设置领地传送点（需要 **tpa.restpset** 权限，不需要的话可以无视这个）  
Set the Residence teleport point (The **tpa.restpset** permission is required, Ignore it if you don't need i)
- **/tpareload**  
重新加载配置文件（需要 **tpa.reload** 权限）
Reload the configuration file (The **tpa.reload** permission is required)

## 感谢 - Thank
本插件使用了 [FoliaLib](https://github.com/handyplus/FoliaLib) 来做 **Folia** 兼容  
This plugin use the [FoliaLib](https://github.com/handyplus/FoliaLib) to do **Folia** compatible
