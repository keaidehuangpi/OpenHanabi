package cn.hanabi.gui.newclickui.impl;

import cn.hanabi.Hanabi;
import cn.hanabi.gui.font.noway.ttfr.HFontRenderer;
import cn.hanabi.gui.newclickui.ClickUI;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.utils.TranslateUtil;
import cn.hanabi.utils.fontmanager.HanabiFonts;
import cn.hanabi.gui.font.compat.WrappedVertexFontRenderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;



public class Panel {
    private final Category category;

    private float desX, desY, x, y, dragX, dragY;

    private boolean needMove;

    private final long delay;

    private int wheel;

    private final String name;

    private final TranslateUtil anima = new TranslateUtil(1,0);
    private final TranslateUtil translate = new TranslateUtil(0,0);

    private final TimeHelper timer = new TimeHelper();

    public Panel(float x, float y, long delay, Category category) {
        this.desX = x;
        this.desY = y;
        this.delay = delay;
        this.category = category;
        this.name = category.toString();
        this.anima.setXY(1, 0);
        this.needMove = false;
        this.dragX = 0;
        this.dragY = 0;
    }

    public void draw(float mouseX, float mouseY) {
        //处理拖动
        if(needMove) setXY(mouseX - dragX, mouseY - dragY);
        if (!Mouse.isButtonDown(0) && needMove) needMove = false;

        //处理动画
        float alpha = 0.01f;
        float yani = 0;
        if(timer.isDelayComplete(delay)) {
            anima.interpolate(100, 20, 2.0E-2f);
            alpha = anima.getX() / 100;
            yani = anima.getY();
        }

        x = desX;
        y = desY + 20 - yani;

        HFontRenderer titlefont = Hanabi.INSTANCE.fontManager.usans25;
        HFontRenderer font = Hanabi.INSTANCE.fontManager.raleway20;
        HFontRenderer icon = Hanabi.INSTANCE.fontManager.icon30;

        float mstartY = y + 40;
        float maddY = 22;

        //Panel背景
        RenderUtil.drawRoundRect(x,y,x + 140,y + 260,4, new Color(0,0,0,(int)(60 * alpha)).getRGB());

        //Panel标题
        titlefont.drawString(this.name, x + 15, y + 12, new Color(255,255,255,(int)(255 * alpha)).getRGB());
        String iconstr = "";
        switch (category.toString()){
            case "Combat":{
                iconstr = HanabiFonts.ICON_CLICKGUI_COMBAT;
                break;
            }
            case "Movement":{
                iconstr = HanabiFonts.ICON_CLICKGUI_MOVEMENT;
                break;
            }
            case "Player":{
                iconstr = HanabiFonts.ICON_CLICKGUI_PLAYER;
                break;
            }
            case "Render":{
                iconstr = HanabiFonts.ICON_CLICKGUI_RENDER;
                break;
            }
            case "World":{
                iconstr = HanabiFonts.ICON_CLICKGUI_WORLD;
                break;
            }
            case "Ghost":{
                iconstr = HanabiFonts.ICON_CLICKGUI_GHOST;
                break;
            }
        }
        icon.drawString(iconstr, x + 110, y + 12, new Color(255,255,255,(int)(255 * alpha)).getRGB());

        //Mod显示
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.doGlScissor((int)x, (int)mstartY - 4, 140,(int) (y + 260 - 5 - (mstartY - 4)));
        float modY = translate.getX();
        for(Mod m : ModManager.getModules(category)){
            //判断搜索栏
            if(ClickUI.isSearching && !ClickUI.searchcontent.equalsIgnoreCase("") && ClickUI.searchcontent != null){
                if(!m.getName().toLowerCase().contains(ClickUI.searchcontent.toLowerCase())) continue;
            }
            boolean mhover = ClickUI.currentMod == null && ClickUI.isHover(mouseX, mouseY, x, mstartY + modY - 4, x + 140, mstartY + modY + 17) && ClickUI.isHover(mouseX, mouseY, x, mstartY - 4, x + 140, y + 260 - 5);
            font.drawString(m.getName(), x + 25, mstartY + modY, mhover ? new Color(180,180,180,(int)(255 * alpha)).getRGB() : new Color(255,255,255,(int)(255 * alpha)).getRGB());
            if(m.isEnabled()) Hanabi.INSTANCE.fontManager.micon15.drawString("B", x + 12, mstartY + modY + 2, mhover ? new Color(180,180,180,(int)(255 * alpha)).getRGB() : new Color(255,255,255,(int)(255 * alpha)).getRGB());
            if(m.hasValues()) font.drawString(">", x + 110, mstartY + modY, mhover ? new Color(180,180,180,(int)(255 * alpha)).getRGB() : new Color(255,255,255,(int)(255 * alpha)).getRGB());

            modY += maddY;
        }
        GL11.glDisable(3089);
        GL11.glPopMatrix();

        //处理滚动
        float moduleHeight = modY - translate.getX() - 1;
        if (Mouse.hasWheel() && ClickUI.isHover(mouseX, mouseY, x, mstartY - 4, x + 140, y + 260 - 5) && ClickUI.currentMod == null) {
            if ((ClickUI.real > 0 && wheel < 0)) {
                for (int i = 0; i < 5; i++) {
                    if (!(wheel < 0))
                        break;
                    wheel += 5;
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    if (!(ClickUI.real < 0 && moduleHeight > y + 260 - 5 - (mstartY - 4) && Math.abs(wheel) < (moduleHeight - (y + 260 - 5 - (mstartY - 4)))))
                        break;
                    wheel -= 5;
                }
            }
        }
        translate.interpolate(wheel, 0, 20.0E-2f);

        //滚动条
        float sliderh = Math.min(y + 260 - 5 - (mstartY - 4), (y + 260 - 5 - (mstartY - 4)) * (y + 260 - 5 - (mstartY - 4)) / moduleHeight);
        float slidert =  -(y + 260 - 5 - (mstartY - 4) - sliderh) * (translate.getX()) / (moduleHeight - (y + 260 - 5 - (mstartY - 4)));
        if(sliderh < y + 260 - 5 - (mstartY - 4)) {
            GL11.glPushMatrix();
            GL11.glEnable(3089);
            RenderUtil.doGlScissor((int)x + 129, (int)mstartY - 4, 1,(int) (y + 260 - 5 - (mstartY - 4)));
            RenderUtil.drawRect(x + 129, mstartY - 4 + slidert, x + 130, mstartY - 4 + slidert + sliderh, new Color(255,255,255,(int)(255 * alpha)).getRGB());
            GL11.glDisable(3089);
            GL11.glPopMatrix();
        }
    }

