package top.craft_hello.tpa.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 优化的异步Tab补全工具类，使用缓存和高效算法
 */
public class OptimizedAsyncTabCompleteUtil {
    // 缓存在线玩家名称列表，使用线程安全的集合
    private static final List<String> cachedOnlinePlayerNames = new CopyOnWriteArrayList<>();
    private static final Map<String, List<String>> prefixCache = new ConcurrentHashMap<>();

    // 缓存刷新间隔（毫秒）
    private static final long CACHE_REFRESH_INTERVAL = 5000; // 5秒刷新一次
    private static long lastCacheUpdate = 0;

    /**
     * 异步根据输入的部分名称过滤在线玩家列表
     * @param input 用户输入的部分名称
     * @param excludePlayer 要排除的玩家（可为null）
     * @return 过滤后的玩家名称列表
     */
    public static List<String> filterOnlinePlayers(String input, Player excludePlayer) {
        // 检查缓存是否需要更新
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCacheUpdate > CACHE_REFRESH_INTERVAL || cachedOnlinePlayerNames.isEmpty()) {
            // 直接在当前线程更新缓存，避免创建过多任务
            updateOnlinePlayerCache();
            lastCacheUpdate = currentTime;
        }

        // 如果没有输入，返回缓存的在线玩家列表（排除指定玩家）
        if (input == null || input.isEmpty()) {
            List<String> result = new ArrayList<>(cachedOnlinePlayerNames);
            if (excludePlayer != null) {
                result.remove(excludePlayer.getName());
            }
            return result;
        }

        // 检查前缀缓存
        String lowerInput = input.toLowerCase();
        List<String> cachedResult = prefixCache.get(lowerInput);
        if (cachedResult != null && !cachedResult.isEmpty()) {
            List<String> result = new ArrayList<>(cachedResult);
            if (excludePlayer != null) {
                result.remove(excludePlayer.getName());
            }
            return result;
        }

        // 使用流式处理进行高效过滤
        List<String> result = cachedOnlinePlayerNames.stream()
            .filter(name -> excludePlayer == null || !name.equals(excludePlayer.getName()))
            .filter(name -> name.toLowerCase().startsWith(lowerInput))
            .collect(Collectors.toList());

        // 缓存结果，但限制缓存大小
        if (prefixCache.size() < 100) { // 限制缓存大小，防止内存泄漏
            prefixCache.put(lowerInput, new ArrayList<>(result));
        }

        return result;
    }

    /**
     * 根据输入的部分名称过滤离线玩家列表
     * @param input 用户输入的部分名称
     * @param excludePlayerName 要排除的玩家名称（可为null）
     * @return 过滤后的玩家名称列表
     */
    public static List<String> filterOfflinePlayers(String input, String excludePlayerName) {
        // 由于离线玩家列表可能很大，我们限制返回的结果数量
        List<String> result = new ArrayList<>();
        int maxResults = 50; // 最多返回50个结果，防止列表过长

        // 如果没有输入，返回部分离线玩家（排除指定玩家）
        if (input == null || input.isEmpty()) {
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                if (excludePlayerName == null || !player.getName().equals(excludePlayerName)) {
                    result.add(player.getName());
                    if (result.size() >= maxResults) break;
                }
            }
            return result;
        }

        // 根据输入过滤玩家名称
        String lowerInput = input.toLowerCase();
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (excludePlayerName != null && player.getName().equals(excludePlayerName)) {
                continue;
            }

            // 检查玩家名称是否以输入字符串开头（不区分大小写）
            if (player.getName() != null && player.getName().toLowerCase().startsWith(lowerInput)) {
                result.add(player.getName());
                if (result.size() >= maxResults) break;
            }
        }

        return result;
    }

    /**
     * 根据输入的部分名称过滤指定的玩家名称列表
     * @param input 用户输入的部分名称
     * @param playerNames 玩家名称列表
     * @return 过滤后的玩家名称列表
     */
    public static List<String> filterPlayerNames(String input, List<String> playerNames) {
        // 如果没有输入或列表为空，返回原列表
        if (input == null || input.isEmpty() || playerNames == null || playerNames.isEmpty()) {
            return playerNames != null ? new ArrayList<>(playerNames) : new ArrayList<>();
        }

        // 使用流式处理进行高效过滤
        String lowerInput = input.toLowerCase();
        return playerNames.stream()
            .filter(name -> name != null && name.toLowerCase().startsWith(lowerInput))
            .collect(Collectors.toList());
    }

    /**
     * 更新在线玩家缓存
     */
    private static void updateOnlinePlayerCache() {
        List<String> newPlayerNames = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            newPlayerNames.add(player.getName());
        }

        // 更新缓存
        cachedOnlinePlayerNames.clear();
        cachedOnlinePlayerNames.addAll(newPlayerNames);

        // 清除过期的前缀缓存
        prefixCache.clear();
    }
}
