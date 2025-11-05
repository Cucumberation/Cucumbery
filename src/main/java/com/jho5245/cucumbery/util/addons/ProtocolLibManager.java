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
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.CustomEffect;
import com.jho5245.cucumbery.custom.customeffect.CustomEffect.DisplayType;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.children.group.DoubleCustomEffect;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectTypeCooldown;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectTypeMinecraft;
import com.jho5245.cucumbery.custom.custommaterial.CustomMaterial;
import com.jho5245.cucumbery.events.addon.protocollib.*;
import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.itemlore.ItemLore.RemoveFlag;
import com.jho5245.cucumbery.util.itemlore.ItemLoreView;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.no_groups.ItemSerializer;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.Method;
import com.jho5245.cucumbery.util.no_groups.Method2;
import com.jho5245.cucumbery.util.storage.component.ItemStackComponent;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.component.util.ItemNameUtil;
import com.jho5245.cucumbery.util.storage.component.util.sendercomponent.SenderComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Constant.CucumberyHideFlag;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.data.Variable;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import com.jho5245.cucumbery.util.storage.no_groups.SoundPlay;
import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.handler.NBTHandlers;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.event.ClickEvent.Payload;
import net.kyori.adventure.text.event.ClickEvent.Payload.Custom;
import net.kyori.adventure.text.event.ClickEvent.Payload.Dialog;
import net.kyori.adventure.text.event.ClickEvent.Payload.Int;
import net.kyori.adventure.text.event.ClickEvent.Payload.Text;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.ShowEntity;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ProtocolLibManager
{
	private static final Set<UUID> firstJoins = new HashSet<>();

	private static final NamespacedKey TEMP_KEY = NamespacedKey.fromString("temp_recipe", Cucumbery.getPlugin());

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("[HH:mm:ss] ");

	// ITEM TEXT DISPLAY MOUNT MAP
	private static final Map<Integer, List<Integer>> itemTextDisplayMountMap = Collections.synchronizedMap(new HashMap<>());

	// 임의로 패킷을 보내기 전 대기 시간
	private static final int RESEND_PACKET_DELAY_IN_TICKS = 0;

	// WINDOW_ITEMS Packet Listener 일정 시간 이내에 2회 이상 요청된 패킷은 무시후 일정 시간 뒤에 임의로 패킷을 보냄
	private static final Set<UUID> TOO_MANY_REQUESTS_COOLDOWN_UUIDS = new HashSet<>();

	// 임의로 패킷을 보낼 UUID들(중복 보냄 방지)
	private static final Set<UUID> RESEND_PACKET_TARGETS = new HashSet<>();

/*	private static final Class<?> recipeHolderClass;

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

	private static final Method toBukkitRecipeFromStoneCuttingRecipe;*/

/*	static
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
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}*/

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
					int duration = (int) modifier.read(3);
					if (entity instanceof Player player)
					{
						if (CustomEffectManager.hasEffect(player, CustomEffectType.GAESANS) && potionEffectType.equals(PotionEffectType.INVISIBILITY)
								&& player.isSneaking())
						{
							modifier.write(3, PotionEffect.INFINITE_DURATION);
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.SPEED) && potionEffectType.equals(PotionEffectType.SPEED))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.SPEED);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.SLOWNESS) && potionEffectType.equals(PotionEffectType.SLOWNESS))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.SLOWNESS);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.HASTE) && potionEffectType.equals(PotionEffectType.HASTE))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.HASTE);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.MINING_FATIGUE) && potionEffectType.equals(PotionEffectType.MINING_FATIGUE))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.MINING_FATIGUE);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.STRENGTH) && potionEffectType.equals(PotionEffectType.STRENGTH))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.STRENGTH);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.INSTANT_HEALTH) && potionEffectType.equals(PotionEffectType.INSTANT_HEALTH))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.INSTANT_HEALTH);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.INSTANT_DAMAGE) && potionEffectType.equals(PotionEffectType.INSTANT_DAMAGE))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.INSTANT_DAMAGE);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.JUMP_BOOST) && potionEffectType.equals(PotionEffectType.JUMP_BOOST))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.JUMP_BOOST);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.NAUSEA) && potionEffectType.equals(PotionEffectType.NAUSEA))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.NAUSEA);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.REGENERATION) && potionEffectType.equals(PotionEffectType.REGENERATION))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.REGENERATION);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.RESISTANCE) && potionEffectType.equals(PotionEffectType.RESISTANCE))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.RESISTANCE);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.FIRE_RESISTANCE) && potionEffectType.equals(PotionEffectType.FIRE_RESISTANCE))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.FIRE_RESISTANCE);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.WATER_BREATHING) && potionEffectType.equals(PotionEffectType.WATER_BREATHING))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.WATER_BREATHING);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.INVISIBILITY) && potionEffectType.equals(PotionEffectType.INVISIBILITY))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.INVISIBILITY);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.BLINDNESS) && potionEffectType.equals(PotionEffectType.BLINDNESS))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.BLINDNESS);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.NIGHT_VISION) && potionEffectType.equals(PotionEffectType.NIGHT_VISION))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.NIGHT_VISION);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.HUNGER) && potionEffectType.equals(PotionEffectType.HUNGER))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.HUNGER);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.WEAKNESS) && potionEffectType.equals(PotionEffectType.WEAKNESS))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.WEAKNESS);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.POISON) && potionEffectType.equals(PotionEffectType.POISON))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.POISON);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.WITHER) && potionEffectType.equals(PotionEffectType.WITHER))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.WITHER);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.HEALTH_BOOST) && potionEffectType.equals(PotionEffectType.HEALTH_BOOST))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.HEALTH_BOOST);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.ABSORPTION) && potionEffectType.equals(PotionEffectType.ABSORPTION))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.ABSORPTION);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.SATURATION) && potionEffectType.equals(PotionEffectType.SATURATION))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.SATURATION);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.GLOWING) && potionEffectType.equals(PotionEffectType.GLOWING))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.GLOWING);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.LEVITATION) && potionEffectType.equals(PotionEffectType.LEVITATION))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.LEVITATION);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.LUCK) && potionEffectType.equals(PotionEffectType.LUCK))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.LUCK);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.UNLUCK) && potionEffectType.equals(PotionEffectType.UNLUCK))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.UNLUCK);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.SLOW_FALLING) && potionEffectType.equals(PotionEffectType.SLOW_FALLING))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.SLOW_FALLING);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.CONDUIT_POWER) && potionEffectType.equals(PotionEffectType.CONDUIT_POWER))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.CONDUIT_POWER);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.DOLPHINS_GRACE) && potionEffectType.equals(PotionEffectType.DOLPHINS_GRACE))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.DOLPHINS_GRACE);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.BAD_OMEN) && potionEffectType.equals(PotionEffectType.BAD_OMEN))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.BAD_OMEN);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.HERO_OF_THE_VILLAGE) && potionEffectType.equals(
								PotionEffectType.HERO_OF_THE_VILLAGE))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.HERO_OF_THE_VILLAGE);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.DARKNESS) && potionEffectType.equals(PotionEffectType.DARKNESS))
						{
							CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectTypeMinecraft.DARKNESS);
							DisplayType displayType = customEffect.getDisplayType();
							// 일부 DisplayType이 None인 효과는 무한 지속으로 표시(주로 아이템 소지 효과)
							modifier.write(3, displayType == DisplayType.NONE ? PotionEffect.INFINITE_DURATION : customEffect.getDuration());
							event.setPacket(packet);
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectTypeMinecraft.DYNAMIC_LIGHT) && potionEffectType.equals(PotionEffectType.NIGHT_VISION))
						{
							PlayerInventory inventory = player.getInventory();
							Material mainHand = inventory.getItemInMainHand().getType(), offHand = inventory.getItemInOffHand().getType();
							if (Constant.OPTIFINE_DYNAMIC_LIGHT_ITEMS.contains(mainHand) || Constant.OPTIFINE_DYNAMIC_LIGHT_ITEMS.contains(offHand))
							{
								modifier.write(3, PotionEffect.INFINITE_DURATION);
								event.setPacket(packet);
							}
						}

						if (CustomEffectManager.hasEffect(player, CustomEffectType.NIGHT_VISION_SPECTATOR) && potionEffectType.equals(PotionEffectType.NIGHT_VISION)
								&& player.getSpectatorTarget() != null)
						{
							modifier.write(3, PotionEffect.INFINITE_DURATION);
							event.setPacket(packet);
						}

						PlayerInventory playerInventory = player.getInventory();
						ItemStack helmet = playerInventory.getHelmet(), chestplate = playerInventory.getChestplate(), leggings = playerInventory.getLeggings(), boots = playerInventory.getBoots();
						//						com.jho5245.cucumbery.util.storage.data.CustomMaterial helmetType = com.jho5245.cucumbery.util.storage.data.CustomMaterial.itemStackOf(
						//								helmet), chestplateType = com.jho5245.cucumbery.util.storage.data.CustomMaterial.itemStackOf(
						//								chestplate), leggingsType = com.jho5245.cucumbery.util.storage.data.CustomMaterial.itemStackOf(
						//								leggings), bootsType = com.jho5245.cucumbery.util.storage.data.CustomMaterial.itemStackOf(boots);
						//
						//						if ((helmetType == com.jho5245.cucumbery.util.storage.data.CustomMaterial.MINER_HELMET
						//								|| helmetType == com.jho5245.cucumbery.util.storage.data.CustomMaterial.MINDAS_HELMET) && potionEffectType.equals(
						//								PotionEffectType.NIGHT_VISION))
						//						{
						//							modifier.write(3, PotionEffect.INFINITE_DURATION);
						//							event.setPacket(packet);
						//						}
						//
						//						if (helmetType == com.jho5245.cucumbery.util.storage.data.CustomMaterial.MINER_HELMET
						//								&& chestplateType == com.jho5245.cucumbery.util.storage.data.CustomMaterial.MINER_CHESTPLATE
						//								&& leggingsType == com.jho5245.cucumbery.util.storage.data.CustomMaterial.MINER_LEGGINGS
						//								&& bootsType == com.jho5245.cucumbery.util.storage.data.CustomMaterial.MINER_BOOTS && potionEffectType.equals(PotionEffectType.HASTE))
						//						{
						//							packet.getModifier().write(3, PotionEffect.INFINITE_DURATION);
						//							event.setPacket(packet);
						//						}
						//
						//						if (helmetType == com.jho5245.cucumbery.util.storage.data.CustomMaterial.FROG_HELMET
						//								&& chestplateType == com.jho5245.cucumbery.util.storage.data.CustomMaterial.FROG_CHESTPLATE
						//								&& leggingsType == com.jho5245.cucumbery.util.storage.data.CustomMaterial.FROG_LEGGINGS
						//								&& bootsType == com.jho5245.cucumbery.util.storage.data.CustomMaterial.FROG_BOOTS && potionEffectType.equals(PotionEffectType.JUMP_BOOST))
						//						{
						//							packet.getModifier().write(3, PotionEffect.INFINITE_DURATION);
						//							event.setPacket(packet);
						//						}
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
				//				MessageUtil.consoleSendMessage("&c" + packet.getType().name());
				// 너무 자주 요청된 패킷은 처리하지 않고 반려
				if (TOO_MANY_REQUESTS_COOLDOWN_UUIDS.contains(uuid))
				{
					if (!RESEND_PACKET_TARGETS.contains(uuid))
					{
						RESEND_PACKET_TARGETS.add(uuid);
						Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () ->
						{
							//							MessageUtil.consoleSendMessage(player.getOpenInventory().getType().toString());
							//							ItemStackUtil.updateInventory(player);
							RESEND_PACKET_TARGETS.remove(uuid);
						}, RESEND_PACKET_DELAY_IN_TICKS);
					}
					return;
				}
				//				MessageUtil.consoleSendMessage("&a" + packet.getType().name());
				TOO_MANY_REQUESTS_COOLDOWN_UUIDS.add(uuid);
				Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> TOO_MANY_REQUESTS_COOLDOWN_UUIDS.remove(uuid), RESEND_PACKET_DELAY_IN_TICKS);
				// 아이템이 표시될 때 실제 적용되야 하는 nbt는 적용함
				//				{
				//					Inventory bottom = player.getOpenInventory().getBottomInventory();
				//					for (int i = 0; i < bottom.getSize(); i++)
				//					{
				//						ItemStack bottomItemStack = bottom.getItem(i);
				//						if (!ItemStackUtil.itemExists(bottomItemStack))
				//							continue;
				//						bottom.setItem(i, ItemLore.setItemLore(bottomItemStack, true, ItemLoreView.of(player)));
				//					}
				//				}
				UserData.WINDOW_ID.set(uuid, packet.getIntegers().read(0));
				StructureModifier<List<ItemStack>> modifier = packet.getItemListModifier();
				ItemStack itemStack = setItemLore(packet.getType(), packet.getItemModifier().read(0), player);
				List<ItemStack> itemStacks = setItemLore(packet.getType(), modifier.read(0), player);
				WindowItemsEvent windowItemsEvent = new WindowItemsEvent(player, itemStack, itemStacks);
				Bukkit.getPluginManager().callEvent(windowItemsEvent);
				if (!windowItemsEvent.isCancelled())
				{
					packet.getItemModifier().write(0, windowItemsEvent.getItemStack());
					modifier.write(0, windowItemsEvent.getItemStacks());
				}
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.NORMAL, Server.SET_CURSOR_ITEM)
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
				ItemStack itemStack = setItemLore(packet.getType(), packet.getItemModifier().read(0), player);
				SetCursorItemEvent setCursorItemEvent = new SetCursorItemEvent(player, itemStack);
				Bukkit.getPluginManager().callEvent(setCursorItemEvent);
				if (!setCursorItemEvent.isCancelled())
				{
					packet.getItemModifier().write(0, setCursorItemEvent.getItemStack());
				}
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
				//				UUID uuid = player.getUniqueId();
				//				MessageUtil.consoleSendMessage("&c" + packet.getType().name());
				//				// 너무 자주 요청된 패킷은 처리하지 않고 반려
				//				if (TOO_MANY_REQUESTS_COOLDOWN_UUIDS.contains(uuid))
				//				{
				////					if (!RESEND_PACKET_TARGETS.contains(uuid))
				////          {
				////						RESEND_PACKET_TARGETS.add(uuid);
				////						Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> {
				////							ItemStackUtil.updateInventory(player);
				////							RESEND_PACKET_TARGETS.remove(uuid);
				////            }, RESEND_PACKET_DELAY_IN_TICKS);
				////          }
				//					return;
				//				}
				//				MessageUtil.consoleSendMessage("&a" + packet.getType().name());
				//				TOO_MANY_REQUESTS_COOLDOWN_UUIDS.add(uuid);
				//				Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> TOO_MANY_REQUESTS_COOLDOWN_UUIDS.remove(uuid), RESEND_PACKET_DELAY_IN_TICKS);
				ItemStack itemStack = setItemLore(packet.getType(), packet.getItemModifier().read(0), player);
				SetSlotEvent setSlotEvent = new SetSlotEvent(player, itemStack);
				Bukkit.getPluginManager().callEvent(setSlotEvent);
				if (!setSlotEvent.isCancelled())
				{
					packet.getItemModifier().write(0, setSlotEvent.getItemStack());
				}
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
				Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> ItemStackUtil.updateInventory(player), 2L);
				StructureModifier<List<MerchantRecipe>> modifier = packet.getMerchantRecipeLists();
				List<MerchantRecipe> merchantRecipeList = modifier.read(0), newMerchantRecipeList = new ArrayList<>(merchantRecipeList.size());
				for (MerchantRecipe recipe : merchantRecipeList)
				{
					ItemStack result = setItemLore(packet.getType(), recipe.getResult(), player);
					List<ItemStack> ingredients = setItemLore(packet.getType(), recipe.getIngredients(), player);
					OpenWindowMerchantEvent openWindowMerchantEvent = new OpenWindowMerchantEvent(player, result, ingredients);
					Bukkit.getPluginManager().callEvent(openWindowMerchantEvent);
					if (!openWindowMerchantEvent.isCancelled())
					{
						MerchantRecipe newRecipe = new MerchantRecipe(openWindowMerchantEvent.getResult(), recipe.getUses(), recipe.getMaxUses(),
								recipe.hasExperienceReward(), recipe.getVillagerExperience(), recipe.getPriceMultiplier(), recipe.getDemand(), recipe.getSpecialPrice(),
								recipe.shouldIgnoreDiscounts());
						newRecipe.setIngredients(openWindowMerchantEvent.getIngredients());
						newMerchantRecipeList.add(newRecipe);
					}
				}
				modifier.write(0, newMerchantRecipeList);
			}
		});

