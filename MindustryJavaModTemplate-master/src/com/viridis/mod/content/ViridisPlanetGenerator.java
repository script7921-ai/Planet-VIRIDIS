package com.viridis.mod.content;

import arc.math.geom.*;
import mindustry.game.*;
import mindustry.graphics.g3d.*;
import mindustry.maps.generators.*;
import mindustry.world.*;

/** Процедурная генерация: перегной + мох + фризо-почва, стены ферро-флоры полосами шума. */
public class ViridisPlanetGenerator extends PlanetGenerator{

  @Override
  protected void genTile(Vec3 position, TileGen tile){
    tile.floor = ViridisBlocks.humus;
    float n = noise(position.x*0.6f, position.y*0.6f, 4d, 0.55d, 2.1d, (double)(seed & 0xFFFF));
    if(n > 0.62f) tile.floor = ViridisBlocks.sporeTurf;
    else if(n < 0.30f) tile.floor = ViridisBlocks.cryoSoil;
    if(noise(position.x*1.3f, position.y*1.3f, 3d, 0.5d, 2.4d, 991d) > 0.72f)
      tile.block = ViridisBlocks.ferroflora;
  }

  @Override
  public int getSectorSize(Sector sector){
    return sector.id == 17 ? 130 : 100 + (Math.abs(sector.id) % 5) * 15;
  }
}
