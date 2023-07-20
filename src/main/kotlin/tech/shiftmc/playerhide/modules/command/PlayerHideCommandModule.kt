package tech.shiftmc.playerhide.modules.command

import com.github.shynixn.mccoroutine.bukkit.launch
import com.google.gson.Gson
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import tech.shiftmc.playerhide.PlayerHide.Companion.instance
import tech.shiftmc.playerhide.modules.Module
import tech.shiftmc.playerhide.modules.storage.PerformanceLevel
import tech.shiftmc.playerhide.modules.storage.PlayerManager

class PlayerHideCommandModule : Module, CommandExecutor, TabCompleter {

    override fun enable() {
        instance.getCommand("performance")?.setExecutor(this)
        instance.getCommand("performance")?.tabCompleter = this
    }

    override fun disable() {
        instance.getCommand("performance")?.setExecutor(null)
        instance.getCommand("performance")?.tabCompleter = null
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cOnly players can use this command.")
            return true
        }

        instance.launch {
            val setting = PlayerManager.getPlayer(sender.name)

            if (args.isNullOrEmpty()) {
                sender.sendMessage("§cUsage: /playerhide <performanceLevel>")
                return@launch
            }

            val performanceLevel = PerformanceLevel.entries.firstOrNull { it.name.equals(args[0], true) } ?: run {
                sender.sendMessage("§cInvalid performance level.")
                return@launch
            }

            sender.sendMessage("§aPerformance level set to ${performanceLevel}.")
            setting.setPerformanceLevel(performanceLevel)
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>?
    ): MutableList<String>? {
        if (sender !is Player) return null
        if (args.isNullOrEmpty()) return PerformanceLevel.entries.sortedByDescending { it.visiblePlayers }.map { it.name }.toMutableList()
        return PerformanceLevel.entries
            .sortedByDescending { it.visiblePlayers }
            .map { it.name }
            .filter { it.startsWith(args[0], true) }
            .toMutableList()
    }
}