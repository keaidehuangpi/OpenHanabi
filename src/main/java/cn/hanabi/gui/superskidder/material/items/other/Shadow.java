package cn.hanabi.gui.superskidder.material.items.other;

import cn.hanabi.gui.superskidder.material.clickgui.AnimationUtils;
import cn.hanabi.utils.RenderUtil;

public class Shadow {
    public double alpha;
    public double size, finalSize;
    public double xPos, yPos;
    public boolean end, delete;
    AnimationUtils animate;
    AnimationUtils animate2;


    public Shadow(double x, double y, double finalSize) {
//        alpha=
        xPos = x;
        yPos = y;
        animate = new AnimationUtils();
        animate2 = new AnimationUtils();

        this.finalSize = finalSize;

    }

    public void update() {
        if (!end) {
            size = animate.animate(finalSize, size, 0.3f);
            alpha = animate2.animate(100, alpha, 0.2f);
        } else {
            alpha = animate2.animate(0, alpha, 0.4f);
            if (alpha == 0) {
                delete = true;
            }
        }
        if (size >= finalSize) {
            end = true;
        }
    }

}
