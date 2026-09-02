plugins {
    kotlin("jvm") version "2.3.21"
    id("com.gradleup.shadow") version "9.4.1"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "top.craft_hello"
version = "4.0.0"

repositories {
    maven("https://mirrors.tuna.tsinghua.edu.cn/maven/repos/public") {
        name = "清华大学开源软件镜像站"
    }
    maven("https://maven.aliyun.com/repository/public") {
        name = "阿里云开源镜像站"
    }
    mavenCentral()
    // Spigot 1.8.8 API（编译期版本兜底：确保代码只用 1.8 就存在的 Bukkit API）
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://nexus.handyplus.cn/releases") {
        name = "handy-repository"
    }
    // PlaceholderAPI（可选依赖，仅编译期）
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        name = "placeholderapi-repo"
    }
    // Vault（可选经济前置，仅编译期）
    maven("https://jitpack.io") {
        name = "jitpack-repo"
    }
    // PlayerPoints（可选点券前置，仅编译期，官方仓库）
    maven("https://repo.rosewooddev.io/repository/public/") {
        name = "rosewood-repo"
    }
    // Geyser/Floodgate（可选基岩互通前置，仅编译期，官方仓库）
    maven("https://repo.opencollab.dev/main/") {
        name = "opencollab-repo"
    }
}

// paper-api 26.x 元数据声明了 capability "org.spigotmc:spigot-api"，与显式依赖的
// spigot-api 1.8.8 互斥；移除该 capability 使两者共存：
// classpath 顺序 spigot 1.8.8 在前 → org.bukkit.* 优先解析 1.8 API（编译期兜底），
// paper-api 补充 io.papermc.* 与新版本 org.bukkit.* 类（供 Brigadier 层/版本分支编译）
abstract class RemovePaperSpigotCapabilityRule : ComponentMetadataRule {
    override fun execute(context: ComponentMetadataContext) {
        if (context.details.id.group == "io.papermc.paper" && context.details.id.name == "paper-api") {
            context.details.allVariants {
                withCapabilities {
                    removeCapability("org.spigotmc", "spigot-api")
                }
            }
        }
    }
}

dependencies {
    components.all<RemovePaperSpigotCapabilityRule>()
}

dependencies {
    // 编译期版本兜底：先声明 spigot 1.8.8，org.bukkit.* 优先解析到 1.8 API（误用高版本 API 会直接编译失败）
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    // Paper API 仅提供 Brigadier 命令 API（io.papermc.paper.command.brigadier.*），
    // 运行时仅在 Paper 1.20.6+ 上通过独立注册器触达，低版本走 legacy 路由不会加载这些类
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("cn.handyplus.lib.adapter:FoliaLib:1.3.0")
    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation("com.zaxxer:HikariCP:4.0.3")
    // Brigadier 纯 Java 库：随包分发（不 relocate，须与 Paper API 签名一致），
    // 1.8-1.20.4 服务器走 legacy 路由，Brigadier 相关类不会被加载
    implementation("com.mojang:brigadier:1.2.9")
    // Adventure/MiniMessage 桥接：shade + relocate 后与 Paper 原生 Adventure 完全隔离，
    // 1.8.8+ 全版本统一走 BukkitAudiences（低版本经 BungeeCord chat 序列化，点击/悬浮交互完整保留）
    implementation("net.kyori:adventure-text-minimessage:4.21.0")
    implementation("net.kyori:adventure-platform-bukkit:4.4.1")
    // PlaceholderAPI 可选依赖：仅编译期，运行时由服务器按需注入
    compileOnly("me.clip:placeholderapi:2.11.6")
    // Vault / PlayerPoints 可选前置：仅编译期，运行时由服务器按需注入
    // VaultAPI 经 JitPack；PlayerPoints 坐标按官方 Wiki（org.black_ixx:playerpoints）
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1") { isTransitive = false }
    compileOnly("org.black_ixx:playerpoints:3.3.5") { isTransitive = false }
    // Floodgate/Cumulus 可选前置：仅编译期，基岩玩家弹窗交互（表单版本与 Floodgate 2.2.x 配套）
    compileOnly("org.geysermc.floodgate:api:2.2.5-SNAPSHOT") { isTransitive = false }
    compileOnly("org.geysermc.cumulus:cumulus:1.1.2") { isTransitive = false }
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("26.2")
        jvmArgs = listOf(
            "-Dfile.encoding=UTF-8",
            "-Dconsole.encoding=UTF-8",
            "-Dsun.stdout.encoding=UTF-8",
            "-Dsun.stderr.encoding=UTF-8"
        )

        environment("LANG", "en_US.UTF-8")
        environment("LC_ALL", "en_US.UTF-8")
    }

    shadowJar {
        // 不用 minimize()：adventure-platform 的 NMS 适配器为反射实例化，
        // 静态引用分析会误删低版本服务器的适配类
        relocate("org.bstats", "${project.group}.tpa.other.bstats")
        relocate("cn.handyplus.lib.adapter", "${project.group}.tpa.other.FoliaLib")
        relocate("net.kyori", "${project.group}.tpa.libs.kyori")
        archiveFileName.set("${project.name}-${project.version}.jar")
        exclude("META-INF/**")
    }
}

// 运行时最低 Java 8（MC 1.8.8 服务器）；编译仍用 JDK 25 工具链
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

// paper-api 26.x 元数据声明仅兼容 JVM 25，与本工程 1.8 目标属性冲突；
// 仅放宽"编译期"classpath 的目标 JVM 属性以允许解析（运行时依赖仍按 Java 8 严格解析，
// 保证 shadowJar 打包的库全部 Java 8 兼容）
configurations.compileClasspath {
    attributes {
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
        javaParameters.set(true)
    }
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching(listOf("plugin.yml", "config.yml")) {
        expand(props)
    }
}
