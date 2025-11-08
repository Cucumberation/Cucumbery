package com.jho5245.cucumbery.util.itemlore;

import com.jho5245.cucumbery.custom.custommaterial.CustomMaterial;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.no_groups.ItemCategory;
import com.jho5245.cucumbery.util.storage.no_groups.ItemCategory.Rarity;
import io.lumine.mythic.core.items.ItemGroup;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemComponent
{
	private static final ItemComponent instance = new ItemComponent();

	public static ItemComponent get()
	{
		return instance;
	}

	public static ItemStack send(@Nullable Player player, @NotNull ItemStack itemStack)
	{
		return get()._send(player, itemStack);
	}

	private ItemStack _send(@Nullable Player player, @NotNull ItemStack itemStack)
	{
		if (!ItemLoreUtil.isCucumberyTMIFood(itemStack))
		{
			return itemStack;
		}

		CustomMaterial customMaterial = CustomMaterial.itemStackOf(itemStack);
		if (customMaterial != null)
		{
			ItemComponentCustomMaterial.send(player, itemStack, customMaterial);
		}

		List<Component> lore = new ArrayList<>();

		lore.add(Component.empty());
		Component itemGroup = getItemGroup(itemStack);
		Component rarity = getItemRarity(itemStack);
		lore.add(itemGroup);
		lore.add(rarity);

		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.lore(lore);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	private Component getItemGroup(ItemStack itemStack)
	{
		Component itemGroup;
		CreativeCategory itemCategoryType = itemStack.getType().getCreativeCategory();
		if (itemCategoryType == null)
		{
			itemGroup = switch (itemStack.getType())
			{
				case SUSPICIOUS_STEW -> ComponentUtil.translate(CreativeCategory.FOOD.translationKey());
				case ENCHANTED_BOOK -> ComponentUtil.translate(Material.ENCHANTED_BOOK.translationKey());
				case WRITTEN_BOOK -> ComponentUtil.translate(CreativeCategory.MISC.translationKey());
				default -> ComponentUtil.translate("치트");
			};
		}
		else
		{
			itemGroup = ComponentUtil.translate(itemStack.getType().translationKey());
		}
		return ComponentUtil.translate("&7key:cucumbery.lore.item_group|아이템 종류 : [%s]", itemGroup);
	}

	private Component getItemRarity(ItemStack itemStack)
	{
		Rarity rarity;
		CustomMaterial customMaterial = CustomMaterial.itemStackOf(itemStack);
		if (customMaterial != null)
		{
			rarity = customMaterial.getRarity();
		}
		else
		{
			rarity = ItemCategory.getItemRarirty(itemStack.getType());
		}
		return ComponentUtil.translate("&7key:cucumbery.lore.item_rarity|아이템 등급 : %s", ComponentUtil.translate(rarity.getDisplay()));
	}
}
