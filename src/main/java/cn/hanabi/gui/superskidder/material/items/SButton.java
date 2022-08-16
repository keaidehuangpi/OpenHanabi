package cn.hanabi.gui.superskidder.material.items;

import cn.hanabi.gui.superskidder.material.items.other.Shadow;

import java.util.ArrayList;

public class SButton {
    public double x, y;
    public double width, height;
    public ArrayList<Shadow> ss = new ArrayList<>();
    public int color;

    public SButton(double x, double y, double width, double height, int color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void draw(double mouseX, double mouseY, int mouseButton) {

    }

    public void update() {
        for (int i = 0; i < ss.size(); i++) {
            Shadow sha = ss.get(i);
            sha.update();
            if (sha.delete)
                ss.remove(i);
            if(ss.size()>8){
                ss.remove(0);
            }
        }
    }

    public void onClicked(double mouseX, double mouseY, int mouseButton) {

    }
}
