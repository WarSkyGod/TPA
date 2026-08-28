package top.craft_hello.tpa.datas

import org.bukkit.Location
import java.util.UUID

// 玩家数据：家、黑名单、语言、位置记录。
// 3.x 以 playerdata/<uuid>.yml 存储，4.0 默认沿用该格式（YamlPlayerDataStore），
// 开启 use_database 后改用数据库存储（DatabasePlayerDataStore），两者可互换。
class PlayerData(val uuid: UUID) {
    var playerName: String? = null
    var language: String? = null
    var setlang: Boolean = false
    var defaultHomeName: String? = null
    val homes = LinkedHashMap<String, Location>()
    val denyList = ArrayList<String>()
    var lastLocation: Location? = null
    var logoutLocation: Location? = null

    fun getHome(name: String): Location? = homes[name]

    fun setHome(name: String, location: Location) {
        homes[name] = location
    }

    fun delHome(name: String): Boolean {
        val removed = homes.remove(name) != null
        // 删除的是默认家：默认家转移到剩下的下一个家，直到没有可用的家为止
        if (removed && defaultHomeName == name) {
            defaultHomeName = if (homes.isEmpty()) null else homes.keys.first()
        }
        return removed
    }

    fun homeNames(): List<String> = homes.keys.toList()

    fun isDeny(uuid: String): Boolean = denyList.contains(uuid)

    fun addDeny(uuid: String): Boolean {
        if (denyList.contains(uuid)) return false
        denyList.add(uuid)
        return true
    }

    fun removeDeny(uuid: String): Boolean = denyList.remove(uuid)

    fun equalsDefaultHomeName(name: String): Boolean = defaultHomeName == name


}
