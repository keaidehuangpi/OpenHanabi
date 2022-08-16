package cn.hanabi.altmanager;

import cn.hanabi.Client;
import cn.hanabi.Hanabi;
import cn.hanabi.utils.ParticleUtils;
import cn.hanabi.utils.RenderUtil;
import com.thealtening.auth.TheAlteningAuthentication;
import com.thealtening.auth.service.AlteningServiceType;
import me.yarukon.BlurBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GuiAltManager extends GuiScreen {
    public static String Api = null;
    private final ResourceLocation background = new ResourceLocation("textures/mainmenubackground.png");
    public Alt selectedAlt;
    private GuiButton login;
    private GuiButton remove;
    private GuiButton rename;
    private AltLoginThread loginThread;
    private int offset;
    private static String status;
    private CustomGuiTextField seatchField;
    private CustomGuiTextField add_UserNameField;
    private CustomGuiTextField add_PassWordField;

    private CustomGuiTextField edit_UserNameField;
    private CustomGuiTextField edit_PassWordField;

    public GuiAltManager() {
        this.selectedAlt = null;
        status = EnumChatFormatting.GRAY + "Idle...";
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }

    public static int getColor(Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getColor(int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(int brightness, int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 3: {
                Hanabi.INSTANCE.mslogin = !Hanabi.INSTANCE.mslogin;
                mc.displayGuiScreen(new GuiAltManager());
                break;
            }
            case 5: {
                ArrayList<Alt> registry = AltManager.registry;
                Random random = new Random();
                if (registry.size() > 1) {
                    Alt randomAlt = registry.get(random.nextInt(AltManager.registry.size()));
                    (this.loginThread = new AltLoginThread(randomAlt)).start();
                } else {
                    status = EnumChatFormatting.RED + "There's no any alts!";
                }
                break;
            }
            case 7: {
                this.mc.displayGuiScreen(null);
                break;
            }
            case 8: {
                AltManager.registry.clear();
                try {
                    Hanabi.INSTANCE.altFileMgr.getFile(Alts.class).loadFile();
                } catch (IOException ignored) {

                }
                status = "\247bReloaded!";
                break;
            }
            case 9: {
                mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            }
            case 10: {
                TheAlteningAuthentication.theAltening().updateService(AlteningServiceType.THEALTENING);

                break;
            }
            case 11: {
                TheAlteningAuthentication.theAltening().updateService(AlteningServiceType.MOJANG);
                break;
            }
            case 12: {
                if (Desktop.isDesktopSupported()) {
                    try {
                        URI uri = URI.create("http://anwen.love");
                        Desktop dp = Desktop.getDesktop();
                        if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                            dp.browse(uri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            }
            case 13: {
                Hanabi.INSTANCE.hypixelBypass = !Hanabi.INSTANCE.hypixelBypass;
                mc.displayGuiScreen(new GuiAltManager());
                break;
            }
            case 14: {
                new Thread("Ban Check") {
                    @Override
                    public void run() {
                        status = "\247cChecking All Alts...";
                        int size = getAlts().size();
                        int count = 0;
                        int bannedCount = 0;
                        for (Alt alt : getAlts()) {
                            if (!alt.getPassword().equals("")) {
                                status += " (" + Math.round(((float) ++count / size) * 100) + "%)";
                            }
                        }
                        Hanabi.INSTANCE.altFileMgr.saveFiles();
                        status = "\247aDone! Deleted " + bannedCount + " alt" + ((bannedCount > 1) ? "s" : "") + "!";
                    }
                }.start();
                break;
            }
        }
    }

    private float currentX;
    private float currentY;

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float par3) {
        ScaledResolution res = new ScaledResolution(mc);
        int w = res.getScaledWidth();
        int h = res.getScaledHeight();
        ScaledResolution sr = new ScaledResolution(this.mc);
        float xDiff = ((float) (mouseX - h / 2) - this.currentX) / (float) sr.getScaleFactor();
        float yDiff = ((float) (mouseY - w / 2) - this.currentY) / (float) sr.getScaleFactor();
        this.currentX += xDiff * 0.3f;
        this.currentY += yDiff * 0.3f;
        GlStateManager.translate(this.currentX / 30.0f, this.currentY / 15.0f, 0.0f);
        if (!Client.onDebug) {
            RenderUtil.drawImage(new ResourceLocation("Client/mainmenu/mainmenu.png"), -30, -30, sr.getScaledWidth() + 60,
                    sr.getScaledHeight() + 60);
        } else {
            RenderUtil.drawImage(new ResourceLocation("Client/mainmenu/scifi.png"), -30, -30, sr.getScaledWidth() + 60,
                    sr.getScaledHeight() + 60);
        }


        ParticleUtils.drawParticles(mouseX, mouseY);

        GlStateManager.translate(-this.currentX / 30.0f, -this.currentY / 15.0f, 0.0f);
        if (Mouse.hasWheel()) {
            final int wheel = Mouse.getDWheel();
            if (wheel < 0) {
                this.offset += 26;
                if (this.offset < 0) {
                    this.offset = 0;
                }
            } else if (wheel > 0) {
                this.offset -= 26;
                if (this.offset < 0) {
                    this.offset = 0;
                }
            }
        }
        BlurBuffer.updateBlurBuffer(true);

        BlurBuffer.blurArea(50.0f, 33.0f, this.width - 100, this.height - 83, true);
        RenderUtil.drawRect(50.0f, 33.0f, this.width - 50, this.height - 50, new Color(0, 0, 0, 150).getRGB());


        this.drawString(this.fontRendererObj, this.mc.getSession().getUsername(), 10, 10, 14540253);
        final FontRenderer fontRendererObj = this.fontRendererObj;
        final StringBuilder sb = new StringBuilder("Account Manager - ");
        this.drawCenteredString(fontRendererObj, sb.append(AltManager.registry.size()).append(" alts").toString(),
                this.width / 2, 10, -1);
        this.drawCenteredString(this.fontRendererObj,
                (this.loginThread == null) ? status : this.loginThread.getStatus(), this.width / 2, 20, -1);
        this.drawHeader(mouseX, mouseY);
        this.drawAlt(mouseX, mouseY);
        this.drawEditAndAdd(mouseX, mouseY);
        if (Keyboard.isKeyDown(200)) {
            this.offset -= 26;
        } else if (Keyboard.isKeyDown(208)) {
            this.offset += 26;
        }
        if (this.offset < 0) {
            this.offset = 0;
        }
        this.seatchField.drawTextBox();
        this.edit_UserNameField.drawTextBox();
        this.edit_PassWordField.drawTextBox();
        this.add_UserNameField.drawTextBox();
        this.add_PassWordField.drawTextBox();
        super.drawScreen(mouseX, mouseY, par3);

//        if (seatchField.getText().isEmpty() && !seatchField.isFocused()) {
//            this.drawString(this.mc.fontRendererObj, "Search Alt", this.width / 2 - 264, this.height - 18, -1);
//        }

    }

    double anim_add = 0;
    double anim_import = 0;

    double anim_confirm = 0;

    public void drawEditAndAdd(int mouseX, int mouseY) {
        Hanabi.INSTANCE.fontManager.usans25.drawString("Add Alt", (width / 2) + 20, 45, new Color(255, 255, 255).getRGB());
        //RenderUtil.drawRect((width / 2f) + 18,150,(width / 2f) + 42,174,new Color(255,0,0).getRGB());
        //(width / 2f) + 18,150,(width / 2f) + 42,174
        String hoverStringAdd = "";

        String hoverStringEdit = "";
        //+
        Hanabi.INSTANCE.fontManager.altManagerIcon45.drawString("C", (width / 2) + 20, 110, Color.WHITE.getRGB());
        if (isRectHovered((width / 2f) + 18, 90, (width / 2f) + 42, 120, mouseX, mouseY)) {
            anim_add = RenderUtil.getAnimationStateSmooth(2, anim_add, 10f / Minecraft.getDebugFPS());
            hoverStringAdd = "Add";
        } else {
            anim_add = RenderUtil.getAnimationStateSmooth(0, anim_add, 10f / Minecraft.getDebugFPS());
        }
        RenderUtil.drawRect((width / 2f) + 18, (float) (125 - anim_add), (width / 2f) + 42, 125, new Color(47, 116, 253, 255).getRGB());

        //import
        Hanabi.INSTANCE.fontManager.altManagerIcon45.drawString("E", (width / 2) + 55, 110, Color.WHITE.getRGB());
        if (isRectHovered((width / 2f) + 48, (float) (90), (width / 2f) + 78, 120, mouseX, mouseY)) {
            anim_import = RenderUtil.getAnimationStateSmooth(2, anim_import, 10f / Minecraft.getDebugFPS());
            hoverStringAdd = "Import from clipboard";
        } else {
            anim_import = RenderUtil.getAnimationStateSmooth(0, anim_import, 10f / Minecraft.getDebugFPS());
        }
        RenderUtil.drawRect((width / 2f) + 48, (float) (125 - anim_import), (width / 2f) + 78, 125, new Color(47, 116, 253, 255).getRGB());


        Hanabi.INSTANCE.fontManager.wqy16.drawString(hoverStringAdd, (width / 2) + 86, 110, Color.WHITE.getRGB());

        //edit
        Hanabi.INSTANCE.fontManager.usans25.drawString("Edit Alt", (width / 2) + 20, 130, new Color(255, 255, 255).getRGB());

        Hanabi.INSTANCE.fontManager.altManagerIcon45.drawString("A", (width / 2) + 20, 190, Color.WHITE.getRGB());
        //(width / 2f) + 18, (float) (298 - anim_import ),(width / 2f) + 43,326
        if (isRectHovered((width / 2f) + 18, (float) (180), (width / 2f) + 43, 210, mouseX, mouseY)) {
            anim_confirm = RenderUtil.getAnimationStateSmooth(2, anim_confirm, 10f / Minecraft.getDebugFPS());
            hoverStringEdit = "Confirm";
        } else {
            anim_confirm = RenderUtil.getAnimationStateSmooth(0, anim_confirm, 10f / Minecraft.getDebugFPS());
        }
        RenderUtil.drawRect((width / 2f) + 18, (float) (200 - anim_confirm), (width / 2f) + 43, 200, new Color(47, 116, 253, 255).getRGB());

        Hanabi.INSTANCE.fontManager.wqy16.drawString(hoverStringEdit, (width / 2) + 70, 185, Color.WHITE.getRGB());
    }

    double anim1 = 0d;
    double anim2 = 0d;
    double anim3 = 0d;

    public void drawHeader(int mouseX, int mouseY) {
        String hovering = "";
        //√
        Hanabi.INSTANCE.fontManager.altManagerIcon45.drawString("A", 65.0f, 55, Color.WHITE.getRGB());
        if (isRectHovered(63.0f, 50, (int) (67.0f + 20), 73, mouseX, mouseY)) {
            anim1 = RenderUtil.getAnimationStateSmooth(2, anim1, 10f / Minecraft.getDebugFPS());
            hovering = "Login";
        } else {
            anim1 = RenderUtil.getAnimationStateSmooth(0, anim1, 10f / Minecraft.getDebugFPS());
        }
        RenderUtil.drawRect(63.0f, (float) (73 - anim1), 67.0f + 20, 73, new Color(47, 116, 253, 255).getRGB());

        //×
        Hanabi.INSTANCE.fontManager.altManagerIcon45.drawString("B", 100.0f, 55, Color.WHITE.getRGB());
        if (isRectHovered(95, 50, (int) (95f + 20), 73, mouseX, mouseY)) {
            anim2 = RenderUtil.getAnimationStateSmooth(2, anim2, 10f / Minecraft.getDebugFPS());
            hovering = "Remove";
        } else {
            anim2 = RenderUtil.getAnimationStateSmooth(0, anim2, 10f / Minecraft.getDebugFPS());
        }

        RenderUtil.drawRect(95.0f, (float) (73 - anim2), 95.0f + 25, 73, new Color(47, 116, 253, 255).getRGB());

        Hanabi.INSTANCE.fontManager.wqy16.drawString(hovering, 130, 52, new Color(200, 200, 200).getRGB());
    }

    public void drawAlt(int par1, int par2) {
        GL11.glPushMatrix();
        this.prepareScissorBox(0.0f, 76.0f, this.width, this.height - 50);
        GL11.glEnable(3089);
        int y = 80;
        for (final Alt alt : getAlts()) {
            if (isAltInArea(y)) {
                String name;
                if (alt.getMask().equals("")) {
                    name = alt.getUsername();
                } else {
                    name = alt.getMask();
                }
                String pass;
                if (alt.getPassword().equals("")) {
                    pass = "Cracked";
                } else {
                    pass = alt.getPassword().replaceAll("\\.", "*");
                }
                if (alt == this.selectedAlt) {
                    if (this.isMouseOverAlt(par1, par2, y - this.offset) && Mouse.isButtonDown(0)) {
                        RenderUtil.drawRect(60.0f, y - this.offset - 4, this.width / 2f, y - this.offset + 30,
                                getColor(250, 50));
                    } else if (this.isMouseOverAlt(par1, par2, y - this.offset)) {
                        RenderUtil.drawRect(60.0f, y - this.offset - 4, this.width / 2f, y - this.offset + 30,
                                getColor(200, 50));
                    } else {
                        RenderUtil.drawRect(60.0f, y - this.offset - 4, this.width / 2f, y - this.offset + 30,
                                getColor(255, 50));
                    }
                } else if (this.isMouseOverAlt(par1, par2, y - this.offset) && Mouse.isButtonDown(0)) {
                    RenderUtil.drawRect(60.0f, y - this.offset - 4, this.width / 2f, y - this.offset + 30,
                            -getColor(250, 50));
                } else if (this.isMouseOverAlt(par1, par2, y - this.offset)) {
                    RenderUtil.drawRect(60.0f, y - this.offset - 4, this.width / 2f, y - this.offset + 30,
                            getColor(200, 50));
                }
                Hanabi.INSTANCE.fontManager.wqy18.drawString(name, 50.0f + 25, y - this.offset, -1);

                Hanabi.INSTANCE.fontManager.wqy13.drawString(alt.getUsername(), 50.0f + 25, y - this.offset + 10, getColor(110));

                //   Hanabi.INSTANCE.fontManager.wqy13.drawString(pass, 50.0f + 25, y - this.offset + 18,
                //           pass.equals("Cracked") ? new Color(255, 107, 107).getRGB() : getColor(110));
                y += 35;
            }
        }
        GL11.glDisable(3089);
        GL11.glPopMatrix();

    }

    @Override
    public void initGui() {
        // HANABI_VERIFY
        //this.buttonList.add(this.login = new GuiButton(1, this.width / 2 - 122, this.height - 48, 100, 20, "Login"));
        //this.buttonList.add(this.remove = new GuiButton(2, this.width / 2 - 16, this.height - 24, 100, 20, "Remove"));
        // this.buttonList.add(new CustomGuiButton(3, this.width / 2 + 4 + 40, this.height - 48, 100, 20, "Add"));
        this.buttonList.add(new CustomGuiButton(4, this.width / 2 - 122, this.height - 48, 100, 20, "Direct Login"));
        this.buttonList.add(new CustomGuiButton(5, this.width / 2 - 122, this.height - 24, 100, 20, "Random"));
        //this.buttonList.add(this.rename = new GuiButton(6, this.width / 2 + 90, this.height - 24, 100, 20, "Edit"));
        this.buttonList.add(new CustomGuiButton(7, this.width / 2 - 200, this.height - 24, 70, 20, "Back"));
        this.buttonList.add(new CustomGuiButton(8, this.width / 2 - 200, this.height - 48, 70, 20, "Reload"));
        this.buttonList.add(new CustomGuiButton(9, this.width / 2 + 122, this.height - 48, 70, 20, "MultPlayer"));
        this.buttonList.add(new CustomGuiButton(10, this.width / 2 - 388, this.height - 48, 68, 20, "Altening"));
        this.buttonList.add(new CustomGuiButton(11, this.width / 2 - 388, this.height - 24, 68, 20, "Mojang"));
        //  this.buttonList.add(new CustomGuiButton(12, this.width / 2 + 16, this.height - 48, 100, 20, "Buy Account"));

        this.buttonList.add(new CustomGuiButton(13, this.width / 2 + 122, this.height - 24, 70, 20, "Hyp Bypass :" + Hanabi.INSTANCE.hypixelBypass));
        this.buttonList.add(new CustomGuiButton(3, this.width / 2 + 16, this.height - 48, 100, 20, "MS Login: " + Hanabi.INSTANCE.mslogin));
        this.buttonList.add(new CustomGuiButton(14, this.width / 2 + 16, this.height - 24, 100, 20, "Ban Checker"));

        seatchField = new CustomGuiTextField(99998, mc.fontRendererObj, "Search Alt", 65, this.height - 14 - 60, 200, 14);

        add_UserNameField = new CustomGuiTextField(99997, mc.fontRendererObj, "Username", (width / 2) + 20, 60, width-(width / 2+80), 14);
        add_PassWordField = new CustomGuiTextField(99996, mc.fontRendererObj, "Password", (width / 2) + 20, 80, width-(width / 2+80), 14);

        edit_UserNameField = new CustomGuiTextField(99995, mc.fontRendererObj, "Select an alt to edit", (width / 2) + 20, 145, width-(width / 2+80), 14);
        edit_PassWordField = new CustomGuiTextField(99994, mc.fontRendererObj, "", (width / 2) + 20, 165, width-(width / 2+80), 14);
    }

    @Override
    protected void keyTyped(final char par1, final int par2) {
        seatchField.textboxKeyTyped(par1, par2);

        add_UserNameField.textboxKeyTyped(par1, par2);
        add_PassWordField.textboxKeyTyped(par1, par2);

        edit_UserNameField.textboxKeyTyped(par1, par2);
        edit_PassWordField.textboxKeyTyped(par1, par2);

        if ((par1 == '\t' || par1 == '\r') && seatchField.isFocused()) {
            seatchField.setFocused(!seatchField.isFocused());
        }

        try {
            super.keyTyped(par1, par2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isAltInArea(final int y) {
        return y - this.offset <= this.height - 50;
    }

    private boolean isMouseOverAlt(final int x, final int y, final int y1) {
        return x >= 52 && y >= y1 - 4 && x <= this.width / 2f && y <= y1 + 30 && y >= 33 && x <= this.width && y <= this.height - 50;
    }

    public boolean isRectHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2;
    }

    @Override
    protected void mouseClicked(final int par1, final int par2, final int par3) {
        seatchField.mouseClicked(par1, par2, par3);

        add_UserNameField.mouseClicked(par1, par2, par3);
        add_PassWordField.mouseClicked(par1, par2, par3);

        edit_UserNameField.mouseClicked(par1, par2, par3);
        edit_PassWordField.mouseClicked(par1, par2, par3);


        if (isRectHovered((width / 2f) + 18, (float) (298), (width / 2f) + 43, 326, par1, par2)) {
            if (selectedAlt != null) {
                selectedAlt.setUsername(edit_UserNameField.getText());
                selectedAlt.setPassword(edit_PassWordField.getText());
            }
        }
        if (isRectHovered((width / 2f) + 18, 90, (width / 2f) + 42, 120, par1, par2)) {
            final AddAltThread login = new AddAltThread(add_UserNameField.getText(), add_PassWordField.getText());
            login.start();
            add_UserNameField.setText("");
            add_PassWordField.setText("");
        }
        if (isRectHovered((width / 2f) + 48, (float) (90), (width / 2f) + 78, 120, par1, par2)) {
            String data = null;
            try {
                data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            } catch (Exception ignored) {
                return;
            }
            if (data.contains(":")) {
                String[] credentials = data.split(":");
                add_UserNameField.setText(credentials[0]);
                add_PassWordField.setText(credentials[1]);
            }
        }
        if (this.offset < 0) {
            this.offset = 0;
        }
        int y = 80 - this.offset;
        if (isRectHovered(63.0f, 50, (int) (67.0f + 20), 73, par1, par2)) {
            (this.loginThread = new AltLoginThread(selectedAlt)).start();
            Hanabi.INSTANCE.altFileMgr.saveFiles();
        }
        if (isRectHovered(95, 50, (int) (95f + 20), 73, par1, par2)) {
            if (this.loginThread != null) {
                this.loginThread = null;
            }
            AltManager.registry.remove(this.selectedAlt);
            status = "\247aRemoved.";
            try {
                Hanabi.INSTANCE.altFileMgr.getFile(Alts.class).saveFile();
            } catch (Exception ignored) {
            }
            this.selectedAlt = null;
        }
        for (final Alt alt : getAlts()) {
            if (isMouseOverAlt(par1, par2, y)) {
                if (alt == this.selectedAlt) {
                    this.actionPerformed(this.buttonList.get(0));
                    return;
                }
                this.selectedAlt = alt;
            }
            y += 35;
        }
        try {
            super.mouseClicked(par1, par2, par3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isRectHovered((width / 2f) + 18, (float) (180), (width / 2f) + 43, 210, par1, par2)) {
            if (selectedAlt != null) {
                edit_UserNameField.setText(selectedAlt.getUsername());
                edit_PassWordField.setText(selectedAlt.getPassword());
            }
        }
    }

    public static void setStatus(String status) {
        GuiAltManager.status = status;
    }

    private List<Alt> getAlts() {
        List<Alt> altList = new ArrayList<>();
        for (final Alt alt : AltManager.registry) {
            if (seatchField.getText().isEmpty()
                    || (alt.getMask().toLowerCase().contains(seatchField.getText().toLowerCase())
                    || alt.getUsername().toLowerCase().contains(seatchField.getText().toLowerCase()))) {
                altList.add(alt);
            }
        }
        return altList;
    }

    public void prepareScissorBox(final float x, final float y, final float x2, final float y2) {
        final ScaledResolution scale = new ScaledResolution(this.mc);
        final int factor = scale.getScaleFactor();
        GL11.glScissor((int) (x * factor), (int) ((scale.getScaledHeight() - y2) * factor), (int) ((x2 - x) * factor),
                (int) ((y2 - y) * factor));
    }

}
