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
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.components.EquippableComponent
import org.bukkit.inventory.meta.components.FoodComponent
import org.bukkit.inventory.meta.components.ToolComponent
import org.bukkit.inventory.meta.components.UseCooldownComponent
import org.bukkit.persistence.PersistentDataType

class ItemBuilder {

    private var itemStack: ItemStack
    private var itemMeta: ItemMeta

    constructor(material: Material) {
        this.itemStack = ItemStack(material)
        this.itemMeta = itemStack.itemMeta
    }

    constructor(itemStack: ItemStack) {
        this.itemStack = itemStack.clone()
        this.itemMeta = this.itemStack.itemMeta
    }

    constructor(itemId: String) : this(ItemUtils.createItem(itemId) ?: ItemStack(Material.STONE))

    fun setType(type: Material): ItemBuilder {
        itemStack = ItemStack(type)
        return this
    }

    fun setType(type: ItemStack): ItemBuilder {
        itemStack = type.clone()
        return this
    }

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

    fun addItemFlags(vararg itemFlags: ItemFlag): ItemBuilder {
        itemMeta.addItemFlags(*itemFlags)
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

//    fun clearAttributes(): ItemBuilder {
////        itemMeta.attributeModifiers = null
////        itemMeta.attributeModifiers?.clear()
//
//        val key = NamespacedKey(GrassRPGItem.instance, "none")
//        val attackSpeedModifier = AttributeModifier(key, 0.0, AttributeModifier.Operation.ADD_NUMBER)
//        itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED, attackSpeedModifier)
//        return this
//    }

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

    fun setToolComponent(toolComponent: ToolComponent): ItemBuilder {
        if (BukkitUtils.isVersionAtOrAbove("1.20.5")) {
            itemMeta.setTool(toolComponent)
        }
        return this
    }

//    fun setFireResistant(fireResistant: Boolean): ItemBuilder {
//        if (BukkitUtils.isVersionAtOrAbove("1.20.5")) {
//            itemMeta.isFireResistant = fireResistant
//        }
//        return this
//    }

    fun setDurability(durability: Int): ItemBuilder {
        if (!BukkitUtils.isVersionAtOrAbove("1.20.5")) {
            if (itemMeta is Damageable) (itemMeta as Damageable).damage = durability
        }
        return this
    }

    fun setDamagedOnBlockBreak(damagedOnBlockBreak: Boolean): ItemBuilder {
        if (!BukkitUtils.isVersionAtOrAbove("1.20.5")) {
            val container = itemMeta.persistentDataContainer
            val key = NamespacedKey(SoldamPlugin.instance, "damagedOnBlockBreak")
            container.set(key, PersistentDataType.BYTE, if (damagedOnBlockBreak) 1.toByte() else 0.toByte())
        }
        return this
    }

    fun setDamagedOnEntityHit(damagedOnEntityHit: Boolean): ItemBuilder {
        if (!BukkitUtils.isVersionAtOrAbove("1.20.5")) {
            val container = itemMeta.persistentDataContainer
            val key = NamespacedKey(SoldamPlugin.instance, "damagedOnEntityHit")
            container.set(key, PersistentDataType.BYTE, if (damagedOnEntityHit) 1.toByte() else 0.toByte())
        }
        return this
    }


    // 1.21.2 +
//    fun tooltipStyle(tooltips: String): ItemBuilder {
//        if (BukkitUtils.isVersionAtOrAbove("1.21.2")) return this
//        val parts = tooltips.split(":")
//        itemMeta.tooltipStyle = NamespacedKey(parts[0], parts[1])
//        return this
//    }

    fun setUseCooldownComponent(useCooldownComponent: UseCooldownComponent): ItemBuilder {
        if (BukkitUtils.isVersionAtOrAbove("1.21.2")) {
            itemMeta.setUseCooldown(useCooldownComponent)
        }
        return this
    }

    fun setTooltipStyle(tooltipStyle: NamespacedKey): ItemBuilder {
        if (BukkitUtils.isVersionAtOrAbove("1.21.2")) {
            itemMeta.tooltipStyle = tooltipStyle
        }
        return this
    }

//    @Deprecated("임시")
//    fun setUseCooldownComponent(seconds: Float, group: NamespacedKey? = null): ItemBuilder {
//        if (BukkitUtils.isVersionAtOrAbove("1.21.2")) {
//            val cooldown = itemMeta.useCooldown ?: return this
//
//            cooldown.cooldownSeconds = seconds
//            cooldown.cooldownGroup = group
//
//            itemMeta.setUseCooldown(cooldown)
//        }
//        return this
//    }

    fun setItemModel(itemModel: NamespacedKey): ItemBuilder {
        if (BukkitUtils.isVersionAtOrAbove("1.21.2")) {
            itemMeta.itemModel = itemModel
        }
        return this
    }

    fun setEnchantable(enchantable: Int): ItemBuilder {
        if (BukkitUtils.isVersionAtOrAbove("1.21.2")) {
            itemMeta.setEnchantable(enchantable)
        }
        return this
    }

    @Deprecated("임시")
    fun setEquippable(equippableComponent: EquippableComponent): ItemBuilder {
        if (BukkitUtils.isVersionAtOrAbove("1.21.2")) {
            itemMeta.setEquippable(equippableComponent)
        }
        return this
    }

    fun build(): ItemStack {
        itemStack.itemMeta = itemMeta
        return itemStack
    }
}