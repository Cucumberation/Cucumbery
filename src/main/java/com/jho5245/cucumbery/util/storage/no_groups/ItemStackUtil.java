package com.jho5245.cucumbery.util.storage.no_groups;

import com.comphenix.protocol.PacketType.Play;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.custom.custommaterial.CustomMaterial;
import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.itemlore.ItemLore.RemoveFlag;
import com.jho5245.cucumbery.util.itemlore.ItemLore4;
import com.jho5245.cucumbery.util.itemlore.ItemLoreView;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.Method2;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.component.util.ItemNameUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.event.DataComponentValue.Removed;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.serializer.gson.GsonDataComponentValue;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;

public class ItemStackUtil
{
	public static final ItemStack AIR = new ItemStack(Material.AIR);

	public static final ItemStack HIDDEN_ITEM, INVALID_ITEM;

	public static final String INVALID_ITEM_TAG = "INVALID_ITEM";

	static
	{
		HIDDEN_ITEM = new ItemStack(Material.TRIAL_SPAWNER);
		ItemMeta itemMeta = HIDDEN_ITEM.getItemMeta();
		itemMeta.itemName(ComponentUtil.translate("key:item.cucumbery.hidden_item|???"));
		itemMeta.lore(Collections.singletonList(ComponentUtil.translate("&7key:item.cucumbery.hidden_item.description|아이템의 정보가 숨겨져 있습니다!")));
		itemMeta.addItemFlags(ItemFlag.values());
		HIDDEN_ITEM.setItemMeta(itemMeta);

		INVALID_ITEM = new ItemStack(Material.BARRIER);
		itemMeta = INVALID_ITEM.getItemMeta();
		itemMeta.itemName(ComponentUtil.translate("key:item.cucumbery.invalid_item|{오류 아이템}"));
		itemMeta.lore(List.of(ComponentUtil.translate("&7key:item.cucumbery.invalid.description|오류가 발생하여 생성된 아이템입니다!"),
				ComponentUtil.translate("&7key:item.cucumbery.invalid.description_2|관리자에게 문의해주세요!")));
		INVALID_ITEM.setItemMeta(itemMeta);
		// TODO: NBTAPI Should fix this
/*		NBT.modify(INVALID_ITEM, nbt ->
		{
			nbt.setBoolean(INVALID_ITEM_TAG, true);
		});*/
	}

	/**
	 * 아이템의 모루 사용 횟수를 가져옵니다. 정상적인 횟수가 아닐 경우, -1을 반환합니다.
	 *
	 * @param itemMeta
	 * 		모루 사용 횟수를 가져올 아이템의 메타
	 * @return 아이템의 모루 사용 횟수
	 */
	public static int getAnvilUsedTime(@NotNull ItemMeta itemMeta)
	{
		if (itemMeta instanceof Repairable repairMeta)
		{
			int repairCost = repairMeta.getRepairCost();
			if (repairCost <= 0)
			{
				return 0;
			}
			repairCost++;
			int useTime = 0;
			while (repairCost / 2D != 1D)
			{
				repairCost /= 2;
				useTime++;
				if (useTime > 100)
				{
					break;
				}
			}
			return useTime + 1;
		}
		return -1;
	}

	/**
	 * 설치할 수 있는 아이템인지 확인합니다.
	 *
	 * @param type
	 * 		확인할 아이템의 종류
	 * @return 설치할 수 있을 경우 true
	 */
	public static boolean isPlacable(@NotNull Material type)
	{
		return switch (type)
		{
			case REDSTONE, BEETROOT_SEEDS, MELON_SEEDS, PUMPKIN_SEEDS, WHEAT_SEEDS, COCOA_BEANS, ITEM_FRAME, PAINTING, ARMOR_STAND, STRING, END_CRYSTAL,
					 SWEET_BERRIES, CARROT, POTATO -> true;
			case WHEAT -> false;
			default -> type.isBlock();
		};
	}

	/**
	 * 양조할 수 있는 아이템인지 확인합니다.
	 *
	 * @param type
	 * 		확인할 아이템의 종류
	 * @return 양조기에 사용할 수 있을 경우 true, 아닐 경우 false
	 */
	public static boolean isBrewable(@NotNull Material type)
	{
		return switch (type)
		{
			case REDSTONE, GLOWSTONE_DUST, GHAST_TEAR, NETHER_WART, GOLDEN_CARROT, BLAZE_POWDER, FERMENTED_SPIDER_EYE, GUNPOWDER, DRAGON_BREATH, SUGAR, RABBIT_FOOT,
					 GLISTERING_MELON_SLICE, SPIDER_EYE, PUFFERFISH, MAGMA_CREAM, TURTLE_HELMET, PHANTOM_MEMBRANE -> true;
			default -> false;
		};
	}

	/**
	 * 먹을 수 있는 아이템인지 확인합니다.
	 *
	 * @param type
	 * 		확인할 아이템의 종류
	 * @return 배고픔에 영향을 주지 않아도, 먹을 수 있으면 true, 아닐 경우 false
	 */
	public static boolean isEdible(@NotNull Material type)
	{
		return type.isEdible() || type == Material.MILK_BUCKET || type == Material.POTION;
	}

	/**
	 * 해당 아이템을 먹으면 상태 효과에 영향을 미치는 지 확인합니다. {@link #isEdible(Material)} 이 false일 경우, false를 반환합니다.
	 *
	 * @param type
	 * 		확인할 아이템의 종류
	 * @return 상태 효과에 영향을 미치면 true, 아닐 경우 false
	 */
	public static boolean hasStatusEffect(@NotNull Material type)
	{
		return ItemStackUtil.isEdible(type) && (type == Material.GOLDEN_APPLE || type == Material.ENCHANTED_GOLDEN_APPLE || type == Material.POISONOUS_POTATO
				|| type == Material.SPIDER_EYE || type == Material.PUFFERFISH || type == Material.ROTTEN_FLESH || type == Material.CHICKEN
				|| type == Material.HONEY_BOTTLE || type == Material.MILK_BUCKET || type == Material.POTION);
	}

	public static int getFoodLevel(@NotNull Material type)
	{
		return switch (type)
		{
			case BEETROOT, DRIED_KELP, POTATO, PUFFERFISH, TROPICAL_FISH -> 1;
			case COOKIE, MELON_SLICE, POISONOUS_POTATO, CHICKEN, COD, MUTTON, SALMON, SPIDER_EYE, SWEET_BERRIES, GLOW_BERRIES -> 2;
			case CARROT, BEEF, PORKCHOP, RABBIT -> 3;
			case APPLE, CHORUS_FRUIT, ENCHANTED_GOLDEN_APPLE, GOLDEN_APPLE, ROTTEN_FLESH -> 4;
			case BAKED_POTATO, BREAD, COOKED_COD, COOKED_RABBIT -> 5;
			case BEETROOT_SOUP, COOKED_CHICKEN, COOKED_MUTTON, COOKED_SALMON, GOLDEN_CARROT, HONEY_BOTTLE, MUSHROOM_STEW, SUSPICIOUS_STEW -> 6;
			case COOKED_PORKCHOP, PUMPKIN_PIE, COOKED_BEEF -> 8;
			case RABBIT_STEW -> 10;
			case CAKE -> 14;
			default -> 0;
		};
	}

