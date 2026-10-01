package com.viridis.mod.content;

import arc.graphics.*;
import arc.math.geom.*;
import mindustry.entities.*;
import mindustry.entities.effect.*;

/**
 * Кастомные эффекты Виридиса. Реализованы на стандартных ParticleEffect/RadialEffect —
 * рендер внутри движка, аллокаций в рантайме нет.
 */
public class ViridisFx{

  /** Разрыв био-сока: бирюзовые осколки (крио-сифон, аварии). */
  public static Effect sapBurst = new ParticleEffect(){{
    particles = 8;
    lifetime = 16f;
    length = 22f; baseLength = 8f;
    sizeFrom = 2.4f; sizeTo = 0f;
    colorFrom = Color.valueOf("00ffd5");
    colorTo = Color.valueOf("1e3f20");
    line = true; strokeFrom = 1.4f; strokeTo = 0.4f;
    lightColor = Color.valueOf("00ffd5"); lightScl = 1.6f; lightOpacity = 0.3f;
  }};

  /** Всплеск желчи при аварийном сбросе автоклава. */
  public static Effect bileSplat = new ParticleEffect(){{
    particles = 10;
    lifetime = 14f;
    length = 18f; baseLength = 4f;
    sizeFrom = 3f; sizeTo = 0.4f;
    colorFrom = Color.valueOf("ffb700");
    colorTo = Color.valueOf("7a4d00");
    lightColor = Color.valueOf("ffb700"); lightScl = 1.2f; lightOpacity = 0.25f;
  }};

  /** Спорное облако (гибель фауны / прорыв корней). */
  public static Effect sporeCloud = new ParticleEffect(){{
    particles = 12;
    lifetime = 26f;
    length = 16f; baseLength = 3f;
    sizeFrom = 4f; sizeTo = 0f;
    colorFrom = Color.valueOf("c800ff");
    colorTo = Color.valueOf("3a0b4d");
    interp = arc.math.Interp.smooth;
    lightColor = Color.valueOf("c800ff"); lightScl = 1.4f; lightOpacity = 0.2f;
  }};

  /** Импульс по нейро-нити (синаптический разряд): radial искры лайм→пурпур. */
  public static Effect neuralPulse = new RadialEffect(new ParticleEffect(){{
    particles = 3;
    lifetime = 12f;
    length = 6f; baseLength = 2f;
    sizeFrom = 2.2f; sizeTo = 0f;
    colorFrom = Color.valueOf("55ff44");
    colorTo = Color.valueOf("c800ff");
  }}, 8, 6f, 45f);

  /** Корневой прорыв: зелёные шипы из земли (атака корней, С12+). */
  public static Effect rootSpikes = new RadialEffect(new ParticleEffect(){{
    particles = 4;
    lifetime = 22f;
    length = 26f; baseLength = 6f;
    sizeFrom = 2.6f; sizeTo = 0f;
    line = true; strokeFrom = 2.2f; strokeTo = 0.3f;
    colorFrom = Color.valueOf("55ff44");
    colorTo = Color.valueOf("c800ff");
    cone = 8f;
  }}, 6, 0f, 60f);

  /** Точка + цвет (перегрузка для единообразия вызовов из блоков). */
  public static void at(Effect e, Vec2 pos, Color c){
    e.at(pos.x, pos.y, c);
  }
}