    public void drawShadow(float mouseX, float mouseY) {
        //处理拖动
        if(needMove) setXY(mouseX - dragX, mouseY - dragY);
        if (!Mouse.isButtonDown(0) && needMove) needMove = false;

        //处理动画
        float alpha = 0.01f;
        float yani = 0;
        if(timer.isDelayComplete(delay)) {
            anima.interpolate(100, 20, 2.0E-2f);
            alpha = anima.getX() / 100;
            yani = anima.getY();
        }

        x = desX;
        y = desY + 20 - yani;

        HFontRenderer titlefont = Hanabi.INSTANCE.fontManager.usans25;
        HFontRenderer font = Hanabi.INSTANCE.fontManager.raleway20;
        HFontRenderer icon = Hanabi.INSTANCE.fontManager.icon30;

        float mstartY = y + 40;
        float maddY = 22;

        //背景阴影
        int spread = 2;
        RenderUtil.drawRoundRect(x - spread,y - spread,x + 140 + spread,y + 260 + spread,4 + spread, new Color(0,0,0,(int)(120 * alpha)).getRGB());

        //标题阴影
        titlefont.drawString(this.name, x + 15, y + 12, new Color(255,255,255,(int)(255 * alpha)).getRGB());
        String iconstr = "";
        switch (category.toString()){
            case "Combat":{
                iconstr = HanabiFonts.ICON_CLICKGUI_COMBAT;
                break;
            }
            case "Movement":{
                iconstr = HanabiFonts.ICON_CLICKGUI_MOVEMENT;
                break;
            }
            case "Player":{
                iconstr = HanabiFonts.ICON_CLICKGUI_PLAYER;
                break;
            }
            case "Render":{
                iconstr = HanabiFonts.ICON_CLICKGUI_RENDER;
                break;
            }
            case "World":{
                iconstr = HanabiFonts.ICON_CLICKGUI_WORLD;
                break;
            }
            case "Ghost":{
                iconstr = HanabiFonts.ICON_CLICKGUI_GHOST;
                break;
            }
        }
        icon.drawString(iconstr, x + 110, y + 12, new Color(255,255,255,(int)(255 * alpha)).getRGB());

        //Mod字体阴影
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.doGlScissor((int)x, (int)mstartY - 4, 140,(int) (y + 260 - 5 - (mstartY - 4)));
        float modY = translate.getX();
        for(Mod m : ModManager.getModules(category)){
            //判断搜索栏
            if(ClickUI.isSearching && !ClickUI.searchcontent.equalsIgnoreCase("") && ClickUI.searchcontent != null){
                if(!m.getName().toLowerCase().contains(ClickUI.searchcontent.toLowerCase())) continue;
            }
            boolean mhover = ClickUI.currentMod == null && ClickUI.isHover(mouseX, mouseY, x, mstartY + modY - 4, x + 140, mstartY + modY + 17) && ClickUI.isHover(mouseX, mouseY, x, mstartY - 4, x + 140, y + 260 - 5);
            font.drawString(m.getName(), x + 25, mstartY + modY, mhover ? new Color(70,70,70,(int)(255 * alpha)).getRGB() : new Color(0,0,0,(int)(255 * alpha)).getRGB());
            if(m.isEnabled()) Hanabi.INSTANCE.fontManager.micon15.drawString("B", x + 12, mstartY + modY + 2, mhover ? new Color(180,180,180,(int)(255 * alpha)).getRGB() : new Color(255,255,255,(int)(255 * alpha)).getRGB());
            if(m.hasValues()) font.drawString(">", x + 110, mstartY + modY, mhover ? new Color(180,180,180,(int)(255 * alpha)).getRGB() : new Color(255,255,255,(int)(255 * alpha)).getRGB());

            modY += maddY;
        }
        GL11.glDisable(3089);
        GL11.glPopMatrix();

        //处理滚动
        float moduleHeight = modY - translate.getX() - 1;
        if (Mouse.hasWheel() && ClickUI.isHover(mouseX, mouseY, x, mstartY - 4, x + 140, y + 260 - 5) && ClickUI.currentMod == null) {
            if ((ClickUI.real > 0 && wheel < 0)) {
                for (int i = 0; i < 5; i++) {
                    if (!(wheel < 0))
                        break;
                    wheel += 5;
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    if (!(ClickUI.real < 0 && moduleHeight > y + 260 - 5 - (mstartY - 4) && Math.abs(wheel) < (moduleHeight - (y + 260 - 5 - (mstartY - 4)))))
                        break;
                    wheel -= 5;
                }
            }
        }
        translate.interpolate(wheel, 0, 20.0E-2f);

        //滚动条阴影
        float sliderh = Math.min(y + 260 - 5 - (mstartY - 4), (y + 260 - 5 - (mstartY - 4)) * (y + 260 - 5 - (mstartY - 4)) / moduleHeight);
        float slidert =  -(y + 260 - 5 - (mstartY - 4) - sliderh) * (translate.getX()) / (moduleHeight - (y + 260 - 5 - (mstartY - 4)));
        if(sliderh < y + 260 - 5 - (mstartY - 4)) {
            GL11.glPushMatrix();
            GL11.glEnable(3089);
            RenderUtil.doGlScissor((int)x + 129, (int)mstartY - 4, 1,(int) (y + 260 - 5 - (mstartY - 4)));
            RenderUtil.drawRect(x + 129, mstartY - 4 + slidert, x + 130, mstartY - 4 + slidert + sliderh, new Color(255,255,255,(int)(120 * alpha)).getRGB());
            GL11.glDisable(3089);
            GL11.glPopMatrix();
        }
    }

