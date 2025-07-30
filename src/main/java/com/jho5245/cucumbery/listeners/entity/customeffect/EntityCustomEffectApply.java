package com.jho5245.cucumbery.listeners.entity.customeffect;

import com.jho5245.cucumbery.custom.customeffect.CustomEffect;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.children.group.AttributeCustomEffect;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.events.entity.EntityCustomEffectApplyEvent;
import com.jho5245.cucumbery.util.storage.data.Variable;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class EntityCustomEffectApply implements Listener
{
	@EventHandler
	public void onEntityCustomEffectApply(EntityCustomEffectApplyEvent event)
	{
		Entity entity = event.getEntity();
		UUID uuid = entity.getUniqueId();
		CustomEffect customEffect = event.getCustomEffect();
		CustomEffectType customEffectType = customEffect.getType();
		if (customEffectType.isToggle() && CustomEffectManager.hasEffect(entity, customEffectType))
		{
			CustomEffectManager.removeEffect(entity, customEffectType);
			event.setCancelled(true);
		}
		if (customEffectType == CustomEffectType.PVP_MODE_ENABLED)
		{
			CustomEffectManager.addEffect(entity, CustomEffectType.PVP_MODE_COOLDOWN);
		}

		if (customEffect instanceof AttributeCustomEffect attributeCustomEffect)
		{
			if (!(entity instanceof Attributable attributable))
			{
				event.setCancelled(true);
				return;
			}
			Attribute attribute = attributeCustomEffect.getAttribute();
			AttributeInstance attributeInstance = attributable.getAttribute(attribute);
			if (attributeInstance == null)
			{
				event.setCancelled(true);
			}
			else
			{
				double value = attributeInstance.getValue();
				// 카메라 거리 변경 애니메이션 효과
				if (List.of(
						CustomEffectType.SECRET_GUARD_EFFECT, CustomEffectType.SNEAK_TO_GIANT_EFFECT
				).contains(customEffectType))
				{
					Map<Attribute, List<Double>> attributeListMap = Variable.ATTRIBUTE_AMOUNT_BEFORE_AFTER.getOrDefault(uuid, new HashMap<>());
					attributeListMap.put(attribute, Collections.singletonList(value));
					Variable.ATTRIBUTE_AMOUNT_BEFORE_AFTER.put(uuid, attributeListMap);
				}
			}
		}
	}
}
