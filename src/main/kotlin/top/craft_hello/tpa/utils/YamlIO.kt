package top.craft_hello.tpa.utils

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files

// yml 读写统一编码封装。
// 1.8-1.12 服务器 YamlConfiguration.load(File)/save(File) 走 JVM 系统默认编码
// （中文 Windows 为 GBK）：按系统编码读 UTF-8 文件会把中文读乱；按系统编码保存
// 又会把 UTF-8 内容写坏（下次读取必乱）。这里统一显式 UTF-8：
// 读：UTF-8 解析；若检出替换符（3.x 旧档为系统编码写入的中文）回退系统默认编码重读；
// 写：saveToString() 经 UTF-8 输出流落盘，杜绝 FileWriter 的系统编码覆盖。
object YamlIO {

    fun load(file: File): YamlConfiguration {
        val utf8 = YamlConfiguration.loadConfiguration(
            InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)
        )
        return if (hasReplacementChar(utf8)) YamlConfiguration.loadConfiguration(file) else utf8
    }

    fun save(config: FileConfiguration, file: File) {
        OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8).use { writer ->
            writer.write(config.saveToString())
        }
    }

    // UTF-8 流读取 GBK 字节必然产生 U+FFFD 替换符，据此识别 3.x 系统编码旧档
    private fun hasReplacementChar(config: FileConfiguration): Boolean {
        for (key in config.getKeys(true)) {
            val value = config.get(key)
            if (value is String && value.contains('\uFFFD')) return true
        }
        return false
    }
}
