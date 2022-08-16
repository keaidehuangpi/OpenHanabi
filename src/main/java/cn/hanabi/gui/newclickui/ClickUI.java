package cn.hanabi.gui.newclickui;

import cn.hanabi.Hanabi;
import cn.hanabi.gui.font.compat.WrappedVertexFontRenderer;
import cn.hanabi.gui.font.noway.ttfr.HFontRenderer;
import cn.hanabi.gui.newclickui.impl.BoolValue;
import cn.hanabi.gui.newclickui.impl.DoubleValue;
import cn.hanabi.gui.newclickui.impl.ModeValue;
import cn.hanabi.gui.newclickui.impl.Panel;
import cn.hanabi.gui.newclickui.misc.ClickEffect;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.BlurUtil;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.utils.TranslateUtil;
import cn.hanabi.utils.fontmanager.HanabiFonts;
import cn.hanabi.value.Value;
import me.yarukon.font.GlyphPageFontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;


public class ClickUI extends GuiScreen {
    //Panel
    public Panel combat, movement, player, render, world, ghost;
    //Panel集合
    List<Panel> panels = new ArrayList<>();

    //ClickEffect集合
    List<ClickEffect> clickEffects = new ArrayList<>();

    //设定Value的Mod
    public static Mod currentMod;
    //鼠标滚轮（不知道为啥不能放Panel里不然会抽风）
    public static int real = Mouse.getDWheel();
    //设定Value的Panel的滚轮
    public static int settingwheel;
    //设定Value的Panel的滚轮动画
    public static TranslateUtil settingtranslate = new TranslateUtil(0,0);
    //设定Value的Panel的动画
    public static TranslateUtil animatranslate = new TranslateUtil(0,0);

    //Value的整合
    public ArrayList<Value> modBooleanValue = new ArrayList<>();
    public ArrayList<Value> modModeValue = new ArrayList<>();
    public ArrayList<Value> modDoubleValue = new ArrayList<>();
    public static Map<Value, BoolValue> booleanValueMap = new HashMap<>();
    public static Map<Value, DoubleValue> doubleValueMap = new HashMap<>();
    public static Map<Value, ModeValue> modeValueMap = new HashMap<>();

    //Search后边的小尾巴的计时器
    public TimeHelper timer = new TimeHelper();
    //Search后边的小尾巴
    public String input;
    //Search的内容
    public static String searchcontent;
    //是否正在搜索
    public static boolean isSearching;

    public ClickUI() {
        currentMod = null;
        //添加Panel至集合
        for (int i = 0; i < Category.values().length; i++) {
            Panel panel = new Panel(10 + 150 * i, 50, 100L * (i + 1), Category.values()[i]);
            panels.add(panel);
        }
        input = "";
        searchcontent = "";
        isSearching = false;
    }

