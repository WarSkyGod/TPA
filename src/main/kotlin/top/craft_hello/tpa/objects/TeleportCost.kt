package top.craft_hello.tpa.objects

import org.bukkit.entity.Player
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.enums.RequestType
import top.craft_hello.tpa.utils.SendMessageUtil
import java.math.BigDecimal
import java.math.RoundingMode

// 传送费用（非管理传送命令）：发起预检（余额不足直接报错不进入流程）
// + 传送执行前扣费（余额被并发消费时在最后一步拦截，取消传送）。
// 会员优惠沿用既有权限 tpa.vip / tpa.vip+ / tpa.mvp / tpa.mvp+ / tpa.mvp++，
// 由配置 vip_discount 指定各等级实付百分比，多项命中取最优惠。
object TeleportCost {

    // 请求类型 → 配置费用键（无对应键的类型不收费；tplogout 为管理指令不收费）
    fun costKeyOf(type: RequestType): String? {
        return when (type) {
            RequestType.TPA -> "tpa"
            RequestType.TPA_HERE -> "tphere"
            RequestType.HOME -> "home"
            RequestType.WARP -> "warp"
            RequestType.SPAWN -> "spawn"
            RequestType.BACK -> "back"
            RequestType.RTP -> "rtp"
            else -> null
        }
    }

    // 配置 vip_discount 键 → 既有会员权限（沿用 tpa.vip 体系，不引入新权限）
    private fun vipPermissionOf(level: String): PermissionType? {
        return when (level.lowercase().trim()) {
            "vip" -> PermissionType.VIP
            "vip+" -> PermissionType.VIP_PLUS
            "mvp" -> PermissionType.MVP
            "mvp+" -> PermissionType.MVP_PLUS
            "mvp++" -> PermissionType.MVP_PLUS_PLUS
            else -> null
        }
    }

    // 实付费用：基础价 + 会员优惠。
    // 管理员豁免：拥有 tpa.admin 权限无视扣费（免费传送）
    // discount_mode: percent=vip_discount 值为实付百分比（50=半价，夹在 [0,100]）；
    //                amount=值为固定减免金额（减免额低于 0 视为无优惠）
    // 多项会员权限命中取实付最低；实付最低为 0（减免超出基础价按 0 扣费，不产生负数）
    fun costOf(player: Player, key: String): Double {
        // 管理员豁免：费用归零，预检与扣费全部短路为免费
        if (PermissionType.hasPermission(player, PermissionType.ADMIN)) return 0.0
        val config = ConfigManager.config
        if (!config.costEnable) return 0.0
        val base = config.costPrices[key] ?: 0.0
        if (base <= 0.0) return 0.0
        val percentMode = config.costDiscountMode != "amount"
        var payable = base
        for ((level, value) in config.costVipDiscount) {
            val permission = vipPermissionOf(level) ?: continue
            if (PermissionType.hasPermission(player, permission)) {
                val discounted = if (percentMode) {
                    base * value.coerceIn(0.0, 100.0) / 100.0
                } else {
                    if (value <= 0.0) base else base - value
                }
                payable = minOf(payable, maxOf(0.0, discounted))
            }
        }
        // 兜底校验：扣费最低为 0，永不为负
        return payable.coerceAtLeast(0.0)
    }

    private fun currencyNameOf(player: Player): String {
        val currency = ConfigManager.config.costCurrency
        val path = if (currency == "points") "system.currency_points" else "system.currency_money"
        return LanguageManager.getLanguage(player).getMessage(path)
    }

    private fun formatAmount(amount: Double): String {
        return BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
    }

    // 发起预检：余额不足 → 错误消息 + 取消进入流程
    fun precheck(player: Player, key: String): Boolean {
        val config = ConfigManager.config
        if (!config.costEnable) return true
        val amount = costOf(player, key)
        if (amount <= 0.0) return true
        val currency = config.costCurrency
        // 前置缺失：不收费（启动时已警告），保证费用配置不阻塞传送
        if (!EconomyHook.isCurrencyAvailable(currency)) return true
        val balance = EconomyHook.getBalance(player, currency)
        if (balance < amount) {
            SendMessageUtil.costInsufficientError(player, formatAmount(amount), formatAmount(balance), currencyNameOf(player))
            return false
        }
        return true
    }

    // 传送执行前扣费：余额不足 → 错误消息 + false（调用方取消传送）
    fun charge(player: Player, key: String): Boolean {
        val config = ConfigManager.config
        if (!config.costEnable) return true
        val amount = costOf(player, key)
        if (amount <= 0.0) return true
        val currency = config.costCurrency
        if (!EconomyHook.isCurrencyAvailable(currency)) return true
        val balance = EconomyHook.getBalance(player, currency)
        if (balance < amount) {
            SendMessageUtil.costInsufficientError(player, formatAmount(amount), formatAmount(balance), currencyNameOf(player))
            return false
        }
        if (!EconomyHook.withdraw(player, currency, amount)) {
            SendMessageUtil.costInsufficientError(player, formatAmount(amount), formatAmount(balance), currencyNameOf(player))
            return false
        }
        SendMessageUtil.costChargedMessage(player, formatAmount(amount), formatAmount(balance - amount), currencyNameOf(player))
        return true
    }
}
