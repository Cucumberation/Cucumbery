package com.jho5245.cucumbery.commands.no_groups;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent.Completion;
import com.jho5245.cucumbery.custom.customeffect.CustomEffect;
import com.jho5245.cucumbery.custom.customeffect.CustomEffect.DisplayType;
import com.jho5245.cucumbery.custom.customeffect.CustomEffect.OverridePropertyBuilder;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectGUI;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.events.entity.EntityCustomEffectAbstractApplyEvent.ApplyReason;
import com.jho5245.cucumbery.util.no_groups.*;

import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandCustomEffect implements CucumberyCommandExecutor
{
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
	{
		boolean failure = !(sender instanceof BlockCommandSender);
		if (!Method.hasPermission(sender, Permission.CMD_CUSTOM_EFFECT, true))
		{
			return true;
		}
		if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(args)))
		{
			return failure;
		}
		int length = args.length;
		if (length == 0)
		{
			MessageUtil.shortArg(sender, 1, args);
			MessageUtil.commandInfo(sender, label, "<give|clear|query> ...");
			return failure;
		}
		switch (args[0])
		{
			case "query" ->
			{
				if (length > 2)
				{
					MessageUtil.longArg(sender, 2, args);
					MessageUtil.commandInfo(sender, label, "query" + (sender instanceof Player ? " [개체]" : " <개체>"));
					return failure;
				}
				if (!(sender instanceof Player) && length < 2)
				{
					MessageUtil.shortArg(sender, 2, args);
					MessageUtil.commandInfo(sender, label, "query <개체>");
					return failure;
				}
				Entity target;
				boolean gui = true;
				if (length == 2)
				{
					target = SelectorUtil.getEntity(sender, args[1]);
					gui = false;
				}
				else
				{
					target = (Entity) sender;
				}
				if (target == null)
				{
					return failure;
				}
				// 명령 블록이나 콘솔에서 /customeffect query <닉네임> 사용 시 해당 플레이어에게 UI 열어주기
				if (!(sender instanceof Player))
				{
					queryEffect(target, target, true);
				}
				else
				{
					queryEffect(sender, target, gui);
				}
				return true;
			}
			case "clear" ->
			{
				if (length > 4)
				{
					MessageUtil.longArg(sender, 4, args);
					MessageUtil.commandInfo(sender, label, args[0] + (sender instanceof Player ? " [개체]" : " <개체>") + " [효과 종류] [명령어 출력 숨김 여부]");
					return failure;
				}
				Entity target = null;
				if (length < 2)
				{
					if (!(sender instanceof Player player))
					{
						MessageUtil.shortArg(sender, 2, args);
						MessageUtil.commandInfo(sender, label, args[0] + " <개체> [효과 종류] [명령어 출력 숨김 여부]");
						return failure;
					}
					target = player;
				}
				CustomEffectType effectType = null;
				boolean hideOutput = false;
				if (length == 1)
				{
					if (CustomEffectManager.hasEffects(target))
					{
						CustomEffectManager.clearEffects(target);
						MessageUtil.info(sender, "commands.effect.clear.everything.success.single", sender);
						MessageUtil.sendAdminMessage(sender, "commands.effect.clear.everything.success.single", sender);
						return true;
					}
					else
					{
						MessageUtil.sendError(sender, "commands.effect.clear.everything.failed");
						return failure;
					}
				}
				List<Entity> entities = SelectorUtil.getEntities(sender, args[1]);
				if (entities == null)
				{
					MessageUtil.commandInfo(sender, label, "clear &c<개체>&p [효과 종류] [명령어 출력 숨김 여부]");
					return failure;
				}
				if (length >= 3)
				{
					try
					{
						effectType = CustomEffectType.valueOf(args[2]);
					}
					catch (Exception e)
					{
						MessageUtil.wrongArg(sender, 3, args);
						MessageUtil.commandInfo(sender, label, "clear <개체> &c[효과 종류]&p [명령어 출력 숨김 여부]");
						return failure;
					}
				}
				if (length == 4)
				{
					if (!MessageUtil.isBoolean(sender, args, 4, true))
					{
						MessageUtil.commandInfo(sender, label, "clear <개체> [효과 종류] &c[명령어 출력 숨김 여부]");
						return failure;
					}
					hideOutput = Boolean.parseBoolean(args[3]);
				}
				return clearEffect(sender, entities, effectType, hideOutput) || failure;
			}
			case "give" ->
			{
				if (length < 3)
				{
					MessageUtil.shortArg(sender, 3, args);
					MessageUtil.commandInfo(sender, label, "give <개체> <효과> [지속 시간(초)] [강도] [표시 유형] [명령어 출력 숨김 여부]");
					return failure;
				}
				if (length > 7)
				{
					MessageUtil.longArg(sender, 7, args);
					MessageUtil.commandInfo(sender, label, "give <개체> <효과> [지속 시간(초)] [강도] [표시 유형] [명령어 출력 숨김 여부]");
					return failure;
				}
				List<Entity> entities = SelectorUtil.getEntities(sender, args[1]);
				if (entities == null)
				{
					MessageUtil.commandInfo(sender, label, "give &c<개체>&p <효과> [지속 시간(초)] [강도] [표시 유형] [명령어 출력 숨김 여부]");
					return failure;
				}
				OverridePropertyBuilder builder = args[2].contains("__") ? new OverridePropertyBuilder() : null;
				boolean force = args[2].endsWith("--force");
				if (force)
				{
					args[2] = args[2].substring(0, args[2].length() - 7);
				}
				if (builder != null)
				{
					String[] split = args[2].split("__");
					String modifier = split.length > 1 ? split[split.length - 1] : "";
					if (modifier.contains("b"))
					{
						modifier = modifier.replace("b", "");
						builder.nonBuffFreezeable();
					}
					if (modifier.contains("B"))
					{
						modifier = modifier.replace("B", "");
						builder.buffFreezeable();
					}
					if (modifier.contains("k"))
					{
						modifier = modifier.replace("k", "");
						builder.keepOnDeath();
					}
					if (modifier.contains("K"))
					{
						modifier = modifier.replace("K", "");
						builder.removeOnDeath();
					}
					if (modifier.contains("m"))
					{
						modifier = modifier.replace("m", "");
						builder.keepOnMilk();
					}
					if (modifier.contains("M"))
					{
						modifier = modifier.replace("M", "");
						builder.removeOnMilk();
					}
					if (modifier.contains("q"))
					{
						modifier = modifier.replace("q", "");
						builder.removeOnQuit();
					}
					if (modifier.contains("Q"))
					{
						modifier = modifier.replace("Q", "");
						builder.keepOnQuit();
					}
					if (modifier.contains("r"))
					{
						modifier = modifier.replace("r", "");
						builder.nonRemoveable();
					}
					if (modifier.contains("R"))
					{
						modifier = modifier.replace("R", "");
						builder.removeable();
					}
					if (modifier.contains("d"))
					{
						modifier = modifier.replace("d", "");
						builder.realDuration();
					}
					if (modifier.contains("D"))
					{
						modifier = modifier.replace("D", "");
						builder.nonRealDuration();
					}
					if (!modifier.isEmpty())
					{
						MessageUtil.wrongArg(sender, 3, args);
						MessageUtil.info(sender, "효과 수정자는 %s 형식으로 사용해야 합니다", Constant.THE_COLOR_HEX + "[효과 이름]__[수정자]");
						MessageUtil.info(sender, "대문자가 기본 바닐라와 같은 시스템을 따릅니다(소문자는 반대)");
						MessageUtil.info(sender, "예시: %s", Constant.THE_COLOR_HEX + args[2] + "__bdR");
						MessageUtil.info(sender, "%s효과 수정자 목록%s", "&m                            ", "&m                             ");
						MessageUtil.info(sender, ComponentUtil.translate("b : %s 효과의 영향을 받지 않게 됩니다",
								ComponentUtil.create(sender instanceof Player player ? player : null, CustomEffectType.BUFF_FREEZE)));
						MessageUtil.info(sender, ComponentUtil.translate("B : %s 효과의 영향을 받게 됩니다",
								ComponentUtil.create(sender instanceof Player player ? player : null, CustomEffectType.BUFF_FREEZE)));
						MessageUtil.info(sender, "d : 효과의 지속시간이 접속을 종료해도 흐릅니다");
						MessageUtil.info(sender, "D : 효과의 지속시간이 접속을 종료해도 흐르지 않습니다");
						MessageUtil.info(sender, "k : 사망해도 효과가 사라지지 않습니다");
						MessageUtil.info(sender, "K : 사망하면 효과가 사라집니다");
						MessageUtil.info(sender, "m : 우유를 마셔도 효과가 사라지지 않습니다");
						MessageUtil.info(sender, "M : 우유를 마시면 효과가 사라집니다");
						MessageUtil.info(sender, "q : 접속을 종료하거나 다른 서버로 이동하면 효과가 사라집니다");
						MessageUtil.info(sender, "Q : 접속을 종료하거나 다른 서버로 이동해도 효과가 사라지지 않습니다");
						MessageUtil.info(sender, "r : 효과 목록 GUI에서 우클릭으로 효과를 해제할 수 없게 됩니다");
						MessageUtil.info(sender, "R : 효과 목록 GUI에서 우클릭으로 효과를 해제할 수 있게 됩니다");
						MessageUtil.info(sender, Constant.SEPARATOR);
						return failure;
					}
					args[2] = args[2].substring(0, args[2].length() - split[split.length - 1].length() - 2);
				}
				CustomEffectType customEffectType;
				try
				{
					customEffectType = CustomEffectType.valueOf(args[2]);
				}
				catch (Exception e)
				{
					MessageUtil.wrongArg(sender, 3, args);
					MessageUtil.commandInfo(sender, label, "give <개체> &c<효과>&p [지속 시간(초)] [강도] [표시 유형] [명령어 출력 숨김 여부]");
					return failure;
				}
				int duration = customEffectType.getDefaultDuration(), amplifier = 0;
				DisplayType displayType = customEffectType.getDefaultDisplayType();
				boolean hideOutput = false;
				if (length >= 4)
				{
					if (args[3].equals("infinite"))
					{
						duration = -1;
					}
					else if (!args[3].equals("default"))
					{
						if (!MessageUtil.isDouble(sender, args[3], true))
						{
							MessageUtil.commandInfo(sender, label, "give <개체> <효과> &c[지속 시간(초)]&p [강도] [표시 유형] [명령어 출력 숨김 여부]");
							return failure;
						}
						double durationInput = Double.parseDouble(args[3]);
						duration = (int) (durationInput * 20);
						if (!MessageUtil.checkNumberSize(sender, durationInput, 0.05, Integer.MAX_VALUE / 20d))
						{
							MessageUtil.commandInfo(sender, label, "give <개체> <효과> &c[지속 시간(초)]&p [강도] [표시 유형] [명령어 출력 숨김 여부]");
							return failure;
						}
					}
				}
				if (length >= 5)
				{
					if (args[4].equals("max"))
					{
						amplifier = customEffectType.getMaxAmplifier();
					}
					else if (!MessageUtil.isInteger(sender, args[4], true))
					{
						MessageUtil.commandInfo(sender, label, "give <개체> <효과> [지속 시간(초)] &c[강도]&p [표시 유형] [명령어 출력 숨김 여부]");
						return failure;
					}
					else
					{
						amplifier = Integer.parseInt(args[4]);
						if (!MessageUtil.checkNumberSize(sender, amplifier, 0, customEffectType.getMaxAmplifier()))
						{
							MessageUtil.commandInfo(sender, label, "give <개체> <효과> [지속 시간(초)] &c[강도]&p [표시 유형] [명령어 출력 숨김 여부]");
							return failure;
						}
					}
				}
				if (length >= 6)
				{
					try
					{
						displayType = DisplayType.valueOf(args[5].toUpperCase());
					}
					catch (Exception e)
					{
						MessageUtil.wrongArg(sender, 6, args);
						MessageUtil.commandInfo(sender, label, "give <개체> <효과> [지속 시간(초)] [강도] &c[표시 유형]&p [명령어 출력 숨김 여부]");
						return failure;
					}
				}
				if (length == 7)
				{
					if (!MessageUtil.isBoolean(sender, args, 7, true))
					{
						MessageUtil.commandInfo(sender, label, "give <개체> <효과> [지속 시간(초)] [강도] [표시 유형] &c[명령어 출력 숨김 여부]");
						return failure;
					}
					hideOutput = Boolean.parseBoolean(args[6]);
				}
				return giveEffect(sender, entities, customEffectType, duration, amplifier, displayType, hideOutput, force, builder) || failure;
			}
			default ->
			{
				MessageUtil.wrongArg(sender, 1, args);
				MessageUtil.commandInfo(sender, label, "&c<give|clear|query>&p ...");
				return failure;
			}
		}
	}

	@Override
	@NotNull
	public List<Completion> completion(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args,
			@NotNull Location location)
	{
		int length = args.length;
		if (length == 1)
		{
			return CommandTabUtil.tabCompleterList(args, "<인수>", false, Completion.completion("give", ComponentUtil.translate("대상에게 효과를 부여합니다")),
					Completion.completion("clear", ComponentUtil.translate("대상으로부터 특정 효과 혹은 모든 효과를 제거합니다")),
					Completion.completion("query", ComponentUtil.translate("자신의 적용 중인 효과를 GUI로 참조하거나 다른 대상의 효과를 참조합니다")));
		}
		switch (args[0])
		{
			case "query" ->
			{
				if (length == 2)
				{
					return CommandTabUtil.tabCompleterEntity(sender, args, "[개체]");
				}
			}
			case "clear" ->
			{
				if (length == 2)
				{
					return CommandTabUtil.tabCompleterEntity(sender, args, "[개체]");
				}
				if (length == 3)
				{
					return CommandTabUtil.tabCompleterList(args, CustomEffectType.getEffectTypeMap(), "<효과>");
				}
				if (length == 4)
				{
					return CommandTabUtil.tabCompleterBoolean(args, "[명령어 출력 숨김 여부]");
				}
			}
			case "give" ->
			{
				if (length == 2)
				{
					return CommandTabUtil.tabCompleterEntity(sender, args, "<개체>");
				}
				boolean force = args[2].endsWith("--force");
				if (force)
				{
					args[2] = args[2].substring(0, args[2].length() - 7);
				}
				boolean useModifier = args[2].endsWith("__");
				String[] split = args[2].split("__");
				args[2] = split[0];
				String effect = args[2];
				if (length == 3)
				{
					try
					{
						CustomEffectType customEffectType = CustomEffectType.valueOf(effect);
						if (force)
						{
							Component component = ComponentUtil.translate("%s 효과를 강제로 적용합니다", ComponentUtil.translate(customEffectType.translationKey()));
							return CommandTabUtil.completions(component);
						}
						else
						{
							List<Completion> list = new ArrayList<>(CommandTabUtil.tabCompleterList(args, CustomEffectType.getEffectTypeMap(), "<효과>"));
							list.add(Completion.completion(args[2] + (split.length > 1 ? ("__" + split[split.length - 1]) : "") + "--force",
									ComponentUtil.translate("%s 효과를 강제로 적용합니다", ComponentUtil.translate(customEffectType.translationKey()))));
							if (useModifier)
							{
								list.add(Completion.completion(args[2] + "__b", ComponentUtil.translate("%s 효과의 영향을 받지 않게 됩니다", CustomEffectType.BUFF_FREEZE)));
								list.add(Completion.completion(args[2] + "__B", ComponentUtil.translate("%s 효과의 영향을 받게 됩니다", CustomEffectType.BUFF_FREEZE)));
								list.add(Completion.completion(args[2] + "__d", ComponentUtil.translate("효과의 지속시간이 접속을 종료해도 흐릅니다")));
								list.add(Completion.completion(args[2] + "__D", ComponentUtil.translate("효과의 지속시간이 접속을 종료해도 흐르지 않습니다")));
								list.add(Completion.completion(args[2] + "__k", ComponentUtil.translate("사망해도 효과가 사라지지 않습니다")));
								list.add(Completion.completion(args[2] + "__K", ComponentUtil.translate("사망하면 효과가 사라집니다")));
								list.add(Completion.completion(args[2] + "__m", ComponentUtil.translate("우유를 마셔도 효과가 사라지지 않습니다")));
								list.add(Completion.completion(args[2] + "__M", ComponentUtil.translate("우유를 마시면 효과가 사라집니다")));
								list.add(Completion.completion(args[2] + "__q", ComponentUtil.translate("접속을 종료하거나 다른 서버로 이동하면 효과가 사라집니다")));
								list.add(Completion.completion(args[2] + "__Q", ComponentUtil.translate("접속을 종료하거나 다른 서버로 이동해도 효과가 사라지지 않습니다")));
								list.add(Completion.completion(args[2] + "__r", ComponentUtil.translate("효과 목록 GUI에서 우클릭으로 효과를 헤제할 수 없게 됩니다")));
								list.add(Completion.completion(args[2] + "__R", ComponentUtil.translate("효과 목록 GUI에서 우클릭으로 효과를 헤제할 수 있게 됩니다")));
							}
							else
							{
								list.add(Completion.completion(args[2] + "__", ComponentUtil.translate("효과 수정자를 사용합니다")));
							}
							return list;
						}
					}
					catch (Exception ignored)
					{

					}
					return CommandTabUtil.tabCompleterList(args, CustomEffectType.getEffectTypeMap(), "<효과>");
				}
				CustomEffectType effectType;
				try
				{
					effectType = CustomEffectType.valueOf(effect);
				}
				catch (Exception e)
				{
					Component component = ComponentUtil.translate("%s은(는) 잘못되거나 알 수 없는 효과입니다", args[2]);
					return CommandTabUtil.completions(component);
				}
				if (length == 4)
				{
					int defaultDuration = effectType.getDefaultDuration();
					List<Completion> list = CommandTabUtil.tabCompleterDoubleRadius(args, 0.05, Integer.MAX_VALUE / 20d, "[지속 시간(초)]");
					String time = Method.timeFormatMilli(defaultDuration * 50L, true, 2);
					List<Completion> list1 = CommandTabUtil.tabCompleterList(args, "[인수]", false, Completion.completion("infinite", ComponentUtil.translate("무제한 지속시간")),
							Completion.completion("default", ComponentUtil.translate("기본 지속시간(%s)", (defaultDuration == -1 ? "무제한" : time))));
					return CommandTabUtil.sortError(list, list1);
				}
				if (length == 5)
				{
					int maxAmplifier = effectType.getMaxAmplifier();
					List<Completion> list = new ArrayList<>(CommandTabUtil.tabCompleterIntegerRadius(args, 0, maxAmplifier, "[농도 레벨]"));
					List<Completion> list1 = CommandTabUtil.tabCompleterList(args, "[인수]", false,
							Completion.completion("max", ComponentUtil.translate("최대 농도 레벨(%s)", maxAmplifier)));
					return CommandTabUtil.sortError(list, list1);
				}
				if (length == 6)
				{
					List<Object> list = new ArrayList<>(Method.enumToList(DisplayType.values()));
					list.add(effectType.getDefaultDisplayType().toString().toLowerCase() + "(기본값)");
					return CommandTabUtil.tabCompleterList(args, list, "[표시 유형]");
				}
				if (length == 7)
				{
					return CommandTabUtil.tabCompleterBoolean(args, "[명령어 출력 숨김 여부]");
				}
			}
		}
		return CommandTabUtil.ARGS_LONG;
	}

	private boolean giveEffect(@NotNull CommandSender sender, @NotNull List<Entity> entities, @NotNull CustomEffectType customEffectType, int duration,
			int amplifier, @NotNull DisplayType displayType, boolean hideOutput, boolean force, @Nullable OverridePropertyBuilder builder)
	{
		CustomEffect customEffect = new CustomEffect(customEffectType, duration, amplifier, displayType, builder);
		List<Entity> successEntities = new ArrayList<>();
		for (Entity entity : entities)
		{
			if (CustomEffectManager.addEffect(entity, customEffect, ApplyReason.COMMAND, force))
			{
				successEntities.add(entity);
			}
		}
		boolean successEntitiesIsEmpty = successEntities.isEmpty();
		if (!hideOutput)
		{
			List<Entity> failureEntities = new ArrayList<>(entities);
			failureEntities.removeAll(successEntities);
			if (!failureEntities.isEmpty())
			{
				MessageUtil.sendWarnOrError(successEntitiesIsEmpty, sender,
						ComponentUtil.translate("%s에게 %s 효과를 적용할 수 없습니다 (대상이 효과에 내성이 있거나 더 긴 지속 시간을 가지고 있거나 효과를 받을 수 없는 상태입니다)", failureEntities, customEffect));
			}
			if (!successEntitiesIsEmpty)
			{
				MessageUtil.info(sender, "commands.effect.give.success.single", customEffect, successEntities);
				MessageUtil.sendAdminMessage(sender, "commands.effect.give.success.single", customEffect, successEntities);
				List<Audience> infoTarget = new ArrayList<>(successEntities);
				infoTarget.remove(sender);
				MessageUtil.info(infoTarget, "%s이(가) 당신에게 %s 효과를 적용했습니다", sender, customEffect);
			}
		}
		return !successEntitiesIsEmpty;
	}

	private boolean clearEffect(@NotNull CommandSender sender, @NotNull List<Entity> entities, @Nullable CustomEffectType customEffectType, boolean hideOutput)
	{
		boolean all = customEffectType == null;
		List<Entity> successEntities = new ArrayList<>();
		for (Entity entity : entities)
		{
			if (all)
			{
				if (CustomEffectManager.clearEffects(entity))
				{
					successEntities.add(entity);
				}
			}
			else
			{
				if (CustomEffectManager.removeEffect(entity, customEffectType))
				{
					successEntities.add(entity);
				}
			}
		}
		boolean successEntitiesIsEmpty = successEntities.isEmpty();
		if (!hideOutput)
		{
			List<Entity> failureEntities = new ArrayList<>(entities);
			failureEntities.removeAll(successEntities);
			if (!failureEntities.isEmpty())
			{
				if (all)
				{
					MessageUtil.sendWarnOrError(successEntitiesIsEmpty, sender, "%s에게 제거할 효과가 없습니다", failureEntities);
				}
				else
				{
					MessageUtil.sendWarnOrError(successEntitiesIsEmpty, sender, "%s에게 %s 효과가 없습니다", failureEntities, customEffectType);
				}
			}
			if (!successEntitiesIsEmpty)
			{
				List<Audience> infoTarget = new ArrayList<>(successEntities);
				infoTarget.remove(sender);
				if (all)
				{
					MessageUtil.info(sender, "commands.effect.clear.everything.success.single", successEntities);
					MessageUtil.sendAdminMessage(sender, "commands.effect.clear.everything.success.single", successEntities);
					MessageUtil.info(infoTarget, "%s이(가) 당신의 모든 효과를 제거했습니다", sender);
				}
				else
				{
					MessageUtil.info(sender, "commands.effect.clear.specific.success.single", customEffectType, successEntities);
					MessageUtil.sendAdminMessage(sender, "commands.effect.clear.specific.success.single", customEffectType, successEntities);
					MessageUtil.info(infoTarget, "%s이(가) 당신의 %s 효과를 제거했습니다", sender, customEffectType);
				}
			}
		}
		return !successEntitiesIsEmpty;
	}

	private void queryEffect(@NotNull CommandSender sender, @NotNull Entity entity, boolean gui)
	{
		if (sender instanceof Player player && gui)
		{
			CustomEffectGUI.openGUI(player, true);
			return;
		}
		boolean hasVanilla =
				entity instanceof LivingEntity livingEntity && !livingEntity.getActivePotionEffects().isEmpty(), hasCustom = CustomEffectManager.hasEffects(entity);

		if (hasVanilla)
		{
			LivingEntity livingEntity = (LivingEntity) entity;
			Collection<PotionEffect> potionEffects = livingEntity.getActivePotionEffects();
			MessageUtil.sendMessage(sender, Prefix.INFO_CUSTOM_EFFECT, ComponentUtil.translate("%s은(는) %s개의 포션 효과를 가지고 있습니다: %s", entity, potionEffects.size(),
					CustomEffectManager.getVanillaDisplay(entity, potionEffects, true)));
		}
		else
		{
			MessageUtil.sendMessage(sender, Prefix.INFO_CUSTOM_EFFECT, ComponentUtil.translate("%s은(는) 포션 효과를 가지고 있지 않습니다", entity));
		}
		if (hasCustom)
		{
			List<CustomEffect> customEffects = CustomEffectManager.getEffects(entity);
			MessageUtil.sendMessage(sender, Prefix.INFO_CUSTOM_EFFECT, ComponentUtil.translate("%s은(는) %s개의 커스텀 효과를 가지고 있습니다: %s", entity, customEffects.size(),
					CustomEffectManager.getDisplay(entity, customEffects, true)));
		}
		else
		{
			MessageUtil.sendMessage(sender, Prefix.INFO_CUSTOM_EFFECT, ComponentUtil.translate("%s은(는) 커스텀 효과를 가지고 있지 않습니다", entity));
		}
	}

}















