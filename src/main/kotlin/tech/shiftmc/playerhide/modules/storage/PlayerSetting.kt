package tech.shiftmc.playerhide.modules.storage

import com.github.shynixn.mccoroutine.bukkit.launch
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.shiftmc.playerhide.PlayerHide.Companion.instance
import java.io.File

data class PlayerSetting(
    val name: String,
    private var performanceLevel: PerformanceLevel = PerformanceLevel.MEDIUM,
) {
    fun getPerformanceLevel() = performanceLevel
    fun setPerformanceLevel(performanceLevel: PerformanceLevel) {
        this.performanceLevel = performanceLevel
        instance.launch { update() }
    }

    private suspend fun update() =
        withContext(Dispatchers.IO) {
            val path = File(instance.dataFolder.path, "settings")
            if (!path.exists()) path.mkdirs()

            val file = File(path, "$name.json")
            if (!file.exists()) file.createNewFile()

            file.writeText(Gson().toJson(this@PlayerSetting))
        }

    companion object {
        suspend fun load(name: String): PlayerSetting =
            withContext(Dispatchers.IO) {
                val path = File(instance.dataFolder.path, "settings")
                if (!path.exists()) path.mkdirs()

                val file = File(path, "$name.json")
                if (!file.exists()) {
                    file.createNewFile()
                    file.writeText(Gson().toJson(PlayerSetting(name)))
                }

                return@withContext Gson().fromJson(file.readText(), PlayerSetting::class.java)
            }
    }
}

/**
 * Represents different performance levels that determine the amount of visible players to the user.
 *
 * The `visiblePlayers` parameter specifies the number of players that should be visible to the user, excluding admins.
 * If the value is set to -1, it means that all players are visible.
 *
 * @param visiblePlayers The number of players that should be visible to the user.
 */
enum class PerformanceLevel(val visiblePlayers: Int) {
    POTATO(0),
    LOW(10),
    MEDIUM(20),
    HIGH(50),
    EXTREME(500)
}