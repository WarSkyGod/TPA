package top.craft_hello.tpa.utils

import cn.handyplus.lib.adapter.HandySchedulerUtil
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.objects.ConfigManager
import java.net.HttpURLConnection
import java.net.URI

// 更新检查：直接拉取 GitHub Releases Latest（api.github.com），比较 tag_name 与插件版本
object VersionUtil {
    private const val API_URL = "https://api.github.com/repos/WarSkyGod/TPA/releases/latest"

    private lateinit var plugin: TPA
    private var oldVersion: Boolean = false

    fun init(plugin: TPA) {
        this.plugin = plugin
        oldVersion = versionComparison(plugin.description.version, "4.0")
    }

    fun isOldVersion(): Boolean = oldVersion

    // 比较两段式/三段式版本号：old > new 返回 true
    fun versionComparison(old: String, new: String): Boolean {
        val oldParts = old.substringBefore("-").split(".")
        val newParts = new.substringBefore("-").split(".")
        val size = maxOf(oldParts.size, newParts.size)
        for (i in 0 until size) {
            val o = oldParts.getOrNull(i)?.toIntOrNull() ?: 0
            val n = newParts.getOrNull(i)?.toIntOrNull() ?: 0
            if (o > n) return true
            if (o < n) return false
        }
        return false
    }

    // 启动时后台检查一次（update_check 开启时）
    fun startAsyncUpdateCheck() {
        if (!ConfigManager.config.updateCheck) return
        HandySchedulerUtil.runTaskAsynchronously {
            updateCheck(Bukkit.getConsoleSender())
        }
    }

    // 检查更新并通知 sender（应在异步线程调用）
    fun updateCheck(sender: CommandSender) {
        SendMessageUtil.checkUpdate(sender)
        val latestVersion = fetchLatestVersion() ?: run {
            SendMessageUtil.updateFailedError(sender)
            return
        }
        if (versionComparison(latestVersion, plugin.description.version)) {
            SendMessageUtil.pluginUpdateMessage(sender, latestVersion)
        } else {
            SendMessageUtil.pluginLatestVersion(sender)
        }
    }

    // 拉取 GitHub Latest 的 tag_name；网络异常返回 null
    private fun fetchLatestVersion(): String? {
        var connection: HttpURLConnection? = null
        return try {
            connection = (URI.create(API_URL).toURL().openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "TPA-Plugin/${plugin.description.version}")
                setRequestProperty("Accept", "application/vnd.github+json")
            }
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return null
            val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            // 不用 gson：1.8 服务器内嵌 gson 2.2.4 没有 JsonParser.parseString（编译期
            // classpath 被 paper-api 携带的新版 gson 调解提升导致编译通过、运行时炸），
            // 只需 tag_name，正则提取即可
            return Regex("\"tag_name\"\\s*:\\s*\"([^\"]+)\"").find(body)
                ?.groupValues?.get(1)?.removePrefix("v")?.removePrefix("V")
        } catch (ignored: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }
}
