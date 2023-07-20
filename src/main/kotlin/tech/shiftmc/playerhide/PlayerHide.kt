package tech.shiftmc.playerhide

import org.bukkit.plugin.java.JavaPlugin
import tech.shiftmc.playerhide.modules.ModuleManager
import java.util.logging.Logger

@Suppress("unused")
class PlayerHide : JavaPlugin() {

    companion object {
        lateinit var instance: PlayerHide
    }

    override fun onEnable() {
        instance = this
        ModuleManager.loadModules()
    }

    override fun onDisable() {
        ModuleManager.unloadModules()
    }
}

val logger: Logger = Logger.getLogger("PlayerHide")