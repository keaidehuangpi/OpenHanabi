package cn.hanabi.modules.modules.render;

import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;

public class PaletteGui extends Mod {

    public me.yarukon.palette.PaletteGui pGui = null;

    public PaletteGui() {
        super("APaletteGui", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        super.onEnable();

        if(pGui == null) {
            pGui = new me.yarukon.palette.PaletteGui();
        }

        mc.displayGuiScreen(pGui);
        this.set(false);
    }
}
