package com.jho5245.cucumbery.listeners.player.no_groups;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerBedLeave implements Listener
{
	private Set<UUID> blanketLoverNotificationCooldown = new HashSet<>();

	@EventHandler
	public void onPlayerBedLeave(PlayerBedLeaveEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		if (CustomEffectManager.hasEffect(player, CustomEffectType.BLANKET_LOVER))
		{
			event.setCancelled(true);
			if (!blanketLoverNotificationCooldown.contains(uuid))
			{
				int duration = CustomEffectManager.getEffect(player, CustomEffectType.BLANKET_LOVER).getDuration();
				MessageUtil.sendMessage(player, "우웅.. 이불 너무 좋아.. %s초만 더..", duration / 20);
				blanketLoverNotificationCooldown.add(uuid);
				Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> blanketLoverNotificationCooldown.remove(uuid), 5L);
			}
		}
	}
}
