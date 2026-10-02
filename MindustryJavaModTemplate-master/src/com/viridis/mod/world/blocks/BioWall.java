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

  public static int timerRegen = -1;

  public BioWall(String name){
    super(name);
    timerRegen = ++timers;
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
    public void update(){
      super.update();
      timeSinceHit += Time.delta;
    }

    @Override
    public void damage(float amount){
      timeSinceHit = 0f;
      super.damage(amount);
    }

    @Override
    public void damage(Team team, float amount){
      timeSinceHit = 0f;
      super.damage(team, amount);
    }

    @Override
    public void damage(Bullet b, Team team, float amount){
      timeSinceHit = 0f;
      super.damage(b, team, amount);
    }

    @Override
    public void updateTile(){
      super.updateTile();
      //пассивная регенерация только вне боя: собственный таймер покоя timeSinceHit
      if(timeSinceHit >= regenDelay && health < maxHealth){
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
