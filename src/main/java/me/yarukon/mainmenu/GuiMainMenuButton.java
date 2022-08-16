package me.yarukon.mainmenu;

import cn.hanabi.Hanabi;
import cn.hanabi.utils.Colors;
import cn.hanabi.utils.RenderUtil;
import me.yarukon.YRenderUtil;

public class GuiMainMenuButton {
	public GuiCustomMainMenu parent;
	
	public String icon;
	public String text;
	public Executor action;
	public int buttonID;
	
	public float x;
	public float y;
	public float textOffset;
	
	public float yAnimation = 0;
	
	public GuiMainMenuButton(GuiCustomMainMenu parent, int id, String icon, String text, Executor action) {
		this.parent = parent;
		this.buttonID = id;
		this.icon = icon;
		this.text = text;
		this.action = action;
		this.textOffset = 0;
	}
	
	public GuiMainMenuButton(GuiCustomMainMenu parent, int id, String icon, String text, Executor action, float yOffset) {
		this.parent = parent;
		this.buttonID = id;
		this.icon = icon;
		this.text = text;
		this.action = action;
		this.textOffset = yOffset;
	}
	
	public void draw(float x, float y, int mouseX, int mouseY) {
		this.x = x;
		this.y = y;
		
		//RenderUtil.drawRect(x, y, x + 50, y + 30, Colors.BLUE.c);
		
		Hanabi.INSTANCE.fontManager.sessionInfoIcon30.drawString(this.icon, x + (50 / 2f) - (Hanabi.INSTANCE.fontManager.sessionInfoIcon30.getStringWidth(icon) / 2f) - 2, y + (30 / 2f), Colors.WHITE.c);

		this.yAnimation = RenderUtil.smoothAnimation(yAnimation, RenderUtil.isHovering(mouseX, mouseY, x, y, x + 50, y + 30) ? 2 : 0, 50, 0.3f);
		YRenderUtil.drawGradientRect(x, y + 30 - (this.yAnimation * 3), x + 50, y + 30, 0x0034b2ff, 0x7834b2ff);
		RenderUtil.drawRect(x, y + 30 - (this.yAnimation), x + 50, y + 30, 0xff34b2ff);
	}

	public void mouseClick(int mouseX, int mouseY, int mouseButton) {
		if(RenderUtil.isHovering(mouseX, mouseY, x, y, x + 50, y + 30) && action != null && mouseButton == 0) {
			action.execute();
		}
	}
	
	interface Executor {
		void execute();
	}
	
}
