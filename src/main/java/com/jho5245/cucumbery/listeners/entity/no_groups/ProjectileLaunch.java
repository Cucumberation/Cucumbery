package com.jho5245.cucumbery.listeners.entity.no_groups;

import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.util.storage.data.CustomMaterial;
import com.jho5245.cucumbery.util.storage.data.custom_enchant.CustomEnchant;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class ProjectileLaunch implements Listener
{
  @EventHandler
  public void onProjectileLaunch(ProjectileLaunchEvent event)
  {
    if (event.isCancelled())
    {
      return;
    }
    Projectile projectile = event.getEntity();
    ProjectileSource projectileSource = projectile.getShooter();
    if (projectileSource instanceof Entity entity)
    {
      if (projectile instanceof Trident)
      {
        int level = 0;
        if (CustomEffectManager.hasEffect(entity, CustomEffectType.IDIOT_SHOOTER))
        {
          level = (CustomEffectManager.getEffect(entity, CustomEffectType.IDIOT_SHOOTER).getAmplifier() + 1);
        }
        if (entity instanceof LivingEntity livingEntity)
        {
          ItemStack itemStack = ItemStackUtil.getPlayerUsingItem(livingEntity, Material.TRIDENT);
          if (ItemStackUtil.itemExists(itemStack) && itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchants())
          {
            level = Math.max(level, CustomEnchant.isEnabled() ? itemStack.getItemMeta().getEnchantLevel(CustomEnchant.IDIOT_SHOOTER) : 0);
          }
        }
        if (level > 0)
        {
          double modifier = level / 10d;
          Vector vector = entity.getLocation().getDirection();
          projectile.setVelocity(vector.add(new Vector(Math.random() * modifier - (modifier / 2d), Math.random() * modifier - (modifier / 2d), Math.random() * modifier - (modifier / 2d))));
        }
      }
    }

    if (projectile instanceof Snowball snowball)
    {
      ItemStack snowBallItemStack = snowball.getItem();
      CustomMaterial customMaterial = CustomMaterial.itemStackOf(snowBallItemStack);
      if (customMaterial != null)
      {
        switch (customMaterial)
        {
          case BRICK_THROWABLE -> snowBallItemStack.setType(Material.BRICK);
          case NETHER_BRICK_THROWABLE -> snowBallItemStack.setType(Material.NETHER_BRICK);
          case COPPER_INGOT_THROWABLE -> snowBallItemStack.setType(Material.COPPER_INGOT);
          case IRON_INGOT_THROWABLE -> snowBallItemStack.setType(Material.IRON_INGOT);
          case GOLD_INGOT_THROWABLE -> snowBallItemStack.setType(Material.GOLD_INGOT);
          case NETHERITE_INGOT_THROWABLE -> snowBallItemStack.setType(Material.NETHERITE_INGOT);
        }
        snowball.setItem(snowBallItemStack);
        snowball.setVelocity(snowball.getVelocity().multiply(0.5d));
      }
    }
  }
}
