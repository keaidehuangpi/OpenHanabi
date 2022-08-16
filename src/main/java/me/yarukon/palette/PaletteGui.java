package me.yarukon.palette;

import cn.hanabi.Hanabi;
import cn.hanabi.utils.RenderUtil;
import me.yarukon.YRenderUtil;
import me.yarukon.palette.impl.ColorPicker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;

public class PaletteGui extends GuiScreen {
    public float x;
    public float y;

    public Minecraft mc = Minecraft.getMinecraft();

    // Dragging
    public float x2;
    public float y2;
    public boolean drag = false;
    public float draggableHeight = 15f;

    public ArrayList<ColorPicker> colorPickers = new ArrayList<>();

    //Scroller
    public int mouseXX;
    public int mouseYY;

    public float scrollY = 0;
    public float scrollAni = 0;
    public float minY = -100;

    @Override
    public void initGui() {
        colorPickers.clear();

        for(ColorValue v : ColorValue.colorValues) {
            this.colorPickers.add(new ColorPicker(this, v));
        }

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.mouseXX = mouseX;
        this.mouseYY = mouseY;

        YRenderUtil.drawRect(x, y, 200, draggableHeight, 0xff00af87);
        YRenderUtil.drawRect(x, y + draggableHeight, 200, 300, 0xff36393f);
        Hanabi.INSTANCE.fontManager.usans18.drawString("Palette", x + 5, y + (draggableHeight / 2) - (Hanabi.INSTANCE.fontManager.usans18.FONT_HEIGHT / 2f), 0xffffffff);

        if(YRenderUtil.isHoveringBound(mouseX, mouseY, x, y + draggableHeight, 200, 300)) {
            minY = 301;
        }

        this.scrollAni = RenderUtil.smoothAnimation(this.scrollAni, scrollY, 50, .3f);

        float startY = y + draggableHeight + 2 + this.scrollAni;
        float totalY = 0;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.doGlScissor((int) x, (int) (y + draggableHeight), 200, 300);
        for(ColorPicker c : this.colorPickers) {
            c.render(x + 5, startY, 188, 140, mouseX, mouseY);

            startY += 145;
            totalY += 145;
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if(YRenderUtil.isHoveringBound(mouseX, mouseY, x, y + draggableHeight, 200, 300)) {
            minY -= totalY;
        }

        if(totalY > 300) {
            float viewable = 296;
            float progress = MathHelper.clamp_float(-this.scrollAni / -this.minY, 0, 1);
            float ratio = (viewable / totalY) * viewable;
            float barHeight = Math.max(ratio, 20f);
            float position = progress * (viewable - barHeight);

            YRenderUtil.drawRect(x + 196.5f, y + draggableHeight + 2, 2, 296, 0xff2e3338);
            YRenderUtil.drawRect(x + 196.5f, y + draggableHeight + 2 + position, 2, barHeight, 0xffffffff);
        }

        coordinateUpdate(mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        if(YRenderUtil.isHoveringBound(mouseXX, mouseYY, x, y + draggableHeight, 200, 300)) {
            scrollY += Mouse.getEventDWheel() / 5f;

            if (scrollY <= minY)
                scrollY = minY;
            if (scrollY >= 0f)
                scrollY = 0f;

        }

        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton == 0) {
            if (YRenderUtil.isHoveringBound(mouseX, mouseY, x, y, 200, draggableHeight)) {
                this.drag = true;
                this.x2 = mouseX - this.x;
                this.y2 = mouseY - this.y;
            }
        }

        for(ColorPicker c : this.colorPickers) {
            c.mouseClicked(mouseX, mouseY, mouseButton);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.drag) {
            this.drag = false;
        }

        for(ColorPicker c : this.colorPickers) {
            c.mouseReleased(mouseX, mouseY, state);
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    public void coordinateUpdate(int mouseX, int mouseY) {
        if (this.drag) {
            this.x = mouseX - this.x2;
            this.y = mouseY - this.y2;
        }
    }

    @Override
    public void onGuiClosed() {
        Hanabi.INSTANCE.fileManager.saveColors();
        super.onGuiClosed();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
