package cn.hanabi.modules.modules.render;

import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.value.Value;

public class HudWindow extends Mod {

    public Value<Boolean> sessionInfo = new Value<>("HudWindow", "Session Info", false);
    public Value<Boolean> plyInventory = new Value<>("HudWindow", "Inventory", false);
    public Value<Boolean> scoreboard = new Value<>("HudWindow", "Scoreboard", false);
    public Value<Boolean> radar = new Value<>("HudWindow", "Radar", false);

    public HudWindow() {
        super("HudWindow", Category.RENDER, false, false, -1);
    }
}
