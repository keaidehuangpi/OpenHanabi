package cn.hanabi.gui.font;

import cn.hanabi.events.EventTick;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;

import java.util.ArrayList;

public class FontsGC {

    private static final int GC_TICKS = 200;
    private static int ticks = 0;
    public static final int REMOVE_TIME = 30000;

    private static final ArrayList<VertexFontRenderer> arr = new ArrayList<>();

    static {
        EventManager.register(FontsGC.class);
    }

    @EventTarget
    public void onTick(final EventTick event) {
        if (ticks++ > GC_TICKS) {
            ticks = 0;
            for (VertexFontRenderer fontRenderer : arr) {
                fontRenderer.gcTick();
            }
        }
    }

    public void add(final VertexFontRenderer fontRenderer) {
        if(arr.contains(fontRenderer)) {
            throw new IllegalArgumentException("FontRenderer already added!");
        }
        arr.add(fontRenderer);
    }

    public void remove(final VertexFontRenderer fontRenderer) {
        if(!arr.contains(fontRenderer)) {
            throw new IllegalArgumentException("FontRenderer not added!");
        }
        arr.remove(fontRenderer);
    }

    public static void removeAll() {
        arr.clear();
    }
}
