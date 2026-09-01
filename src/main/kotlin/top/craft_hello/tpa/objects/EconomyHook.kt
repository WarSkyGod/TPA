package top.craft_hello.tpa.objects

import net.milkbowl.vault.economy.Economy
import org.black_ixx.playerpoints.PlayerPoints
import org.black_ixx.playerpoints.PlayerPointsAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.utils.SendMessageUtil

// 可选经济前置挂钩（启动时检测，未安装时对应货币功能自动不可用）：
// - Vault（金钱）：通过 ServicesManager 获取已注册的 Economy 实现
// - PlayerPoints（点券）：按官方 Wiki 规范调用 PlayerPoints.getInstance().getAPI()
object EconomyHook {
    private var economy: Economy? = null
    private var playerPointsApi: PlayerPointsAPI? = null

    var vaultAvailable = false
        private set
    var pointsAvailable = false
        private set

    fun init(plugin: TPA) {
        vaultAvailable = try {
            if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                economy = Bukkit.getServicesManager().getRegistration(Economy::class.java)?.provider
                economy != null
            } else {
                false
            }
        } catch (e: Throwable) {
            false
        }
        if (vaultAvailable) plugin.logger.info(SendMessageUtil.consoleLog("system.log.vault_hooked"))

        pointsAvailable = try {
            if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
                playerPointsApi = PlayerPoints.getInstance().getAPI()
                playerPointsApi != null
            } else {
                false
            }
        } catch (e: Throwable) {
            false
        }
        if (pointsAvailable) plugin.logger.info(SendMessageUtil.consoleLog("system.log.playerpoints_hooked"))
    }

    // 指定货币类型是否有可用提供者（money=Vault / points=PlayerPoints）
    fun isCurrencyAvailable(currency: String): Boolean {
        return if (currency == "points") pointsAvailable else vaultAvailable
    }

    fun getBalance(player: Player, currency: String): Double {
        return try {
            if (currency == "points") {
                playerPointsApi?.look(player.uniqueId)?.toDouble() ?: 0.0
            } else {
                economy?.getBalance(player) ?: 0.0
            }
        } catch (e: Throwable) {
            0.0
        }
    }

    fun withdraw(player: Player, currency: String, amount: Double): Boolean {
        return try {
            if (currency == "points") {
                playerPointsApi?.take(player.uniqueId, Math.round(amount).toInt()) ?: false
            } else {
                economy?.withdrawPlayer(player, amount)?.transactionSuccess() ?: false
            }
        } catch (e: Throwable) {
            false
        }
    }
}
