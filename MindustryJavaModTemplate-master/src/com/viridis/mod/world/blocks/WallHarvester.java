package com.viridis.mod.world.blocks;

import arc.math.*;
import arc.struct.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;
import com.viridis.mod.system.*;

/**
 * «Корневой надрезатель» — стенной бур.
 * Ставится на пол (перекрытый), но сканирует СМЕЖНЫЕ солид-блоки (стены ферро-флоры instanceof Wall).
 * При добыче поднимает вибрацию → регистрирует дельту раздражения в IrritationManager.
 */
public class WallHarvester extends Drill{
  /** Предмет, который «содержат» стены (жилы металлов). */
  public Item wallDrop = null;
  public float harvestTime = 90f;
  /** Дельта раздражения в секунду при активной работе. */
  public float irritationPerSecond = 0.4f;

  public WallHarvester(String name){
    super(name);
    size = 2;
    solid = true;
    updateLiquids = true;
    drillTime = harvestTime;
    tier = 4;
    flags = IntSet.with(BlockFlag.expandDuctless ? BlockFlag.duct : 0); //no-op safe default below
    flags = IntSet.with(0);
    itemCapacity = 10;
    liquidCapacity = 20f;
    //нет тайловой добычи — вся логика в updateTile()
    tiles = false;
  }

  {
    //после конструктора: корректный набор флагов
    flags = IntSet.with(BlockFlag.waterExtract);
  }

  @Override
  public boolean canPlaceOn(Tile tile, Team team, int rotation){
    //хотя бы одна смежная стена-препятствие
    for(Point2 p : Geometry.d4){
      Tile other = tile.near(p.x, p.y);
      if(other != null && isFerroWall(other.block())) return true;
    }
    return false;
  }

  /** Стена ферро-флоры: неразрушимый солид-блок, являющийся стеной. */
  public static boolean isFerroWall(Block b){
    return b != null && b.solid && b instanceof Wall;
  }

  @Override
  public void init(){
    //не полагаться на стандартный oreItemDrillTime — дроп задаётся вручную
    dropItem = wallDrop;
    super.init();
  }

  public class WallHarvesterBuild extends DrillBuild{
    float vibTimer = 0f;
    boolean registered = false;

    @Override
    public void placed(){
      super.placed();
      IrritationManager.register(this, irritationPerSecond);
      registered = true;
    }

    @Override
    public void onProximityRemoved(){
      super.onProximityRemoved();
      if(registered){
        IrritationManager.unregister(this);
        registered = false;
      }
    }

    @Override
    public void updateTile(){
      //скан смежных стен каждый ~30 тиков, без опроса карты
      boolean mining = false;
      if(timer(timerWarm, 30f)){
        for(Point2 p : Geometry.d4){
          Tile other = tile.near(p.x, p.y);
          if(other != null && isFerroWall(other.block()) && other.floor() != null){
            mining = true;
            break;
          }
        }
      } else {
        mining = !items.isEmpty() || progress > 0f;
      }

      if(mining && efficiency > 0f){
        progress += timeDelta / harvestTime * warmup;
        timeDrilled += timeDelta;
        vibTimer += timeDelta;
        //вибрация: раз в 2 секунды всплеск раздражения
        if(vibTimer > 120f){
          vibTimer = 0f;
          IrritationManager.bump(0.004f);
        }
        if(progress >= 1f){
          progress = 0f;
          if(wallDrop != null && handleItem(null, wallDrop)){
            produced(wallDrop);
            Events.fire(new ItemDropEvent(tile.worldx(), tile.worldy(), wallDrop));
          }else if(wallDrop != null){
            items.add(wallDrop, 1);
          }
        }
      }else{
        progress = Mathf.zeroOut(progress, 0.02f);
      }
      //разгрузка в конвейеры/ядро
      if(!items.empty()){
        dump(wallDrop);
      }
    }

    @Override
    public boolean shouldConsume(){
      return items.get(wallDrop) < itemCapacity;
    }

    @Override
    public void draw(){
      drawBase();
      //вращающийся резак
      Drawf.spinSprite(Core.atlas.find("viridis-wall-harvester-rotator"), x, y, rotate ? rotation*90f + time() * 2f : time()*2f);
    }
  }

  /** Пустотелый конструктор для ItemDropEvent не нужен — используем встроенный EventHandle. */
  static class Dummy{}
}
