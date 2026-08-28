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
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("cn.handyplus.lib.adapter:FoliaLib:1.3.0")
    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation("com.zaxxer:HikariCP:4.0.3")
    // PlaceholderAPI 可选依赖：仅编译期，运行时由服务器按需注入
    compileOnly("me.clip:placeholderapi:2.11.6")
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("26.1.2")
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
        minimize()
        relocate("org.bstats", "${project.group}.tpa.other.bstats")
        relocate("cn.handyplus.lib.adapter", "${project.group}.tpa.other.FoliaLib")
        archiveFileName.set("${project.name}-${project.version}.jar")
        exclude("META-INF/**")
    }
}

val targetJavaVersion = 25
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching(listOf("paper-plugin.yml", "config.yml")) {
        expand(props)
    }
}
