package cn.hanabi.modules.modules.render;

import cn.hanabi.Hanabi;
import cn.hanabi.Wrapper;
import cn.hanabi.events.EventRender;
import cn.hanabi.events.EventRender2D;
import cn.hanabi.gui.font.compat.WrappedVertexFontRenderer;
import cn.hanabi.gui.font.noway.ttfr.HFontRenderer;
import cn.hanabi.injection.interfaces.IEntityRenderer;
import cn.hanabi.injection.interfaces.IRenderManager;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.Colors;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.rotation.RotationUtil;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import me.yarukon.font.GlyphPageFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;


public class Nametags extends Mod {

    public static Map<EntityLivingBase, double[]> entityPositions = new HashMap<>();

    public Value<Boolean> invis = new Value<>("Nametags", "Invisible", false);
    public Value<Boolean> armor = new Value<>("Nametags", "Armor", false);

    public Nametags() {
        super("Nametags", Category.RENDER);
    }

    @EventTarget
    public void update(EventRender event) {
        try {
            updatePositions();
        } catch (Exception ignored) {}
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        ScaledResolution scaledRes = new ScaledResolution(mc);

        try {
            for (EntityLivingBase ent : entityPositions.keySet()) {

                if (ent != mc.thePlayer && (this.invis.getValue() || !ent.isInvisible())) {
                    GlStateManager.pushMatrix();
                    if ((ent instanceof EntityPlayer)) {
                        double[] renderPositions = entityPositions.get(ent);
                        if ((renderPositions[3] < 0.0D) || (renderPositions[3] >= 1.0D)) {
                            GlStateManager.popMatrix();
                            continue;
                        }

                        HFontRenderer font = Hanabi.INSTANCE.fontManager.wqy16;

                        GlStateManager.translate(renderPositions[0] / scaledRes.getScaleFactor(), renderPositions[1] / scaledRes.getScaleFactor(), 0.0D);

                        GlStateManager.scale(1, 1, 1);
                        GlStateManager.translate(0.0D, -2.5D, 0.0D);

                        String str = ent.getName();


                        float allWidth = font.getStringWidth(str.replaceAll("\247.", "")) + 14;

                        RenderUtil.drawRect(-allWidth / 2, -14.0f, allWidth / 2, 0, Colors.getColor(0, 150));

                        font.drawString(str.replaceAll("\247.", ""), -allWidth / 2 + 5.5f, -13F, Colors.WHITE.c);

                        float nowhealth = (float) Math.ceil(ent.getHealth() + ent.getAbsorptionAmount());
                        float maxHealth = ent.getMaxHealth() + ent.getAbsorptionAmount();
                        float healthP = nowhealth / maxHealth;

                        int color = Colors.RED.c;
                        String text = ent.getDisplayName().getFormattedText();

                        //Megawalls
                        text = text.replaceAll((text.contains("[") && text.contains("]")) ? "\2477":  "", "");
                        for (int i = 0; i < text.length(); i++) {
                            if ((text.charAt(i) == '\247') && (i + 1 < text.length())) {
                                char oneMore = Character.toLowerCase(text.charAt(i + 1));
                                int colorCode = "0123456789abcdefklmnorg".indexOf(oneMore);
                                if (colorCode < 16) {
                                    try {
                                        color = RenderUtil.reAlpha(mc.fontRendererObj.getColorCode(oneMore), 1f);
                                    } catch (ArrayIndexOutOfBoundsException ignored) {}
                                }
                            }
                        }

                        RenderUtil.drawRect(-allWidth / 2, -2f, allWidth / 2 - ((allWidth / 2) * (1 - healthP)) * 2, 0, RenderUtil.reAlpha(color, 0.8f));

                        boolean armors = this.armor.getValue();

                        if (armors) {
                            List<ItemStack> itemsToRender = new ArrayList<>();

                            for (int i = 0; i < 5; i++) {
                                ItemStack stack = ent.getEquipmentInSlot(i);
                                if (stack != null) {
                                    itemsToRender.add(stack);
                                }
                            }

                            int x = -(itemsToRender.size() * 9) - 3;

                            for (ItemStack stack : itemsToRender) {

                                GlStateManager.pushMatrix();
                                RenderHelper.enableGUIStandardItemLighting();
                                GlStateManager.disableAlpha();
                                GlStateManager.clear(256);
                                mc.getRenderItem().zLevel = -150.0F;
                                this.fixGlintShit();
                                mc.getRenderItem().renderItemIntoGUI(stack, x + 6, -32);
                                mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x + 6, -32);
                                mc.getRenderItem().zLevel = 0.0F;
                                x += 6;
                                GlStateManager.enableAlpha();
                                RenderHelper.disableStandardItemLighting();
                                GlStateManager.popMatrix();

                                if (stack != null) {
                                    int y = 0;
                                    int sLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId,
                                            stack);
                                    int fLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId,
                                            stack);
                                    int kLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId,
                                            stack);
                                    if (sLevel > 0) {
                                        drawEnchantTag("Sh" + getColor(sLevel) + sLevel, x, y);
                                        y += Hanabi.INSTANCE.fontManager.wqy13.FONT_HEIGHT - 2;
                                    }
                                    if (fLevel > 0) {
                                        drawEnchantTag("Fir" + getColor(fLevel) + fLevel, x, y);
                                        y += Hanabi.INSTANCE.fontManager.wqy13.FONT_HEIGHT - 2;
                                    }
                                    if (kLevel > 0) {
                                        drawEnchantTag("Kb" + getColor(kLevel) + kLevel, x, y);
                                    } else if ((stack.getItem() instanceof ItemArmor)) {
                                        int pLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId,
                                                stack);
                                        int tLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId,
                                                stack);
                                        int uLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId,
                                                stack);
                                        if (pLevel > 0) {
                                            drawEnchantTag("P" + getColor(pLevel) + pLevel, x, y);
                                            y += Hanabi.INSTANCE.fontManager.wqy13.FONT_HEIGHT - 2;
                                        }
                                        if (tLevel > 0) {
                                            drawEnchantTag("Th" + getColor(tLevel) + tLevel, x, y);
                                            y += Hanabi.INSTANCE.fontManager.wqy13.FONT_HEIGHT - 2;
                                        }
                                        if (uLevel > 0) {
                                            drawEnchantTag("Unb" + getColor(uLevel) + uLevel, x, y);
                                        }
                                    } else if ((stack.getItem() instanceof ItemBow)) {
                                        int powLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
                                        int punLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
                                        int fireLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);

                                        if (powLevel > 0) {
                                            drawEnchantTag("Pow" + getColor(powLevel) + powLevel, x, y);
                                            y += Hanabi.INSTANCE.fontManager.wqy13.FONT_HEIGHT - 2;
                                        }

                                        if (punLevel > 0) {
                                            drawEnchantTag("Pun" + getColor(punLevel) + punLevel, x, y);
                                            y += Hanabi.INSTANCE.fontManager.wqy13.FONT_HEIGHT - 2;
                                        }

                                        if (fireLevel > 0) {
                                            drawEnchantTag("Fir" + getColor(fireLevel) + fireLevel, x, y);
                                        }
                                    } else if (stack.getRarity() == EnumRarity.EPIC) {
                                        drawEnchantTag("\2476\247lGod", x - 0.5f, y + 12);
                                    }
                                    x += 12;
                                }
                            }
                        }
                    }
                    GlStateManager.popMatrix();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fixGlintShit() {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    private String getColor(int level) {
        if (level == 2) {
            return "\247a";
        } else if (level == 3) {
            return "\2473";
        } else if (level == 4) {
            return "\2474";
        } else if (level >= 5) {
            return "\2476";
        }
        return "\247f";
    }

    private void drawEnchantTag(String text, float x, float y) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        x = (int) (x * 1.05D);
        y -= 6;
        Hanabi.INSTANCE.fontManager.wqy13.drawString(text, x, -44 - y, Colors.WHITE.c);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private void updatePositions() {
        entityPositions.clear();
        float pTicks = Wrapper.getTimer().renderPartialTicks;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity != mc.thePlayer && entity instanceof EntityPlayer && (!entity.isInvisible() || this.invis.getValue())) {
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pTicks - ((IRenderManager)mc.getRenderManager()).getRenderPosX();
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pTicks - ((IRenderManager)mc.getRenderManager()).getRenderPosY();
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pTicks - ((IRenderManager)mc.getRenderManager()).getRenderPosZ();
                y += entity.height + 0.25d;
                if ((Objects.requireNonNull(convertTo2D(x, y, z))[2] >= 0.0D) && (Objects.requireNonNull(convertTo2D(x, y, z))[2] < 1.0D)) {
                    entityPositions.put((EntityPlayer) entity, new double[]{Objects.requireNonNull(convertTo2D(x, y, z))[0], Objects.requireNonNull(convertTo2D(x, y, z))[1], Math.abs(convertTo2D(x, y + 1.0D, z, entity)[1] - convertTo2D(x, y, z, entity)[1]), Objects.requireNonNull(convertTo2D(x, y, z))[2]});
                }
            }
        }
    }

    private double[] convertTo2D(double x, double y, double z, Entity ent) {
        float pTicks = Wrapper.getTimer().renderPartialTicks;
        float prevYaw = mc.thePlayer.rotationYaw;
        float prevPrevYaw = mc.thePlayer.prevRotationYaw;
        float[] rotations = RotationUtil.getRotationFromPosition(
                ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks,
                ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks,
                ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - 1.6D);
        mc.getRenderViewEntity().rotationYaw = (mc.getRenderViewEntity().prevRotationYaw = rotations[0]);
        ((IEntityRenderer) Minecraft.getMinecraft().entityRenderer).runSetupCameraTransform(pTicks, 0);
        double[] convertedPoints = convertTo2D(x, y, z);
        mc.getRenderViewEntity().rotationYaw = prevYaw;
        mc.getRenderViewEntity().prevRotationYaw = prevPrevYaw;
        ((IEntityRenderer) Minecraft.getMinecraft().entityRenderer).runSetupCameraTransform(pTicks, 0);
        return convertedPoints;
    }

    private double[] convertTo2D(double x, double y, double z) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, screenCoords);
        if (result) {
            return new double[]{screenCoords.get(0), Display.getHeight() - screenCoords.get(1), screenCoords.get(2)};
        }
        return null;
    }
}
