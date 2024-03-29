package com.jho5245.cucumbery.listeners.entity.no_groups;

import com.jho5245.cucumbery.custom.customeffect.CustomEffect;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectTypeCustomMining;
import com.jho5245.cucumbery.events.entity.EntityCustomEffectAbstractApplyEvent.ApplyReason;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.data.Variable;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class EntityPotionEffect implements Listener
{
	@EventHandler
	public void onEntityPotionEffect(EntityPotionEffectEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		Entity entity = event.getEntity();
		UUID uuid = entity.getUniqueId();
		Action action = event.getAction();
		Cause cause = event.getCause();
		PotionEffect newEffect = event.getNewEffect();
		PotionEffect oldEffect = event.getOldEffect();
		if (newEffect != null && (action == Action.ADDED || action == Action.CHANGED))
		{
			int duration = newEffect.getDuration();
			PotionEffectType effectType = newEffect.getType();
			// 커스텀 채광 시스템으로 인한 성급함, 채굴 피로 효과 적용 여부
			boolean isCustomMiningBaseEffect = duration == 2 && (effectType.equals(PotionEffectType.FAST_DIGGING) || effectType.equals(PotionEffectType.SLOW_DIGGING))
					&& CustomEffectManager.hasEffect(entity, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE);
			if (!isCustomMiningBaseEffect)
			{
				HashMap<String, Integer> hashMap;
				if (Variable.potionEffectApplyMap.containsKey(uuid))
				{
					hashMap = Variable.potionEffectApplyMap.get(uuid);
				}
				else
				{
					hashMap = new HashMap<>();
				}
				hashMap.put(effectType.translationKey(), duration);
				Variable.potionEffectApplyMap.put(uuid, hashMap);
				CustomEffectType customEffectType = CustomEffectType.getByKey(effectType.getKey());
				CustomEffect customEffect = customEffectType != null ? CustomEffectManager.getEffectNullable(entity, customEffectType) : null;
				if (customEffectType != null && (customEffect == null || customEffect.getAmplifier() != newEffect.getAmplifier()
						|| Math.abs(customEffect.getDuration() - newEffect.getDuration()) >= 2))
				{
					// 바닐라 효과와 유사한 커스텀 효과(예: 우유를 마셔도 안사라지는 독)가 있을 경우 효과를 지급하지 않음
					boolean hasSimilar = false;
					for (CustomEffect effect : CustomEffectManager.getEffects(entity))
					{
						NamespacedKey namespacedKey = effect.getType().getNamespacedKey();
						if (namespacedKey.getNamespace().equals("minecraft") && namespacedKey.getKey().contains("__") && effectType.getKey().getKey()
								.equals(namespacedKey.getKey().split("__")[0]))
						{
							hasSimilar = true;
							break;
						}
					}
					if (!hasSimilar)
						CustomEffectManager.addEffect(entity, new CustomEffect(customEffectType, duration, newEffect.getAmplifier()), ApplyReason.PLUGIN, false, false);
				}
			}
		}

		PotionEffectType effectType = event.getModifiedType();

		if (oldEffect != null && (action == Action.CLEARED || action == Action.REMOVED))
		{
			CustomEffectType customEffectType = CustomEffectType.getByKey(effectType.getKey());
			if (customEffectType != null && CustomEffectManager.hasEffect(entity, customEffectType))
			{
				CustomEffect customEffect = CustomEffectManager.getEffect(entity, customEffectType);
				// MessageUtil.broadcast(ComponentUtil.translate("CED: %s, PED: %s, CEA: %s, PEA: %s", customEffect.getDuration(), oldEffect.getDuration(), customEffect.getAmplifier(), oldEffect.getAmplifier()));
				if (Math.abs(customEffect.getDuration() - oldEffect.getDuration()) <= 1 && customEffect.getAmplifier() == oldEffect.getAmplifier())
				{
					if (!(cause == Cause.MILK && customEffect.isKeepOnMilk() || cause == Cause.DEATH && customEffect.isKeepOnDeath()))
						CustomEffectManager.removeEffect(entity, customEffectType, oldEffect.getAmplifier(), false);
					if (cause == Cause.COMMAND)
					{
						CustomEffectManager.removeEffect(entity, customEffectType, false);
					}
				}
			}
		}

		if (CustomEffectManager.hasEffect(entity, CustomEffectType.LEVITATION_RESISTANCE) && effectType.equals(PotionEffectType.LEVITATION)
				&& cause == Cause.ATTACK)
		{
			CustomEffect customEffect = CustomEffectManager.getEffect(entity, CustomEffectType.LEVITATION_RESISTANCE);
			int amplifier = customEffect.getAmplifier() + 1;
			if (Math.random() * 10d < amplifier)
			{
				event.setCancelled(true);
			}
		}
		if (entity instanceof Player player && cause == Cause.MILK && CustomEffectManager.hasEffect(player, CustomEffectType.CHEESE_EXPERIMENT))
		{
			CustomEffectManager.removeEffect(player, CustomEffectType.CHEESE_EXPERIMENT);
			event.setCancelled(true);
		}

		if (action == Action.ADDED && (effectType.equals(PotionEffectType.POISON) || effectType.equals(PotionEffectType.CONFUSION) || effectType.equals(
				PotionEffectType.BLINDNESS) || effectType.equals(PotionEffectType.UNLUCK)) && CustomEffectManager.hasEffect(entity, CustomEffectType.VAR_DETOXICATE)
				&& entity instanceof LivingEntity livingEntity)
		{
			CustomEffect customEffect = CustomEffectManager.getEffect(livingEntity, CustomEffectType.VAR_DETOXICATE);
			int amplifier = customEffect.getAmplifier();
			boolean doubleDecreased = Math.random() < (amplifier + 1), tripledDecreased = Math.random() < (amplifier + 1) * 0.1, allRemoved =
					doubleDecreased && tripledDecreased;
			if (allRemoved)
			{
				livingEntity.removePotionEffect(PotionEffectType.POISON);
				livingEntity.removePotionEffect(PotionEffectType.CONFUSION);
				livingEntity.removePotionEffect(PotionEffectType.BLINDNESS);
				livingEntity.removePotionEffect(PotionEffectType.UNLUCK);
				MessageUtil.sendMessage(livingEntity, Prefix.INFO_CUSTOM_EFFECT, "와 샌즈! %s의 효과로 인해 디버프가 제거되었습니다!", customEffect);
			}
			event.setCancelled(true);
		}
	}
}