/*		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.NORMAL, Server.MOUNT)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
					return;
				Player player = event.getPlayer();
				MessageUtil.broadcastDebug("mount");
				PacketContainer packet = event.getPacket();
				MessageUtil.broadcastDebug(packet.getIntegerArrays().read(0) + "");
				for (int i = 0; i < packet.getModifier().size(); i++)
				{
					Object o = packet.getModifier().read(i);
					MessageUtil.broadcastDebug(o.getClass() + ": " + o);
					if (o.getClass().isArray())
					{
						Arrays.stream((int[]) o).forEach(MessageUtil::broadcastDebug);
					}
				}
			}
		});*/

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.HIGH, Server.ENTITY_DESTROY)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
					return;
				PacketContainer packet = event.getPacket();
				List<Integer> integers = packet.getIntLists().read(0);
				//				MessageUtil.broadcastDebug("destroy(%s) - %s".formatted(integers.size(), integers));
				for (int id : integers)
				{
					if (ProtocolLibManager.itemTextDisplayMountMap.containsKey(id))
					{
						PacketContainer remove = protocolManager.createPacket(Server.ENTITY_DESTROY);
						remove.getIntLists().write(0, ProtocolLibManager.itemTextDisplayMountMap.get(id));
						protocolManager.sendServerPacket(event.getPlayer(), remove);
					}
				}
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.HIGH, Server.SPAWN_ENTITY)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
					return;
				PacketContainer packet = event.getPacket();
				Player player = event.getPlayer();
				Entity entity = packet.getEntityModifier(player.getWorld()).read(0);
				if (entity instanceof Item item)
				{
					ItemStack itemStack = setItemLore(Server.WINDOW_ITEMS, item.getItemStack(), player);
					int itemAmount = itemStack.getAmount();
					Component itemName = ItemNameUtil.itemName(itemStack);
					Component component =
							itemAmount == 1 ? itemName : Component.translatable("%s (%s)").arguments(itemName, Component.text(itemAmount, Constant.THE_COLOR));
					Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> mountTextDisplayToItem(protocolManager, player, component, item), 0L);
				}
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.HIGH, Server.ENTITY_METADATA)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (!Cucumbery.using_ProtocolLib)
					return;
				PacketContainer packet = event.getPacket();
				Player player = event.getPlayer();
				Entity entity = packet.getEntityModifier(player.getWorld()).read(0);
				if (entity instanceof Item item)
				{
					ItemStack itemStack = setItemLore(Server.WINDOW_ITEMS, item.getItemStack(), player);
					int itemAmount = itemStack.getAmount();
					Component itemName = ItemNameUtil.itemName(itemStack);
					//					boolean showCustomName =
					//							UserData.SHOW_DROPPED_ITEM_CUSTOM_NAME.getBoolean(player) && !UserData.FORCE_HIDE_DROPPED_ITEM_CUSTOM_NAME.getBoolean(player);
					//					Boolean shouldShowCustomName = ItemStackUtil.shouldShowCustomName(itemStack);
					EntityMetaDataEvent entityMetaDataEvent = new EntityMetaDataEvent(player, itemStack, entity);
					entityMetaDataEvent.callEvent();
					if (!entityMetaDataEvent.isCancelled())
					{
						itemName = ItemNameUtil.itemName(entityMetaDataEvent.getItemStack());
						StructureModifier<List<WrappedDataValue>> watchableAccessor = packet.getDataValueCollectionModifier();
						List<WrappedDataValue> wrappedDataValues = watchableAccessor.read(0);
						wrappedDataValues.add(new WrappedDataValue(8, WrappedDataWatcher.Registry.getItemStackSerializer(false),
								MinecraftReflection.getMinecraftItemStack(entityMetaDataEvent.getItemStack())));
						watchableAccessor.write(0, wrappedDataValues);
					}
					Component component =
							itemAmount == 1 ? itemName : Component.translatable("%s (%s)").arguments(itemName, Component.text(itemAmount, Constant.THE_COLOR));

					//					wrappedDataValues.add(new WrappedDataValue(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true),
					//							Optional.of(WrappedChatComponent.fromJson(ComponentUtil.serializeAsJson(component)).getHandle())));
					//					wrappedDataValues.add(
					//							new WrappedDataValue(3, WrappedDataWatcher.Registry.get(Boolean.class), shouldShowCustomName != null ? shouldShowCustomName : showCustomName));
					//					// 아이템 웅크리게 하기
					//					byte sneakStatus = UserData.SHOW_DROPPED_ITEM_CUSTOM_NAME_BEHIND_BLOCKS.getBoolean(player) ? (byte) 0 : (byte) 0x02;
					//					wrappedDataValues.add(new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), sneakStatus));

					// entity metadata 패킷이 플레이어가 서버 접속 직후 보낸 패킷인지/접속 도중 보낸 패킷인지 구분하여
					// 만약 서버 접속 직후 보낸 패킷이라면 일정 시간 뒤에 객체를 탑승시킨다.
					mountTextDisplayToItem(protocolManager, player, component, item);
				}
				if (entity instanceof ItemFrame itemFrame)
				{
					ItemStack itemStack = setItemLore(Server.WINDOW_ITEMS, itemFrame.getItem(), player);
					EntityMetaDataEvent entityMetaDataEvent = new EntityMetaDataEvent(player, itemStack, entity);
					entityMetaDataEvent.callEvent();
					if (!entityMetaDataEvent.isCancelled())
					{
						StructureModifier<List<WrappedDataValue>> watchableAccessor = packet.getDataValueCollectionModifier();
						List<WrappedDataValue> wrappedDataValues = watchableAccessor.read(0);
						wrappedDataValues.add(new WrappedDataValue(9, WrappedDataWatcher.Registry.getItemStackSerializer(false),
								MinecraftReflection.getMinecraftItemStack(entityMetaDataEvent.getItemStack())));
						watchableAccessor.write(0, wrappedDataValues);
					}
				}
				if (entity instanceof ThrowableProjectile throwableProjectile && !(entity instanceof Trident))
				{
					ItemStack itemStack = setItemLore(Server.WINDOW_ITEMS, throwableProjectile.getItem(), player);
					EntityMetaDataEvent entityMetaDataEvent = new EntityMetaDataEvent(player, itemStack, entity);
					entityMetaDataEvent.callEvent();
					if (!entityMetaDataEvent.isCancelled())
					{
						StructureModifier<List<WrappedDataValue>> watchableAccessor = packet.getDataValueCollectionModifier();
						List<WrappedDataValue> wrappedDataValues = watchableAccessor.read(0);
						wrappedDataValues.add(new WrappedDataValue(8, WrappedDataWatcher.Registry.getItemStackSerializer(false),
								MinecraftReflection.getMinecraftItemStack(entityMetaDataEvent.getItemStack())));
						watchableAccessor.write(0, wrappedDataValues);
					}
				}
				if (entity instanceof EnderSignal enderSignal)
				{
					ItemStack itemStack = setItemLore(Server.WINDOW_ITEMS, enderSignal.getItem(), player);
					EntityMetaDataEvent entityMetaDataEvent = new EntityMetaDataEvent(player, itemStack, entity);
					entityMetaDataEvent.callEvent();
					if (!entityMetaDataEvent.isCancelled())
					{
						StructureModifier<List<WrappedDataValue>> watchableAccessor = packet.getDataValueCollectionModifier();
						List<WrappedDataValue> wrappedDataValues = watchableAccessor.read(0);
						wrappedDataValues.add(new WrappedDataValue(8, WrappedDataWatcher.Registry.getItemStackSerializer(false),
								MinecraftReflection.getMinecraftItemStack(entityMetaDataEvent.getItemStack())));
						watchableAccessor.write(0, wrappedDataValues);
					}
				}
				if (entity instanceof ItemDisplay itemDisplay && ItemStackUtil.itemExists(itemDisplay.getItemStack()))
				{
					ItemStack itemStack = setItemLore(Server.WINDOW_ITEMS, itemDisplay.getItemStack(), player);
					EntityMetaDataEvent entityMetaDataEvent = new EntityMetaDataEvent(player, itemStack, entity);
					entityMetaDataEvent.callEvent();
					if (!entityMetaDataEvent.isCancelled())
					{
						StructureModifier<List<WrappedDataValue>> watchableAccessor = packet.getDataValueCollectionModifier();
						List<WrappedDataValue> wrappedDataValues = watchableAccessor.read(0);
						wrappedDataValues.add(new WrappedDataValue(23, WrappedDataWatcher.Registry.getItemStackSerializer(false),
								MinecraftReflection.getMinecraftItemStack(entityMetaDataEvent.getItemStack())));
						watchableAccessor.write(0, wrappedDataValues);
					}
				}
				if (entity instanceof Trident)
				{
					if (!UserData.SHOW_ENCHANTED_ITEM_GLINTS.getBoolean(player))
					{
						StructureModifier<List<WrappedDataValue>> watchableAccessor = packet.getDataValueCollectionModifier();
						List<WrappedDataValue> wrappedDataValues = watchableAccessor.read(0);
						wrappedDataValues.add(new WrappedDataValue(12, WrappedDataWatcher.Registry.get(Boolean.class), false));
						watchableAccessor.write(0, wrappedDataValues);
					}
				}
				if (entity instanceof OminousItemSpawner ominousItemSpawner)
				{
					ItemStack itemStack = setItemLore(Server.WINDOW_ITEMS, ominousItemSpawner.getItem(), player);
					EntityMetaDataEvent entityMetaDataEvent = new EntityMetaDataEvent(player, itemStack, entity);
					entityMetaDataEvent.callEvent();
					if (!entityMetaDataEvent.isCancelled())
					{
						StructureModifier<List<WrappedDataValue>> watchableAccessor = packet.getDataValueCollectionModifier();
						List<WrappedDataValue> wrappedDataValues = watchableAccessor.read(0);
						wrappedDataValues.add(new WrappedDataValue(8, WrappedDataWatcher.Registry.getItemStackSerializer(false),
								MinecraftReflection.getMinecraftItemStack(entityMetaDataEvent.getItemStack())));
						watchableAccessor.write(0, wrappedDataValues);
					}
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
					if (!ItemStackUtil.itemExists(pair.getSecond()))
						continue;
					ItemStack itemStack = setItemLore(Server.WINDOW_ITEMS, pair.getSecond(), player);
					EntityEquipmentEvent entityEquipmentEvent = new EntityEquipmentEvent(player, itemStack);
					if (entityEquipmentEvent.callEvent())
					{
						pair.setSecond(entityEquipmentEvent.getItemStack());
					}
				}
				packet.getSlotStackPairLists().write(0, listStructureModifier);
			}
		});

		//		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.HIGH, Server.RECIPE_UPDATE)
		//		{
		//			@Override
		//			public void onPacketSending(PacketEvent event)
		//			{
		//				if (!Cucumbery.using_ProtocolLib)
		//				{
		//					return;
		//				}
		//				Player player = event.getPlayer();
		//				PacketContainer packet = event.getPacket();
		//				StructureModifier<Object> modifier = packet.getModifier();
		//				UUID uuid = player.getUniqueId();
		//				if (!ProtocolLibManager.firstJoins.contains(uuid) && player.getDiscoveredRecipes().size() > 100)
		//				{
		//					ProtocolLibManager.firstJoins.add(uuid);
		//					return;
		//				}
		//				try
		//				{
		//					List recipeHolders = (List) modifier.read(0);
		//					for (int i = 0; i < recipeHolders.size(); i++)
		//					{
		//						Object recipeHolderObject = recipeHolders.get(i);
		//						Recipe recipe = (Recipe) toBukkitRecipeFromRecipeHolder.invoke(recipeHolderObject);
		//						if (recipe.getResult().getType().isAir())
		//						{
		//							continue;
		//						}
		//						Object iRecipe = getIRecipeFromRecipeHolder.invoke(recipeHolderObject);
		//						switch (iRecipe.getClass().getSimpleName())
		//						{
		//							case "ShapelessRecipes" ->
		//							{
		//								Object nonNullList = getNonNullListFromShapelessRecipes.invoke(iRecipe);
		//								List recipeItemStackList = new ArrayList();
		//								if (nonNullList instanceof Iterable<?> iterable)
		//								{
		//									for (Object o : iterable)
		//									{
		//										recipeItemStackList.add(o);
		//									}
		//								}
		//								for (int j = 0; j < recipeItemStackList.size(); j++)
		//								{
		//									Object o = recipeItemStackList.get(j);
		//									Object minecraftItemStackArray = getItemStackArrayFromRecipeItemStack.invoke(o);
		//									if (minecraftItemStackArray instanceof Object[] array)
		//									{
		//										List newList = new ArrayList();
		//										for (Object minecraftItemStack : array)
		//										{
		//											newList.add(MinecraftReflection.getMinecraftItemStack(
		//													setItemLore(Server.WINDOW_ITEMS, MinecraftReflection.getBukkitItemStack(minecraftItemStack), player)));
		//										}
		//										Object recipeItemStack = staticRecipeItemStack.invoke(null, newList.stream());
		//										setFromNonNullListClass.invoke(nonNullList, j, recipeItemStack);
		//									}
		//								}
		//								Object shapelessRecipes = shaplessRecipesConstructor.newInstance(getStringFromShapelessRecipes.invoke(iRecipe),
		//										getCraftingBookCategoryFromShapelessRecipes.invoke(iRecipe), MinecraftReflection.getMinecraftItemStack(
		//												setItemLore(Server.WINDOW_ITEMS, ((ShapelessRecipe) toBukkitRecipeFromShaplessRecipes.invoke(iRecipe, TEMP_KEY)).getResult().clone(),
		//														player)), nonNullList);
		//								Object newRecipeHolderObject = recipeHolderConstructor.newInstance(getMinecraftKeyFromRecipeHolder.invoke(recipeHolderObject),
		//										shapelessRecipes);
		//								recipeHolders.set(i, newRecipeHolderObject);
		//							}
		//							case "ShapedRecipes" ->
		//							{
		//								Object nonNullList = getNonNullListFromShapedRecipes.invoke(iRecipe);
		//								List recipeItemStackList = new ArrayList();
		//								if (nonNullList instanceof Iterable<?> iterable)
		//								{
		//									for (Object o : iterable)
		//									{
		//										recipeItemStackList.add(o);
		//									}
		//								}
		//								for (int j = 0; j < recipeItemStackList.size(); j++)
		//								{
		//									Object o = recipeItemStackList.get(j);
		//									Object minecraftItemStackArray = getItemStackArrayFromRecipeItemStack.invoke(o);
		//									if (minecraftItemStackArray instanceof Object[] array)
		//									{
		//										List newList = new ArrayList();
		//										for (Object minecraftItemStack : array)
		//										{
		//											newList.add(MinecraftReflection.getMinecraftItemStack(
		//													setItemLore(Server.WINDOW_ITEMS, MinecraftReflection.getBukkitItemStack(minecraftItemStack), player)));
		//										}
		//										Object recipeItemStack = staticRecipeItemStack.invoke(null, newList.stream());
		//										setFromNonNullListClass.invoke(nonNullList, j, recipeItemStack);
		//									}
		//								}
		//								ShapedRecipe shapedRecipe = (ShapedRecipe) toBukkitRecipeFromShapedRecipes.invoke(iRecipe, TEMP_KEY);
		//								Object shapedRecipePattern = shapedRecipePatternConstructor.newInstance(shapedRecipe.getShape()[0].length(), shapedRecipe.getShape().length,
		//										nonNullList, Optional.empty());
		//								Object shapedRecipes = shapedRecipeConstructor.newInstance(shapedRecipe.getGroup(), getCraftingBookCategoryFromShapedRecipes.invoke(iRecipe),
		//										shapedRecipePattern,
		//										MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, shapedRecipe.getResult().clone(), event.getPlayer())), false);
		//								Object newRecipeHolderObject = recipeHolderConstructor.newInstance(getMinecraftKeyFromRecipeHolder.invoke(recipeHolderObject), shapedRecipes);
		//								recipeHolders.set(i, newRecipeHolderObject);
		//							}
		//							case "FurnaceRecipe" ->
		//							{
		//								FurnaceRecipe furnaceRecipe = (FurnaceRecipe) furnaceRecipeClass.getDeclaredMethod("toBukkitRecipe", NamespacedKey.class)
		//										.invoke(iRecipe, TEMP_KEY);
		//								RecipeChoice recipeChoice = furnaceRecipe.getInputChoice();
		//								List choices = new ArrayList<>();
		//								if (recipeChoice instanceof MaterialChoice materialChoice)
		//								{
		//									for (Material material : materialChoice.getChoices())
		//									{
		//										choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, new ItemStack(material), player)));
		//									}
		//								}
		//								if (recipeChoice instanceof ExactChoice exactChoice)
		//								{
		//									for (ItemStack itemStack : exactChoice.getChoices())
		//									{
		//										choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, itemStack, player)));
		//									}
		//								}
		//								Object recipeItemStack = staticRecipeItemStack.invoke(null, choices.stream());
		//								Object furnaceRecipeObject = furnaceRecipeClass.getDeclaredConstructors()[0].newInstance(furnaceRecipe.getGroup(),
		//										getCraftingBookCategoryFromCookingRecipeClass.invoke(iRecipe), recipeItemStack,
		//										MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, furnaceRecipe.getResult().clone(), player)),
		//										furnaceRecipe.getExperience(), furnaceRecipe.getCookingTime());
		//								Object newRecipeHolderObject = recipeHolderConstructor.newInstance(getMinecraftKeyFromRecipeHolder.invoke(recipeHolderObject),
		//										furnaceRecipeObject);
		//								recipeHolders.set(i, newRecipeHolderObject);
		//							}
		//							case "RecipeBlasting" ->
		//							{
		//								BlastingRecipe blastingRecipe = (BlastingRecipe) blastingRecipeClass.getDeclaredMethod("toBukkitRecipe", NamespacedKey.class)
		//										.invoke(iRecipe, TEMP_KEY);
		//								RecipeChoice recipeChoice = blastingRecipe.getInputChoice();
		//								List choices = new ArrayList<>();
		//								if (recipeChoice instanceof MaterialChoice materialChoice)
		//								{
		//									for (Material material : materialChoice.getChoices())
		//									{
		//										choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, new ItemStack(material), player)));
		//									}
		//								}
		//								if (recipeChoice instanceof ExactChoice exactChoice)
		//								{
		//									for (ItemStack itemStack : exactChoice.getChoices())
		//									{
		//										choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, itemStack, player)));
		//									}
		//								}
		//								Object recipeItemStack = staticRecipeItemStack.invoke(null, choices.stream());
		//								Object blastingRecipeObject = blastingRecipeClass.getDeclaredConstructors()[0].newInstance(blastingRecipe.getGroup(),
		//										getCraftingBookCategoryFromCookingRecipeClass.invoke(iRecipe), recipeItemStack,
		//										MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, blastingRecipe.getResult().clone(), player)),
		//										blastingRecipe.getExperience(), blastingRecipe.getCookingTime());
		//								Object newRecipeHolderObject = recipeHolderConstructor.newInstance(getMinecraftKeyFromRecipeHolder.invoke(recipeHolderObject),
		//										blastingRecipeObject);
		//								recipeHolders.set(i, newRecipeHolderObject);
		//							}
		//							case "RecipeSmoking" ->
		//							{
		//								SmokingRecipe smokingRecipe = (SmokingRecipe) smokingRecipeClass.getDeclaredMethod("toBukkitRecipe", NamespacedKey.class)
		//										.invoke(iRecipe, TEMP_KEY);
		//								RecipeChoice recipeChoice = smokingRecipe.getInputChoice();
		//								List choices = new ArrayList<>();
		//								if (recipeChoice instanceof MaterialChoice materialChoice)
		//								{
		//									for (Material material : materialChoice.getChoices())
		//									{
		//										choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, new ItemStack(material), player)));
		//									}
		//								}
		//								if (recipeChoice instanceof ExactChoice exactChoice)
		//								{
		//									for (ItemStack itemStack : exactChoice.getChoices())
		//									{
		//										choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, itemStack, player)));
		//									}
		//								}
		//								Object recipeItemStack = staticRecipeItemStack.invoke(null, choices.stream());
		//								Object smokingRecipeObject = smokingRecipeClass.getDeclaredConstructors()[0].newInstance(smokingRecipe.getGroup(),
		//										getCraftingBookCategoryFromCookingRecipeClass.invoke(iRecipe), recipeItemStack,
		//										MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, smokingRecipe.getResult().clone(), player)),
		//										smokingRecipe.getExperience(), smokingRecipe.getCookingTime());
		//								Object newRecipeHolderObject = recipeHolderConstructor.newInstance(getMinecraftKeyFromRecipeHolder.invoke(recipeHolderObject),
		//										smokingRecipeObject);
		//								recipeHolders.set(i, newRecipeHolderObject);
		//							}
		//							case "RecipeStonecutting" ->
		//							{
		//								StonecuttingRecipe stonecuttingRecipe = (StonecuttingRecipe) toBukkitRecipeFromStoneCuttingRecipe.invoke(iRecipe, TEMP_KEY);
		//								RecipeChoice recipeChoice = stonecuttingRecipe.getInputChoice();
		//								List choices = new ArrayList<>();
		//								if (recipeChoice instanceof MaterialChoice materialChoice)
		//								{
		//									for (Material material : materialChoice.getChoices())
		//									{
		//										choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, new ItemStack(material), player)));
		//									}
		//								}
		//								if (recipeChoice instanceof ExactChoice exactChoice)
		//								{
		//									for (ItemStack itemStack : exactChoice.getChoices())
		//									{
		//										choices.add(MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, itemStack, player)));
		//									}
		//								}
		//								Object recipeItemStack = staticRecipeItemStack.invoke(null, choices.stream());
		//								Object stonecuttingRecipeObject = stoneCuttingRecipeConstructor.newInstance(stonecuttingRecipe.getGroup(), recipeItemStack,
		//										MinecraftReflection.getMinecraftItemStack(setItemLore(Server.WINDOW_ITEMS, stonecuttingRecipe.getResult().clone(), player)));
		//								Object newRecipeHolderObject = recipeHolderConstructor.newInstance(getMinecraftKeyFromRecipeHolder.invoke(recipeHolderObject),
		//										stonecuttingRecipeObject);
		//								recipeHolders.set(i, newRecipeHolderObject);
		//							}
		//						}
		//					}
		//					modifier.write(0, recipeHolders);
		//					event.setPacket(packet);
		//				}
		//				catch (Throwable ignored)
		//				{
		//
		//				}
		//			}
		//		});

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
				String json = wrappedChatComponent.getJson();
				final Component originComponent = JSONComponentSerializer.json().deserialize(json);
				Component component = JSONComponentSerializer.json().deserialize(json);
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
						originComponent instanceof TranslatableComponent translatableComponent && translatableComponent.key().equals("chat.type.admin"), true, component,
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
				{
					prefix = null;
				}
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
					// why this exists
					//					List<Component> components = new ArrayList<>();
					//					for (ComponentLike componentLike : translatableComponent.arguments())
					//						components.add(componentLike.asComponent());
					String key = ComponentUtil.convertConsonant(translatableComponent.key(), translatableComponent).key();
					component = translatableComponent.key(key).fallback(key);
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

		protocolManager.addPacketListener(new PacketAdapter(Cucumbery.getPlugin(), ListenerPriority.NORMAL, Server.UPDATE_ATTRIBUTES)
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
				List<WrappedAttribute> attributes = packet.getAttributeCollectionModifier().read(0);
				for (WrappedAttribute attribute : attributes)
				{
					String key = attribute.getAttributeKey();
					switch (key)
					{
						case "camera_distance" ->
						{
							if (CustomEffectManager.hasEffect(player, CustomEffectType.SECRET_GUARD_EFFECT_PROTOCOL))
							{
								CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectType.SECRET_GUARD_EFFECT_PROTOCOL);
								if (customEffect instanceof DoubleCustomEffect doubleCustomEffect)
								{
									double d = doubleCustomEffect.getDouble();
									if (attribute.getModifiers().isEmpty())
									{
										//										MessageUtil.broadcastDebug("cancelled");
										event.setCancelled(true);
									}
									else
									{
										for (var modifier : attribute.getModifiers())
										{
											MinecraftKey minecraftKey = modifier.getKey();
											if (minecraftKey != null && minecraftKey.getFullKey().equals(CustomEffectType.SECRET_GUARD_EFFECT.getNamespacedKey().toString())
													&& modifier.getAmount() == d)
											{
												//												MessageUtil.broadcastDebug("cancelled");
												event.setCancelled(true);
											}
										}
									}
								}
							}
						}
						case "movement_speed" ->
						{

						}
						case "block_break_speed" ->
						{

						}
						case "scale" ->
						{
							if (CustomEffectManager.hasEffect(player, CustomEffectType.SNEAK_TO_GIANT_EFFECT_PROTOCOL))
							{
								CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectType.SNEAK_TO_GIANT_EFFECT_PROTOCOL);
								if (customEffect instanceof DoubleCustomEffect doubleCustomEffect)
								{
									double d = doubleCustomEffect.getDouble();
									if (attribute.getModifiers().isEmpty())
									{
										//										MessageUtil.broadcastDebug("cancelled");
										event.setCancelled(true);
									}
									else
									{
										for (var modifier : attribute.getModifiers())
										{
											MinecraftKey minecraftKey = modifier.getKey();
											if (minecraftKey != null && minecraftKey.getFullKey().equals(CustomEffectType.SNEAK_TO_GIANT_EFFECT.getNamespacedKey().toString())
													&& modifier.getAmount() == d)
											{
												//												MessageUtil.broadcastDebug("cancelled");
												event.setCancelled(true);
											}
										}
									}
								}
							}
						}
						default ->
						{
						}
					}
				}
			}
		});
	}

	/**
	 * 아이템 이름을 보이게 하기 위해 아이템에 Text display 패킷 엔티티를 탑승 시킵니다.
	 *
	 * @param protocolManager
	 * 		Manager
	 * @param player
	 * 		이 패킷을 받는 플레이어
	 * @param component
	 * 		아이템 이름
	 * @param item
	 * 		아이템 개체
	 */
	private static void mountTextDisplayToItem(ProtocolManager protocolManager, Player player, Component component, Item item)
	{
		if (!UserData.SHOW_DROPPED_ITEM_CUSTOM_NAME.getBoolean(player))
			return;
		int entityId = Method.random(1, Integer.MAX_VALUE);
		PacketContainer spawnEntity = new PacketContainer(Server.SPAWN_ENTITY);
		Location itemLocation = item.getLocation();
		ItemStack itemStack = item.getItemStack();
		int maxStackSize = itemStack.getMaxStackSize();
		boolean isMaxStackSizeOne = maxStackSize == 1;

		spawnEntity.getIntegers().write(0, entityId);
		spawnEntity.getEntityTypeModifier().write(0, EntityType.TEXT_DISPLAY);
		// 가끔 엔티티가 여러 번 소환댐 ???? 그래서 기본 위치를 망한 위치로 지정
		spawnEntity.getDoubles().write(0, itemLocation.getX());
		spawnEntity.getDoubles().write(1, 0d);
		spawnEntity.getDoubles().write(2, itemLocation.getZ());
		// Set yaw/pitch
		spawnEntity.getBytes().write(0, (byte) (itemLocation.getPitch() * 256f / 360f));
		spawnEntity.getBytes().write(1, (byte) (itemLocation.getYaw() * 256f / 360f));
		// Set UUID
		spawnEntity.getUUIDs().write(0, UUID.randomUUID());
		protocolManager.sendServerPacket(player, spawnEntity);

		PacketContainer edit = protocolManager.createPacket(Server.ENTITY_METADATA);
		StructureModifier<List<WrappedDataValue>> modifier = edit.getDataValueCollectionModifier();
		WrappedChatComponent wrappedChatComponent = WrappedChatComponent.fromJson(ComponentUtil.serializeAsJson(component));
		List<WrappedDataValue> values = Lists.newArrayList(new WrappedDataValue(15, WrappedDataWatcher.Registry.get(Byte.class), (byte) 1), // Billboard
				new WrappedDataValue(16, WrappedDataWatcher.Registry.get(Integer.class), (15 << 4 | 15 << 20)), // Brightness override
				new WrappedDataValue(17, WrappedDataWatcher.Registry.get(Float.class), 0.25f), // view range
				new WrappedDataValue(19, WrappedDataWatcher.Registry.get(Float.class), 0f), // shadow strength
				new WrappedDataValue(23, WrappedDataWatcher.Registry.getChatComponentSerializer(), wrappedChatComponent.getHandle()), // text
				new WrappedDataValue(25, WrappedDataWatcher.Registry.get(Integer.class), 0x40_00_00_00), // background color ARGB
				new WrappedDataValue(26, WrappedDataWatcher.Registry.get(Byte.class), (byte) -1), // text opacity
				new WrappedDataValue(27, WrappedDataWatcher.Registry.get(Byte.class), (byte) 0x01) // shadow / see through / default bgcolor / alignment
		);
		// 아이템 최대 겹치기 개수가 1일 경우 표시되는 아이템 이름 글자를 좀 더 크게 + 좀 더 위에 보이도록 지정
		if (isMaxStackSizeOne)
		{
			values.add(new WrappedDataValue(11, WrappedDataWatcher.Registry.get(Vector3f.class), new Vector3f(0f, 0.4f, 0f))); // Translation
			values.add(new WrappedDataValue(12, WrappedDataWatcher.Registry.get(Vector3f.class), new Vector3f(1.2f, 1.2f, 1.2f))); // Scale
		}
		else
		{
			values.add(new WrappedDataValue(11, WrappedDataWatcher.Registry.get(Vector3f.class), new Vector3f(0f, 0.3f, 0f))); // Translation
		}
		modifier.write(0, values);
		edit.getIntegers().write(0, entityId);
		protocolManager.sendServerPacket(player, edit);

		List<Integer> passengerIDs = ProtocolLibManager.itemTextDisplayMountMap.getOrDefault(item.getEntityId(), Collections.synchronizedList(new ArrayList<>()));
		if (!passengerIDs.isEmpty())
		{
			PacketContainer remove = protocolManager.createPacket(Server.ENTITY_DESTROY);
			remove.getIntLists().write(0, passengerIDs);
			protocolManager.sendServerPacket(player, remove);
		}
		passengerIDs.add(entityId);
		ProtocolLibManager.itemTextDisplayMountMap.put(item.getEntityId(), passengerIDs);
		PacketContainer mount = protocolManager.createPacket(Server.MOUNT);
		mount.getIntegers().write(0, item.getEntityId());
		mount.getIntegerArrays().write(0, new int[] { entityId });
		protocolManager.sendServerPacket(player, mount);

	}

	@NotNull
	private static Component parse(@NotNull Player player, boolean isAdminMessage, boolean changeArgumentColor, @NotNull Component component,
			@NotNull Component root)
	{
		HoverEvent<?> hoverEvent = component.hoverEvent();
		if (hoverEvent != null)
		{
			try
			{
				if (hoverEvent.value() instanceof ShowItem showItem)
				{
					ItemStack itemStack = ItemStackUtil.getItemStackFromHoverEvent(showItem);
					ParseComponentItemStackEvent event = new ParseComponentItemStackEvent(player, itemStack);
					event.callEvent();
					if (!event.isCancelled())
					{
						component = ItemStackComponent.itemStackComponent(event.getItemStack(), 1, Constant.THE_COLOR, false, player);
					}
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
				e.printStackTrace();
			}
		}
		ClickEvent clickEvent = component.clickEvent();
		// 컴포넌트의 ClickEvent, Insertion 관련 데이터가 있을 때 새로 추가할 hoverEvent 메시지(기존에 hoverEvent가 없는 경우)
		Component hover = Component.empty();
		if (clickEvent != null)
		{
			Action action = clickEvent.action();
			Payload payload = clickEvent.payload();
			String value = resolvePayload(payload);
			if (action == Action.OPEN_URL)
			{
				if (hoverEvent == null)
				{
					hover = ComponentUtil.translate("클릭하여 %s 주소로 연결합니다", Component.text(value, component.color() != null ? component.color() : Constant.THE_COLOR));
					component = component.decoration(TextDecoration.UNDERLINED, State.TRUE);
				}
			}
			else
			{
				if (hoverEvent == null && UserData.SHOW_CLICK_EVENT_INFORMATION_ON_CHAT_IF_NULL.getBoolean(player))
				{
					hover = ComponentUtil.translate("클릭 이벤트 유형 : %s", Constant.THE_COLOR_HEX + action);
					hover = hover.append(Component.text("\n"));
					hover = hover.append(ComponentUtil.translate("값 : %s", Constant.THE_COLOR_HEX + value));
				}
			}
		}
		String insertion = component.insertion();
		if (insertion != null && hoverEvent == null)
		{
			if (!hover.equals(Component.empty()))
			{
				hover = hover.append(Component.text("\n"));
				hover = hover.append(Component.text("\n"));
			}
			hover = hover.append(ComponentUtil.translate("insertion 텍스트 : %s", Constant.THE_COLOR_HEX + insertion));
		}
		if (!hover.equals(Component.empty()))
		{
			if (player.hasPermission("asdf"))
			{
				hover = hover.append(Component.text("\n"));
				hover = hover.append(Component.text("\n"));
				hover = hover.append(ComponentUtil.translate("&8&oCucumbery에 의해 HoverEvent 추가됨"));
			}
			component = component.hoverEvent(hover);
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
				translatableComponent = translatableComponent.key(key);
			}

			List<ComponentLike> translationArguments = new ArrayList<>(translatableComponent.arguments());
			List<Component> arguments = new ArrayList<>();
			for (ComponentLike componentLike : translationArguments)
			{
				Component argument = componentLike.asComponent();
				// 인수에 숫자가 있는데 만약 컴포넌트 색이 없으면 숫자의 색깔을 THE COLOR로 / 아닐 경우 색깔을 바꾸지 않음
				if (changeArgumentColor && argument instanceof TextComponent textComponent && textComponent.color() == null)
				{
					String content = textComponent.content();
					try
					{
						// 1,200 같이 구분자 있는 숫자도 숫자로 판별
						Double.parseDouble(content.replace(",", ""));
						// 채팅 메시지에 숫자를 입력한 것은 색깔 변경하지 않음
						// 일부 메시지는 색깔 변경하지 않음
						switch (key)
						{
							case "chat.type.text", "chat.type.team.sent", "chat.type.team.text", "chat.type.emote", "chat.coordinates" -> throw new Exception();
						}
						// key에 색상이 있는 메시지는 색깔 변경하지 않음
						if (component.color() != null)
							throw new Exception();
						// Bukkit.getConsoleSender().sendMessage(key + ", color: " + component.color());
						argument = argument.color(Constant.THE_COLOR);
					}
					catch (Exception ignored)
					{
					}
				}

				argument = parse(player, isAdminMessage, changeArgumentColor, argument, root);
				// 관리자 명령어 메시지는 회색 기울임꼴이므로 인수의 색깔, 기울임 decoration을 제거한다.
				if (isAdminMessage)
				{
					argument = argument.color(null).decoration(TextDecoration.ITALIC, State.NOT_SET);
				}
				arguments.add(argument);
				translatableComponent = translatableComponent.arguments(arguments);
			}
			// 시스템 메시지를 보는 플레이어의 언어가 한국어일 경우 조사(을/를, 은/는 등) 적절하게 조정
			if (player.locale().equals(Locale.KOREA))
			{
				String translationKey = ComponentUtil.convertConsonant(translatableComponent.key(), translatableComponent).key();
				// 만약 translationKey가 변형되었다면 fallback은 null로 설정
				if (!translationKey.equals(translatableComponent.key()))
				{
					translatableComponent = translatableComponent.fallback(null);
				}
				translatableComponent = translatableComponent.key(translationKey);
			}
			translatableComponent = translatableComponent.arguments(arguments);
			component = translatableComponent;
		}
		if (!component.children().isEmpty())
		{
			List<Component> newList = new ArrayList<>();
			for (int i = 0; i < component.children().size(); i++)
			{
				newList.add(parse(player, isAdminMessage, changeArgumentColor, component.children().get(i), root));
			}
			component = component.children(newList);
		}
		return component;
	}

	private static String resolvePayload(Payload payload)
	{
		if (payload == null)
		{
			throw new NullPointerException("payload null");
		}
		switch (payload)
		{
			case Text text ->
			{
				return text.value();
			}
			case Int i ->
			{
				return String.valueOf(i.integer());
			}
			case Dialog dialog ->
			{
				DialogLike dialogLike = dialog.dialog();
				return dialogLike.toString();
			}
			case Custom custom ->
			{
				BinaryTagHolder nbt = custom.nbt();
				return nbt.string();
			}
			default -> throw new UnsupportedOperationException("cannot resolve payload with class: " + payload.getClass());
		}
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

		if (Objects.equals(NBT.get(itemStack, nbt ->
		{
			return nbt.getBoolean(CucumberyTag.INTERNAL_DO_NOT_SET_ITEM_LORE);
		}), Boolean.TRUE))
		{
			return itemStack;
		}

		// GUI에서 설명이 없는 아이템(배경)을 더블클릭할 경우 서로 겹쳐서 클릭 이벤트 반복 호출 -> 서버 랙 유발 방지용 랜덤 nbt 패킷 전송
		{
			if (itemStack.getItemMeta().isHideTooltip() && !itemStack.getItemMeta().hasLore())
			{
				NBT.modify(itemStack, nbt ->
				{
					nbt.setString("Obfuscated", UUID.randomUUID().toString());
				});
				return itemStack;
			}
		}

		if (CustomEffectManager.hasEffect(player, CustomEffectType.HIDE_ITEM_INFORMATION))
		{
			ItemStack hiddenItem = ItemStackUtil.HIDDEN_ITEM.clone();
			hiddenItem.setAmount(itemStack.getAmount());
			return hiddenItem;
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

		if (!useLoreInCreative && !lore.isEmpty() && lore.getFirst() instanceof TranslatableComponent translatableComponent && translatableComponent.key()
				.isEmpty())
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
					itemMeta.removeItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
				}
			}
		}

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

		//		com.jho5245.cucumbery.util.storage.data.CustomMaterial customMaterial = com.jho5245.cucumbery.util.storage.data.CustomMaterial.itemStackOf(clone);
		//		if (customMaterial != null)
		//		{
		//			itemMeta.itemName(customMaterial.getDisplayName());
		//		}
		CustomMaterial customMaterial = CustomMaterial.itemStackOf(clone);
		//
		//		if (ignoreCreativeWhat && customMaterial != null)
		//		{
		//			switch (customMaterial)
		//			{
		//				case BRICK_THROWABLE -> clone.setType(Material.BRICK);
		//				case NETHER_BRICK_THROWABLE -> clone.setType(Material.NETHER_BRICK);
		//				case COPPER_INGOT_THROWABLE -> clone.setType(Material.COPPER_INGOT);
		//				case IRON_INGOT_THROWABLE -> clone.setType(Material.IRON_INGOT);
		//				case GOLD_INGOT_THROWABLE -> clone.setType(Material.GOLD_INGOT);
		//				case NETHERITE_INGOT_THROWABLE -> clone.setType(Material.NETHERITE_INGOT);
		//			}
		//		}

		if (!itemMeta.hasAttributeModifiers())
		{
			Material type = clone.getType();
			Multimap<Attribute, AttributeModifier> attributeModifierMultimap = type.getDefaultAttributeModifiers();
			if (!attributeModifierMultimap.isEmpty())
			{
				if (showItemLore)
					itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				for (Attribute attribute : attributeModifierMultimap.keySet())
				{
					for (AttributeModifier modifier : attributeModifierMultimap.get(attribute))
					{
						AttributeModifier newModifier = new AttributeModifier(new NamespacedKey(Cucumbery.getPlugin(), "protocollib_random_number_" + Math.random()),
								modifier.getAmount(), modifier.getOperation(), modifier.getSlotGroup());
						itemMeta.addAttributeModifier(attribute, newModifier);
					}
				}
			}
		}

		// 일부 인챈트는 Attribute 설명을 추가하므로 클라이언트에서 해당 인챈트를 제거하여 보냄
		if (showItemLore && ignoreCreativeWhat && itemMeta.hasEnchants())
		{
			itemMeta.removeEnchant(Enchantment.EFFICIENCY);
			itemMeta.removeEnchant(Enchantment.FIRE_PROTECTION);
			itemMeta.removeEnchant(Enchantment.BLAST_PROTECTION);
			itemMeta.removeEnchant(Enchantment.DEPTH_STRIDER);
			itemMeta.removeEnchant(Enchantment.RESPIRATION);
			itemMeta.removeEnchant(Enchantment.AQUA_AFFINITY);
			itemMeta.removeEnchant(Enchantment.SWEEPING_EDGE);
			itemMeta.removeEnchant(Enchantment.SWIFT_SNEAK);

			if (!itemMeta.hasEnchants())
			{
				itemMeta.setEnchantmentGlintOverride(true);
			}
		}

		if (!useLoreInCreative)
		{
			clone.setItemMeta(itemMeta);
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
					//					if (overrideItemStackString.startsWith("custom_material_"))
					//						overrideItemStack = com.jho5245.cucumbery.util.storage.data.CustomMaterial.valueOf(overrideItemStackString.substring("custom_material_".length()))
					//								.create();
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
				originMeta.itemName(originDisplayname);
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
		if (lore == null)
			lore = new ArrayList<>();

		if (showItemLore && UserData.SHOW_ITEM_COMPONENTS_INFO.getBoolean(player))
		{
			List<Component> componentLore = new ArrayList<>();
			NBT.getComponents(clone, nbt ->
			{
				for (String key : nbt.getKeys())
				{
					if (!nbt.hasTag(key))
						continue;
					NBTType nbtType = nbt.getType(key);
					String value = String.valueOf(get(key, nbt, nbtType));
					String skipped = "";
					if (value.length() > 60)
					{
						skipped = " .. 외 %s글자 더..".formatted(value.length() - 50);
						value = value.substring(0, 50);
					}
					componentLore.add(ComponentUtil.translate("&f%s : %s%s - %s", "&b" + key, "&a" + value, "&7" + skipped, "&6" + nbtType));
				}
			});
			if (!componentLore.isEmpty())
			{
				lore.add(Component.empty());
				lore.add(ComponentUtil.translate("&b[아이템 컴포넌트 정보] [구성 요소 %s개]", componentLore.size()));
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
			materials.removeIf(material -> material.isAir() || !material.isItem());
			clone.setType(materials.get((int) (Math.random() * materials.size())));
		}

		// 혼벤토리 효과로 아이템이 공기가 될 경우
		if (!ItemStackUtil.itemExists(clone))
		{
			return clone;
		}

		// 플레이어 언어가 한국어일 경우 lore 조사 수정
		if (player.locale().equals(Locale.KOREA))
		{
			itemMeta = clone.getItemMeta();
			lore = itemMeta.lore();
			if (lore != null)
			{
				for (int i = 0; i < lore.size(); i++)
				{
					Component changedLore = parse(player, false, false, lore.get(i), lore.get(i));
					lore.set(i, changedLore);
				}
			}
			itemMeta.lore(lore);
			clone.setItemMeta(itemMeta);
		}

		if (player.getGameMode() != GameMode.CREATIVE && !player.hasPermission("asdf"))
		{
			// 관리자가 아닐 경우 Material이 DEBUG_STICK이고 ItemModel이 있는 CustomMaterial들을 전부 Material <= ItemModel로 변경
			//			if (customMaterial != null && clone.getType() == Material.DEBUG_STICK)
			//			{
			//				ItemMeta cloneMeta = clone.getItemMeta();
			//				if (cloneMeta.hasItemModel())
			//				{
			//					NamespacedKey itemModel = cloneMeta.getItemModel();
			//					Material material = Method2.valueOf(itemModel != null ? itemModel.getKey().toUpperCase() : "", Material.class);
			//					if (material != null)
			//					{
			//						cloneMeta.setItemModel(null);
			//						clone.setItemMeta(cloneMeta);
			//						clone.setType(material);
			//					}
			//				}
			//			}
			if (customMaterial != null && (customMaterial.getRealMaterial() == Material.DEBUG_STICK
					|| customMaterial.getRealMaterial() != Material.PLAYER_HEAD && customMaterial.getDisplayMaterial() == Material.PLAYER_HEAD))
			{
				ItemMeta cloneMeta = clone.getItemMeta();
				if (cloneMeta.hasItemModel())
				{
					NamespacedKey itemModel = cloneMeta.getItemModel();
					Material material = Method2.valueOf(itemModel != null ? itemModel.getKey().toUpperCase() : "", Material.class);
					if (material == customMaterial.getDisplayMaterial() && material != null)
					{
						cloneMeta.setItemModel(null);
						clone.setItemMeta(cloneMeta);
						clone.setType(material);
					}
				}
			}
			NBT.modify(clone, nbt ->
			{
				nbt.getKeys().forEach(key ->
				{
					if (!key.equals(CustomMaterial.IDENDTIFER))
						nbt.removeKey(key);
				});
			});
		}

		return clone;
	}

	private static final PacketType[] ITEM_TYPES = {
			Server.WINDOW_ITEMS,
			Server.OPEN_WINDOW_MERCHANT,
			Server.SET_SLOT,
			Server.SET_CURSOR_ITEM,
			};

	private static Object get(String key, ReadableNBT nbt, NBTType nbtType)
	{
		Object o = nbt.get(key, NBTHandlers.STORE_READWRITE_TAG);
		if (o == null)
		{
			o = nbt.get(key, NBTHandlers.STORE_READABLE_TAG);
		}
		if (o == null)
		{
			o = nbt.get(key, NBTHandlers.ITEM_STACK);
		}
		if (o == null)
			switch (nbtType)
			{
				case NBTTagByte -> o = nbt.getByte(key);
				case NBTTagShort -> o = nbt.getShort(key);
				case NBTTagInt -> o = nbt.getInteger(key);
				case NBTTagLong -> o = nbt.getLong(key);
				case NBTTagFloat -> o = nbt.getFloat(key);
				case NBTTagDouble -> o = nbt.getDouble(key);
				case NBTTagByteArray -> o = nbt.getByte(key);
				case NBTTagString -> o = nbt.getString(key);
				case NBTTagList ->
				{
					o = nbt.getCompoundList(key);
					NBTType listType = nbt.getListType(key);
					switch (listType)
					{
						case NBTTagInt -> o = nbt.getIntegerList(key);
						case NBTTagLong -> o = nbt.getLongList(key);
						case NBTTagFloat -> o = nbt.getFloatList(key);
						case NBTTagDouble -> o = nbt.getDoubleList(key);
						case NBTTagString -> o = nbt.getStringList(key);
						case NBTTagCompound -> o = nbt.getCompoundList(key);
						case NBTTagIntArray -> o = nbt.getIntArrayList(key);
						case null, default ->
						{
						}
					}
				}
				case NBTTagCompound -> o = nbt.getCompound(key);
				case NBTTagIntArray -> o = nbt.getIntArray(key);
				case NBTTagLongArray -> o = nbt.getLongArray(key);
			}
		return o;
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
