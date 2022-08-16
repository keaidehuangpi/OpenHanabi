package cn.hanabi.injection.interfaces;

import net.minecraft.client.resources.LanguageManager;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;

public interface IMinecraft {

    Session getSession();

    void setSession(Session session);

    LanguageManager getLanguageManager();

    Timer getTimer();

    void setRightClickDelayTimer(int i);

    void setClickCounter(int a);

    void runCrinkMouse();

    void runRightMouse();

}
