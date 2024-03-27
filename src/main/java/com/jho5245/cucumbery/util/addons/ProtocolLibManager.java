package com.jho5245.cucumbery.util.addons;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.google.common.collect.Lists;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.children.group.LocationCustomEffectImple;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectTypeCustomMining;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectTypeMinecraft;
import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.itemlore.ItemLore.RemoveFlag;
import com.jho5245.cucumbery.util.itemlore.ItemLoreView;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.Method2;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.component.util.ItemNameUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Constant.CucumberyHideFlag;
import com.jho5245.cucumbery.util.storage.data.CustomMaterial;
import com.jho5245.cucumbery.util.storage.data.Variable;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public class ProtocolLibManager
{
	public static void manage()
	{
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.HIGH, Server.WORLD_PARTICLES)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
				{
					return;
				}
				PacketContainer packet = event.getPacket();
				if (packet.getType() == PacketType.Play.Server.WORLD_PARTICLES)
				{
					StructureModifier<Object> modifier = packet.getModifier();
					int particleSize = (int) modifier.read(7);
					@SuppressWarnings("all") StructureModifier<WrappedParticle> particles = packet.getNewParticles();
					for (WrappedParticle<?> particle : particles.getValues())
					{
						int configSize = Cucumbery.config.getInt("use-damage-indicator.protocollib.max-vanilla-damage-indicator-particles");
						if (configSize < 0)
						{
							return;
						}
						if (particle.getParticle() == Particle.DAMAGE_INDICATOR && particleSize > configSize)
						{
							event.setCancelled(true);
							if (configSize == 0)
							{
								return;
							}
							double x = (double) modifier.read(0), y = (double) modifier.read(1), z = (double) modifier.read(2);
							float offsetX = (float) modifier.read(3), offSetY = (float) modifier.read(4), offsetZ = (float) modifier.read(5);
							float speed = (float) modifier.read(6);
							boolean force = (boolean) modifier.read(8);
							Player player = event.getPlayer();
							World world = player.getWorld();
							world.spawnParticle(Particle.DAMAGE_INDICATOR, x, y, z, configSize, offsetX, offSetY, offsetZ, speed, null, force);
							break;
						}
					}
				}
			}
		});
		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.NORMAL, Server.ENTITY_EFFECT)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
				{
					return;
				}
				PacketContainer packet = event.getPacket();
				if (packet.getType() == Server.ENTITY_EFFECT)
				{
					int id = packet.getIntegers().read(0);
					Entity entity = Method2.getEntityById(id);
					StructureModifier<PotionEffectType> effectTypes = packet.getEffectTypes();
					PotionEffectType potionEffectType = effectTypes.read(0);
					StructureModifier<Object> modifier = packet.getModifier();
					@SuppressWarnings("unused") byte amplifier = (byte) modifier.read(2);
					int duration = (int) modifier.read(3);
					if (entity instanceof Player player)
					{
						if (CustomEffectManager.hasEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE))
						{
							if (potionEffectType.equals(PotionEffectType.FAST_DIGGING) && duration < 3 && amplifier == 0)
							{
								modifier.write(3, -1);
								event.setPacket(packet);
							}
							if (potionEffectType.equals(PotionEffectType.SLOW_DIGGING) && duration < 3 && amplifier == 0)
							{
								modifier.write(3, -1);
								event.setPacket(packet);
							}
							if (potionEffectType.equals(PotionEffectType.SLOW_DIGGING))
							{
								modifier.write(2, (byte) 127);
								event.setPacket(packet);
							}
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.SPEED) && potionEffectType.equals(PotionEffectType.SPEED))
						{
							modifier.write(3, CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.SPEED).getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.SLOWNESS) && potionEffectType.equals(PotionEffectType.SLOW))
						{
							modifier.write(3, CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.SLOWNESS).getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.HASTE) && potionEffectType.equals(PotionEffectType.FAST_DIGGING))
						{
							modifier.write(3, CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.HASTE).getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.MINING_FATIGUE) && potionEffectType.equals(PotionEffectType.SLOW_DIGGING))
						{
							modifier.write(3, CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.MINING_FATIGUE).getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.NIGHT_VISION) && potionEffectType.equals(PotionEffectType.NIGHT_VISION))
						{
							modifier.write(3, CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.NIGHT_VISION).getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.DYNAMIC_LIGHT) && potionEffectType.equals(PotionEffectType.NIGHT_VISION))
						{
							PlayerInventory inventory = player.getInventory();
							Material mainHand = inventory.getItemInMainHand().getType(), offHand = inventory.getItemInOffHand().getType();
							if (Constant.OPTIFINE_DYNAMIC_LIGHT_ITEMS.contains(mainHand) || Constant.OPTIFINE_DYNAMIC_LIGHT_ITEMS.contains(offHand))
							{
								modifier.write(3, Integer.MAX_VALUE);
								event.setPacket(packet);
							}
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectType.NIGHT_VISION_SPECTATOR) && potionEffectType.equals(PotionEffectType.NIGHT_VISION)
								&& player.getSpectatorTarget() != null)
						{
							modifier.write(3, Integer.MAX_VALUE);
							event.setPacket(packet);
						}

						PlayerInventory playerInventory = player.getInventory();
						ItemStack helmet = playerInventory.getHelmet(), chestplate = playerInventory.getChestplate(), leggings = playerInventory.getLeggings(), boots = playerInventory.getBoots();
						CustomMaterial helmetType = CustomMaterial.itemStackOf(helmet), chestplateType = CustomMaterial.itemStackOf(
								chestplate), leggingsType = CustomMaterial.itemStackOf(leggings), bootsType = CustomMaterial.itemStackOf(boots);

						if ((helmetType == CustomMaterial.MINER_HELMET || helmetType == CustomMaterial.MINDAS_HELMET) && potionEffectType.equals(
								PotionEffectType.NIGHT_VISION) && duration < 3)
						{
							modifier.write(3, -1);
							event.setPacket(packet);
						}

						if (helmetType == CustomMaterial.MINER_HELMET && chestplateType == CustomMaterial.MINER_CHESTPLATE && leggingsType == CustomMaterial.MINER_LEGGINGS
								&& bootsType == CustomMaterial.MINER_BOOTS && potionEffectType.equals(PotionEffectType.FAST_DIGGING) && duration < 5)
						{
							packet.getModifier().write(3, -1);
							event.setPacket(packet);
						}

						if (helmetType == CustomMaterial.FROG_HELMET && chestplateType == CustomMaterial.FROG_CHESTPLATE && leggingsType == CustomMaterial.FROG_LEGGINGS
								&& bootsType == CustomMaterial.FROG_BOOTS && potionEffectType.equals(PotionEffectType.JUMP) && duration < 3)
						{
							packet.getModifier().write(3, -1);
							event.setPacket(packet);
						}
					}
				}
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.NORMAL, Server.BLOCK_CHANGE)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
				{
					return;
				}
				PacketContainer packet = event.getPacket();
				if (packet.getType() == Server.BLOCK_CHANGE)
				{
					Player player = event.getPlayer();
					StructureModifier<Object> modifier = packet.getModifier();
					int[] ary = getBlockPositionOf(modifier.read(0));
					Location location = new Location(player.getWorld(), ary[0], ary[1], ary[2]);
					if (Variable.customMiningCooldown.containsKey(location) || Variable.customMiningExtraBlocks.containsKey(location) || Variable.fakeBlocks.containsKey(
							location))
					{
						Material type = packet.getBlockData().read(0).getType();
						Material locationType = location.getBlock().getType();
						if (type == location.getBlock().getType() && !Variable.customMiningMode2BlockData.containsKey(location) || (
								Variable.customMiningMode2BlockData.containsKey(location) && locationType != Variable.customMiningMode2BlockData.get(location).getMaterial()))
						{
							if ((!Variable.fakeBlocks.containsKey(location) || Variable.fakeBlocks.get(location).getMaterial() != type) && type != Material.AIR)
							{
								event.setCancelled(true);
							}
						}
					}
					if (!event.isCancelled())
					{
						if (!Variable.customMiningCooldown.containsKey(location) && !Variable.customMiningExtraBlocks.containsKey(location)
								&& !Variable.customMiningMode2BlockData.containsKey(location) && Variable.fakeBlocks.containsKey(location))
						{
							Material type = packet.getBlockData().read(0).getType();
							if (Variable.fakeBlocks.get(location).getMaterial() != Material.AIR && type == Material.AIR)
							{
								event.setCancelled(true);
							}
						}
					}
				}
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.NORMAL, Client.BLOCK_DIG)
		{
			@Override
			public void onPacketReceiving(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
				{
					return;
				}
				PacketContainer packet = event.getPacket();
				Player player = event.getPlayer();
				if (player.isSneaking() && CustomEffectManager.hasEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE))
				{
					PlayerDigType playerDigType = packet.getPlayerDigTypes().read(0);
					if (playerDigType == PlayerDigType.START_DESTROY_BLOCK)
					{
						if (CustomEffectManager.hasEffect(player, CustomEffectType.CURSE_OF_CREATIVITY) || CustomEffectManager.hasEffect(player,
								CustomEffectType.CURSE_OF_CREATIVITY_BREAK))
						{
							event.setCancelled(true);
							return;
						}
						int[] pos = getBlockPositionOf(packet.getModifier().read(0));
						Location location = new Location(player.getWorld(), pos[0], pos[1], pos[2]);
						if (!Variable.customMiningCooldown.containsKey(location) || Variable.customMiningExtraBlocks.containsKey(location))
						{
							// 가끔 hasEffect가 true인데 getEffect가 null을 반환함 왜?
							try
							{
								CustomEffectManager.addEffect(player, new LocationCustomEffectImple(CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_PROGRESS, location));
							}
							catch (IllegalStateException ignored)
							{

							}
							catch (Throwable t)
							{
								Cucumbery.getPlugin().getLogger().warning(t.getMessage());
							}
						}
					}
				}
			}
		});

		protocolManager.addPacketListener(
				new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.NORMAL, Server.WINDOW_ITEMS, Server.OPEN_WINDOW_MERCHANT, Server.SET_SLOT)
				{
					@Override
					public void onPacketSending(PacketEvent event)
					{
						if (!Cucumbery.using_ProtocolLib)
						{
							return;
						}
						PacketContainer packet = event.getPacket();
						Player player = event.getPlayer();
						if (packet.getType() == Server.WINDOW_ITEMS)
						{
							StructureModifier<List<ItemStack>> modifier = packet.getItemListModifier();
							modifier.write(0, setItemLore(packet.getType(), modifier.read(0), player));
						}
						else if (packet.getType() == Server.OPEN_WINDOW_MERCHANT)
						{
							StructureModifier<List<MerchantRecipe>> modifier = packet.getMerchantRecipeLists();
							List<MerchantRecipe> merchantRecipeList = modifier.read(0), newMerchantRecipeList = new ArrayList<>(merchantRecipeList.size());
							for (MerchantRecipe recipe : merchantRecipeList)
							{
								MerchantRecipe newRecipe = new MerchantRecipe(setItemLore(packet.getType(), recipe.getResult(), player), recipe.getUses(), recipe.getMaxUses(),
										recipe.hasExperienceReward(), recipe.getVillagerExperience(), recipe.getPriceMultiplier());
								newRecipe.setIngredients(setItemLore(packet.getType(), recipe.getIngredients(), player));
								newMerchantRecipeList.add(newRecipe);
							}
							modifier.write(0, newMerchantRecipeList);
						}
						else if (packet.getType() == Server.SET_SLOT)
						{
							StructureModifier<ItemStack> modifier = packet.getItemModifier();
							modifier.write(0, setItemLore(packet.getType(), modifier.read(0), player));
						}
					}
				});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.NORMAL, Server.SPAWN_ENTITY)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
				{
					return;
				}
				PacketContainer packetContainer = event.getPacket();
				Player player = event.getPlayer();
				Entity entity = packetContainer.getEntityModifier(player.getWorld()).read(0);
				if (entity instanceof Item item)
				{
					PacketContainer container = protocolManager.createPacket(Server.ENTITY_METADATA);
					StructureModifier<List<WrappedDataValue>> structureModifier = container.getDataValueCollectionModifier();
					List<WrappedDataValue> values = Lists.newArrayList(new WrappedDataValue(8, WrappedDataWatcher.Registry.getItemStackSerializer(false),
							MinecraftReflection.getMinecraftItemStack(setItemLore(event.getPacketType(), item.getItemStack(), player))));
					structureModifier.write(0, values);
					container.getIntegers().write(0, item.getEntityId());
					protocolManager.sendServerPacket(player, container);
					Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> protocolManager.sendServerPacket(player, container), 2L);
				}
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(
				Cucumbery.getPlugin(),
				ListenerPriority.NORMAL,
				PacketType.Play.Client.CHAT
		) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				String message = packet.getStrings().read(0);

				if (message.contains("a")) {
					event.setCancelled(true);
					event.getPlayer().sendMessage("Bad manners!");
				}
			}
		});
	}

	private static List<ItemStack> setItemLore(PacketType packetType, List<ItemStack> itemStacks, Player player)
	{
		if (itemStacks.isEmpty())
			return Collections.emptyList();
		return itemStacks.stream().map(itemStack -> setItemLore(packetType, itemStack, player)).collect(Collectors.toList());
	}

	@NotNull
	public static ItemStack setItemLore(PacketType packetType, final @NotNull ItemStack itemStack, final @NotNull Player player)
	{
		if (!ItemStackUtil.itemExists(itemStack))
		{
			return itemStack;
		}

		boolean isCreative = !UserData.SHOW_ITEM_LORE_IN_CREATIVE_MODE.getBoolean(player) && player.getGameMode() == GameMode.CREATIVE, showItemLore =
				!isCreative && UserData.SHOW_ITEM_LORE.getBoolean(player), showEnchantGlints = UserData.SHOW_ENCHANTED_ITEM_GLINTS.getBoolean(
				player), forceShowEnchants = UserData.EVENT_EXCEPTION_ACCESS.getBoolean(player);

		NBTItem nbtItem = new NBTItem(itemStack.clone());
		@Nullable NBTCompound itemTag = nbtItem.getCompound(CucumberyTag.KEY_MAIN);
		NBTList<String> hideFlags = NBTAPI.getStringList(itemTag, CucumberyTag.HIDE_FLAGS_KEY);
		boolean hideFlagsTagExists = hideFlags != null;
		ItemStack clone = itemStack.clone();
		Set<ItemFlag> itemFlags = clone.getItemMeta().getItemFlags();

		ItemLore.setItemLore(clone, false, ItemLoreView.of(player));
		if (!showItemLore)
		{
			ItemLore.removeItemLore(clone, RemoveFlag.create().removeItemFlags().removeUUID());
		}

		ItemMeta itemMeta = clone.getItemMeta();
		boolean showEnchants =
				!(hideFlagsTagExists && NBTAPI.arrayContainsValue(hideFlags, CucumberyHideFlag.ENCHANTS)) && !itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
		showEnchantGlints = showEnchantGlints && !(hideFlagsTagExists && NBTAPI.arrayContainsValue(hideFlags, CucumberyHideFlag.ENCHANTS_GLINTS));

		if (hideFlagsTagExists)
		{
			if (NBTAPI.arrayContainsValue(hideFlags, CucumberyHideFlag.EXISTENCE))
			{
				return ItemStackUtil.HIDDEN_ITEM;
			}
			if (packetType == Server.OPEN_WINDOW_MERCHANT && NBTAPI.arrayContainsValue(hideFlags, CucumberyHideFlag.EXISTENCE_ON_VILLAGER_TRADE))
			{
				return ItemStackUtil.HIDDEN_ITEM;
			}
		}

		if (!isCreative)
		{
			itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[itemFlags.size()]));
		}

		List<Component> lore = itemMeta.lore();
		if (lore == null)
			lore = new ArrayList<>();

		if (!isCreative && !lore.isEmpty() && lore.get(0) instanceof TranslatableComponent translatableComponent && translatableComponent.key().isEmpty())
		{
			lore.set(0, Component.empty());
		}

		// 크리에이티브 모드에서는 아이템의 마법을 제거하면 실제로 아이템의 마법이 제거되므로 비활성화
		if (!isCreative && itemMeta.hasEnchants())
		{
			if (showEnchantGlints)
			{
				if (!forceShowEnchants && !showEnchants)
				{
					itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				}
				else
				{
					if (forceShowEnchants && !showEnchants)
					{
						lore.add(ComponentUtil.translate("&8관리자 권한으로 숨겨진 마법을 참조합니다"));
						itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
					}
				}
			}
			else
			{
				if (!showItemLore && (forceShowEnchants || showEnchants))
				{
					if (forceShowEnchants && !showEnchants)
					{
						lore.add(ComponentUtil.translate("&8관리자 권한으로 숨겨진 마법을 참조합니다"));
					}
					int loop = 0;
					Map<Enchantment, Integer> enchants = itemMeta.getEnchants();
					for (Enchantment enchant : enchants.keySet())
					{
						lore.add(loop, enchant.displayName(itemMeta.getEnchantLevel(enchant)).decoration(TextDecoration.ITALIC, State.FALSE));
						loop++;
					}
				}
				itemMeta.removeEnchantments();
			}
		}

		// 크리에이티브 모드에서는 마법이 부여된 책의 반짝임을 제거하면 실제로 아이템의 마법이 제거되므로 비활성화
		if (!isCreative && itemMeta instanceof EnchantmentStorageMeta storageMeta && storageMeta.hasStoredEnchants())
		{
			if (!forceShowEnchants && !showEnchants)
			{
				itemMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
			}
			else
			{
				if (forceShowEnchants && !showEnchants)
				{
					lore.add(ComponentUtil.translate("&8관리자 권한으로 숨겨진 마법을 참조합니다"));
					itemMeta.removeItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
				}
			}
		}

		boolean isStorageMeta = false;
		// 크리에이티브 모드에서는 발광 아이템을 대체하면 실제로 아이템이 바뀌므로 비활성화
		if (!isCreative && !showEnchantGlints)
		{
			switch (clone.getType())
			{
				case ENCHANTED_GOLDEN_APPLE, ENCHANTED_BOOK, END_CRYSTAL, NETHER_STAR, EXPERIENCE_BOTTLE ->
				{
					isStorageMeta = clone.getType() == Material.ENCHANTED_BOOK;
					Material type = switch (clone.getType())
					{
						case ENCHANTED_BOOK -> Material.BOOK;
						case ENCHANTED_GOLDEN_APPLE -> Material.GOLDEN_APPLE;
						case END_CRYSTAL -> Material.PURPLE_STAINED_GLASS_PANE;
						case NETHER_STAR -> Material.SUGAR;
						case EXPERIENCE_BOTTLE -> Material.LIME_CANDLE;
						default -> clone.getType();
					};
					clone.setType(type);
					final Component displayName = itemMeta.displayName();
					if (displayName == null)
					{
						itemMeta.displayName(ItemNameUtil.itemName(itemStack.getType()));
					}
					else if (displayName.color() == null)
					{
						ItemStack clone2 = clone.clone();
						clone2.setType(itemStack.getType());
						itemMeta.displayName(displayName.color(ItemNameUtil.itemName(clone2).color()));
					}
					if (itemMeta instanceof EnchantmentStorageMeta storageMeta && !showItemLore && showEnchants)
					{
						int loop = 0;
						Map<Enchantment, Integer> enchants = storageMeta.getStoredEnchants();
						for (Enchantment enchant : new ArrayList<>(enchants.keySet()))
						{
							lore.add(loop, enchant.displayName(storageMeta.getStoredEnchantLevel(enchant)).decoration(TextDecoration.ITALIC, State.FALSE));
							loop++;
						}
					}
				}
			}
		}

		if (!isCreative && showItemLore && !new NBTItem(itemStack).getKeys().isEmpty())
		{
			itemMeta.addItemFlags(ItemFlag.values());
		}

		if (!isCreative)
		{
			itemMeta.lore(lore.isEmpty() ? null : lore);
		}

		CustomMaterial customMaterial = CustomMaterial.itemStackOf(clone);
		if (customMaterial != null && itemMeta.displayName() == null)
		{
			itemMeta.displayName(customMaterial.getDisplayName());
		}

		if (!isCreative && customMaterial != null)
		{
			switch (customMaterial)
			{
				case BRICK_THROWABLE -> clone.setType(Material.BRICK);
				case NETHER_BRICK_THROWABLE -> clone.setType(Material.NETHER_BRICK);
				case COPPER_INGOT_THROWABLE -> clone.setType(Material.COPPER_INGOT);
				case IRON_INGOT_THROWABLE -> clone.setType(Material.IRON_INGOT);
				case GOLD_INGOT_THROWABLE -> clone.setType(Material.GOLD_INGOT);
				case NETHERITE_INGOT_THROWABLE -> clone.setType(Material.NETHERITE_INGOT);
			}
		}

		if (!isCreative)
		{
			clone.setItemMeta(itemMeta);
		}

		if (!isCreative && isStorageMeta)
		{
			ItemStack temp = new ItemStack(Material.ENCHANTED_BOOK);
			temp.setItemMeta(itemMeta);
			NBTItem tempNBTItem = new NBTItem(temp);
			tempNBTItem.removeKey("StoredEnchantments");
			new NBTItem(clone, true).mergeCompound(tempNBTItem);
		}

		if (!isCreative && CustomEffectManager.hasEffect(player, CustomEffectType.THE_CHAOS_INVENTORY))
		{
			List<Material> materials = new ArrayList<>(Arrays.asList(Material.values()));
			materials.removeIf(material -> material.isAir() || !material.isItem() || !material.isEnabledByFeature(player.getWorld()));
			clone.setType(materials.get((int) (Math.random() * materials.size())));
		}

		// 혼벤토리 효과로 아이템이 공기가 될 경우
		if (!ItemStackUtil.itemExists(clone))
		{
			return clone;
		}

		nbtItem = new NBTItem(clone, true);
		if (nbtItem.hasTag("Enchantments") && nbtItem.getCompoundList("Enchantments").isEmpty())
		{
			nbtItem.removeKey("Enchantments");
		}
		if (!player.hasPermission("asdf") && player.getGameMode() != GameMode.CREATIVE)
		{
			for (String key : nbtItem.getKeys())
			{
				switch (key)
				{
					case "display", "Lore", "Enchantments", "Damage", "HideFlags", "CustomModelData", "SkullOwner", "Potion",
							"pages", "author", "title", "CustomPotionColor", "StoredEnchantments", "AttributeModifiers", "Unbreakable", "CanPlaceOn", "CanDestroy", "BlockEntityTag" ->
					{
					}
					default -> nbtItem.removeKey(key);
				}
			}
		}

		return clone;
	}

	public static int[] getBlockPositionOf(@NotNull Object object)
	{
		String pos = object.toString().replace("}", "");
		String[] split = pos.split(", ");
		int x = Integer.parseInt(split[0].split("x=")[1]);
		int y = Integer.parseInt(split[1].split("y=")[1]);
		int z = Integer.parseInt(split[2].split("z=")[1]);
		return new int[] {
				x,
				y,
				z
		};
	}

	@NotNull
	public static Object getBlockPosition(int x, int y, int z)
	{
		try
		{
			Class<?> clazz = Class.forName("net.minecraft.core.BlockPosition");
			Constructor<?> constructor = clazz.getConstructors()[4];
			return constructor.newInstance(x, y, z);
		}
		catch (Exception e)
		{
			Cucumbery.getPlugin().getLogger().warning(e.getMessage());
		}
		throw new IllegalStateException();
	}

	@SuppressWarnings("unused")
	@NotNull
	public static Object getBlockPosition(@NotNull Location location)
	{
		return getBlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
}
