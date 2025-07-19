package com.jho5245.cucumbery.custom.customeffect.type;

import com.jho5245.cucumbery.custom.customeffect.TypeBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffectType;

public class CustomEffectTypeMinecraft extends CustomEffectType
{
  private static final TypeBuilder
      B = builder().removeOnMilk().nonBuffFreezable(),
      D = builder().removeOnMilk().realDuration().maxAmplifier(255),
      K = builder().removeOnMilk().keepOnDeath().maxAmplifier(255),
      M = builder().maxAmplifier(255),
      Q = builder().removeOnMilk().removeOnQuit().maxAmplifier(255),
      R = builder().removeOnMilk().nonRemovable().maxAmplifier(255),



      BD = builder().maxAmplifier(255),
      BK = builder().maxAmplifier(255),
      BM = builder().maxAmplifier(255),
      BQ = builder().maxAmplifier(255),
      BR = builder().maxAmplifier(255),

      DK = builder().maxAmplifier(255),
      DM = builder().maxAmplifier(255),
      DQ = builder().maxAmplifier(255),
      DR = builder().maxAmplifier(255),

      KM = builder().maxAmplifier(255),
      KQ = builder().maxAmplifier(255),
      KR = builder().maxAmplifier(255),

      MQ = builder().maxAmplifier(255),
      MR = builder().maxAmplifier(255),

      QR = builder().maxAmplifier(255),



      BDK = builder().maxAmplifier(255),
      BDM = builder().maxAmplifier(255),
      BDQ = builder().maxAmplifier(255),
      BDR = builder().maxAmplifier(255),
      BKM = builder().maxAmplifier(255),
      BKQ = builder().maxAmplifier(255),
      BKR = builder().maxAmplifier(255),
      BMQ = builder().maxAmplifier(255),
      BMR = builder().maxAmplifier(255),
      BQR = builder().maxAmplifier(255),

      DKM = builder().maxAmplifier(255),
      DKQ = builder().maxAmplifier(255),
      DKR = builder().maxAmplifier(255),
      DMQ = builder().maxAmplifier(255),
      DMR = builder().maxAmplifier(255),
      DRQ = builder().maxAmplifier(255),

      KMQ = builder().maxAmplifier(255),
      KMR = builder().maxAmplifier(255),
      KQR = builder().maxAmplifier(255),

      MQR = builder().maxAmplifier(255),



      BDKM = builder().maxAmplifier(255),
      BDKQ = builder().maxAmplifier(255),
      BDKR = builder().maxAmplifier(255),
      BKMQ = builder().maxAmplifier(255),
      BKMR = builder().maxAmplifier(255),
      BKQR = builder().maxAmplifier(255),

      DKMQ = builder().maxAmplifier(255),
      DKMR = builder().maxAmplifier(255),
      DKQR = builder().maxAmplifier(255),
      DMQR = builder().maxAmplifier(255),

      KMQR = builder().maxAmplifier(255),



      BDKMQ = builder().maxAmplifier(255),
      BDKMR = builder().maxAmplifier(255),
      BDKQR = builder().maxAmplifier(255),
      BDMQR = builder().maxAmplifier(255),
      BKMQR = builder().maxAmplifier(255),

      DKMQR = builder().maxAmplifier(255),



      BDKMQR = builder().nonBuffFreezable().realDuration().keepOnDeath().removeOnQuit().nonRemovable().maxAmplifier(255)
          ;