	public static double getSaturation(@NotNull Material type)
	{
		return switch (type)
		{
			case PUFFERFISH, TROPICAL_FISH -> 0.2;
			case COOKIE, COD, SALMON, SWEET_BERRIES, GLOW_BERRIES -> 0.4;
			case DRIED_KELP, POTATO -> 0.6;
			case ROTTEN_FLESH -> 0.8;
			case BEETROOT, MELON_SLICE, POISONOUS_POTATO, CHICKEN, MUTTON, HONEY_BOTTLE -> 1.2;
			case BEEF, PORKCHOP, RABBIT -> 1.8;
			case APPLE, CHORUS_FRUIT -> 2.4;
			case CAKE -> 2.8;
			case SPIDER_EYE -> 3.2;
			case CARROT -> 3.6;
			case PUMPKIN_PIE -> 4.8;
			case BAKED_POTATO, BREAD, COOKED_COD, COOKED_RABBIT -> 6;
			case BEETROOT_SOUP, COOKED_CHICKEN, MUSHROOM_STEW, SUSPICIOUS_STEW -> 7.2;
			case ENCHANTED_GOLDEN_APPLE, GOLDEN_APPLE, COOKED_MUTTON, COOKED_SALMON -> 9.6;
			case RABBIT_STEW -> 12;
			case COOKED_PORKCHOP, COOKED_BEEF -> 12.8;
			case GOLDEN_CARROT -> 14.4;
			default -> 0d;
		};
	}

	public static String getNourishment(int foodLevel, double saturation)
	{
		if (foodLevel == 0 || (foodLevel < 0 && saturation < 0))
		{
			return "#57B6F0;공허";
		}
		double ratio = saturation / foodLevel;
		if (ratio < 0.4)
		{
			return "#47B6F0;허-전";
		}
		else if (ratio < 1.0)
		{
			return "#16F06C;낮음";
		}
		else if (ratio < 1.5)
		{
			return "#F0CA4F;보통";
		}
		else if (ratio < 2.0)
		{
			return "#F05C48;높음";
		}
		else
		{
			return "#E553F0;든-든";
		}
	}

	@Nullable
	public static String getNourishment(@NotNull Material type)
	{
		if (!type.isEdible() && type != Material.CAKE && type != Material.MILK_BUCKET && type != Material.POTION)
		{
			return null;
		}
		return switch (type)
		{
			case GOLDEN_APPLE, ENCHANTED_GOLDEN_APPLE, GOLDEN_CARROT -> "#E553F0;든-든";
			case COOKED_MUTTON, COOKED_PORKCHOP, COOKED_SALMON, COOKED_BEEF -> "#F05C48;높음";
			case BAKED_POTATO, BEETROOT_SOUP, BEETROOT, BREAD, CARROT, COOKED_CHICKEN, COOKED_COD, COOKED_RABBIT, MUSHROOM_STEW, RABBIT_STEW, SUSPICIOUS_STEW ->
					"#F0CA4F;보통";
			case APPLE, CHORUS_FRUIT, DRIED_KELP, MELON_SLICE, POISONOUS_POTATO, POTATO, PUMPKIN_PIE, BEEF, PORKCHOP, MUTTON, CHICKEN, RABBIT -> "#16F06C;낮음";
			case CAKE, COOKIE, HONEY_BOTTLE, PUFFERFISH, COD, SALMON, ROTTEN_FLESH, SPIDER_EYE, SWEET_BERRIES, TROPICAL_FISH, GLOW_BERRIES -> "#47B6F0;허-전";
			default -> null;
		};
	}

	public static boolean itemExists(ItemStack item) // 아이템이 존재하는지 아닌지 판별
	{
		return (item != null && item.getType() != Material.AIR) && item.getAmount() > 0;
	}

	public static boolean hasItemMeta(ItemStack item)
	{
		return ItemStackUtil.itemExists(item) && !new ItemStack(item.getType(), item.getAmount()).equals(item);
	}

	@SuppressWarnings("unused")
	public static boolean hasItemMeta(ItemStack item, boolean exist)
	{
		if (exist)
		{
			return !new ItemStack(item.getType(), item.getAmount()).equals(item);
		}
		return ItemStackUtil.itemExists(item) && !new ItemStack(item.getType(), item.getAmount()).equals(item);
	}

	public static boolean hasDisplayName(ItemStack item) // 아이템이 이름을 가지고 있는지 판별
	{
		return ItemStackUtil.itemExists(item) && item.hasItemMeta() && item.getItemMeta().hasDisplayName();
	}

	@SuppressWarnings("unused")
	public static boolean hasDisplayName(ItemStack item, boolean exist) // 아이템이 이름을 가지고 있는지만 판별
	{
		if (exist)
		{
			return item.hasItemMeta() && item.getItemMeta().hasDisplayName();
		}
		return ItemStackUtil.itemExists(item) && item.hasItemMeta() && item.getItemMeta().hasDisplayName();
	}

	public static boolean hasLore(ItemStack item) // 아이템이 설명을 가지고 있는지 판별
	{
		return ItemStackUtil.itemExists(item) && item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().lore() != null && !Objects.requireNonNull(
				item.getItemMeta().lore()).isEmpty();
	}

	public static boolean hasLore(ItemStack item, boolean exist) // 아이템이 설명만 가지고 있는지 판별
	{
		if (exist)
		{
			return item.hasItemMeta() && item.getItemMeta().hasLore();
		}
		return ItemStackUtil.itemExists(item) && item.hasItemMeta() && item.getItemMeta().hasLore();
	}

	/**
	 * 두 아이템을 서로 비교하여 같으면 true를 반환합니다. 내구도는 무시할 수 있습니다.
	 *
	 * @param item1
	 * 		첫 번째 아이템
	 * @param item2
	 * 		두 번째 아이템
	 * @param ignoreDurability
	 * 		내구도 무시
	 * @return 만약 두 아이템이 완전히 값이 일치하면 true
	 */
	public static boolean itemEquals(@Nullable ItemStack item1, @Nullable ItemStack item2, boolean ignoreDurability)
	{
		if (!itemExists(item1) && !itemExists(item2))
		{
			return true;
		}
		if (!itemExists(item1) || !itemExists(item2))
		{
			return false;
		}
		item1 = item1.clone();
		item1.setAmount(1);
		item2 = item2.clone();
		item2.setAmount(1);
		if (ignoreDurability)
		{
			((Damageable) item2.getItemMeta()).setDamage(((Damageable) item1.getItemMeta()).getDamage());
		}
		return item1.equals(item2);
	}

