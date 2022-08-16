package cn.hanabi.modules.modules.render;

import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import me.yarukon.palette.ColorValue;


public class Thermal extends Mod {
    //Color Moment
    public static ColorValue renderColor = new ColorValue("Thermal Color", 0.5f, 1f, 1f, 1f, true, false, 10f);

    public Thermal() {
        super("Thermal", Category.RENDER);
    }


}


