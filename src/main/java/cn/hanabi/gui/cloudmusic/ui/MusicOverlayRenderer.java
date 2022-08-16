package cn.hanabi.gui.cloudmusic.ui;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Hanabi;
import cn.hanabi.gui.cloudmusic.MusicManager;
import cn.hanabi.gui.font.noway.ttfr.HFontRenderer;
import cn.hanabi.modules.modules.render.HUD;
import cn.hanabi.utils.ClientUtil;
import cn.hanabi.utils.Colors;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.gui.font.compat.WrappedVertexFontRenderer;
import me.yarukon.font.GlyphPageFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@ObfuscationClass
public enum MusicOverlayRenderer {
    INSTANCE;

    public String downloadProgress = "0";

    public long readedSecs = 0;
    public long totalSecs = 0;

    public float animation = 0;

    public TimeHelper timer = new TimeHelper();

    public boolean firstTime = true;

    public Hanabi hanaInstance = Hanabi.INSTANCE;

    public void renderOverlay() {
        int addonX = HUD.musicPosX.getValueState().intValue();
        int addonY = HUD.musicPosY.getValueState().intValue();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        if (MusicManager.INSTANCE.getCurrentTrack() != null && MusicManager.INSTANCE.getMediaPlayer() != null) {
            readedSecs = (int) MusicManager.INSTANCE.getMediaPlayer().getCurrentTime().toSeconds();
            totalSecs = (int) MusicManager.INSTANCE.getMediaPlayer().getStopTime().toSeconds();
        }

        if (MusicManager.INSTANCE.getCurrentTrack() != null && MusicManager.INSTANCE.getMediaPlayer() != null) {
            hanaInstance.fontManager.wqy18.drawString(MusicManager.INSTANCE.getCurrentTrack().name + " - " + MusicManager.INSTANCE.getCurrentTrack().artists, 36f + addonX, 10 + addonY, Colors.WHITE.c);
            hanaInstance.fontManager.wqy18.drawString(formatSeconds((int) readedSecs) + "/" + formatSeconds((int) totalSecs), 36f + addonX, 20 + addonY, 0xffffffff);

            if (MusicManager.INSTANCE.circleLocations.containsKey(MusicManager.INSTANCE.getCurrentTrack().id)) {
                GL11.glPushMatrix();
                GL11.glColor4f(1, 1, 1, 1);
                ResourceLocation icon = MusicManager.INSTANCE.circleLocations.get(MusicManager.INSTANCE.getCurrentTrack().id);
                RenderUtil.drawImage(icon, 4 + addonX, 6 + addonY, 28, 28);
                GL11.glPopMatrix();
            } else {
                MusicManager.INSTANCE.getCircle(MusicManager.INSTANCE.getCurrentTrack());
            }

            try {
                float currentProgress = (float) (MusicManager.INSTANCE.getMediaPlayer().getCurrentTime().toSeconds() / Math.max(1, MusicManager.INSTANCE.getMediaPlayer().getStopTime().toSeconds())) * 100;
                RenderUtil.drawArc(18 + addonX, 19 + addonY, 14, Colors.WHITE.c, 0, 360, 4);
                RenderUtil.drawArc(18 + addonX, 19 + addonY, 14, Colors.BLUE.c, 180, 180 + (currentProgress * 3.6f), 4);
            } catch (Exception ignored) {
            }
        }

        if(MusicManager.INSTANCE.lyric) { {
            HFontRenderer lyricFont = Hanabi.INSTANCE.fontManager.wqy18;
            int addonYlyr = HUD.musicPosYlyr.getValueState().intValue();
            //Lyric
            int col = MusicManager.INSTANCE.tlrc.isEmpty() ? Colors.GREY.c : 0xff00af87;

            GlStateManager.disableBlend();
            lyricFont.drawCenteredString(MusicManager.INSTANCE.lrcCur.contains("_EMPTY_") ? "等待中......." : MusicManager.INSTANCE.lrcCur, sr.getScaledWidth() / 2f - 0.5f, sr.getScaledHeight() - 140 - 80 + addonYlyr, 0xff00af87);
            lyricFont.drawCenteredString(MusicManager.INSTANCE.tlrcCur.contains("_EMPTY_") ? "Waiting......." : MusicManager.INSTANCE.tlrcCur, sr.getScaledWidth() / 2f, sr.getScaledHeight() - 125 + 0.5f - 80 + addonYlyr, col);
            GlStateManager.enableBlend();
        }}

        if ((MusicManager.showMsg)) {
            if (firstTime) {
                timer.reset();
                firstTime = false;
            }

            HFontRenderer wqy = Hanabi.INSTANCE.fontManager.wqy18;
            HFontRenderer sans = Hanabi.INSTANCE.fontManager.usans25;

            float width1 = wqy.getStringWidth(MusicManager.INSTANCE.getCurrentTrack().name);
            float width2 = sans.getStringWidth("Now playing");
            float allWidth = (Math.max(Math.max(width1, width2), 150));

            RenderUtil.drawRect(sr.getScaledWidth() - animation, 5, sr.getScaledWidth(), 40, ClientUtil.reAlpha(Colors.BLACK.c, 0.7f));

            if (MusicManager.INSTANCE.circleLocations.containsKey(MusicManager.INSTANCE.getCurrentTrack().id)) {
                GL11.glPushMatrix();
                GL11.glColor4f(1, 1, 1, 1);
                ResourceLocation icon = MusicManager.INSTANCE.circleLocations.get(MusicManager.INSTANCE.getCurrentTrack().id);
                RenderUtil.drawImage(icon, sr.getScaledWidth() - animation + 5, 8, 28, 28);
                GL11.glPopMatrix();
            } else {
                MusicManager.INSTANCE.getCircle(MusicManager.INSTANCE.getCurrentTrack());
            }

            RenderUtil.drawArc(sr.getScaledWidth() - animation - 31 + 50, 22, 14, Colors.WHITE.c, 0, 360, 2);

            sans.drawString("Now playing", sr.getScaledWidth() - animation - 12 + 50, 8, Colors.WHITE.c);
            wqy.drawString(MusicManager.INSTANCE.getCurrentTrack().name, sr.getScaledWidth() - animation - 12 + 50, 26, Colors.WHITE.c);

            if (timer.isDelayComplete(5000)) {
                this.animation = (float) RenderUtil.getAnimationStateSmooth(0, animation, 10.0f / Minecraft.getDebugFPS());
                if (this.animation <= 0) {
                    MusicManager.showMsg = false;
                    firstTime = true;
                }
            } else {
                this.animation = (float) RenderUtil.getAnimationStateSmooth(allWidth, animation, 10.0f / Minecraft.getDebugFPS());
            }

        }

        GlStateManager.resetColor();
    }

    public String formatSeconds(int seconds) {
        String rstl = "";
        int mins = seconds / 60;
        if (mins < 10) {
            rstl += "0";
        }
        rstl += mins + ":";
        seconds %= 60;
        if (seconds < 10) {
            rstl += "0";
        }
        rstl += seconds;
        return rstl;
    }
}
