package cn.hanabi.utils.checks;

import cn.hanabi.utils.CrashUtils;
import cn.hanabi.utils.jprocess.main.JProcesses;
import cn.hanabi.utils.jprocess.main.model.ProcessInfo;

public class InvalidProcess {

    public static void run() {
        for (ProcessInfo pi : JProcesses.getProcessList()) {
            for (String str : java.util.Arrays.asList("fiddler",
                    "wireshark",
                    "sandboxie")) {
                if (pi.getName().toLowerCase().contains(str)) {
                    try {
                        Class.forName("javax.swing.JOptionPane").getDeclaredMethod("showMessageDialog", java.awt.Component.class, Object.class, String.class, int.class).invoke(Class.forName("javax.swing.JOptionPane"), null, "Debuggers open... really?" + "\n" + "That's kinda SUS bro", "Stop", 0);
                        CrashUtils.doCrash();
                    } catch (Exception e) {
                        CrashUtils.doCrash();
                    }
                    try {
                        JProcesses.killProcess((int) Class.forName("com.sun.jna.platform.win32.Kernel32").getDeclaredField("INSTANCE").get(Class.forName("com.sun.jna.platform.win32.Kernel32")).getClass().getDeclaredMethod("GetCurrentProcessId").invoke(Class.forName("com.sun.jna.platform.win32.Kernel32").getDeclaredField("INSTANCE").get(Class.forName("com.sun.jna.platform.win32.Kernel32"))));
                    } catch (Exception e) {
                        CrashUtils.doCrash();
                    }
                    break;
                }
            }
        }
    }
}
