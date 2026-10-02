package com.viridis.mod.content;

import mindustry.type.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.ai.types.*;
import arc.struct.*;

public class ViridisUnitTypes{
  public static UnitType bee, centipede, titanCrab, goliath, bioTitan;
  public static UnitType gnawer, skater, leviathan; // фауна (враги)

  public static void load(){
    bee = new UnitType("viridis-bee"){{
      controller = u -> new MinerAI();
      health = 45f; speed = 0.55f; flying = true; lowAltitude = true;
      mineSpeed = 5f; mineTier = 1; mineItems = Seq.with(ViridisItems.chitin, ViridisItems.silicaMoss);
      itemCapacity = 20; hitSize = 7f; engineSize = 2.1f;
      constructor = UnitEntity::create;
    }};
    centipede = new UnitType("viridis-centipede"){{
      controller = u -> new DefenderAI();
      health = 260f; speed = 0.42f; hitSize = 11f; armor = 3f;
      constructor = UnitEntity::create;
    }};
    centipede.weapons.add(new Weapon("viridis-centipede-gun"){{
      bullet = new BasicBulletType(4.5f, 12f, "missileLarge"){{ sprite = "missileSmall"; hitSize = 4f; }};
      reload = 30f; x = 4f; y = 0f; top = false;
    }});
    titanCrab = new UnitType("viridis-titan-crab"){{
      controller = u -> new DefenderAI();
      health = 900f; speed = 0.3f; hitSize = 16f; armor = 12f;
      constructor = UnitEntity::create;
    }};
    goliath = new UnitType("viridis-goliath"){{
      controller = u -> new DefenderAI();
      health = 2600f; speed = 0.2f; hitSize = 22f; armor = 25f;
      constructor = UnitEntity::create;
    }};
    bioTitan = new UnitType("viridis-bio-titan"){{
      controller = u -> new DefenderAI();
      health = 6500f; speed = 0.15f; hitSize = 30f; armor = 40f;
      constructor = UnitEntity::create;
    }};
    //=== фауна (Team.crux) ===
    gnawer = new UnitType("viridis-gnawer"){{
      controller = u -> new GroundAI();
      health = 60f; speed = 0.6f; hitSize = 8f; isEnemy = true;
      constructor = UnitEntity::create;
    }};
    skater = new UnitType("viridis-skater"){{
      controller = u -> new FlyingAI();
      health = 90f; speed = 0.8f; flying = true; hitSize = 9f; isEnemy = true;
      constructor = UnitEntity::create;
    }};
    leviathan = new UnitType("viridis-leviathan"){{
      controller = u -> new GroundAI();
      health = 4200f; speed = 0.18f; hitSize = 28f; armor = 30f; isEnemy = true; faceTarget = false;
      constructor = UnitEntity::create;
    }};
  }
}
