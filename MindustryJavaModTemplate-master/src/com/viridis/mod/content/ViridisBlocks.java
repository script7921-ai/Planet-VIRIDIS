package com.viridis.mod.content;

import arc.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.draw.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.world.meta.Env;
import mindustry.gen.*;
import mindustry.content.*;
import com.viridis.mod.world.blocks.*;
import static mindustry.type.ItemStack.with;

public class ViridisBlocks{
  //=== окружение ===
  public static Block humus, sporeTurf, cryoSoil, bilePuddleFloor;
  public static Block ferroflora;      // неразрушимая стена
  public static Block underbrush;      // разрушаемый подлесок (Prop)
  //=== логистика/хранилище ===
  public static Block chitinTray, cornerJunction, bioVault, coreSprout;
  //=== производство ===
  public static WallHarvester rootCarver;
  public static LyticAutoclave autoclave;
  public static PeltierPump cryoSiphon;
  public static GenericCrafter sapPress, mossFilter, bioSiliconForge, neuralWeaver, keratinFoundry;
  public static GenericCrafter lignoPress, pollenLab;
  public static Pump waterPump;
  public static Block seismoDampener;
  //=== оборона ===
  public static BioWall chitinWall, keratoBastion;
  public static ItemTurret needleTurret, bileMortar, defoliant;
  //=== спец ===
  public static Block irritationMonitor, neuroSpire;

