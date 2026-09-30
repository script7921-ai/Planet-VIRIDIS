package com.viridis.mod.system;

import arc.*;
import arc.graphics.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;

/**
 * Синглтон-контроллер шкалы Раздражения биосферы.
 * Архитектурный принцип: НИКАКОГО опроса всей карты каждый тик.
 * Блоки-загрязнители регистрируются при постройке (register) и снимаются при разрушении (unregister).
 * Суммарная дельта пересчитывается РАЗ В 60 ТИКОВ. Zero-allocation в рантайме.
 */
public class IrritationManager{
  /** Все здания, влияющие на раздражение. */
  public static final Seq<Building> sources = new Seq<>();
  /** Дельта раздражения в секунду от каждого источника. */
  public static final ObjectMap<Building, Float> deltas = new ObjectMap<>();

  /** Текущее раздражение сектора, 0..1 (0..100%). */
  public static float irritation = 0f;
  /** Заблокировано ли раздражение (Сектор 1: планета спит). */
  public static boolean locked = false;
  /** Финал: биосфера контролируется (после «Глубокого Симбиоза»). */
  public static boolean controlled = false;
  /** Множитель роста (Споровый Туман — x2). */
  public static float growthMultiplier = 1f;

  static int tickCounter = 0;
  static float pendingBump = 0f;
  //временные цвета — без аллокаций в draw/update
  static final Color colCalm = Color.valueOf("55ff44");
  static final Color colWarn = Color.valueOf("ff9900");
  static final Color colRage = Color.valueOf("c800ff");
  static final Color tmpCol = new Color();

  public static void init(){
    Events.on(ClientLoadEvent.class, e -> reset());
    Events.on(GameOverEvent.class, e -> reset());
    //пересчёт пула агрессии строго раз в секунду (60 тиков), не каждый кадр
    Timer.schedule(() -> update(), 1f, 1f);
  }

  public static void reset(){
    sources.clear();
    deltas.clear();
    irritation = 0f;
    locked = false;
    controlled = false;
    growthMultiplier = 1f;
    pendingBump = 0f;
    tickCounter = 0;
  }

  /** Регистрация источника (+delta/sec). Вызывается из Building.placed(). */
  public static void register(Building b, float perSecond){
    if(!sources.contains(b, true)){
      sources.add(b);
      deltas.put(b, perSecond);
    }
  }

  /** Снятие источника. Вызывается из onProximityRemoved()/удаления здания. */
  public static void unregister(Building b){
    sources.removeValue(b, true);
    deltas.remove(b);
  }

  /** Мгновенный всплеск ярости (выстрел «Дефолианта», прорыв желчи). Копится до ближайшего тика. */
  public static void bump(float amount){
    pendingBump += amount;
  }

  /** Обновление пула: вызывается таймером раз в секунду. */
  public static void update(){
    if(controlled || locked){
      pendingBump = 0f;
      return;
    }

    float sum = 0f;
    //ленивая чистка мёртвых зданий + суммирование дельт
    for(int i = sources.size - 1; i >= 0; i--){
      Building b = sources.get(i);
      if(b == null || !b.exists()){
        sources.removeIndex(i);
        if(b != null) deltas.remove(b);
        continue;
      }
      sum += deltas.get(b, 0f);
    }

    //естественное медленное успокоение биосферы при отсутствии активности
    float net = (sum > 0f ? sum : sum - 0.35f) * growthMultiplier;
    irritation = Mathf.clamp(irritation + net / 60f + pendingBump);
    pendingBump = 0f;
  }

  /** Цвет узла кардиограммы по текущему уровню (без аллокации — возврат во временный цвет). */
  public static Color gaugeColor(){
    if(controlled) return tmpCol.set(colCalm);
    if(irritation <= 0.3f) return tmpCol.set(colCalm).lerp(colWarn, irritation / 0.3f * 0.3f);
    if(irritation <= 0.75f) return tmpCol.set(colWarn).lerp(colRage, (irritation - 0.3f) / 0.45f);
    return tmpCol.set(colRage);
  }

  /** Частота пульса узла: спокойно → беспокойно на 100%. */
  public static float pulseRate(){
    return 1.6f + irritation * 7f;
  }

  /** Тултип с точными источниками агрессии для HUD. */
  public static String tooltip(){
    StringBuilder sb = new StringBuilder(256);
    sb.append("[accent]Раздражение биосферы: []).white(").append(Mathf.round(irritation * 100f)).append("%[]\n");
    int shown = 0;
    for(int i = 0; i < sources.size && shown < 6; i++){
      Building b = sources.get(i);
      if(b == null || !b.exists()) continue;
      sb.append("[gray]").append(b.block.localizedName).append(": +")
        .append(String.format("%.2f", deltas.get(b, 0f))).append("/s\n");
      shown++;
    }
    if(locked) sb.append("[green]Планета спит. Шкала заблокирована.");
    if(controlled) sb.append("[green]Биосфера: Контролируется.");
    return sb.toString();
  }
}
