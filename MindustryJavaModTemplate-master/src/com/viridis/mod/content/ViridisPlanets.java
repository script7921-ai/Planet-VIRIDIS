package com.viridis.mod.content;

import arc.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.content.*;
import mindustry.game.*;
import com.viridis.mod.system.IrritationManager;

public class ViridisPlanets{
  public static Planet viridis;
  public static SectorPreset
    touchpoint, emeraldShallows, rootPath, boilingGarden,      // релиз 1
    allergy, rift, ferroTerrace, hiveQueen,                    // релиз 2
    forgottenCanopy, frostSpike, acidCanyon, maw, sleepingNode,// релиз 3
    nerveOutgrowths, runoff, barkCitadel, heart;               // релиз 4

  public static void load(){
    viridis = new Planet("viridis", Planets.serpulo, 1.1f, 8){{
      generator = new ViridisPlanetGenerator();
      meshLoader = () -> new HexMesh(this, 6);
      cloudMeshLoader = () -> new HexSkyMesh(this, 11, 0.15f, 0.5f, 5, Color.valueOf("1e3f20").a(0.75f), 3, 0.0f, 3.2f, 0.7f);
      iconColor = Color.valueOf("55ff44");
      defaultCore = ViridisBlocks.coreSprout;
      startSector = 17;
      allowWaves = true;
      ruleSetter = r -> {
        r.waveTeam = Team.crux;
        r.placeRangeCheck = false;
        r.hideSpawns = true;
        r.coreIncinerates = true;
      };
      orbitRadius = 30f;
      orbitSpacing = 19f;
      solarSystem = Planets.serpulo;
    }};

    touchpoint      = preset("viridis-touchpoint",       "Точка Касания",     17, 0,  1f);
    emeraldShallows = preset("viridis-emerald-shallows", "Изумрудная Заводь", 18, 8,  2f);
    rootPath        = preset("viridis-root-path",        "Корневой Лабиринт", 26, 10, 3f);
    boilingGarden   = preset("viridis-boiling-garden",   "Кипящий Сад",       34, 16, 4f);
    allergy         = preset("viridis-allergy",          "Аллергия",          35, 20, 5f);
    rift            = preset("viridis-rift",             "Гнилостный Разлом", 43, 24, 6f);
    ferroTerrace    = preset("viridis-ferro-terrace",    "Броне-Терраса",     42, 30, 7f);
    hiveQueen       = preset("viridis-hive",             "Улей Матки",        50, 36, 8f);
    forgottenCanopy = preset("viridis-canopy",           "Забытый Полог",     58, 40, 9f);
    frostSpike      = preset("viridis-frost-spike",      "Морозный Шип",      57, 46, 10f);
    acidCanyon      = preset("viridis-acid-canyon",      "Кислотный Каньон",  49, 50, 11f);
    maw             = preset("viridis-maw",              "Чрево",             41, 56, 12f);
    sleepingNode    = preset("viridis-sleeping-node",    "Спящий Узел",       33, 60, 13f);
    nerveOutgrowths = preset("viridis-nerves",           "Нервные Отростки",  25, 64, 14f);
    runoff          = preset("viridis-runoff",           "Первобытный Сток",  19, 68, 15f);
    barkCitadel     = preset("viridis-citadel",          "Цитадель Коры",     11, 70, 16f);
    heart           = preset("viridis-heart",            "Сердце Виридиса",    3, 74, 20f);
  }

  static SectorPreset preset(String id, String locName, int sector, int wave, float diff){
    SectorPreset p = new SectorPreset(id, viridis, sector);
    p.captureWave = wave;
    p.difficulty = diff;
    p.localizedName = locName;
    p.startWaveTimeMultiplier = 2.6f;
    if(sector == 17) p.rules = r -> IrritationManager.locked = true; // планета спит
    if(sector == 3)  p.isLastSector = true;                          // финал
    return p;
  }
}
