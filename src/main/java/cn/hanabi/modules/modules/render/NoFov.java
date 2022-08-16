package cn.hanabi.modules.modules.render;

import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.value.Value;


public class NoFov extends Mod {
    public static Value<Double> fovspoof = new Value("NoFov", "Fov", 1.0d, 0.1d, 1.5d, 0.01d);

    public NoFov() {
        super("NoFov", Category.RENDER);
    }

}