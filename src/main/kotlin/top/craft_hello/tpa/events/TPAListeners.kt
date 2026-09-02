package top.craft_hello.tpa.events

import cn.handyplus.lib.adapter.HandySchedulerUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLocaleChangeEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.LanguageManager
import top.craft_hello.tpa.objects.PlayerDataManager
import top.craft_hello.tpa.datas.TeleportRequest
import top.craft_hello.tpa.utils.LocaleUtil
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil
import top.craft_hello.tpa.utils.TeleportUtil
import top.craft_hello.tpa.utils.VersionUtil

// 玩家死亡：记录死亡位置为"上一次的位置"
// （getEntity() 全版本可用；getPlayer() 为 1.9+ API，1.8 编译兜底不可用）
object TPAPlayerDeathEvent : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        SafeGuard.event("onPlayerDeath") {
            val player = event.entity
            PlayerDataManager.get(player).lastLocation = player.location
            PlayerDataManager.save(player)
        }
    }
}

// 玩家加入：加载数据、语言跟随、force_spawn 传送、更新检查通知
object TPAPlayerJoinEvent : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        SafeGuard.event("onPlayerJoin") {
            val player = event.player
            val playerData = PlayerDataManager.get(player)
            playerData.playerName = player.name
            PlayerDataManager.save(player)

            // 未手动设置语言时跟随客户端语言（1.11 及以下无法获取，回退配置默认）
            if (!playerData.setlang) {
                val clientLanguage = LanguageManager.formatLangStr(LocaleUtil.playerLocale(player) ?: "")
                if (LanguageManager.hasLanguage(clientLanguage)) {
                    playerData.language = clientLanguage
                    PlayerDataManager.save(player)
                }
            }

            // force_spawn：加入服务器强制传送到主城
            if (ConfigManager.config.forceSpawn) {
                val spawnLocation = ConfigManager.spawnConfig.getLocation()
                if (spawnLocation != null) TeleportUtil.teleport(player, spawnLocation)
            }

            // 更新检查通知（有权限的管理者）
            if (ConfigManager.config.updateCheck && PermissionType.hasPermission(player, PermissionType.VERSION)) {
                HandySchedulerUtil.runTaskAsynchronously {
                    VersionUtil.updateCheck(player)
                }
            }
        }
    }
}

// 玩家退出：记录最后下线位置、保存并清理数据与请求
object TPAPlayerQuitEvent : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        SafeGuard.event("onPlayerQuit") {
            val player = event.player
            val playerData = PlayerDataManager.getIfLoaded(player.uniqueId) ?: return@event
            playerData.logoutLocation = player.location
            PlayerDataManager.save(player)
            PlayerDataManager.unload(player.uniqueId)
            TeleportRequest.clearQueue(player.uniqueId)
        }
    }
}

// 玩家重生：force_spawn 开启时重生后传送到主城
object TPAPlayerRespawnEvent : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        SafeGuard.event("onPlayerRespawn") {
            if (!ConfigManager.config.forceSpawn) return@event
            val spawnLocation = ConfigManager.spawnConfig.getLocation() ?: return@event
            event.respawnLocation = spawnLocation
        }
    }
}

// 玩家传送：仅记录命令/插件发起的传送为"上一次的位置"
object TPAPlayerTeleportEvent : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        SafeGuard.event("onPlayerTeleport") {
            val cause = event.cause.name
            if (cause != "COMMAND" && cause != "PLUGIN") return@event
            val player = event.player
            PlayerDataManager.get(player).lastLocation = event.from
            PlayerDataManager.save(player)
        }
    }
}

// 客户端语言切换：未手动设置语言时跟随（Bukkit 事件 getLocale() 返回 "zh_CN" 格式字符串）
object TPAPlayerLocaleChangeEvent : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerLocaleChange(event: PlayerLocaleChangeEvent) {
        SafeGuard.event("onPlayerLocaleChange") {
            val player = event.player
            val playerData = PlayerDataManager.getIfLoaded(player.uniqueId) ?: return@event
            if (playerData.setlang) return@event
            val clientLanguage = LanguageManager.formatLangStr(event.locale)
            if (LanguageManager.hasLanguage(clientLanguage) && playerData.language != clientLanguage) {
                playerData.language = clientLanguage
                PlayerDataManager.save(player)
            }
        }
    }
}
