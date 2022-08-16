package cn.hanabi.gui.superskidder.material.clickgui;


public class Category {
    public cn.hanabi.modules.Category moduleType;
    public boolean needRemove;
    public AnimationUtils rollAnim2 = new AnimationUtils();
    float anim;
    public boolean show;
    public float modsY2 = 0;
    public float modsY3 = 0;

    public Category(cn.hanabi.modules.Category mt, int i, boolean b) {
        this.moduleType = mt;
        this.anim = i;
        this.show = b;
    }
}
