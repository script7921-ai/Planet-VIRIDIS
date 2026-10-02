package com.viridis.mod;

import arc.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import mindustry.mod.*;

import com.viridis.mod.content.*;
import com.viridis.mod.system.*;

/**
 * Точка входа мода «Виридис».
 * Порядок загрузки критичен: items/liquids → blocks (ссылаются на них) → units → planet → techTree.
 */
public class ViridisMod extends Mod{

  public ViridisMod(){
    //контент должен быть зарегистрирован до ClientLoadEvent — стандартный путь Mindustry вызывает loadContent() сам
  }

  @Override
  public void loadContent(){
    ViridisItems.load();
    ViridisLiquids.load();
    ViridisTechConstants.init();
    ViridisBlocks.load();
    ViridisUnitTypes.load();
    ViridisPlanets.load();
    ViridisTechTree.build();
  }

  @Override
  public void init(){
    //синглтон раздражения + HUD био-сканера
    IrritationManager.init();
    VascularOverlay.init();

    Events.on(EventType.ClientLoadEvent.class, e -> {
      //бандл локализации подтягивается автоматически из assets/bundles (если есть); здесь — только хуки
    });

    //мировой оверлей «Vascular Vision»: zero-alloc — переопределяем draw() невидимого Group поверх HUD
    VascularOverlay.installDrawHook();
  }
}
