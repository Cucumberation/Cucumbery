package com.jho5245.cucumbery.util.storage.data.custom_enchant;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class CustomEnchant extends Enchantment
{
	private static final HashMap<NamespacedKey, Enchantment> CUSTOM_ENCHANTS = new HashMap<>();

	/**
	 * 데이터팩을 통해 등록을 하지 못한 인챈트 키
	 */
	private static final Set<NamespacedKey> NOT_REGISTERED_ENCHANTS = new HashSet<>();
	
	/**
	 * If an {@link ItemStack} has this enchant, it is never dropped upon death.
	 */
	public static Enchantment KEEP_INVENTORY;

	/**
	 * Blocks and mob drops go directly to {@link Player}'s inventory.
	 */
	public static Enchantment TELEKINESIS;

	/**
	 * If keepInv is false, player kill drops go directly to {@link Player}'s inventory.
	 */
	public static Enchantment TELEKINESIS_PVP;

	/**
	 * By predefined chance table, mining {@link Material#ICE} will drop certain ice block.
	 */
	public static Enchantment COLD_TOUCH;

	/**
	 * Removes target's {@link LivingEntity#getNoDamageTicks()} (set to zero).
	 */
	public static Enchantment JUSTIFICATION;

	/**
	 * Removes target's {@link LivingEntity#getNoDamageTicks()} (set to zero). Only applicable for {@link Material#BOW}
	 */
	public static Enchantment JUSTIFICATION_BOW;

	/**
	 * Blocks and mob drops become its smelted form.
	 */
	public static Enchantment SMELTING_TOUCH;

	/**
	 * By predefined chance table, mining {@link Material#CACTUS} will drop certain ice block.
	 */
	public static Enchantment WARM_TOUCH;

	/**
	 * Blocks and mob drops will disappear.
	 */
	public static Enchantment COARSE_TOUCH;

	/**
	 * Blocks drops will become {@link Material#FLINT} in chance.
	 */
	public static Enchantment DULL_TOUCH;

	/**
	 * Blocks and mob drops will not grant any xp.
	 */
	public static Enchantment UNSKILLED_TOUCH;

	/**
	 * Blocks that is {@link BlockInventoryHolder#getInventory()}'s item will be vanished.
	 */
	public static Enchantment VANISHING_TOUCH;

	/**
	 * ANY Blocks drops will be doubled
	 */
	public static Enchantment FRANTIC_FORTUNE;

	/**
	 * Fishing root items will be DOUBLED
	 */
	public static Enchantment FRANTIC_LUCK_OF_THE_SEA;

	/**
	 * If kill an entity with any item with this Enchant, the killer on death message will be hidden.
	 */
	public static Enchantment ASSASSINATION;

	/**
	 * If kill an entity with any item with this Enchant, the killer on death message will be hidden. Only applicable for {@link Material#BOW}
	 */
	public static Enchantment ASSASSINATION_BOW;

	/**
	 * If a throwable weapon/projectile has this, It's accuracy will be dropped.
	 */
	public static Enchantment IDIOT_SHOOTER;

	/**
	 * 1+(1*레벨) 추가대미지, 1레벨 오를때마다 상대의 방패 피격시 상대의 방패사용불가 0.5초씩 추가
	 */
	public static Enchantment CLEAVING;

	/**
	 * 플레이어의 손에 방패가 있을 때 다른 몹이나 플레이어가 피격 시 막고있지 않을 때 (6*레벨)% 확률로 막아지고(해당 공격에 대해 무적) 방패의 내구도가 ((들어온 대미지/6)*레벨) 만큼 깎인다. (1 이하의 데미지는 내구도 깎지 않음, 소수점은 정수로 반올림) (특징:
	 * 레벨이 높아질수록 막을 확률은 높아지지만 이 효과로 막았을때 내구도가 더 많이 깎임) (내구성 인챈트와 같이 인챈트 불가)
	 */
	public static Enchantment DEFENSE_CHANCE;

	/**
	 * 괭이의 농업 행운 증가 - 작물 드롭율 증가
	 */
	public static Enchantment HARVESTING;

	/**
	 * 도끼의 농업 행운 증가 - 작물 드롭율 증가
	 */
	public static Enchantment SUNDER;

	/**
	 * 도끼, 괭이가 자라지 않은 작물과 수박, 호박의 줄기를 부수지 않도록 함
	 */
	public static Enchantment DELICATE;

	/**
	 * 신발을 신고 경작지 위에서 점프해도 경작지가 파괴되지 않음
	 */
	public static Enchantment FARMERS_GRACE;

	// Ulitmate Enchants
	public static Enchantment HIGH_RISK_HIGH_RETURN;

	/**
	 * 구사 일생
	 * <p>Grants chances not to die upon death.
	 */
	public static Enchantment CLOSE_CALL;

	public static boolean isUltimate(Enchantment enchantment)
	{
		return enchantment == HIGH_RISK_HIGH_RETURN || enchantment == CLOSE_CALL;
	}

	public static void registerEnchants()
	{
		KEEP_INVENTORY = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "keep_inventory"));
		TELEKINESIS = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "telekinesis"));
		TELEKINESIS_PVP = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "telekinesis_pvp"));

		COLD_TOUCH = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "cold_touch"));
		JUSTIFICATION = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "justification"));
		JUSTIFICATION_BOW = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "justification_bow"));
		SMELTING_TOUCH = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "smelting_touch"));
		WARM_TOUCH = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "warm_touch"));

		COARSE_TOUCH = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "coarse_touch"));
		DULL_TOUCH = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "dull_touch"));
		UNSKILLED_TOUCH = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "unskilled_touch"));
		VANISHING_TOUCH = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "vanishing_touch"));

		FRANTIC_FORTUNE = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "frantic_fortune"));
		FRANTIC_LUCK_OF_THE_SEA = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "frantic_luck_of_the_sea"));

		ASSASSINATION = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "assassination"));
		ASSASSINATION_BOW = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "assassination_bow"));

		IDIOT_SHOOTER = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "idiot_shooter"));

		CLEAVING = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "cleaving"));

		DEFENSE_CHANCE = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "defense_chance"));

		HARVESTING = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "harvesting"));

		SUNDER = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "sunder"));

		DELICATE = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "delicate"));

		FARMERS_GRACE = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "farmers_grace"));

		// Ulitmate Enchants

		HIGH_RISK_HIGH_RETURN = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "high_risk_high_return"));

		CLOSE_CALL = registerEnchant(new NamespacedKey(Cucumbery.getPlugin(), "close_call"));
	}


	@Nullable
	private static Enchantment registerEnchant(@NotNull NamespacedKey namespacedKey)
	{
		Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(namespacedKey);
		if (enchantment == null)
		{
			NOT_REGISTERED_ENCHANTS.add(namespacedKey);
		}
		else
		{
			CUSTOM_ENCHANTS.put(namespacedKey, enchantment);
		}
		return enchantment;
	}

	public static boolean isEnabled()
	{
		return Cucumbery.config.getBoolean("use-custom-enchant") && NOT_REGISTERED_ENCHANTS.isEmpty() && !CUSTOM_ENCHANTS.keySet().isEmpty();
	}

	public static void onEnable()
	{
		registerEnchants();
		if (!isEnabled())
		{
			MessageUtil.sendWarn(Bukkit.getConsoleSender(), "커스텀 인챈트 기능을 사용하려 했으나 데이터 팩이 존재하지 않거나 일부 인챈트가 정상적으로 등록되지 않아 커스텀 인챈트 기능이 비활성화됩니다");
		}
	}
}
