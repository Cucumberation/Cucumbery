package com.jho5245.cucumbery.util.addons;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectTypeCustomMining;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectTypeMinecraft;
import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.itemlore.ItemLore.RemoveFlag;
import com.jho5245.cucumbery.util.itemlore.ItemLoreView;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.no_groups.ItemSerializer;
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
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import de.tr7zw.changeme.nbtapi.NBTType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.signature.qual.IdentifierOrPrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProtocolLibManager
{
	private static Set<UUID> firstJoins = new HashSet<>();

	public static List RECIPE_HOLDER = null;

	private static final NamespacedKey TEMP_KEY = NamespacedKey.fromString("temp_recipe", Cucumbery.getPlugin());

	public static List<Integer> RECIPE_HOLDER_INTEGERS = null;

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

/*		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.NORMAL, Client.BLOCK_DIG)
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
		});*/

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.NORMAL, ITEM_TYPES)
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
					UUID uuid = player.getUniqueId();
					// MessageUtil.broadcastDebug("Window ID: " + packet.getIntegers().read(0));
					UserData.WINDOW_ID.set(uuid, packet.getIntegers().read(0));
					packet.getItemModifier().write(0, setItemLore(packet.getType(), packet.getItemModifier().read(0), player));
					StructureModifier<List<ItemStack>> modifier = packet.getItemListModifier();
