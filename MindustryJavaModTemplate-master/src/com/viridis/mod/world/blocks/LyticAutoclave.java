package com.viridis.mod.world.blocks;

import java.util.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.Env;
import mindustry.world.blocks.production.*;

import com.viridis.mod.content.*;
import com.viridis.mod.system.*;

import static mindustry.Vars.*;

/**
 * «Литический Автоклав» (С4): перерабатывает Био-ячейки в Био-кремний + Био-жижу.
 * Ключевая механика ТЗ: при аварийном ПЕРЕПОЛНЕНИИ жидкостного бака
 * вместо трассы выбрасывает ЖЕЛЧЬ на тайлы вокруг и даёт всплеск Раздражения.
 * Наследуется от GenericCrafter — состояние (progress/warmup) сериализуется родителем.
 */
public class LyticAutoclave extends GenericCrafter{
  /** Предмет на входе. */
  public Item input = null;
  /** Аварийный продукт переполнения. */
  public Liquid overflowFluid = null;
  /** Дельта раздражения в секунду (термический след). */
  public float irritationPerSecond = 0.5f;
  /** Порог срабатывания аварии (доля заполнения бака). */
  public float overflowThreshold = 0.92f;

  public final int timerDumpBile = timers++;

  public LyticAutoclave(String name){
    super(name);
    hasLiquids = true;
    hasItems = true;
    solid = true;
    update = true;
    destructible = true;
    envEnabled |= Env.space;
    category = Category.crafting;
  }

  @Override
  public void init(){
    super.init();
    if(liquidCapacity == 0f) liquidCapacity = 30f;
  }

  public class LyticAutoclaveBuild extends GenericCrafterBuild{
    boolean registered;

    @Override
    public void placed(){
      super.placed();
      //децентрализованная регистрация термического следа
      IrritationManager.register(this, irritationPerSecond);
      registered = true;
    }

    @Override
    public void onProximityRemoved(){
      super.onProximityRemoved();
      unregister();
    }

    @Override
    public void onDestroyed(){
      //при гибели — остаточный разрыв желчи
      if(overflowFluid != null && wasVisible){
        ViridisFx.bileSplat.at(x, y);
        IrritationManager.bump(0.01f);
      }
      unregister();
      super.onDestroyed();
    }

    void unregister(){
      if(registered){
        IrritationManager.unregister(this);
        registered = false;
      }
    }

    @Override
    public void updateTile(){
      super.updateTile();

      //аварийный сброс: бак забит сверх порога — сливаем Желчь наружу раз в ~1 сек
      if(overflowFluid != null && liquids.get(overflowFluid) >= overflowThreshold * liquidCapacity){
        if(timer(timerDumpBile, 60f)){
          float excess = Math.min(liquids.get(overflowFluid) - overflowThreshold * liquidCapacity, 0.8f);
          liquids.remove(overflowFluid, excess);
          //прорыв наружу: лужа вокруг автоклава (Puddles.deposit по соседним тайлам) + раздражение планеты
          for(int i = 0; i < 4; i++){
            Tile near = tile.nearby(i);
            if(near != null && near.block() != null && !near.block().solid) Puddles.deposit(near, overflowFluid, 0.35f);
          }
          if(wasVisible){
            ViridisFx.bileSplat.at(x, y);
          }
          IrritationManager.bump(0.006f);
        }
      }
    }

    @Override
    public boolean shouldConsume(){
      //обычная логика родителя + запрет крафта при критическом переполнении (чтобы авария была видимой)
      return super.shouldConsume() && (overflowFluid == null || liquids.get(overflowFluid) < liquidCapacity * 1.05f);
    }
  }
}
