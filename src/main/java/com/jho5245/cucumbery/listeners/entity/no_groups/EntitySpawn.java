package com.jho5245.cucumbery.listeners.entity.no_groups;

import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.children.group.LongCustomEffectImple;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.util.blockplacedata.BlockPlaceDataConfig;
import com.jho5245.cucumbery.util.storage.data.CustomMaterial;
import com.jho5245.cucumbery.util.storage.data.Variable;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;

public class EntitySpawn implements Listener
{
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event)
	{
		Entity entity = event.getEntity();
		if (entity instanceof Projectile projectile)
		{
			UUID projectileUUID = projectile.getUniqueId();
			ProjectileSource projectileSource = projectile.getShooter();
			if (projectileSource instanceof BlockProjectileSource blockProjectileSource)
			{
				Variable.entityAndSourceLocation.put(projectileUUID, blockProjectileSource.getBlock().getLocation().toString());
			}
			// 엔더 진주는 무기로 취급하지 않는다
			if (!(projectile instanceof EnderPearl) && projectile instanceof ThrowableProjectile throwableProjectile)
			{
				ItemStack item = throwableProjectile.getItem();
				if (projectileSource instanceof LivingEntity livingEntity)
				{
					Variable.attackerAndWeapon.put(livingEntity.getUniqueId(), item.clone());
				}
				else if (projectileSource instanceof BlockProjectileSource blockProjectileSource)
				{
					Variable.projectile.put(projectileUUID, Variable.blockAttackerAndWeapon.get(blockProjectileSource.getBlock().getLocation().toString()));
				}
				else
				{
					Variable.projectile.put(projectileUUID, item.clone());
				}
			}
			else if (projectile instanceof Firework firework)
			{
				ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);
				item.setItemMeta(firework.getFireworkMeta());
				if (projectileSource instanceof LivingEntity livingEntity)
				{
					ItemStack crossBow = ItemStackUtil.getPlayerUsingItem(livingEntity, Material.CROSSBOW);
					if (ItemStackUtil.itemExists(crossBow) && firework.isShotAtAngle())
					{
						Variable.projectile.put(firework.getUniqueId(), crossBow.clone());
						Variable.attackerAndWeapon.put(livingEntity.getUniqueId(), crossBow.clone());
					}
					else
					{
						Variable.projectile.put(firework.getUniqueId(), item.clone());
						Variable.attackerAndWeapon.put(livingEntity.getUniqueId(), item.clone());
					}
				}
				else if (projectileSource instanceof BlockProjectileSource blockProjectileSource)
				{

					Variable.projectile.put(projectileUUID, Variable.blockAttackerAndWeapon.get(blockProjectileSource.getBlock().getLocation().toString()));
				}
				else
				{

					Variable.projectile.put(firework.getUniqueId(), item.clone());
					Location loc = projectile.getLocation();
					String key = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).toString();
					Variable.entityAndSourceLocation.put(projectileUUID, key);
				}
			}
		}
		if (entity instanceof TNTPrimed tntPrimed)
		{
			ItemStack itemStack = BlockPlaceDataConfig.getItem(entity.getLocation());
			CustomMaterial customMaterial = CustomMaterial.itemStackOf(itemStack);
			if (customMaterial != null)
			{
				switch (customMaterial)
				{
					case TNT_I_WONT_LET_YOU_GO -> entity.getScoreboardTags().add("custom_material_tnt_i_wont_let_you_go");
					case TNT_COMBAT -> entity.getScoreboardTags().add("custom_material_tnt_combat");
					case TNT_DRAIN -> entity.getScoreboardTags().add("custom_material_tnt_drain");
					case TNT_DONUT ->
					{
						entity.getScoreboardTags().add("custom_material_tnt_donut");
						if (itemStack != null)
						{
							CustomEffectManager.addEffect(entity,
									new LongCustomEffectImple(CustomEffectType.CUSTOM_MATERIAL_TNT_DONUT, new NBTItem(itemStack).getShort("Size")));
						}
					}
				}
			}
			if (ItemStackUtil.itemExists(itemStack))
			{
				NBTItem nbtItem = new NBTItem(itemStack);
				Float explodePower = nbtItem.getFloat("ExplodePower");
				if (nbtItem.hasTag("ExplodePower") && nbtItem.getType("ExplodePower") == NBTType.NBTTagFloat && explodePower != null)
				{
					tntPrimed.setYield(explodePower);
				}
				Boolean fire = nbtItem.getBoolean("Fire");
				if (nbtItem.hasTag("Fire") && nbtItem.getType("Fire") == NBTType.NBTTagByte && fire != null && fire)
				{
					tntPrimed.setIsIncendiary(true);
				}
				Short fuse = nbtItem.getShort("Fuse");
				if (nbtItem.hasTag("Fuse") && nbtItem.getType("Fuse") == NBTType.NBTTagShort && fuse != null)
				{
					tntPrimed.setFuseTicks(fuse);
				}
			}
		}
	}
}
