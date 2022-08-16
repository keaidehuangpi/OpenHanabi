package cn.hanabi.gui.cloudmusic.ui;

import cn.hanabi.Hanabi;
import cn.hanabi.gui.cloudmusic.MusicManager;
import cn.hanabi.gui.cloudmusic.api.CloudMusicAPI;
import cn.hanabi.gui.cloudmusic.impl.Track;
import cn.hanabi.utils.PaletteUtil;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.RenderUtil;
import javafx.scene.media.MediaPlayer.Status;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class MusicPlayerUI extends GuiScreen {
	public float x=10;
	public float y=10;
	public float x2=0;
	public float y2=0;

	public boolean drag = false;
	//	public MouseHandler handler = new MouseHandler(0);
	public CopyOnWriteArrayList<TrackSlot> slots = new CopyOnWriteArrayList<>();

	public float width = 150;
	public float height = 250;

	public boolean extended = false;
	public float sidebarAnimation = 0;

	// 滚动
	public float scrollY = 0;
	public float scrollAni = 0;
	public float minY = -100;

	public CustomTextField textField = new CustomTextField("");

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		super.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		//侧边栏
		sidebarAnimation = RenderUtil.smoothAnimation(sidebarAnimation, extended ? width + 5 : 0.1f, 50, 0.4f);

		if(Math.ceil(sidebarAnimation) > 1) {
			float newX = x + sidebarAnimation;
			float newWidth = x + width + sidebarAnimation;
			RenderUtil.drawRoundedRect(newX, y, newWidth, y + height, 2, 0xff2f3136);

			//歌单导入输入框
			textField.draw(newX + 6, y + 2);
			RenderUtil.drawRoundedRect(newWidth - 26, y + 5, newWidth - 7, y + 17, 2, RenderUtil.isHovering(mouseX, mouseY, newWidth - 26, y + 5, newWidth - 7, y + 17) || MusicManager.INSTANCE.analyzeThread != null? new Color(80, 80, 80).getRGB():0xff34373c);
			Hanabi.INSTANCE.fontManager.wqy13.drawString("导入", newWidth - 23f, y + 6f, Color.GRAY.getRGB());

			if(textField.textString.isEmpty()) {
				Hanabi.INSTANCE.fontManager.wqy13.drawString("输入歌单ID", newX + 8, y + 6f, Color.GRAY.getRGB());
			}

			if(RenderUtil.isHovering(mouseX, mouseY, newX + 5, y + 20, newWidth - 5, y + height - 4)) {
				int wheel = Mouse.getDWheel() / 2;

				scrollY += wheel;
				if (scrollY <= minY)
					scrollY = minY;
				if (scrollY >= 0f)
					scrollY = 0f;

				minY = height - 24;
			} else {
				Mouse.getDWheel(); //用于刷新滚轮数据
			}

			this.scrollAni = RenderUtil.getAnimationState(this.scrollAni, scrollY, Math.max(10, (Math.abs(this.scrollAni - (scrollY))) * 50) * 0.3f);
			float startY = y + 21 + this.scrollAni;
			float yShouldbe = 0;

			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			RenderUtil.doGlScissor((int)newX + 6, (int)y + 21, 137, 224);

			//RenderUtil.drawRect(newX + 6, y + 21, newX + 143, y + 245, Colors.GREEN.c);

			for(TrackSlot s : slots) {
				if(startY > y && startY < y + height - 4) {
					s.render(newX + 6, startY, mouseX, mouseY);
				}
				startY += 22;
				yShouldbe += 22;
			}

			GL11.glDisable(GL11.GL_SCISSOR_TEST);

			if(RenderUtil.isHovering(mouseX, mouseY, newX + 5, y + 20, newWidth - 5, y + height - 4)) {
				minY -= yShouldbe;
			}

			//遮板
			//RenderUtil.drawOutlinedRect(newX + 4, y + 18, newWidth - 5f, y + height - 2, 2f, 0xff2f3136);

			if(this.slots.size() > 10) {
				float viewable = 224f;

				float progress = MathHelper.clamp_float(-this.scrollAni / -this.minY, 0, 1);

				float ratio = (viewable / yShouldbe) * viewable;
				float barHeight = Math.max(ratio, 20f);

				float position = progress * (viewable - barHeight);

				RenderUtil.drawRect(newWidth - 5, y + 21, newWidth - 2, y + 245f, new Color(100,100,100).getRGB());
				RenderUtil.drawRect(newWidth - 5, y + 21 + position, newWidth - 2, y + 21 + position + barHeight, new Color(73, 73, 73).getRGB());
			}

		} else {
			Mouse.getDWheel(); //用于刷新滚轮数据
		}

		//主框架
		RenderUtil.drawRoundedRect(x, y, x + width, y + height, 2, 0xff2f3136);
		RenderUtil.drawRoundedRect(x, y + height - 60, x + width, y + height, 2, 0xff34373c);
		RenderUtil.drawRect(x, y + height - 60, x + width, y + height - 58, 0xff34373c);

		Hanabi.INSTANCE.fontManager.wqy16.drawString("网易云音乐", x + (width / 2) - (Hanabi.INSTANCE.fontManager.wqy16.getStringWidth("网易云音乐") / 2f) - 2, y + 5, -1);

		float progress = 0;
		if (MusicManager.INSTANCE.getMediaPlayer() != null) {
			progress = (float) MusicManager.INSTANCE.getMediaPlayer().getCurrentTime().toSeconds() / (float) MusicManager.INSTANCE.getMediaPlayer().getStopTime().toSeconds() * 100;
		}

		//进度条
		RenderUtil.drawRoundedRect(x + 10, y + height - 50, x + width - 10, y + height - 46,1.4f, Color.GRAY.getRGB());

		if(MusicManager.INSTANCE.loadingThread != null) {
			RenderUtil.drawRoundedRect(x + 10, y + height - 50, x + 10 + (1.3f * MusicManager.INSTANCE.downloadProgress), y + height - 46,1.4f, Color.WHITE.getRGB());
			RenderUtil.circle(x + 10 + (1.3f * MusicManager.INSTANCE.downloadProgress), y + height - 48, 3, new Color(255, 255, 255).getRGB());
			RenderUtil.circle(x + 10 + (1.3f * MusicManager.INSTANCE.downloadProgress), y + height - 48, 2, PaletteUtil.fade(new Color(255, 50, 50, 255)).getRGB());
		} else {
			RenderUtil.drawRoundedRect(x + 10, y + height - 50, x + 10 + (1.3f * progress), y + height - 46,1.4f, Color.WHITE.getRGB());
			RenderUtil.circle(x + 10 + (1.3f * progress), y + height - 48, 3,new Color(255, 255, 255).getRGB());
			RenderUtil.circle(x + 10 + (1.3f * progress), y + height - 48, 2,new Color(50, 176, 255, 255).getRGB());
		}

		//按钮
		RenderUtil.circle(x + (width / 2), y + height - 24, 12, 0xff40444b); //播放和暂停

		if(extended) {
			Hanabi.INSTANCE.fontManager.micon15.drawString("C", x + width - 15, y + 5.5f, Color.WHITE.getRGB());
		} else {
			Hanabi.INSTANCE.fontManager.micon15.drawString("Q", x + width - 15, y + 5.5f, Color.WHITE.getRGB());
		}

		Hanabi.INSTANCE.fontManager.micon15.drawString("R", x + 5, y + 5.5f, Color.WHITE.getRGB());

		String songName = MusicManager.INSTANCE.currentTrack == null ? "当前未在播放" : MusicManager.INSTANCE.currentTrack.name;
		String songArtist = MusicManager.INSTANCE.currentTrack == null ? "N/A" : MusicManager.INSTANCE.currentTrack.artists;

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		RenderUtil.doGlScissor((int) x, (int) y + (int) (height / 2 - 95), (int) width, 25);
		Hanabi.INSTANCE.fontManager.wqy16.drawString(songName, x + (width / 2) - (Hanabi.INSTANCE.fontManager.wqy16.getStringWidth(songName) / 2) - 1.5f, y + (height / 2 - 95), -1);
		Hanabi.INSTANCE.fontManager.wqy13.drawString(songArtist, x + (width / 2) - (Hanabi.INSTANCE.fontManager.wqy13.getStringWidth(songArtist) / 2) - 1.5f, y + (height / 2 - 82), -1);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		if(MusicManager.INSTANCE.getMediaPlayer() != null) {

			if(MusicManager.INSTANCE.getMediaPlayer().getStatus() == Status.PLAYING) {
				Hanabi.INSTANCE.fontManager.micon30.drawString("K", x + (width / 2) - (Hanabi.INSTANCE.fontManager.micon30.getStringWidth("K") / 2) - 2f, y + height - 32, Color.WHITE.getRGB());
			} else {
				Hanabi.INSTANCE.fontManager.micon30.drawString("J", x + (width / 2) - (Hanabi.INSTANCE.fontManager.micon30.getStringWidth("J") / 2), y + height - 32, Color.WHITE.getRGB());
			}

		} else {
			Hanabi.INSTANCE.fontManager.micon30.drawString("J", x + (width / 2) - (Hanabi.INSTANCE.fontManager.micon30.getStringWidth("J") / 2), y + height - 32, Color.WHITE.getRGB());
		}

		Hanabi.INSTANCE.fontManager.micon30.drawString("L", x + width / 2 - (Hanabi.INSTANCE.fontManager.micon30.getStringWidth("L") / 2) - 30, y + height - 32, Color.WHITE.getRGB());
		Hanabi.INSTANCE.fontManager.micon30.drawString("M", x + width / 2 - (Hanabi.INSTANCE.fontManager.micon30.getStringWidth("M") / 2) + 27.5f, y + height - 32, Color.WHITE.getRGB());

		if(MusicManager.INSTANCE.repeat) {
			Hanabi.INSTANCE.fontManager.micon15.drawString("O", x + width - 20, y + height - 28.5f, Color.WHITE.getRGB());
		} else {
			Hanabi.INSTANCE.fontManager.micon15.drawString("N", x + width - 20, y + height - 28.5f, Color.WHITE.getRGB());
		}

		if(MusicManager.INSTANCE.lyric) {
			Hanabi.INSTANCE.fontManager.micon15.drawString("P", x + 10, y + height - 28.5f, 0xffffffff);
		} else {
			Hanabi.INSTANCE.fontManager.micon15.drawString("P", x + 10, y + height - 28.5f, 0xff6b6e71);
		}

		if(MusicManager.INSTANCE.currentTrack != null) {
			if(MusicManager.INSTANCE.getArt(MusicManager.INSTANCE.currentTrack.id) != null) {
				GL11.glPushMatrix();
				RenderUtil.drawImage(MusicManager.INSTANCE.getArt(MusicManager.INSTANCE.currentTrack.id), x + (width / 2) - 50, y + (height / 2 - 10) - 50, 100, 100, 1f);
				GL11.glPopMatrix();
			}
		}

		//RenderUtil.drawOutlinedRect(x + (width / 2) - 50, y + (height / 2 - 10) - 50, x + (width / 2) + 50, y + (height / 2 - 10) + 50, .5f, Color.WHITE.getRGB());

		//Debug
		//RenderUtil.drawOutlinedRect(x, y, x + width, y + 20, .5f, Color.RED.getRGB()); //标题框
		//RenderUtil.drawOutlinedRect(x + width - 15, y + 5, x + width - 5, y + 15, .5f, Color.RED.getRGB()); //展开侧栏
		//RenderUtil.drawOutlinedRect(x + 5, y + 5, x + 15, y + 15, .5f, Color.RED.getRGB()); //二维码登录
		//RenderUtil.drawOutlinedRect(x + width - 20, y + height - 29, x + width - 10, y + height - 19, .5f, Color.RED.getRGB()); //单曲循环
		//RenderUtil.drawOutlinedRect(x + 10, y + height - 29, x + 20, y + height - 19, .5f, Color.RED.getRGB()); //歌词按钮
		//RenderUtil.drawOutlinedRect(x + (width / 2) - 12, y + height - 36, x + (width / 2) + 12, y + height - 12, .5f, Color.RED.getRGB()); //播放和暂停
		//RenderUtil.drawOutlinedRect(x + 39, y + height - 32, x + 55, y + height - 16, .5f, Color.RED.getRGB()); //上一曲
		//RenderUtil.drawOutlinedRect(x + 96, y + height - 32, x + 112, y + height - 16, .5f, Color.RED.getRGB()); //下一曲

		this.dragWindow(mouseX, mouseY);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

		if(RenderUtil.isHovering(mouseX, mouseY, x + width - 15, y + 5, x + width - 5, y + 15) && mouseButton == 0) {
			extended = !extended;
		}

		if(mouseButton == 0) {
			//播放/暂停
			if(RenderUtil.isHovering(mouseX, mouseY, x + (width / 2) - 12, y + height - 36, x + (width / 2) + 12, y + height - 12)) {
				if(!MusicManager.INSTANCE.playlist.isEmpty()) {
					if(MusicManager.INSTANCE.currentTrack == null) {
						MusicManager.INSTANCE.play(MusicManager.INSTANCE.playlist.get(0));
					} else {
						if(MusicManager.INSTANCE.getMediaPlayer() != null) {
							if(MusicManager.INSTANCE.getMediaPlayer().getStatus() == Status.PLAYING) {
								MusicManager.INSTANCE.getMediaPlayer().pause();
							} else {
								MusicManager.INSTANCE.getMediaPlayer().play();
							}
						}
					}
				}
			}

			//上一曲
			if(RenderUtil.isHovering(mouseX, mouseY, x + 39, y + height - 32, x + 55, y + height - 16)) {
				MusicManager.INSTANCE.prev();
			}

			//下一曲
			if(RenderUtil.isHovering(mouseX, mouseY, x + 96, y + height - 32, x + 112, y + height - 16)) {
				MusicManager.INSTANCE.next();
			}

			//歌词按钮
			if(RenderUtil.isHovering(mouseX, mouseY, x + 10, y + height - 29, x + 20, y + height - 19)) {
				MusicManager.INSTANCE.lyric = !MusicManager.INSTANCE.lyric;
			}

			//单曲循环
			if(RenderUtil.isHovering(mouseX, mouseY, x + width - 20, y + height - 29, x + width - 10, y + height - 19)) {
				MusicManager.INSTANCE.repeat = !MusicManager.INSTANCE.repeat;
			}

			//QRCode
			if(RenderUtil.isHovering(mouseX, mouseY, x + 5, y + 5, x + 15, y + 15)) {
				mc.displayGuiScreen(new QRLoginScreen(this));
			}
		}

		if(extended && Math.ceil(sidebarAnimation) >= width + 5) {
			float newX = x + sidebarAnimation;
			float newWidth = x + width + sidebarAnimation;

			if(mouseButton == 0) {
				if(RenderUtil.isHovering(mouseX, mouseY, newWidth - 26, y + 5, newWidth - 7, y + 17) && !this.textField.textString.isEmpty() && MusicManager.INSTANCE.analyzeThread == null) {
					MusicManager.INSTANCE.analyzeThread = new Thread(() -> {
						try {
							this.slots.clear();

							MusicManager.INSTANCE.playlist = (ArrayList<Track>) CloudMusicAPI.INSTANCE.getPlaylistDetail(this.textField.textString)[1];

							for(Track t : MusicManager.INSTANCE.playlist) {
								this.slots.add(new TrackSlot(t));
							}

						} catch (Exception ex) {
							PlayerUtil.tellPlayer("解析歌单时发生错误!");
							ex.printStackTrace();
						}

						MusicManager.INSTANCE.analyzeThread = null;
					});

					MusicManager.INSTANCE.analyzeThread.start();
				}
			}

			//歌曲列表
			if(RenderUtil.isHovering(mouseX, mouseY, newX + 5, y + 20, newWidth - 5, y + height - 4)) {
				float startY = y + 21 + this.scrollAni;
				for(TrackSlot s : slots) {
					if(startY > y && startY < y + height - 4) {
						s.click(mouseX, mouseY, mouseButton);
					}
					startY += 22;
				}
			}

			this.textField.mouseClicked(mouseX, mouseY, mouseButton);
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);
	}



	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {

		if(extended) {
			this.textField.keyPressed(keyCode);
			this.textField.charTyped(typedChar);
		}

		super.keyTyped(typedChar, keyCode);
	}

	public void dragWindow(int mouseX, int mouseY) {
		if (RenderUtil.isHovering(mouseX, mouseY, x + width - 15, y + 5, x + width - 5, y + 15)) return;

		if (!Mouse.isButtonDown(0) && this.drag) {
			this.drag = false;
		}

		if (this.drag) {
			this.x = mouseX - this.x2;
			this.y = mouseY - this.y2;
		}else if (RenderUtil.isHovering(mouseX, mouseY, x, y, x + width, y + 20) && Mouse.isButtonDown(0)) {
			this.drag = true;
			this.x2 = mouseX - this.x;
			this.y2 = mouseY - this.y;
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}