  public static void load(){
    //--- floors ---
    humus = new Floor("viridis-humus"){{ variants = 4; }};
    sporeTurf = new Floor("viridis-spore-turf"){{ variants = 3; damageTaken = 0.05f; }};
    cryoSoil = new Floor("viridis-cryo-soil"){{ variants = 3; }};
    bilePuddleFloor = new Floor("viridis-bile-pool"){{
      isLiquid = true; liquidDrop = ViridisLiquids.bile; speedMultiplier = 0.5f;
    }};
    ferroflora = new StaticWall("viridis-ferroflora"){{ variants = 4; }};
    underbrush = new Prop("viridis-underbrush"){{ variants = 3; health = 40; destructible = true; }};

    //--- core ---
    coreSprout = new CoreBlock("viridis-core-sprout"){{
      envEnabled |= Env.terrestrial;
      health = 1400; size = 3; itemCapacity = 4000;
      unitType = ViridisUnitTypes.bee;
      alwaysUnlocked = true;
      requirements(Category.effect, with(ViridisItems.chitin, 300, ViridisItems.fossilAmber, 120));
    }};

    //--- logistics ---
    chitinTray = new Conveyor("viridis-chitin-tray"){{
      speed = 0.7f;
      requirements(Category.units, with(ViridisItems.chitin, 1));
    }};
    cornerJunction = new Junction("viridis-corner-junction"){{
      requirements(Category.units, with(ViridisItems.chitin, 2, ViridisItems.silicaMoss, 1));
    }};
    bioVault = new StorageBlock("viridis-bio-vault"){{
      itemCapacity = 1600;
      requirements(Category.effect, with(ViridisItems.chitin, 60, ViridisItems.ferroBark, 30));
    }};

    //--- production ---
    rootCarver = new WallHarvester("viridis-root-carver"){{
      wallDrop = ViridisItems.ferroBark; harvestTime = 150f;
      health = 140;
      requirements(Category.production, with(ViridisItems.chitin, 45, ViridisItems.fossilAmber, 20));
    }};
    sapPress = new GenericCrafter("viridis-sap-press"){{
      size = 2; craftTime = 90f;
      drawer = new DrawMulti(new DrawDefault(), new DrawRegion("-rotator"));
      consumeItem(ViridisItems.bioCell);
      outputLiquid = new LiquidStack(ViridisLiquids.bioSludge, 0.1f);
      requirements(Category.crafting, with(ViridisItems.chitin, 30));
    }};
    waterPump = new Pump("viridis-water-pump"){{
      size = 3; pumpAmount = 0.25f;
      requirements(Category.crafting, with(ViridisItems.chitin, 30));
    }};
    mossFilter = new GenericCrafter("viridis-moss-filter"){{
      craftTime = 60f; size = 2;
      consumeItem(ViridisItems.silicaMoss, 2);
      outputItem = new ItemStack(ViridisItems.silicaMoss, 1);
      requirements(Category.crafting, with(ViridisItems.chitin, 20));
    }};
    autoclave = new LyticAutoclave("viridis-lytic-autoclave"){{
      input = ViridisItems.bioCell;
      consumeItem(ViridisItems.bioCell, 2);
      outputItem = new ItemStack(ViridisItems.bioSilicon, 1);
      outputLiquid = new LiquidStack(ViridisLiquids.bioSludge, 0.18f);
      overflowFluid = ViridisLiquids.bile;
      size = 3; craftTime = 110f;
      requirements(Category.crafting, with(ViridisItems.chitin, 90, ViridisItems.ferroBark, 40));
    }};
    cryoSiphon = new PeltierPump("viridis-cryo-siphon"){{
      result = ViridisLiquids.cryoSap; pumpAmount = 0.25f; size = 3;
      consumePower(1.8f);
      requirements(Category.crafting, with(ViridisItems.keratinAlloy, 40, ViridisItems.luminite, 30));
    }};
    seismoDampener = new LightBlock("viridis-seismo-dampener"){{
      size = 1; health = 90; radius = 60f;
      requirements(Category.effect, with(ViridisItems.chitin, 25, ViridisItems.fossilAmber, 10));
    }};

    //--- synthesis ---
    bioSiliconForge = new GenericCrafter("viridis-bio-silicon-forge"){{
      craftTime = 120f; size = 3;
      consumeItem(ViridisItems.silicaMoss, 3);
      consumeLiquid(ViridisLiquids.bioSludge, 0.1f);
      outputItem = new ItemStack(ViridisItems.bioSilicon, 1);
      requirements(Category.crafting, with(ViridisItems.bioSilicon, 40));
    }};
    keratinFoundry = new GenericCrafter("viridis-keratin-foundry"){{
      size = 3; craftTime = 150f;
      consumeItems(new ItemStack(ViridisItems.ferroBark, 2), new ItemStack(ViridisItems.chitin, 2));
      consumeLiquid(ViridisLiquids.bile, 0.05f);
      outputItem = new ItemStack(ViridisItems.keratinAlloy, 1);
      requirements(Category.crafting, with(ViridisItems.ferroBark, 60));
    }};
    //замена отсутствующего Press: лигнопресс — обычный крафтер
    lignoPress = new GenericCrafter("viridis-ligno-press"){{
      size = 3; craftTime = 100f;
      consumeItems(new ItemStack(ViridisItems.fossilAmber, 2), new ItemStack(ViridisItems.bioCell, 1));
      consumeLiquid(ViridisLiquids.bioSludge, 0.15f);
      outputItem = new ItemStack(ViridisItems.lignoPlast, 1);
      requirements(Category.crafting, with(ViridisItems.fossilAmber, 50));
    }};
    //замена отсутствующего Mixer: поленовая лаборатория — обычный крафтер
    Block pollenLab = new GenericCrafter("viridis-pollen-lab"){{
      size = 2; craftTime = 90f;
      consumeItems(new ItemStack(ViridisItems.luminite, 1), new ItemStack(ViridisItems.bioCell, 2));
      outputItem = new ItemStack(ViridisItems.nitroPollen, 1);
      requirements(Category.crafting, with(ViridisItems.luminite, 40));
    }};
    neuralWeaver = new GenericCrafter("viridis-neural-weaver"){{
      size = 3; craftTime = 200f;
      consumeItems(new ItemStack(ViridisItems.radBulb, 1), new ItemStack(ViridisItems.luminite, 2));
      consumeLiquid(ViridisLiquids.cryoSap, 0.1f);
      outputItem = new ItemStack(ViridisItems.neuralFiber, 1);
      requirements(Category.crafting, with(ViridisItems.radBulb, 50));
    }};

    //--- defense ---
    chitinWall = new BioWall("viridis-chitin-wall"){{
      health = 380; regenAmount = 1.2f;
      requirements(Category.defense, with(ViridisItems.chitin, 4));
    }};
    keratoBastion = new BioWall("viridis-kerato-bastion"){{
      health = 1600; regenAmount = 4f; size = 2;
      requirements(Category.defense, with(ViridisItems.keratinAlloy, 6));
    }};
    needleTurret = new ItemTurret("viridis-needle-thrower"){{
      size = 1; range = 130f; reload = 14f; inaccuracy = 3f; shootCone = 8f;
      ammo(ViridisItems.chitin, new BasicBulletType(5f, 9f, "bullet"){{ hitSize = 4f; }});
      ammo(ViridisItems.ferroBark, new BasicBulletType(5.5f, 14f, "bullet"){{ hitSize = 4f; pierceCap = 2; }});
      requirements(Category.turret, with(ViridisItems.chitin, 35));
    }};
    bileMortar = new ItemTurret("viridis-bile-mortar"){{
      size = 3; range = 240f; reload = 90f; inaccuracy = 6f; shootCone = 20f;
      ammo(ViridisItems.nitroPollen, new ArtilleryBulletType(3f, 26f){{
        splashDamageRadius = 24f; splashDamage = 30f; collidesGround = true;
      }});
      requirements(Category.turret, with(ViridisItems.ferroBark, 60, ViridisItems.chitin, 40));
    }};
    defoliant = new ItemTurret("viridis-defoliant"){{
      size = 4; range = 320f; reload = 140f;
      ammo(ViridisItems.nitroPollen, new ArtilleryBulletType(2.6f, 40f){{
        splashDamageRadius = 40f; splashDamage = 55f;
      }});
      requirements(Category.turret, with(ViridisItems.keratinAlloy, 80, ViridisItems.luminite, 50));
    }};
    irritationMonitor = new PowerTurret("viridis-irritation-monitor"){{
      size = 2; range = 0f; consumePower(0.2f);
      requirements(Category.effect, with(ViridisItems.bioSilicon, 30, ViridisItems.luminite, 20));
    }};
    neuroSpire = new PowerNode("viridis-neuro-spire"){{
      size = 2; maxRange = 260f;
      requirements(Category.effect, with(ViridisItems.neuralFiber, 20, ViridisItems.keratinAlloy, 30));
    }};
  }
}
