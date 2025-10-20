package com.jho5245.cucumbery.util.storage.component.util;

import com.jho5245.cucumbery.util.storage.data.CustomMaterial;
import de.tr7zw.changeme.nbtapi.NBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemNameUtil
{
	/**
	 * 아이템의 이름을 컴포넌트 형태로 반환합니다.
	 *
	 * @param type
	 * 		아이템의 종류
	 * @return 컴포넌트 형태의 아이템 이름
	 */
	public static Component itemName(@NotNull Material type)
	{
		return itemName(type, null);
	}

	/**
	 * 아이템의 이름을 컴포넌트 형태로 반환합니다.
	 *
	 * @param type
	 * 		아이템의 종류
	 * @return 컴포넌트 형태의 아이템 이름
	 */
	public static Component itemName(@NotNull Material type, @Nullable TextColor defaultColor)
	{
		// argument validation
		if (!type.isItem())
		{
			return Component.translatable(type.translationKey(), defaultColor);
		}
		return itemName(new ItemStack(type), defaultColor);
	}

	/**
	 * 아이템의 이름을 컴포넌트 형태로 반환합니다.
	 *
	 * @param itemStack
	 * 		아이템
	 * @return 컴포넌트 형태의 아이템 이름
	 */
	@NotNull
	public static Component itemName(@NotNull ItemStack itemStack)
	{
		return itemName(itemStack, null);
	}

	/**
	 * 아이템의 이름을 컴포넌트 형태로 반환합니다.
	 *
	 * @param itemStack
	 * 		아이템
	 * @param defaultColor
	 * 		색상이 없을 경우 적용할 기본 값
	 * @return 컴포넌트 형태의 아이템 이름
	 */
	@NotNull
	public static Component itemName(@NotNull ItemStack itemStack, @Nullable TextColor defaultColor)
	{
		return itemName(itemStack, defaultColor, false);
	}

	/**
	 * 아이템의 이름을 컴포넌트 형태로 반환합니다.
	 *
	 * @param itemStack
	 * 		아이템
	 * @param defaultColor
	 * 		색상이 없을 경우 적용할 기본 값
	 * @param respectCustomMaterial
	 *    {@link CustomMaterial#getDisplayName()} 을 보전할 것인가?
	 * @return 컴포넌트 형태의 아이템 이름
	 */
	@NotNull
	public static Component itemName(@NotNull ItemStack itemStack, @Nullable TextColor defaultColor, boolean respectCustomMaterial)
	{
		Material material = itemStack.getType();
		ItemMeta itemMeta = itemStack.getItemMeta();
		Component component;
		// 아이템의 이름이 있을 경우 이름을 가져온다
		if (itemMeta != null && itemMeta.hasDisplayName())
		{
			try
			{
				component = itemMeta.displayName();
			}
			catch (Throwable t)
			{
				itemMeta.displayName(null);
				itemStack.setItemMeta(itemMeta);
				component = itemName(itemStack);
			}

			if (component == null) // 이런 경우는 없다.
			{
				component = Component.empty();
			}

			// 아이템의 기울임꼴 값이 없는 경우 기본적으로 아이템의 이름이 기울어져 있으므로 기울임 효과를 추가한다.
			if (component.decorations().get(TextDecoration.ITALIC) == State.NOT_SET)
			{
				component = component.decoration(TextDecoration.ITALIC, State.TRUE);
			}

			List<Component> children = new ArrayList<>(component.children());
			boolean childrenChanged = false;
			for (int i = 0; i < children.size(); i++)
			{
				Component child = children.get(i);
				if (child.decorations().get(TextDecoration.ITALIC) == State.NOT_SET)
				{
					child = child.decoration(TextDecoration.ITALIC, State.TRUE);
					childrenChanged = true;
				}
				children.set(i, child);
			}
			if (childrenChanged)
			{
				component = component.children(children);
			}
		}

		// 아이템의 이름이 없을 경우 번역된 아이템 이름을 가져온다.
		else
		{
			String id = material.translationKey();
			component = ComponentUtil.translate(id);
			// 특정 아이템은 다른 번역 규칙을 가지고 있으므로 해당 규칙을 적용한다.
			switch (material)
			{
				case WHEAT -> component = ComponentUtil.translate("item.minecraft.wheat");
				case PLAYER_HEAD, PLAYER_WALL_HEAD ->
				{
					String playerName = NBT.getComponents(itemStack, nbt -> {
						de.tr7zw.changeme.nbtapi.iface.ReadableNBT readableNBT = nbt.getCompound("minecraft:profile");
						if (readableNBT != null)
						{
							return readableNBT.getString("name");
						}
						return "";
					});
					if (playerName != null)
					{
						component = ComponentUtil.translate("block.minecraft.player_head.named").arguments(Component.text(playerName));
					}
				}
				case POTION, SPLASH_POTION, LINGERING_POTION, TIPPED_ARROW ->
				{
					PotionMeta potionMeta = (PotionMeta) itemMeta;
					if (potionMeta != null)
					{
						PotionType potionType = potionMeta.getBasePotionType();
						String potionId = potionType != null ? potionType.getKey().getKey().toLowerCase().replace("strong_", "").replace("long_", "") : "empty";
						component = ComponentUtil.translate(id + ".effect." + potionId);
					}
				}
				case WRITTEN_BOOK ->
				{
					BookMeta bookMeta = (BookMeta) itemMeta;
					if (bookMeta != null && bookMeta.hasTitle())
					{
						component = bookMeta.title();
						if (component == null || ComponentUtil.serialize(component).isEmpty())
						{
							component = ComponentUtil.translate(id);
						}
					}
				}
				case COMPASS ->
				{
					CompassMeta compassMeta = (CompassMeta) itemMeta;
					if (compassMeta != null && compassMeta.hasLodestone())
					{
						component = ComponentUtil.translate("item.minecraft.lodestone_compass");
					}
				}
				case SHIELD ->
				{
					BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
					if (blockStateMeta != null && blockStateMeta.hasBlockState())
					{
						BlockState blockState = blockStateMeta.getBlockState();
						Banner bannerState = (Banner) blockState;
						DyeColor baseColor = bannerState.getBaseColor();
						component = ComponentUtil.translate(id + "." + baseColor.toString().toLowerCase());
					}
				}
			}
			// 이름이 없는 아이템은 기울임 효과가 없으므로 반드시 false로 한다.
			component = component.decoration(TextDecoration.ITALIC, State.FALSE);
		}

		//
		//    // 커스텀 아이템의 경우 커스텀 아이템을 가져온다
		//    if (respectCustomMaterial || itemMeta == null || !itemMeta.hasDisplayName())
		//    {
		//      CustomMaterial customMaterial = CustomMaterial.itemStackOf(itemStack);
		//      if (customMaterial != null)
		//      {
		//        component = customMaterial.getDisplayName();
		//      }
		//    }

		// 아이템 기본 이름이 있을 경우 기본 이름 반환
		if (itemMeta != null && itemMeta.hasItemName())
		{
			component = itemMeta.itemName();
		}

		@Nullable TextColor textColor = component.color();
		// 아이템의 등급이 있고 아이템 이름에 색깔이 없을 경우와 기본 아이템 색깔이 흰색이 아닐 경우 색깔을 추가한다.
		if (material.isItem() && material != Material.AIR)
		{
			ItemRarity itemRarity = itemMeta != null && itemMeta.hasRarity() ? itemMeta.getRarity() : null;
			// 아이템에 마법이 부여되어 있을 경우 기본 아이템 이름의 색깔이 변경되므로 해당 색깔을 추가한다.
			boolean hasEnchants = itemMeta != null && itemMeta.hasEnchants();
			if (textColor == null)
			{
				switch (itemRarity)
				{
					case UNCOMMON -> textColor = hasEnchants ? NamedTextColor.AQUA : NamedTextColor.YELLOW;
					case RARE -> textColor = hasEnchants ? NamedTextColor.LIGHT_PURPLE : NamedTextColor.AQUA;
					case EPIC -> textColor = NamedTextColor.LIGHT_PURPLE;
					case null, default -> textColor = hasEnchants ? NamedTextColor.AQUA : null;
				}
				component = component.color(textColor);
				if (itemRarity == null)
				{
					// TODO: Default item doesn't have itemmeta for rarity - use deprecated API instead
					try
					{
						switch (material)
						{
							case CREEPER_BANNER_PATTERN, DISC_FRAGMENT_5, COAST_ARMOR_TRIM_SMITHING_TEMPLATE, BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, MUSIC_DISC_WAIT,
									 MUSIC_DISC_5, MUSIC_DISC_11, MUSIC_DISC_13, MUSIC_DISC_BLOCKS, MUSIC_DISC_CAT, MUSIC_DISC_CHIRP, MUSIC_DISC_CREATOR_MUSIC_BOX,
									 MUSIC_DISC_FAR, MUSIC_DISC_MALL, MUSIC_DISC_MELLOHI,  MUSIC_DISC_PRECIPICE, MUSIC_DISC_RELIC, MUSIC_DISC_STAL,
									 MUSIC_DISC_STRAD, MUSIC_DISC_TEARS, MUSIC_DISC_WARD, TOTEM_OF_UNDYING, NAUTILUS_SHELL, SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,
									 WILD_ARMOR_TRIM_SMITHING_TEMPLATE, EXPERIENCE_BOTTLE, ECHO_SHARD, TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, CONDUIT, SNIFFER_EGG,
									 GOAT_HORN, HEART_OF_THE_SEA, DRAGON_BREATH, CHAINMAIL_BOOTS, CHAINMAIL_CHESTPLATE, CHAINMAIL_HELMET, CHAINMAIL_LEGGINGS,
									 ANGLER_POTTERY_SHERD, ARCHER_POTTERY_SHERD, ARMS_UP_POTTERY_SHERD, BLADE_POTTERY_SHERD, BREWER_POTTERY_SHERD, BURN_POTTERY_SHERD,
									 DANGER_POTTERY_SHERD, EXPLORER_POTTERY_SHERD, FLOW_POTTERY_SHERD, FRIEND_POTTERY_SHERD, GUSTER_POTTERY_SHERD, HEART_POTTERY_SHERD,
									 HEARTBREAK_POTTERY_SHERD, HOWL_POTTERY_SHERD, MINER_POTTERY_SHERD, MOURNER_POTTERY_SHERD, PLENTY_POTTERY_SHERD, PRIZE_POTTERY_SHERD,
									 SCRAPE_POTTERY_SHERD, SHEAF_POTTERY_SHERD, SHELTER_POTTERY_SHERD, SKULL_POTTERY_SHERD, SNORT_POTTERY_SHERD, RIB_ARMOR_TRIM_SMITHING_TEMPLATE,
									 OMINOUS_BOTTLE, WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, PLAYER_HEAD, SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, ZOMBIE_HEAD, CREEPER_HEAD, SKELETON_SKULL,
									 PIGLIN_HEAD, RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, HOST_ARMOR_TRIM_SMITHING_TEMPLATE, FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, DUNE_ARMOR_TRIM_SMITHING_TEMPLATE,
									 SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, NETHERITE_UPGRADE_SMITHING_TEMPLATE, RECOVERY_COMPASS -> textColor = hasEnchants ? NamedTextColor.AQUA : NamedTextColor.YELLOW;
							case FLOW_BANNER_PATTERN, ENCHANTED_GOLDEN_APPLE, WARD_ARMOR_TRIM_SMITHING_TEMPLATE, SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, MOJANG_BANNER_PATTERN,
									 MUSIC_DISC_CREATOR, GUSTER_BANNER_PATTERN, TRIDENT, EYE_ARMOR_TRIM_SMITHING_TEMPLATE, WITHER_SKELETON_SKULL, MUSIC_DISC_PIGSTEP,
									 MUSIC_DISC_LAVA_CHICKEN, ENCHANTED_BOOK, NETHER_STAR, VEX_ARMOR_TRIM_SMITHING_TEMPLATE, SKULL_BANNER_PATTERN,
									 MUSIC_DISC_OTHERSIDE, BEACON -> textColor = hasEnchants ? NamedTextColor.LIGHT_PURPLE : NamedTextColor.AQUA;
							case ELYTRA, DRAGON_HEAD, DRAGON_EGG, SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, HEAVY_CORE, MACE, BARRIER, COMMAND_BLOCK,
									 REPEATING_COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, LIGHT, STRUCTURE_BLOCK, DEBUG_STICK, TEST_BLOCK, TEST_INSTANCE_BLOCK,
									 JIGSAW, COMMAND_BLOCK_MINECART, STRUCTURE_VOID, KNOWLEDGE_BOOK -> textColor = NamedTextColor.LIGHT_PURPLE;
						}
						component = component.color(textColor);
					}
					catch (IllegalStateException ignored)
					{

					}
				}
			}
		}
		if (textColor == null && defaultColor != null)
		{
			component = component.color(defaultColor);
		}
		return component;
	}
}
