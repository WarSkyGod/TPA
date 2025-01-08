package top.craft_hello.tpa.utils;

import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.Messages;
import top.craft_hello.tpa.TPA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;


/*
* 版本更新检查工具
* 代码来源：EasyLibrary
* 代码原作者：@ZhangYang0204
* 作者Github：https://github.com/ZhangYang0204
* https://github.com/ZhangYang0204/easylibrary (现已不存在)
* Fork：https://github.com/WarSkyGod/easy-gui-shop
* */
public class VersionUtil {
   public static @Nullable String readFirstLine(URL url) throws IOException {
      if (url == null) {
         throw new NullPointerException();
      } else {
         InputStream is = url.openStream();
         BufferedReader br = new BufferedReader(new InputStreamReader(is));
         return br.readLine();
      }
   }

   public static void updateCheck(final CommandSender sender) {
      final String latestVersion;
      String latestVersion1;
      FileConfiguration config = LoadingConfigFileUtil.getConfig();
      String version = Objects.requireNonNull(config.getString("version"));
      PluginMeta pluginMeta = TPA.getPlugin(TPA.class).getPluginMeta();
      try {
         String urlStringPart1 = "https://warskygod.github.io/";
         String urlStringPart2 =  pluginMeta.getName();
         String urlStringPart3 =  "/latest.html";
         latestVersion1 = readFirstLine(new URL(urlStringPart1 + urlStringPart2 + urlStringPart3));
      } catch (Throwable var5) {
         latestVersion1 = null;
      }

      latestVersion = latestVersion1;

      if (latestVersion != null) {
         int serverBigVersion = getPluginBigVersion(version);
         int serverMiddleVersion = getPluginMiddleVersion(version);
         int serverSmallVersion = getPluginSmallVersion(version);
         int latestBigVersion = getPluginBigVersion(latestVersion);
         int latestMiddleVersion = getPluginMiddleVersion(latestVersion);
         int latestSmallVersion = getPluginSmallVersion(latestVersion);
         if (isOlderThan(serverBigVersion, serverMiddleVersion, serverSmallVersion, latestBigVersion, latestMiddleVersion, latestSmallVersion)){
            Messages.pluginUpdateMessage(sender, latestVersion);
         }
      }
   }

   public static int getPluginBigVersion(@NotNull String version) {
      return Integer.parseInt(version.split("\\.")[0]);
   }

   public static int getPluginMiddleVersion(@NotNull String version) {
      return Integer.parseInt(version.split("\\.")[1]);
   }

   public static int getPluginSmallVersion(@NotNull String version) {
      return Integer.parseInt(version.split("\\.")[2]);
   }

   public static boolean isOlderThan(int currentBig, int currentMiddle, int currentSmall, int big, int middle, int small) {
      if (currentBig > big) {
         return false;
      } else if (currentBig < big) {
         return true;
      } else if (currentMiddle > middle) {
         return false;
      } else if (currentMiddle < middle) {
         return true;
      } else {
         return currentSmall < small;
      }
   }

   public static boolean isOlderThan(int currentBig, int currentMiddle, int big, int middle) {
      if (currentBig > big) {
         return false;
      } else if (currentBig < big) {
         return true;
      } else {
         return currentMiddle < middle;
      }
   }
}
