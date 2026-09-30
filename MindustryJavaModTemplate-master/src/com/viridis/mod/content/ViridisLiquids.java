package com.viridis.mod.content;

import arc.graphics.*;
import mindustry.type.*;

/** Жидкости Виридиса: био-жижа (аналог нефти), желчь (аналог шлака), эндо-сок (крио-хладагент). */
public class ViridisLiquids{
public static Liquid bioSludge, bile, cryoSap;

public static void load(){
bioSludge = new Liquid("viridis-bio-sludge", Color.valueOf("1e3f20")){{
viscosity = 0.8f;
flammability = 0.9f;
heatCapacity = 0.4f;
lightColor = Color.valueOf("1e3f20").a(0.3f);
canStayOn = new Liquid[]{this};
localizedName = "Био-жижа";
}};
bile = new Liquid("viridis-bile", Color.valueOf("ffb700")){{
viscosity = 0.55f;
temperature = 1.7f; //кипящий пищеварительный сок
heatCapacity = 0.3f;
lightColor = Color.valueOf("ffb700").a(0.4f);
canStayOn = new Liquid[]{this};
localizedName = "Желчь";
}};
cryoSap = new Liquid("viridis-cryo-sap", Color.valueOf("00ffd5")){{
temperature = 58f/140f*0.4f; //сверххолодный (~58°K)
coolant = true;
heatCapacity = 1.2f;
viscosity = 0.3f;
lightColor = Color.valueOf("00ffd5").a(0.4f);
canStayOn = new Liquid[]{this};
localizedName = "Эндо-сок";
}};
}
}