  public static final CustomEffectType
          SPEED = new CustomEffectType(NamespacedKey.minecraft("speed"), PotionEffectType.SPEED.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          SLOWNESS = new CustomEffectType(NamespacedKey.minecraft("slowness"), PotionEffectType.SLOWNESS.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          HASTE = new CustomEffectType(NamespacedKey.minecraft("haste"), PotionEffectType.HASTE.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          MINING_FATIGUE = new CustomEffectType(NamespacedKey.minecraft("mining_fatigue"), PotionEffectType.MINING_FATIGUE.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          STRENGTH = new CustomEffectType(NamespacedKey.minecraft("strength"), PotionEffectType.STRENGTH.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          INSTANT_HEALTH = new CustomEffectType(NamespacedKey.minecraft("instant_health"), PotionEffectType.INSTANT_HEALTH.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          INSTANT_DAMAGE = new CustomEffectType(NamespacedKey.minecraft("instant_damage"), PotionEffectType.INSTANT_DAMAGE.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          JUMP_BOOST = new CustomEffectType(NamespacedKey.minecraft("jump_boost"), PotionEffectType.JUMP_BOOST.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          NAUSEA = new CustomEffectType(NamespacedKey.minecraft("nausea"), PotionEffectType.NAUSEA.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          REGENERATION = new CustomEffectType(NamespacedKey.minecraft("regeneration"), PotionEffectType.REGENERATION.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          RESISTANCE = new CustomEffectType(NamespacedKey.minecraft("resistance"), PotionEffectType.RESISTANCE.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          FIRE_RESISTANCE = new CustomEffectType(NamespacedKey.minecraft("fire_resistance"), PotionEffectType.FIRE_RESISTANCE.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          WATER_BREATHING = new CustomEffectType(NamespacedKey.minecraft("water_breathing"), PotionEffectType.WATER_BREATHING.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          INVISIBILITY = new CustomEffectType(NamespacedKey.minecraft("invisibility"), PotionEffectType.INVISIBILITY.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          BLINDNESS = new CustomEffectType(NamespacedKey.minecraft("blindness"), PotionEffectType.BLINDNESS.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          NIGHT_VISION = new CustomEffectType(NamespacedKey.minecraft("night_vision"), PotionEffectType.NIGHT_VISION.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          HUNGER = new CustomEffectType(NamespacedKey.minecraft("hunger"), PotionEffectType.HUNGER.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          WEAKNESS = new CustomEffectType(NamespacedKey.minecraft("weakness"), PotionEffectType.WEAKNESS.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          POISON = new CustomEffectType(NamespacedKey.minecraft("poison"), PotionEffectType.POISON.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          WITHER = new CustomEffectType(NamespacedKey.minecraft("wither"), PotionEffectType.WITHER.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          HEALTH_BOOST = new CustomEffectType(NamespacedKey.minecraft("health_boost"), PotionEffectType.HEALTH_BOOST.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          ABSORPTION = new CustomEffectType(NamespacedKey.minecraft("absorption"), PotionEffectType.ABSORPTION.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          SATURATION = new CustomEffectType(NamespacedKey.minecraft("saturation"), PotionEffectType.SATURATION.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          GLOWING = new CustomEffectType(NamespacedKey.minecraft("glowing"), PotionEffectType.GLOWING.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          LEVITATION = new CustomEffectType(NamespacedKey.minecraft("levitation"), PotionEffectType.LEVITATION.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          LUCK = new CustomEffectType(NamespacedKey.minecraft("luck"), PotionEffectType.LUCK.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          UNLUCK = new CustomEffectType(NamespacedKey.minecraft("unluck"), PotionEffectType.UNLUCK.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          SLOW_FALLING = new CustomEffectType(NamespacedKey.minecraft("slow_falling"), PotionEffectType.SLOW_FALLING.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          CONDUIT_POWER = new CustomEffectType(NamespacedKey.minecraft("conduit_power"), PotionEffectType.CONDUIT_POWER.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          DOLPHINS_GRACE = new CustomEffectType(NamespacedKey.minecraft("dolphins_grace"), PotionEffectType.DOLPHINS_GRACE.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          BAD_OMEN = new CustomEffectType(NamespacedKey.minecraft("bad_omen"), PotionEffectType.BAD_OMEN.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          HERO_OF_THE_VILLAGE = new CustomEffectType(NamespacedKey.minecraft("hero_of_the_village"), PotionEffectType.HERO_OF_THE_VILLAGE.translationKey(), builder().removeOnMilk().maxAmplifier(255)),
          DARKNESS = new CustomEffectType(NamespacedKey.minecraft("darkness"), PotionEffectType.DARKNESS.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          INFESTED = new CustomEffectType(NamespacedKey.minecraft("infested"), PotionEffectType.INFESTED.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          OOZING = new CustomEffectType(NamespacedKey.minecraft("oozing"), PotionEffectType.OOZING.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          RAID_OMEN = new CustomEffectType(NamespacedKey.minecraft("raid_omen"), PotionEffectType.RAID_OMEN.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          TRIAL_OMEN = new CustomEffectType(NamespacedKey.minecraft("trial_omen"), PotionEffectType.TRIAL_OMEN.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          WEAVING = new CustomEffectType(NamespacedKey.minecraft("weaving"), PotionEffectType.WEAVING.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255)),
          WIND_CHARGED = new CustomEffectType(NamespacedKey.minecraft("wind_charged"), PotionEffectType.WIND_CHARGED.translationKey(), builder().removeOnMilk().negative().maxAmplifier(255))

//          SPEED__K = new CustomEffectType(NamespacedKey.minecraft("speed__m"), PotionEffectType.SPEED.translationKey(), builder().removeOnMilk().keepOnDeath().maxAmplifier(255)),
//          SPEED__M = new CustomEffectType(NamespacedKey.minecraft("speed__m"), PotionEffectType.SPEED.translationKey(), builder().maxAmplifier(255)),
//          SPEED__N = new CustomEffectType(NamespacedKey.minecraft("speed__m"), PotionEffectType.SPEED.translationKey(), builder().removeOnMilk().nonRemovable().maxAmplifier(255)),
//          SPEED__Q = new CustomEffectType(NamespacedKey.minecraft("speed__m"), PotionEffectType.SPEED.translationKey(), builder().removeOnMilk().removeOnQuit().maxAmplifier(255)),
//          SPEED__R = new CustomEffectType(NamespacedKey.minecraft("speed__m"), PotionEffectType.SPEED.translationKey(), builder().removeOnMilk().realDuration().maxAmplifier(255)),
//          SPEED__KM = new CustomEffectType(NamespacedKey.minecraft("speed__mr"), PotionEffectType.SPEED.translationKey(), builder().keepOnDeath().maxAmplifier(255)),
//          SPEED__KN = new CustomEffectType(NamespacedKey.minecraft("speed__mr"), PotionEffectType.SPEED.translationKey(), builder().removeOnMilk().keepOnDeath().nonRemovable().maxAmplifier(255)),
//          SPEED__KQ = new CustomEffectType(NamespacedKey.minecraft("speed__mr"), PotionEffectType.SPEED.translationKey(), builder().removeOnMilk().keepOnDeath().removeOnQuit().maxAmplifier(255)),
//          SPEED__KR = new CustomEffectType(NamespacedKey.minecraft("speed__mr"), PotionEffectType.SPEED.translationKey(), builder().removeOnMilk().keepOnDeath().realDuration().maxAmplifier(255)),
//          SPEED__MN = new CustomEffectType(NamespacedKey.minecraft("speed__mr"), PotionEffectType.SPEED.translationKey(), builder().nonRemovable().maxAmplifier(255)),
//          SPEED__MQ = new CustomEffectType(NamespacedKey.minecraft("speed__mr"), PotionEffectType.SPEED.translationKey(), builder().nonRemovable().removeOnQuit().maxAmplifier(255)),
//          SPEED__MR = new CustomEffectType(NamespacedKey.minecraft("speed__mr"), PotionEffectType.SPEED.translationKey(), builder().realDuration().maxAmplifier(255)),
//          SPEED__NQ = new CustomEffectType(NamespacedKey.minecraft("speed__mr"), PotionEffectType.SPEED.translationKey(), builder().removeOnMilk().nonRemovable().removeOnQuit().maxAmplifier(255)),
//          SPEED__NR = new CustomEffectType(NamespacedKey.minecraft("speed__mr"), PotionEffectType.SPEED.translationKey(), builder().removeOnMilk().nonRemovable().realDuration().maxAmplifier(255)),
//          SPEED__QR = new CustomEffectType(NamespacedKey.minecraft("speed__mr"), PotionEffectType.SPEED.translationKey(), builder().removeOnMilk().removeOnQuit().realDuration().maxAmplifier(255)),

//          SLOWNESS__M = new CustomEffectType(NamespacedKey.minecraft("slowness__m"), PotionEffectType.SLOWNESS.translationKey(), builder().negative().maxAmplifier(255)),
//          HASTE__M = new CustomEffectType(NamespacedKey.minecraft("haste__m"), PotionEffectType.HASTE.translationKey(), builder().maxAmplifier(255)),
//          MINING_FATIGUE__M = new CustomEffectType(NamespacedKey.minecraft("mining_fatigue__m"), PotionEffectType.SLOWNESS.translationKey(), builder().negative().maxAmplifier(255)),
//          STRENGTH__M = new CustomEffectType(NamespacedKey.minecraft("strength__m"), PotionEffectType.STRENGTH.translationKey(), builder().maxAmplifier(255)),
//          INSTANT_HEALTH__M = new CustomEffectType(NamespacedKey.minecraft("instant_health__m"), PotionEffectType.INSTANT_HEALTH.translationKey(), builder().maxAmplifier(255)),
//          INSTANT_DAMAGE__M = new CustomEffectType(NamespacedKey.minecraft("instant_damage__m"), PotionEffectType.INSTANT_DAMAGE.translationKey(), builder().negative().maxAmplifier(255)),
//          JUMP_BOOST__M = new CustomEffectType(NamespacedKey.minecraft("jump_boost__m"), PotionEffectType.JUMP_BOOST.translationKey(), builder().maxAmplifier(255)),
//          NAUSEA__M = new CustomEffectType(NamespacedKey.minecraft("nausea__m"), PotionEffectType.NAUSEA.translationKey(), builder().negative().maxAmplifier(255)),
//          REGENERATION__M = new CustomEffectType(NamespacedKey.minecraft("regeneration__m"), PotionEffectType.REGENERATION.translationKey(), builder().maxAmplifier(255)),
//          RESISTANCE__M = new CustomEffectType(NamespacedKey.minecraft("resistance__m"), PotionEffectType.RESISTANCE.translationKey(), builder().maxAmplifier(255)),
//          FIRE_RESISTANCE__M = new CustomEffectType(NamespacedKey.minecraft("fire_resistance__m"), PotionEffectType.FIRE_RESISTANCE.translationKey(), builder().maxAmplifier(255)),
//          WATER_BREATHING__M = new CustomEffectType(NamespacedKey.minecraft("water_breathing__m"), PotionEffectType.WATER_BREATHING.translationKey(), builder().maxAmplifier(255)),
//          INVISIBILITY__M = new CustomEffectType(NamespacedKey.minecraft("invisibility__m"), PotionEffectType.INVISIBILITY.translationKey(), builder().maxAmplifier(255)),
//          BLINDNESS__M = new CustomEffectType(NamespacedKey.minecraft("blindness__m"), PotionEffectType.BLINDNESS.translationKey(), builder().negative().maxAmplifier(255)),
//          NIGHT_VISION__M = new CustomEffectType(NamespacedKey.minecraft("night_vision__m"), PotionEffectType.NIGHT_VISION.translationKey(), builder().maxAmplifier(255)),
//          HUNGER__M = new CustomEffectType(NamespacedKey.minecraft("hunge__mr"), PotionEffectType.HUNGER.translationKey(), builder().negative().maxAmplifier(255)),
//          WEAKNESS__M = new CustomEffectType(NamespacedKey.minecraft("weakness__m"), PotionEffectType.WEAKNESS.translationKey(), builder().negative().maxAmplifier(255)),
//          POISON__M = new CustomEffectType(NamespacedKey.minecraft("poison__m"), PotionEffectType.POISON.translationKey(), builder().negative().maxAmplifier(255)),
//          WITHER__M = new CustomEffectType(NamespacedKey.minecraft("wither__m"), PotionEffectType.WITHER.translationKey(), builder().negative().maxAmplifier(255)),
//          HEALTH_BOOST__M = new CustomEffectType(NamespacedKey.minecraft("health_boost__m"), PotionEffectType.HEALTH_BOOST.translationKey(), builder().maxAmplifier(255)),
//          ABSORPTION__M = new CustomEffectType(NamespacedKey.minecraft("absorption__m"), PotionEffectType.ABSORPTION.translationKey(), builder().maxAmplifier(255)),
//          SATURATION__M = new CustomEffectType(NamespacedKey.minecraft("saturation__m"), PotionEffectType.SATURATION.translationKey(), builder().maxAmplifier(255)),
//          GLOWING__M = new CustomEffectType(NamespacedKey.minecraft("glowing__m"), PotionEffectType.GLOWING.translationKey(), builder().negative().maxAmplifier(255)),
//          LEVITATION__M = new CustomEffectType(NamespacedKey.minecraft("levitation__m"), PotionEffectType.LEVITATION.translationKey(), builder().negative().maxAmplifier(255)),
//          LUCK__M = new CustomEffectType(NamespacedKey.minecraft("luck__m"), PotionEffectType.LUCK.translationKey(), builder().maxAmplifier(255)),
//          UNLUCK__M = new CustomEffectType(NamespacedKey.minecraft("unluck__m"), PotionEffectType.UNLUCK.translationKey(), builder().negative().maxAmplifier(255)),
//          SLOW_FALLING__M = new CustomEffectType(NamespacedKey.minecraft("slow_falling__m"), PotionEffectType.SLOW_FALLING.translationKey(), builder().maxAmplifier(255)),
//          CONDUIT_POWER__M = new CustomEffectType(NamespacedKey.minecraft("conduit_power__m"), PotionEffectType.CONDUIT_POWER.translationKey(), builder().maxAmplifier(255)),
//          DOLPHINS_GRACE__M = new CustomEffectType(NamespacedKey.minecraft("dolphins_grace__m"), PotionEffectType.DOLPHINS_GRACE.translationKey(), builder().maxAmplifier(255)),
//          BAD_OMEN__M = new CustomEffectType(NamespacedKey.minecraft("bad_omen__m"), PotionEffectType.BAD_OMEN.translationKey(), builder().negative().maxAmplifier(255)),
//          HERO_OF_THE_VILLAGE__M = new CustomEffectType(NamespacedKey.minecraft("hero_of_the_village__m"), PotionEffectType.HERO_OF_THE_VILLAGE.translationKey(), builder().maxAmplifier(255)),
//          DARKNESS__M = new CustomEffectType(NamespacedKey.minecraft("darkness__m"), PotionEffectType.DARKNESS.translationKey(), builder().negative().maxAmplifier(255))
              ;

  protected static void registerEffect()
  {
    register(
            SPEED, SLOWNESS, HASTE, MINING_FATIGUE, STRENGTH, INSTANT_HEALTH, INSTANT_DAMAGE, JUMP_BOOST, NAUSEA, REGENERATION,
            RESISTANCE, FIRE_RESISTANCE, WATER_BREATHING, INVISIBILITY, BLINDNESS, NIGHT_VISION, HUNGER, WEAKNESS, POISON, WITHER,
            HEALTH_BOOST, ABSORPTION, SATURATION, GLOWING, LEVITATION, LUCK, UNLUCK, SLOW_FALLING, CONDUIT_POWER, DOLPHINS_GRACE,
            BAD_OMEN, HERO_OF_THE_VILLAGE, DARKNESS, INFESTED, OOZING, RAID_OMEN, TRIAL_OMEN, WEAVING, WIND_CHARGED
//            SPEED__M, SPEED__MR,
//            SLOWNESS__M,
//            HASTE__M,
//            MINING_FATIGUE__M,
//            STRENGTH__M,
//            INSTANT_HEALTH__M,
//            INSTANT_DAMAGE__M,
//            JUMP_BOOST__M,
//            NAUSEA__M,
//            REGENERATION__M,
//            RESISTANCE__M,
//            FIRE_RESISTANCE__M,
//            WATER_BREATHING__M,
//            INVISIBILITY__M,
//            BLINDNESS__M,
//            NIGHT_VISION__M,
//            HUNGER__M,
//            WEAKNESS__M,
//            POISON__M,
//            WITHER__M,
//            HEALTH_BOOST__M,
//            ABSORPTION__M,
//            SATURATION__M,
//            GLOWING__M,
//            LEVITATION__M,
//            LUCK__M,
//            UNLUCK__M,
//            SLOW_FALLING__M,
//            CONDUIT_POWER__M,
//            DOLPHINS_GRACE__M,
//            BAD_OMEN__M,
//            HERO_OF_THE_VILLAGE__M,
//            DARKNESS__M
    );
  }
}
