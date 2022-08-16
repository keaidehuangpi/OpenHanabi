package cn.hanabi.utils.animation;

public class SmoothAnimation {

    private double value;
    private Animation animation = null;
    private int duration;

    public SmoothAnimation(double initValue, int duration) {
        value = initValue;
        this.duration = duration;
    }

    public double get() {
        if (animation != null) {
            value = animation.getNow();
            if (animation.getState() == Animation.EnumAnimationState.FINISHED) {
                animation = null;
            }
        }
        return value;
    }

    public void set(double valueIn) {
        if (animation == null || animation.getTo() != valueIn) {
            animation = new Animation(value, valueIn, duration);
            animation.start();
        }
    }
}
