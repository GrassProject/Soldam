package com.github.soldam.item

import com.github.grassproject.grassLib.api.item.ItemUtils
import com.github.grassproject.grassLib.api.utilities.BukkitUtils
import com.github.soldam.SoldamPlugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.components.FoodComponent
import org.bukkit.inventory.meta.components.UseCooldownComponent
import java.util.*

class ItemBuilder {

    private val itemStack: ItemStack
    private val itemMeta: ItemMeta

    constructor(material: Material) {
        this.itemStack = ItemStack(material)
        this.itemMeta = itemStack.itemMeta
    }

    constructor(itemStack: ItemStack) {
        this.itemStack = itemStack.clone()
        this.itemMeta = this.itemStack.itemMeta
    }

    constructor(itemId: String) : this(ItemUtils.createItem(itemId) ?: ItemStack(Material.STONE))

//    fun setType(type: Material): ItemBuilder {
//        itemStack.type
//    }

    fun setAmount(amount: Int): ItemBuilder {
        this.itemStack.amount = amount
        return this
    }

    //    fun setName(name: Component): ItemBuilder {
//        if (itemMeta.hasDisplayName()) itemMeta.displayName(name)
//        if (BukkitUtils.isVersionAtOrAbove("1.20.5")) {
//            if (itemMeta.hasItemName()) itemMeta.itemName(name) // else itemMeta.displayName(name)
//        }
//        return this
//    }
    fun setName(itemName: Component): ItemBuilder {
        val styledName = itemName.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .colorIfAbsent(NamedTextColor.WHITE)

        setDisplayName(styledName)
        if (BukkitUtils.isVersionAtOrAbove("1.20.5")) {
            setItemName(styledName)
        }
        return this
    }


    fun setDisplayName(name: Component): ItemBuilder {
        itemMeta.displayName(name)
        return this
    }

    fun setLore(lore: List<Component>): ItemBuilder {
        val styledLore = lore.map { it.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE) }
        itemMeta.lore(styledLore)
        return this
    }

    fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
        itemMeta.isUnbreakable = unbreakable
        return this
    }

    fun addFlags(vararg itemFlags: ItemFlag): ItemBuilder {
        Collections.addAll(itemMeta.itemFlags, *itemFlags)
        return this
    }

    fun addEnchant(enchant: Enchantment, level: Int): ItemBuilder {
        itemMeta.addEnchant(enchant, level, true)
        return this
    }

    fun setCustomModelData(modelDate: Int): ItemBuilder {
        itemMeta.setCustomModelData(modelDate)
        return this
    }

    // 1.20.5 +
    fun setItemName(name: Component): ItemBuilder {
        if (BukkitUtils.isVersionAtOrAbove("1.20.5")) {
            itemMeta.itemName(name)
        } else {
            itemMeta.displayName(name)
        }
        return this
    }

    fun setHideTooltip(hideTooltip: Boolean): ItemBuilder {
        if (BukkitUtils.isVersionAtOrAbove("1.20.5")) {
            itemMeta.isHideTooltip = hideTooltip
        }
        return this
    }

    fun setUnstackable(unstackable: Boolean): ItemBuilder {
        if (unstackable && BukkitUtils.isVersionAtOrAbove("1.20.5")) {
            itemMeta.setMaxStackSize(1)
        }
        return this
    }

    fun setMaxStackSize(size: Int): ItemBuilder {
        if (BukkitUtils.isVersionAtOrAbove("1.20.5")) {
            itemMeta.setMaxStackSize(size)
        }
        return this
    }

    fun setFoodComponent(foodComponent: FoodComponent): ItemBuilder {
        if (BukkitUtils.isVersionAtOrAbove("1.20.5")) {
            itemMeta.setFood(foodComponent)
        }
        return this
    }

    // 1.21.2 +
//    fun tooltipStyle(tooltips: String): ItemBuilder {
//        if (!BukkitUtils.isVersionAtOrAbove("1.21.2")) return this
//        val parts = tooltips.split(":")
//        itemMeta.tooltipStyle = NamespacedKey(parts[0], parts[1])
//        return this
//    }

    fun setTooltipStyle(tooltipStyle: NamespacedKey): ItemBuilder {
        if (!BukkitUtils.isVersionAtOrAbove("1.21.2")) {
            itemMeta.tooltipStyle = tooltipStyle
        }
        return this
    }

    fun setUseCooldown(useCooldownComponent: UseCooldownComponent): ItemBuilder {
        if (BukkitUtils.isVersionAtOrAbove("1.21.2")) {
            itemMeta.setUseCooldown(useCooldownComponent)
        }
        return this
    }

    fun setUseCooldown(seconds: Float, group: NamespacedKey? = null): ItemBuilder {
        if (!BukkitUtils.isVersionAtOrAbove("1.21.2")) return this

        val cooldown = itemMeta.useCooldown ?: return this

        cooldown.cooldownSeconds = seconds
        cooldown.cooldownGroup = group

        itemMeta.setUseCooldown(cooldown)
        return this
    }


    fun build(): ItemStack {
        itemStack.itemMeta = itemMeta
        return itemStack
    }
}