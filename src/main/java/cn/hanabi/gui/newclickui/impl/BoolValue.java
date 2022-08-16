package cn.hanabi.gui.newclickui.impl;

import cn.hanabi.Hanabi;
import cn.hanabi.gui.font.compat.WrappedVertexFontRenderer;
import cn.hanabi.gui.font.noway.ttfr.HFontRenderer;
import cn.hanabi.gui.newclickui.ClickUI;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.TranslateUtil;
import cn.hanabi.value.Value;
import me.yarukon.font.GlyphPageFontRenderer;

import java.awt.*;

public class BoolValue {
	public Value values;
	
	public float length, x, y;
	
	public String name;
	
	public TranslateUtil anima = new TranslateUtil(0,0);
	
	public BoolValue(Value values) {
		this.values = values;
		this.name = values.getValueName().split("_")[1];
		anima.setXY((boolean) values.getValueState() ? 10 : 0, (boolean) values.getValueState() ? 0 : 255);
	}
	
	public void draw(float x, float y, float mouseX, float mouseY) {
		this.x = x;
		this.y = y;
		anima.interpolate((boolean) values.getValueState() ? 10 : 0, (boolean) values.getValueState() ? 255 : 0, 0.25f);
		length = 30;
		HFontRenderer font = Hanabi.INSTANCE.fontManager.wqy18;
		font.drawString(name, x + 20, y + 8, new Color(255,255,255,255).getRGB());
		RenderUtil.circle(x + 220, y + 12, 4, new Color(70,70,70,255).getRGB());
		RenderUtil.circle(x + 210, y + 12, 4, new Color(70,70,70,255).getRGB());
		RenderUtil.drawRect(x + 210, y + 7.5f, x + 220, y + 16.5f, new Color(70,70,70,255).getRGB());

		RenderUtil.circle(x + 210 + anima.getX(), y + 12, 4, new Color(180,180,180,255).getRGB());
		RenderUtil.circle(x + 210, y + 12, 4, new Color(180,180,180,255).getRGB());
		RenderUtil.drawRect(x + 210, y + 7.5f, x + 210 + anima.getX(), y + 16.5f, new Color(180,180,180,255).getRGB());

		RenderUtil.circle(x + 210 + anima.getX(), y + 12, 5, new Color(255,255,255,255).getRGB());
	}
	
	public void handleMouse(float mouseX, float mouseY, int key) {
		if(ClickUI.isHover(mouseX, mouseY, x + 205, y + 7, x + 225, y + 17) && key == 0) {
			values.setValueState(!(boolean)(values.getValueState()));
		}
	}
	
	public float getLength() {
		return this.length;
	}
}
