package cn.hanabi.modules.modules.render;

import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;

public class HitAnimation extends Mod {
    public Value<String> mode = new Value("HitAnimation", "Mode", 0);
    public Value<Double> swingSpeed = new Value<>("HitAnimation", "SwingSpeed", 1.0, 0.5, 5.0);
    public Value<Double> posX = new Value<>("HitAnimation", "ItemPosX", 0.0, -1.0, 1.0, 0.05);
    public Value<Double> posY = new Value<>("HitAnimation", "ItemPosY", 0.0, -1.0, 1.0, 0.05);
    public Value<Double> posZ = new Value<>("HitAnimation", "ItemPosZ", 0.0, -1.0, 1.0, 0.05);
    public Value<Double> itemScale = new Value<>("HitAnimation", "ItemScale", 0.7, 0.0, 2.0, 0.05);
    public Value<Double> equipProgMultProperty = new Value<>("HitAnimation", "E-Prog", 2.0, 0.5, 3.0, 0.1);
    public Value<Boolean> equipProgressProperty = new Value<>("HitAnimation", "Equip Prog", false);

    public HitAnimation() {
        super("HitAnimation", Category.RENDER);
        mode.addValue("Vanilla");
        mode.addValue("1.7");
        mode.addValue("Swang");
        mode.addValue("Swank");
        mode.addValue("Swong");
        mode.addValue("Sigma");
        mode.addValue("Jello");
        mode.addValue("Slide");
        mode.addValue("Ohare");
        mode.addValue("Wizzard");
        mode.addValue("Lennox");
        mode.addValue("Leaked");
        mode.addValue("Butter");
        mode.addValue("Lucky");
        mode.addValue("Long Hit");
        mode.addValue("Tiny Whack");
        mode.addValue("Skid");
        mode.addValue("Slide2");
        mode.addValue("Mix");
        mode.addValue("SlideT");
        mode.addValue("SlideA");
        mode.addValue("Epic");
        mode.addValue("Punch");
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        this.setDisplayName(mode.getValueState());
    }
}