	/**
	 * 두 아이템을 서로 비교하여 같으면 true를 반환합니다.
	 *
	 * @param item1
	 * 		첫 번째 아이템
	 * @param item2
	 * 		두 번째 아이템
	 * @return 만약 두 아이템이 완전히 값이 일치하면 true
	 */
	public static boolean itemEquals(@Nullable ItemStack item1, @Nullable ItemStack item2)
	{
		return itemEquals(item1, item2, false);
	}

	/**
	 * 주어진 인벤토리에 해당 아이템이 얼마나 있는지 반환합니다. 내구도는 무시할 수 있습니다.
	 *
	 * @param inv
	 * 		주어진 인벤토리
	 * @param item
	 * 		해당 아이템
	 * @param ignoreDurability
	 * 		내구도 무시
	 * @return 일치하는 아이템 개수를 반환합니다.
	 */
	public static int countItem(@NotNull Inventory inv, @NotNull ItemStack item, boolean ignoreDurability)
	{
		int amount = 0;
		for (ItemStack iStack : inv.getStorageContents())
		{
			if (itemEquals(item, iStack, ignoreDurability))
			{
				if (itemExists(iStack))
				{
					amount += iStack.getAmount();
				}
			}
		}
		return amount;
	}

	/**
	 * 주어진 인벤토리에 해당 아이템이 얼마나 있는지 반환합니다.
	 *
	 * @param inv
	 * 		주어진 인벤토리
	 * @param item
	 * 		해당 아이템
	 * @return 일치하는 아이템 개수를 반환합니다.
	 */
	public static int countItem(@NotNull Inventory inv, @NotNull ItemStack item)
	{
		return countItem(inv, item, false);
	}

	public static int countItem(@NotNull Inventory inv, @NotNull String predicate)
	{
		int amount = 0;
		for (ItemStack iStack : inv.getStorageContents())
		{
			if (iStack != null && ItemStackUtil.predicateItem(iStack, predicate))
			{
				if (itemExists(iStack))
				{
					amount += iStack.getAmount();
				}
			}
		}
		return amount;
	}

	/**
	 * 주어진 인벤토리에 해당 아이템이 (예외 처리 없이) 얼마나 들어갈 수 있는지 반환합니다. Method.itemEquals(item1, item2) 로 아이템이 같은지 확인합니다. 내구도는 무시할 수 있습니다.
	 *
	 * @param inv
	 * 		주어진 인벤토리
	 * @param item
	 * 		해당 아이템
	 * @param ignoreDurability
	 * 		내구도 무시
	 * @return 해당 아이템이 인벤토리에 얼마나 들어갈 수 있는지 개수를 반환합니다.
	 */
	public static int countSpace(@NotNull Inventory inv, @NotNull ItemStack item, boolean ignoreDurability)
	{
		int space = 0;
		ItemStack[] contents = inv.getStorageContents();
		for (ItemStack iStack : contents)
		{
			int maxStackSize = item.getMaxStackSize();
			if (!itemExists(iStack))
			{
				space += maxStackSize;
			}
			else if (itemEquals(item, iStack, ignoreDurability))
			{
				space += Math.max(0, maxStackSize - iStack.getAmount());
			}
		}
		if (space < 0)
		{
			space = 0;
		}
		return space;
	}

	/**
	 * 주어진 인벤토리에 해당 아이템이 (예외 처리 없이) 얼마나 들어갈 수 있는지 반환합니다. Method.itemEquals(item1, item2) 로 아이템이 같은지 확인합니다.
	 *
	 * @param inv
	 * 		주어진 인벤토리
	 * @param item
	 * 		해당 아이템
	 * @return 해당 아이템이 인벤토리에 얼마나 들어갈 수 있는지 개수를 반환합니다.
	 */
	public static int countSpace(@NotNull Inventory inv, @NotNull ItemStack item)
	{
		return countSpace(inv, item, false);
	}

	/**
	 * 플레이어의 인벤토리에 해당 아이템이 (예외 처리 없이) 얼마나 들어갈 수 있는지 반환합니다. Method.itemEquals(item1, item2) 로 아이템이 같은지 확인합니다.
	 *
	 * @param player
	 * 		비교할 플레이어
	 * @param itemStack
	 * 		아이템
	 * @return 해당 아이템이 인벤토리에 얼마나 들어갈 수 있는지 개수를 반환합니다.
	 */
	public static int countSpace(@NotNull Player player, @NotNull ItemStack itemStack)
	{
		return countSpace(player.getInventory(), itemStack);
	}

	/**
	 * 목록에 있는 아이템들 중 화로에서 제련할 수 있는 아이템이 있으면 제련된 형태로 바꿔서 반환합니다.
	 *
	 * @param player
	 * 		제련하는 플레이어
	 * @param input
	 * 		교체할 아이템이 있는 목록
	 * @param expOutput
	 * 		각 아이템의 제련 경험치를 반환하기 위한 리스트
	 * @return 아이템이 교체된 배열
	 */
	public static List<ItemStack> getSmeltedResult(@Nullable Player player, @NotNull Collection<ItemStack> input, @Nullable List<Double> expOutput)
	{
		List<ItemStack> dropsClone = new ArrayList<>();
		for (ItemStack drop : input)
		{
			for (Recipe recipe : RecipeChecker.recipes)
			{
				if (recipe instanceof CookingRecipe<?> cookingRecipe)
				{
					RecipeChoice recipeChoice = cookingRecipe.getInputChoice();
					if (recipeChoice.test(drop))
					{
						drop.setType(cookingRecipe.getResult().getType());
						if (expOutput != null)
						{
							double exp = cookingRecipe.getExperience();
							if (player != null && CustomEffectManager.hasEffect(player, CustomEffectType.EXPERIENCE_BOOST))
							{
								int amplifier = CustomEffectManager.getEffect(player, CustomEffectType.EXPERIENCE_BOOST).getAmplifier();
								exp = (1d * exp * (1 + (amplifier + 1) * 0.05));
							}
							expOutput.add(exp);
						}
						break;
					}
				}
			}
			if (ItemStackUtil.itemExists(drop))
			{
				NBTItem nbtItem = new NBTItem(drop, true);
				CustomMaterial customMaterial = CustomMaterial.itemStackOf(drop);
				if (customMaterial != null)
				{
					CustomMaterial smeltedItem = customMaterial.getSmeltedItem();
					if (smeltedItem != null)
					{
						nbtItem.setString(CustomMaterial.IDENDTIFER, smeltedItem.toString().toLowerCase());
					}
					Material smeltedItemVanilla = customMaterial.getSmeltedItemVanilla();
					if (smeltedItemVanilla != null)
					{
						drop = new ItemStack(smeltedItemVanilla, drop.getAmount());
					}
				}
			}
			dropsClone.add(drop);
		}
		return dropsClone;
	}

