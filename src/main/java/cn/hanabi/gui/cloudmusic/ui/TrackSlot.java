package cn.hanabi.gui.cloudmusic.ui;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Hanabi;
import cn.hanabi.gui.cloudmusic.MusicManager;
import cn.hanabi.gui.cloudmusic.impl.Track;
import cn.hanabi.utils.RenderUtil;

import java.awt.*;

@ObfuscationClass
public class TrackSlot {
	
	public Track track;
	public float x;
	public float y;
	
	public TrackSlot(Track t) {
		this.track = t;
	}
	
	public void render(float a, float b, int mouseX, int mouseY) {
		this.x = a;
		this.y = b;
		
		RenderUtil.drawRoundedRect(x, y, x + 137, y + 20, 2, 0xff34373c);

		Hanabi.INSTANCE.fontManager.wqy16.drawString(track.name, x + 2, y + 1, Color.WHITE.getRGB());
		Hanabi.INSTANCE.fontManager.wqy13.drawString(track.artists, x + 2, y + 10, Color.WHITE.getRGB());
		
		RenderUtil.drawRoundedRect(x + 122, y, x + 137, y + 20, 2, 0xff34373c);
		//RenderUtil.drawGradientSideways(x + 100, y, x + 124, y + 20, 0x00818181, 0xff34373c);

		Hanabi.INSTANCE.fontManager.micon15.drawString("J", x + 125.5f, y + 5.5f, Color.WHITE.getRGB());
		
		//RenderUtil.drawOutlinedRect(x + 125, y + 5, x + 135, y + 15, .5f, Color.RED.getRGB());
	}
	
	public void click(int mouseX, int mouseY, int mouseButton) {
		if(RenderUtil.isHovering(mouseX, mouseY, x + 125, y + 5, x + 135, y + 15) && mouseButton == 0) {
			MusicManager.INSTANCE.play(track);
		}
	}
}
