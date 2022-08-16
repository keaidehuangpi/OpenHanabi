package me.yarukon.mainmenu;

import cn.hanabi.Client;
import cn.hanabi.Hanabi;
import cn.hanabi.altmanager.GuiAltManager;
import cn.hanabi.gui.superskidder.material.items.SButton;
import cn.hanabi.gui.superskidder.material.items.impl.RoundButton;
import cn.hanabi.utils.ParticleUtils;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.fontmanager.HanabiFonts;
import me.yarukon.BlurBuffer;
import me.yarukon.YRenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class GuiCustomMainMenu extends GuiScreen {
    public ArrayList<GuiMainMenuButton> butt = new ArrayList<>();

    private float currentX;
    private float currentY;
    private ScaledResolution res;

    @Override
    public void initGui() {
        butt.clear();
        butt.add(new GuiMainMenuButton(this, 0, "G", "SinglePlayer", () -> mc.displayGuiScreen(new GuiSelectWorld(this))));
        butt.add(new GuiMainMenuButton(this, 1, "H", "MultiPlayer", () -> mc.displayGuiScreen(new GuiMultiplayer(this))));
        butt.add(new GuiMainMenuButton(this, 2, "I", "AltManager", () -> mc.displayGuiScreen(new GuiAltManager())));
        butt.add(new GuiMainMenuButton(this, 3, "J", "Mods", () -> mc.displayGuiScreen(new net.minecraftforge.fml.client.GuiModList(this)), .5f));
        butt.add(new GuiMainMenuButton(this, 4, "K", "Options", () -> mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings))));
        butt.add(new GuiMainMenuButton(this, 5, "L", "Languages", () -> mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()))));
        butt.add(new GuiMainMenuButton(this, 6, "M", "Quit", () -> mc.shutdown()));

        res = new ScaledResolution(this.mc);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawGradientRect(0, 0, this.width, this.height, 0x00FFFFFF, 0x00FFFFFF);

        int h = this.height;
        int w = this.width;

        float xDiff = ((float) (mouseX - h / 2) - this.currentX) / (float) res.getScaleFactor();
        float yDiff = ((float) (mouseY - w / 2) - this.currentY) / (float) res.getScaleFactor();
        this.currentX += xDiff * 0.3f;
        this.currentY += yDiff * 0.3f;

        GlStateManager.translate(this.currentX / 30.0f, this.currentY / 15.0f, 0.0f);

        if (!Client.onDebug)
            RenderUtil.drawImage(new ResourceLocation("Client/mainmenu/mainmenu.png"), -30, -30, res.getScaledWidth() + 60, res.getScaledHeight() + 60);
        else
            RenderUtil.drawImage(new ResourceLocation("Client/mainmenu/scifi.png"), -30, -30, res.getScaledWidth() + 60, res.getScaledHeight() + 60);


        GlStateManager.translate(-this.currentX / 30.0f, -this.currentY / 15.0f, 0.0f);

        BlurBuffer.updateBlurBuffer(true);

        ParticleUtils.drawParticles(mouseX, mouseY);

        BlurBuffer.blurArea(width / 2f - (50 * (butt.size() / 2f)), height / 2f - 50, 50 * butt.size(), 100, true);

        //噪音
        //RenderUtil.drawImageSpread(new ResourceLocation("hythelper/glass.png"), width / 2 - (50 * (butt.size() / 2)), height / 2 - 50, 50 * butt.size(), 100, .45f);

        YRenderUtil.drawRectNormal(width / 2f - (50 * (butt.size() / 2f)), height / 2f - 50, width / 2f + (50 * (butt.size() / 2f)), height / 2f + 50, 0x7D000000);
        YRenderUtil.drawRectNormal(width / 2f - (50 * (butt.size() / 2f)), height / 2f + 20, width / 2f + (50 * (butt.size() / 2f)), height / 2f + 50, 0x3E000000);

        float startX = width / 2f - (50f * (butt.size() / 2f));
        for (GuiMainMenuButton button : butt) {
            button.draw(startX, height / 2f + 20, mouseX, mouseY);
            startX += 50f;
        }

        Hanabi.INSTANCE.fontManager.icon130.drawString(HanabiFonts.ICON_HANABI_LOGO, width / 2f - (50 * (butt.size() / 2f)) + 10, this.height / 2f + 5, 0xff2f64fd);
        Hanabi.INSTANCE.fontManager.usans25.drawString("Hanabi client", width / 2f - (50 * (butt.size() / 2f)) + 80, this.height / 2f - 30, 0xffffffff);
        Hanabi.INSTANCE.fontManager.usans20.drawString("Build " + Hanabi.CLIENT_VERSION, width / 2f - (50 * (butt.size() / 2f)) + 80, this.height / 2f - 10, 0xffffffff);

        String s = "Logged in as " + Client.username;
        Hanabi.INSTANCE.fontManager.usans20.drawString(s, width / 2f + (50 * (butt.size() / 2f)) - Hanabi.INSTANCE.fontManager.usans20.getStringWidth(s) - 10, this.height / 2f + 5, 0xffffffff);



        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {


        for (GuiMainMenuButton button : butt) {
            button.mouseClick(mouseX, mouseY, mouseButton);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        res = new ScaledResolution(mc);
        super.updateScreen();
    }

}
