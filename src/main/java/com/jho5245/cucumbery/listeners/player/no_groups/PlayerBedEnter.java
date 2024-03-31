package com.jho5245.cucumbery.listeners.player.no_groups;

import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;

public class PlayerBedEnter implements Listener
{
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}

		Player player = event.getPlayer();
		Player wnynya = Bukkit.getPlayer("Wnynya");
		BedEnterResult bedEnterResult = event.getBedEnterResult();
		if (bedEnterResult == BedEnterResult.OK && wnynya != null)
		{
			MessageUtil.broadcastDebug(ComponentUtil.translate(player, "%s(이)가 온라인이다! 그래서 %s은(는) 잠을 자면 이불이 너무 좋아서 잠시 일어날 수 없다!", wnynya, player));
			CustomEffectManager.addEffect(player, CustomEffectType.BLANKET_LOVER);
		}
	}
}
