package com.github.soldam

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import org.bukkit.plugin.java.JavaPlugin

class SoldamPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: SoldamPlugin
            private set
    }

    init {
        instance = this
    }

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).silentLogs(true).skipReloadDatapacks(true))
    }

    override fun onEnable() {
        CommandAPI.onEnable()
    }

    override fun onDisable() {
        CommandAPI.onDisable()
    }

}