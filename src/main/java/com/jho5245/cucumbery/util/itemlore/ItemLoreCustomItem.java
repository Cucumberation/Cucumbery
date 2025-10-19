package com.jho5245.cucumbery.util.itemlore;

import com.google.common.collect.Multimap;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.custom_mining.MiningManager;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant.ExtraTag;
import com.jho5245.cucumbery.util.storage.data.Constant.RestrictionType;
import com.jho5245.cucumbery.util.storage.data.CustomMaterial;
import com.jho5245.cucumbery.util.storage.data.Variable;
import com.jho5245.cucumbery.util.storage.no_groups.ItemCategory.Rarity;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import com.jho5245.cucumbery.util.storage.no_groups.RecipeChecker;
import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.handler.NBTHandlers;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.util.ARGBLike;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemLoreCustomItem
{
	private static final NamespacedKey ATTRIBUTE_IDENTIFIER_1_0 = new NamespacedKey(Cucumbery.getPlugin(), "attribute_identifier_1");

	private static final NamespacedKey ATTRIBUTE_IDENTIFIER_1_1 = new NamespacedKey(Cucumbery.getPlugin(), "attribute_identifier_11");

	private static final NamespacedKey ATTRIBUTE_IDENTIFIER_1_2 = new NamespacedKey(Cucumbery.getPlugin(), "attribute_identifier_12");

	private static final NamespacedKey ATTRIBUTE_IDENTIFIER_1_3 = new NamespacedKey(Cucumbery.getPlugin(), "attribute_identifier_13");

	private static final NamespacedKey ATTRIBUTE_IDENTIFIER_1_4 = new NamespacedKey(Cucumbery.getPlugin(), "attribute_identifier_14");

	private static final NamespacedKey ATTRIBUTE_IDENTIFIER_1_5 = new NamespacedKey(Cucumbery.getPlugin(), "attribute_identifier_15");

	private static final NamespacedKey ATTRIBUTE_IDENTIFIER_2_0 = new NamespacedKey(Cucumbery.getPlugin(), "attribute_identifier_2");

	private static final NamespacedKey ATTRIBUTE_IDENTIFIER_2_1 = new NamespacedKey(Cucumbery.getPlugin(), "attribute_identifier_21");

	private static final NamespacedKey ATTRIBUTE_IDENTIFIER_2_2 = new NamespacedKey(Cucumbery.getPlugin(), "attribute_identifier_22");

	private static final NamespacedKey ATTRIBUTE_IDENTIFIER_2_3 = new NamespacedKey(Cucumbery.getPlugin(), "attribute_identifier_23");

	private static final NamespacedKey ATTRIBUTE_IDENTIFIER_3_0 = new NamespacedKey(Cucumbery.getPlugin(), "attribute_identifier_3");

	private static final NamespacedKey ATTRIBUTE_IDENTIFIER_4_0 = new NamespacedKey(Cucumbery.getPlugin(), "attribute_identifier_4");

	private static final NamespacedKey ATTRIBUTE_IDENTIFIER_4_1 = new NamespacedKey(Cucumbery.getPlugin(), "attribute_identifier_41");

	protected static void itemLore(@NotNull ItemStack itemStack, @NotNull CustomMaterial customMaterial)
	{
		Material displayMaterial = customMaterial.getDisplayMaterial();
		NBTItem nbtItem = new NBTItem(itemStack);
		NBTCompound itemTag = nbtItem.getCompound(CucumberyTag.KEY_MAIN);
		if (itemTag == null)
		{
			itemTag = nbtItem.addCompound(CucumberyTag.KEY_MAIN);
		}
		NBTCompoundList restrictionTag = itemTag.getCompoundList(CucumberyTag.ITEM_USAGE_RESTRICTIONS_KEY);
		NBTList<String> extraTags = itemTag.getStringList(CucumberyTag.EXTRA_TAGS_KEY);
		{
			int expireDateInDays = customMaterial.getExpireDateInDays();
			if (expireDateInDays > 0)
			{
				itemTag.setString(CucumberyTag.EXPIRE_DATE_KEY, "~" + expireDateInDays + "일");
			}
			if (customMaterial.isUntradeable())
			{
				NBTCompound nbtCompound = new NBTContainer();
				if (!NBTAPI.isRestricted(itemStack, RestrictionType.NO_TRADE))
				{
					nbtCompound.setString(CucumberyTag.VALUE_KEY, RestrictionType.NO_TRADE.toString());
					nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, false);
					restrictionTag.addCompound(nbtCompound);
				}
			}
			CustomMaterial origin = customMaterial.getOrigin();
			if (origin != null)
			{
				nbtItem.setString(CustomMaterial.IDENDTIFER, origin.toString().toLowerCase());
				itemLore(itemStack, origin);
				return;
			}
		}
		{
			NBTCompound nbtCompound = new NBTContainer();
			if (RecipeChecker.hasCraftingRecipe(displayMaterial))
			{
				if (!NBTAPI.isRestricted(itemStack, RestrictionType.NO_CRAFT))
				{
					nbtCompound.setString(CucumberyTag.VALUE_KEY, RestrictionType.NO_CRAFT.toString());
					nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, true);
					restrictionTag.addCompound(nbtCompound);
				}
			}
			if (RecipeChecker.hasSmeltingRecipe(new ItemStack(displayMaterial)))
			{
				if (!NBTAPI.isRestricted(itemStack, RestrictionType.NO_SMELT))
				{
					nbtCompound.setString(CucumberyTag.VALUE_KEY, RestrictionType.NO_SMELT.toString());
					nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, true);
					restrictionTag.addCompound(nbtCompound);
				}
			}
			if (displayMaterial == Material.PLAYER_HEAD)
			{
				switch (customMaterial)
				{
					default ->
					{
						if (!NBTAPI.isRestricted(itemStack, RestrictionType.NO_EQUIP))
						{
							nbtCompound.setString(CucumberyTag.VALUE_KEY, RestrictionType.NO_EQUIP.toString());
							nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, true);
							restrictionTag.addCompound(nbtCompound);
						}
					}
					case DOEHAERIM_BABO, BAMIL_PABO ->
					{

					}
				}
			}
			if (ItemStackUtil.isPlacable(displayMaterial))
			{
				switch (customMaterial)
				{
					case DOEHAERIM_BABO, BAMIL_PABO, TNT_I_WONT_LET_YOU_GO, DIAMOND_BLOCK_DECORATIVE, NETHERITE_BLOCK_DECORATIVE, BEACON_DECORATIVE, TNT_SUPERIOR,
							 TNT_COMBAT, TNT_DRAIN, TNT_DONUT, WNYNYA_ORE, REDSTONE_BLOCK_INSTA_BREAK, CUSTOM_CRAFTING_TABLE, BLUE_NUMBER_BLOCK, I_WONT_LET_YOU_GO_BLOCK, SUS,
							 DIA_RON_LAPI_RED_COAL_GOLD_ORE, RE_PPER_LD_RALD_AMON_YST_RGANITE_BLOCK ->
					{
						if (!NBTAPI.arrayContainsValue(extraTags, ExtraTag.PRESERVE_BLOCK_NBT))
						{
							extraTags.add(ExtraTag.PRESERVE_BLOCK_NBT.toString());
							// 커스텀 블록은 기존 마크의 블록 드롭 테이블 규칙을 무시하고 자기 자신을 드롭
							nbtItem.setBoolean(MiningManager.IGNORE_VANILLA_MODIFICATION, true);
						}
					}
					default ->
					{
						if (customMaterial.isVerticalSlab())
						{
							if (!NBTAPI.arrayContainsValue(extraTags, ExtraTag.PRESERVE_BLOCK_NBT))
							{
								extraTags.add(ExtraTag.PRESERVE_BLOCK_NBT.toString());
								// 커스텀 블록은 기존 마크의 블록 드롭 테이블 규칙을 무시하고 자기 자신을 드롭
								nbtItem.setBoolean(MiningManager.IGNORE_VANILLA_MODIFICATION, true);
							}
						}
						else if (!NBTAPI.isRestricted(itemStack, RestrictionType.NO_PLACE))
						{
							nbtCompound.setString(CucumberyTag.VALUE_KEY, RestrictionType.NO_PLACE.toString());
							nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, true);
							restrictionTag.addCompound(nbtCompound);
						}
					}
				}
			}
			if (ItemStackUtil.isBrewable(displayMaterial))
			{
				if (!NBTAPI.isRestricted(itemStack, RestrictionType.NO_BREW))
				{
					nbtCompound.setString(CucumberyTag.VALUE_KEY, RestrictionType.NO_BREW.toString());
					nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, true);
					restrictionTag.addCompound(nbtCompound);
				}
			}
			if (ItemStackUtil.isEdible(displayMaterial))
			{
				switch (customMaterial)
				{
					case BAD_APPLE, BREAD_DIRTY ->
					{
						NBTCompound foodTag = itemTag.addCompound(CucumberyTag.FOOD_KEY);
						foodTag.setInteger(CucumberyTag.FOOD_LEVEL_KEY, -20);
						foodTag.setDouble(CucumberyTag.SATURATION_KEY, -20d);
					}
					case TOMATO ->
					{
						NBTCompound foodTag = itemTag.addCompound(CucumberyTag.FOOD_KEY);
						foodTag.setInteger(CucumberyTag.FOOD_LEVEL_KEY, 6);
						foodTag.setDouble(CucumberyTag.SATURATION_KEY, 0.2d);
					}
					// 먹을 수 있는 음식
					case YOUPEOPLEGAME_DAMP_COOKIE_POTION, YOUPEOPLEGAME_MOIST_COOKIE_BOOSTER, YOUPEOPLEGAME_SUPER_MOIST_COOKIE_BOOSTER -> {}
					default ->
					{
						if (!NBTAPI.isRestricted(itemStack, RestrictionType.NO_CONSUME))
						{
							nbtCompound.setString(CucumberyTag.VALUE_KEY, RestrictionType.NO_CONSUME.toString());
							nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, true);
							restrictionTag.addCompound(nbtCompound);
						}
					}
				}
			}
			if (ItemStackUtil.getFuelTimeInSecond(displayMaterial) > 0)
			{
				switch (customMaterial)
				{
					case ENCHANTED_COAL ->
					{
						nbtItem.setInteger("BurnTime", 20 * 60 * 20);
						nbtItem.setDouble("CookSpeed", 3d);
					}
					case ENCHANTED_COAL_BLOCK ->
					{
						nbtItem.setInteger("BurnTime", 20 * 60 * 25);
						nbtItem.setDouble("CookSpeed", 15d);
					}
					default ->
					{
						if (!customMaterial.isVerticalSlab() && !NBTAPI.isRestricted(itemStack, RestrictionType.NO_FUEL))
						{
							nbtCompound.setString(CucumberyTag.VALUE_KEY, RestrictionType.NO_FUEL.toString());
							nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, true);
							restrictionTag.addCompound(nbtCompound);
						}
					}
				}
			}
			if (ItemStackUtil.getCompostChance(displayMaterial) > 0)
			{
				if (!NBTAPI.isRestricted(itemStack, RestrictionType.NO_COMPOSTER))
				{
					nbtCompound.setString(CucumberyTag.VALUE_KEY, RestrictionType.NO_COMPOSTER.toString());
					nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, true);
					restrictionTag.addCompound(nbtCompound);
				}
			}
			if (displayMaterial == Material.LAPIS_LAZULI)
			{
				if (!NBTAPI.isRestricted(itemStack, RestrictionType.NO_ENCHANT))
				{
					nbtCompound.setString(CucumberyTag.VALUE_KEY, RestrictionType.NO_ENCHANT.toString());
					nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, true);
					restrictionTag.addCompound(nbtCompound);
				}
			}
			switch (customMaterial)
			{
				case TODWOT_PICKAXE ->
				{
					if (!NBTAPI.isRestricted(itemStack, RestrictionType.NO_ANVIL_ENCHANTED_BOOK_ENCHANT))
					{
						nbtCompound.setString(CucumberyTag.VALUE_KEY, RestrictionType.NO_ANVIL_ENCHANTED_BOOK_ENCHANT.toString());
						nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, false);
						restrictionTag.addCompound(nbtCompound);
					}
				}
				case BEACON_DECORATIVE ->
				{
					if (!NBTAPI.isRestricted(itemStack, RestrictionType.NO_BLOCK_BEACON))
					{
						nbtCompound.setString(CucumberyTag.VALUE_KEY, RestrictionType.NO_BLOCK_BEACON.toString());
						nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, true);
						restrictionTag.addCompound(nbtCompound);
					}
				}
				case BEACON_HAT ->
				{
					NBTCompound usageTag = itemTag.addCompound(CucumberyTag.USAGE_KEY);
					usageTag.setString("EquipmentSlot", "HELMET");
				}
			}
			switch (customMaterial)
			{
				case MUSHROOM_STEW_PICKAXE, STONK, TODWOT_PICKAXE ->
				{
					if (!NBTAPI.isRestricted(itemStack, RestrictionType.NO_GRINDSTONE))
					{
						nbtCompound.setString(CucumberyTag.VALUE_KEY, RestrictionType.NO_GRINDSTONE.toString());
						nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, false);
						restrictionTag.addCompound(nbtCompound);
					}
				}
			}
		}
		// CustomNBT 설정
		{
			switch (customMaterial)
			{
				case DIA_RON_LAPI_RED_COAL_GOLD_ORE ->
				{
					nbtItem.setString("change_material", Material.GRAY_STAINED_GLASS.toString());
					nbtItem.setInteger("BlockTier", 3);
					nbtItem.setDouble("BlockHardness", 500d);
					nbtItem.setString("BreakSound", Sound.BLOCK_STONE_BREAK.toString());
					nbtItem.setString("BreakParticle", "block:tnt");
					NBTCompound nbtCompound = nbtItem.addCompound("displays");
					nbtCompound.setString("type", "player_heads");
					NBTList<String> urls = nbtCompound.getStringList("value");
					urls.clear();
					{
						urls.add("9298caead935bf415382b47574973a00120e4daa825272f0221fde528a445493");
						urls.add("27c3d5d89130f609d12653f646c3ad7f87dc117281d78f15e7e23339f57b4728");
						urls.add("271c7388f39864915e4d1f3215696aeabc12751c28b9fdaf7a796a77ca0977fe");
						urls.add("9a39b83c1f63639916ff212d11988e43ca5f857838927e814a63d7f2429835e0");
						urls.add("cfc36763bcb275df92245fa065388f3b77670fb515fcd8447fc7a50895435e5d");
						urls.add("c45106c5eddf342f41b43711d2e7e18de3f209cf90d64729980adde39aa5176e");
						urls.add("e9fabdf1debe1e4104791ab6a6536ad74b888373afaba640526e3a9af77ea54c");
						urls.add("22c3cca6a6adeb3b39c758d3201687a54351de4ffc75813e40d006eb85a087e3");
					}
				}
				case RE_PPER_LD_RALD_AMON_YST_RGANITE_BLOCK ->
				{
					nbtItem.setString("change_material", Material.GRAY_STAINED_GLASS.toString());
					nbtItem.setInteger("BlockTier", 3);
					nbtItem.setDouble("BlockHardness", 500d);
					nbtItem.setString("BreakSound", Sound.BLOCK_METAL_BREAK.toString());
					nbtItem.setString("BreakParticle", "block:soul_sand");
					nbtItem.setBoolean("perspectiveYaw", true);
					nbtItem.setBoolean("perspectiveGrid", true);
					NBTCompound nbtCompound = nbtItem.addCompound("displays");
					nbtCompound.setString("type", "player_heads");
					NBTList<String> urls = nbtCompound.getStringList("value");
					urls.clear();
					{
						urls.add("b6501bc864eaf64d408ca5474bf8d718cd15a0879ef557daaaae334cdedeb05d");
						urls.add("dec7823e9702048edd96ca7cc75d8e516dac64256b1d793d4cf5489b3e2f6e22");
						urls.add("985ac4850d62d4b707b9bb3cd8c0cda8acf005b9e15ad33df2887ff683e3981b");
						urls.add("cd211eb6c40d48e7dfd998b67d48bd5ae940bc9e381ae005426db216ccaa8d3c");
						urls.add("91aef5ddb066716c30808807343dc28e757b9e6a0e90c3ea4d6e48c0c375f39e");
						urls.add("6877235d824d3cd88c9f38ec499dc30fcb0e40d66e4cee4f799d9dd9aba82041");
						urls.add("185aaa46b4359ea95032391d6ee226e2d13b79f93bbad36d70382993c23cb23a");
						urls.add("66bdc340726d6066a6c789c3e10b6fe06c7a9d4f5f180a9c9c57ab7ce809b28");
					}
				}
				case WNYNYA_ORE ->
				{
					nbtItem.setString("change_material", Material.GRAY_STAINED_GLASS.toString());
					nbtItem.setInteger("BlockTier", 2);
					nbtItem.setDouble("BlockHardness", 500d);
					nbtItem.setString("BreakSound", Sound.BLOCK_STONE_BREAK.toString());
					nbtItem.setString("BreakParticle", "block:stone[]");
					NBTCompound nbtCompound = nbtItem.addCompound("displays");
					nbtCompound.setString("type", "player_heads");
					NBTList<String> urls = nbtCompound.getStringList("value");
					urls.clear();
					{
						urls.add("b892df651cc7aa1b2a29b0dbda49ce4faae9fd49292612751aa997ebf0dc3dac");
						urls.add("7eb1ca6f34d04a2bf7379594a4c61a48f0b2053947b366cc14947086f309f315");
						urls.add("6b09a7f9b9df42cf881a71d23bb92938e32ba68e1e8da5a22678ec8a024497c2");
						urls.add("2771d58bcd60a56ac0bbf306acb8eef399308d1ee9f0bc444b91cd8cfa54397");
						urls.add("d88ac94a21975bea9e2e43ef670566b04629164f171f2a041e476270e0e6a5ed");
						urls.add("ff176fd84f05c0b78a85d744fdae5279e3d818489394c215d357cd3b7a1a9757");
						urls.add("76bad50fe9757edd2ce2823b3af26d1f59c9d7ea31238aac92374fada2494ec1");
						urls.add("1a2643647c3f26f94c62dd2ae20cfd24ba3f8236f84d6563773ff5e8a64708c1");
					}
				}
				case SUS ->
				{
					nbtItem.setString("change_material", Material.LIGHT_GRAY_STAINED_GLASS.toString());
					nbtItem.setDouble("BlockHardness", 195d);
					nbtItem.setString("BreakSound", "custom_sus_broken");
					nbtItem.setFloat("BreakSoundPitch", 1f);
					nbtItem.setString("BreakParticle", "block:stone[]");
					NBTCompound nbtCompound = nbtItem.addCompound("displays");
					nbtCompound.setString("type", "player_heads");
					NBTList<String> urls = nbtCompound.getStringList("value");
					urls.clear();
					{
						urls.add("7ac2b6ecce29dd9502bcbc362ecd349be68022d2a00b909f04681af0f4aff899");
						urls.add("499762ed8d0296e8e7ce233d7f0198d13de312e9ad374da8ca36b8f040b61822");
						urls.add("36eccd778fd564bd6cf173eb56a740c63f677413f6fe9824b4899f1a21cc2400");
						urls.add("ad17363c036e02d5144a662967eb527e4034e4af83268f8df24014dd1dbcec50");
						urls.add("226236515285ea3a883afc622799662f234210d05e3f5bb9f57270bc29f668a7");
						urls.add("63b4420a78de2fcb156109c7c09f408867511215a27530556fa6da2d6d2ec27f");
						urls.add("3f4ce84233d2ec4183016970af19008528c3775fb98c8dc99cbc673574b70073");
						urls.add("9d35cf4ca94602dae949c5abe537285824da28c02ba9ce5dd9b7f17aad981474");
					}
				}
				case CUSTOM_CRAFTING_TABLE ->
				{
					nbtItem.setString("change_material", Material.CYAN_STAINED_GLASS.toString());
					nbtItem.setDouble("BlockHardness", MiningManager.getBlockHardness(Material.CRAFTING_TABLE) * 1.5f);
					nbtItem.setString("BreakSound", Sound.BLOCK_WOOD_BREAK.toString());
					nbtItem.setString("BreakParticle", "block:crafting_table[]");
					nbtItem.setString("MatchTools", "AXE");
					NBTCompound nbtCompound = nbtItem.addCompound("displays");
					nbtCompound.setString("type", "player_heads");
					NBTList<String> urls = nbtCompound.getStringList("value");
					urls.clear();
					{
						urls.add("f7d74d5c72a7119d53e050a21756ec723f6660f3ad7c35794702025dc151cd9b");
						urls.add("e3485d7a84b628fc42b216e1bc1dc46a36674dd99a69e1c5ff1a451597e54382");
						urls.add("311592233e82d30240c06765b54565f054fc95d1d2003d54ba8affa7a7dcdba1");
						urls.add("d2cd87ffc98407333910d35ae4c88e6546e1bcd749246e91ddbe4858fd8027f1");
						urls.add("37b1588bf10797d1ee806960a1b1640f89f51d874d39a2f8531436e1ecb7e891");
						urls.add("dcaa590be32a4e867baf14eda6f90fcffdc89aaf12bdc95f27dfe87d9ea3b2e1");
						urls.add("a0ec4fde0b18686d54ae89ff33b19be335ddb7dc9abea8c41e8deebb5792bfaf");
						urls.add("8922865ba09c632c3987557826f6f41ffab28ac52c59d7d34482ed88a28c070f");
					}
				}
				case BLUE_NUMBER_BLOCK ->
				{
					nbtItem.setString("change_material", Material.BLUE_STAINED_GLASS.toString());
					nbtItem.setString("BreakParticle", "block:blue_concrete[]");
					nbtItem.setInteger("BlockTier", 1);
					nbtItem.setDouble("BlockHardness", 1000d);
					nbtItem.setString("BreakSound", Sound.BLOCK_STONE_BREAK.toString());
					NBTCompound nbtCompound = nbtItem.addCompound("displays");
					nbtCompound.setString("type", "player_heads");
					NBTList<String> urls = nbtCompound.getStringList("value");
					urls.clear();
					{
						urls.add("2be97ce60d48e290280fb4d353ddeaae9579ef4b931dc0360a3f3af549f2f3b9");
						urls.add("80e3e6c8db57074e04149778f0ef0b3d5a304c38bf71161cb83a2268541c772a");
						urls.add("a6782535a73e6ae00503de896ce516ff690ab98e51edefcb1cf241dd4b8740fb");
						urls.add("2ed972f0262708e8d4fb553828a3a1ee5e9e532edd29724d7fe39a6bc7753fb");
						urls.add("f3c3fc814133b878a04df25f5310575bb041d507640cb8171efa836296b2d96b");
						urls.add("963f0dcb3e50be5570ea3c3a3e7e419e434ae08d980450aa32d919262e5e9dfa");
						urls.add("840fbfecfb7a1b2cbbe5137c8549324679d7e8bb09a4fb42433daaf2baee4af1");
						urls.add("275430d2235b9015d8ab7d45774f474c203558b6771646985a1e0b3c58e001dd");
					}
				}
				case I_WONT_LET_YOU_GO_BLOCK ->
				{
					nbtItem.setString("change_material", Material.ORANGE_STAINED_GLASS.toString());
					nbtItem.setString("BreakParticle", "block:orange_concrete[]");
					nbtItem.setDouble("BlockHardness", 100d);
					nbtItem.setString("BreakSound", "custom_i_wont_let_you_go");
					nbtItem.setFloat("BreakSoundPitch", 1f);
					NBTCompound nbtCompound = nbtItem.addCompound("displays");
					nbtCompound.setString("type", "player_heads");
					NBTList<String> urls = nbtCompound.getStringList("value");
					urls.clear();
					{
						urls.add("be70633bdac7400250e66db44cb62b84047ec868ab0d491bcba87fb0462a102");
						urls.add("94906b0af0213ab0df9ef40c601081a656474d28bc219e122308f9c7fa0a6c81");
						urls.add("a1daa2d84a59ddebdfc7810d0880fc3a35ed62ad252ce2f9378413e77312378b");
						urls.add("f69bf74d568371d8f7e6ef58b916d3ca39f0d355c57c0d2c6572dec56e808b5c");
						urls.add("951aa50ad117bef22f37e16bb643b83dca01c167ba848f03ee1889bc149c4651");
						urls.add("bb692fb4576fb86c64dc34451c1b32593dda586b7a2ce5ca5227aed5de0e73b7");
						urls.add("d8effc2d7d13cc97ca1751098efe705d091b137a2e01f04c07780bad16a062dc");
						urls.add("75a6c86c8b652ab5ed8cd107eb6a7bf1e66f8b3a354ed31dd44d0bcbb32bcbe9");
					}
				}
				case TEST_PICKAXE ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, 18000d);
					nbtItem.setInteger(MiningManager.TOOL_TIER, 10);
				}
				case MUSHROOM_STEW_PICKAXE ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, 18_000_000d);
					nbtItem.setInteger(MiningManager.TOOL_TIER, 99);
				}
				case TODWOT_PICKAXE ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, 1d);
					nbtItem.setInteger(MiningManager.TOOL_TIER, 1);
				}
				case COPPER_AXE, COPPER_HOE, COPPER_PICKAXE, COPPER_SHOVEL, COPPER_SWORD ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, customMaterial != CustomMaterial.COPPER_SWORD ? 300d : 600d);
					if (customMaterial == CustomMaterial.COPPER_PICKAXE)
					{
						nbtItem.setInteger(MiningManager.TOOL_TIER, 2);
					}
				}
				case PLATINUM_AXE, PLATINUM_HOE, PLATINUM_PICKAXE, PLATINUM_SHOVEL, PLATINUM_SWORD ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, 600d);
					if (customMaterial == CustomMaterial.PLATINUM_PICKAXE)
					{
						nbtItem.setInteger(MiningManager.TOOL_TIER, 2);
					}
				}
				case TIN_AXE, TIN_HOE, TIN_PICKAXE, TIN_SHOVEL, TIN_SWORD ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, customMaterial != CustomMaterial.TIN_SWORD ? 200d : 550d);
					if (customMaterial == CustomMaterial.TIN_PICKAXE)
					{
						nbtItem.setInteger(MiningManager.TOOL_TIER, 3);
					}
				}
				case LEAD_AXE, LEAD_HOE, LEAD_PICKAXE, LEAD_SHOVEL, LEAD_SWORD ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, customMaterial != CustomMaterial.LEAD_SWORD ? 250d : 550d);
					if (customMaterial == CustomMaterial.LEAD_PICKAXE)
					{
						nbtItem.setInteger(MiningManager.TOOL_TIER, 4);
					}
				}
				case PLASTIC_AXE, PLASTIC_HOE, PLASTIC_PICKAXE, PLASTIC_SHOVEL, PLASTIC_SWORD ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, customMaterial != CustomMaterial.PLASTIC_SWORD ? 250d : 550d);
					if (customMaterial == CustomMaterial.PLASTIC_PICKAXE)
					{
						nbtItem.setInteger(MiningManager.TOOL_TIER, 4);
					}
				}
				case BRONZE_AXE, BRONZE_HOE, BRONZE_PICKAXE, BRONZE_SHOVEL, BRONZE_SWORD ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, customMaterial != CustomMaterial.BRONZE_SWORD ? 270d : 600d);
					if (customMaterial == CustomMaterial.BRONZE_PICKAXE)
					{
						nbtItem.setInteger(MiningManager.TOOL_TIER, 5);
					}
				}
				case CEMENTED_CARBIDE_AXE, CEMENTED_CARBIDE_HOE, CEMENTED_CARBIDE_PICKAXE, CEMENTED_CARBIDE_SHOVEL, CEMENTED_CARBIDE_SWORD ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, customMaterial != CustomMaterial.CEMENTED_CARBIDE_SWORD ? 800d : 1000d);
					if (customMaterial == CustomMaterial.CEMENTED_CARBIDE_PICKAXE)
					{
						nbtItem.setInteger(MiningManager.TOOL_TIER, 10);
					}
				}
				case NAUTILITE_AXE, NAUTILITE_HOE, NAUTILITE_PICKAXE, NAUTILITE_SHOVEL, NAUTILITE_SWORD ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, customMaterial != CustomMaterial.NAUTILITE_SWORD ? 800d : 1000d);
					if (customMaterial == CustomMaterial.NAUTILITE_PICKAXE)
					{
						nbtItem.setInteger(MiningManager.TOOL_TIER, 10);
					}
				}
				case TUNGSTEN_AXE, TUNGSTEN_HOE, TUNGSTEN_PICKAXE, TUNGSTEN_SHOVEL, TUNGSTEN_SWORD ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, customMaterial != CustomMaterial.TUNGSTEN_SWORD ? 600d : 850d);
					if (customMaterial == CustomMaterial.TUNGSTEN_PICKAXE)
					{
						nbtItem.setInteger(MiningManager.TOOL_TIER, 9);
					}
				}
				case COBALT_AXE, COBALT_HOE, COBALT_PICKAXE, COBALT_SHOVEL, COBALT_SWORD ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, customMaterial != CustomMaterial.COBALT_SWORD ? 250d : 600d);
					if (customMaterial == CustomMaterial.COBALT_PICKAXE)
					{
						nbtItem.setInteger(MiningManager.TOOL_TIER, 6);
					}
				}
				case MITHRIL_PICKAXE, MITHRIL_SWORD, MITHRIL_AXE, MITHRIL_HOE, MITHRIL_SHOVEL ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, customMaterial != CustomMaterial.MITHRIL_SWORD ? 550d : 800d);
					if (customMaterial == CustomMaterial.MITHRIL_PICKAXE)
					{
						nbtItem.setInteger(MiningManager.TOOL_TIER, 7);
					}
				}
				case MITHRIL_PICKAXE_REFINED ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, 2350d);
					nbtItem.setInteger(MiningManager.TOOL_TIER, 6);
				}
				case TITANIUM_PICKAXE, TITANIUM_SWORD, TITANIUM_AXE, TITANIUM_HOE, TITANIUM_SHOVEL ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, customMaterial != CustomMaterial.TITANIUM_SWORD ? 650d : 900d);
					if (customMaterial == CustomMaterial.TITANIUM_PICKAXE)
					{
						nbtItem.setInteger(MiningManager.TOOL_TIER, 9);
					}
				}
				case TITANIUM_PICKAXE_REFINED ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, 2850d);
					nbtItem.setInteger(MiningManager.TOOL_TIER, 7);
				}
				case TITANIUM_DRILL_R266 ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, 3500d);
					nbtItem.setInteger(MiningManager.TOOL_TIER, 7);
				}
				case TITANIUM_DRILL_R366 ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, 4000d);
					nbtItem.setInteger(MiningManager.TOOL_TIER, 7);
				}
				case TITANIUM_DRILL_R466 ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, 4500d);
					nbtItem.setInteger(MiningManager.TOOL_TIER, 8);
				}
				case TITANIUM_DRILL_R566 ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, 5000d);
					nbtItem.setInteger(MiningManager.TOOL_TIER, 9);
				}
				case MINDAS_DRILL ->
				{
					nbtItem.setDouble(MiningManager.TOOL_SPEED, 6000d);
					nbtItem.setInteger(MiningManager.TOOL_TIER, 10);
				}
				case IQ_CHOOK_CHUCK ->
				{
					if (!nbtItem.hasTag("IQ"))
					{
						nbtItem.setInteger("IQ", 70);
					}
					if (Math.random() > 0.2)
					{
						nbtItem.setInteger("IQ", nbtItem.getInteger("IQ") + 1);
					}
					else
					{
						nbtItem.setInteger("IQ", nbtItem.getInteger("IQ") - 1);
					}
				}
				case EXPERIENCE_BOTTLE_GRAND ->
				{
					nbtItem.setInteger("MinExp", 5000);
					nbtItem.setInteger("MaxExp", 5000);
				}
				case EXPERIENCE_BOTTLE_TITANIC ->
				{
					nbtItem.setInteger("MinExp", 100000);
					nbtItem.setInteger("MaxExp", 100000);
				}
				case EXPERIENCE_BOTTLE_COLOSSAL ->
				{
					nbtItem.setInteger("MinExp", 2500000);
					nbtItem.setInteger("MaxExp", 2500000);
				}
				case SMALL_MINING_SACK ->
				{
					NBTCompound sack = nbtItem.addCompound("Sack");
					sack.setInteger("Capacity", 1200);
				}
				case TNT_SUPERIOR ->
				{
					nbtItem.setFloat("ExplodePower", 10f);
				}
				case TNT_DRAIN ->
				{
					nbtItem.setFloat("ExplodePower", 6f);
					nbtItem.setShort("Fuse", (short) 20);
				}
				case TNT_DONUT ->
				{
					if (!nbtItem.hasTag("Size"))
					{
						nbtItem.setShort("Size", (short) 30);
					}
				}
				// YouPeople
				case YOUPEOPLEGAME_CRISPY_COOKIE_PICKAXE, YOUPEOPLEGAME_CRISPY_COOKIE_AXE, YOUPEOPLEGAME_CRISPY_COOKIE_SHOVEL -> {
					nbtItem.addCompound(CucumberyTag.KEY_MAIN).addCompound(CucumberyTag.CUSTOM_RARITY_KEY).setString(CucumberyTag.CUSTOM_RARITY_FINAL_KEY, Rarity.RARE.toString());
				}
				case YOUPEOPLEGAME_BURNING_FIRE, YOUPEOPLEGAME_CRISPY_COOKIE_BOX, YOUPEOPLEGAME_DAMP_COOKIE_POTION, YOUPEOPLEGAME_MOIST_COOKIE_BOOSTER, YOUPEOPLEGAME_SUPER_MOIST_COOKIE_BOOSTER -> {
					nbtItem.addCompound(CucumberyTag.KEY_MAIN).addCompound(CucumberyTag.CUSTOM_RARITY_KEY).setString(CucumberyTag.CUSTOM_RARITY_FINAL_KEY, Rarity.NORMAL.toString());
				}
			}
			if (customMaterial.isVerticalSlab())
			{
				nbtItem.setString("change_material", Material.BARRIER.toString());
				if (Tag.MINEABLE_PICKAXE.isTagged(customMaterial.getDisplayMaterial()))
					nbtItem.setInteger("BlockTier", 1);
				else
				{
					nbtItem.removeKey("BlockTier");
					nbtItem.setString("MatchTools", "AXE");
				}
				nbtItem.setDouble("BlockHardness", MiningManager.getBlockHardness(customMaterial.getDisplayMaterial()));
				nbtItem.setString("BreakParticle", "block:" + customMaterial.getDisplayMaterial().toString().toLowerCase());
				nbtItem.setString("BreakSound", customMaterial.getDisplayMaterial().createBlockData().getSoundGroup().getBreakSound().toString());
				nbtItem.setFloat("additionalPitch", 90f);
				nbtItem.setBoolean("perspectiveYaw", true);
				nbtItem.setBoolean("perspectivePitch", true);
				nbtItem.setBoolean("perspectiveGrid", true);
				NBTCompound nbtCompound = nbtItem.addCompound("displays");
				nbtCompound.setString("type", "item");
				nbtCompound.setString("value", customMaterial.getDisplayMaterial().toString().toLowerCase());
			}
			if (nbtItem.getCompound(CucumberyTag.KEY_MAIN).getKeys().isEmpty())
			{
				nbtItem.removeKey(CucumberyTag.KEY_MAIN);
			}
		}
		itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
		itemStack.setType(displayMaterial);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.itemName(customMaterial.getDisplayName());
		// isGlow가 true일 경우 반짝이는 효과 추가
		if (customMaterial.isGlow())
		{
			itemMeta.setEnchantmentGlintOverride(true);
		}
		// 아닐 경우 추가된 반짝이는 효과 제거
		else if (itemMeta.hasEnchantmentGlintOverride())
		{
			itemMeta.setEnchantmentGlintOverride(null);
		}
		// 커스텀 모델 데이터 설정
		{
			CustomModelDataComponent customModelDataComponent = itemMeta.getCustomModelDataComponent();
			customModelDataComponent.setStrings(Collections.singletonList(customMaterial.toString().toLowerCase()));
			itemMeta.setCustomModelDataComponent(customModelDataComponent);
		}

		// ItemMeta NBT 설정
		{
			Multimap<Attribute, AttributeModifier> attributeMap = itemMeta.getAttributeModifiers();
			if (attributeMap != null)
			{
				for (Attribute attribute : attributeMap.keySet())
				{
					Collection<AttributeModifier> modifiers = itemMeta.getAttributeModifiers(attribute);
					if (modifiers != null)
					{
						for (AttributeModifier modifier : modifiers)
						{
							if (modifier.getKey().getNamespace().equals("cucumbery"))
							{
								itemMeta.removeAttributeModifier(attribute, modifier);
							}
						}
					}
				}
			}
			switch (customMaterial)
			{
				// 이상한 곡괭이

				case STONK ->
				{
					if (!itemMeta.hasEnchant(Enchantment.EFFICIENCY))
					{
						itemMeta.addEnchant(Enchantment.EFFICIENCY, 6, true);
					}
					itemMeta.setUnbreakable(true);
				}
				case MUSHROOM_STEW_PICKAXE ->
				{
					if (!itemMeta.hasEnchant(Enchantment.FORTUNE))
					{
						itemMeta.addEnchant(Enchantment.FORTUNE, 10, true);
					}
				}
				case TODWOT_PICKAXE ->
				{
					if (!itemMeta.hasEnchant(Enchantment.FORTUNE))
					{
						itemMeta.addEnchant(Enchantment.FORTUNE, 1, true);
					}
				}

				// 곡괭이
				case PLATINUM_PICKAXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 2, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.8, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case COPPER_PICKAXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 2.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.8, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case TIN_PICKAXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.8, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case LEAD_PICKAXE, BRONZE_PICKAXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 3.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.8, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case PLASTIC_PICKAXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case COBALT_PICKAXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 4, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.8, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case MITHRIL_PICKAXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.8, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case TUNGSTEN_PICKAXE, TITANIUM_PICKAXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 6.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.8, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case CEMENTED_CARBIDE_PICKAXE, NAUTILITE_PICKAXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 7, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.8, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}

				// 검

				case PLATINUM_SWORD ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.4, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case COPPER_SWORD ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 3.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.4, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case TIN_SWORD, LEAD_SWORD, COBALT_SWORD ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 4, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.4, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case PLASTIC_SWORD ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 3.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -1.9, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case BRONZE_SWORD ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.4, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case MITHRIL_SWORD ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 6, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.4, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case TUNGSTEN_SWORD, TITANIUM_SWORD ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 8, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.4, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case CEMENTED_CARBIDE_SWORD, NAUTILITE_SWORD ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 9, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.4, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}

				// 도끼

				case PLATINUM_AXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 4, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3.2, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case COPPER_AXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 4.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3.2, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case TIN_AXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3.2, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case PLASTIC_AXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 5.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.7, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case BRONZE_AXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 5.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3.1, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case COBALT_AXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 6, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3.1, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case MITHRIL_AXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 7, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case TUNGSTEN_AXE, TITANIUM_AXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 9, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.9, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case CEMENTED_CARBIDE_AXE, NAUTILITE_AXE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 10, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.8, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}

				// 삽

				case PLATINUM_SHOVEL ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 2.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case COPPER_SHOVEL ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case TIN_SHOVEL ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 3.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case LEAD_SHOVEL, BRONZE_SHOVEL ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 4, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case PLASTIC_SHOVEL ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 3.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case COBALT_SHOVEL ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 4.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case MITHRIL_SHOVEL ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 5.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case TUNGSTEN_SHOVEL, TITANIUM_SHOVEL ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 7, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case CEMENTED_CARBIDE_SHOVEL, NAUTILITE_SHOVEL ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 7.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}

				// 괭이

				case PLATINUM_HOE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 1, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case COPPER_HOE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 1.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case TIN_HOE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 2, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -2, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case LEAD_HOE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 2, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -1.6, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case PLASTIC_HOE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 1.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -1.1, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case BRONZE_HOE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 2, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -1.2, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case COBALT_HOE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, -1, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case MITHRIL_HOE -> itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
						new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 3, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				case TUNGSTEN_HOE, TITANIUM_HOE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 4, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, 1, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}
				case CEMENTED_CARBIDE_HOE, NAUTILITE_HOE ->
				{
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, 2, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
				}

				// 갑옷

				case SANS_HELMET, FROG_HELMET, MINER_HELMET -> itemMeta.addAttributeModifier(Attribute.ARMOR,
						new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 2, Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD));
				case SANS_CHESTPLATE, FROG_CHESTPLATE, MINER_CHESTPLATE -> itemMeta.addAttributeModifier(Attribute.ARMOR,
						new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, 2, Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));
				case SANS_LEGGINGS, FROG_LEGGINGS, MINER_LEGGINGS -> itemMeta.addAttributeModifier(Attribute.ARMOR,
						new AttributeModifier(ATTRIBUTE_IDENTIFIER_3_0, 2, Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS));
				case SANS_BOOTS ->
				{
					itemMeta.addAttributeModifier(Attribute.ARMOR,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_4_0, 2, Operation.ADD_NUMBER, EquipmentSlotGroup.FEET));
					itemMeta.addAttributeModifier(Attribute.MOVEMENT_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_4_1, 0.3, Operation.ADD_SCALAR, EquipmentSlotGroup.FEET));
				}
				case FROG_BOOTS, MINER_BOOTS -> itemMeta.addAttributeModifier(Attribute.ARMOR,
						new AttributeModifier(ATTRIBUTE_IDENTIFIER_4_0, 1, Operation.ADD_NUMBER, EquipmentSlotGroup.FEET));
				case MINDAS_HELMET ->
				{
					itemMeta.addAttributeModifier(Attribute.ARMOR,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_0, 20, Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD));
					itemMeta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_1, 20, Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD));
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_2, 0.5, Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.HEAD));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_3, 2, Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD));
					itemMeta.addAttributeModifier(Attribute.LUCK,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_4, 5, Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD));
					itemMeta.addAttributeModifier(Attribute.MAX_HEALTH,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_5, 0.3, Operation.ADD_SCALAR, EquipmentSlotGroup.HEAD));
					itemMeta.addEnchant(Enchantment.PROTECTION, 20, true);
					itemMeta.addEnchant(Enchantment.RESPIRATION, 20, true);
					itemMeta.addEnchant(Enchantment.AQUA_AFFINITY, 1, true);
				}
				case MINDAS_CHESTPLATE ->
				{
					itemMeta.addAttributeModifier(Attribute.ARMOR,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, 20, Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));
					itemMeta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_1, 20, Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_2, 0.8, Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.CHEST));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_3, 2, Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));
					itemMeta.addAttributeModifier(Attribute.LUCK,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_4, 8, Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));
					itemMeta.addAttributeModifier(Attribute.MAX_HEALTH,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_5, 0.5, Operation.ADD_SCALAR, EquipmentSlotGroup.CHEST));
					itemMeta.addEnchant(Enchantment.PROTECTION, 20, true);
					itemMeta.addEnchant(Enchantment.UNBREAKING, 10, true);
					itemMeta.addEnchant(Enchantment.MENDING, 1, true);
				}
				case MINDAS_LEGGINGS ->
				{
					itemMeta.addAttributeModifier(Attribute.ARMOR,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, 20, Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS));
					itemMeta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_1, 20, Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS));
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_2, 0.6, Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.LEGS));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_3, 2, Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS));
					itemMeta.addAttributeModifier(Attribute.LUCK,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_4, 6, Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS));
					itemMeta.addAttributeModifier(Attribute.MAX_HEALTH,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_5, 0.4, Operation.ADD_SCALAR, EquipmentSlotGroup.LEGS));
					itemMeta.addEnchant(Enchantment.PROTECTION, 20, true);
					itemMeta.addEnchant(Enchantment.UNBREAKING, 10, true);
					itemMeta.addEnchant(Enchantment.MENDING, 1, true);
					itemMeta.addEnchant(Enchantment.SWIFT_SNEAK, 5, true);
				}
				case MINDAS_BOOTS ->
				{
					itemMeta.addAttributeModifier(Attribute.ARMOR,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, 20, Operation.ADD_NUMBER, EquipmentSlotGroup.FEET));
					itemMeta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_1, 20, Operation.ADD_NUMBER, EquipmentSlotGroup.FEET));
					itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_2, 0.55, Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.FEET));
					itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_3, 2, Operation.ADD_NUMBER, EquipmentSlotGroup.FEET));
					itemMeta.addAttributeModifier(Attribute.LUCK,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_4, 5, Operation.ADD_NUMBER, EquipmentSlotGroup.FEET));
					itemMeta.addAttributeModifier(Attribute.MAX_HEALTH,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_1_5, 0.35, Operation.ADD_SCALAR, EquipmentSlotGroup.FEET));
					itemMeta.addEnchant(Enchantment.PROTECTION, 20, true);
					itemMeta.addEnchant(Enchantment.UNBREAKING, 10, true);
					itemMeta.addEnchant(Enchantment.MENDING, 1, true);
					itemMeta.addEnchant(Enchantment.DEPTH_STRIDER, 3, true);
				}
				// 겉날개

				case DIAMOND_CHESTPLATE_WITH_ELYTRA ->
				{
					itemMeta.addAttributeModifier(Attribute.ARMOR,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, 4, Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));
					itemMeta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_1, 1, Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));
				}
				case NETHERITE_CHESTPLATE_WITH_ELYTRA ->
				{
					itemMeta.addAttributeModifier(Attribute.ARMOR,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_0, 4, Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));
					itemMeta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_1, 1.5, Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));
					itemMeta.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE,
							new AttributeModifier(ATTRIBUTE_IDENTIFIER_2_2, 0.5, Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));
				}

				// 던질 수 있는 아이템 (눈덩이)
				case BRICK_THROWABLE, COPPER_INGOT_THROWABLE, GOLD_INGOT_THROWABLE, IRON_INGOT_THROWABLE, NETHER_BRICK_THROWABLE, NETHERITE_INGOT_THROWABLE ->
						itemMeta.setMaxStackSize(64);
			}

		}
		if (itemMeta instanceof Damageable damageable)
		{
			switch (customMaterial)
			{
				case TEST_PICKAXE -> damageable.setMaxDamage(12345678);
				case TODWOT_PICKAXE -> damageable.setMaxDamage(1000);
				case COPPER_AXE, COPPER_HOE, COPPER_PICKAXE, COPPER_SHOVEL, COPPER_SWORD -> damageable.setMaxDamage(76);
				case PLATINUM_AXE, PLATINUM_HOE, PLATINUM_PICKAXE, PLATINUM_SHOVEL, PLATINUM_SWORD -> damageable.setMaxDamage(34);
				case TIN_AXE, TIN_HOE, TIN_PICKAXE, TIN_SHOVEL, TIN_SWORD -> damageable.setMaxDamage(142);
				case LEAD_AXE, LEAD_HOE, LEAD_PICKAXE, LEAD_SHOVEL, LEAD_SWORD -> damageable.setMaxDamage(176);
				case PLASTIC_AXE, PLASTIC_HOE, PLASTIC_PICKAXE, PLASTIC_SHOVEL, PLASTIC_SWORD -> damageable.setMaxDamage(185);
				case BRONZE_AXE, BRONZE_HOE, BRONZE_PICKAXE, BRONZE_SHOVEL, BRONZE_SWORD -> damageable.setMaxDamage(209);
				case CEMENTED_CARBIDE_AXE, CEMENTED_CARBIDE_HOE, CEMENTED_CARBIDE_PICKAXE, CEMENTED_CARBIDE_SHOVEL, CEMENTED_CARBIDE_SWORD ->
						damageable.setMaxDamage(5876);
				case NAUTILITE_AXE, NAUTILITE_HOE, NAUTILITE_PICKAXE, NAUTILITE_SHOVEL, NAUTILITE_SWORD -> damageable.setMaxDamage(3413);
				case TUNGSTEN_AXE, TUNGSTEN_HOE, TUNGSTEN_PICKAXE, TUNGSTEN_SHOVEL, TUNGSTEN_SWORD -> damageable.setMaxDamage(2474);
				case COBALT_AXE, COBALT_HOE, COBALT_PICKAXE, COBALT_SHOVEL, COBALT_SWORD -> damageable.setMaxDamage(318);
				case MITHRIL_AXE, MITHRIL_HOE, MITHRIL_PICKAXE, MITHRIL_SHOVEL, MITHRIL_SWORD -> damageable.setMaxDamage(1824);
				case TITANIUM_AXE, TITANIUM_HOE, TITANIUM_PICKAXE, TITANIUM_SHOVEL, TITANIUM_SWORD -> damageable.setMaxDamage(3710);
				case MITHRIL_PICKAXE_REFINED -> damageable.setMaxDamage(3800);
				case TITANIUM_PICKAXE_REFINED -> damageable.setMaxDamage(4500);
				case SANS_BOOTS, SANS_CHESTPLATE, SANS_HELMET, SANS_LEGGINGS, RAINBOW_BOOTS, RAINBOW_CHESTPLATE, RAINBOW_HELMET, RAINBOW_LEGGINGS, SPIDER_BOOTS ->
						damageable.setMaxDamage(500);
				case FROG_BOOTS, FROG_CHESTPLATE, FROG_HELMET, FROG_LEGGINGS -> damageable.setMaxDamage(400);
				case MINER_BOOTS, MINER_CHESTPLATE, MINER_HELMET, MINER_LEGGINGS -> damageable.setMaxDamage(450);
				case MINDAS_BOOTS, MINDAS_CHESTPLATE, MINDAS_HELMET, MINDAS_LEGGINGS -> damageable.setMaxDamage(100000);
				case TITANIUM_DRILL_R266 -> damageable.setMaxDamage(5000);
				case TITANIUM_DRILL_R366 -> damageable.setMaxDamage(7000);
				case TITANIUM_DRILL_R466 -> damageable.setMaxDamage(9000);
				case TITANIUM_DRILL_R566 -> damageable.setMaxDamage(12000);
				case MINDAS_DRILL -> damageable.setMaxDamage(15000);
				case FLINT_SHOVEL -> damageable.setMaxDamage(600);

			}
			if (damageable.hasMaxDamage() && !damageable.hasMaxStackSize())
			{
				damageable.setMaxStackSize(1);
			}
		}
		if (itemMeta instanceof SkullMeta skullMeta)
		{
			if (!skullMeta.hasOwner())
			{
				switch (customMaterial)
				{
					case ENCHANTED_TITANIUM -> ItemStackUtil.setTexture(skullMeta, "3dcc0ec9873f4f8d407ba0a0f983e257787772eaf8784e226a61c7f727ac9e26");
					case MITHRIL_REFINED -> ItemStackUtil.setTexture(skullMeta, "35a7663300619bb6a156d76351ac05f7b3cafeac31e2ff04c55cc9f236327832");
					case TITANIUM_REFINED -> ItemStackUtil.setTexture(skullMeta, "704baabf7ef854825aae1992e4a75ff7286ed1654d8f1a08952e7b8669cf692d");
					case PORTABLE_CRAFTING_TABLE -> ItemStackUtil.setTexture(skullMeta, "4c36045208f9b5ddcf8c4433e424b1ca17b94f6b96202fb1e5270ee8d53881b1");
					case PORTABLE_ENDER_CHEST -> ItemStackUtil.setTexture(skullMeta, "a6cc486c2be1cb9dfcb2e53dd9a3e9a883bfadb27cb956f1896d602b4067");
					case FROG_HELMET -> ItemStackUtil.setTexture(skullMeta, "5710f6f91fafea57278df853131b775f2c2d324a6274bee40d776b16cb5d60b6");
					case MINDAS_HELMET -> ItemStackUtil.setTexture(skullMeta, "316fc913e6ab9b6911003de60c797bad3fbeb80eb73d51afb6928de9c9eedac3");
					case WNYNYA_ORE -> ItemStackUtil.setTexture(skullMeta, "3fff5379e980aca266d7d1a3a4939ecafd42e2028d436ffa41ae96b076937d09");
					case CUSTOM_CRAFTING_TABLE -> ItemStackUtil.setTexture(skullMeta, "80a4334f6a61e40c0c63deb665fa7b581e6eb259f7a3207ced7a1ff8bdc8a9f9");
					case TIGHTLY_TIED_HAY_BLOCK -> ItemStackUtil.setTexture(skullMeta, "f7c33cd0c14ba830da149907f7a6aae835b6a35aea01e0ce073fb3c59cc46326");
					case BLUE_NUMBER_BLOCK -> ItemStackUtil.setTexture(skullMeta, "786eddd98d12a9b3768919e22c258c2833b3347b5b15e1a8b9669abde8652a6c");
					case I_WONT_LET_YOU_GO_BLOCK -> ItemStackUtil.setTexture(skullMeta, "4e68ed81ce08c1b5006ae041dc91c7b59fe1f698c2e575c60c7e5b25d6434484");
					case SUS -> ItemStackUtil.setTexture(skullMeta, "a68fc422a3bf7dba87bc2b03e68fb6c3de5b8428d91d20453e70cd14f110454f");
					case DIA_RON_LAPI_RED_COAL_GOLD_ORE -> ItemStackUtil.setTexture(skullMeta, "30b7dfb933d07941d1308b48cb8afacca80a9c81109033524f8bbbaa451a292f");
					case RE_PPER_LD_RALD_AMON_YST_RGANITE_BLOCK ->
							ItemStackUtil.setTexture(skullMeta, "49de823d60248aa50e0bc9f61270fdaba19fb157b6aecc7eb497f5937f8063e1");
				}
			}
		}
		// YouPeopleGame
		{
			switch (customMaterial)
			{
				case YOUPEOPLEGAME_CRISPY_COOKIE_PICKAXE, YOUPEOPLEGAME_CRISPY_COOKIE_AXE, YOUPEOPLEGAME_CRISPY_COOKIE_SHOVEL -> {
					itemMeta.addEnchant(Enchantment.EFFICIENCY, 3, true);
				}
				case YOUPEOPLEGAME_BURNING_FIRE -> {
					itemMeta.setItemModel(Material.CAMPFIRE.getKey());
					itemMeta.setMaxStackSize(64);
				}
				case YOUPEOPLEGAME_CRISPY_COOKIE_BOX -> {
					itemMeta.setItemModel(Material.ORANGE_SHULKER_BOX.getKey());
					itemMeta.setMaxStackSize(64);
				}
				case YOUPEOPLEGAME_DAMP_COOKIE_POTION, YOUPEOPLEGAME_MOIST_COOKIE_BOOSTER, YOUPEOPLEGAME_SUPER_MOIST_COOKIE_BOOSTER -> {
					itemMeta.setItemModel(Material.HONEY_BOTTLE.getKey());
				}
				case YOUPEOPLEGAME_MEDAL_OF_PARKOUR -> {
					itemMeta.setMaxStackSize(99);
				}
			}
			// 반짝이 효과가 없는 아이템 중 디버그 막대기인 아이템은 반짝이 효과 제거
			if (customMaterial.getDisplayMaterial() == Material.DEBUG_STICK && !customMaterial.isGlow() && !itemMeta.hasEnchants())
			{
				itemMeta.setEnchantmentGlintOverride(false);
			}
		}
		// 모든 아이템 등급 COMMON으로
		itemMeta.setRarity(ItemRarity.COMMON);
		itemStack.setItemMeta(itemMeta);
		if (itemMeta instanceof SkullMeta skullMeta && !skullMeta.hasOwner())
		{
			switch (customMaterial)
			{
				case DOEHAERIM_BABO -> skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("Ai_vo"));
				case BAMIL_PABO -> skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("Pamille_27"));
			}
			itemStack.setItemMeta(skullMeta);
		}
		if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta)
		{
			leatherArmorMeta.addItemFlags(ItemFlag.HIDE_DYE);
			switch (customMaterial)
			{
				case SANS_BOOTS, SANS_CHESTPLATE, SANS_HELMET, SANS_LEGGINGS -> leatherArmorMeta.setColor(Color.fromRGB(240, 240, 240));
				case FROG_BOOTS, FROG_CHESTPLATE, FROG_LEGGINGS -> leatherArmorMeta.setColor(Color.fromRGB(50, 255, 50));
				case MINER_BOOTS, MINER_CHESTPLATE, MINER_HELMET, MINER_LEGGINGS -> leatherArmorMeta.setColor(Color.fromRGB(200, 200, 200));
				case COPPER_BOOTS, COPPER_CHESTPLATE, COPPER_HELMET, COPPER_LEGGINGS -> leatherArmorMeta.setColor(Color.fromRGB(255, 150, 100));
				case TUNGSTEN_BOOTS, TUNGSTEN_CHESTPLATE, TUNGSTEN_HELMET, TUNGSTEN_LEGGINGS -> leatherArmorMeta.setColor(Color.fromRGB(139, 175, 154));
				case COBALT_BOOTS, COBALT_CHESTPLATE, COBALT_HELMET, COBALT_LEGGINGS -> leatherArmorMeta.setColor(Color.fromRGB(0, 150, 235));
				case MITHRIL_BOOTS, MITHRIL_CHESTPLATE, MITHRIL_HELMET, MITHRIL_LEGGINGS -> leatherArmorMeta.setColor(Color.fromRGB(10, 200, 200));
				case TITANIUM_BOOTS, TITANIUM_CHESTPLATE, TITANIUM_HELMET, TITANIUM_LEGGINGS -> leatherArmorMeta.setColor(Color.fromRGB(150, 150, 150));
			}
			itemStack.setItemMeta(leatherArmorMeta);
		}
		if (itemMeta instanceof FireworkMeta fireworkMeta)
		{
			switch (customMaterial)
			{
				case FIREWORK_ROCKET_CHAIN, FIREWORK_ROCKET_REPEATING ->
				{
					if (!fireworkMeta.hasEffects())
					{
						fireworkMeta.addEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.WHITE).build());
						fireworkMeta.setPower(1);
						itemStack.setItemMeta(fireworkMeta);
					}
				}
				case FIREWORK_ROCKET_EXPLOSIVE, FIREWORK_ROCKET_EXPLOSIVE_DESTRUCTION, FIREWORK_ROCKET_EXPLOSIVE_FLAME ->
				{
					fireworkMeta.clearEffects();
					fireworkMeta.addEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.WHITE).build());
					fireworkMeta.setPower(1);
					itemStack.setItemMeta(fireworkMeta);
				}
			}
		}
		// NBT API
		{
			switch (customMaterial)
			{
				case YOUPEOPLEGAME_DAMP_COOKIE_POTION, YOUPEOPLEGAME_MOIST_COOKIE_BOOSTER, YOUPEOPLEGAME_SUPER_MOIST_COOKIE_BOOSTER -> {
					NBT.modifyComponents(itemStack, nbt -> {
						nbt.set("minecraft:consumable", new NBTContainer(), NBTHandlers.STORE_READWRITE_TAG);
					});
				}
			}
		}

	}

	protected static void itemLore(@NotNull ItemStack itemStack, @NotNull NBTItem nbtItem, @NotNull String id)
	{
		ConfigurationSection section = Variable.customItemsConfig.getConfigurationSection(id);
		if (section != null)
		{
			List<String> restrictions = section.getStringList("restrictions");
			NBTCompound itemTag = nbtItem.getCompound(CucumberyTag.KEY_MAIN);
			for (String r : restrictions)
			{
				if (itemTag == null)
				{
					itemTag = nbtItem.addCompound(CucumberyTag.KEY_MAIN);
				}
				NBTCompoundList restrictionsTag = itemTag.getCompoundList(CucumberyTag.ITEM_USAGE_RESTRICTIONS_KEY);
				try
				{
					String[] split = r.split("\\|");
					RestrictionType restrictionType = RestrictionType.valueOf(split[0]);
					if (!NBTAPI.isRestricted(itemStack, restrictionType))
					{
						boolean hideFromLore = Boolean.parseBoolean(split[1]);
						NBTCompound nbtCompound = new NBTContainer();
						nbtCompound.setString(CucumberyTag.VALUE_KEY, restrictionType.toString());
						nbtCompound.setBoolean(CucumberyTag.ITEM_USAGE_RESTRICTIONS_HIDE_FROM_LORE_KEY, hideFromLore);
						restrictionsTag.addCompound(nbtCompound);
					}
				}
				catch (Exception ignored)
				{

				}
			}
			String typeString = section.getString("type");
			try
			{
				if (typeString != null)
				{
					itemStack.setType(Material.valueOf(typeString.toUpperCase()));
				}
			}
			catch (Exception ignored)
			{
				return;
			}
			ItemMeta itemMeta = itemStack.getItemMeta();
			String displayName = section.getString("display-name");
			if (displayName == null)
			{
				throw new NullPointerException("display name cannot be null! CustomItem.yml - " + id);
			}
			itemMeta.itemName(ComponentUtil.create(MessageUtil.n2s(displayName)));
			List<String> enchants = section.getStringList("enchants");
			enchants.forEach(s ->
			{
				try
				{
					String[] split = s.split("\\|");
					String[] split2 = split[0].split(":");
					Enchantment enchantment = Registry.ENCHANTMENT.get(new NamespacedKey(split2[0], split2[1]));
					int level = Integer.parseInt(split[1]);
					if (enchantment != null)
					{
						itemMeta.addEnchant(enchantment, level, true);
					}
				}
				catch (Exception ignored)
				{
				}
			});
			String skull = section.getString("skull");
			if (skull != null && itemMeta instanceof SkullMeta skullMeta)
			{
				ItemStackUtil.setTexture(skullMeta, skull);
			}
			itemStack.setItemMeta(itemMeta);
			String nbt = section.getString("nbt");
			if (nbt != null)
			{
				try
				{
					NBTContainer nbtContainer = new NBTContainer("{" + nbt + "}");
					new NBTItem(itemStack, true).mergeCompound(nbtContainer);
				}
				catch (Exception ignored)
				{

				}
			}
		}
	}
}
