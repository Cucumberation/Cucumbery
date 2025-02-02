package com.jho5245.cucumbery.listeners.player.no_groups;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.CustomEffect;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectTypeCustomMining;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.storage.data.Variable;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.util.RayTraceResult;

import java.util.Objects;
import java.util.UUID;

public class PlayerArmSwing implements Listener
{
	@EventHandler
	public void onPlayerArmSwing(PlayerArmSwingEvent event)
	{
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
		{
			return;
		}
		CustomEffectManager.addEffect(player, CustomEffectType.ARM_SWING);
		Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> {
			UUID uuid = player.getUniqueId();
			if (!Variable.customMiningBlockBreakCooldown.contains(uuid) && CustomEffectManager.hasEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE))
			{
				// 블록을 때리는 위치가 저장되었으나 삭제되지 못해 현재 때리는 위치랑 다를 경우 위치 정보 갱신
				if (Variable.customMiningFallbackLocation.containsKey(uuid))
				{
					Location location = Variable.customMiningFallbackLocation.get(uuid);
					double playerBlockInteractionRange = Objects.requireNonNull(player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE)).getValue();
					RayTraceResult rayTraceResult = player.rayTraceBlocks(playerBlockInteractionRange, FluidCollisionMode.NEVER);
					Block targetBlock = rayTraceResult != null ? rayTraceResult.getHitBlock() : null;
					Location targetLocation = targetBlock != null ? targetBlock.getLocation() : null;
					if (targetLocation != null)
					{
						if (targetLocation.getBlockX() != location.getBlockX() || targetLocation.getBlockY() != location.getBlockY()
								|| targetLocation.getBlockZ() != location.getBlockZ())
						{
							CustomEffectManager.removeEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_PROGRESS);
							Variable.customMiningFallbackLocation.remove(uuid);
							customMining(player, rayTraceResult);
							return;
						}

						// 위치가 같은데 채광 효과가 없을 경우! 채광 시작.
						else if (targetLocation.getBlockX() == location.getBlockX() && targetLocation.getBlockY() == location.getBlockY()
								&& targetLocation.getBlockZ() == location.getBlockZ() && !CustomEffectManager.hasEffect(player,
								CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_PROGRESS))
						{
							if (Variable.customMiningCooldown.containsKey(targetLocation))
							{
								return;
							}
							customMining(player, rayTraceResult);
							return;
						}
					}
				}
				if (!CustomEffectManager.hasEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_PROGRESS) && !CustomEffectManager.hasEffect(player,
						CustomEffectType.IGNORE_ARM_SWING) && !Variable.customMiningFallbackLocation.containsKey(uuid))
				{
					double playerBlockInteractionRange = Objects.requireNonNull(player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE)).getValue();
					RayTraceResult rayTraceResult = player.rayTraceBlocks(playerBlockInteractionRange, FluidCollisionMode.NEVER);
					customMining(player, rayTraceResult);
				}
			}
		}, 0L);
	}

	private void customMining(Player player, RayTraceResult rayTraceResult)
	{
		Block targetBlock = rayTraceResult != null ? rayTraceResult.getHitBlock() : null;
		if (targetBlock != null)
		{
			BlockFace blockFace = rayTraceResult.getHitBlockFace();
			Variable.customMiningFallbackLocation.put(player.getUniqueId(), targetBlock.getLocation().clone());
			BlockDamageEvent blockDamageEvent = new BlockDamageEvent(player, targetBlock, blockFace != null ? blockFace : BlockFace.SOUTH,
					player.getInventory().getItemInMainHand(), false);
			Bukkit.getPluginManager().callEvent(blockDamageEvent);
		}
	}
}
