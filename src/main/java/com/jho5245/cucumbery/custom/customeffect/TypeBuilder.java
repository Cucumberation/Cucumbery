package com.jho5245.cucumbery.custom.customeffect;

import com.jho5245.cucumbery.custom.customeffect.CustomEffect.DisplayType;
import com.jho5245.cucumbery.events.entity.EntityCustomEffectPreApplyEvent;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class TypeBuilder
{
  private @Nullable String shortenTranslationKey;

  private int maxAmplifier, defaultDuration = 20 * 30;

  private boolean isBuffFreezable = true;
  private boolean isKeepOnDeath;
  private boolean isKeepOnMilk = true;
  private boolean isKeepOnQuit = true;
  private boolean isRemoveable = true;
  private boolean isRealDuration;
  private boolean isNegative;
  private boolean isInstant;
  private boolean isToggle;
  private boolean isHidden;
  private boolean isTimeHidden;
  private boolean isTimeHiddenWhenFull;
  private boolean isEnumHidden;
  private boolean isStackDisplayed;

  private boolean callEvent = true;

  private Component description = Component.empty();

  private @Nullable ItemStack icon;

  private DisplayType defaultDisplayType = DisplayType.BOSS_BAR;

  private Predicate<Entity> targetFilter;
  
  @NotNull
  public TypeBuilder nonBuffFreezable()
  {
    this.isBuffFreezable = false;
    return this;
  }
  
  @NotNull
  public TypeBuilder keepOnDeath()
  {
    this.isKeepOnDeath = true;
    return this;
  }

  @NotNull
  public TypeBuilder removeOnMilk()
  {
    this.isKeepOnMilk = false;
    return this;
  }

  @NotNull
  public TypeBuilder removeOnQuit()
  {
    this.isKeepOnQuit = false;
    return this;
  }

  @NotNull
  public TypeBuilder realDuration()
  {
    this.isRealDuration = true;
    return this;
  }

  @NotNull
  public TypeBuilder nonRemovable()
  {
    this.isRemoveable = false;
    return this;
  }

  @NotNull
  public TypeBuilder negative()
  {
    this.isNegative = true;
    return this;
  }

  @NotNull
  public TypeBuilder instant()
  {
    this.isInstant = true;
    this.defaultDuration = -1;
    this.isKeepOnMilk = false;
    this.defaultDisplayType = DisplayType.NONE;
    return this;
  }

  @NotNull
  public TypeBuilder toggle()
  {
    this.isToggle = true;
    return this;
  }

  /**
   * 명령어 탭 목록에서도 보이지 않고 효과 리스트에도 보이지 않음
   * @return this
   */
  @NotNull
  public TypeBuilder hidden()
  {
    this.isHidden = true;
    this.isEnumHidden = true;
    return this;
  }

  /**
   * 명령어 탭 목록에서도 보이지 않고 효과 리스트에도 보이지 않음
   * @return this
   */
  @NotNull
  public TypeBuilder hidden(boolean b)
  {
    this.isHidden = b;
    this.isEnumHidden = b;
    return this;
  }

  @NotNull
  public TypeBuilder timeHidden()
  {
    this.isTimeHidden = true;
    return this;
  }

  @NotNull
  public TypeBuilder timeHiddenWhenFull()
  {
    this.isTimeHiddenWhenFull = true;
    return this;
  }

  @NotNull
  public TypeBuilder enumHidden()
  {
    this.isEnumHidden = true;
    return this;
  }

  @NotNull
  public TypeBuilder stackDisplayed()
  {
    this.isStackDisplayed = true;
    return this;
  }

  @NotNull
  public TypeBuilder maxAmplifier(int maxAmplifier)
  {
    this.maxAmplifier = maxAmplifier;
    return this;
  }

  @NotNull
  public TypeBuilder defaultDuration(int defaultDuration)
  {
    this.defaultDuration = defaultDuration;
    return this;
  }
  
  /**
   * 효과 설명 지정
   */
  @NotNull
  public TypeBuilder description(@NotNull Component description)
  {
    this.description = description;
    return this;
  }


  /**
   * 효과 설명 지정
   */
  @NotNull
  public TypeBuilder description(@NotNull String description)
  {
    this.description = ComponentUtil.translate(description);
    return this;
  }

  /**
   * 효과 설명 지정
   */
  @NotNull
  public TypeBuilder description(@NotNull String description, @NotNull Object... args)
  {
    this.description = ComponentUtil.translate(description, args);
    return this;
  }

  /**
   * 효과 아이콘 지정
   */
  @NotNull
  public TypeBuilder icon(@Nullable Material icon)
  {
    this.icon = icon != null ? new ItemStack(icon) : null;
    return this;
  }

  /**
   * 효과 아이콘 지정
   */
  @NotNull
  public TypeBuilder icon(@Nullable ItemStack icon)
  {
    this.icon = icon;
    return this;
  }
  
  /**
   * 효과 아이콘 지정
   */
  @NotNull
  public TypeBuilder icon(@NotNull Supplier<ItemStack> supplier)
  {
    this.icon = supplier.get();
    return this;
  }

  /**
   * 기본 표시 유형 지정, 지정하지 않을 경우 {@link DisplayType#BOSS_BAR} 사용
   * @param defaultDisplayType
   * @return
   */
  @NotNull
  public TypeBuilder defaultDisplayType(@NotNull DisplayType defaultDisplayType)
  {
    this.defaultDisplayType = defaultDisplayType;
    return this;
  }

  /**
   * 보스바에 효과가 너무 많아서 짧게 표시할 때 사용할 문자열
   * @param s 보스바에 효과가 너무 많아서 짧게 표시할 때 사용할 문자열
   */
  @NotNull
  public TypeBuilder shortenTransltionKey(@NotNull String s)
  {
    this.shortenTranslationKey = s;
    return this;
  }

  /**
   * 효과가 적용되거나 사라질 때 이벤트를 호출하지 않습니다
   */
  @NotNull
  public TypeBuilder skipEvent()
  {
    this.callEvent = false;
    return this;
  }

  @NotNull
  public TypeBuilder targetFilter(@NotNull Predicate<Entity> predicate)
  {
    this.targetFilter = predicate;
    return this;
  }

  @Nullable
  public String getShortenTranslationKey()
  {
    return shortenTranslationKey;
  }

  public boolean isBuffFreezable()
  {
    return isBuffFreezable;
  }

  public boolean isKeepOnDeath()
  {
    return isKeepOnDeath;
  }

  public boolean isKeepOnMilk()
  {
    return isKeepOnMilk;
  }
  
  public boolean isKeepOnQuit()
  {
    return isKeepOnQuit;
  }
  
  public boolean isRealDuration()
  {
    return isRealDuration;
  }
  
  public boolean isRemoveable()
  {
    return !isNegative && isRemoveable;
  }
  
  public boolean isNegative()
  {
    return isNegative;
  }

  public int getMaxAmplifier()
  {
    return maxAmplifier;
  }

  public int getDefaultDuration()
  {
    return defaultDuration;
  }

  public Component getDescription()
  {
    return description;
  }

  @Nullable
  public ItemStack getIcon()
  {
    return icon;
  }

  public boolean isInstant()
  {
    return isInstant;
  }

  public boolean isToggle()
  {
    return isToggle;
  }

  public boolean isHidden()
  {
    return isHidden;
  }

  public boolean isTimeHidden()
  {
    return isTimeHidden;
  }

  public boolean isTimeHiddenWhenFull()
  {
    return isTimeHiddenWhenFull;
  }

  public boolean isEnumHidden()
  {
    return isEnumHidden;
  }

  public boolean isStackDisplayed()
  {
    return isStackDisplayed;
  }

  public DisplayType getDefaultDisplayType()
  {
    return defaultDisplayType;
  }

  public boolean doesCallEvent()
  {
    return this.callEvent;
  }

  /**
   * 해당 필터에 만족하는 개체만 효과를 적용받을 수 있음. 아닐 경우 {@link EntityCustomEffectPreApplyEvent} 이벤트 호출 시 cancel됨
   * @return this
   */
  @Nullable
  public Predicate<Entity> getTargetFilter()
  {
    return this.targetFilter;
  }
}
