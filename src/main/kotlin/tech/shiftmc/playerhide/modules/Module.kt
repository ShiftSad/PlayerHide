package tech.shiftmc.playerhide.modules

import tech.shiftmc.playerhide.logger
import tech.shiftmc.playerhide.modules.command.PlayerHideCommandModule
import tech.shiftmc.playerhide.modules.playerhide.PlayerHideModule
import tech.shiftmc.playerhide.modules.storage.StorageModule
import java.util.logging.Level.SEVERE
import kotlin.system.measureTimeMillis

interface Module {
    fun enable()
    fun disable()
}

object ModuleManager {
    private val modules = mutableListOf(
        PlayerHideCommandModule(),
        PlayerHideModule(),
        StorageModule()
    )

    fun loadModules() = modules.forEach { module ->
        runCatching { val time = measureTimeMillis { module.enable() }; logger.info("${module.javaClass.simpleName} carregado com sucesso em ${time}ms!") }
            .onFailure { logger.log(SEVERE, "Erro ao INICIAR ${module.javaClass.simpleName}\n${it.stackTrace}") }
    }

    fun unloadModules() = modules.forEach { module ->
        runCatching { module.disable() }
            .onFailure { logger.log(SEVERE, "Erro ao ENCERRAR ${module.javaClass.simpleName}\n${it.stackTrace}") }
    }
}