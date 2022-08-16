package cn.hanabi.gui.newclickui.misc;

import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.TranslateUtil;

import java.awt.*;


public class ClickEffect {
    public float x, y;

    public TranslateUtil anima = new TranslateUtil(0,0);

    public ClickEffect(float x, float y){
        this.x = x;
        this.y = y;
        anima.setXY(0, 0);
    }

    public void draw(){
        anima.interpolate(100, 100, 15.0E-2f);
//        anima.interpolate(100, 100, 10.0E-2f);
        double radius = 8 * anima.getX() / 100;
//        double radius = 15 * anima.getX() / 100;
        int alpha = (int)(255 - 255 * anima.getY() / 100);
        RenderUtil.drawArc(x, y, radius, new Color(255,255,255, alpha).getRGB(), 0, 360, 5);
//        Hanabi.INSTANCE.fontManager.icon20.drawCenteredString(HanabiFonts.ICON_HANABI_LOGO, x, (float) (y - radius), new Color(255,255,255,Math.max(alpha, 30)).getRGB());
    }

    public boolean canRemove(){
        return anima.getX() == 100 && anima.getY() == 100;
    }
}
