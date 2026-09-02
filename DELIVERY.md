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

## 语言文件 MiniMessage 标签修复（86bd9ae）

- **hover 颜色闭合错配**：`<red><bold>…</green>` 误配共 **25 份语言文件 × 3 处 = 75 处**，全部改为 `</red>`。
- **拼写错误**：zh_CN `deny_button` 的 `insert:/tpadeny` → `/tpdeny`（其余 24 份本就正确）。
- **未闭合标签**：全量 strict 解析发现 **1937 行**依赖宽松模式隐式闭合（如 `<green><bold>…<gold><bold>%target%</bold>` 行尾无 `</green>`），按 strict 异常自动逆序补齐。
- **最终校验**：25 份文件 **3626 条值全部通过 MiniMessage strict 解析**（failures=0）；标签开合配对审计 200 条 hover 全部正确；修复后重新打包并冒烟通过（语言文件加载正常、零 ERROR）。
- 说明：宽松模式下未闭合标签会自动闭合、逐条解析亦无样式泄漏（渲染不受影响），本次补齐消除的是对宽松行为的隐式依赖。

## 1.8.8 实测反馈修复（597d41f）

### ① 更新检查 `NoSuchMethodError`（已修复）

- **根因**：`JsonParser.parseString` 是 Gson 2.8.6+ API。编译期 classpath 中 spigot-api 1.8.8 携带的 gson 2.2.4 与 paper-api 携带的新版 gson 发生依赖调解，Gradle 选了高版本 → 编译通过；运行时 1.8.8 服务器只有内嵌 gson 2.2.4 → `NoSuchMethodError`。
- **修复**：`VersionUtil` 弃用 gson，改用正则提取 GitHub API 响应中的 `tag_name`（该场景仅需这一个字段，零依赖最稳）。字节码复核已无任何 gson 引用。

### ② yml 编码统一（已修复）

1.8–1.12 服务器的 `YamlConfiguration.load(File)/save(File)` 与 `JavaPlugin.getConfig` 按 JVM 系统编码（中文 Windows 为 GBK）读写文件，会把 UTF-8 中文读乱/写坏。新增 `utils/YamlIO.kt` 统一封装：

- **读**：显式 UTF-8 解析；检出 U+FFFD 替换符时回退系统编码重读（兼容 3.x 旧档在 1.8 上按系统编码写入的中文）；
- **写**：`saveToString()` 经 UTF-8 输出流落盘，杜绝 `FileWriter` 系统编码覆盖；
- **覆盖范围**：config.yml（覆盖 `getConfig/reloadConfig/saveConfig`）、playerdata、spawn.yml、warp.yml、语言文件、迁移读写全部走 `YamlIO`。

### ③ 控制台中文乱码显示（环境问题，附解决办法）

**机制定位**：模拟复现输出与用户日志**逐字符一致**（`鏈惎鐢ㄦ暟鎹簱` = 「未启用数据库」的 UTF-8 字节被按 GBK 解码），且 GBK 回编可还原原始 UTF-8 字节（负负得正）。即：

- **插件消息字符串本身是正确的**（游戏内聊天不受影响，用户实测 `/tpa`、`/tpaccept` 交互正常）；
- 乱码发生在**服务端控制台输出链**：1.8 服务端 stdout 产出 UTF-8 字节，而终端按 GBK 解码显示。3.x 时代同机显示"正常"是因为旧语言文件恰为系统编码（GBK 字节按 GBK 读再按 GBK 输出还原），4.0 释放全新 UTF-8 语言文件后字符串正确、终端解码不匹配的矛盾显露。

**解决办法（任选其一）**：
1. 启动脚本去掉 `-Dfile.encoding=UTF-8`（终端为 GBK 时），或改为 `-Dfile.encoding=GBK`；
2. 启动前在控制台执行 `chcp 65001` 将终端切到 UTF-8（推荐 Windows Terminal）；
3. 直接查看 `logs/latest.log`（UTF-8 编码，用文本编辑器打开显示正常）。

控制台显示乱码**不影响任何功能**与游戏内消息。

## 1.8.8 RTP 全部失败修复

### ④ RTP 随机点生成全部失败（已修复）

- **现象**：1.8.8 上 `/rtp` 每次都提示生成失败，后台无报错、debug 无输出。
- **根因**：`World.getHighestBlockYAt` 的 y 语义跨版本不同——1.8–1.12 返回 heightMap 值（最高方块**上方一格**，即站立层）；1.13+ 返回最高方块**自身 y**。原代码按现代语义把返回值当"最高方块层"，1.8 上实际拿到的是站立层 → 判地面层 = 空气 → `isSafeStanding` 全列判不安全 → 静默重试耗尽。
- **修复**：主世界扫描改为**双候选层探测**（`scanY+1` 与 `scanY` 各试一次，任一安全即用）——现代语义下第一候选即中，1.8 语义下第二候选命中，对两种语义都正确，无需版本分支。

### ⑤ 黑名单方块解析按版本对照表回退（已修复）

- **现象**：config 的 `rtp.blacklisted_blocks` 全是现代 Material 名，1.8 上 13 个名字解析不到被静默丢弃（如 COBWEB/NETHER_PORTAL 在 1.8 有旧名却没被用上）。
- **修复**：按指定逻辑重构 `resolveMaterial`——读到当前版本没有的方块名时**先查新旧名对照表**（MAGMA_BLOCK→MAGMA[1.10-1.12]、NETHER_PORTAL→PORTAL、COBWEB→WEB、END_PORTAL→ENDER_PORTAL），**低版本没有对应方块则直接略过**（营火/细雪/滴水石锥等 1.8 确实不存在）。已用 1.8.8 spigot-api 的真实 Material 枚举逐项验证：18 项 = 8 项解析成功（含 3 项对照表换名）+ 10 项安全略过，无异常、无意外陆地方块混入。

### ⑥ 1.8 下界 RTP 概率性传送进岩浆（已修复）

- **根因**：1.8–1.12 的水/岩浆分"流动/静止"两种 Material（`LAVA`/`STATIONARY_LAVA`、`WATER`/`STATIONARY_WATER`，1.13+ 合并）。黑名单只有流动形态；`isSafeStanding` 的地面层靠 `isSolid` 挡住了静止岩浆，但**脚/头层的静止岩浆 `isSolid=false` 且不在黑名单** → 漏判。下界岩浆海边缘"固体地面 + 脚层静止岩浆"的列型常见，故概率性触发。
- **修复**：`expandLegacyLiquids`——黑名单命中 WATER/LAVA 任一形态时自动扩展对应 `STATIONARY_*` 形态（新版服务器该枚举不存在，解析为 null 自然跳过）。已用 1.8.8 枚举验证四种液体 Material 均存在且 `isSolid=false`。

### ⑦ 1.8 末地 RTP 百分百失败（已修复）

- **根因**：末地走自低向高柱扫描，逻辑本身正确；但 1.8–1.15 的末地**只有小主岛、无外岛**（探针佐证：1.8 连 `END_STONE` 都还没有，1.9 才改入），随机范围 ±limit（默认数千格）内的随机点几乎必然落在虚空列——整列 ground/feet/head 全为空气，5 次尝试全部落空。
- **修复**：末地的重试范围以玩家为中心**逐次减半**（`limit/2`，下限 16），让后续尝试收敛到有地形的区域；下界地形连续、主世界取地表最高点，均不收缩。5 次默认尝试下范围收敛到 1/16，配合 attempt 内 effLimit 实时计算正常生效。



