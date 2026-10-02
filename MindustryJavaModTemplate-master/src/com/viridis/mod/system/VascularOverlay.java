package com.viridis.mod.system;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;

/**
 * HUD «био-сканера» Виридиса:
 * 1) Счетчик Раздражения — пульсирующий нервный узел в колбе с кардиограммой (правый край, под миникартой).
 * 2) Vascular Vision — клавиша V: зелёный светофильтр + подсветка стен ферро-флоры/вен.
 * Zero-allocation в act/draw: только Tmp.c1 и статические цвета.
 */
public class VascularOverlay{
  //палитра интерфейса «био-сканера»
  public static final Color panelBack = Color.valueOf("0b120c");
  static final Color colCalm = Color.valueOf("55ff44");
  static final Color colWarn = Color.valueOf("ff9900");
  static final Color colRage = Color.valueOf("c800ff");

  /** Режим обзора «Сосудистое зрение». */
  public static boolean vascularVision = false;

  static float pulse = 0f;
  static Table gaugeTable;

  public static void init(){
    Events.on(ClientLoadEvent.class, e -> build());
  }

  static void build(){
    if(gaugeTable != null) return;

    gaugeTable = new Table(){{
      background(Tex.pane);
      touchable(Touchable.enabled);
      clicked(() -> vascularVision = !vascularVision);
    }};

    if(Vars.ui != null && Vars.ui.hudGroup != null){
    Vars.ui.hudGroup.addChild(new Table(){{
      top().right();
      margin(6f);
      add(gaugeTable).padTop(Vars.mobile ? 220f : 150f);
    }});
    }

    attachGauge();

    //клавиша переключения режима обзора
    Core.scene.addListener(new InputListener(){
      @Override
      public boolean keyDown(InputEvent event, KeyCode key){
        if(key == KeyCode.v && Vars.state != null && Vars.state.isPlaying()){
          vascularVision = !vascularVision;
          if(vascularVision && Vars.ui != null && Vars.ui.hudfrag != null) Vars.ui.hudfrag.showToast("[green]Vascular Vision: [])ON[]");
          return true;
        }
        return false;
      }
    });
  }

  /** Колба-актор: ноль аллокаций в draw(). */
  static class GaugeActor extends Element{
    @Override
    public void act(float delta){
      super.act(delta);
      if(Vars.state != null && Vars.state.isPlaying()){
        pulse += Time.delta * IrritationManager.pulseRate();
      }
    }

    @Override
    public void draw(){
      drawGauge(x, y, width, height);
    }
  }

  /** Вызывается один раз при загрузке клиента. */
  static void attachGauge(){
    GaugeActor g = new GaugeActor();
    gaugeTable.add(g).size(78f, 118f);
    gaugeTable.row();
    //кнопка Vascular Vision (дублирует клавишу V)
    TextButton btn = new TextButton("VASC", Styles.flatt);
    btn.clicked(() -> vascularVision = !vascularVision);
    btn.update(() -> btn.setChecked(vascularVision));
    gaugeTable.add(btn).size(78f, 30f).padTop(2f);
  }

  static float absSin(float a){ return Math.abs(Mathf.sin(a)); }

