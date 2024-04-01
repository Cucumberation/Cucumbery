package com.jho5245.cucumbery.custom.recipe;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.util.storage.data.CustomMaterial;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class RecipeManager
{
  public static Set<NamespacedKey> recipes = new HashSet<>();

  public static void registerRecipe()
  {
    register(new FurnaceRecipe(of("tungsten_ingot_from_furnace"), CustomMaterial.TUNGSTEN_INGOT.create(), new ExactChoice(CustomMaterial.TUNGSTEN_ORE.create()), 0.7f, 200));
    register(new BlastingRecipe(of("tungsten_ingot_from_blast_furnace"), CustomMaterial.TUNGSTEN_INGOT.create(), new ExactChoice(CustomMaterial.TUNGSTEN_ORE.create()), 0.7f, 100));

    register(new FurnaceRecipe(of("cobalt_ingot_from_furnace"), CustomMaterial.COBALT_INGOT.create(), new ExactChoice(CustomMaterial.COBALT_ORE.create()), 1f, 400));
    register(new BlastingRecipe(of("cobalt_ingot_from_blast_furnace"), CustomMaterial.COBALT_INGOT.create(), new ExactChoice(CustomMaterial.COBALT_ORE.create()), 1f, 200));

    register(new FurnaceRecipe(of("titanium_ingot_from_furnace"), CustomMaterial.TITANIUM_INGOT.create(), new ExactChoice(CustomMaterial.TITANIUM_ORE.create()), 1f, 400));
    register(new BlastingRecipe(of("titanium_ingot_from_blast_furnace"), CustomMaterial.TITANIUM_INGOT.create(), new ExactChoice(CustomMaterial.TITANIUM_ORE.create()), 1f, 200));

    register(new FurnaceRecipe(of("shroomite_ingot_from_furnace"), CustomMaterial.SHROOMITE_INGOT.create(), new ExactChoice(CustomMaterial.SHROOMITE_ORE.create()), 1f, 400));
    register(new BlastingRecipe(of("shroomite_ingot_from_blast_furnace"), CustomMaterial.SHROOMITE_INGOT.create(), new ExactChoice(CustomMaterial.SHROOMITE_ORE.create()), 1f, 200));

    register(new FurnaceRecipe(of("cucumberite_ingot_from_furnace"), CustomMaterial.CUCUMBERITE_INGOT.create(), new ExactChoice(CustomMaterial.CUCUMBERITE_ORE.create()), 1f, 400));
    register(new BlastingRecipe(of("cucumberite_ingot_from_blast_furnace"), CustomMaterial.CUCUMBERITE_INGOT.create(), new ExactChoice(CustomMaterial.CUCUMBERITE_ORE.create()), 1f, 200));

    register(new FurnaceRecipe(of("tnt_from_furnace"), new ItemStack(Material.TNT), new ExactChoice(CustomMaterial.WNYNYA_ORE.create()), 2f, 100));
    register(new BlastingRecipe(of("tnt_from_blast_furnace"), new ItemStack(Material.TNT), new ExactChoice(CustomMaterial.WNYNYA_ORE.create()), 2f, 50));

    registerVerticalSlabs();

    register(new PotionMix(of("test_jade_to_tnt"), new ItemStack(Material.TNT), INPUT_WATER_BOTTLE,
        PotionMix.createPredicateChoice(itemStack -> isAdminOnline() && CustomMaterial.itemStackOf(itemStack) == CustomMaterial.JADE)));

    register(new PotionMix(of("test_enchanted_jade_to_diamond"), new ItemStack(Material.STONE), INPUT_WATER_BOTTLE,
        PotionMix.createPredicateChoice(itemStack -> isAdminOnline() && CustomMaterial.itemStackOf(itemStack) == CustomMaterial.JADE && itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchants())));

    register(new PotionMix(of("test_enchanted_ruby_to_stone"), new ItemStack(Material.STONE), INPUT_WATER_BOTTLE,
        PotionMix.createPredicateChoice(itemStack -> isAdminOnline() && CustomMaterial.itemStackOf(itemStack) == CustomMaterial.RUBY && itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchants())));

    register(new PotionMix(of("test_amber_to_tnt"), new ItemStack(Material.TNT), INPUT_WATER_BOTTLE,
        PotionMix.createPredicateChoice(itemStack -> isAdminOnline() && CustomMaterial.itemStackOf(itemStack) == CustomMaterial.AMBER && Bukkit.getOnlinePlayers().size() > 1)));
  }

  private static void registerVerticalSlabs()
  {
    ShapelessRecipe oak_slab_from_oak_slab_vertical = new ShapelessRecipe(of("oak_slab_from_oak_slab_vertical"), new ItemStack(Material.OAK_SLAB)).addIngredient(new ExactChoice(CustomMaterial.OAK_SLAB_VERTICAL.create()));
    oak_slab_from_oak_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    oak_slab_from_oak_slab_vertical.setGroup("slab_from_slab_vertical");
    register(oak_slab_from_oak_slab_vertical);

    ShapelessRecipe oak_slab_vertical_from_oak_slab = new ShapelessRecipe(of("oak_slab_vertical_from_oak_slab"), CustomMaterial.OAK_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.OAK_SLAB)));
    oak_slab_vertical_from_oak_slab.setCategory(CraftingBookCategory.BUILDING);
    oak_slab_vertical_from_oak_slab.setGroup("slab_vertical");
    register(oak_slab_vertical_from_oak_slab);

    ShapedRecipe oak_slab_vertical = new ShapedRecipe(of("oak_slab_vertical"), CustomMaterial.OAK_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.OAK_PLANKS);
    oak_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    oak_slab_vertical.setGroup("slab_vertical");
    register(oak_slab_vertical);



    ShapelessRecipe spruce_slab_from_spruce_slab_vertical = new ShapelessRecipe(of("spruce_slab_from_spruce_slab_vertical"), new ItemStack(Material.SPRUCE_SLAB)).addIngredient(new ExactChoice(CustomMaterial.SPRUCE_SLAB_VERTICAL.create()));
    spruce_slab_from_spruce_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    spruce_slab_from_spruce_slab_vertical.setGroup("slab_from_slab_vertical");
    register(spruce_slab_from_spruce_slab_vertical);

    ShapelessRecipe spruce_slab_vertical_from_spruce_slab = new ShapelessRecipe(of("spruce_slab_vertical_from_spruce_slab"), CustomMaterial.SPRUCE_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.SPRUCE_SLAB)));
    spruce_slab_vertical_from_spruce_slab.setCategory(CraftingBookCategory.BUILDING);
    spruce_slab_vertical_from_spruce_slab.setGroup("slab_vertical");
    register(spruce_slab_vertical_from_spruce_slab);

    ShapedRecipe spruce_slab_vertical = new ShapedRecipe(of("spruce_slab_vertical"), CustomMaterial.SPRUCE_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.SPRUCE_PLANKS);
    spruce_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    spruce_slab_vertical.setGroup("slab_vertical");
    register(spruce_slab_vertical);



    ShapelessRecipe birch_slab_from_birch_slab_vertical = new ShapelessRecipe(of("birch_slab_from_birch_slab_vertical"), new ItemStack(Material.BIRCH_SLAB)).addIngredient(new ExactChoice(CustomMaterial.BIRCH_SLAB_VERTICAL.create()));
    birch_slab_from_birch_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    birch_slab_from_birch_slab_vertical.setGroup("slab_from_slab_vertical");
    register(birch_slab_from_birch_slab_vertical);

    ShapelessRecipe birch_slab_vertical_from_birch_slab = new ShapelessRecipe(of("birch_slab_vertical_from_birch_slab"), CustomMaterial.BIRCH_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.BIRCH_SLAB)));
    birch_slab_vertical_from_birch_slab.setCategory(CraftingBookCategory.BUILDING);
    birch_slab_vertical_from_birch_slab.setGroup("slab_vertical");
    register(birch_slab_vertical_from_birch_slab);

    ShapedRecipe birch_slab_vertical = new ShapedRecipe(of("birch_slab_vertical"), CustomMaterial.BIRCH_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.BIRCH_PLANKS);
    birch_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    birch_slab_vertical.setGroup("slab_vertical");
    register(birch_slab_vertical);



    ShapelessRecipe jungle_slab_from_jungle_slab_vertical = new ShapelessRecipe(of("jungle_slab_from_jungle_slab_vertical"), new ItemStack(Material.JUNGLE_SLAB)).addIngredient(new ExactChoice(CustomMaterial.JUNGLE_SLAB_VERTICAL.create()));
    jungle_slab_from_jungle_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    jungle_slab_from_jungle_slab_vertical.setGroup("slab_from_slab_vertical");
    register(jungle_slab_from_jungle_slab_vertical);

    ShapelessRecipe jungle_slab_vertical_from_jungle_slab = new ShapelessRecipe(of("jungle_slab_vertical_from_jungle_slab"), CustomMaterial.JUNGLE_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.JUNGLE_SLAB)));
    jungle_slab_vertical_from_jungle_slab.setCategory(CraftingBookCategory.BUILDING);
    jungle_slab_vertical_from_jungle_slab.setGroup("slab_vertical");
    register(jungle_slab_vertical_from_jungle_slab);

    ShapedRecipe jungle_slab_vertical = new ShapedRecipe(of("jungle_slab_vertical"), CustomMaterial.JUNGLE_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.JUNGLE_PLANKS);
    jungle_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    jungle_slab_vertical.setGroup("slab_vertical");
    register(jungle_slab_vertical);



    ShapelessRecipe dark_oak_slab_from_dark_oak_slab_vertical = new ShapelessRecipe(of("dark_oak_slab_from_dark_oak_slab_vertical"), new ItemStack(Material.DARK_OAK_SLAB)).addIngredient(new ExactChoice(CustomMaterial.DARK_OAK_SLAB_VERTICAL.create()));
    dark_oak_slab_from_dark_oak_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    dark_oak_slab_from_dark_oak_slab_vertical.setGroup("slab_from_slab_vertical");
    register(dark_oak_slab_from_dark_oak_slab_vertical);

    ShapelessRecipe dark_oak_slab_vertical_from_dark_oak_slab = new ShapelessRecipe(of("dark_oak_slab_vertical_from_dark_oak_slab"), CustomMaterial.DARK_OAK_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.DARK_OAK_SLAB)));
    dark_oak_slab_vertical_from_dark_oak_slab.setCategory(CraftingBookCategory.BUILDING);
    dark_oak_slab_vertical_from_dark_oak_slab.setGroup("slab_vertical");
    register(dark_oak_slab_vertical_from_dark_oak_slab);

    ShapedRecipe dark_oak_slab_vertical = new ShapedRecipe(of("dark_oak_slab_vertical"), CustomMaterial.DARK_OAK_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.DARK_OAK_PLANKS);
    dark_oak_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    dark_oak_slab_vertical.setGroup("slab_vertical");
    register(dark_oak_slab_vertical);



    ShapelessRecipe acacia_slab_from_acacia_slab_vertical = new ShapelessRecipe(of("acacia_slab_from_acacia_slab_vertical"), new ItemStack(Material.ACACIA_SLAB)).addIngredient(new ExactChoice(CustomMaterial.ACACIA_SLAB_VERTICAL.create()));
    acacia_slab_from_acacia_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    acacia_slab_from_acacia_slab_vertical.setGroup("slab_from_slab_vertical");
    register(acacia_slab_from_acacia_slab_vertical);

    ShapelessRecipe acacia_slab_vertical_from_acacia_slab = new ShapelessRecipe(of("acacia_slab_vertical_from_acacia_slab"), CustomMaterial.ACACIA_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.ACACIA_SLAB)));
    acacia_slab_vertical_from_acacia_slab.setCategory(CraftingBookCategory.BUILDING);
    acacia_slab_vertical_from_acacia_slab.setGroup("slab_vertical");
    register(acacia_slab_vertical_from_acacia_slab);

    ShapedRecipe acacia_slab_vertical = new ShapedRecipe(of("acacia_slab_vertical"), CustomMaterial.ACACIA_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.ACACIA_PLANKS);
    acacia_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    acacia_slab_vertical.setGroup("slab_vertical");
    register(acacia_slab_vertical);



    ShapelessRecipe mangrove_slab_from_mangrove_slab_vertical = new ShapelessRecipe(of("mangrove_slab_from_mangrove_slab_vertical"), new ItemStack(Material.MANGROVE_SLAB)).addIngredient(new ExactChoice(CustomMaterial.MANGROVE_SLAB_VERTICAL.create()));
    mangrove_slab_from_mangrove_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    mangrove_slab_from_mangrove_slab_vertical.setGroup("slab_from_slab_vertical");
    register(mangrove_slab_from_mangrove_slab_vertical);

    ShapelessRecipe mangrove_slab_vertical_from_mangrove_slab = new ShapelessRecipe(of("mangrove_slab_vertical_from_mangrove_slab"), CustomMaterial.MANGROVE_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.MANGROVE_SLAB)));
    mangrove_slab_vertical_from_mangrove_slab.setCategory(CraftingBookCategory.BUILDING);
    mangrove_slab_vertical_from_mangrove_slab.setGroup("slab_vertical");
    register(mangrove_slab_vertical_from_mangrove_slab);

    ShapedRecipe mangrove_slab_vertical = new ShapedRecipe(of("mangrove_slab_vertical"), CustomMaterial.MANGROVE_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.MANGROVE_PLANKS);
    mangrove_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    mangrove_slab_vertical.setGroup("slab_vertical");
    register(mangrove_slab_vertical);



    ShapelessRecipe cherry_slab_from_cherry_slab_vertical = new ShapelessRecipe(of("cherry_slab_from_cherry_slab_vertical"), new ItemStack(Material.CHERRY_SLAB)).addIngredient(new ExactChoice(CustomMaterial.CHERRY_SLAB_VERTICAL.create()));
    cherry_slab_from_cherry_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    cherry_slab_from_cherry_slab_vertical.setGroup("slab_from_slab_vertical");
    register(cherry_slab_from_cherry_slab_vertical);

    ShapelessRecipe cherry_slab_vertical_from_cherry_slab = new ShapelessRecipe(of("cherry_slab_vertical_from_cherry_slab"), CustomMaterial.CHERRY_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.CHERRY_SLAB)));
    cherry_slab_vertical_from_cherry_slab.setCategory(CraftingBookCategory.BUILDING);
    cherry_slab_vertical_from_cherry_slab.setGroup("slab_vertical");
    register(cherry_slab_vertical_from_cherry_slab);

    ShapedRecipe cherry_slab_vertical = new ShapedRecipe(of("cherry_slab_vertical"), CustomMaterial.CHERRY_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.CHERRY_PLANKS);
    cherry_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    cherry_slab_vertical.setGroup("slab_vertical");
    register(cherry_slab_vertical);



    ShapelessRecipe bamboo_slab_from_bamboo_slab_vertical = new ShapelessRecipe(of("bamboo_slab_from_bamboo_slab_vertical"), new ItemStack(Material.BAMBOO_SLAB)).addIngredient(new ExactChoice(CustomMaterial.BAMBOO_SLAB_VERTICAL.create()));
    bamboo_slab_from_bamboo_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    bamboo_slab_from_bamboo_slab_vertical.setGroup("slab_from_slab_vertical");
    register(bamboo_slab_from_bamboo_slab_vertical);

    ShapelessRecipe bamboo_slab_vertical_from_bamboo_slab = new ShapelessRecipe(of("bamboo_slab_vertical_from_bamboo_slab"), CustomMaterial.BAMBOO_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.BAMBOO_SLAB)));
    bamboo_slab_vertical_from_bamboo_slab.setCategory(CraftingBookCategory.BUILDING);
    bamboo_slab_vertical_from_bamboo_slab.setGroup("slab_vertical");
    register(bamboo_slab_vertical_from_bamboo_slab);

    ShapedRecipe bamboo_slab_vertical = new ShapedRecipe(of("bamboo_slab_vertical"), CustomMaterial.BAMBOO_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.BAMBOO_PLANKS);
    bamboo_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    bamboo_slab_vertical.setGroup("slab_vertical");
    register(bamboo_slab_vertical);



    ShapelessRecipe bamboo_mosaic_slab_from_bamboo_mosaic_slab_vertical = new ShapelessRecipe(of("bamboo_mosaic_slab_from_bamboo_mosaic_slab_vertical"), new ItemStack(Material.BAMBOO_MOSAIC_SLAB)).addIngredient(new ExactChoice(CustomMaterial.BAMBOO_MOSAIC_SLAB_VERTICAL.create()));
    bamboo_mosaic_slab_from_bamboo_mosaic_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    bamboo_mosaic_slab_from_bamboo_mosaic_slab_vertical.setGroup("slab_from_slab_vertical");
    register(bamboo_mosaic_slab_from_bamboo_mosaic_slab_vertical);

    ShapelessRecipe bamboo_mosaic_slab_vertical_from_bamboo_mosaic_slab = new ShapelessRecipe(of("bamboo_mosaic_slab_vertical_from_bamboo_mosaic_slab"), CustomMaterial.BAMBOO_MOSAIC_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.BAMBOO_MOSAIC_SLAB)));
    bamboo_mosaic_slab_vertical_from_bamboo_mosaic_slab.setCategory(CraftingBookCategory.BUILDING);
    bamboo_mosaic_slab_vertical_from_bamboo_mosaic_slab.setGroup("slab_vertical");
    register(bamboo_mosaic_slab_vertical_from_bamboo_mosaic_slab);

    ShapedRecipe bamboo_mosaic_slab_vertical = new ShapedRecipe(of("bamboo_mosaic_slab_vertical"), CustomMaterial.BAMBOO_MOSAIC_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.BAMBOO_MOSAIC);
    bamboo_mosaic_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    bamboo_mosaic_slab_vertical.setGroup("slab_vertical");
    register(bamboo_mosaic_slab_vertical);



    ShapelessRecipe crimson_slab_from_crimson_slab_vertical = new ShapelessRecipe(of("crimson_slab_from_crimson_slab_vertical"), new ItemStack(Material.CRIMSON_SLAB)).addIngredient(new ExactChoice(CustomMaterial.CRIMSON_SLAB_VERTICAL.create()));
    crimson_slab_from_crimson_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    crimson_slab_from_crimson_slab_vertical.setGroup("slab_from_slab_vertical");
    register(crimson_slab_from_crimson_slab_vertical);

    ShapelessRecipe crimson_slab_vertical_from_crimson_slab = new ShapelessRecipe(of("crimson_slab_vertical_from_crimson_slab"), CustomMaterial.CRIMSON_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.CRIMSON_SLAB)));
    crimson_slab_vertical_from_crimson_slab.setCategory(CraftingBookCategory.BUILDING);
    crimson_slab_vertical_from_crimson_slab.setGroup("slab_vertical");
    register(crimson_slab_vertical_from_crimson_slab);

    ShapedRecipe crimson_slab_vertical = new ShapedRecipe(of("crimson_slab_vertical"), CustomMaterial.CRIMSON_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.CRIMSON_PLANKS);
    crimson_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    crimson_slab_vertical.setGroup("slab_vertical");
    register(crimson_slab_vertical);



    ShapelessRecipe warped_slab_from_warped_slab_vertical = new ShapelessRecipe(of("warped_slab_from_warped_slab_vertical"), new ItemStack(Material.WARPED_SLAB)).addIngredient(new ExactChoice(CustomMaterial.WARPED_SLAB_VERTICAL.create()));
    warped_slab_from_warped_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    warped_slab_from_warped_slab_vertical.setGroup("slab_from_slab_vertical");
    register(warped_slab_from_warped_slab_vertical);

    ShapelessRecipe warped_slab_vertical_from_warped_slab = new ShapelessRecipe(of("warped_slab_vertical_from_warped_slab"), CustomMaterial.WARPED_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.WARPED_SLAB)));
    warped_slab_vertical_from_warped_slab.setCategory(CraftingBookCategory.BUILDING);
    warped_slab_vertical_from_warped_slab.setGroup("slab_vertical");
    register(warped_slab_vertical_from_warped_slab);

    ShapedRecipe warped_slab_vertical = new ShapedRecipe(of("warped_slab_vertical"), CustomMaterial.WARPED_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.WARPED_PLANKS);
    warped_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    warped_slab_vertical.setGroup("slab_vertical");
    register(warped_slab_vertical);



    ShapelessRecipe stone_slab_from_stone_slab_vertical = new ShapelessRecipe(of("stone_slab_from_stone_slab_vertical"), new ItemStack(Material.STONE_SLAB)).addIngredient(new ExactChoice(CustomMaterial.STONE_SLAB_VERTICAL.create()));
    stone_slab_from_stone_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    stone_slab_from_stone_slab_vertical.setGroup("slab_from_slab_vertical");
    register(stone_slab_from_stone_slab_vertical);

    ShapelessRecipe stone_slab_vertical_from_stone_slab = new ShapelessRecipe(of("stone_slab_vertical_from_stone_slab"), CustomMaterial.STONE_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.STONE_SLAB)));
    stone_slab_vertical_from_stone_slab.setCategory(CraftingBookCategory.BUILDING);
    stone_slab_vertical_from_stone_slab.setGroup("slab_vertical");
    register(stone_slab_vertical_from_stone_slab);

    ShapedRecipe stone_slab_vertical = new ShapedRecipe(of("stone_slab_vertical"), CustomMaterial.STONE_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.STONE);
    stone_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    stone_slab_vertical.setGroup("slab_vertical");
    register(stone_slab_vertical);



    ShapelessRecipe cobblestone_slab_from_cobblestone_slab_vertical = new ShapelessRecipe(of("cobblestone_slab_from_cobblestone_slab_vertical"), new ItemStack(Material.COBBLESTONE_SLAB)).addIngredient(new ExactChoice(CustomMaterial.COBBLESTONE_SLAB_VERTICAL.create()));
    cobblestone_slab_from_cobblestone_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    cobblestone_slab_from_cobblestone_slab_vertical.setGroup("slab_from_slab_vertical");
    register(cobblestone_slab_from_cobblestone_slab_vertical);

    ShapelessRecipe cobblestone_slab_vertical_from_cobblestone_slab = new ShapelessRecipe(of("cobblestone_slab_vertical_from_cobblestone_slab"), CustomMaterial.COBBLESTONE_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.COBBLESTONE_SLAB)));
    cobblestone_slab_vertical_from_cobblestone_slab.setCategory(CraftingBookCategory.BUILDING);
    cobblestone_slab_vertical_from_cobblestone_slab.setGroup("slab_vertical");
    register(cobblestone_slab_vertical_from_cobblestone_slab);

    ShapedRecipe cobblestone_slab_vertical = new ShapedRecipe(of("cobblestone_slab_vertical"), CustomMaterial.COBBLESTONE_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.COBBLESTONE);
    cobblestone_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    cobblestone_slab_vertical.setGroup("slab_vertical");
    register(cobblestone_slab_vertical);



    ShapelessRecipe mossy_cobblestone_slab_from_mossy_cobblestone_slab_vertical = new ShapelessRecipe(of("mossy_cobblestone_slab_from_mossy_cobblestone_slab_vertical"), new ItemStack(Material.MOSSY_COBBLESTONE_SLAB)).addIngredient(new ExactChoice(CustomMaterial.MOSSY_COBBLESTONE_SLAB_VERTICAL.create()));
    mossy_cobblestone_slab_from_mossy_cobblestone_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    mossy_cobblestone_slab_from_mossy_cobblestone_slab_vertical.setGroup("slab_from_slab_vertical");
    register(mossy_cobblestone_slab_from_mossy_cobblestone_slab_vertical);

    ShapelessRecipe mossy_cobblestone_slab_vertical_from_mossy_cobblestone_slab = new ShapelessRecipe(of("mossy_cobblestone_slab_vertical_from_mossy_cobblestone_slab"), CustomMaterial.MOSSY_COBBLESTONE_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.MOSSY_COBBLESTONE_SLAB)));
    mossy_cobblestone_slab_vertical_from_mossy_cobblestone_slab.setCategory(CraftingBookCategory.BUILDING);
    mossy_cobblestone_slab_vertical_from_mossy_cobblestone_slab.setGroup("slab_vertical");
    register(mossy_cobblestone_slab_vertical_from_mossy_cobblestone_slab);

    ShapedRecipe mossy_cobblestone_slab_vertical = new ShapedRecipe(of("mossy_cobblestone_slab_vertical"), CustomMaterial.MOSSY_COBBLESTONE_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.MOSSY_COBBLESTONE);
    mossy_cobblestone_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    mossy_cobblestone_slab_vertical.setGroup("slab_vertical");
    register(mossy_cobblestone_slab_vertical);



    ShapelessRecipe smooth_stone_slab_from_smooth_stone_slab_vertical = new ShapelessRecipe(of("smooth_stone_slab_from_smooth_stone_slab_vertical"), new ItemStack(Material.SMOOTH_STONE_SLAB)).addIngredient(new ExactChoice(CustomMaterial.SMOOTH_STONE_SLAB_VERTICAL.create()));
    smooth_stone_slab_from_smooth_stone_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    smooth_stone_slab_from_smooth_stone_slab_vertical.setGroup("slab_from_slab_vertical");
    register(smooth_stone_slab_from_smooth_stone_slab_vertical);

    ShapelessRecipe smooth_stone_slab_vertical_from_smooth_stone_slab = new ShapelessRecipe(of("smooth_stone_slab_vertical_from_smooth_stone_slab"), CustomMaterial.SMOOTH_STONE_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.SMOOTH_STONE_SLAB)));
    smooth_stone_slab_vertical_from_smooth_stone_slab.setCategory(CraftingBookCategory.BUILDING);
    smooth_stone_slab_vertical_from_smooth_stone_slab.setGroup("slab_vertical");
    register(smooth_stone_slab_vertical_from_smooth_stone_slab);

    ShapedRecipe smooth_stone_slab_vertical = new ShapedRecipe(of("smooth_stone_slab_vertical"), CustomMaterial.SMOOTH_STONE_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.SMOOTH_STONE);
    smooth_stone_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    smooth_stone_slab_vertical.setGroup("slab_vertical");
    register(smooth_stone_slab_vertical);



    ShapelessRecipe stone_brick_slab_from_stone_brick_slab_vertical = new ShapelessRecipe(of("stone_brick_slab_from_stone_brick_slab_vertical"), new ItemStack(Material.STONE_BRICK_SLAB)).addIngredient(new ExactChoice(CustomMaterial.STONE_BRICK_SLAB_VERTICAL.create()));
    stone_brick_slab_from_stone_brick_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    stone_brick_slab_from_stone_brick_slab_vertical.setGroup("slab_from_slab_vertical");
    register(stone_brick_slab_from_stone_brick_slab_vertical);

    ShapelessRecipe stone_brick_slab_vertical_from_stone_brick_slab = new ShapelessRecipe(of("stone_brick_slab_vertical_from_stone_brick_slab"), CustomMaterial.STONE_BRICK_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.STONE_BRICK_SLAB)));
    stone_brick_slab_vertical_from_stone_brick_slab.setCategory(CraftingBookCategory.BUILDING);
    stone_brick_slab_vertical_from_stone_brick_slab.setGroup("slab_vertical");
    register(stone_brick_slab_vertical_from_stone_brick_slab);

    ShapedRecipe stone_brick_slab_vertical = new ShapedRecipe(of("stone_brick_slab_vertical"), CustomMaterial.STONE_BRICK_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.STONE_BRICKS);
    stone_brick_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    stone_brick_slab_vertical.setGroup("slab_vertical");
    register(stone_brick_slab_vertical);



    ShapelessRecipe mossy_stone_brick_slab_from_mossy_stone_brick_slab_vertical = new ShapelessRecipe(of("mossy_stone_brick_slab_from_mossy_stone_brick_slab_vertical"), new ItemStack(Material.MOSSY_STONE_BRICK_SLAB)).addIngredient(new ExactChoice(CustomMaterial.MOSSY_STONE_BRICK_SLAB_VERTICAL.create()));
    mossy_stone_brick_slab_from_mossy_stone_brick_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    mossy_stone_brick_slab_from_mossy_stone_brick_slab_vertical.setGroup("slab_from_slab_vertical");
    register(mossy_stone_brick_slab_from_mossy_stone_brick_slab_vertical);

    ShapelessRecipe mossy_stone_brick_slab_vertical_from_mossy_stone_brick_slab = new ShapelessRecipe(of("mossy_stone_brick_slab_vertical_from_mossy_stone_brick_slab"), CustomMaterial.MOSSY_STONE_BRICK_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.MOSSY_STONE_BRICK_SLAB)));
    mossy_stone_brick_slab_vertical_from_mossy_stone_brick_slab.setCategory(CraftingBookCategory.BUILDING);
    mossy_stone_brick_slab_vertical_from_mossy_stone_brick_slab.setGroup("slab_vertical");
    register(mossy_stone_brick_slab_vertical_from_mossy_stone_brick_slab);

    ShapedRecipe mossy_stone_brick_slab_vertical = new ShapedRecipe(of("mossy_stone_brick_slab_vertical"), CustomMaterial.MOSSY_STONE_BRICK_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.MOSSY_STONE_BRICKS);
    mossy_stone_brick_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    mossy_stone_brick_slab_vertical.setGroup("slab_vertical");
    register(mossy_stone_brick_slab_vertical);



    ShapelessRecipe granite_slab_from_granite_slab_vertical = new ShapelessRecipe(of("granite_slab_from_granite_slab_vertical"), new ItemStack(Material.GRANITE_SLAB)).addIngredient(new ExactChoice(CustomMaterial.GRANITE_SLAB_VERTICAL.create()));
    granite_slab_from_granite_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    granite_slab_from_granite_slab_vertical.setGroup("slab_from_slab_vertical");
    register(granite_slab_from_granite_slab_vertical);

    ShapelessRecipe granite_slab_vertical_from_granite_slab = new ShapelessRecipe(of("granite_slab_vertical_from_granite_slab"), CustomMaterial.GRANITE_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.GRANITE_SLAB)));
    granite_slab_vertical_from_granite_slab.setCategory(CraftingBookCategory.BUILDING);
    granite_slab_vertical_from_granite_slab.setGroup("slab_vertical");
    register(granite_slab_vertical_from_granite_slab);

    ShapedRecipe granite_slab_vertical = new ShapedRecipe(of("granite_slab_vertical"), CustomMaterial.GRANITE_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.GRANITE);
    granite_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    granite_slab_vertical.setGroup("slab_vertical");
    register(granite_slab_vertical);



    ShapelessRecipe polished_granite_slab_from_polished_granite_slab_vertical = new ShapelessRecipe(of("polished_granite_slab_from_polished_granite_slab_vertical"), new ItemStack(Material.POLISHED_GRANITE_SLAB)).addIngredient(new ExactChoice(CustomMaterial.POLISHED_GRANITE_SLAB_VERTICAL.create()));
    polished_granite_slab_from_polished_granite_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    polished_granite_slab_from_polished_granite_slab_vertical.setGroup("slab_from_slab_vertical");
    register(polished_granite_slab_from_polished_granite_slab_vertical);

    ShapelessRecipe polished_granite_slab_vertical_from_polished_granite_slab = new ShapelessRecipe(of("polished_granite_slab_vertical_from_polished_granite_slab"), CustomMaterial.POLISHED_GRANITE_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.POLISHED_GRANITE_SLAB)));
    polished_granite_slab_vertical_from_polished_granite_slab.setCategory(CraftingBookCategory.BUILDING);
    polished_granite_slab_vertical_from_polished_granite_slab.setGroup("slab_vertical");
    register(polished_granite_slab_vertical_from_polished_granite_slab);

    ShapedRecipe polished_granite_slab_vertical = new ShapedRecipe(of("polished_granite_slab_vertical"), CustomMaterial.POLISHED_GRANITE_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.POLISHED_GRANITE);
    polished_granite_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    polished_granite_slab_vertical.setGroup("slab_vertical");
    register(polished_granite_slab_vertical);



    ShapelessRecipe diorite_slab_from_diorite_slab_vertical = new ShapelessRecipe(of("diorite_slab_from_diorite_slab_vertical"), new ItemStack(Material.DIORITE_SLAB)).addIngredient(new ExactChoice(CustomMaterial.DIORITE_SLAB_VERTICAL.create()));
    diorite_slab_from_diorite_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    diorite_slab_from_diorite_slab_vertical.setGroup("slab_from_slab_vertical");
    register(diorite_slab_from_diorite_slab_vertical);

    ShapelessRecipe diorite_slab_vertical_from_diorite_slab = new ShapelessRecipe(of("diorite_slab_vertical_from_diorite_slab"), CustomMaterial.DIORITE_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.DIORITE_SLAB)));
    diorite_slab_vertical_from_diorite_slab.setCategory(CraftingBookCategory.BUILDING);
    diorite_slab_vertical_from_diorite_slab.setGroup("slab_vertical");
    register(diorite_slab_vertical_from_diorite_slab);

    ShapedRecipe diorite_slab_vertical = new ShapedRecipe(of("diorite_slab_vertical"), CustomMaterial.DIORITE_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.DIORITE);
    diorite_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    diorite_slab_vertical.setGroup("slab_vertical");
    register(diorite_slab_vertical);



    ShapelessRecipe polished_diorite_slab_from_polished_diorite_slab_vertical = new ShapelessRecipe(of("polished_diorite_slab_from_polished_diorite_slab_vertical"), new ItemStack(Material.POLISHED_DIORITE_SLAB)).addIngredient(new ExactChoice(CustomMaterial.POLISHED_DIORITE_SLAB_VERTICAL.create()));
    polished_diorite_slab_from_polished_diorite_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    polished_diorite_slab_from_polished_diorite_slab_vertical.setGroup("slab_from_slab_vertical");
    register(polished_diorite_slab_from_polished_diorite_slab_vertical);

    ShapelessRecipe polished_diorite_slab_vertical_from_polished_diorite_slab = new ShapelessRecipe(of("polished_diorite_slab_vertical_from_polished_diorite_slab"), CustomMaterial.POLISHED_DIORITE_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.POLISHED_DIORITE_SLAB)));
    polished_diorite_slab_vertical_from_polished_diorite_slab.setCategory(CraftingBookCategory.BUILDING);
    polished_diorite_slab_vertical_from_polished_diorite_slab.setGroup("slab_vertical");
    register(polished_diorite_slab_vertical_from_polished_diorite_slab);

    ShapedRecipe polished_diorite_slab_vertical = new ShapedRecipe(of("polished_diorite_slab_vertical"), CustomMaterial.POLISHED_DIORITE_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.POLISHED_DIORITE);
    polished_diorite_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    polished_diorite_slab_vertical.setGroup("slab_vertical");
    register(polished_diorite_slab_vertical);



    ShapelessRecipe andesite_slab_from_andesite_slab_vertical = new ShapelessRecipe(of("andesite_slab_from_andesite_slab_vertical"), new ItemStack(Material.ANDESITE_SLAB)).addIngredient(new ExactChoice(CustomMaterial.ANDESITE_SLAB_VERTICAL.create()));
    andesite_slab_from_andesite_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    andesite_slab_from_andesite_slab_vertical.setGroup("slab_from_slab_vertical");
    register(andesite_slab_from_andesite_slab_vertical);

    ShapelessRecipe andesite_slab_vertical_from_andesite_slab = new ShapelessRecipe(of("andesite_slab_vertical_from_andesite_slab"), CustomMaterial.ANDESITE_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.ANDESITE_SLAB)));
    andesite_slab_vertical_from_andesite_slab.setCategory(CraftingBookCategory.BUILDING);
    andesite_slab_vertical_from_andesite_slab.setGroup("slab_vertical");
    register(andesite_slab_vertical_from_andesite_slab);

    ShapedRecipe andesite_slab_vertical = new ShapedRecipe(of("andesite_slab_vertical"), CustomMaterial.ANDESITE_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.ANDESITE);
    andesite_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    andesite_slab_vertical.setGroup("slab_vertical");
    register(andesite_slab_vertical);



    ShapelessRecipe polished_andesite_slab_from_polished_andesite_slab_vertical = new ShapelessRecipe(of("polished_andesite_slab_from_polished_andesite_slab_vertical"), new ItemStack(Material.POLISHED_ANDESITE_SLAB)).addIngredient(new ExactChoice(CustomMaterial.POLISHED_ANDESITE_SLAB_VERTICAL.create()));
    polished_andesite_slab_from_polished_andesite_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    polished_andesite_slab_from_polished_andesite_slab_vertical.setGroup("slab_from_slab_vertical");
    register(polished_andesite_slab_from_polished_andesite_slab_vertical);

    ShapelessRecipe polished_andesite_slab_vertical_from_polished_andesite_slab = new ShapelessRecipe(of("polished_andesite_slab_vertical_from_polished_andesite_slab"), CustomMaterial.POLISHED_ANDESITE_SLAB_VERTICAL.create()).addIngredient(new ExactChoice(new ItemStack(Material.POLISHED_ANDESITE_SLAB)));
    polished_andesite_slab_vertical_from_polished_andesite_slab.setCategory(CraftingBookCategory.BUILDING);
    polished_andesite_slab_vertical_from_polished_andesite_slab.setGroup("slab_vertical");
    register(polished_andesite_slab_vertical_from_polished_andesite_slab);

    ShapedRecipe polished_andesite_slab_vertical = new ShapedRecipe(of("polished_andesite_slab_vertical"), CustomMaterial.POLISHED_ANDESITE_SLAB_VERTICAL.create(6)).shape("o", "o", "o").setIngredient('o', Material.POLISHED_ANDESITE);
    polished_andesite_slab_vertical.setCategory(CraftingBookCategory.BUILDING);
    polished_andesite_slab_vertical.setGroup("slab_vertical");
    register(polished_andesite_slab_vertical);
  }

  private static boolean isAdminOnline()
  {
    return Bukkit.getPlayer("jho5245") != null;
  }

  private static final RecipeChoice INPUT_WATER_BOTTLE = PotionMix.createPredicateChoice(itemStack ->
  {
    ItemMeta itemMeta = itemStack.getItemMeta();
    return itemMeta instanceof PotionMeta potionMeta && potionMeta.getBasePotionType() == PotionType.WATER && potionMeta.getCustomEffects().isEmpty();
  });

  @NotNull
  private static NamespacedKey of(@NotNull String s)
  {
    NamespacedKey namespacedKey = NamespacedKey.fromString(s, Cucumbery.getPlugin());
    recipes.add(namespacedKey);
    if (namespacedKey == null)
    {
      throw new IllegalArgumentException("Invalid key!: " + s);
    }
    return namespacedKey;
  }

  private static void register(@NotNull Recipe recipe)
  {
    if (recipe instanceof Keyed keyed && Bukkit.getRecipe(keyed.getKey()) != null)
    {
      Bukkit.removeRecipe(keyed.getKey());
    }
    Bukkit.addRecipe(recipe);
  }

  private static void register(@NotNull PotionMix potionMix)
  {
    NamespacedKey namespacedKey = potionMix.getKey();
    Bukkit.getPotionBrewer().removePotionMix(namespacedKey);
    Bukkit.getPotionBrewer().addPotionMix(potionMix);
  }

  public static void unload()
  {
    recipes.forEach(Bukkit::removeRecipe);
  }
}
