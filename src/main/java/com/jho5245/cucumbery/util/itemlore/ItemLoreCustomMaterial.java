package com.jho5245.cucumbery.util.itemlore;

import com.jho5245.cucumbery.custom.custommaterial.CustomMaterial;
import com.jho5245.cucumbery.events.itemlore.ItemLoreCustomMaterialEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class ItemLoreCustomMaterial
{
	protected static void itemLore(@Nullable Player player, @NotNull ItemStack itemStack, @NotNull CustomMaterial customMaterial)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.itemName(customMaterial.getDisplayName());
		Material realMaterial = customMaterial.getRealMaterial();
		Material displayMaterial = customMaterial.getDisplayMaterial();
		// 표시 아이템 모델 설정
		{
			if (displayMaterial != null)
			{
				itemMeta.setItemModel(displayMaterial.getKey());
			}
		}
		// RealItem이 디버그 막대기고 표시 아이템이 있을 경우 기본 속성 제어
		{
			if (realMaterial == Material.DEBUG_STICK && displayMaterial != null)
			{
				itemMeta.setRarity(ItemRarity.COMMON);
				itemMeta.setMaxStackSize(64);
				itemMeta.setEnchantmentGlintOverride(false);
			}
		}
		// 커스텀 모델 데이터 설정
		{
			CustomModelDataComponent customModelDataComponent = itemMeta.getCustomModelDataComponent();
			customModelDataComponent.setStrings(Collections.singletonList(customMaterial.toString().toLowerCase()));
			itemMeta.setCustomModelDataComponent(customModelDataComponent);
		}
		itemStack.setItemMeta(itemMeta);
		ItemLoreCustomMaterialEvent event = new ItemLoreCustomMaterialEvent(player, itemStack);
		event.callEvent();
		itemStack.setItemMeta(event.getItemStack().getItemMeta());
	}
}
