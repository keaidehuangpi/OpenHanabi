package cn.hanabi.utils;

public class RectUtil {
    public float x, y, x2, y2;
    public float xd, yd, x2d, y2d;
    public TranslateUtil tl = new TranslateUtil(0,0);
    public TranslateUtil br = new TranslateUtil(0,0);

    public RectUtil (float x, float y, float x2, float y2) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.xd = x;
        this.yd = y;
        this.x2d = x2;
        this.y2d = y2;
        tl.setXY(x, y);
        br.setXY(x2, y2);
    }

    public void interpolate(float x, float y, float x2, float y2, float s1, float s2) {
        this.xd = x;
        this.yd = y;
        this.x2d = x2;
        this.y2d = y2;
        if (tl.getX() < xd) {
            tl.interpolate(xd, tl.getY(), s1);
            br.interpolate(x2d, br.getY(), s2);
        }else {
            tl.interpolate(xd, tl.getY(), s2);
            br.interpolate(x2d, br.getY(), s1);
        }
        if(tl.getY() < yd) {
            tl.interpolate(tl.getX(), yd, s1);
            br.interpolate(br.getX(), y2d, s2);
        }else {
            tl.interpolate(tl.getX(), yd, s2);
            br.interpolate(br.getX(), y2d, s1);
        }
        this.x = tl.getX();
        this.y = tl.getY();
        this.x2 = br.getX();
        this.y2 = br.getY();
    }

    public void setX(float x) {
        tl.setX(x);
        this.x = tl.getX();
    }

    public void setY(float y) {
        tl.setY(y);
        this.y = tl.getY();
    }

    public void setX2(float x2) {
        br.setX(x2);
        this.x2 = br.getX();
    }

    public void setY2(float y2) {
        br.setY(y2);
        this.y2 = br.getY();
    }

    public float getX() {
        return this.x;
    }

    public float getX2() {
        return this.x2;
    }

    public float getY() {
        return this.y;
    }

    public float getY2() {
        return this.y2;
    }
}