  static void drawGauge(float x, float y, float w, float h){

    Draw.color(panelBack);
    Draw.alpha(0.9f);
    Fill.rect(x + w/2f, y + h/2f, w, h);
    Draw.reset();

    Color c = IrritationManager.gaugeColor();

    //стеклянная колба
    Draw.color(c);
    Lines.stroke(2f);
    Lines.circle(x + w/2f, y + h*0.72f, 22f);

    //пульсирующий нервный узел
    float r = 8f + absSin(pulse) * 4f + IrritationManager.irritation * 6f;
    Draw.alpha(0.9f);
    Fill.circle(x + w/2f, y + h*0.72f, r);
    Draw.color(Color.white);
    Draw.alpha(0.75f);
    Fill.circle(x + w/2f, y + h*0.72f, r*0.35f);

    //кардиограмма P-QRS-T
    Draw.color(c);
    Draw.alpha(1f);
    Lines.stroke(1.5f);
    float py = y + h*0.38f, amp = 4f + IrritationManager.irritation * 14f;
    int segs = 22;
    float px0 = x + 6f, pw = w - 12f;
    float lastX = px0, lastY = py + ecg(-pulse*0.05f) * amp;
    for(int i = 1; i <= segs; i++){
      float f = i/(float)segs;
      float nx = px0 + f*pw, ny = py + ecg(f - pulse*0.05f) * amp;
      Lines.line(lastX, lastY, nx, ny);
      lastX = nx; lastY = ny;
    }

    //процент / статус
    String text = IrritationManager.controlled ? "SIM" : IrritationManager.locked ? "Zzz" : (int)(IrritationManager.irritation*100) + "%";
    Draw.color(Color.white);
    Fonts.outline.draw(text, x + w/2f, y + 10f, 0.55f, Align.center, false);

    //помехи при критическом раздражении (76–100%)
    if(IrritationManager.irritation > 0.76f && !IrritationManager.controlled){
      Draw.color(colRage);
      Draw.alpha(0.3f * absSin(pulse*2.3f));
      for(int i = 0; i < 3; i++){
        float gy = y + randLine(i)*h;
        Fill.rect(x + w/2f, gy, w*0.9f, 1.5f);
      }
      Draw.alpha(1f);
    }
    Draw.reset();
  }

  static float randLine(int i){
    //детерминированный шум без аллокаций
    return 0.15f + 0.7f * absSin(Time.time*0.07f + i*2.4f);
  }

  /** Форма импульса кардиограммы: P-QRS-T, t ∈ [0..1]. */
  static float ecg(float t){
    t = Mathf.mod(t, 1f);
    if(t < 0.12f) return Mathf.sin(t/0.12f * Mathf.PI) * 0.2f;
    if(t < 0.2f) return -Mathf.sin((t-0.12f)/0.08f * Mathf.PI) * 0.35f;
    if(t < 0.3f) return Mathf.sin((t-0.2f)/0.1f * Mathf.PI) * 1f;
    if(t < 0.36f) return -Mathf.sin((t-0.3f)/0.06f * Mathf.PI) * 0.6f;
    if(t >= 0.42f && t < 0.6f) return Mathf.sin((t-0.42f)/0.18f * Mathf.PI) * 0.3f;
    return 0f;
  }

  /** Оверлей мира: рисуется поверх тайлов во время world draw (из ViridisMod через RenderEvent). */
  public static void drawWorldOverlay(){
    if(!vascularVision || Vars.state == null || !Vars.state.isPlaying()) return;

    //зелёный светофильтр
    Camera cam = Core.camera;
    Draw.color(colCalm);
    Draw.alpha(0.07f);
    Fill.rect(cam.position.x, cam.position.y, cam.width, cam.height);
    Draw.alpha(1f);

    //подсветка вен/стен в зоне видимости камеры — итерация по координатам мира
    int mw = Vars.world.width(), mh = Vars.world.height();
    int cx = (int)(cam.position.x / 8f), cy = (int)(cam.position.y / 8f);
    int rx = (int)(cam.width / 8f / 2f) + 2, ry = (int)(cam.height / 8f / 2f) + 2;
    int x0 = Math.max(0, cx - rx), x1 = Math.min(mw - 1, cx + rx);
    int y0 = Math.max(0, cy - ry), y1 = Math.min(mh - 1, cy + ry);

    Draw.blend(Blending.additive);
    for(int tx = x0; tx <= x1; tx++){
      for(int ty = y0; ty <= y1; ty++){
        Tile t = Vars.world.rawTile(tx, ty);
        if(t == null || !t.block().solid || !(t.block() instanceof Wall)) continue;
        Draw.color(colRage);
        Draw.alpha(0.16f + 0.1f * Mathf.sin(pulse * 0.6f + tx * 0.3f + ty * 0.5f));
        Fill.rect(t.worldx(), t.worldy(), 8f, 8f);
      }
    }
    Draw.alpha(1f);
    Draw.blend();
    Draw.reset();
  }
}
