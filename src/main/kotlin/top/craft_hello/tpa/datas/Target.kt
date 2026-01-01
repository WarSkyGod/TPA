package top.craft_hello.tpa.datas

import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

data class Target(val location : Location?) {
    lateinit var players : MutableList<Player>
    constructor(location: Location?, vararg offlinePlayers: OfflinePlayer) : this(location) {
        for (offlinePlayer in offlinePlayers) if (offlinePlayer is Player) players.add(offlinePlayer)
    }
}