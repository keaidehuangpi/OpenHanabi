package cn.hanabi.modules.modules.render;

import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.value.Value;

public class WorldColor extends Mod {

    public final Value<Double> r = new Value<>("World Color", "Red Color", 0d, 0d, 255d, 1d);
    public final Value<Double> g = new Value<>("World Color", "Green Color", 0d, 0d, 255d, 1d);
    public final Value<Double> b = new Value<>("World Color", "Blue Color", 0d, 0d, 255d, 1d);
    public final Value<Double> a = new Value<>("World Color", "Alpha", 0d, 0d, 255d, 5d);


    public WorldColor() {
        super("World Color", Category.RENDER);

    }

}
