package com.github.soldam.item

import com.github.grassproject.grassLib.api.item.ItemUtils
import com.github.grassproject.grassLib.api.utilities.BukkitUtils
import com.github.grassproject.grassLib.api.utilities.component.toMiniMessage
import com.github.soldam.SoldamPlugin
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ItemParser(private val section: ConfigurationSection) {

    private val type: Material = run {
        val materialName = section.getString("material") ?: "STONE"
        ItemUtils.createItem(materialName)?.type ?: Material.STONE
    }

    fun buildItem(): ItemBuilder {
        val item = ItemBuilder(type)
        return applyConfig(item)
    }

    fun applyConfig(item: ItemBuilder): ItemBuilder {
        val materialName = section.getString("material") ?: "STONE"
//        ItemUtils.createItem(materialName)?.let {
//            item.setType(it)
//        }
        item.setType(type)

        section.getString("item_name")?.takeIf { it.isNotBlank() }?.let { name ->
            val component = name.toMiniMessage()
            if (BukkitUtils.isVersionAtOrAbove("1.20.5")) {
                item.setItemName(component)
            } else {
                item.setDisplayName(component)
            }
        }

        section.getStringList("lore").let { loreList ->
            item.setLore(loreList.map { it.toMiniMessage() })
        }

        section.getBoolean("unbreakable", false).let { item.setUnbreakable(it) }
        section.getBoolean("unstackable", false).let { item.setUnstackable(it) }

        section.getInt("model_data", 0).takeIf { it > 0 }?.let { modelData ->
            val isCustomItem = materialName.contains(":") && !materialName.contains("MINECRAFT:")
            item.setCustomModelData(if (isCustomItem) -1 else modelData)
        }

        parseDataComponents(item)
        parseVanillaSections(item)

        return item
    }

    fun parseDataComponents(item: ItemBuilder) {
        val components = section.getConfigurationSection("Components") ?: return
        if (!BukkitUtils.isVersionAtOrAbove("1.20.5")) return
        handleLegacyComponents(item, components)
    }

    fun handleLegacyComponents(item: ItemBuilder, components: ConfigurationSection) {
        components.getBoolean("durability.damage_block_break").let {
            item.setDamagedOnBlockBreak(it)
        }
        components.getBoolean("durability.damage_entity_hit").let {
            item.setDamagedOnEntityHit(it)
        }
        maxOf(components.getInt("durability.value"), components.getInt("durability", 1)).let {
            item.setDurability(it)
        }

        components.getBoolean("hide_tooltip", false).let { item.setHideTooltip(it) }

        // components.getConfigurationSection("food").let { item.setFoodComponent(it) }

        components.getConfigurationSection("tool")?.let { toolSection ->
            parseToolComponent(item, toolSection)
        }

        if (!BukkitUtils.isVersionAtOrAbove("1.21.2")) return

        components.getConfigurationSection("use_cooldown")?.let {
            runCatching {
                ItemStack(Material.PAPER).itemMeta?.useCooldown?.apply {
//                    cooldownGroup = NamespacedKey.fromString(
//                        it.getString("group") ?: "grassrpgitem:${RPGItems.getIdByItem(item)}"
//                    )
                    cooldownSeconds = maxOf(it.getDouble("seconds", 1.0), 0.0).toFloat()
                    item.setUseCooldownComponent(this)
                }
            }.onFailure { e ->
                SoldamPlugin.instance.logger.warning("Error setting UseCooldownComponent: This component is not available in your server version")
                // if (Settings.DEBUG.toBool()) e.printStackTrace()
            }
        }

        components.getString("tooltip_style")?.let(NamespacedKey::fromString)?.let(item::setTooltipStyle)

    }

    fun parseVanillaSections(item: ItemBuilder) {
        section.getStringList("ItemFlags").mapNotNull {
            runCatching { ItemFlag.valueOf(it.uppercase()) }.getOrNull()
        }.takeIf { it.isNotEmpty() }?.let { item.addItemFlags(*it.toTypedArray()) }

        section.getConfigurationSection("enchantments")?.let { enchSection ->
            enchSection.getKeys(false).forEach { enchName ->
                val key = NamespacedKey.minecraft(enchName.lowercase())
                val enchant = RegistryAccess.registryAccess()
                    .getRegistry(RegistryKey.ENCHANTMENT)
                    .get(key)

                if (enchant != null) {
                    item.addEnchant(enchant, enchSection.getInt(enchName, 1))
                }
            }
        }
    }

    private fun parseToolComponent(item: ItemBuilder, toolSection: ConfigurationSection) {
        val toolComponent = ItemStack(type).itemMeta.tool
        toolComponent.damagePerBlock = toolSection.getInt("damage_per_block", 1).coerceAtLeast(0)
        toolComponent.defaultMiningSpeed =
            toolSection.getDouble("default_mining_speed", 1.0).toFloat().coerceAtLeast(0f)

        toolSection.getMapList("rules").forEach { rule ->
            val map = rule as? Map<*, *> ?: return@forEach
            val speed = map["speed"].toString().toFloatOrNull() ?: 1f
            val correctForDrops = map["correct_for_drops"].toString().toBoolean()

            val materials = (map["materials"] as? List<*>)?.mapNotNull {
                runCatching { Material.valueOf(it.toString()) }.getOrNull()?.takeIf { it.isBlock }
            }?.toMutableSet() ?: mutableSetOf()

            map["material"]?.toString()?.let {
                runCatching { Material.valueOf(it) }.getOrNull()?.takeIf { it.isBlock }?.let(materials::add)
            }

            val tags = (map["tags"] as? List<*>)?.mapNotNull {
                NamespacedKey.fromString(it.toString())?.let { key ->
                    Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material::class.java)
                }
            }?.toMutableSet() ?: mutableSetOf()

            map["tag"]?.toString()?.let {
                NamespacedKey.fromString(it)?.let { key ->
                    Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material::class.java)?.let(tags::add)
                }
            }

            if (materials.isNotEmpty()) toolComponent.addRule(materials, speed, correctForDrops)
            tags.forEach { tag -> toolComponent.addRule(tag, speed, correctForDrops) }
        }

        item.setToolComponent(toolComponent)
    }
}