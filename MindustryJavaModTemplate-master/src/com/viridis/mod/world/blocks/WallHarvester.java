package com.viridis.mod.world.blocks;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import com.viridis.mod.content.ViridisFx;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;

import com.viridis.mod.system.*;

import static mindustry.Vars.*;

/**
 * «Корневой надрезатель» — стенной бур.
 * Ставится на пол, сканирует солид-блоки в направлении ротации (стены ферро-флоры: solid && instanceof Wall).
 * Вибрация при добыче регистрируется в IrritationManager (всплеск раз в ~2 секунды, без опроса карты).
 */
public class WallHarvester extends Block{
  /** Предмет, который «содержат» стены (жилы металлов). */
  public Item wallDrop = null;
  public float harvestTime = 150f;
  /** Дельта раздражения в секунду при активной работе. */
  public float irritationPerSecond = 0.4f;
  public float rotateSpeed = 2.4f;
  public Effect updateEffect = ViridisFx.sapBurst;
  public float updateEffectChance = 0.03f;

  public final int timerVib = timers++;

  public WallHarvester(String name){
    super(name);
    size = 2;
    hasItems = true;
    itemCapacity = 12;
    rotate = true;
    update = true;
    solid = true;
    destructible = true;
    flags = EnumSet.of(BlockFlag.drill);
    envEnabled |= Env.space;
    category = Category.production;
  }

  @Override
  public void setStats(){
    super.setStats();
    stats.add(Stat.output, wallDrop);
    stats.add(Stat.drillSpeed, 60f / harvestTime * size, StatUnit.itemsSecond);
  }

  @Override
  public void setBars(){
    super.setBars();
    addBar("harvest", (WallHarvesterBuild e) -> new Bar(() -> Core.bundle.format("bar.drillspeed", Strings.fixed(e.lastEff * 60f / harvestTime, 2)), () -> Pal.ammo, () -> e.warmup));
  }

  @Override
  public boolean outputsItems(){
    return true;
  }

  @Override
  public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
    Draw.rect(region, plan.drawx(), plan.drawy(), plan.rotation * 90);
  }

  /** Стена ферро-флоры: неразрушимый солид-блок, являющийся стеной. */
  public static boolean isFerroWall(@Nullable Block b){
    return b != null && b.solid && (b instanceof StaticWall || b instanceof Wall);
  }

  /** Сколько целевых стен в линии ротации. */
  int scanCount(int tx, int ty, int rotation){
    int cornerX = tx - (size - 1)/2, cornerY = ty - (size - 1)/2;
    int n = 0;
    for(int i = 0; i < size; i++){
      int rx = 0, ry = 0;
      switch(rotation){
        case 0 -> { rx = cornerX + size; ry = cornerY + i; }
        case 1 -> { rx = cornerX + i; ry = cornerY + size; }
        case 2 -> { rx = cornerX - 1; ry = cornerY + i; }
        case 3 -> { rx = cornerX + i; ry = cornerY - 1; }
      }
      Tile other = world.tile(rx, ry);
      if(other != null && isFerroWall(other.block())) n++;
    }
    return n;
  }

  @Override
  public boolean canPlaceOn(Tile tile, Team team, int rotation){
    return scanCount(tile.x, tile.y, rotation) > 0;
  }

  @Override
  public void drawPlace(int x, int y, int rotation, boolean valid){
    super.drawPlace(x, y, rotation, valid);
    int n = scanCount(x, y, rotation);
    drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / harvestTime * n, 2), x, y, valid);
  }

  public class WallHarvesterBuild extends Building{
    public float time, warmup, totalTime, lastEff;
    boolean registered;

    @Override
    public void placed(){
      super.placed();
      //децентрализованная регистрация влияния при установке
      IrritationManager.register(this, irritationPerSecond);
      registered = true;
    }

    @Override
    public void onProximityRemoved(){
      super.onProximityRemoved();
      unregisterIrritation();
    }

    @Override
    public void handleDestroyed(DamageType type){
      unregisterIrritation();
      super.handleDestroyed(type);
    }

    void unregisterIrritation(){
      if(registered){
        IrritationManager.unregister(this);
        registered = false;
      }
    }

    @Override
    public void updateTile(){
      super.updateTile();

      int walls = scanCount(tile.x, tile.y, rotation);
      float eff = walls / (float)size;
      lastEff = eff;
      warmup = Mathf.approachDelta(warmup, Mathf.num(eff > 0f && shouldConsume()), 1f / 40f);

      if(walls > 0 && shouldConsume() && (time += edelta() * warmup * eff) >= harvestTime){
        time %= harvestTime;
        items.add(wallDrop, 1);
        produced(wallDrop);
        //вибрация: всплеск раздражения раз в 2 секунды активной добычи
        if(timer(timerVib, 120f)){
          IrritationManager.bump(0.004f);
        }
        if(wasVisible){
          Vec2 v = Geometry.d4(rotation);
          updateEffect.at(x + v.x * tilesize, y + v.y * tilesize, wallDrop.color);
        }
      }

      totalTime += warmup * edelta();

      if(timer(timerDump, dumpTime)){
        dump(wallDrop);
      }
    }

    @Override
    public boolean shouldConsume(){
      return items.get(wallDrop) < itemCapacity;
    }

    @Override
    public void draw(){
      Draw.rect(region, x, y, rotdeg());
      Draw.z(Layer.blockOver + 0.1f);
      Drawf.spinSprite(region, x, y, totalTime * rotateSpeed);
      Draw.z(Layer.block);
    }
  }
}