	/**
	 * 사용하는 손이 상관없는 아이템을 사용하는 이벤트 (예 : 양동이 비우기/채우기는 주로 사용하는 손이나 다른 손이나 상관 없음)에서 어느 손에 아이템을 사용하는지 가져옵니다.
	 *
	 * @param livingEntity
	 * 		이벤트를 호출하는 플레이어
	 * @param typeList
	 * 		감지할 아이템 (목록)
	 * @return 아이템 목록에 플레이어가 들고 있는 아이템이 없을 경우 null
	 */
	public static ItemStack getPlayerUsingItem(@NotNull LivingEntity livingEntity, @NotNull Set<Material> typeList)
	{
		EntityEquipment entityEquipment = livingEntity.getEquipment();
		if (entityEquipment == null)
		{
			return null;
		}
		ItemStack mainHand = entityEquipment.getItemInMainHand(), offHand = entityEquipment.getItemInOffHand();
		boolean isMainHand = false;
		boolean hasItem = false;
		if (itemExists(mainHand) && typeList.contains(mainHand.getType()))
		{
			isMainHand = true;
			hasItem = true;
		}
		else if (itemExists(offHand) && typeList.contains(offHand.getType()) && (!itemExists(mainHand) || !typeList.contains(mainHand.getType())))
		{
			hasItem = true;
		}
		if (hasItem)
		{
			return isMainHand ? mainHand : offHand;
		}
		return null;
	}

	/**
	 * 사용하는 손이 상관없는 아이템을 사용하는 이벤트 (예 : 양동이 비우기/채우기는 주로 사용하는 손이나 다른 손이나 상관 없음)에서 어느 손에 아이템을 사용하는지 가져옵니다.
	 *
	 * @param livingEntity
	 * 		이벤트를 호출하는 플레이어
	 * @param typeList
	 * 		감지할 아이템 (목록)
	 * @return 아이템 목록에 플레이어가 들고 있는 아이템이 없을 경우 null
	 */
	public static ItemStack getPlayerUsingItem(@NotNull LivingEntity livingEntity, Material... typeList)
	{
		return getPlayerUsingItem(livingEntity, materialArrayToList(typeList));
	}

	private static Set<Material> materialArrayToList(Material... typeList)
	{
		Set<Material> list = new HashSet<>();
		Collections.addAll(list, typeList);
		return list;
	}

	@NotNull
	public static EquipmentSlot getPlayerUsingSlot(@NotNull LivingEntity livingEntity, @NotNull Set<Material> typeList)
	{
		EntityEquipment entityEquipment = livingEntity.getEquipment();
		if (entityEquipment == null)
		{
			return EquipmentSlot.HAND;
		}
		ItemStack mainHand = entityEquipment.getItemInMainHand(), offHand = entityEquipment.getItemInOffHand();
		boolean isMainHand = false;
		boolean hasItem = false;
		if (itemExists(mainHand) && typeList.contains(mainHand.getType()))
		{
			isMainHand = true;
			hasItem = true;
		}
		else if (itemExists(offHand) && typeList.contains(offHand.getType()) && (!itemExists(mainHand) || !typeList.contains(mainHand.getType())))
		{
			hasItem = true;
		}
		if (hasItem)
		{
			return isMainHand ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
		}
		return EquipmentSlot.HAND;
	}

	@NotNull
	@SuppressWarnings("unused")
	public static EquipmentSlot getPlayerUsingSlot(@NotNull Player player, @NotNull Material... typeList)
	{
		return getPlayerUsingSlot(player, materialArrayToList(typeList));
	}

