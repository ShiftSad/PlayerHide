package tech.shiftmc.playerhide.modules.playerhide

import com.comphenix.protocol.PacketType
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.delay
import org.bukkit.entity.Player
import tech.shiftmc.playerhide.PlayerHide.Companion.instance
import tech.shiftmc.playerhide.modules.Module
import tech.shiftmc.playerhide.modules.storage.PerformanceLevel
import tech.shiftmc.playerhide.modules.storage.PlayerManager

class PlayerHideModule : Module {

    private var enabled = false
    private val playerVisibilityMap = mutableMapOf<Player, MutableList<Player>>()

    override fun enable() {
        enabled = true
        instance.launch {
            while (enabled) {
                val onlinePlayers = instance.server.onlinePlayers
                val adminPlayers = onlinePlayers.filter { it.hasPermission("playerhide.admin") }

                onlinePlayers.forEach { player ->
                    val setting = PlayerManager.getPlayer(player.name)
                    playerVisibilityMap[player] ?: run {
                        playerVisibilityMap[player] = mutableListOf()
                        return@forEach
                    }

                    // If the player changed the performance level, we need to update the visibility
                    playerVisibilityMap[player] = playerVisibilityMap[player]!!.take(setting.getPerformanceLevel().visiblePlayers).toMutableList()

                    val nearPlayers = nearPlayers(player, 1000, 30.0)
                    playerVisibilityMap[player]!!.apply {
                        // Admin should not count towards the limit
                        filterNot { it.hasPermission("playerhide.admin") }
                        retainAll { it in nearPlayers }
                    }

                    playerVisibilityMap[player]!!.addAll(nearPlayers.filter {
                        it !in playerVisibilityMap[player]!! && !it.hasPermission("playerhide.admin")
                    }.take(setting.getPerformanceLevel().visiblePlayers - playerVisibilityMap[player]!!.size))
//                    player.sendMessage("§aShowing ${playerVisibilityMap[player]!!.size} players.\n${setting.getPerformanceLevel().visiblePlayers - playerVisibilityMap[player]!!.size} slots left.}")

                    // Add admin players, if they are not already in the list
                    val playerToShowList = (playerVisibilityMap[player]!! + adminPlayers).toMutableList()
//                    player.sendMessage("§aShowing ${playerToShowList.size} players.\n${setting.getPerformanceLevel().visiblePlayers - playerToShowList.size} slots left.}")
                    onlinePlayers
                        .filterNot { it in playerToShowList }
                        .forEach { playerToHide -> player.hidePlayer(instance, playerToHide) }
                    playerToShowList.forEach { playerToShow -> player.showPlayer(instance, playerToShow) }
                }

                delay(1000)
            }
        }
    }

    override fun disable() {
        enabled = false
    }

    private fun nearPlayers(player: Player, limit: Int, maxDistance: Double): List<Player> {
        return player.world.players
            .filter { p ->
                p !== player && p.location.distance(player.location) <= maxDistance
            }
            .sortedBy { p ->
                p.location.distance(player.location)
            }
            .take(limit)
    }
}