package cn.hanabi.modules.modules.render;

import cn.hanabi.Client;
import cn.hanabi.Hanabi;
import cn.hanabi.events.EventPacket;
import cn.hanabi.events.EventRender2D;
import cn.hanabi.events.EventText;
import cn.hanabi.events.EventWorldChange;
import cn.hanabi.gui.cloudmusic.ui.MusicOverlayRenderer;
import cn.hanabi.gui.font.noway.ttfr.FontLoaders;
import cn.hanabi.gui.font.noway.ttfr.HFontRenderer;
import cn.hanabi.gui.tabgui.SubTab;
import cn.hanabi.gui.tabgui.Tab;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.world.Scaffold;
import cn.hanabi.utils.*;
import cn.hanabi.utils.animation.SmoothAnimation;
import cn.hanabi.utils.fontmanager.HanabiFonts;
import cn.hanabi.gui.font.compat.WrappedVertexFontRenderer;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;
import me.yarukon.YRenderUtil;
import me.yarukon.font.GlyphPageFontRenderer;
import me.yarukon.palette.ColorValue;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class HUD extends Mod {
    private static final ArrayList<Long> times = new ArrayList<>();
    public static Value<Double> musicPosX = new Value<>("HUD", "MusicPlayerX",
            70d, 0d, 400d, 1d);
    public static Value<Double> musicPosY = new
            Value<>("HUD", "MusicPlayerY", 5d, 0d, 200d, 1d);
    public static
    Value<Double> musicPosYlyr = new Value<>("HUD", "MusicPlayerLyricY", 120d,
            0d, 200d, 1d);
    public static Value hudMode = new Value("HUD", "HudMode", 0);
    private final HashMap<Integer, Integer> potionMaxDurations = new HashMap<>();
    private final WaitTimer tpsTimer = new WaitTimer();
    public Value<Boolean> arraylist = new Value<>("HUD", "ArrayList", true);
    public Value<Boolean> arraylistfade = new Value<>("HUD", "Fade", false);
    //  public Value<Boolean> arraylistoutline = new Value<>("HUD", "Outline", false);
    public Value<Boolean> logo = new Value<>("HUD", "Logo", true);
    public Value<Boolean> hotbar = new Value<>("HUD", "Hotbar", true);
    public Value<Boolean> music = new Value<>("HUD", "MusicPlayer", true);
    public Value<Boolean> potion = new Value<>("HUD", "Potion", true);
    public Value<Boolean> armor = new Value<>("HUD", "Armor", true);
    public Value<Boolean> compass = new Value<>("HUD", "Compass", true);
    public Value<Boolean> noti = new Value<>("HUD", "Notification", true);
    public Value<Boolean> posDisplay = new Value<>("HUD", "Postion", true);
    public Value<Boolean> fixname = new Value<>("HUD", "Obfuscation Name Fix", true);
    public Value<String> sound = new Value<>("HUD", "Sound", 0);
    public Value<String> hitsound = new Value<>("HUD", "Hit Sound", 0);

    public static Value<String> array = new Value<String>("HUD", "Array List Color", 0)
            .LoadValue(new String[]{"Random", "Theme", "Rainbow"});

    public static ColorValue design = new ColorValue("Design Color", 0.5f, 1f, 1f, 1f, false, false, 10f);

    public static Value<Double> rainbowspeed = new
            Value<>("HUD", "ArrayList Speed", 3d, 1d, 6d, 1d);
    public static Value<Double> offset = new
            Value<>("HUD", "RainBow Offset", 2d, 1d, 6d, 1d);



    public static Value<Double> fade = new
            Value<>("HUD", "Fade Offset", 14d, 1d, 20d, 1d);


    public float lastTPS = 20.0f;
    public float alphaAnimation = 0;
    public float yAxisAnimation = 0;
    public int blockCount = 0;
    SimpleDateFormat f = new SimpleDateFormat("HH:mm");
    SimpleDateFormat f2 = new SimpleDateFormat("yyyy/MM/dd");
    public CompassUtil compasslol;
    Map<Potion, Double> timerMap = new HashMap<>();
    boolean isLag;
    private SmoothAnimation hotbarAnimation = new SmoothAnimation(0, 200);

    public HUD() {
        super("HUD", Category.RENDER, true, false, Keyboard.KEY_NONE);
        sound.LoadValue(new String[]{"Custom2", "Minecraft", "Custom1"});
        hitsound.LoadValue(new String[]{"Minecraft", "Ding", "Crack"});
        hudMode.LoadValue(new String[]{"Classic", "Simple"});
        compasslol = new CompassUtil(325, 325, 1, 2, true);
        setState(true);

        HashMap<Category, java.util.List<Mod>> moduleCategoryMap = new HashMap<>();

        for (Mod module : ModManager.getModules()) {
            if (!moduleCategoryMap.containsKey(module.getCategory())) {
                moduleCategoryMap.put(module.getCategory(), new ArrayList<>());
            }

            moduleCategoryMap.get(module.getCategory()).add(module);
        }

        moduleCategoryMap.entrySet().stream().sorted(Comparator.comparingInt(cat -> cat.getKey().toString().hashCode()))
                .forEach(cat -> {
                    Tab<Mod> tab = new Tab<>(cat.getKey().toString());

                    for (Mod module : cat.getValue()) {
                        tab.addSubTab(new SubTab<>(module.getName(),
                                subTab -> subTab.getObject().setState(!subTab.getObject().getState()), module));
                    }
                });

    }

    public static int rainbow(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 0.8f, 0.7f).getRGB();
    }

    @EventTarget
    public void onText(final EventText event) {
        if (mc.thePlayer == null)
            return;
        if (fixname.getValue())
            event.setText(StringUtils.replace(event.getText(), "\247k", ""));


    }

    @EventTarget
    public void onReload(EventWorldChange e) {
        /*HUD hud = (HUD) ModManager.getModule("HUD");

        new Thread(() -> {
            hud.set(false);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            hud.set(true);
        }).start();*/
    }

    public void color(int color) {
        float f = (float) (color >> 24 & 255) / 255.0f;
        float f1 = (float) (color >> 16 & 255) / 255.0f;
        float f2 = (float) (color >> 8 & 255) / 255.0f;
        float f3 = (float) (color & 255) / 255.0f;
        GL11.glColor4f(f1, f2, f3, f);
    }

    @EventTarget(Priority.LOWEST)
    private void render2D(EventRender2D event) {
        ScaledResolution sr = new ScaledResolution(mc);
        float width = sr.getScaledWidth();
        float height = sr.getScaledHeight();

        if (potion.getValueState()) {

            this.renderPotionStatus((int) width, (int) height);
        }

        if (compass.getValueState()) {
            renderCompass(sr);
        }

        if (armor.getValueState()) {
            for (int slot = 3, xOffset = 0; slot >= 0; slot--) {
                ItemStack stack = mc.thePlayer.inventory.armorItemInSlot(slot);

                if (stack != null) {
                    mc.getRenderItem().renderItemIntoGUI(stack, RenderUtil.width() / 2 + 15 - xOffset,
                            RenderUtil.height() - 55
                                    - (((mc.thePlayer.isInsideOfMaterial(Material.water) && mc.thePlayer.getAir() > 0)
                                    || mc.thePlayer.isRidingHorse()) ? 10 : 0));
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                    GL11.glScalef(2F, 2F, 2F);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    xOffset -= 18;
                }
            }
        }

        if (arraylist.getValueState()) {
            renderArray(sr);
        }

        if (noti.getValueState()) {
            if (mc.thePlayer.ticksExisted <= 10) {
                ClientUtil.clear();
            }
            ClientUtil.INSTANCE.drawNotifications();
        }

        if (music.getValueState())
            MusicOverlayRenderer.INSTANCE.renderOverlay();

        if (logo.getValueState()) {
            if (hudMode.isCurrentMode("Classic")) {
                Hanabi.INSTANCE.fontManager.icon130.drawStringWithShadow(HanabiFonts.ICON_HANABI_LOGO, 15, 60, array.isCurrentMode("Rainbow") ? new Color(47, 100, 253).getRGB() : design.getColor(), 120);

            } else {
                Hanabi.INSTANCE.fontManager.usans50.drawStringWithShadow("Hanabi", 10, 20, new Color(255, 255, 255, PaletteUtil.fade(new Color(Colors.WHITE.c), 1, 5).getRed()).getRGB(), 100);
                Hanabi.INSTANCE.fontManager.usans20.drawStringWithShadow("Build 3.1", 12, 35, new Color(255, 255, 255, PaletteUtil.fade(new Color(Colors.WHITE.c), 1, 5).getRed()).getRGB(), 100);
            }
        }

        if (posDisplay.getValueState()) {
            String pos = "";

            if (mc.thePlayer != null) {
                BlockPos blockPos = mc.thePlayer.getPosition();
                pos = "X:" + blockPos.getX() + "   Y:" + blockPos.getY() + "   Z:" + blockPos.getZ();

                Hanabi.INSTANCE.fontManager.wqy18.drawCenteredString(pos, sr.getScaledWidth() / 2f, 50, new Color(255, 255, 255, 180).getRGB());
            }
        }

        if (hotbar.getValueState() && mc.getRenderViewEntity() instanceof EntityPlayer && !mc.gameSettings.hideGUI) {
            HFontRenderer font = Hanabi.INSTANCE.fontManager.wqy18;

            RenderUtil.drawRect(width / 2 - 91, height - 22, (width / 2 + 90), height - 2,
                    new Color(17, 17, 17, 200).getRGB());

            RenderUtil.drawRect(sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() + 230, sr.getScaledWidth() / 2 + 100, sr.getScaledHeight() + 250, new Color(0, 0, 0).getRGB());

            long ping = (mc.getCurrentServerData() != null) ? mc.getCurrentServerData().pingToServer : -1;

            font.drawString("PING:" + ((ping <= 0) ? "N/A" : ping) + "ms     FPS:" + Minecraft.getDebugFPS(), 16f, height - 16f, -1);

            String ez = "Hanabi Build " + Hanabi.CLIENT_VERSION + " - " + Client.username;
            Hanabi.INSTANCE.fontManager.wqy18.drawString(ez, sr.getScaledWidth() - font.getStringWidth(ez) - 5,
                    sr.getScaledHeight() - 16, -1);

            hotbarAnimation.set(mc.thePlayer.inventory.currentItem * 20);
            float hbx = width / 2 - 91 + ((float) hotbarAnimation.get());
            RenderUtil.drawRect(hbx, height - 2, hbx + 21, height,
                    design.getColor());
//            if (mc.thePlayer.inventory.currentItem == 0) {
//                RenderUtil.drawRect(width / 2 - 91, height - 2, (width / 2 + 90) - 20 * 8, height,
//                        design.getColor());
//
//            } else {
//                RenderUtil.drawRect((width / 2) - 91 + mc.thePlayer.inventory.currentItem * 20, height - 2,
//                        (width / 2) + 90 - 20 * (8 - mc.thePlayer.inventory.currentItem), height,
//                        design.getColor());
//            }
//            Hanabi.INSTANCE.fontManager.wqy13.drawString("2716UGDHSADGTYD672",100,100,-1);


            RenderHelper.enableGUIStandardItemLighting();
            for (int j = 0; j < 9; ++j) {
                int k = (int) (width / 2 - 90 + j * 20 + 2);
                int l = (int) (height - 16 - 4);
                this.customRenderHotbarItem(j, k, l, event.partialTicks, mc.thePlayer);
                ItemStack itemstack = mc.thePlayer.inventory.mainInventory[j];
                if (itemstack == null)
                    Hanabi.INSTANCE.fontManager.tahomabd_15.drawString(String.valueOf(j + 1), k + 4, l, -1);
            }

            GlStateManager.disableBlend();
            GlStateManager.color(1, 1, 1);

            RenderHelper.disableStandardItemLighting();

            GL11.glColor4f(1, 1, 1, 1);
            renderBlockCount(sr.getScaledWidth(), sr.getScaledHeight() / 2f);

        }


        isLag = PacketHelper.getServerLagTime() != 0;

        if (isLag) {
            mc.fontRendererObj.drawString(Minecraft.getMinecraft().isSingleplayer() ? "\u00a74\u00a7lX" : "\u00a74\u00a7" + PacketHelper.getServerLagTime(), 16, 240, -1);
        }

    }

    public void renderCompass(ScaledResolution sr) {
        CompassUtil.draw(sr);
    }

    public void customRenderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer p_175184_5_) {

        GlStateManager.disableBlend();

        ItemStack itemstack = p_175184_5_.inventory.mainInventory[index];

        if (itemstack != null) {
            float f = (float) itemstack.animationsToGo - partialTicks;

            if (f > 0.0F) {
                GlStateManager.pushMatrix();
                float f1 = 1.0F + f / 5.0F;
                GlStateManager.translate((float) (xPos + 8), (float) (yPos + 12), 0.0F);
                GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float) (-(xPos + 8)), (float) (-(yPos + 12)), 0.0F);
            }

            mc.getRenderItem().renderItemAndEffectIntoGUI(itemstack, xPos, yPos);

            if (f > 0.0F) {
                GlStateManager.popMatrix();
            }

            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, itemstack, xPos - 1, yPos);
        }
    }

    private void renderArray(ScaledResolution sr) {
        ArrayList<Mod> mods = new ArrayList<>(ModManager.getEnabledModListHUD());
        float nextY = (/*arraylistoutline.getValueState() ? 4f : */ 3f) + (hudMode.isCurrentMode("Simple") ? 6f : 0f);
        int lastX = 0;
        HFontRenderer font = Hanabi.INSTANCE.fontManager.raleway17;
        HFontRenderer newfont = Hanabi.INSTANCE.fontManager.usans18;

        boolean simple = !hudMode.isCurrentMode("Classic");
        boolean first = true;
        int base;
        int color;

        for (Mod module : mods) {
            module.lastY = module.posY;
            module.posY = nextY;

            if (array.isCurrentMode("Random")) {
                base = module.getColor();
            } else if (array.isCurrentMode("Theme")) {
                base = design.getColor();
            } else {
                base = RenderUtil.getRainbow(6000, (int) (-15 * nextY), rainbowspeed.getValue(), offset.getValue(), design.saturation, design.brightness);
            }

            if (arraylistfade.getValueState()) {
                if (simple)
                    color = PaletteUtil
                            .fade(new Color(RenderUtil.reAlpha(Colors.WHITE.c, 0.75F)),
                                    (int) ((nextY + 11) / 11), fade.getValue().intValue())
                            .getRGB();
                else
                    color = PaletteUtil
                            .fade(new Color(base),
                                    (int) ((nextY + 11) / 11), fade.getValue().intValue())
                            .getRGB();
            } else {
                if (simple)
                    color = new Color(RenderUtil.reAlpha(Colors.WHITE.c, 0.75F)).getRGB();
                else {
                    color = base;
                }
            }
            module.onRenderArray();
            //不渲染渲染类
            if (module.getName().equals("TargetStrafe"))
                continue;
            if (module.getCategory() == Category.RENDER)
                continue;
            if (!module.isEnabled() && module.posX <= 0)
                continue;

            // Module 信息
            String modName = module.getName();
            String displayName = module.getDisplayName();
            float modwidth = module.posX;

            // 画背景
            if (!simple) {
                //FullRect
                int x = (int) (sr.getScaledWidth() - modwidth - 3f);
         /*       if (arraylistoutline.getValueState()) {
                    if (first) {
                        RenderUtil.drawRect(x - 4, 0, x + font.getStringWidth(modName + " " + displayName) + 7, 1, color);
                    }
                    final int i = (lastX - 3 - 1) - (lastX - 3 - 1 - (x - 3 - 1)) + 2;
                    boolean Del = (lastX - 3 - 1) > i;

                    if (!first) {
                        RenderUtil.drawRect(lastX - 3 - 1 + (Del ? 2 : 0), nextY + module.posYRend - 6 + (2), i - 1 + (Del ? -2 : 0), nextY + module.posYRend - 5 + (2), color);
                    }
                }

          */
            }


            // 画字体
            if (simple)
                newfont.drawStringWithShadow(modName, sr.getScaledWidth() - modwidth - 11, nextY + module.posYRend - 1.5f, color, 80);
            else
                font.drawStringWithShadow(modName, sr.getScaledWidth() - modwidth - 11, nextY + module.posYRend + 1, color, 100);

            first = false;
            lastX = (int) (sr.getScaledWidth() - modwidth - 3);
            if (displayName != null)
                if (simple)
                    newfont.drawStringWithShadow(displayName, sr.getScaledWidth() - modwidth + newfont.getStringWidth(modName) - 8,
                            nextY + module.posYRend - 1.5f, new Color(159, 159, 159).getRGB(), 80);
                else
                    font.drawStringWithShadow(displayName, sr.getScaledWidth() - 8 - modwidth + font.getStringWidth(modName),
                            nextY + module.posYRend + 1, new Color(159, 159, 159).getRGB(), 200);

            if (simple)
                nextY += 13;
            else
                nextY += font.FONT_HEIGHT;
        }
    }

    public void renderBlockCount(float width, float height) {
        boolean state = ModManager.getModule("Scaffold").isEnabled();
        alphaAnimation = RenderUtil.getAnimationState(alphaAnimation, state ? 0.7f : 0, 10f);
        yAxisAnimation = RenderUtil.getAnimationState(this.yAxisAnimation, state ? 0 : 10, (float) Math.max(10, (Math.abs(this.yAxisAnimation - (state ? 0 : 10)) * 50) * 0.5));

        float trueHeight = 18;

        //渲染用
        if (alphaAnimation > 0.2f) {
            try {
                blockCount = Scaffold.items.stackSize;
            } catch (Exception ignore) {
                blockCount = 0;
            }
            String cunt = "block" + (blockCount > 1 ? "s" : "");
            HFontRenderer font = Hanabi.INSTANCE.fontManager.usans20;
            HFontRenderer font2 = Hanabi.INSTANCE.fontManager.usans18;
            float length = font.getStringWidth(blockCount + "  ") + font2.getStringWidth(cunt) + 1f;
            YRenderUtil.drawRoundedRect(width / 2 - (length / 2), height + trueHeight - yAxisAnimation, length, 15, 2, RenderUtil.reAlpha(Colors.BLACK.c, alphaAnimation), 0.5f, RenderUtil.reAlpha(Colors.BLACK.c, alphaAnimation));
            this.drawArrowRect(width / 2 - 5, height + trueHeight - 5 - yAxisAnimation, width / 2 + 5, height + trueHeight - yAxisAnimation, RenderUtil.reAlpha(0xff000000, alphaAnimation));

            font.drawString(blockCount + "", width / 2 - (length / 2 - 2f), height + trueHeight + 3 - yAxisAnimation, RenderUtil.reAlpha(Colors.WHITE.c, MathUtils.clampValue(alphaAnimation + 0.25f, 0f, 1f)));
            font2.drawString(cunt, width / 2 - (length / 2 - 1f) + font.getStringWidth(blockCount + " "), height + (trueHeight + 4) - yAxisAnimation, RenderUtil.reAlpha(Colors.WHITE.c, MathUtils.clampValue(alphaAnimation - 0.1f, 0f, 1f)));
        }
    }

    public void drawArrowRect(float left, float top, float right, float bottom, int color) {
        float e;

        if (left < right) {
            e = left;
            left = right;
            right = e;
        }

        if (top < bottom) {
            e = top;
            top = bottom;
            bottom = e;
        }

        float a = (float) (color >> 24 & 255) / 255.0F;
        float b = (float) (color >> 16 & 255) / 255.0F;
        float c = (float) (color >> 8 & 255) / 255.0F;
        float d = (float) (color & 255) / 255.0F;
        Tessellator tes = Tessellator.getInstance();
        WorldRenderer bufferBuilder = Tessellator.getInstance().getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(b, c, d, a);
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(left - 5, bottom, 0.0D).endVertex();
        bufferBuilder.pos(right + 5, bottom, 0.0D).endVertex();
        bufferBuilder.pos(right, top, 0.0D).endVertex();
        bufferBuilder.pos(left, top, 0.0D).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }


    public void renderPotionStatus(int width, int height) {
        int x = 0;

        final ArrayList<Integer> needRemove = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : potionMaxDurations.entrySet()) {
            if (mc.thePlayer.getActivePotionEffect(Potion.potionTypes[entry.getKey()]) == null) {
                needRemove.add(entry.getKey());
            }
        }
        for (int id : needRemove) {
            potionMaxDurations.remove(id);
        }

        for (PotionEffect effect : mc.thePlayer.getActivePotionEffects()) {
            if (!potionMaxDurations.containsKey(effect.getPotionID()) || potionMaxDurations.get(effect.getPotionID()) < effect.getDuration()) {
                potionMaxDurations.put(effect.getPotionID(), effect.getDuration());
            }
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            String PType = I18n.format(potion.getName());
            int minutes;
            int seconds;

            try {
                minutes = Integer.parseInt(Potion.getDurationString(effect).split(":")[0]);
                seconds = Integer.parseInt(Potion.getDurationString(effect).split(":")[1]);
            } catch (Exception ex) {
                minutes = 0;
                seconds = 0;
            }

            double total = (minutes * 60) + seconds;

            if (!timerMap.containsKey(potion)) {
                timerMap.put(potion, total);
            }

            if (timerMap.get(potion) == 0 || total > timerMap.get(potion)) {
                timerMap.replace(potion, total);
            }

            switch (effect.getAmplifier()) {
                case 0:
                    PType = PType + " I";
                    break;
                case 1:
                    PType = PType + " II";
                    break;
                case 2:
                    PType = PType + " III";
                    break;
                case 3:
                    PType = PType + " IV";
                    break;
                default:
                    PType = PType + " " + (effect.getAmplifier() + 1);
                    break;
            }

            int x1 = (int) ((width - 6) * 1.33f);
            int y1 = (int) ((height - 52 - mc.fontRendererObj.FONT_HEIGHT + x + 5) * 1.33F);

            float rectX = width - 120 + (110 * (effect.getDuration() / (1f * potionMaxDurations.get(effect.getPotionID()))));


            RenderUtil.drawRect(width - 120, height - 60 + x, rectX, height - 30 + x, new Color(0, 0, 0, 100).getRGB());

            RenderUtil.drawRect(rectX, height - 60 + x, width - 10, height - 30 + x, new Color(50, 50, 50, 100).getRGB());

            if (potion.hasStatusIcon()) {
                GlStateManager.pushMatrix();

                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDepthMask(false);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glColor4f(1, 1, 1, 1);
                int index = potion.getStatusIconIndex();
                ResourceLocation location = new ResourceLocation("textures/gui/container/inventory.png");
                mc.getTextureManager().bindTexture(location);
                GlStateManager.scale(0.75, 0.75, 0.75);
                mc.ingameGUI.drawTexturedModalRect(x1 - 138, y1 + 8, index % 8 * 18, 198 + index / 8 * 18, 18, 18);

                GL11.glDepthMask(true);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GlStateManager.popMatrix();
            }

            int y = (height - mc.fontRendererObj.FONT_HEIGHT + x) - 38;
            HFontRenderer font = Hanabi.INSTANCE.fontManager.wqy18;
            font.drawString(PType.replaceAll("\247.", ""), (float) width - 91f,
                    y - mc.fontRendererObj.FONT_HEIGHT + 1, design.getColor());

            Hanabi.INSTANCE.fontManager.comfortaa16.drawString(Potion.getDurationString(effect).replaceAll("\247.", ""),
                    width - 91f, y + 4, ClientUtil.reAlpha(-1, 0.8f));

            x -= 35;

        }
    }

    @EventTarget
    public void onRPacket(EventPacket event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            times.add(Math.max(1000, tpsTimer.getTime()));
            long timesAdded = 0;
            if (times.size() > 5) {
                times.remove(0);
            }
            for (long l : times) {
                timesAdded += l;
            }
            long roundedTps = timesAdded / times.size();
            lastTPS = (float) ((20.0 / roundedTps) * 1000.0);
            tpsTimer.reset();
        }

    }

    public static class WaitTimer {
        public long time;

        public WaitTimer() {
            this.time = (System.nanoTime() / 1000000l);
        }

        public boolean hasTimeElapsed(long time, boolean reset) {
            if (getTime() >= time) {
                if (reset) {
                    reset();
                }
                return true;
            }
            return false;
        }

        public long getTime() {
            return System.nanoTime() / 1000000l - this.time;
        }

        public void reset() {
            this.time = (System.nanoTime() / 1000000l);
        }
    }
}
