package com.jho5245.cucumbery.listeners.player.no_groups;

import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerArmSwing implements Listener
{
	@EventHandler
	public void onPlayerArmSwing(PlayerArmSwingEvent event)
	{
		Player player = event.getPlayer();
		CustomEffectManager.addEffect(player, CustomEffectType.ARM_SWING);
	}
}