    public void handleMouseClicked(float mouseX, float mouseY, int key) {
        float mstartY = y + 40;
        float maddY = 22;

        //处理拖动
        boolean tophover = ClickUI.currentMod == null && ClickUI.isHover(mouseX, mouseY, x, y, x + 140, mstartY);
        if(tophover && key == 0){
            dragX = mouseX - desX;
            dragY = mouseY - desY;
            needMove = true;
        }

        //处理Mod的MouseClicked的Event
        float modY = mstartY + translate.getX();
        for(Mod m : ModManager.getModules(category)){
            //判断搜索栏
            if(ClickUI.isSearching && !ClickUI.searchcontent.equalsIgnoreCase("") && ClickUI.searchcontent != null){
                if(!m.getName().toLowerCase().contains(ClickUI.searchcontent.toLowerCase())) continue;
            }
            boolean mhover = ClickUI.currentMod == null && ClickUI.isHover(mouseX, mouseY, x, modY - 4, x + 140, modY + 17) && ClickUI.isHover(mouseX, mouseY, x, mstartY - 4, x + 140, y + 260 - 5);
            if(mhover){
                if(key == 0) m.set(!m.isEnabled(),false);
                if(key == 1 && m.hasValues()) {
                    ClickUI.currentMod = m;
                    ClickUI.settingwheel = 0;
                    ClickUI.settingtranslate.setXY(0, 0);
                    ClickUI.animatranslate.setXY(0, 0);
                }
            }
            modY += maddY;
        }
    }

    public void handleMouseReleased(float mouseX, float mouseY, int key) {
        float mstartY = y + 40;

        //处理拖动
        boolean tophover = ClickUI.currentMod == null && ClickUI.isHover(mouseX, mouseY, x, y, x + 140, mstartY);
        if(tophover && key == 0){
            dragX = mouseX - desX;
            dragY = mouseY - desY;
            needMove = false;
        }
    }

    public void resetAnimation(){
        timer.reset();
        anima.setXY(1,0);
        needMove = false;
        dragX = 0;
        dragY = 0;
    }

    public void resetTranslate(){
        translate.setXY(0,0);
        wheel = 0;
    }

    public void setXY(float x, float y){
        this.desX = x;
        this.desY = y;
    }
}
