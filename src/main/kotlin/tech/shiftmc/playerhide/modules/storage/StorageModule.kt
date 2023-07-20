package tech.shiftmc.playerhide.modules.storage

import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import tech.shiftmc.playerhide.PlayerHide.Companion.instance
import tech.shiftmc.playerhide.modules.Module

class StorageModule : Module, Listener {

    override fun enable() {
        // Registering the listener
        instance.server.pluginManager.registerEvents(this, instance)
    }

    override fun disable() {
        // Unregistering the listener
        PlayerManager.removeAll()
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        instance.launch {
            PlayerManager.addPlayer(player.name)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        PlayerManager.removePlayer(player.name)
    }
}