package com.jho5245.cucumbery.listeners.player.no_groups;

import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.children.group.DoubleCustomEffectImple;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;

public class PlayerToggleSprint implements Listener
{
	@EventHandler
	public void onPlayerToggleSprint(PlayerToggleSprintEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		Player player = event.getPlayer();
		if (CustomEffectManager.hasEffect(player, CustomEffectType.STOP))
		{
			event.setCancelled(true);
			return;
		}

		boolean isSprinting = event.isSprinting();
		if (CustomEffectManager.hasEffect(player, CustomEffectType.SECRET_GUARD))
		{
			int amplifier = CustomEffectManager.getEffect(player, CustomEffectType.SECRET_GUARD).getAmplifier();
			if (isSprinting)
			{
				CustomEffectManager.addEffect(player, CustomEffectType.SECRET_GUARD_EFFECT, CustomEffectType.SECRET_GUARD_EFFECT.getDefaultDuration(), amplifier);
				double amount = CustomEffectManager.getAttributeModifierAmount(CustomEffectManager.getEffect(player, CustomEffectType.SECRET_GUARD_EFFECT));
				CustomEffectManager.addEffect(player, new DoubleCustomEffectImple(CustomEffectType.SECRET_GUARD_EFFECT_PROTOCOL, amount));
			}
			else
			{
				AttributeInstance attributeInstance = player.getAttribute(Attribute.CAMERA_DISTANCE);
				if (attributeInstance != null)
				{
					AttributeModifier attributeModifier = attributeInstance.getModifier(CustomEffectType.SECRET_GUARD_EFFECT.getNamespacedKey());
					if (attributeModifier != null)
					{
						CustomEffectManager.addEffect(player, new DoubleCustomEffectImple(CustomEffectType.SECRET_GUARD_EFFECT_PROTOCOL, attributeModifier.getAmount()));
					}
				}
				CustomEffectManager.removeEffect(player, CustomEffectType.SECRET_GUARD_EFFECT);
			}
		}
	}
}
