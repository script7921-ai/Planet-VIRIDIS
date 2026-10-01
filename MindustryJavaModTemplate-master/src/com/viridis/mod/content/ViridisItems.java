package com.viridis.mod.content;

import arc.graphics.*;
import mindustry.content.*;
import mindustry.type.*;

/** 7 родных ресурсов + 5 синтезируемых ресурсов «Виридиса». */
public class ViridisItems{
//родные
public static Item chitin, ferroBark, silicaMoss, bioCell, luminite, fossilAmber, radBulb;
//синтетические
public static Item bioSilicon, lignoPlast, keratinAlloy, nitroPollen, neuralFiber;

public static void load(){
chitin = new Item("viridis-chitin", Color.valueOf("b0885a")){{
cost = 1.2f; hardness = 1;
localizedName = "Хитин";
}};
ferroBark = new Item("viridis-ferro-bark", Color.valueOf("6d7484")){{
cost = 3f; hardness = 4;
localizedName = "Ферро-кора";
}};
silicaMoss = new Item("viridis-silica-moss", Color.valueOf("cfe0c9")){{
cost = 0.8f; hardness = 1;
localizedName = "Силикатная крошка";
}};
bioCell = new Item("viridis-bio-cell", Color.valueOf("3ff27c")){{
cost = 1.5f; hardness = 2;
localizedName = "Био-ячейка";
}};
luminite = new Item("viridis-luminite", Color.valueOf("9a6cff")){{
cost = 2f; hardness = 3; chargeTime = 40f; chargeBeginColor = Color.valueOf("9a6cff"); chargeMidColor = Color.valueOf("55ff44");
lightColor = Color.valueOf("9a6cff").a(0.4f);
localizedName = "Фосфорный Люминит";
}};
fossilAmber = new Item("viridis-fossil-amber", Color.valueOf("ffa640")){{
cost = 1.4f; hardness = 2;
localizedName = "Окаменевшая смола";
}};
radBulb = new Item("viridis-rad-bulb", Color.valueOf("7fff3f")){{
cost = 4f; hardness = 4; radioactivity = 0.7f;
lightColor = Color.valueOf("7fff3f").a(0.35f);
localizedName = "Радио-клубень";
}};

bioSilicon = new Item("viridis-bio-silicon", Color.valueOf("72a6c9")){{
cost = 2f; hardness = 2;
localizedName = "Био-кремний";
}};
lignoPlast = new Item("viridis-ligno-plast", Color.valueOf("d8e05a")){{
cost = 2.2f;
localizedName = "Лигно-пластик";
}};
keratinAlloy = new Item("viridis-keratin-alloy", Color.valueOf("9aa87e")){{
cost = 3.5f; hardness = 4;
localizedName = "Керато-сплав";
}};
nitroPollen = new Item("viridis-nitro-pollen", Color.valueOf("e8ff59")){{
cost = 2.5f; explosiveness = 1.4f; flammability = 0.8f;
lightColor = Color.valueOf("e8ff59").a(0.5f);
localizedName = "Нитро-пыльца";
}};
neuralFiber = new Item("viridis-neural-fiber", Color.valueOf("c800ff")){{
cost = 5f;
chargeTime = 60f; chargeBeginColor = Color.valueOf("c800ff"); chargeMidColor = Color.valueOf("00ffd5");
lightColor = Color.valueOf("c800ff").a(0.5f);
localizedName = "Нейро-нить";
}};
}
}
