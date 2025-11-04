package com.jho5245.cucumbery.custom.custommaterial;

import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.no_groups.ItemCategory.Rarity;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import de.tr7zw.changeme.nbtapi.NBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CustomMaterial implements Comparable<CustomMaterial>, Translatable
{
	private static final HashMap<NamespacedKey, CustomMaterial> customMaterials = new HashMap<>();

	public static final CustomMaterial AMBER = new CustomMaterial(Material.ORANGE_DYE, "key:item.cucumbery.amber|호박",
			"key:itemGroup.cucumbery_test_item|테스트 아이템"), WEATHER_FORECAST = new CustomMaterial(Material.ENDER_EYE, "key:item.cucumbery.weather_forecast|날씨를 알려주는 눈",
			Rarity.ELITE, CreativeCategory.TOOLS), WNYNYA_ORE = new CustomMaterial(Material.BARRIER, Material.PLAYER_HEAD,
			"rgb200,100,255;key:block.cucumbery.wnynya_ore|완YEE 광석", Rarity.RARE, "itemGroup.natural"), TEST_ITEM = new CustomMaterial(Material.DIAMOND,
			"key:item.cucumbery.test_item|테스트 아이템", "key:itemGroup.cucumbery_test_item|테스트 아이템");

	/**
	 * Internal registriation. must NOT be called by other plugins!
	 */
	public static void register()
	{
		register(AMBER, WEATHER_FORECAST, WNYNYA_ORE, TEST_ITEM);
	}

	public static void unregister()
	{
		customMaterials.clear();
	}

	public static void unregister(Plugin plugin)
	{
		customMaterials.keySet().forEach(key ->
		{
			if (plugin.getName().equals(key.getNamespace()))
			{
				customMaterials.remove(key);
			}
		});
	}

	public static boolean register(CustomMaterial... customMaterial)
	{
		boolean success = true;
		for (CustomMaterial newMaterial : customMaterial)
		{
			if (customMaterials.containsKey(newMaterial.key))
			{
				success = false;
				MessageUtil.sendWarn(Bukkit.getConsoleSender(), "중복되는 CustomMaterial이 이미 존재하여 등록에 실패했습니다: %s", newMaterial.key);
				continue;
			}
			customMaterials.put(newMaterial.key, newMaterial);
		}
		return success;
	}

	public static CustomMaterial getByKey(@NotNull NamespacedKey key)
	{
		return customMaterials.get(key);
	}

	public static final String IDENDTIFER = "internal_material_id";

	private final NamespacedKey key;

	private final Material realMaterial, displayMaterial;

	private final Component displayName;

	private final Rarity rarity;

	private final Component category;

	private CustomMaterial(@NotNull Material displayMaterial, @NotNull String displayNameString, @NotNull String categoryString)
	{
		this(displayNameString.split("cucumbery\\.")[1].split("\\|")[0], Material.DEBUG_STICK, displayMaterial, displayNameString, Rarity.NORMAL, categoryString);
	}

	private CustomMaterial(@NotNull Material displayMaterial, @NotNull String displayNameString, @NotNull Rarity rarity, @NotNull String categoryString)
	{
		this(displayNameString.split("cucumbery\\.")[1].split("\\|")[0], Material.DEBUG_STICK, displayMaterial, ComponentUtil.translate(displayNameString), rarity,
				ComponentUtil.translate(categoryString));
	}

	private CustomMaterial(@NotNull String keyString, @NotNull Material displayMaterial, @NotNull String displayNameString, @NotNull Rarity rarity,
			@NotNull String categoryString)
	{
		this(keyString, Material.DEBUG_STICK, displayMaterial, ComponentUtil.translate(displayNameString), rarity, ComponentUtil.translate(categoryString));
	}

	private CustomMaterial(@NotNull Material displayMaterial, @NotNull String displayNameString, @NotNull Rarity rarity,
			@NotNull CreativeCategory creativeCategory)
	{
		this(displayNameString.split("cucumbery\\.")[1].split("\\|")[0], Material.DEBUG_STICK, displayMaterial, ComponentUtil.translate(displayNameString), rarity,
				ComponentUtil.translate(creativeCategory.translationKey()));
	}

	private CustomMaterial(@NotNull Material realMaterial, @NotNull Material displayMaterial, @NotNull String displayNameString, @NotNull Rarity rarity,
			@NotNull String categoryString)
	{
		this(displayNameString.split("cucumbery\\.")[1].split("\\|")[0], realMaterial, displayMaterial, ComponentUtil.translate(displayNameString), rarity,
				ComponentUtil.translate(categoryString));
	}

	private CustomMaterial(@NotNull String keyString, @NotNull Material realMaterial, Material displayMaterial, @NotNull String displayNameString,
			@NotNull Rarity rarity, @NotNull String categoryString)
	{
		this(keyString, realMaterial, displayMaterial, ComponentUtil.translate(displayNameString), rarity, ComponentUtil.translate(categoryString));
	}

	private CustomMaterial(@NotNull String keyString, @NotNull Material realMaterial, Material displayMaterial, @NotNull Component displayName,
			@NotNull Rarity rarity, @NotNull Component category)
	{
		this(new NamespacedKey("cucumbery", keyString), realMaterial, displayMaterial, displayName, rarity, category);
	}

	public CustomMaterial(@NotNull NamespacedKey key, @NotNull Material displayMaterial, @NotNull String displayNameString, @NotNull String categoryString)
	{
		this(key, Material.DEBUG_STICK, displayMaterial, ComponentUtil.translate(displayNameString), Rarity.NORMAL, ComponentUtil.translate(categoryString));
	}

	public CustomMaterial(@NotNull NamespacedKey key, @NotNull Material displayMaterial, @NotNull String displayNameString, @NotNull Rarity rarity,
			@NotNull String categoryString)
	{
		this(key, Material.DEBUG_STICK, displayMaterial, ComponentUtil.translate(displayNameString), rarity, ComponentUtil.translate(categoryString));
	}

	public CustomMaterial(@NotNull NamespacedKey key, @NotNull Material displayMaterial, @NotNull Component displayName, @NotNull Rarity rarity,
			@NotNull Component category)
	{
		this(key, Material.DEBUG_STICK, displayMaterial, displayName, rarity, category);
	}

	public CustomMaterial(@NotNull NamespacedKey key, @NotNull Material realMaterial, @Nullable Material displayMaterial, @NotNull Component displayName,
			@NotNull Rarity rarity, @NotNull String categoryString)
	{
		this(key, realMaterial, displayMaterial, displayName, rarity, ComponentUtil.translate(categoryString));
	}

	public CustomMaterial(@NotNull NamespacedKey key, @Nullable Material displayMaterial, @NotNull Component displayName,
			@NotNull Rarity rarity, @NotNull String categoryString)
	{
		this(key, Material.DEBUG_STICK, displayMaterial, displayName, rarity, ComponentUtil.translate(categoryString));
	}

	public CustomMaterial(@NotNull NamespacedKey key, @NotNull Material realMaterial, @Nullable Material displayMaterial, @NotNull String displayNameString, @NotNull String categoryString)
	{
		this(key, realMaterial, displayMaterial, ComponentUtil.translate(displayNameString), Rarity.NORMAL, ComponentUtil.translate(categoryString));
	}

	public CustomMaterial(@NotNull NamespacedKey key, @NotNull Material realMaterial, @Nullable Material displayMaterial, @NotNull Component displayName,
			@NotNull Rarity rarity, @NotNull Component category)
	{
		this.key = key;
		this.realMaterial = realMaterial;
		this.displayMaterial = displayMaterial;
		this.displayName = displayName;
		this.rarity = rarity;
		this.category = category;
	}

	public NamespacedKey getKey()
	{
		return key;
	}

	/**
	 * minecraft:item_model component로 지정될 표시용 Material을 반환합니다.
	 *
	 * @return 표시용 Material, 없을 경우 <code>null</code>
	 */
	@Nullable
	public Material getDisplayMaterial()
	{
		return displayMaterial;
	}

	/**
	 * 실제 이 아이템의 Material을 반환합니다.
	 *
	 * @return 아이템의 실제 Material
	 */
	@NotNull
	public Material getRealMaterial()
	{
		return realMaterial;
	}

	@NotNull
	public Component getDisplayName()
	{
		return displayName;
	}

	@NotNull
	public Rarity getRarity()
	{
		return rarity;
	}

	@NotNull
	public Component getCategory()
	{
		return category;
	}

	@Nullable
	public CustomMaterial getSmeltedItem()
	{
		return null;
	}

	@Nullable
	public Material getSmeltedItemVanilla()
	{
		if (this == WNYNYA_ORE)
		{
			return Material.TNT;
		}
		return null;
	}

	@NotNull
	public ItemStack create()
	{
		return create(1);
	}

	@NotNull
	public ItemStack create(int amount)
	{
		return create(amount, true);
	}

	@NotNull
	public ItemStack create(boolean nbtOnly)
	{
		return create(1, nbtOnly);
	}

	@NotNull
	public ItemStack create(int amount, boolean nbtOnly)
	{
		ItemStack itemStack = new ItemStack(realMaterial, amount);
		NBT.modify(itemStack, nbt ->
		{
			nbt.setString(IDENDTIFER, key.toString());
		});
		ItemLore.setItemLore(itemStack, nbtOnly);
		return itemStack;
	}

	public boolean isBlock()
	{
		return realMaterial.isBlock();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CustomMaterial that = (CustomMaterial) o;
		return that.key.equals(this.key);
	}

	@Override
	public int hashCode()
	{
		return key.hashCode();
	}

	@Override
	public String toString()
	{
		return key.toString();
	}

	@Override
	public int compareTo(@NotNull CustomMaterial o)
	{
		return key.compareTo(o.key);
	}

	/**
	 * Gets the translation key.<p> this will return <code>"(item|block).{@link NamespacedKey#getNamespace()}.{@link NamespacedKey#getKey()}"</code><p> Example:
	 * item.cucumbery.test_item for items, block.cucumbery.test_block for blocks.
	 * </p>
	 *
	 * @return the translation key
	 */
	@Override
	public @NotNull String translationKey()
	{
		return isBlock() ? "block." : "item." + key.getNamespace() + "." + key.getKey();
	}

	@Nullable
	public static CustomMaterial itemStackOf(@Nullable ItemStack itemStack)
	{
		if (!ItemStackUtil.itemExists(itemStack))
			return null;
		String rawKey = NBT.get(itemStack, nbt ->
		{
			return nbt.getString(IDENDTIFER);
		});
		if (rawKey == null)
			return null;
		try
		{
			String[] split = rawKey.split(":");
			NamespacedKey key = new NamespacedKey(split[0], split[1]);
			return customMaterials.get(key);
		}
		catch (Exception ignored)
		{
			return null;
		}
	}

	/**
	 * Gets all keys from registered custom materials.
	 *
	 * @return keyset from all registered custom material.
	 */
	@NotNull
	public static Set<NamespacedKey> keySet()
	{
		return new HashSet<>(customMaterials.keySet());
	}

	/**
	 * Gets all registered custom materials.
	 *
	 * @return all registered custom materials
	 */
	@NotNull
	public static Collection<CustomMaterial> values()
	{
		return new ArrayList<>(customMaterials.values());
	}
}
