package cn.hanabi.utils.animation;

public class Animation {

    private long startTime = 0L;
    private EnumAnimationState state = EnumAnimationState.NOT_STARTED;

    private double from;
    private double to;
    private int duration;

    public Animation(double from, double to, int duration) {
        this.from = from;
        this.to = to;
        this.duration = duration;
    }

    public void start() {
//        if(state == EnumAnimationState.DURING) {
//            throw new IllegalStateException("Animation is already started");
//        }
        state = EnumAnimationState.DURING;
        startTime = System.currentTimeMillis();
    }

    public double getNow() {
        if (state == EnumAnimationState.NOT_STARTED) {
            return from;
        } else if (state == EnumAnimationState.DURING) {
            double percent = (System.currentTimeMillis() - startTime) / (double) duration;
            if (percent >= 1.0) {
                state = EnumAnimationState.FINISHED;
                return to;
            }
            return from + (to - from) * EaseUtils.easeOutQuad(percent);
        } else if (state == EnumAnimationState.FINISHED) {
            return to;
        }
        throw new IllegalStateException("Unknown animation state");
    }

    public EnumAnimationState getState() {
        return state;
    }

    public double getFrom() {
        return from;
    }

    public double getTo() {
        return to;
    }

    public enum EnumAnimationState {
        NOT_STARTED,
        DURING,
        FINISHED
    }
}