	@NotNull
	public static ItemStack getItemStackFromBlock(@NotNull Block block)
	{
		Material type = block.getType();
		BlockData blockData = block.getBlockData();
		Material placedBlockType = blockData.getPlacementMaterial();
		if (!placedBlockType.isAir())
		{
			type = placedBlockType;
		}
		ItemStack itemStack = new ItemStack(type.isAir() || !type.isItem() ? Material.BARRIER : type);
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null)
		{
			return itemStack;
		}
		if (type.isAir() || !type.isItem())
		{
			itemMeta.displayName(ComponentUtil.translate("벽").color(Constant.THE_COLOR).decoration(TextDecoration.ITALIC, State.FALSE));
		}
		else
		{
			itemMeta.displayName(ItemNameUtil.itemName(block.getType()));
		}
		itemStack.setItemMeta(itemMeta);
		if (itemMeta instanceof BlockStateMeta blockStateMeta)
		{
			BlockState blockState = block.getState();
			blockStateMeta.setBlockState(blockState);
			if (blockState instanceof Nameable nameable && nameable.customName() != null)
			{
				blockStateMeta.displayName(nameable.customName());
			}
			itemStack.setItemMeta(blockStateMeta);
		}
		itemMeta = itemStack.getItemMeta();
		if (itemMeta instanceof BlockDataMeta blockDataMeta)
		{
			blockDataMeta.setBlockData(block.getBlockData());
			itemStack.setItemMeta(blockDataMeta);
		}
		Location location = block.getLocation();
		World world = location.getWorld();
		int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
		NBTItem nbtItem = new NBTItem(itemStack, true);
		NBTCompound nbtCompound = nbtItem.addCompound("block_location_info");
		nbtCompound.setString("world", world.getName());
		nbtCompound.setInteger("x", x);
		nbtCompound.setInteger("y", y);
		nbtCompound.setInteger("z", z);
		return itemStack;
	}

	public static boolean isBlockStateMetadatable(@NotNull Material type)
	{
		return type.isItem() && new ItemStack(type).getItemMeta() instanceof BlockStateMeta;
	}

	public static boolean predicateItem(@NotNull ItemStack itemStack, @NotNull String nbt)
	{
		try
		{
			itemStack = itemStack.clone();
			ItemLore4.setItemLore(itemStack);
			NBTContainer nbtContainer = new NBTContainer(nbt);
			NBTItem nbtItem = new NBTItem(itemStack);
			nbtItem.mergeCompound(nbtContainer);
			return nbtItem.getItem().equals(itemStack);
		}
		catch (Exception ignored)
		{
			return false;
		}
	}

	/**
	 * 음표에 따른 음높이 문자열을 반환합니다.
	 *
	 * @param note
	 * 		음
	 * @return 문자열
	 */
	public static String getNoteString(int note)
	{
		return switch (note)
		{
			case 0 -> "낮은 파#(F#3)";
			case 1 -> "낮은 솔(G3)";
			case 2 -> "낮은 솔#(G#3)";
			case 3 -> "낮은 라(A3)";
			case 4 -> "낮은 라#(A#3)";
			case 5 -> "낮은 시(B3)";
			case 6 -> "도(C4)";
			case 7 -> "도#(C#4)";
			case 8 -> "레(D4)";
			case 9 -> "레#(D#4)";
			case 10 -> "미(E4)";
			case 11 -> "파(F4)";
			case 12 -> "파#(F#4)";
			case 13 -> "솔(G4)";
			case 14 -> "솔#(G#4)";
			case 15 -> "라(A4)";
			case 16 -> "라#(A#4)";
			case 17 -> "시(B4)";
			case 18 -> "높은 도(C5)";
			case 19 -> "높은 도#(C#5)";
			case 20 -> "높은 레(D5)";
			case 21 -> "높은 레#(D#5)";
			case 22 -> "높은 미(E5)";
			case 23 -> "높은 파(F5)";
			case 24 -> "높은 파#(F#5)";
			default -> note + "";
		};
	}

	@NotNull
	public static List<Component> getItemInfoAsComponents(@NotNull ItemStack itemStack, @Nullable Component tag, boolean separator)
	{
		return getItemInfoAsComponents(itemStack, null, tag, separator);
	}

	@NotNull
	public static List<Component> getItemInfoAsComponents(@NotNull ItemStack itemStack, @Nullable Object param, @Nullable Component tag, boolean separator)
	{
		itemStack = itemStack.clone();
		if (param instanceof ItemLoreView view && UserData.SHOW_ITEM_LORE.getBoolean(view.getPlayer()))
		{
			ItemLore.setItemLore(itemStack, false, param);
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta instanceof FireworkMeta fireworkMeta)
		{
			fireworkMeta.clearEffects();
			itemStack.setItemMeta(fireworkMeta);
		}
		List<Component> components = new ArrayList<>();
		if (separator)
		{
			components.add(ComponentUtil.create("&8" + Constant.SEPARATOR));
		}
		if (tag != null)
		{
			components.add(tag);
		}
		Component itemName = ItemNameUtil.itemName(itemStack, NamedTextColor.WHITE);
		if (itemName.decoration(TextDecoration.ITALIC) == State.NOT_SET)
		{
			itemName = itemName.decoration(TextDecoration.ITALIC, State.FALSE);
		}
		components.add(itemName);
		if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore())
		{
			List<Component> lore = itemStack.getItemMeta().lore();
			if (lore != null)
			{
				for (int i = 0; i < lore.size(); i++)
				{
					if (i == 50)
					{
						components.add(ComponentUtil.translate("&7&ocontainer.shulkerBox.more", lore.size() - i));
						break;
					}
					components.add(lore.get(i));
				}
			}
		}
		if (separator)
		{
			components.add(ComponentUtil.create("&8" + Constant.SEPARATOR));
		}
		return components;
	}

	@NotNull
	public static Block getExactBlockFromWithLimited(@NotNull Location location, @NotNull Collection<Material> include)
	{
		Collection<Material> values = new ArrayList<>(Arrays.asList(Material.values()));
		values.removeAll(include);
		return getExactBlockFrom(location, values);
	}

	@NotNull
	public static Block getExactBlockFrom(@NotNull Location location, @Nullable Collection<Material> exceptions)
	{
		Block block = location.getBlock();
		if (exceptions != null)
		{
			if (!exceptions.contains(block.getType()))
			{
				return block;
			}
			block = new Location(location.getWorld(), Math.round(location.getX()), Math.round(location.getY()), Math.round(location.getZ())).getBlock();
			if (!exceptions.contains(block.getType()))
			{
				return block;
			}
			for (int x = location.getBlockX() - 1; x <= location.getBlockX() + 1; x++)
			{
				for (int y = location.getBlockY() - 1; y <= location.getBlockY() + 1; y++)
				{
					for (int k = location.getBlockZ() - 1; k <= location.getBlockZ() + 1; k++)
					{
						block = location.getWorld().getBlockAt(x, y, k);
						if (!exceptions.contains(block.getType()))
						{
							return block;
						}
					}
				}
			}
		}
		return block;
	}

	@NotNull
	private static Material getAnimatedMaterial(@NotNull Collection<Material> materials)
	{
		List<Material> newList = new ArrayList<>(materials);
		newList.removeIf(m -> !m.isItem() || m.isAir());
		return Method2.getAnimated(newList, 2000);
	}

	@NotNull
	private static ItemStack getAnimatedItemStack(@Nullable Collection<ItemStack> itemStacks,
			@Nullable Collection<Material> materials)
	{
		List<ItemStack> newList = new ArrayList<>();
		if (itemStacks != null)
		{
			newList.addAll(itemStacks);
		}
		if (materials != null)
		{
			for (Material material : materials)
			{
				if (!material.isItem() || material.isAir())
				{
					continue;
				}
				newList.add(new ItemStack(material));
			}
		}
		return Method2.getAnimated(newList, 2000);
	}

	/**
	 * gets an itemstack preview depending on the predicate nbt
	 *
	 * @param predicate
	 * 		nbt
	 * @return a previes itemstack
	 */
	@NotNull
	public static ItemStack getItemStackPredicate(@NotNull String predicate)
	{
		ItemStack itemStack = new ItemStack(Material.PAPER);
		ItemMeta itemMeta = itemStack.getItemMeta();
		Component display = ComponentUtil.translate("&aPredicate: %s", predicate);
		try
		{
			NBTContainer nbtContainer = new NBTContainer(predicate);
//			String id = nbtContainer.getString(com.jho5245.cucumbery.util.storage.data.CustomMaterial.IDENDTIFER);
//			if (!id.isEmpty())
//			{
//				com.jho5245.cucumbery.util.storage.data.CustomMaterial customMaterial = com.jho5245.cucumbery.util.storage.data.CustomMaterial.itemStackOf(itemStack);
//				if (customMaterial != null)
//				{
//					itemStack = customMaterial.create();
//					display = customMaterial.getDisplayName();
//				}
//				else
//				{
//					ItemStack i = new ItemStack(Material.STONE);
//					NBTItem nbtItem = new NBTItem(i, true);
//					nbtItem.setString(com.jho5245.cucumbery.util.storage.data.CustomMaterial.IDENDTIFER, id);
//					itemStack = i;
//					display = i.getItemMeta().displayName();
//				}
//			}
//			else
			{
				NBTCompound tmi = nbtContainer.getCompound(CucumberyTag.KEY_TMI);
				if (tmi != null)
				{
					NBTCompound vanillaTags = tmi.getCompound(CucumberyTag.TMI_VANILLA_TAGS), customTags = tmi.getCompound(CucumberyTag.TMI_CUSTOM_TAGS);
					if (vanillaTags != null)
					{
						boolean containerEmpty = vanillaTags.getBoolean("container_empty");
						StringBuilder displayKey = new StringBuilder();
						List<Component> displayArgs = new ArrayList<>();
						Set<Material> matches = new HashSet<>();
						{
							for (Tag<Material> tag : Bukkit.getTags(Tag.REGISTRY_ITEMS, Material.class))
							{
								if (vanillaTags.getBoolean(tag.getKey().toString()))
								{
									boolean isInventoryHolder = false;
									if (containerEmpty)
									{
										for (Material material : tag.getValues())
										{
											ItemStack i = new ItemStack(material);
											if (i.getItemMeta() instanceof BlockStateMeta blockStateMeta && blockStateMeta.getBlockState() instanceof InventoryHolder)
											{
												isInventoryHolder = true;
												break;
											}
										}
									}
									displayKey.append("%s, ");
									Component arg = ComponentUtil.create(Cucumbery.config.getString("tag-translation." + tag.getKey(), tag.getKey().toString()));
									if (isInventoryHolder)
									{
										arg = arg.append(Component.translatable("cucumbery.container_empty", "(아이템이 들어있지 않아야함)"));
									}
									displayArgs.add(arg);
									matches.addAll(tag.getValues());
								}
							}
							if (matches.isEmpty())
							{
								for (Tag<Material> tag : Bukkit.getTags(Tag.REGISTRY_BLOCKS, Material.class))
								{
									if (vanillaTags.getBoolean(tag.getKey().toString()))
									{
										boolean isInventoryHolder = false;
										if (containerEmpty)
										{
											for (Material material : tag.getValues())
											{
												ItemStack i = new ItemStack(material);
												if (i.getItemMeta() instanceof BlockStateMeta blockStateMeta && blockStateMeta.getBlockState() instanceof InventoryHolder)
												{
													isInventoryHolder = true;
													break;
												}
											}
										}
										displayKey.append("%s, ");
										Component arg = ComponentUtil.create(Cucumbery.config.getString("tag-translation." + tag.getKey(), tag.getKey().toString()));
										if (isInventoryHolder)
										{
											arg = arg.append(Component.translatable("cucumbery.container_empty", "(아이템이 들어있지 않아야함)"));
										}
										displayArgs.add(arg);
										matches.addAll(tag.getValues());
									}
								}
							}
						}
						for (String tag : vanillaTags.getKeys())
						{
							if (tag.startsWith("material_"))
							{
								Material material = Method2.valueOf(tag.substring("material_".length()), Material.class);
								if (material != null)
								{
									displayKey.append("%s, ");
									displayArgs.add(ComponentUtil.translate("아무 %s", ItemNameUtil.itemName(material)));
									matches.add(material);
								}
								break;
							}
						}

						if (!matches.isEmpty())
						{
							if (displayKey.length() > 2)
								displayKey = new StringBuilder(displayKey.substring(0, displayKey.length() - 2));
							display = ComponentUtil.translate(displayKey.toString()).arguments(displayArgs);
							itemStack.setType(getAnimatedMaterial(matches));
						}
						else
						{
							itemStack = INVALID_ITEM.clone();
							itemMeta = itemStack.getItemMeta();
							display = itemMeta.displayName();
						}
					}
					else if (customTags != null)
					{
						if (customTags.getBoolean("cucumbery_likes"))
						{
							display = ComponentUtil.translate("굳검버리가 좋아하는 것");
							itemStack.setType(getAnimatedMaterial(
									Arrays.asList(Material.TNT, Material.TNT_MINECART, Material.COMMAND_BLOCK, Material.FLINT_AND_STEEL, Material.TURTLE_SCUTE)));
						}
//						if (customTags.getBoolean("gemstones"))
//						{
//							display = ComponentUtil.translate("아무 젬스톤");
//							ItemStack clone = getAnimatedItemStack(null,
//									Arrays.asList(
//											com.jho5245.cucumbery.util.storage.data.CustomMaterial.AMBER, com.jho5245.cucumbery.util.storage.data.CustomMaterial.JADE, com.jho5245.cucumbery.util.storage.data.CustomMaterial.MORGANITE, com.jho5245.cucumbery.util.storage.data.CustomMaterial.SAPPHIRE, com.jho5245.cucumbery.util.storage.data.CustomMaterial.TOPAZ), null);
//							itemStack.setType(clone.getType());
//							itemStack.setItemMeta(clone.getItemMeta());
//							itemMeta = itemStack.getItemMeta();
//						}
					}
					else
					{
						throw new Exception();
					}
				}

			}
		}
		catch (Exception e)
		{
			Cucumbery.getPlugin().getLogger().warning(e.getMessage());
			display = ComponentUtil.translate("&cInvalid Predicate!: %s", predicate);
		}
		itemMeta.displayName(display);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	/**
	 * 마인크래프트 명령어 형식으로 아이템을 제작합니다
	 * <p>
	 * <p>
	 * <p>
	 * 아이템 예시
	 * <p>
	 * stone = {@link Material#STONE}
	 * <p>
	 * minecraft:diamond_sowrd = {@link Material#DIAMOND_SWORD}
	 * <p>
	 * stick[minecraft:custom_name:'{"text":"foo","italic":false}',minecraft:enchantments={"minecraft:sharpness":3}] = {@link Enchantment#SHARPNESS} 인챈트 레벨 3이 부여된
	 * foo 라는 이름의 {@link Material#STICK}
	 *
	 * @param sender
	 * 		제작할 때 오류 발생 시 메시지를 보낼 개체
	 * @param input
	 * 		명령어 형식의 아이템
	 * @return 제작된 아이템 또는 null
	 */
	@Nullable
	public static ItemStack createItemStack(@Nullable CommandSender sender, @NotNull String input)
	{
		return createItemStack(sender, input, true);
	}

	/**
	 * 마인크래프트 명령어 형식으로 아이템을 제작합니다
	 * <p>
	 * <p>
	 * <p>
	 * 아이템 예시
	 * <p>
	 * stone = {@link Material#STONE}
	 * <p>
	 * minecraft:diamond_sowrd = {@link Material#DIAMOND_SWORD}
	 * <p>
	 * stick[minecraft:custom_name:'{"text":"foo","italic":false}',minecraft:enchantments={"minecraft:sharpness":3}] = {@link Enchantment#SHARPNESS} 인챈트 레벨 3이 부여된
	 * foo 라는 이름의 {@link Material#STICK}
	 *
	 * @param sender
	 * 		제작할 때 오류 발생 시 메시지를 보낼 개체
	 * @param input
	 * 		명령어 형식의 아이템
	 * @param notice
	 * 		제작 실패 시 오류 메시지 출력 여부
	 * @return 제작된 아이템 또는 null
	 */
	@Nullable
	public static ItemStack createItemStack(@Nullable CommandSender sender, @NotNull String input, boolean notice)
	{
		try
		{
			if (!input.contains("{") && input.endsWith("}"))
			{
				if (sender != null && notice)
				{
					MessageUtil.sendError(sender, "command.expected.separator");
				}
				return null;
			}
			if (!input.matches("(.*)(.[a-z0-9_}-])$"))
			{
				if (sender != null && notice)
				{
					MessageUtil.sendError(sender, "command.expected.separator");
				}
				return null;
			}
			ItemStack itemStack = Bukkit.getItemFactory().createItemStack(input);
			if (!ItemStackUtil.itemExists(itemStack))
			{
				if (sender != null && notice)
				{
					MessageUtil.sendError(sender, "argument.item.id.invalid", input.split("\\{")[0]);
				}
				return null;
			}
			return itemStack;
		}
		catch (Exception e)
		{
			if (sender != null && notice)
			{
				Throwable t = e.getCause();
				MessageUtil.sendError(sender, getErrorCreateItemStack(t != null ? t : e));
			}
		}
		return null;
	}

	@NotNull
	public static TranslatableComponent getErrorCreateItemStack(@NotNull Throwable throwable)
	{
		String message = throwable.getMessage();
		if (message.startsWith("Unknown item '"))
		{
			String s = message.split("'")[1];
			try
			{
				s = s.split(":")[1];
			}
			catch (Exception ignored)
			{
			}
			return ComponentUtil.translate("argument.item.id.invalid", s);
		}
		if (message.startsWith("Expected '"))
		{
			String s = message.split("'")[1];
			return ComponentUtil.translate("parsing.expected", s);
		}

		if (message.startsWith("Invalid array type '"))
		{
			String s = message.split("'")[1];
			return ComponentUtil.translate("argument.nbt.array.invalid", s);
		}

		if (message.startsWith("Can't insert"))
		{
			String[] split = message.split(" ");
			if (message.contains("into list of"))
			{
				String s = split[2], s1 = split[6];
				return ComponentUtil.translate("argument.nbt.list.mixed", s, s1);
			}
			else
			{
				String s = split[2], s1 = split[4];
				return ComponentUtil.translate("argument.nbt.array.mixed", s, s1);
			}
		}
		if (message.startsWith("Expected key"))
		{
			return ComponentUtil.translate("argument.nbt.expected.key");
		}
		if (message.startsWith("Expected value"))
		{
			return ComponentUtil.translate("argument.nbt.expected.value");
		}
		return ComponentUtil.translate("");
	}

	public static void setTexture(@NotNull SkullMeta skullMeta, @NotNull String url)
	{
		String base64Data = getTextureBase64(url);
		PlayerProfile playerProfile = Bukkit.createProfile(UUID.fromString("0-0-0-0-0"), null);
		playerProfile.setProperty(new ProfileProperty("textures", base64Data));
		skullMeta.setPlayerProfile(playerProfile);
	}

	public static String getTextureBase64(String url)
	{
		if (url.startsWith("https://textures.minecraft.net/texture/"))
		{
			url = url.substring("https://textures.minecraft.net/texture/".length());
		}
		String decodedString;
		try
		{
			decodedString = new String(Base64.getDecoder().decode(url));
		}
		catch (Exception e)
		{
			decodedString = "";
		}
		String base64Data;
		if (decodedString.startsWith("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/"))
		{
			base64Data = url;
		}
		else
		{
			if (!url.startsWith("http://textures.minecraft.net/texture/"))
			{
				url = "http://textures.minecraft.net/texture/" + url;
			}
			String JSONData = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
			base64Data = Base64.getEncoder().encodeToString(JSONData.getBytes());
		}
		return base64Data;
	}

	/**
	 * 플레이어의 인벤토리에 있는 모든 아이템의 설명을 새로고침합니다.
	 *
	 * @param player
	 * 		인벤토리에 있는 모든 아이템의 설명을 업데이트할 플레이어
	 */
	public static void updateInventory(@NotNull Player player)
	{
		updateInventory(player, false, true);
	}

	/**
	 * 플레이어의 인벤토리에 있는 모든 아이템의 설명을 새로고침합니다.
	 *
	 * @param player
	 * 		인벤토리에 있는 모든 아이템의 설명을 업데이트할 플레이어
	 */
	public static void updateInventory(@NotNull Player player, boolean callAPI, boolean resendPacket)
	{
		InventoryView openInventory = player.getOpenInventory();
		Inventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getSize(); i++)
		{
			ItemStack item = inventory.getItem(i);
			if (item == null)
			{
				continue;
			}
			if (CustomMaterial.itemStackOf(item) != null)
			{
				ItemLore.setItemLore(item, ItemLoreView.of(player));
			}
			ItemLore.removeItemLore(item, RemoveFlag.create().removeItemFlags());
		}
		inventory = openInventory.getTopInventory();
		for (int i = 0; i < inventory.getSize(); i++)
		{
			ItemStack item = inventory.getItem(i);
			if (item == null)
			{
				continue;
			}
			if (CustomMaterial.itemStackOf(item) != null)
			{
				ItemLore.setItemLore(item, ItemLoreView.of(player));
			}
			ItemLore.removeItemLore(item, RemoveFlag.create().removeItemFlags());
		}
		if (CustomMaterial.itemStackOf(player.getItemOnCursor()) != null)
		{
			player.setItemOnCursor(ItemLore.setItemLore(player.getItemOnCursor(), ItemLoreView.of(player)));
		}
		player.setItemOnCursor(ItemLore.removeItemLore(player.getItemOnCursor(), RemoveFlag.create().removeItemFlags()));
		if (resendPacket && Cucumbery.using_ProtocolLib)
		{
			ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
			boolean showItemLore = UserData.SHOW_ITEM_LORE.getBoolean(player);
			PacketContainer packet = new PacketContainer(Play.Server.WINDOW_ITEMS);
			InventoryView inventoryView = player.getOpenInventory();
			InventoryType inventoryType = inventoryView.getType();
			Inventory top = inventoryView.getTopInventory(), bottom = inventoryView.getBottomInventory();
//			MessageUtil.broadcastDebug(inventoryType + ", top: " + top.getSize() + ", bottom: " + bottom.getSize(), "type:" + inventoryType);
			List<ItemStack> itemStackList = new ArrayList<>(Arrays.asList(top.getContents()));
			int windowID;
			if (inventoryType == InventoryType.CRAFTING || inventoryType == InventoryType.CREATIVE)
			{
				windowID = 0;
				EntityEquipment entityEquipment = player.getEquipment();
				itemStackList.add(entityEquipment.getHelmet());
				itemStackList.add(entityEquipment.getChestplate());
				itemStackList.add(entityEquipment.getLeggings());
				itemStackList.add(entityEquipment.getBoots());
			}
			else
			{
				windowID = UserData.WINDOW_ID.getInt(player.getUniqueId());
			}
			for (int i = 9; i < 36; i++)
			{
				itemStackList.add(bottom.getItem(i));
			}
			for (int i = 0; i < 9; i++)
			{
				itemStackList.add(bottom.getItem(i));
			}
			for (int i = 0; i < itemStackList.size(); i++)
			{
				ItemStack itemStack = itemStackList.get(i);
				if (ItemStackUtil.itemExists(itemStack))
				{
					itemStack = itemStack.clone();
					if (showItemLore)
					{
						ItemLore.setItemLore(itemStack, ItemLoreView.of(player));
					}
					else
					{
						ItemLore.removeItemLore(itemStack);
					}
				}
				else
				{
					itemStackList.set(i, AIR);
				}
			}
			packet.getItemModifier().write(0, player.getItemOnCursor().clone());
			packet.getItemListModifier().write(0, itemStackList);
			// MessageUtil.broadcastDebug("sent by plugin id: " + windowID);
			packet.getIntegers().write(0, windowID);
			protocolManager.sendServerPacket(player, packet);
		}
		if (callAPI)
		{
			player.updateInventory();
		}
	}

	/**
	 * 플레이어의 인벤토리에 있는 아이템의 설명을 새로고침합니다.
	 *
	 * @param player
	 * 		인벤토리에 있는 아이템의 설명을 업데이트할 플레이어
	 * @param item
	 * 		설명을 새로고침할 아이템
	 */
	public static void updateInventory(@NotNull Player player, @NotNull ItemStack item)
	{
		ItemLore.removeItemLore(item);
	}

	public static Boolean shouldShowCustomName(@NotNull ItemStack itemStack)
	{
		NBTCompound itemTag = NBTAPI.getMainCompound(itemStack);
		NBTList<String> hideFlags = NBTAPI.getStringList(itemTag, CucumberyTag.HIDE_FLAGS_KEY);
		if (NBTAPI.arrayContainsValue(hideFlags, Constant.CucumberyHideFlag.CUSTOM_NAME))
			return false;
//		com.jho5245.cucumbery.util.storage.data.CustomMaterial customMaterial = com.jho5245.cucumbery.util.storage.data.CustomMaterial.itemStackOf(itemStack);
//		// 일부 커스텀 아이템은 별도의 이름 표기 규칙을 가짐 (적용 후 return)
//		{
//			if (customMaterial != null)
//			{
//				switch (customMaterial)
//				{
//					case CORE_GEMSTONE, CORE_GEMSTONE_EXPERIENCE, CORE_GEMSTONE_MIRROR, CORE_GEMSTONE_MITRA ->
//					{
//						return false;
//					}
//					case RUNE_DESTRUCTION, RUNE_EARTHQUAKE ->
//					{
//						return true;
//					}
//				}
//			}
//		}
		return null;
	}

	/**
	 * {@link HoverEvent}의 {@link ShowItem}에서 {@link ItemStack}을 반환합니다.
	 *
	 * @param showItem
	 * 		아이템을 가져올 {@link ShowItem}
	 * @return 해당하는 아이템
	 */
	@NotNull
	public static ItemStack getItemStackFromHoverEvent(ShowItem showItem)
	{
		return Bukkit.getItemFactory().createItemStack(showItem.item() + getComponentsFromHoverEvent(showItem));
	}

	/**
	 * {@link HoverEvent}의 {@link ShowItem}에서 {@link ItemStack}의 컴포넌트를 문자열 형태로 반환합니다.
	 * <p>반환된 문자열은 /give 명령어의 아이템 인자로 사용할 수 있습니다.</p>
	 * <p>예시: <code>[minecraft:item_model="minecraft:campfire"]</code></p>
	 *
	 * @param showItem
	 * 		아이템을 가져올 {@link ShowItem}
	 * @return 문자열 형태의 아이템의 컴포넌트 or 컴포넌트가 없을 경우 빈 문자열
	 */
	@NotNull
	public static String getComponentsFromHoverEvent(ShowItem showItem)
	{
		Map<Key, DataComponentValue> map = showItem.dataComponents();
		StringBuilder nbt = new StringBuilder();
		List<String> strings = new ArrayList<>();
		map.forEach((key, dataComponentValue) ->
		{
			try
			{
				switch (dataComponentValue)
				{
					case GsonDataComponentValue componentValue -> strings.add(key.asString() + "=" + componentValue.element() + ",");
					case Removed ignored -> strings.add("!" + key.asString() + ",");
					case null -> MessageUtil.consoleSendMessage("dataComponentValue null with key: " + key.asString());
					default ->
					{
						// with reflection
						Class<?> clazz = dataComponentValue.getClass();
						switch (clazz.getName())
						{
							case "io.papermc.paper.adventure.PaperAdventure$DataComponentValueImpl" ->
							{
								Method method = clazz.getDeclaredMethod("asBinaryTag");
								method.setAccessible(true);
								strings.add(key.asString() + "=");
								strings.add(method.invoke(dataComponentValue).toString() + ",");
							}
							default -> MessageUtil.consoleSendMessage("unhandled data component class found: " + clazz.getName());
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
		for (String s : strings)
		{
			nbt.append(s);
		}
		if (nbt.isEmpty())
			return "";

		nbt = new StringBuilder("[" + nbt.substring(0, nbt.length() - 1) + "]");
		return nbt.toString();
	}

	/**
	 * {@link ItemStack}의 컴포넌트를 문자열 형태로 반환합니다.
	 *
	 * @param itemStack
	 * 		컴포넌트를 읽을 아이템
	 * @return 문자열 형태의 컴포넌트
	 */
	@NotNull
	public static String getComponentsFromItemStack(@NotNull ItemStack itemStack)
	{
		return getComponentsFromHoverEvent(itemStack.asHoverEvent().value());
	}
}


















