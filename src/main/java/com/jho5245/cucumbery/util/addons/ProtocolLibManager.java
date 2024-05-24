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
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.*;
import com.google.gson.JsonElement;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectTypeCooldown;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectTypeCustomMining;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectTypeMinecraft;
import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.itemlore.ItemLore.RemoveFlag;
import com.jho5245.cucumbery.util.itemlore.ItemLoreView;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.Method2;
import com.jho5245.cucumbery.util.storage.component.ItemStackComponent;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.component.util.ItemNameUtil;
import com.jho5245.cucumbery.util.storage.component.util.sendercomponent.SenderComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Constant.CucumberyHideFlag;
import com.jho5245.cucumbery.util.storage.data.CustomMaterial;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.data.Variable;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import com.jho5245.cucumbery.util.storage.no_groups.SoundPlay;
import de.tr7zw.changeme.nbtapi.*;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.ShowEntity;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonDataComponentValue;
import net.kyori.examination.ExaminableProperty;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.management.ReflectionException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProtocolLibManager
{
	private static final Set<UUID> firstJoins = new HashSet<>();

	private static final NamespacedKey TEMP_KEY = NamespacedKey.fromString("temp_recipe", Cucumbery.getPlugin());

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("[HH:mm:ss] ");

	private static final Class<?> recipeHolderClass;

	private static final Constructor<?> recipeHolderConstructor;

	private static final Method getMinecraftKeyFromRecipeHolder;

	private static final Method getIRecipeFromRecipeHolder;

	private static final Method toBukkitRecipeFromRecipeHolder;

	private static final Class<?> nonNullListClass = MinecraftReflection.getNonNullListClass();

	private static final Method setFromNonNullListClass;

	private static final Class<?> recipeItemStackClass;

	private static final Method getItemStackArrayFromRecipeItemStack;

	private static final Method staticRecipeItemStack;

	private static final Class<?> shapedRecipePatternClass;

	private static final Constructor<?> shapedRecipePatternConstructor;

	private static final Class<?> shapelessRecipesClass;

	private static final Constructor<?> shaplessRecipesConstructor;

	private static final Method getCraftingBookCategoryFromShapelessRecipes;

	private static final Method getNonNullListFromShapelessRecipes;

	private static final Method getStringFromShapelessRecipes;

	private static final Method toBukkitRecipeFromShaplessRecipes;

	private static final Class<?> shapedRecipesClass;

	private static final Constructor<?> shapedRecipeConstructor;

	private static final Method getCraftingBookCategoryFromShapedRecipes;

	private static final Method getNonNullListFromShapedRecipes;

	private static final Method toBukkitRecipeFromShapedRecipes;

	private static final Class<?> cookingRecipeClass;

	private static final Method getCraftingBookCategoryFromCookingRecipeClass;

	private static final Class<?> furnaceRecipeClass;

	private static final Class<?> blastingRecipeClass;

	private static final Class<?> smokingRecipeClass;

	private static final Class<?> stoneCuttingRecipeClass;

	private static final Constructor<?> stoneCuttingRecipeConstructor;

	private static final Method toBukkitRecipeFromStoneCuttingRecipe;

	static
	{
		try
		{
			recipeHolderClass = Class.forName("net.minecraft.world.item.crafting.RecipeHolder");
			recipeHolderConstructor = recipeHolderClass.getDeclaredConstructors()[0];
			getMinecraftKeyFromRecipeHolder = recipeHolderClass.getDeclaredMethod("a");
			getIRecipeFromRecipeHolder = recipeHolderClass.getMethod("b");
			toBukkitRecipeFromRecipeHolder = recipeHolderClass.getMethod("toBukkitRecipe");

			setFromNonNullListClass = nonNullListClass.getDeclaredMethod("set", Integer.TYPE, Object.class);

			recipeItemStackClass = Class.forName("net.minecraft.world.item.crafting.RecipeItemStack");
			getItemStackArrayFromRecipeItemStack = recipeItemStackClass.getDeclaredMethod("a");
			staticRecipeItemStack = recipeItemStackClass.getDeclaredMethod("a", Stream.class);

			shapedRecipePatternClass = Class.forName("net.minecraft.world.item.crafting.ShapedRecipePattern");
			shapedRecipePatternConstructor = shapedRecipePatternClass.getConstructors()[0];

			shapelessRecipesClass = Class.forName("net.minecraft.world.item.crafting.ShapelessRecipes");
			shaplessRecipesConstructor = shapelessRecipesClass.getDeclaredConstructors()[0];
			getCraftingBookCategoryFromShapelessRecipes = shapelessRecipesClass.getDeclaredMethod("d");
			getNonNullListFromShapelessRecipes = shapelessRecipesClass.getDeclaredMethod("a");
			getStringFromShapelessRecipes = shapelessRecipesClass.getDeclaredMethod("c");
			toBukkitRecipeFromShaplessRecipes = shapelessRecipesClass.getDeclaredMethod("toBukkitRecipe", NamespacedKey.class);

			shapedRecipesClass = Class.forName("net.minecraft.world.item.crafting.ShapedRecipes");
			shapedRecipeConstructor = shapedRecipesClass.getDeclaredConstructors()[0];
			getCraftingBookCategoryFromShapedRecipes = shapedRecipesClass.getDeclaredMethod("d");
			getNonNullListFromShapedRecipes = shapedRecipesClass.getDeclaredMethod("a");
			toBukkitRecipeFromShapedRecipes = shapedRecipesClass.getDeclaredMethod("toBukkitRecipe", NamespacedKey.class);

			cookingRecipeClass = Class.forName("net.minecraft.world.item.crafting.RecipeCooking");
			getCraftingBookCategoryFromCookingRecipeClass = cookingRecipeClass.getDeclaredMethod("f");

			furnaceRecipeClass = Class.forName("net.minecraft.world.item.crafting.FurnaceRecipe");
			blastingRecipeClass = Class.forName("net.minecraft.world.item.crafting.RecipeBlasting");
			smokingRecipeClass = Class.forName("net.minecraft.world.item.crafting.RecipeSmoking");

			stoneCuttingRecipeClass = Class.forName("net.minecraft.world.item.crafting.RecipeStonecutting");
			stoneCuttingRecipeConstructor = stoneCuttingRecipeClass.getDeclaredConstructors()[0];
			toBukkitRecipeFromStoneCuttingRecipe = stoneCuttingRecipeClass.getDeclaredMethod("toBukkitRecipe", NamespacedKey.class);
		}
		catch (ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}

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
							if (potionEffectType.equals(PotionEffectType.HASTE) && duration < 3 && amplifier == 0)
							{
								modifier.write(3, -1);
								event.setPacket(packet);
							}
							if (potionEffectType.equals(PotionEffectType.MINING_FATIGUE) && duration < 3 && amplifier == 0)
							{
								modifier.write(3, -1);
								event.setPacket(packet);
							}
							if (potionEffectType.equals(PotionEffectType.MINING_FATIGUE))
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

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.SLOWNESS) && potionEffectType.equals(PotionEffectType.SLOWNESS))
						{
							modifier.write(3, CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.SLOWNESS).getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.HASTE) && potionEffectType.equals(PotionEffectType.HASTE))
						{
							modifier.write(3, CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.HASTE).getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.MINING_FATIGUE) && potionEffectType.equals(PotionEffectType.MINING_FATIGUE))
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
								&& bootsType == CustomMaterial.MINER_BOOTS && potionEffectType.equals(PotionEffectType.HASTE) && duration < 5)
						{
							packet.getModifier().write(3, -1);
							event.setPacket(packet);
						}

						if (helmetType == CustomMaterial.FROG_HELMET && chestplateType == CustomMaterial.FROG_CHESTPLATE && leggingsType == CustomMaterial.FROG_LEGGINGS
								&& bootsType == CustomMaterial.FROG_BOOTS && potionEffectType.equals(PotionEffectType.JUMP_BOOST) && duration < 3)
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

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.NORMAL, Server.WINDOW_ITEMS)
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
				UUID uuid = player.getUniqueId();
				// 아이템이 표시될 때 실제 적용되야 하는 nbt는 적용함
				{
					Inventory top = player.getOpenInventory().getTopInventory(), bottom = player.getOpenInventory().getBottomInventory();
					for (int i = 0; i < top.getSize(); i++)
					{
						ItemStack topItemStack = top.getItem(i);
						if (!ItemStackUtil.itemExists(topItemStack))
							continue;
						top.setItem(i, ItemLore.setItemLore(topItemStack, true, ItemLoreView.of(player)));
					}
					for (int i = 0; i < bottom.getSize(); i++)
					{
						ItemStack bottomItemStack = bottom.getItem(i);
						if (!ItemStackUtil.itemExists(bottomItemStack))
							continue;
						bottom.setItem(i, ItemLore.setItemLore(bottomItemStack, true, ItemLoreView.of(player)));
					}
				}
				// MessageUtil.broadcastDebug("Window ID: " + packet.getIntegers().read(0));
				UserData.WINDOW_ID.set(uuid, packet.getIntegers().read(0));
				packet.getItemModifier().write(0, setItemLore(packet.getType(), packet.getItemModifier().read(0), player));
				StructureModifier<List<ItemStack>> modifier = packet.getItemListModifier();
				modifier.write(0, setItemLore(packet.getType(), modifier.read(0), player));
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.NORMAL, Server.SET_SLOT)
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
				StructureModifier<ItemStack> modifier = packet.getItemModifier();
				modifier.write(0, setItemLore(packet.getType(), modifier.read(0), player));
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.NORMAL, Server.OPEN_WINDOW_MERCHANT)
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
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.HIGH, Server.ENTITY_METADATA)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
					return;
				Player player = event.getPlayer();
				PacketContainer packet = event.getPacket();
				Entity entity = packet.getEntityModifier(player.getWorld()).read(0);
				if (entity instanceof Item item)
				{
					ItemStack itemStack = setItemLore(Server.WINDOW_ITEMS, item.getItemStack(), player);
					Component component = itemStack.getAmount() == 1
							? ItemNameUtil.itemName(itemStack)
							: Component.translatable("%s (%s)").arguments(ItemNameUtil.itemName(itemStack), Component.text(itemStack.getAmount(), Constant.THE_COLOR));
					boolean showCustomName =
							UserData.SHOW_DROPPED_ITEM_CUSTOM_NAME.getBoolean(player) && !UserData.FORCE_HIDE_DROPPED_ITEM_CUSTOM_NAME.getBoolean(player);
					Boolean shouldShowCustomName = ItemStackUtil.shouldShowCustomName(itemStack);
					StructureModifier<List<WrappedDataValue>> watchableAccessor = packet.getDataValueCollectionModifier();
					List<WrappedDataValue> wrappedDataValues = watchableAccessor.read(0);
					wrappedDataValues.add(
							new WrappedDataValue(8, WrappedDataWatcher.Registry.getItemStackSerializer(false), MinecraftReflection.getMinecraftItemStack(itemStack)));
					wrappedDataValues.add(new WrappedDataValue(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true),
							Optional.of(WrappedChatComponent.fromJson(ComponentUtil.serializeAsJson(component)).getHandle())));
					wrappedDataValues.add(
							new WrappedDataValue(3, WrappedDataWatcher.Registry.get(Boolean.class), shouldShowCustomName != null ? shouldShowCustomName : showCustomName));
					watchableAccessor.write(0, wrappedDataValues);
				}
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.HIGH, Server.ENTITY_EQUIPMENT)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
				{
					return;
				}
				Player player = event.getPlayer();
				PacketContainer packet = event.getPacket();
				List<Pair<ItemSlot, ItemStack>> listStructureModifier = packet.getSlotStackPairLists().read(0);
				for (Pair<ItemSlot, ItemStack> pair : listStructureModifier)
				{
					pair.setSecond(setItemLore(Server.WINDOW_ITEMS, pair.getSecond(), player));
				}
				packet.getSlotStackPairLists().write(0, listStructureModifier);
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.HIGH, Server.RECIPE_UPDATE)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
				{
					return;
				}
				Player player = event.getPlayer();
				PacketContainer packet = event.getPacket();
				StructureModifier<Object> modifier = packet.getModifier();
				UUID uuid = player.getUniqueId();
				if (!ProtocolLibManager.firstJoins.contains(uuid) && player.getDiscoveredRecipes().size() > 100)
				{
					ProtocolLibManager.firstJoins.add(uuid);
					return;
				}
				try
				{
					List recipeHolders = (List) modifier.read(0);
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
												setItemLore(Server.WINDOW_ITEMS, ((ShapelessRecipe) toBukkitRecipeFromShaplessRecipes.invoke(iRecipe, TEMP_KEY)).getResult().clone(),
														player)), nonNullList);
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
								Object shapedRecipePattern = shapedRecipePatternConstructor.newInstance(shapedRecipe.getShape()[0].length(), shapedRecipe.getShape().length,
										nonNullList, Optional.empty());
								Object shapedRecipes = shapedRecipeConstructor.newInstance(shapedRecipe.getGroup(), getCraftingBookCategoryFromShapedRecipes.invoke(iRecipe),
										shapedRecipePattern,
										MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, shapedRecipe.getResult().clone(), event.getPlayer())), false);
								Object newRecipeHolderObject = recipeHolderConstructor.newInstance(getMinecraftKeyFromRecipeHolder.invoke(recipeHolderObject), shapedRecipes);
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
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.HIGH, Server.SYSTEM_CHAT)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
				{
					return;
				}
				Player player = event.getPlayer();
				PacketContainer packet = event.getPacket();
				boolean isActionBar = packet.getBooleans().read(0);
				WrappedChatComponent wrappedChatComponent = packet.getChatComponents().read(0);
				final Component originComponent = GsonComponentSerializer.gson().deserialize(wrappedChatComponent.getJson());
				Component component = GsonComponentSerializer.gson().deserialize(wrappedChatComponent.getJson());
				// 채팅창에 시각 표시 - 메시지가 여러줄될 경우 줄마다 시각 추가 표시
				if (!isActionBar && UserData.SHOW_TIMESTAMP_ON_CHAT_MESSAGES.getBoolean(event.getPlayer()))
				{
					Date date = new Date();
					List<Component> children = new ArrayList<>();
					for (Component child : component.children())
					{
						if (child instanceof TextComponent textComponent && textComponent.content().contains("\n"))
						{
							child = textComponent.append(Component.text(DATE_FORMAT.format(date), NamedTextColor.GRAY));
						}
						children.add(child);
					}
					component = component.children(children);
				}
				component = parse(event.getPlayer(),
						originComponent instanceof TranslatableComponent translatableComponent && translatableComponent.key().equals("chat.type.admin"), component,
						component);
				Component prefix = null;
				if (originComponent instanceof TranslatableComponent translatableComponent)
				{
					String key = translatableComponent.key();
					switch (key)
					{
						case "chat.type.text", "chat.type.admin" ->
						{
						}
						case "commands.give.success.single" -> prefix = Prefix.INFO_HANDGIVE.get();
						case "commands.teleport.success.entity.single", "commands.teleport.success.entity.multiple", "commands.teleport.success.location.single",
								 "commands.teleport.success.location.multiple" -> prefix = Prefix.INFO_TELEPORT.get();
						default -> prefix = Prefix.INFO.get();
					}
				}

				// 에러 메시지인가?
				if (originComponent instanceof TextComponent textComponent && textComponent.content().isEmpty() && originComponent.color() != null
						&& originComponent.color().value() == NamedTextColor.RED.value() && !originComponent.children().isEmpty()
						&& originComponent.children().getFirst().color() == null)
				{
					// Bukkit.getConsoleSender().sendMessage(ComponentUtil.serializeAsJson(originComponent));
					SoundPlay.playErrorSound(player);
					prefix = Prefix.INFO_ERROR.get();
					component = component.color(null);
				}
				if (isActionBar)
					prefix = null;
				if (prefix != null)
				{
					component = Component.translatable("%s%s").arguments(prefix, component);
				}

				// 채팅창에 시각 표시
				if (!isActionBar && UserData.SHOW_TIMESTAMP_ON_CHAT_MESSAGES.getBoolean(event.getPlayer()))
				{
					Date date = new Date();
					component = Component.empty().append(Component.text(DATE_FORMAT.format(date), NamedTextColor.GRAY)).append(component);
				}
				packet.getChatComponents().write(0, WrappedChatComponent.fromJson(ComponentUtil.serializeAsJson(component)));
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.HIGH, Server.SET_ACTION_BAR_TEXT)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
				{
					return;
				}
				Player player = event.getPlayer();
				if (!player.locale().equals(Locale.KOREA))
					return;
				PacketContainer packet = event.getPacket();
				WrappedChatComponent wrappedChatComponent = packet.getChatComponents().read(0);
				Component component = GsonComponentSerializer.gson().deserialize(wrappedChatComponent.getJson());
				if (component instanceof TranslatableComponent translatableComponent && !translatableComponent.arguments().isEmpty())
				{
					List<Component> components = new ArrayList<>();
					for (ComponentLike componentLike : translatableComponent.arguments())
						components.add(componentLike.asComponent());
					component = translatableComponent.key(ComponentUtil.translate(player, translatableComponent.key(), components).key());
				}
				packet.getChatComponents().write(0, WrappedChatComponent.fromJson(ComponentUtil.serializeAsJson(component)));
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.HIGH, Server.CHAT)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
				{
					return;
				}
				Player player = event.getPlayer();
				PacketContainer packet = event.getPacket();
				Player sender = Bukkit.getPlayer(packet.getUUIDs().read(0));
				WrappedChatComponent wrappedChatComponent = packet.getChatComponents().read(0);
				// 보통 대화 메시지 검증 명령어에서 null을 반환함(예: /say, /tell 등)
				if (wrappedChatComponent == null)
				{
					return;
				}
				final Component originComponent = GsonComponentSerializer.gson().deserialize(wrappedChatComponent.getJson());
				ItemStack itemStack = sender != null ? sender.getInventory().getItemInMainHand() : null;
				if (ItemStackUtil.itemExists(itemStack))
				{
					Component component = originComponent;
					List<Component> newChildren = new ArrayList<>();
					for (Component child : originComponent.children())
					{
						if (child instanceof TextComponent textComponent && textComponent.content().contains("[i]"))
						{
							newChildren.add(
									ComponentUtil.translate(player, textComponent.content().replace("%", "%%").replace("[i]", "%s"), sender.hasPermission("asdf"), itemStack));
						}
					}
					if (!newChildren.isEmpty())
						component = component.children(newChildren);
					packet.getChatComponents().write(0, WrappedChatComponent.fromJson(ComponentUtil.serializeAsJson(component)));
				}
				if (UserData.SHOW_TIMESTAMP_ON_CHAT_MESSAGES.getBoolean(player) && sender != null)
				{
					event.setCancelled(true);
					Component message = ComponentUtil.translate("chat.type.text", SenderComponentUtil.senderComponent(player, sender, null),
							GsonComponentSerializer.gson().deserialize(packet.getChatComponents().read(0).getJson()));
					player.sendMessage(message);
				}
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.HIGH, Client.CHAT)
		{
			@Override
			public void onPacketReceiving(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
				{
					return;
				}
				Player player = event.getPlayer();
				PacketContainer packet = event.getPacket();
				String message = packet.getStrings().read(0);
				if (message.contains("[i]"))
				{
					ItemStack itemStack = player.getInventory().getItemInMainHand();
					if (!ItemStackUtil.itemExists(itemStack))
					{
						MessageUtil.sendError(player, Prefix.NO_HOLDING_ITEM);
						event.setCancelled(true);
						return;
					}
					if (CustomEffectManager.hasEffect(player, CustomEffectTypeCooldown.COOLDOWN_ITEM_MEGAPHONE))
					{
						MessageUtil.sendWarn(player, ComponentUtil.translate("아직 아이템 확성기를 사용할 수 없습니다"));
						event.setCancelled(true);
						return;
					}
					if (!player.hasPermission("asdf"))
					{
						Bukkit.getScheduler().runTask(Cucumbery.getPlugin(), () -> CustomEffectManager.addEffect(player, CustomEffectTypeCooldown.COOLDOWN_ITEM_MEGAPHONE));
					}
				}
			}
		});
	}

	@NotNull
	private static Component parse(@NotNull Player player, boolean isAdminMessage, @NotNull Component component, @NotNull Component root)
	{
		HoverEvent<?> hoverEvent = component.hoverEvent();
		if (hoverEvent != null)
		{
			try
			{
				if (hoverEvent.value() instanceof ShowItem showItem)
				{
					component = ItemStackComponent.itemStackComponent(ItemStackUtil.getItemStackFromHoverEvent(showItem), 1, Constant.THE_COLOR, false, player);
				}
				else if (hoverEvent.value() instanceof ShowEntity showEntity)
				{
					UUID uuid = showEntity.id();
					Entity entity = Method2.getEntity(uuid);
					if (entity != null)
					{
						component = SenderComponentUtil.senderComponent(player, entity, Constant.THE_COLOR);
					}
				}
			}
			catch (Exception e)
			{
				Bukkit.getConsoleSender().sendMessage("§4" + e.getMessage());
			}
		}
		if (component instanceof TranslatableComponent translatableComponent)
		{
			String key = translatableComponent.key();
			// 타임스탬프 표시되면 채팅창 줄바꿈됨 빡침 그래서 글자 수 좀 줄임
			if (key.isBlank())
			{
				int componentLength = MessageUtil.stripColor(ComponentUtil.serialize(root)).length();
				// Bukkit.getConsoleSender().sendMessage("length: " + componentLength);
				int maxLength = UserData.SHOW_TIMESTAMP_ON_CHAT_MESSAGES.getBoolean(player) ? 65 : 77;
				if (componentLength >= maxLength)
					key = key.substring(Math.min(key.length(), Math.abs(componentLength - maxLength)));
			}

			List<ComponentLike> translationArguments = new ArrayList<>(translatableComponent.arguments());
			List<Component> arguments = new ArrayList<>();
			for (ComponentLike componentLike : translationArguments)
			{
				Component argument = componentLike.asComponent();
				// 인수에 숫자가 있는데 만약 컴포넌트 색이 없으면 숫자의 색깔을 THE COLOR로 / 아닐 경우 색깔을 바꾸지 않음
				if (argument instanceof TextComponent textComponent && textComponent.color() == null)
				{
					String content = textComponent.content();
					try
					{
						Double.parseDouble(content);
						argument = argument.color(Constant.THE_COLOR);
					}
					catch (Exception ignored)
					{
					}
				}

				argument = parse(player, isAdminMessage, argument, root);
				// 관리자 명령어 메시지는 회색 기울임꼴이므로 인수의 색깔, 기울임 decoration을 제거한다.
				if (isAdminMessage)
				{
					argument = argument.color(null).decoration(TextDecoration.ITALIC, State.NOT_SET);
				}
				arguments.add(argument);
			}
			component = translatableComponent.key(ComponentUtil.translate(player, key, arguments).key()).arguments(arguments);
		}
		if (!component.children().isEmpty())
		{
			List<Component> newList = new ArrayList<>();
			for (int i = 0; i < component.children().size(); i++)
			{
				newList.add(parse(player, isAdminMessage, component.children().get(i), root));
			}
			component = component.children(newList);
		}
		return component;
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

		if (!useLoreInCreative && !lore.isEmpty() && lore.getFirst() instanceof TranslatableComponent translatableComponent && translatableComponent.key().isEmpty())
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
				itemMeta.setEnchantmentGlintOverride(false);
			}
		}

		// 크리에이티브 모드에서는 마법이 부여된 책의 반짝임을 제거하면 실제로 아이템의 마법이 제거되므로 비활성화
		if (ignoreCreativeWhat && itemMeta instanceof EnchantmentStorageMeta storageMeta && storageMeta.hasStoredEnchants())
		{
			if (!forceShowEnchants && !showEnchants)
			{
				itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
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
			itemMeta.setEnchantmentGlintOverride(false);
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
		if (customMaterial != null)
		{
			itemMeta.itemName(customMaterial.getDisplayName());
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
				if (!nbtItem.hasTag("hide_override_item_stack"))
				{
					ItemMeta overrideItemMeta = overrideItemStack.getItemMeta();
					overrideItemMeta.displayName(null);
					overrideItemStack.setItemMeta(overrideItemMeta);
					cloneLore.add(Component.empty());
					cloneLore.add(ComponentUtil.translate("#CCFF52;신비한 샌즈의 힘에 의해 %s의 외형이 합성됨", ItemNameUtil.itemName(overrideItemStack)));
				}
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
		
		itemMeta = clone.getItemMeta();
		lore = itemMeta.lore();
		if (lore == null) lore = new ArrayList<>();
		
		if (player.hasPermission("asdf") && UserData.SHOW_ITEM_COMPONENTS_INFO.getBoolean(player))
		{
			List<Component> componentLore = new ArrayList<>();
			if (itemMeta.hasMaxStackSize())
			{
				componentLore.add(ComponentUtil.translate("&7최대 스택 크기 : %s", itemMeta.getMaxStackSize()));
			}
			if (!componentLore.isEmpty())
			{
				lore.add(Component.empty());
				lore.add(ComponentUtil.translate("&b[아이템 컴포넌트 정보]"));
				lore.addAll(componentLore);
			}
			itemMeta.lore(lore);
			clone.setItemMeta(itemMeta);
		}

		if (CustomEffectManager.hasEffect(player, CustomEffectType.HIDE_ITEM_TOOLTIP))
		{
			itemMeta.setHideTooltip(true);
			clone.setItemMeta(itemMeta);
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
		if (!player.hasPermission("asdf") && ignoreCreativeWhat)
		{
			for (String key : nbtItem.getKeys())
			{
				nbtItem.removeKey(key);
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
