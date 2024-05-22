package com.jho5245.cucumbery.util.itemlore;

import com.jho5245.cucumbery.util.storage.data.Constant;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemLoreEnchantRarity
{
  @SuppressWarnings("all")
  public static void enchantRarity(@NotNull ItemStack itemStack, @NotNull List<Component> lore, @NotNull Material material, @NotNull ItemMeta itemMeta)
  {
    int enchantSize = itemMeta.getEnchants().size();
    double init = 10d;
//    for (int i = 0; i < enchantSize - 1; i++)
//    {
//      init = init * Math.pow(1.1, 2);
//    }
    long value = 0;
    value += enchantSize * (int) init;
    if (itemMeta.hasEnchant(Enchantment.BINDING_CURSE))
    {
      value -= 25;
    }
    if (itemMeta.hasEnchant(Enchantment.VANISHING_CURSE))
    {
      value -= 25;
    }
    if (Constant.DURABLE_ITEMS.contains(material) && itemMeta.hasEnchant(Enchantment.MENDING))
    {
      value += 200;
    }

    // 보호
    if (itemMeta.hasEnchant(Enchantment.PROTECTION))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.PROTECTION);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.4, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }
      }
      value += level * (int) init;
    }
    // 폭발로부터 보호
    if (itemMeta.hasEnchant(Enchantment.BLAST_PROTECTION))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.BLAST_PROTECTION);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.5, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 화염으로부터 보호
    if (itemMeta.hasEnchant(Enchantment.FIRE_PROTECTION))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.FIRE_PROTECTION);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.3, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 발사체로부터 보호
    if (itemMeta.hasEnchant(Enchantment.PROJECTILE_PROTECTION))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.PROJECTILE_PROTECTION);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.4, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 가시
    if (itemMeta.hasEnchant(Enchantment.THORNS))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.THORNS);
      init = 15D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.3, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 호흡
    if (itemMeta.hasEnchant(Enchantment.RESPIRATION))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.RESPIRATION);
      init = 15D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.3, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 친수성
    if (itemMeta.hasEnchant(Enchantment.AQUA_AFFINITY))
    {
      value += 100;
    }
    // 가벼운 착지
    if (itemMeta.hasEnchant(Enchantment.FEATHER_FALLING))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.FEATHER_FALLING);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.4, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 물갈퀴
    if (itemMeta.hasEnchant(Enchantment.DEPTH_STRIDER))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.5, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 신발 - 차가운 걸음
    if (itemMeta.hasEnchant(Enchantment.FROST_WALKER))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.FROST_WALKER);
      init = 80D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(2, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 신발 - 영혼 가속
    if (itemMeta.hasEnchant(Enchantment.SOUL_SPEED))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.SOUL_SPEED);
      init = 40D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.8, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 신발 -
    if (itemMeta.hasEnchant(Enchantment.SWIFT_SNEAK))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.SWIFT_SNEAK);
      init = 40D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.8, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 날카로움
    if (itemMeta.hasEnchant(Enchantment.SHARPNESS))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.SHARPNESS);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.25, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 강타
    if (itemMeta.hasEnchant(Enchantment.SMITE))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.SMITE);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.15, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 살충
    if (itemMeta.hasEnchant(Enchantment.BANE_OF_ARTHROPODS))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.BANE_OF_ARTHROPODS);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.1, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 약탈
    if (itemMeta.hasEnchant(Enchantment.LOOTING))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.LOOTING);
      init = 25D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.8, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 검 - 휘몰아치는 칼날
    if (Constant.SWORDS.contains(material) && itemMeta.hasEnchant(Enchantment.SWEEPING_EDGE))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.SWEEPING_EDGE);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.6, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 발화
    if (itemMeta.hasEnchant(Enchantment.FIRE_ASPECT))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.FIRE_ASPECT);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.6, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 밀치기
    if (itemMeta.hasEnchant(Enchantment.KNOCKBACK))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.KNOCKBACK);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.3, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }
      }
      value += level * (int) init;
    }
    // 활 - 힘
    if (material == Material.BOW && itemMeta.hasEnchant(Enchantment.POWER))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.POWER);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.2, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 활 - 화염
    if (material == Material.BOW && itemMeta.hasEnchant(Enchantment.FLAME))
    {
      value += 50;
    }
    // 활 - 무한
    if (material == Material.BOW && itemMeta.hasEnchant(Enchantment.INFINITY))
    {
      value += 50;
    }
    // 활 - 밀어내기
    if (material == Material.BOW && itemMeta.hasEnchant(Enchantment.PUNCH))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.PUNCH);
      init = 30D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.6, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 삼지창 - 찌르기
    if (material == Material.TRIDENT && itemMeta.hasEnchant(Enchantment.IMPALING))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.IMPALING);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.3, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 삼지창 - 충절
    if (material == Material.TRIDENT && itemMeta.hasEnchant(Enchantment.LOYALTY))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.LOYALTY);
      init = 10D;
      for (int i = 0; i < level - 1; i++)
      {
        init = init * Math.pow(1.4, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 삼지창 - 급류
    if (material == Material.TRIDENT && itemMeta.hasEnchant(Enchantment.RIPTIDE))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.RIPTIDE);
      init = 10D;
      for (int i = 0; i < level - 1; i++)
      {
        init = init * Math.pow(1.4, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 삼지창 - 집전
    if (material == Material.TRIDENT && itemMeta.hasEnchant(Enchantment.CHANNELING))
    {
      value += 80;
    }
    // 쇠뇌 - 빠른 장전
    if (material == Material.CROSSBOW && itemMeta.hasEnchant(Enchantment.QUICK_CHARGE))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.QUICK_CHARGE);
      if (level <= 5)
      {
        init = 10D;
        for (int i = 0; i < level - 1; i++)
        {
          init = init * Math.pow(1.8, i);
          if ((long) level * (int) init > 1_000_000_000)
          {
            init = 1_000_000_000 / level;
          }

        }
        value += level * (int) init;
      }
      else
      {
        value = -1_000_000_000;
      }
    }
    // 쇠뇌 - 관통
    if (material == Material.CROSSBOW && itemMeta.hasEnchant(Enchantment.PIERCING))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.PIERCING);
      init = 10D;
      for (int i = 0; i < level - 1; i++)
      {
        init = init * Math.pow(1.3, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 쇠뇌 - 다중 발사
    if (material == Material.CROSSBOW && itemMeta.hasEnchant(Enchantment.MULTISHOT))
    {
      value += 50;
    }
    // 도구 - 효율
    if (Constant.TOOLS.contains(material) && itemMeta.hasEnchant(Enchantment.EFFICIENCY))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.EFFICIENCY);
      init = 20D;
      for (int i = 0; i < level - 1; i++)
      {
        init = init * Math.pow(1.2, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 행운
    if (itemMeta.hasEnchant(Enchantment.FORTUNE))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.FORTUNE);
      init = 30D;
      for (int i = 0; i < level - 1; i++)
      {
        init = init * Math.pow(1.8, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 섬세한 손길
    if (itemMeta.hasEnchant(Enchantment.SILK_TOUCH))
    {
      value += 50;
    }
    // 낚싯대 - 미끼
    if (material == Material.FISHING_ROD && itemMeta.hasEnchant(Enchantment.LURE))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.LURE);
      if (level <= 5)
      {
        init = 10D;
        for (int i = 0; i < level - 1; i++)
        {
          init = init * Math.pow(1.4, i);
          if ((long) level * (int) init > 1_000_000_000)
          {
            init = 1_000_000_000 / level;
          }

        }
        value += level * 30 + level * (int) init;
      }
      else
      {
        value = -1_000_000_000;
      }
    }
    // 낚싯대 - 바다의 행운
    if (material == Material.FISHING_ROD && itemMeta.hasEnchant(Enchantment.LUCK_OF_THE_SEA))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA);
      init = 25D;
      for (int i = 0; i < level - 1; i++)
      {
        init = init * Math.pow(2., i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 내구성
    if (Constant.DURABLE_ITEMS.contains(material) && itemMeta.hasEnchant(Enchantment.UNBREAKING))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.UNBREAKING);
      init = 20D;
      for (int i = 1; i < level; i++)
      {
        if (Constant.ARMORS.contains(material))
        {
          init = init * Math.pow(1.1, i);
        }
        else
        {
          init = init * Math.pow(1.3, i);
        }
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 내구성과 수선 동시 적용
    if (Constant.DURABLE_ITEMS.contains(material) && itemMeta.hasEnchant(Enchantment.UNBREAKING) && itemMeta.hasEnchant(Enchantment.MENDING))
    {
      int level = itemStack.getEnchantmentLevel(Enchantment.UNBREAKING);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.2, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }

    ItemLoreUtil.setItemRarityValue(lore, value);
  }

  @SuppressWarnings("all")
  public static void enchantedBookRarity(@NotNull ItemStack itemStack, @NotNull List<Component> lore, @NotNull Material material, @NotNull EnchantmentStorageMeta bookMeta)
  {
    int enchantSize = bookMeta.getEnchants().size();
    double init = 10d;
    long value = 0;
    value += enchantSize * (int) init;
    if (bookMeta.hasStoredEnchant(Enchantment.BINDING_CURSE))
    {
      value -= 25;
    }
    if (bookMeta.hasStoredEnchant(Enchantment.VANISHING_CURSE))
    {
      value -= 25;
    }
    if (bookMeta.hasStoredEnchant(Enchantment.MENDING))
    {
      value += 200;
    }

    // 갑옷 - 보호
    if (bookMeta.hasStoredEnchant(Enchantment.PROTECTION))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.PROTECTION);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.4, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 갑옷 - 폭발로부터 보호
    if (bookMeta.hasStoredEnchant(Enchantment.BLAST_PROTECTION))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.BLAST_PROTECTION);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.3, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 갑옷 - 화염으로부터 보호
    if (bookMeta.hasStoredEnchant(Enchantment.FIRE_PROTECTION))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.FIRE_PROTECTION);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.3, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 갑옷 - 발사체로부터 보호
    if (bookMeta.hasStoredEnchant(Enchantment.PROJECTILE_PROTECTION))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.PROJECTILE_PROTECTION);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.4, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 갑옷 - 가시
    if (bookMeta.hasStoredEnchant(Enchantment.THORNS))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.THORNS);
      init = 15D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.3, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 투구 - 호흡
    if (bookMeta.hasStoredEnchant(Enchantment.RESPIRATION))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.RESPIRATION);
      init = 15D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.3, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 투구 - 친수성
    if (bookMeta.hasStoredEnchant(Enchantment.AQUA_AFFINITY))
    {
      value += 100;
    }
    // 신발 - 가벼운 착지
    if (bookMeta.hasStoredEnchant(Enchantment.FEATHER_FALLING))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.FEATHER_FALLING);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.4, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 신발 - 물갈퀴
    if (bookMeta.hasStoredEnchant(Enchantment.DEPTH_STRIDER))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.DEPTH_STRIDER);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.4, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 신발 - 차가운 걸음
    if (bookMeta.hasStoredEnchant(Enchantment.FROST_WALKER))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.FROST_WALKER);
      init = 80D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(2, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 신발 - 영혼 가속
    if (bookMeta.hasStoredEnchant(Enchantment.SOUL_SPEED))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.SOUL_SPEED);
      init = 40D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.8, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 신발 - 신속한 잠행
    if (bookMeta.hasStoredEnchant(Enchantment.SWIFT_SNEAK))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.SWIFT_SNEAK);
      init = 40D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.8, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 검 - 날카로움
    if (bookMeta.hasStoredEnchant(Enchantment.SHARPNESS))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.SHARPNESS);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.3, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 검 - 강타
    if (bookMeta.hasStoredEnchant(Enchantment.SMITE))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.SMITE);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.25, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 검 - 살충
    if (bookMeta.hasStoredEnchant(Enchantment.BANE_OF_ARTHROPODS))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.BANE_OF_ARTHROPODS);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.1, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 검 - 약탈
    if (bookMeta.hasStoredEnchant(Enchantment.LOOTING))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.LOOTING);
      init = 25D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.8, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 검 - 휘몰아치는 칼날
    if (bookMeta.hasStoredEnchant(Enchantment.SWEEPING_EDGE))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.SWEEPING_EDGE);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.6, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 검 - 발화
    if (bookMeta.hasStoredEnchant(Enchantment.FIRE_ASPECT))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.FIRE_ASPECT);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.6, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 검 - 밀치기
    if (bookMeta.hasStoredEnchant(Enchantment.KNOCKBACK))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.KNOCKBACK);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.1, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 활 - 힘
    if (bookMeta.hasStoredEnchant(Enchantment.POWER))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.POWER);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.2, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 활 - 화염
    if (bookMeta.hasStoredEnchant(Enchantment.FLAME))
    {
      value += 50;
    }
    // 활 - 무한
    if (bookMeta.hasStoredEnchant(Enchantment.INFINITY))
    {
      value += 50;
    }
    // 활 - 밀어내기
    if (bookMeta.hasStoredEnchant(Enchantment.PUNCH))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.PUNCH);
      init = 30D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.6, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 삼지창 - 찌르기
    if (bookMeta.hasStoredEnchant(Enchantment.IMPALING))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.IMPALING);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.3, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 삼지창 - 충절
    if (bookMeta.hasStoredEnchant(Enchantment.LOYALTY))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.LOYALTY);
      init = 10D;
      for (int i = 0; i < level - 1; i++)
      {
        init = init * Math.pow(1.4, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 삼지창 - 급류
    if (bookMeta.hasStoredEnchant(Enchantment.RIPTIDE))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.RIPTIDE);
      init = 10D;
      for (int i = 0; i < level - 1; i++)
      {
        init = init * Math.pow(1.4, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 삼지창 - 집전
    if (bookMeta.hasStoredEnchant(Enchantment.CHANNELING))
    {
      value += 80;
    }
    // 쇠뇌 - 빠른 장전
    if (bookMeta.hasStoredEnchant(Enchantment.QUICK_CHARGE))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.QUICK_CHARGE);
      if (level <= 5)
      {
        init = 10D;
        for (int i = 0; i < level - 1; i++)
        {
          init = init * Math.pow(1.7, i);
          if ((long) level * (int) init > 1_000_000_000)
          {
            init = 1_000_000_000 / level;
          }

        }
        value += level * (int) init;
      }
      else
      {
        value = -1_000_000_000;
      }
    }
    // 쇠뇌 - 관통
    if (bookMeta.hasStoredEnchant(Enchantment.PIERCING))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.PIERCING);
      init = 10D;
      for (int i = 0; i < level - 1; i++)
      {
        init = init * Math.pow(1.5, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 쇠뇌 - 다중 발사
    if (bookMeta.hasStoredEnchant(Enchantment.MULTISHOT))
    {
      value += 50;
    }
    // 도구 - 효율
    if (bookMeta.hasStoredEnchant(Enchantment.EFFICIENCY))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.EFFICIENCY);
      init = 20D;
      for (int i = 0; i < level - 1; i++)
      {
        init = init * Math.pow(1.2, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }
      }
      value += level * (int) init;
    }
    // 도구 - 행운
    if (bookMeta.hasStoredEnchant(Enchantment.FORTUNE))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.FORTUNE);
      init = 30D;
      for (int i = 0; i < level - 1; i++)
      {
        init = init * Math.pow(1.8, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 도구 - 섬세한 손길
    if (bookMeta.hasStoredEnchant(Enchantment.SILK_TOUCH))
    {
      value += 50;
    }
    // 낚싯대 - 미끼
    if (bookMeta.hasStoredEnchant(Enchantment.LURE))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.LURE);
      if (level <= 5)
      {
        init = 10D;
        for (int i = 0; i < level - 1; i++)
        {
          init = init * Math.pow(1.4, i);
          if ((long) level * (int) init > 1_000_000_000)
          {
            init = 1_000_000_000 / level;
          }

        }
        value += level * 30 + level * (int) init;
      }
      else
      {
        value = -1_000_000_000;
      }
    }
    // 낚싯대 - 바다의 행운
    if (bookMeta.hasStoredEnchant(Enchantment.LUCK_OF_THE_SEA))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.LUCK_OF_THE_SEA);
      init = 25D;
      for (int i = 0; i < level - 1; i++)
      {
        init = init * Math.pow(2., i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    // 내구성
    if (bookMeta.hasStoredEnchant(Enchantment.UNBREAKING))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.UNBREAKING);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.5, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }

    // 내구성과 수선 동시 적용
    if (bookMeta.hasStoredEnchant(Enchantment.UNBREAKING) && bookMeta.hasStoredEnchant(Enchantment.MENDING))
    {
      int level = bookMeta.getStoredEnchantLevel(Enchantment.UNBREAKING);
      init = 10D;
      for (int i = 1; i < level; i++)
      {
        init = init * Math.pow(1.1, i);
        if ((long) level * (int) init > 1_000_000_000)
        {
          init = 1_000_000_000 / level;
        }

      }
      value += level * (int) init;
    }
    ItemLoreUtil.setItemRarityValue(lore, value);
  }
}
