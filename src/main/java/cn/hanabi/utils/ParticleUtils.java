package cn.hanabi.utils;

import cn.hanabi.gui.particles.ParticleGenerator;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public final class ParticleUtils {

    private static final ParticleGenerator particleGenerator = new ParticleGenerator(100);

    public static void drawParticles(int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        particleGenerator.draw(mouseX, mouseY);
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}