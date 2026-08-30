package top.craft_hello.tpa.utils

import cn.handyplus.lib.adapter.EntitySchedulerUtil
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.geysermc.cumulus.component.ButtonComponent
import org.geysermc.cumulus.form.SimpleForm
import org.geysermc.cumulus.form.util.FormBuilder
import org.geysermc.floodgate.api.FloodgateApi
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.LanguageManager

// 基岩玩家弹窗交互（可选前置 Floodgate，Java 版玩家不受影响）：
// 基岩版聊天栏不支持点击组件，点击类消息改为 Cumulus SimpleForm 弹窗按钮，
// 按钮点击后经实体调度器回到玩家 region 线程以该玩家身份执行命令。
// 表单文本复用现有语言键（MiniMessage 标签剥离为纯文本），无需新增翻译。
object BedrockFormHook {
    private var floodgateApi: FloodgateApi? = null

    var available = false
        private set

    fun init(plugin: TPA) {
        available = try {
            val floodgatePlugin = Bukkit.getPluginManager().getPlugin("Floodgate")
            if (floodgatePlugin != null) {
                floodgateApi = FloodgateApi.getInstance()
                floodgateApi != null
            } else {
                plugin.logger.info("未检测到 Floodgate 插件，基岩玩家将使用聊天交互")
                false
            }
        } catch (e: Throwable) {
            plugin.logger.warning("Floodgate 挂钩失败，基岩玩家将使用聊天交互: ${e.javaClass.name}: ${e.message}")
            e.printStackTrace()
            false
        }
        if (available) plugin.logger.info("已挂钩 Floodgate（基岩玩家点击类消息将以弹窗按钮呈现）")
    }

    // 是否为基岩玩家（前置未安装时恒为 false，走 Java 版聊天交互）
    fun isBedrockPlayer(player: Player): Boolean {
        val result = try {
            floodgateApi?.isFloodgatePlayer(player.uniqueId) ?: false
        } catch (e: Throwable) {
            TPA.plugin.logger.warning("基岩玩家判定异常（按非基岩处理）: ${e.javaClass.name}: ${e.message}")
            e.printStackTrace()
            false
        }
        if (result && ConfigManager.config.debug) TPA.plugin.logger.info("[debug] 已识别基岩玩家: ${player.name}")
        return result
    }

    // MiniMessage 文本剥离为纯文本（基岩表单不支持 Adventure 标签）
    // 占位符替换沿用 Language.formatText 槽位语义：1 变量填满全部槽位；
    // 2 变量第一个填 %target%，第二个填其余槽位（变量来自玩家名/秒数，仍做转义防注入）
    private val SLOT_KEYS = arrayOf("%target%", "%command%", "%message%", "%max_home_amount%", "%seconds%")

    private fun plainTextOf(player: Player, path: String, vararg vars: String): String {
        val raw = LanguageManager.getLanguage(player).getMessage(path)
        val mini = MiniMessage.miniMessage()
        val escaped = vars.map { mini.escapeTags(it) }
        var replaced = raw
        when (vars.size) {
            1 -> for (slot in SLOT_KEYS) replaced = replaced.replace(slot, escaped[0])
            2 -> {
                replaced = replaced.replace("%target%", escaped[0])
                for (slot in SLOT_KEYS.filter { it != "%target%" }) replaced = replaced.replace(slot, escaped[1])
            }
        }
        return mini.stripTags(replaced).trim()
    }

    // 发送弹窗（FormBuilder 未 build 也可直接发送，FloodgateApi.sendForm 支持两种形态）
    private fun sendForm(player: Player, builder: FormBuilder<*, *, *>): Boolean {
        return try {
            val result = floodgateApi?.sendForm(player.uniqueId, builder) ?: false
            if (!result) TPA.plugin.logger.warning("弹窗发送返回 false（目标 ${player.name} 可能不在线或非基岩玩家）")
            result
        } catch (e: Throwable) {
            TPA.plugin.logger.warning("弹窗发送异常: ${e.javaClass.name}: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    // 传送请求弹窗：接受 / 拒绝 / 拒绝并加入黑名单（对齐 Java 版 acceptOrDeny 三按钮）
    fun sendTeleportRequestForm(target: Player, executorName: String, delay: String, toHere: Boolean): Boolean {
        if (!isBedrockPlayer(target)) return false
        val contentPath = if (toHere) "request.to_target" else "request.to_here"
        val builder = SimpleForm.builder()
            .title(plainTextOf(target, "request.title", executorName))
            .content(plainTextOf(target, contentPath, executorName, delay))
            .button(plainTextOf(target, "accept_button", executorName))
            .button(plainTextOf(target, "deny_button", executorName))
            .button(plainTextOf(target, "blacklist.add_button", executorName))
            .validResultHandler { _, response ->
                val command = when (response.clickedButtonId()) {
                    0 -> "tpaccept"
                    1 -> "tpdeny"
                    2 -> "denys add $executorName"
                    else -> return@validResultHandler
                }
                // 回调在 Geyser 线程：调度回玩家 region 线程以该玩家身份执行命令
                EntitySchedulerUtil.runSafeOnEntityScheduler(target) {
                    if (target.isOnline) target.performCommand(command)
                }
            }
        return sendForm(target, builder)
    }

    // 传送目标列表弹窗：每个条目一个"传送"按钮（homes/warps 通用）
    fun sendTeleportListForm(player: Player, titlePath: String, entries: List<String>, command: String): Boolean {
        if (!isBedrockPlayer(player) || entries.isEmpty()) return false
        val builder = SimpleForm.builder()
            .title(plainTextOf(player, titlePath))
        for (name in entries) builder.button(ButtonComponent.of(name))
        builder.validResultHandler { _, response ->
            val index = response.clickedButtonId()
            if (index < 0 || index >= entries.size) return@validResultHandler
            val entry = entries[index]
            EntitySchedulerUtil.runSafeOnEntityScheduler(player) {
                if (player.isOnline) player.performCommand("$command $entry")
            }
        }
        return sendForm(player, builder)
    }
}
