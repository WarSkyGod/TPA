package top.craft_hello.tpa.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.exceptions.CannotGetTheLatestVersionErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import static java.util.Objects.isNull;


/*
* 版本更新检查工具
* 代码来源：EasyLibrary
* 代码原作者：@ZhangYang0204
* 作者Github：https://github.com/ZhangYang0204
* https://github.com/ZhangYang0204/easylibrary (现已不存在)
* Fork：https://github.com/WarSkyGod/easy-gui-shop
* */
public class VersionUtil {
   private static String pluginName;
   private static String pluginVersion;


   public static void init(Plugin plugin) {
      PluginDescriptionFile description = plugin.getDescription();
      pluginName = description.getName();
      pluginVersion = description.getVersion();
   }


   public static @Nullable String readFirstLine(URL url) throws IOException {
      if (isNull(url)) {
         throw new NullPointerException();
      } else {
         InputStream is = url.openStream();
         BufferedReader br = new BufferedReader(new InputStreamReader(is));
         return br.readLine();
      }
   }

   public static void updateCheck(CommandSender sender)  {
      SendMessageUtil.checkUpdate(sender);
      final String latestVersion;
      String latestVersion1;
      try {
         String urlStringPart1 = "https://warskygod.github.io/";
         String urlStringPart2 =  pluginName;
         String urlStringPart3 =  "/latest.html";
         latestVersion1 = readFirstLine(new URL(urlStringPart1 + urlStringPart2 + urlStringPart3));
      } catch (Throwable var5) {
         throw new CannotGetTheLatestVersionErrorException(sender);
      }

      latestVersion = latestVersion1;

      if (versionComparison(pluginVersion, latestVersion)) {
         SendMessageUtil.pluginUpdateMessage(sender, latestVersion);
         return;
      }
      SendMessageUtil.pluginLatestVersion(sender);
   }

   public static String getPluginVersion() {
      return pluginVersion;
   }

   public static int getPluginBigVersion(@NotNull String version) {
      String[] versions = version.split("\\.");
      return Integer.parseInt(versions.length >= 1 ? versions[0] : "0");
   }

   public static int getPluginMiddleVersion(@NotNull String version) {
      String[] versions = version.split("\\.");
      return Integer.parseInt(versions.length >= 2 ? versions[1] : "0");
   }

   public static int getPluginSmallVersion(@NotNull String version) {
      String[] versions = version.split("\\.");
      return Integer.parseInt(versions.length == 3 ? versions[2] : "0");
   }

   public static boolean versionComparison(String version1, String version2){
      int version1Big = getPluginBigVersion(version1);
      int version1Middle = getPluginMiddleVersion(version1);
      int version1Small = getPluginSmallVersion(version1);
      int version2Big = getPluginBigVersion(version2);
      int version2Middle = getPluginMiddleVersion(version2);
      int version2Small = getPluginSmallVersion(version2);
      return isOlderThan(version1Big, version1Middle, version1Small, version2Big, version2Middle, version2Small);
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
}
