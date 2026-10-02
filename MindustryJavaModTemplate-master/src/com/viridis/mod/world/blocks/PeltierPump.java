package com.viridis.mod.world.blocks;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.production.*;

import com.viridis.mod.content.*;
import com.viridis.mod.system.*;

import java.util.*;

import static mindustry.Vars.tilesize;

/**
 * «Крио-сифон» (Пельтье-помпа, С10): качает Эндо-сок из Фризо-почвы,
 * но работает как тепловой насос — НАГРЕВАЕТ зону вокруг себя.
 * Если тепло не отводится (нет питания/эффективности), бак перегревается и помпа взрывается.
 * Наследует SolidPump: result(Liquid) = cryoSap, attribute = steam/ice-like.
 */
public class PeltierPump extends SolidPump{
  /** Накопленное тепло 0..heatMax; при достижении heatMax — детонация. */
  public float heatPerTick = 0.5f;
  /** Скорость пассивного остывания за тик. */
  public float coolRate = 0.35f;
  public float heatMax = 100f;
  /** Дельта раздражения в секунду (сейсмический шум откачки). */
  public float irritationPerSecond = 0.3f;

  public static int timerVib = -1;

  public PeltierPump(String name){
    super(name);
    timerVib = ++timers;
    hasLiquids = true;
    update = true;
    destructible = true;
    envEnabled |= Env.space;
    category = Category.crafting;
  }

  @Override
  public void init(){
    super.init();
    if(liquidCapacity == 0f) liquidCapacity = 40f;
  }

  static float absSin(float a){ return Math.abs(Mathf.sin(a)); }

  public class PeltierPumpBuild extends SolidPumpBuild{
    public float heat;
    boolean registered;

    @Override
    public void placed(){
      super.placed();
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
      unregister();
      //перегретая помпа разрывает контур: желчная авария + всплеск ярости
      if(heat >= heatMax * 0.7f){
        ViridisFx.sapBurst.at(x, y, Color.valueOf("00ffd5"));
        IrritationManager.bump(0.02f);
        Damage.damage(team, x, y, tilesize * 4f, 60f, false, false, false);
      }
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

      boolean pumping = efficiency > 0f && liquids.get(result) < liquidCapacity;

      if(pumping){
        //теплонасос: чем активнее качаем — тем больше тепла в контуре
        heat += heatPerTick * edelta() * warmup;
        //всплеск вибрации раз в 2 сек
        if(timer(timerVib, 120f)){
          IrritationManager.bump(0.003f);
        }
      }else{
        heat -= coolRate * Time.delta;
      }
      heat = Mathf.clamp(heat, 0f, heatMax);

      //ДЕТОНАЦИЯ перегрева
      if(heat >= heatMax){
        Fx.explosion.at(x, y);
        Sounds.explosion.at(x, y);
        kill();
      }

      //тепловой след видим: пар над помпой
      if(wasVisible && heat > heatMax * 0.5f && Mathf.chanceDelta(0.08f)){
        ViridisFx.sapBurst.at(x + Mathf.range(6f), y + Mathf.range(6f));
      }
    }

    @Override
    public void draw(){
      super.draw();
      //индикатор нагрева: пульсирующее янтарное кольцо при heat > 60%
      if(heat > heatMax * 0.6f){
        Draw.z(Layer.blockOver + 0.1f);
        Draw.color(Color.valueOf("ff9900"));
        Draw.alpha(0.4f + 0.3f * absSin(Time.time * 0.2f));
        Lines.stroke(1.5f);
        Lines.circle(x, y, 6f + size * 4f);
        Draw.reset();
      }
    }

    //=== сетевая безопасность: heat влияет на геймплей → ОБЯЗАТЕЛЬНО сериализуется ===
    @Override
    public void write(Writes write){
      super.write(write);
      write.f(heat);
    }

    @Override
    public void read(Reads read, byte revision){
      super.read(read, revision);
      heat = read.f();
    }

    @Override
    public byte version(){
      return 1;
    }
  }
}