    @Override
    public void initGui() {
        //初始化
        if(searchcontent.equalsIgnoreCase("") || searchcontent == null) isSearching = false;
        clickEffects.clear();
        if(currentMod == null && !isSearching) {
            //初始化Panel动画
            for (Panel panel : panels) {
                panel.resetAnimation();
            }
            //初始化设定Value的Panel动画
            animatranslate.setXY(0,0);
        }
        //搜索栏RepeatEvent
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float par) {
        //搜索小尾巴
        if (timer.isDelayComplete(500)) {
            input = input.equals("_") ? "" : "_";
            timer.reset();
        }

        //背景加了层白色，不然如果后边是黑的看不清
        RenderUtil.drawRect(0, 0, width, height, new Color(255,255,255,30).getRGB());

        //鼠标滚轮
        real = Mouse.getDWheel();

        //Blur后边的Panel的Shadow
        for (Panel panel : panels) {
            panel.drawShadow(mouseX, mouseY);
        }

        //Blur后边的搜索栏的Shadow
        RenderUtil.drawRect(width / 2 - 60, 0, width / 2 + 60, 20, new Color(0,0,0,120).getRGB());
        if(!searchcontent.equalsIgnoreCase("") && searchcontent != null)  isSearching = true;
        HFontRenderer font = Hanabi.INSTANCE.fontManager.wqy18;
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.startGlScissor(width / 2 - 55, 0, 110, 20);
        if(isSearching){
            font.drawString(searchcontent + input, Math.min(width / 2 - 55, width / 2 + 50 - font.getStringWidth(searchcontent)), 4, new Color(255,255,255,255).getRGB());
        }else{
            font.drawString("Search", width / 2f - 55, 4, new Color(180,180,180,255).getRGB());
        }
        RenderUtil.stopGlScissor();
        GL11.glDisable(3089);
        GL11.glPopMatrix();

        //Blur后边的Reset的Shadow
        RenderUtil.drawRect(width / 2 + 80, 0, width / 2 + 130, 20, new Color(0,0,0,120).getRGB());
        Hanabi.INSTANCE.fontManager.wqy18.drawCenteredString("Reset Gui", width / 2 + 105, 4, new Color(255,255,255,255).getRGB());

        //Blur后边的Logo的Shadow
        Hanabi.INSTANCE.fontManager.usans30.drawCenteredString(Hanabi.CLIENT_NAME + " Build " + Hanabi.CLIENT_VERSION, width / 2, height - 20, new Color(0,0,0,255).getRGB());

        //Blur后边的ClickEffect的Shadow
        if(clickEffects.size() > 0) {
            Iterator<ClickEffect> clickEffectIterator= clickEffects.iterator();
            while(clickEffectIterator.hasNext()){
                ClickEffect clickEffect = clickEffectIterator.next();
                clickEffect.draw();
                if (clickEffect.canRemove()) clickEffectIterator.remove();
            }
        }

        //Blur
        BlurUtil.doBlur(7);

        //Logo
        Hanabi.INSTANCE.fontManager.usans30.drawCenteredString(Hanabi.CLIENT_NAME + " Build " + Hanabi.CLIENT_VERSION, width / 2, height - 20, new Color(255,255,255,255).getRGB());

        //画Panel
        for (Panel panel : panels) {
            panel.draw(mouseX, mouseY);
        }

        //画搜索栏
        boolean searchHover = isHover(mouseX, mouseY, width / 2 - 60, 0, width / 2 + 60, 20) && currentMod == null;
        RenderUtil.drawRect(width / 2 - 60, 0, width / 2 + 60, 20, new Color(0,0,0,searchHover ? 80 : 60).getRGB());
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.startGlScissor(width / 2 - 55, 0, 110, 20);
        if(isSearching){
            font.drawString(searchcontent + input, Math.min(width / 2 - 55, width / 2 + 50 - font.getStringWidth(searchcontent)), 4, new Color(255,255,255,255).getRGB());
        }else{
            font.drawString("Search", width / 2 - 55, 4, new Color(180,180,180,255).getRGB());
        }
        RenderUtil.stopGlScissor();
        GL11.glDisable(3089);
        GL11.glPopMatrix();

        //画Reset按钮
        boolean resetHover = isHover(mouseX, mouseY, width / 2 + 80, 0, width / 2 + 130, 20) && currentMod == null;
        RenderUtil.drawRect(width / 2 + 80, 0, width / 2 + 130, 20, new Color(0,0,0,resetHover ? 80 : 60).getRGB());
        Hanabi.INSTANCE.fontManager.wqy18.drawCenteredString("Reset Gui", width / 2 + 105, 4, new Color(255,255,255,255).getRGB());

        //设定Value的Panel的动画
        if(currentMod != null){
            animatranslate.interpolate(0, height, 20.0E-2f);
        }else{
            animatranslate.interpolate(0, 0, 40.0E-2f);
        }

        //画设定Value的Panel
        if(animatranslate.getY() > 0) {
            float startX = width / 2 - 120;
            float startY = height + height / 2 - 140 - animatranslate.getY();

            //Blur后边的ValuePanel的Shadow
            RenderUtil.drawRoundRect(startX, startY, startX + 240, startY + 280, 4, new Color(30, 30, 30, 255).getRGB());

            // FIXME: 这么搞会闪黑边
//            BlurUtil.doBlur(Math.max(animatranslate.getY() / height, 0) * 6 + 1);
            //Blur * 2
            BlurUtil.doBlur(7);

            //画ValuePanel
            RenderUtil.drawRoundRect(startX, startY, startX + 240, startY + 280, 4, new Color(30, 30, 30, 255).getRGB());
            if(currentMod != null) {
                //Blur后边的ValuePanel的标题阴影
                Hanabi.INSTANCE.fontManager.usans25.drawString(currentMod.getName(), startX + 15, startY + 12, new Color(255, 255, 255, 255).getRGB());

                String iconstr = "";
                switch (currentMod.getCategory().toString()){
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
                Hanabi.INSTANCE.fontManager.icon30.drawString(iconstr, startX + 210, startY + 12, new Color(255,255,255,255).getRGB());

                //Blur * 3
                BlurUtil.doBlur(startX + 10, startY + 5, 220, 270, 7, 0, 1);

                //画Value
                drawValue(mouseX, mouseY, startX, startY);
            }
        }

        //画Click效果
        if(clickEffects.size() > 0) {
            Iterator<ClickEffect> clickEffectIterator= clickEffects.iterator();
            while(clickEffectIterator.hasNext()){
                ClickEffect clickEffect = clickEffectIterator.next();
                clickEffect.draw();
                if (clickEffect.canRemove()) clickEffectIterator.remove();
            }
        }
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int key) {
        //Click效果
        ClickEffect clickEffect = new ClickEffect(mouseX, mouseY);
        clickEffects.add(clickEffect);

        //处理Panel的MouseClicked的Event
        for (Panel panel : panels) {
            panel.handleMouseClicked(mouseX, mouseY, key);
        }

        //处理搜索栏的MouseClicked的Event
        boolean searchHover = isHover(mouseX, mouseY, width / 2 - 60, 0, width / 2 + 60, 20) && currentMod == null;
        if(searchHover && key == 0) isSearching = true;
        if(!searchHover && key == 0 && (searchcontent.equalsIgnoreCase("") || searchcontent == null)) isSearching = false;

        //处理Reset按钮的MouseClicked的Event
        boolean resetHover = isHover(mouseX, mouseY, width / 2 + 80, 0, width / 2 + 130, 20) && currentMod == null;
        if(resetHover){
            for (Panel panel : panels) {
                panel.setXY(10 + 150 * panels.indexOf(panel), 50);
            }
            for (Panel panel : panels) {
                panel.resetAnimation();
            }
        }

        //处理ValuePanel的关闭
        if(currentMod != null && key == 0){
            float startX = width / 2 - 120;
            float startY = height + height / 2 - 140 - animatranslate.getY();
            boolean valueHover = isHover(mouseX, mouseY, startX, startY, startX + 240, startY + 280);
            if(!valueHover) currentMod = null;
        }

        //处理Value的设置
        if(currentMod != null){
            for (Value values2 : this.modBooleanValue) {
                BoolValue o;
                if (booleanValueMap.containsKey(values2)) {
                    o = booleanValueMap.get(values2);
                    o.handleMouse(mouseX, mouseY, key);
                }
            }
            for (Value values2 : this.modModeValue) {
                ModeValue o;
                if (modeValueMap.containsKey(values2)) {
                    o = modeValueMap.get(values2);
                    o.handleMouse(mouseX, mouseY, key);
                }
            }
            for (Value values2 : this.modDoubleValue) {
                DoubleValue o;
                if (doubleValueMap.containsKey(values2)) {
                    o = doubleValueMap.get(values2);
                    o.handleMouse(mouseX, mouseY, key);
                }
            }
        }
    }

    @Override
    public void mouseReleased(final int mouseX, final int mouseY, final int key) {
        //处理Panel的MouseReleased的Event
        for (Panel panel : panels) {
            panel.handleMouseReleased(mouseX, mouseY, key);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        //关闭窗口
        if(keyCode == Keyboard.KEY_ESCAPE){
            mc.displayGuiScreen(null);
        }

        //搜索栏内容添加
        if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && isSearching) {
            searchcontent = searchcontent + typedChar;
            for (Panel panel : panels) {
                panel.resetTranslate();
            }
        }

        //搜索栏内容粘贴
        if (keyCode == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown() && isSearching)
        {
            searchcontent += GuiScreen.getClipboardString();
            for (Panel panel : panels) {
                panel.resetTranslate();
            }
        }

        //搜索栏内容删除
        if (keyCode == 14 && isSearching) {
            int length = searchcontent.length();
            if (length != 0) {
                searchcontent = searchcontent.substring(0, length - 1);
                for (Panel panel : panels) {
                    panel.resetTranslate();
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        //保存设置
        try {
            Hanabi.INSTANCE.fileManager.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //搜索栏RepeatEvent关闭
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    public static boolean isHover(float mouseX, float mouseY, float x1, float y1, float x2, float y2) {
        return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
    }

    public void drawValue(float mouseX, float mouseY, float startX, float startY) {
        float vstartY = startY + 40;
        //标题
        Hanabi.INSTANCE.fontManager.usans25.drawString(currentMod.getName(), startX + 15, startY + 12, new Color(255, 255, 255, 255).getRGB());

        String iconstr = "";
        switch (currentMod.getCategory().toString()){
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
        Hanabi.INSTANCE.fontManager.icon30.drawString(iconstr, startX + 210, startY + 12, new Color(255,255,255,255).getRGB());

        float valueY = settingtranslate.getX();

        //集合Value
        if(!this.modBooleanValue.isEmpty()) this.modBooleanValue.clear();
        if(!this.modModeValue.isEmpty()) this.modModeValue.clear();
        if(!this.modDoubleValue.isEmpty()) this.modDoubleValue.clear();
        for (Value values : Value.list) {
            if (values.getValueName().split("_")[0].equalsIgnoreCase(this.currentMod.getName())) {
                Mod curMod = currentMod;
                ++curMod.valueSize;
                if (values.isValueDouble) {
                    this.modDoubleValue.add(values);
                }
                if (values.isValueMode) {
                    this.modModeValue.add(values);
                }
                if (values.isValueBoolean) {
                    this.modBooleanValue.add(values);
                }
            }
        }

        GL11.glPushMatrix();
        RenderUtil.startGlScissor((int)startX, (int)vstartY, 240, 235);

        //ModeValue
        for (Value values4 : this.modModeValue) {
            ModeValue o;
            if (modeValueMap.containsKey(values4)) {
                o = modeValueMap.get(values4);
            }
            else {
                o = new ModeValue(values4);
                modeValueMap.put(values4, o);
            }
            o.draw(startX, vstartY + valueY, mouseX, mouseY);
            valueY += o.getLength();
        }

        //DoubleValue
        for (Value values3 : this.modDoubleValue) {
            DoubleValue o;
            if (doubleValueMap.containsKey(values3)) {
                o = doubleValueMap.get(values3);
            } else {
                o = new DoubleValue(values3);
                doubleValueMap.put(values3, o);
            }
            o.draw(startX, vstartY + valueY, mouseX, mouseY);
            o.handleMouseinRender(mouseX, mouseY, 1);
            valueY += o.getLength();
        }

        //BoolValue
        for (Value values2 : this.modBooleanValue) {
            BoolValue o;
            if (booleanValueMap.containsKey(values2)) {
                o = booleanValueMap.get(values2);
            }
            else {
                o = new BoolValue(values2);
                booleanValueMap.put(values2, o);
            }
            o.draw(startX, vstartY + valueY, mouseX, mouseY);
            valueY += o.getLength();
        }
        RenderUtil.stopGlScissor();
        GL11.glPopMatrix();

        //处理ValuePanel的滚轮
        float moduleHeight = valueY - settingtranslate.getX() - 1;
        if (Mouse.hasWheel() && ClickUI.isHover(mouseX, mouseY, startX, startY, startX + 240, vstartY + 235) && ClickUI.currentMod != null) {
            if ((ClickUI.real > 0 && settingwheel < 0)) {
                for (int i = 0; i < 10; i++) {
                    if (!(settingwheel < 0))
                        break;
                    settingwheel += 5;
                }
            } else {
                for (int i = 0; i < 10; i++) {
                    if (!(ClickUI.real < 0 && moduleHeight > 235 && Math.abs(settingwheel) < (moduleHeight - 235)))
                        break;
                    settingwheel -= 5;
                }
            }
        }
        settingtranslate.interpolate(settingwheel, 0, 20.0E-2f);

        //ValuePanel的滚动条
        float sliderh = Math.min(235, (235) * (235) / moduleHeight);
        float slidert =  -(235 - sliderh) * (settingtranslate.getX()) / (moduleHeight - (235));
        if(sliderh < 235) {
            GL11.glPushMatrix();
            GL11.glEnable(3089);
            RenderUtil.doGlScissor((int)startX + 229, (int)vstartY, 1, 235);
            RenderUtil.drawRect(startX + 229, vstartY + slidert, startX + 230, vstartY + slidert + sliderh, new Color(255,255,255,255).getRGB());
            GL11.glDisable(3089);
            GL11.glPopMatrix();
        }
    }
}