/*					List<ItemStack> itemStacks = modifier.read(0);
					List<ItemStack> modifiedItemStacks = setItemLore(packet.getType(), itemStacks, player);
					if (player.getOpenInventory().getTopInventory().getLocation() == null)
					{
						List<ItemStack> itemStacksList = modifier.read(0);
						for (int i = 0; i < player.getOpenInventory().getTopInventory().getSize(); i++)
						{
							modifiedItemStacks.set(i, itemStacksList.get(i));
						}
					}
					modifier.write(0, modifiedItemStacks);*/
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
				if (false && entity instanceof Item item)
				{
					boolean showCustomName =
							UserData.SHOW_DROPPED_ITEM_CUSTOM_NAME.getBoolean(player) && !UserData.FORCE_HIDE_DROPPED_ITEM_CUSTOM_NAME.getBoolean(player);
					Boolean shouldShowCustomName = ItemStackUtil.shouldShowCustomName(item.getItemStack());
					PacketContainer container = protocolManager.createPacket(Server.ENTITY_METADATA);
					StructureModifier<List<WrappedDataValue>> structureModifier = container.getDataValueCollectionModifier();
					List<WrappedDataValue> values = Lists.newArrayList(
							new WrappedDataValue(3, WrappedDataWatcher.Registry.get(Boolean.class), shouldShowCustomName != null ? shouldShowCustomName : showCustomName),
							new WrappedDataValue(8, WrappedDataWatcher.Registry.getItemStackSerializer(false),
									MinecraftReflection.getMinecraftItemStack(setItemLore(event.getPacketType(), item.getItemStack(), player))));
					structureModifier.write(0, values);
					container.getIntegers().write(0, item.getEntityId());
					protocolManager.sendServerPacket(player, container);
					Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> protocolManager.sendServerPacket(player, container), 0L);
				}
			}
		});

		protocolManager.addPacketListener(
				new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.HIGH, Server.RECIPE_UPDATE, Server.RECIPES, Server.ENTITY_METADATA, Server.ENTITY_EQUIPMENT)
				{
					@Override
					public void onPacketSending(PacketEvent event)
					{
						if (!Cucumbery.using_ProtocolLib)
						{
							return;
						}
						PacketContainer packet = event.getPacket();
						//MessageUtil.broadcast("sending:" + packet.getType());
						StructureModifier<Object> modifier = packet.getModifier();
						if (packet.getType() == Server.RECIPE_UPDATE)
						{
							Player player = event.getPlayer();
							UUID uuid = player.getUniqueId();
							if (!ProtocolLibManager.firstJoins.contains(uuid) && player.getDiscoveredRecipes().size() > 100)
							{
								ProtocolLibManager.firstJoins.add(uuid);
								return;
							}
							try
							{
								Class<?> recipeHolderClass = Class.forName("net.minecraft.world.item.crafting.RecipeHolder");
								Constructor<?> recipeHolderConstructor = recipeHolderClass.getDeclaredConstructors()[0];
								Method getMinecraftKeyFromRecipeHolder = recipeHolderClass.getDeclaredMethod("a");
								Method getIRecipeFromRecipeHolder = recipeHolderClass.getMethod("b");
								Method toBukkitRecipeFromRecipeHolder = recipeHolderClass.getMethod("toBukkitRecipe");

								Class<?> nonNullListClass = MinecraftReflection.getNonNullListClass();
								Method setFromNonNullListClass = nonNullListClass.getDeclaredMethod("set", Integer.TYPE, Object.class);

								Class<?> recipeItemStackClass = Class.forName("net.minecraft.world.item.crafting.RecipeItemStack");
								Method getItemStackArrayFromRecipeItemStack = recipeItemStackClass.getDeclaredMethod("a");
								Method staticRecipeItemStack = recipeItemStackClass.getDeclaredMethod("a", Stream.class);

								Class<?> shapedRecipePatternClass = Class.forName("net.minecraft.world.item.crafting.ShapedRecipePattern");
								Constructor<?> shapedRecipePatternConstructor = shapedRecipePatternClass.getConstructors()[0];

								Class<?> shapelessRecipesClass = Class.forName("net.minecraft.world.item.crafting.ShapelessRecipes");
								Constructor<?> shaplessRecipesConstructor = shapelessRecipesClass.getDeclaredConstructors()[0];
								Method getCraftingBookCategoryFromShapelessRecipes = shapelessRecipesClass.getDeclaredMethod("d");
								Method getNonNullListFromShapelessRecipes = shapelessRecipesClass.getDeclaredMethod("a");
								Method getStringFromShapelessRecipes = shapelessRecipesClass.getDeclaredMethod("c");
								Method toBukkitRecipeFromShaplessRecipes = shapelessRecipesClass.getDeclaredMethod("toBukkitRecipe", NamespacedKey.class);

								Class<?> shapedRecipesClass = Class.forName("net.minecraft.world.item.crafting.ShapedRecipes");
								Constructor<?> shapedRecipeConstructor = shapedRecipesClass.getDeclaredConstructors()[0];
								Method getCraftingBookCategoryFromShapedRecipes = shapedRecipesClass.getDeclaredMethod("d");
								Method getNonNullListFromShapedRecipes = shapedRecipesClass.getDeclaredMethod("a");
								Method toBukkitRecipeFromShapedRecipes = shapedRecipesClass.getDeclaredMethod("toBukkitRecipe", NamespacedKey.class);

								Class<?> cookingRecipeClass = Class.forName("net.minecraft.world.item.crafting.RecipeCooking");
								Method getCraftingBookCategoryFromCookingRecipeClass = cookingRecipeClass.getDeclaredMethod("f");

								Class<?> furnaceRecipeClass = Class.forName("net.minecraft.world.item.crafting.FurnaceRecipe");
								Class<?> blastingRecipeClass = Class.forName("net.minecraft.world.item.crafting.RecipeBlasting");
								Class<?> smokingRecipeClass = Class.forName("net.minecraft.world.item.crafting.RecipeSmoking");

								Class<?> stoneCuttingRecipeClass = Class.forName("net.minecraft.world.item.crafting.RecipeStonecutting");
								Constructor<?> stoneCuttingRecipeConstructor = stoneCuttingRecipeClass.getDeclaredConstructors()[0];
								Method toBukkitRecipeFromStoneCuttingRecipe = stoneCuttingRecipeClass.getDeclaredMethod("toBukkitRecipe", NamespacedKey.class);

								List recipeHolders = (List) modifier.read(0);
/*								for (Constructor<?> constructor : cookingRecipeClass.getDeclaredConstructors())
								{
									MessageUtil.broadcast(constructor);
								}
								for (Method method : cookingRecipeClass.getDeclaredMethods())
								{
									MessageUtil.broadcast(method);
								}*/
								//								if (RECIPE_HOLDER == null && !recipeHolders.isEmpty())
								//								{
								//									RECIPE_HOLDER = new ArrayList(recipeHolders);
								//								}
								//								if (RECIPE_HOLDER_INTEGERS == null)
								//								{
								//									RECIPE_HOLDER_INTEGERS = new ArrayList<>(packet.getIntLists().read(0));
								//								}
								for (int i = 0; i < recipeHolders.size(); i++)
								{
									Object recipeHolderObject = recipeHolders.get(i);
									Recipe recipe = (Recipe) toBukkitRecipeFromRecipeHolder.invoke(recipeHolderObject);
									if (recipe.getResult().getType().isAir())
									{
										continue;
									}
									if (recipe instanceof Keyed keyed && !player.hasDiscoveredRecipe(keyed.getKey()))
									{
										continue;
									}
									Object iRecipe = getIRecipeFromRecipeHolder.invoke(recipeHolderObject);
									switch (iRecipe.getClass().getSimpleName())
									{
										case "ShapelessRecipes" ->
										{
											Object nonNullList = getNonNullListFromShapelessRecipes.invoke(iRecipe);
											List recipeItemStackList = new ArrayList();
											if (nonNullList instanceof Iterable<?> iterable)
											{
												for (Object o : iterable)
												{
													recipeItemStackList.add(o);
												}
											}
											for (int j = 0; j < recipeItemStackList.size(); j++)
											{
												Object o = recipeItemStackList.get(j);
												Object minecraftItemStackArray = getItemStackArrayFromRecipeItemStack.invoke(o);
												if (minecraftItemStackArray instanceof Object[] array)
												{
													List newList = new ArrayList();
													for (Object minecraftItemStack : array)
													{
														newList.add(MinecraftReflection.getMinecraftItemStack(
																setItemLore(Server.WINDOW_ITEMS, MinecraftReflection.getBukkitItemStack(minecraftItemStack), player)));
													}
													Object recipeItemStack = staticRecipeItemStack.invoke(null, newList.stream());
													setFromNonNullListClass.invoke(nonNullList, j, recipeItemStack);
												}
											}
											Object shapelessRecipes = shaplessRecipesConstructor.newInstance(getStringFromShapelessRecipes.invoke(iRecipe),
													getCraftingBookCategoryFromShapelessRecipes.invoke(iRecipe), MinecraftReflection.getMinecraftItemStack(
															setItemLore(Server.WINDOW_ITEMS,
																	((ShapelessRecipe) toBukkitRecipeFromShaplessRecipes.invoke(iRecipe, TEMP_KEY)).getResult().clone(), player)), nonNullList);
											Object newRecipeHolderObject = recipeHolderConstructor.newInstance(getMinecraftKeyFromRecipeHolder.invoke(recipeHolderObject),
													shapelessRecipes);
											recipeHolders.set(i, newRecipeHolderObject);
										}
										case "ShapedRecipes" ->
										{
											Object nonNullList = getNonNullListFromShapedRecipes.invoke(iRecipe);
											List recipeItemStackList = new ArrayList();
											if (nonNullList instanceof Iterable<?> iterable)
											{
												for (Object o : iterable)
												{
													recipeItemStackList.add(o);
												}
											}
											for (int j = 0; j < recipeItemStackList.size(); j++)
											{
												Object o = recipeItemStackList.get(j);
												Object minecraftItemStackArray = getItemStackArrayFromRecipeItemStack.invoke(o);
												if (minecraftItemStackArray instanceof Object[] array)
												{
													List newList = new ArrayList();
													for (Object minecraftItemStack : array)
													{
														newList.add(MinecraftReflection.getMinecraftItemStack(
																setItemLore(Server.WINDOW_ITEMS, MinecraftReflection.getBukkitItemStack(minecraftItemStack), player)));
													}
													Object recipeItemStack = staticRecipeItemStack.invoke(null, newList.stream());
													setFromNonNullListClass.invoke(nonNullList, j, recipeItemStack);
												}
											}
											ShapedRecipe shapedRecipe = (ShapedRecipe) toBukkitRecipeFromShapedRecipes.invoke(iRecipe, TEMP_KEY);
											Object shapedRecipePattern = shapedRecipePatternConstructor.newInstance(shapedRecipe.getShape()[0].length(),
													shapedRecipe.getShape().length, nonNullList, Optional.empty());
											Object shapedRecipes = shapedRecipeConstructor.newInstance(shapedRecipe.getGroup(),
													getCraftingBookCategoryFromShapedRecipes.invoke(iRecipe), shapedRecipePattern,
													MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, shapedRecipe.getResult().clone(), event.getPlayer())),
													false);
											Object newRecipeHolderObject = recipeHolderConstructor.newInstance(getMinecraftKeyFromRecipeHolder.invoke(recipeHolderObject),
													shapedRecipes);
											recipeHolders.set(i, newRecipeHolderObject);
										}
										case "FurnaceRecipe" ->
										{
											FurnaceRecipe furnaceRecipe = (FurnaceRecipe) furnaceRecipeClass.getDeclaredMethod("toBukkitRecipe", NamespacedKey.class)
													.invoke(iRecipe, TEMP_KEY);
											RecipeChoice recipeChoice = furnaceRecipe.getInputChoice();
											List choices = new ArrayList<>();
											if (recipeChoice instanceof MaterialChoice materialChoice)
											{
												for (Material material : materialChoice.getChoices())
												{
													choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, new ItemStack(material), player)));
												}
											}
											if (recipeChoice instanceof ExactChoice exactChoice)
											{
												for (ItemStack itemStack : exactChoice.getChoices())
												{
													choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, itemStack, player)));
												}
											}
											Object recipeItemStack = staticRecipeItemStack.invoke(null, choices.stream());
											Object furnaceRecipeObject = furnaceRecipeClass.getDeclaredConstructors()[0].newInstance(furnaceRecipe.getGroup(),
													getCraftingBookCategoryFromCookingRecipeClass.invoke(iRecipe), recipeItemStack,
													MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, furnaceRecipe.getResult().clone(), player)),
													furnaceRecipe.getExperience(), furnaceRecipe.getCookingTime());
											Object newRecipeHolderObject = recipeHolderConstructor.newInstance(getMinecraftKeyFromRecipeHolder.invoke(recipeHolderObject),
													furnaceRecipeObject);
											recipeHolders.set(i, newRecipeHolderObject);
										}
										case "RecipeBlasting" ->
										{
											BlastingRecipe blastingRecipe = (BlastingRecipe) blastingRecipeClass.getDeclaredMethod("toBukkitRecipe", NamespacedKey.class)
													.invoke(iRecipe, TEMP_KEY);
											RecipeChoice recipeChoice = blastingRecipe.getInputChoice();
											List choices = new ArrayList<>();
											if (recipeChoice instanceof MaterialChoice materialChoice)
											{
												for (Material material : materialChoice.getChoices())
												{
													choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, new ItemStack(material), player)));
												}
											}
											if (recipeChoice instanceof ExactChoice exactChoice)
											{
												for (ItemStack itemStack : exactChoice.getChoices())
												{
													choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, itemStack, player)));
												}
											}
											Object recipeItemStack = staticRecipeItemStack.invoke(null, choices.stream());
											Object blastingRecipeObject = blastingRecipeClass.getDeclaredConstructors()[0].newInstance(blastingRecipe.getGroup(),
													getCraftingBookCategoryFromCookingRecipeClass.invoke(iRecipe), recipeItemStack,
													MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, blastingRecipe.getResult().clone(), player)),
													blastingRecipe.getExperience(), blastingRecipe.getCookingTime());
											Object newRecipeHolderObject = recipeHolderConstructor.newInstance(getMinecraftKeyFromRecipeHolder.invoke(recipeHolderObject),
													blastingRecipeObject);
											recipeHolders.set(i, newRecipeHolderObject);
										}
										case "RecipeSmoking" ->
										{
											SmokingRecipe smokingRecipe = (SmokingRecipe) smokingRecipeClass.getDeclaredMethod("toBukkitRecipe", NamespacedKey.class)
													.invoke(iRecipe, TEMP_KEY);
											RecipeChoice recipeChoice = smokingRecipe.getInputChoice();
											List choices = new ArrayList<>();
											if (recipeChoice instanceof MaterialChoice materialChoice)
											{
												for (Material material : materialChoice.getChoices())
												{
													choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, new ItemStack(material), player)));
												}
											}
											if (recipeChoice instanceof ExactChoice exactChoice)
											{
												for (ItemStack itemStack : exactChoice.getChoices())
												{
													choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, itemStack, player)));
												}
											}
											Object recipeItemStack = staticRecipeItemStack.invoke(null, choices.stream());
											Object smokingRecipeObject = smokingRecipeClass.getDeclaredConstructors()[0].newInstance(smokingRecipe.getGroup(),
													getCraftingBookCategoryFromCookingRecipeClass.invoke(iRecipe), recipeItemStack,
													MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, smokingRecipe.getResult().clone(), player)),
													smokingRecipe.getExperience(), smokingRecipe.getCookingTime());
											Object newRecipeHolderObject = recipeHolderConstructor.newInstance(getMinecraftKeyFromRecipeHolder.invoke(recipeHolderObject),
													smokingRecipeObject);
											recipeHolders.set(i, newRecipeHolderObject);
										}
										case "RecipeStonecutting" ->
										{
											StonecuttingRecipe stonecuttingRecipe = (StonecuttingRecipe) toBukkitRecipeFromStoneCuttingRecipe.invoke(iRecipe, TEMP_KEY);
											RecipeChoice recipeChoice = stonecuttingRecipe.getInputChoice();
											List choices = new ArrayList<>();
											if (recipeChoice instanceof MaterialChoice materialChoice)
											{
												for (Material material : materialChoice.getChoices())
												{
													choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, new ItemStack(material), player)));
												}
											}
											if (recipeChoice instanceof ExactChoice exactChoice)
											{
												for (ItemStack itemStack : exactChoice.getChoices())
												{
													choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, itemStack, player)));
												}
											}
											Object recipeItemStack = staticRecipeItemStack.invoke(null, choices.stream());
											Object stonecuttingRecipeObject = stoneCuttingRecipeConstructor.newInstance(stonecuttingRecipe.getGroup(), recipeItemStack,
													MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, stonecuttingRecipe.getResult().clone(), player)));
											Object newRecipeHolderObject = recipeHolderConstructor.newInstance(getMinecraftKeyFromRecipeHolder.invoke(recipeHolderObject),
													stonecuttingRecipeObject);
											recipeHolders.set(i, newRecipeHolderObject);
										}
									}
								}
								modifier.write(0, recipeHolders);
								event.setPacket(packet);
							}
							catch (Throwable ignored)
							{

							}
						}
						if (packet.getType() == Server.RECIPES)
						{
						}
						if (packet.getType() == Server.ENTITY_METADATA)
						{
							Player player = event.getPlayer();
							Entity entity = packet.getEntityModifier(player.getWorld()).read(0);
							if (entity instanceof Item item)
							{
								ItemStack itemStack = item.getItemStack();
								boolean showCustomName =
										UserData.SHOW_DROPPED_ITEM_CUSTOM_NAME.getBoolean(player) && !UserData.FORCE_HIDE_DROPPED_ITEM_CUSTOM_NAME.getBoolean(player);
								Boolean shouldShowCustomName = ItemStackUtil.shouldShowCustomName(itemStack);
								StructureModifier<List<WrappedDataValue>> watchableAccessor = packet.getDataValueCollectionModifier();
								List<WrappedDataValue> wrappedDataValues = watchableAccessor.read(0);
								wrappedDataValues.add(new WrappedDataValue(8, WrappedDataWatcher.Registry.getItemStackSerializer(false),
										MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, item.getItemStack(), player))));

								wrappedDataValues.add(new WrappedDataValue(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true),
										Optional.of(WrappedChatComponent.fromJson(ComponentUtil.serializeAsJson(ItemNameUtil.itemName(itemStack))).getHandle())));
								wrappedDataValues.add(new WrappedDataValue(3, WrappedDataWatcher.Registry.get(Boolean.class),
										shouldShowCustomName != null ? shouldShowCustomName : showCustomName));
								watchableAccessor.write(0, wrappedDataValues);
							}
							if (entity instanceof BlockDisplay blockDisplay)
							{
								StructureModifier<List<WrappedDataValue>> watchableAccessor = packet.getDataValueCollectionModifier();
								List<WrappedDataValue> wrappedDataValues = watchableAccessor.read(0);
								for (WrappedDataValue wrappedDataValue : wrappedDataValues)
								{
									MessageUtil.broadcastDebug(wrappedDataValue.getHandle().getClass() + " : " + wrappedDataValue.getHandle());
								}
							}
						}
						if (packet.getType() == Server.ENTITY_EQUIPMENT)
						{
/*							for (int i = 0; i < modifier.size(); i++)
							{
								Object o = modifier.read(i);
								MessageUtil.broadcast(o.getClass() + " : " + o);
							}*/
							Player player = event.getPlayer();
							List<Pair<ItemSlot, ItemStack>> listStructureModifier = packet.getSlotStackPairLists().read(0);
							for (Pair<ItemSlot, ItemStack> pair : listStructureModifier)
							{
								pair.setSecond(setItemLore(Server.WINDOW_ITEMS, pair.getSecond(), player));
							}
							packet.getSlotStackPairLists().write(0, listStructureModifier);
						}
						if (packet.getType() == Server.TAB_COMPLETE)
						{
							Suggestions suggestions = (Suggestions) modifier.read(1);
							List<Suggestion> list = suggestions.getList();
							boolean isPlayerList = false;
							for (Suggestion suggestion : list)
							{
								String text = suggestion.getText();
								if (text.equals("@a"))
								{
									isPlayerList = true;
									break;
								}
							}
							if (isPlayerList)
							{
								for (Player online : Bukkit.getOnlinePlayers())
								{
									String name = Variable.ORIGINAL_NAME.getOrDefault(online.getUniqueId(), online.getName());
									suggestions.getList().add(new Suggestion(StringRange.at(1), name));
								}
								modifier.write(1, suggestions);
								event.setPacket(packet);
							}
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

		boolean useLoreInCreative = !UserData.SHOW_ITEM_LORE_IN_CREATIVE_MODE.getBoolean(player) && player.getGameMode() == GameMode.CREATIVE, showItemLore =
				!useLoreInCreative && UserData.SHOW_ITEM_LORE.getBoolean(player), showEnchantGlints = UserData.SHOW_ENCHANTED_ITEM_GLINTS.getBoolean(
				player), forceShowEnchants = UserData.EVENT_EXCEPTION_ACCESS.getBoolean(player);

		boolean ignoreCreativeWhat = player.getGameMode() != GameMode.CREATIVE || !Arrays.stream(ITEM_TYPES).toList().contains(packetType);

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

		if (!useLoreInCreative)
		{
			itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[itemFlags.size()]));
		}

		List<Component> lore = itemMeta.lore();
		if (lore == null)
			lore = new ArrayList<>();

		if (!useLoreInCreative && !lore.isEmpty() && lore.get(0) instanceof TranslatableComponent translatableComponent && translatableComponent.key().isEmpty())
		{
			lore.set(0, Component.empty());
		}

		// 크리에이티브 모드에서는 아이템의 마법을 제거하면 실제로 아이템의 마법이 제거되므로 비활성화
		if (ignoreCreativeWhat && itemMeta.hasEnchants())
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
		if (ignoreCreativeWhat && itemMeta instanceof EnchantmentStorageMeta storageMeta && storageMeta.hasStoredEnchants())
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
		if (ignoreCreativeWhat && !showEnchantGlints)
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

		if (ignoreCreativeWhat && showItemLore && !new NBTItem(itemStack).getKeys().isEmpty())
		{
			itemMeta.addItemFlags(ItemFlag.values());
		}

		if (!useLoreInCreative)
		{
			itemMeta.lore(lore.isEmpty() ? null : lore);
		}

		CustomMaterial customMaterial = CustomMaterial.itemStackOf(clone);
		if (customMaterial != null && itemMeta.displayName() == null)
		{
			itemMeta.displayName(customMaterial.getDisplayName());
		}

		if (ignoreCreativeWhat && customMaterial != null)
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

		if (!useLoreInCreative)
		{
			clone.setItemMeta(itemMeta);
		}

		if (ignoreCreativeWhat && isStorageMeta)
		{
			ItemStack temp = new ItemStack(Material.ENCHANTED_BOOK);
			temp.setItemMeta(itemMeta);
			NBTItem tempNBTItem = new NBTItem(temp);
			tempNBTItem.removeKey("StoredEnchantments");
			new NBTItem(clone, true).mergeCompound(tempNBTItem);
		}

		if (player.getGameMode() == GameMode.CREATIVE && !showEnchantGlints)
		{
			if (itemMeta.hasEnchants() || List.of(Material.ENCHANTED_GOLDEN_APPLE, Material.EXPERIENCE_BOTTLE, Material.END_CRYSTAL, Material.NETHER_STAR,
					Material.ENCHANTED_BOOK).contains(itemStack.getType()))
			{
				lore.add(Component.empty());
				lore.add(ComponentUtil.translate("&b크레이이티브 모드에서는 아이템의 반짝임이"));
				lore.add(ComponentUtil.translate("&b설정에 관계없이 켜짐으로 설정됩니다"));
				itemMeta.lore(lore);
				clone.setItemMeta(itemMeta);
			}
		}

		if (nbtItem.hasTag("override_item_stack"))
		{
			ItemStack overrideItemStack = null;
			if (nbtItem.getType("override_item_stack") == NBTType.NBTTagString)
			{
				String overrideItemStackString = nbtItem.getString("override_item_stack");
				try
				{
					if (overrideItemStackString.startsWith("custom_material_"))
						overrideItemStack = CustomMaterial.valueOf(overrideItemStackString.substring("custom_material_".length())).create();
					if (overrideItemStackString.startsWith("material_"))
						overrideItemStack = new ItemStack(Material.valueOf(overrideItemStackString.substring("material_".length())));
				}
				catch (Exception ignored)
				{
				}
			}
			else if (ItemStackUtil.itemExists(nbtItem.getItemStack("override_item_stack")))
			{
				overrideItemStack = nbtItem.getItemStack("override_item_stack");
			}
			if (ItemStackUtil.itemExists(overrideItemStack))
			{
				setItemLore(packetType, overrideItemStack, player);
				Component originDisplayname = player.getGameMode() == GameMode.CREATIVE || clone.getItemMeta().hasDisplayName()
						? clone.getItemMeta().displayName()
						: ItemNameUtil.itemName(clone);
				List<Component> originLore = clone.getItemMeta().lore();
				if (ignoreCreativeWhat)
					clone.setType(overrideItemStack.getType());
				new NBTItem(clone, true).mergeNBT(overrideItemStack);
				ItemMeta originMeta = clone.getItemMeta();
				originMeta.lore(originLore);
				originMeta.displayName(originDisplayname);
				List<Component> cloneLore = originMeta.lore();
				if (cloneLore == null)
					cloneLore = new ArrayList<>();
				cloneLore.add(Component.empty());
				cloneLore.add(ComponentUtil.translate("#CCFF52;신비한 샌즈의 힘에 의해 %s의 외형이 합성됨", ItemNameUtil.itemName(overrideItemStack)));
				if (player.getGameMode() == GameMode.CREATIVE)
					cloneLore.add(ComponentUtil.translate("&c크리에이티브 모드여서 아이템의 유형은 변경되지 않음!"));
				if (overrideItemStack.getAmount() > 1)
				{
					clone.setAmount(overrideItemStack.getAmount());
					if (player.hasPermission("asdf"))
					{
						cloneLore.add(ComponentUtil.translate("&b[관리자 모드 - 아이템 개수가 %s개로 고정되게 보임]", clone.getAmount()));
					}
				}
				originMeta.lore(cloneLore);
				clone.setItemMeta(originMeta);
			}
		}

		if (nbtItem.hasTag("override_item_stack_amount") && nbtItem.getType("override_item_stack_amount") == NBTType.NBTTagInt)
		{
			ItemMeta originMeta = clone.getItemMeta();
			List<Component> cloneLore = originMeta.lore();
			if (cloneLore == null)
				cloneLore = new ArrayList<>();
			clone.setAmount(nbtItem.getInteger("override_item_stack_amount"));
			if (player.hasPermission("asdf"))
			{
				cloneLore.add(ComponentUtil.translate("&b[관리자 모드 - 아이템 개수가 %s개로 고정되게 보임]", clone.getAmount()));
			}
			originMeta.lore(cloneLore);
			clone.setItemMeta(originMeta);
		}

		if (ignoreCreativeWhat && CustomEffectManager.hasEffect(player, CustomEffectType.THE_CHAOS_INVENTORY))
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
		if (!player.hasPermission("asdf") && ignoreCreativeWhat)
		{
			for (String key : nbtItem.getKeys())
			{
				switch (key)
				{
					case "display", "Lore", "Enchantments", "Damage", "HideFlags", "CustomModelData", "SkullOwner", "Potion", "pages", "author", "title",
							 "CustomPotionColor", "StoredEnchantments", "AttributeModifiers", "Unbreakable", "CanPlaceOn", "CanDestroy", "BlockEntityTag" ->
					{
					}
					default -> nbtItem.removeKey(key);
				}
			}
		}
		return clone;
	}

	private static final PacketType[] ITEM_TYPES = {
			Server.WINDOW_ITEMS,
			Server.OPEN_WINDOW_MERCHANT,
			Server.SET_SLOT
	};

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
