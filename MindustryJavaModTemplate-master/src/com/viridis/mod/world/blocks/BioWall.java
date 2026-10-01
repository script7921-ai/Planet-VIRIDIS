package com.viridis.mod.world.blocks;

import arc.util.*;
import mindustry.gen.*;

/**
 * «Био-стена» (Хитиновая / Керато-бронестена): пассивная регенерация вне боя.
 * Состояние regenTimer сериализуется — multiplayer-ready.
 */
public class BioWall extends mindustry.world.blocks.defense.Wall{
  /** Тиков между тиками регенера. */
  public float regenInterval = 30f;
  /** HP, восстанавливаемые за тик регенера (когда стена не получала урон recently). */
  public float regenAmount = 1f;
  /** Сколько тиков после получения урона регенер отключён. */
  public float regenDelay = 240f;

  public final int timerRegen = timers++;

  public BioWall(String name){
    super(name);
    update = true;
    destructible = true;
  }

  @Override
  public void init(){
    super.init();
    //стены дешёвые по восстановлению — стандартное поведение Wall сохраняем
  }

  public class BioWallBuild extends Building{
    /** Тики с последнего урона (transient — выводится из lastDamageTime на клиенте не требуется). */
    public float timeSinceHit = 9999f;

    @Override
    public void handleDestroyed(DamageType type){
      super.handleDestroyed(type);
    }

    @Override
    public void updateTile(){
      super.updateTile();
      //пассивный регенер только вне боя: used recent-damage флаг движка
      if(!wasRecentlyDamaged() && health < maxHealth){
        if(timer(timerRegen, regenInterval)){
          health = Math.min(maxHealth, health + regenAmount);
        }
      }
    }

    //=== сетевая безопасность: время покоя влияет на геймплей → сериализуется ===
    @Override
    public void write(Writes write){
      super.write(write);
      write.f(timeSinceHit);
    }

    @Override
    public void read(Reads read, byte revision){
      super.read(read, revision);
      timeSinceHit = read.f();
    }

    @Override
    public byte version(){
      return 1;
    }
  }
}
