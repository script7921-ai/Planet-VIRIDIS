package com.viridis.mod.content;

import mindustry.content.*;
import mindustry.type.*;

import static mindustry.type.ItemStack.with;

/**
 * Дерево исследований Виридиса (4 релиза).
 * Корень — хитин; ветки: добыча → переработка → оборона → юниты → финал («Сердце Виридиса»).
 */
public class ViridisTechTree{

  public static void build(){
    TechTree.nodeRoot("viridis", ViridisItems.chitin, () -> {

      //--- релиз 1: базовая цепочка ---
      TechTree.node(ViridisItems.ferroBark, with(ViridisItems.chitin, 50), () -> {
        TechTree.node(ViridisBlocks.rootCarver, with(ViridisItems.ferroBark, 40), () -> {});
        TechTree.node(ViridisBlocks.sapPress, with(ViridisItems.ferroBark, 60), () -> {
          TechTree.node(ViridisLiquids.bioSludge, () -> {});
          TechTree.node(ViridisBlocks.mossFilter, with(ViridisItems.silicaMoss, 60), () -> {
            TechTree.node(ViridisItems.luminite, () -> {});
          });
        });
        TechTree.node(ViridisBlocks.chitinTray, with(ViridisItems.chitin, 80), () -> {
          TechTree.node(ViridisBlocks.cornerJunction, () -> {});
          TechTree.node(ViridisBlocks.bioVault, with(ViridisItems.chitin, 150), () -> {});
        });
      });

      //--- релиз 2: биохимия и оборона ---
      TechTree.node(ViridisItems.bioCell, with(ViridisItems.chitin, 120), () -> {
        TechTree.node(ViridisBlocks.autoclave, with(ViridisItems.bioCell, 60), () -> {
          TechTree.node(ViridisLiquids.bile, () -> {});
          TechTree.node(ViridisItems.bioSilicon, with(ViridisItems.silicaMoss, 80), () -> {
            TechTree.node(ViridisBlocks.bioSiliconForge, with(ViridisItems.bioCell, 90), () -> {});
          });
        });
        TechTree.node(ViridisBlocks.chitinWall, with(ViridisItems.chitin, 100), () -> {
          TechTree.node(ViridisBlocks.needleTurret, with(ViridisItems.ferroBark, 80), () -> {
            TechTree.node(ViridisBlocks.bileMortar, with(ViridisItems.bioCell, 70), () -> {});
          });
        });
      });

      //--- релиз 3: криогеника и юниты ---
      TechTree.node(ViridisUnitTypes.centipede, with(ViridisItems.bioCell, 100), () -> {
        TechTree.node(ViridisBlocks.cryoSiphon, with(ViridisItems.bioSilicon, 80), () -> {
          TechTree.node(ViridisLiquids.cryoSap, () -> {});
          TechTree.node(ViridisBlocks.neuralWeaver, with(ViridisItems.bioSilicon, 120), () -> {
            TechTree.node(ViridisItems.neuralFiber, () -> {});
            TechTree.node(ViridisBlocks.neuroSpire, with(ViridisItems.neuralFiber, 60), () -> {
              TechTree.node(ViridisBlocks.irritationMonitor, () -> {});
              TechTree.node(ViridisBlocks.seismoDampener, () -> {});
            });
          });
        });
        TechTree.node(ViridisUnitTypes.titanCrab, with(ViridisItems.keratinAlloy, 60), () -> {
          TechTree.node(ViridisItems.keratinAlloy, with(ViridisItems.bioCell, 150), () -> {
            TechTree.node(ViridisBlocks.keratinFoundry, with(ViridisItems.ferroBark, 120), () -> {});
            TechTree.node(ViridisBlocks.keratoBastion, with(ViridisItems.keratinAlloy, 80), () -> {});
          });
          TechTree.node(ViridisUnitTypes.gnawer, () -> {});
          TechTree.node(ViridisUnitTypes.skater, () -> {});
        });
      });

      //--- релиз 4: титаны и финал ---
      TechTree.node(ViridisItems.nitroPollen, with(ViridisItems.bioCell, 200), () -> {
        TechTree.node(ViridisBlocks.pollenLab, with(ViridisItems.luminite, 100), () -> {});
        TechTree.node(ViridisItems.lignoPlast, with(ViridisItems.ferroBark, 150), () -> {
          TechTree.node(ViridisBlocks.lignoPress, with(ViridisItems.lignoPlast, 80), () -> {});
        });
        TechTree.node(ViridisUnitTypes.goliath, with(ViridisItems.keratinAlloy, 150), () -> {
          TechTree.node(ViridisUnitTypes.bioTitan, with(ViridisItems.neuralFiber, 120), () -> {
            TechTree.node(ViridisUnitTypes.leviathan, with(ViridisItems.bioSilicon, 200), () -> {});
          });
          TechTree.node(ViridisBlocks.defoliant, with(ViridisItems.nitroPollen, 100), () -> {});
        });
        //финал: контроль биосферы
        TechTree.node(ViridisPlanets.viridis, with(ViridisItems.neuralFiber, 250, ViridisItems.luminite, 200), () -> {
          TechTree.nodeProduce(ViridisItems.fossilAmber, () -> {});
        });
      });
    });
  }
}
