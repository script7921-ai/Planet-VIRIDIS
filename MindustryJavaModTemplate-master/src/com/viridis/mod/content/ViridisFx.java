package com.viridis.mod.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;

/**
 * Кастомные эффекты Виридиса. Все draw-колбэки zero-allocation:
 * цвета — статические, координаты — только примитивы.
 */
public class ViridisFx{
  //статические палитры эффектов — никаких new Color() в рантайме
  static final Color sap = Color.valueOf("00ffd5");
  static final Color bile = Color.valueOf("ffb700");
  static final Color spore = Color.valueOf("c800ff");
  static final Color lime = Color.valueOf("55ff44");
  static final Rand rand = new Rand();

  /** Разрыв био-сока: бирюзовые осколки (крио-сифон, аварии). */
  public static Effect sapBurst = Effect.dynamic(16f, 0.9f, 3f, i -> {
    rand.setSeed(i * 97L + Time.time);
    float ang = rand.range(360f), len = rand.random(10f, 26f);
    Tmp.v1.trns(ang, len);
    Draw.color(sap);
    Lines.stroke(1.6f + i * 0.1f);
    Lines.lineAng(Callers.get(Position.class, 0).x + Tmp.v1.x, Callers.get(Position.class, 0).y + Tmp.v1.y - len/2f, ang + 90f, len, false);
    Draw.reset();
  });

  /** Всплеск желчи при аварийном сбросе автоклава. */
  public static Effect bileSplat = Effect.dynamic(14f, 1f, 2f, i -> {
    rand.setSeed(i * 31L + Time.time);
    float ang = rand.range(360f), d = rand.random(4f, 20f) * (i / 14f + 0.3f);
    Tmp.v1.trns(ang, d);
    Draw.color(bile);
    Fill.circle(Callers.get(Position.class, 1).x + Tmp.v1.x, Callers.get(Position.class, 1).y + Tmp.v1.y, 2.4f * (1f - i/14f));
    Draw.reset();
  });

  /** Спорное облако (гибель фауны / прорыв корней). */
  public static Effect sporeCloud = Effect.dynamic(26f, 1.1f, 1f, i -> {
    rand.setSeed(i * 53L + Time.time);
    Tmp.v1.trns(rand.range(360f), rand.random(2f, 18f));
    Draw.color(spore);
    Draw.alpha(0.5f * (1f - i/26f));
    Fill.circle(Callers.get(Position.class, 2).x + Tmp.v1.x, Callers.get(Position.class, 2).y + Tmp.v1.y, 3f + i * 0.35f);
    Draw.reset();
  });

  /** Импульс по нейро-нити (синаптический разряд). */
  public static Effect neuralPulse = Effect.dynamic(20f, 0.8f, 1f, i -> {
    Position s = Callers.get(Position.class, 3), t = Callers.get(Position.class, 4);
    float f = Mathf.clamp(i / 20f);
    Tmp.v1.set(s).lerp(t, f);
    Draw.z(Layer.power + 1f);
    Draw.color(lime, spore, f);
    Fill.circle(Tmp.v1.x, Tmp.v1.y, 2.6f * (1f - f) + 0.6f);
    Draw.reset();
  });

  /** Корневой прорыв: зелёные шипы из земли (атака корней, С12+). */
  public static Effect rootSpikes = Effect.dynamic(30f, 1f, 1f, i -> {
    float fin = i / 30f;
    Draw.color(lime, spore, fin);
    Lines.stroke(2.5f * (1f - fin));
    for(int s = 0; s < 6; s++){
      float ang = s * 60f + 22f;
      float len = 8f + fin * 20f;
      Lines.lineAngle(Callers.get(Position.class, 5).x, Callers.get(Position.class, 5).y, ang, len * fin);
    }
    Draw.reset();
  });
}
