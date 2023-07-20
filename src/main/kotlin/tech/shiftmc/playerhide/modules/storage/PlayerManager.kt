package tech.shiftmc.playerhide.modules.storage

object PlayerManager {

    private val settings = mutableMapOf<String, PlayerSetting>()

    suspend fun addPlayer(name: String) {
        if (settings.containsKey(name)) return
        settings[name] = PlayerSetting.load(name)
    }

    suspend fun getPlayer(name: String): PlayerSetting {
        if (!settings.containsKey(name)) addPlayer(name)
        return settings[name]!!
    }

    fun removePlayer(name: String) {
        settings.remove(name)
    }

    fun removeAll() {
        settings.clear()
    }
}