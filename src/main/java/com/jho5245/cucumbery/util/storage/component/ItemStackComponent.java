package com.jho5245.cucumbery.util.storage.component;

import com.comphenix.protocol.PacketType.Play.Server;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.util.addons.ProtocolLibManager;
import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.itemlore.ItemLoreView;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.no_groups.ItemSerializer;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.component.util.ItemNameUtil;
import com.jho5245.cucumbery.util.storage.data.Constant.CucumberyHideFlag;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemStackComponent
{
	@NotNull
	public static Component itemStackComponent(@NotNull ItemStack itemStack)
	{
		return itemStackComponent(itemStack, itemStack.getAmount(), NamedTextColor.GRAY, true);
	}
	@NotNull
	public static Component itemStackComponent(@NotNull ItemStack itemStack, @Nullable Player viewer)
	{
		return itemStackComponent(itemStack, itemStack.getAmount(), NamedTextColor.GRAY, true, viewer);
	}

	@NotNull
	@SuppressWarnings("unused")
	public static Component itemStackComponent(@NotNull ItemStack itemStack, int amount)
	{
		return itemStackComponent(itemStack, amount, NamedTextColor.GRAY, true);
	}

	@NotNull
	public static Component itemStackComponent(@NotNull ItemStack itemStack, @Nullable TextColor defaultColor)
	{
		return itemStackComponent(itemStack, itemStack.getAmount(), defaultColor, true);
	}

	@NotNull
	public static Component itemStackComponent(@NotNull ItemStack itemStack, @Nullable TextColor defaultColor, @Nullable Player viewer)
	{
		return itemStackComponent(itemStack, itemStack.getAmount(), defaultColor, true, viewer);
	}

	@NotNull
	public static Component itemStackComponent(@NotNull ItemStack itemStack, int amount, @Nullable TextColor defaultColor, boolean showAmount)
	{
		return itemStackComponent(itemStack, amount, defaultColor, showAmount, null);
	}

	@NotNull
	public static Component itemStackComponent(@NotNull ItemStack itemStack, int amount, @Nullable TextColor defaultColor, boolean showAmount,
			@Nullable Player viewer)
	{
		itemStack = itemStack.clone();
		ItemLore.removeItemLore(itemStack);
		final ItemStack giveItem = itemStack.clone();
		itemStack.setAmount(Math.max(1, Math.min(64, itemStack.getAmount())));
		Component itemName = ItemNameUtil.itemName(itemStack, defaultColor, true);
		ItemStack hover = new ItemStack(Material.BUNDLE);
		BundleMeta bundleMeta = (BundleMeta) hover.getItemMeta();
		if (viewer != null)
		{
			boolean showItemLore = UserData.SHOW_ITEM_LORE.getBoolean(viewer), showAll = UserData.EVENT_EXCEPTION_ACCESS.getBoolean(viewer);
			NBTItem nbtItem = new NBTItem(itemStack.clone());
			@Nullable NBTCompound itemTag = nbtItem.getCompound(CucumberyTag.KEY_MAIN);
			NBTList<String> hideFlags = NBTAPI.getStringList(itemTag, CucumberyTag.HIDE_FLAGS_KEY);
			boolean hideFlagsTagExists = hideFlags != null;
			ItemStack clone = itemStack.clone();
			ItemMeta cloneMeta = clone.getItemMeta();
			boolean showEnchants =
					!(hideFlagsTagExists && NBTAPI.arrayContainsValue(hideFlags, CucumberyHideFlag.ENCHANTS)) && !cloneMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
			if (showItemLore) // 아이템 설명을 사용할 경우 큐컴버리의 setItemLore의 설명만 사용하고 모든 ItemFlag 추가
			{
				clone = Cucumbery.using_ProtocolLib ? ProtocolLibManager.setItemLore(Server.ABILITIES, clone, viewer) : ItemLore.setItemLore(itemStack, false, ItemLoreView.of(viewer));
				cloneMeta = clone.getItemMeta();
				cloneMeta.addItemFlags(ItemFlag.values());
			}
			else if (showAll) // 사용하지 않을 경우 만약 이벤트 예외 액세스가 켜져 있을 경우 모든 ItemFlag 삭제
			{
				cloneMeta.removeItemFlags(ItemFlag.values());
			}
			if (!showAll && !showEnchants)
			{
				cloneMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				if (cloneMeta instanceof EnchantmentStorageMeta)
					cloneMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
			}
			clone.setItemMeta(cloneMeta);
			List<Component> tooltip = new ArrayList<>(clone.computeTooltipLines(TooltipContext.create(), viewer));
			if (!tooltip.isEmpty())
				tooltip.remove(0);
			if (!showItemLore && !tooltip.isEmpty() && !showEnchants && showAll)
			{
				tooltip.add(0, ComponentUtil.translate("&8관리자 권한으로 숨겨진 마법을 참조합니다"));
			}
			for (int i = 0; i < tooltip.size(); i++)
			{
				Component tooltip1 = tooltip.get(i);
				if (tooltip1.color() != null && tooltip1.decoration(TextDecoration.ITALIC) == State.NOT_SET)
					tooltip.set(i, tooltip1.decoration(TextDecoration.ITALIC, State.FALSE));
			}
			if (viewer.hasPermission("asdf") && !nbtItem.hasTag("VirtualItem"))
			{
				tooltip.add(Component.empty());
				tooltip.add(ComponentUtil.translate("&7클릭하여 /give 명령어로 복사"));
				Object o = new NBTItem(giveItem).getCompound();
				String nbt = o != null ? o.toString() : "";
				if (nbt.equals("{}"))
					nbt = "";
				if (UserData.SHOW_GIVE_COMMAND_NBT_ON_ITEM_ON_CHAT.getBoolean(viewer) && !nbt.isEmpty())
				{
					String nbtClone = nbt;
					int count = 0;
					while (!nbtClone.isEmpty())
					{
						count++;
						if (count > 20)
						{
							tooltip.add(ComponentUtil.translate("&7&ocontainer.shulkerBox.more", nbtClone.length() / 50));
							break;
						}
						String cut = nbtClone.substring(0, Math.min(50, nbtClone.length()));
						tooltip.add(Component.text(cut, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, State.FALSE));
						nbtClone = nbtClone.substring(cut.length());
					}
				}

				String clickEventString = "/give @p minecraft:" + giveItem.getType().toString().toLowerCase() + nbt;
				itemName = itemName.clickEvent(ClickEvent.copyToClipboard(clickEventString));
			}
			bundleMeta.lore(tooltip);
		}
		bundleMeta.addItem(Cucumbery.using_ProtocolLib && viewer != null ? ProtocolLibManager.setItemLore(Server.ABILITIES, itemStack, viewer) : itemStack);
		bundleMeta.addItemFlags(ItemFlag.values());
		bundleMeta.displayName(ItemNameUtil.itemName(itemStack));
		hover.setItemMeta(bundleMeta);
		if (itemName instanceof TextComponent textComponent && textComponent.content().isEmpty())
		{
			List<Component> children = new ArrayList<>(itemName.children());
			children.replaceAll(component -> component.hoverEvent(hover.asHoverEvent()));
			itemName = itemName.children(children);
		}
		else
		{
			itemName = itemName.hoverEvent(hover.asHoverEvent());
		}
		if (!showAmount || (amount == 1 && itemStack.getType().getMaxStackSize() == 1))
		{
			return itemName;
		}
		return ComponentUtil.translate("&i%s %s", itemName, ComponentUtil.translate("%s개", amount)).color(defaultColor).hoverEvent(hover.asHoverEvent())
				.clickEvent(itemName.clickEvent());
	}
}
