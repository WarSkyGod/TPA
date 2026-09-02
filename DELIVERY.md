# TPA 4.0 向下兼容至 Minecraft 1.8.8 — 交付说明

## 交付物

- **插件包**：`build/libs/TPA-4.0.0.jar`（shadowJar，3.73 MB，Java 8 字节码 major 52）
- **分支**：`4.0-Refactoring`，本地提交 `08a2c6704e67f3e87243a21b81def85ede8d95d3`（**未 push**）
- **验证环境**：Paper 26.2 build 121 冒烟通过；1.8.8 由编译期兜底 + 代码分层保证

## 兼容方案总览

| 层 | 方案 |
| --- | --- |
| 命令路由 | 全版本统一 plugin.yml 声明 + `LegacyCommandRouter` 传统注册（Paper 1.19+ 自动桥接进 Brigadier 树）；21 个命令统一 `executeXxx(sender: CommandSender, args: List<String>)` 签名 |
| 消息渲染 | Adventure 桥接（adventure-platform-bukkit 4.4.1，shade + relocate 到 `top.craft_hello.tpa.libs.kyori`），1.8.8+ 经 BungeeCord chat 序列化，点击/悬浮交互完整保留 |
| API 分层 | `TpaVersion` 阈值：supportsBrigadier(1.20.6+) / supportsLocaleEvent(1.12+) / supportsAsyncChunk(1.13+) / supportsWorldMinHeight(1.17+) / legacySoundNames(1.8) |
| 音效 | `loadSound` 四候选解析（配置名→默认名→旧名→旧默认名），1.8 回退 `EXPERIENCE_ORB_PICKUP`/`ENDERMAN_TELEPORT`/`VILLAGER_NO`/`NOTE_BASS` 等 |
| 材料 | `resolveMaterial(vararg alias)` 运行时解析（MAGMA_BLOCK→MAGMA、NETHER_PORTAL→PORTAL、COBWEB→WEB 等），1.8 无的方块安全跳过 |
| RTP | getChunkAtAsync(1.13+) 与主线程同步扫描双路径；worldMinHeight 反射（1.17+）；`Material.isSolid` 替代 Paper `Block.isSolid` |
| 语言跟随 | `LocaleUtil.playerLocale` 反射双路径（Bukkit getLocale 字符串 / Paper locale() Locale），1.8-1.11 安全返回 null 回退默认 |
| 传送 | Paper teleportAsync 反射调用（1.13+），否则同步调度传送 |
| 玩家数据 | Location 手工分键双写（3.x 格式互通），`YamlConfiguration.getLocation`(1.13+) 反射兜底读取 == 序列化格式 |
| 构建兜底 | classpath 同时挂 spigot-api 1.8.8（优先）与 paper-api 26.1.2（ComponentMetadataRule 移除 capability 冲突），误用 1.8 不存在的 API 直接编译失败 |

## 验证证据

1. **构建**：`gradle build` BUILD SUCCESSFUL，仅 2 个无害警告（deprecated getLocale / unchecked cast）。
2. **字节码**：javap 验证 `TPA.class` major version 52（Java 8），1.8 JVM 可加载。
3. **冒烟（Paper 26.2）**：插件启用 → `[TPA] 插件已成功加载！` → 控制台执行 `tpa`/`tpac reload`/`warp`/`denys` 全部有预期响应（含控制台限制分支）→ `stop` 后 `[TPA] 插件已成功卸载！`，全程零 `[TPA] ERROR`。
4. **jar 审计**：kyori 全部 relocate；`com.mojang.brigadier` 保持原签名（与 Paper API 一致）；包内含 `plugin.yml`，不含 `paper-plugin.yml`。
5. **工作区**：untracked=0 / modified=0 / missing=0，提交历史接续 `9041501`（i18n）。
