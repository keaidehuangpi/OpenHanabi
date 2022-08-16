package cn.hanabi.utils.fileSystem;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Hanabi;
import cn.hanabi.altmanager.GuiAltManager;
import cn.hanabi.gui.notifications.Notification;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.ClientUtil;
import cn.hanabi.utils.FriendManager;
import cn.hanabi.utils.TargetManager;
import cn.hanabi.value.Value;
import me.yarukon.hud.window.HudWindow;
import me.yarukon.hud.window.HudWindowManager;
import me.yarukon.palette.ColorValue;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.io.*;

@ObfuscationClass
public class FileManager {

    public final String fileDir;

    public FileManager() {
        Minecraft mc = Minecraft.getMinecraft();
        this.fileDir = mc.mcDataDir.getAbsolutePath() + "/" + Hanabi.CLIENT_NAME;
        File fileFolder = new File(this.fileDir);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }

        File configDir = new File(fileDir + "/Config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
    }

    public void saveConfig(String configName) {
        if (configName.contains(".")) {
            ClientUtil.sendClientMessage("Config name shouldn't contain <.>.", Notification.Type.INFO);
            return;
        }
        File settingsFile = new File(fileDir + "/Config/" + configName + ".hanabi");
        try {
            if (!settingsFile.exists()) {
                settingsFile.createNewFile();
            }
            PrintWriter e = new PrintWriter(settingsFile);
            for (Value value : Value.list) {
                String valueName = "v:" + value.getValueName();
                if (value.isValueBoolean) {
                    e.println(valueName + ":b:" + value.getValueState());
                    continue;
                }
                if (value.isValueDouble) {
                    e.println(valueName + ":d:" + value.getValueState());
                    continue;
                }
                if (value.isValueMode) {
                    e.println(valueName + ":s:" + value.getModeTitle() + ":" + value.getCurrentMode());
                }
            }

            for (Mod m : ModManager.getModList()) {
                e.println("m:" + m.getName() + ":" + m.isEnabled());
            }

            for (Mod m : ModManager.getModList()) {
                String keyName = m.getKeybind() < 0 ? "None" : Keyboard.getKeyName(m.getKeybind());
                e.println("b:" + m.getName() + ":" + keyName);
            }

            e.close();
        } catch (Exception var6) {
            ClientUtil.sendClientMessage("Fail to save config <" + configName + ">.", Notification.Type.INFO);
            var6.printStackTrace();
        }
        ClientUtil.sendClientMessage("Successfully to save config <" + configName + ">.", Notification.Type.INFO);
    }

    public void loadConfig(String configName) {
        if (configName.contains(".")) {
            ClientUtil.sendClientMessage("Config name shouldn't contain <.>.", Notification.Type.INFO);
            return;
        }
        File settingsFile = new File(fileDir + "/Config/" + configName + ".hanabi");
        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(settingsFile));
            while ((line = br.readLine()) != null) {
                if (!line.contains(":"))
                    continue;
                String[] split = line.split(":");
                if (split[0].equalsIgnoreCase("v")) {
                    Mod m = ModManager.getModule(split[1].split("_")[0]);
                    if (m != null && m.getCategory() != Category.RENDER) {
                        for (Value value : Value.list) {
                            if (value.getValueName().equalsIgnoreCase(split[1])) {
                                if (value.isValueBoolean && split[2].equalsIgnoreCase("b")) {
                                    value.setValueState(Boolean.parseBoolean(split[3]));
                                } else if (value.isValueDouble && split[2].equalsIgnoreCase("d")) {
                                    value.setValueState(Double.parseDouble(split[3]));
                                } else if (value.isValueMode && split[2].equalsIgnoreCase("s") && split[3].equalsIgnoreCase(value.getModeTitle())) {
                                    value.setCurrentMode(Integer.parseInt(split[4]));
                                }
                            }
                        }
                    }
                } else if (split[0].equalsIgnoreCase("m")) {
                    Mod m = ModManager.getModule(split[1]);
                    boolean state = Boolean.parseBoolean(split[2]);
                    if (m == null || m.getCategory() == Category.RENDER) continue;
                    try {
                        if (m.isEnabled() != state) m.set(state, false);
                    } catch (Exception var8) {
                        var8.printStackTrace();
                    }
                } else if (split[0].equalsIgnoreCase("b")) {
                    Mod m = ModManager.getModule(split[1]);
                    int key = Keyboard.getKeyIndex(split[2]);
                    if (m == null || key == -1 || m.getCategory() == Category.RENDER) continue;
                    m.setKeybind(key);
                }
            }
        } catch (Exception var6) {
            ClientUtil.sendClientMessage("Fail to load config <" + configName + ">.", Notification.Type.INFO);
            var6.printStackTrace();
        }
        try {
            Hanabi.INSTANCE.fileManager.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ClientUtil.sendClientMessage("Successfully to load config <" + configName + ">.", Notification.Type.INFO);
    }

    public void save() throws Exception {
        File targetFile = new File(this.fileDir + "/target.txt");
        File friendFile = new File(this.fileDir + "/friend.txt");
        File keyFile = new File(this.fileDir + "/keys.txt");
        File moduleFile = new File(this.fileDir + "/mods.txt");
        File valueFile = new File(this.fileDir + "/values.txt");
        File gen = new File(this.fileDir + "/gen.txt");

        try {
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            PrintWriter targetPM = new PrintWriter(targetFile);
            if (TargetManager.getTarget() != null && TargetManager.getTarget().size() > 0) {
                for (int i = 0; i < TargetManager.getTarget().size(); i++) {
                    String name = (String) TargetManager.getTarget().get(i);
                    targetPM.write(name);
                }
            }
            targetPM.close();


            if (!friendFile.exists()) {
                friendFile.createNewFile();
            }
            PrintWriter friendPW = new PrintWriter(friendFile);
            if (FriendManager.getFriends() != null && FriendManager.getFriends().size() > 0) {
                for (int i = 0; i < FriendManager.getFriends().size(); i++) {
                    String name = (String) FriendManager.getFriends().get(i);
                    friendPW.write(name);
                }
            }
            friendPW.close();

            // Key
            if (!keyFile.exists()) {
                keyFile.createNewFile();
            }
            PrintWriter keyPw = new PrintWriter(keyFile);
            for (Mod m : ModManager.getModules()) {
                String keyName = m.getKeybind() < 0 ? "None" : Keyboard.getKeyName(m.getKeybind());
                keyPw.write(m.getName() + ":" + keyName + "\n");
            }
            keyPw.close();

            // altgen
            if (!gen.exists()) {
                gen.createNewFile();
            }
            PrintWriter genPW = new PrintWriter(gen);
            if (GuiAltManager.Api != null)
                genPW.write(GuiAltManager.Api);
            genPW.close();
            // HANABI_VERIFY
            // HANABI_VERIFY
            /*
             * try { if
             * (!Hanabi.AES_UTILS.decrypt(Hanabi.HWID_VERIFY).contains(Wrapper.getHWID())) {
             * FMLCommonHandler.instance().exitJava(0, true); Client.sleep = true; } } catch
             * (Exception e) { FMLCommonHandler.instance().exitJava(0, true); Client.sleep =
             * true; }
             *
             */
            // Module
            if (!moduleFile.exists()) {
                moduleFile.createNewFile();
            }
            PrintWriter modulePw = new PrintWriter(moduleFile);
            for (Mod m : ModManager.getModules()) {
                modulePw.print(m.getName() + ":" + m.isEnabled() + "\n");
            }
            modulePw.close();

            // Value
            if (!valueFile.exists()) {
                valueFile.createNewFile();
            }
            PrintWriter valuePw = new PrintWriter(valueFile);
            for (Value value : Value.list) {
                String valueName = value.getValueName();
                if (value.isValueBoolean) {
                    valuePw.print(valueName + ":b:" + value.getValueState() + "\n");
                    continue;
                }
                if (value.isValueDouble) {
                    valuePw.print(valueName + ":d:" + value.getValueState() + "\n");
                    continue;
                }
                if (!value.isValueMode)
                    continue;
                valuePw.print(valueName + ":s:" + value.getModeTitle() + ":" + value.getCurrentMode() + "\n");
            }
            valuePw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File targetFile = new File(this.fileDir + "/target.txt");
        File friendFile = new File(this.fileDir + "/friend.txt");
        File keyFile = new File(this.fileDir + "/keys.txt");
        File moduleFile = new File(this.fileDir + "/mods.txt");
        File valueFile = new File(this.fileDir + "/values.txt");
        File gen = new File(this.fileDir + "/gen.txt");
        File window = new File(this.fileDir + "/windows.txt");
        File colors = new File(this.fileDir + "/colors.txt");

        try {
            if(!window.exists()) {
                window.createNewFile();
            } else {
                this.loadWindows();
            }

            if(!colors.exists()) {
                colors.createNewFile();
            } else {
                this.loadColors();
            }

            if (!targetFile.exists()) {
                targetFile.createNewFile();
            } else {
                BufferedReader br = new BufferedReader(new FileReader(targetFile));
                if (br.readLine() != null)
                    TargetManager.getTarget().add(br.readLine());
            }

            if (!friendFile.exists()) {
                friendFile.createNewFile();
            } else {
                BufferedReader br = new BufferedReader(new FileReader(friendFile));
                if (br.readLine() != null)
                    FriendManager.getFriends().add(br.readLine());
            }

            // Key
            if (!keyFile.exists()) {
                keyFile.createNewFile();
            } else {
                String line;
                BufferedReader br = new BufferedReader(new FileReader(keyFile));
                while ((line = br.readLine()) != null) {
                    if (!line.contains(":"))
                        continue;
                    String[] split = line.split(":");
                    Mod m = ModManager.getModule(split[0]);
                    int key = Keyboard.getKeyIndex(split[1]);
                    if (m == null || key == -1)
                        continue;
                    m.setKeybind(key);
                }
            }
            // altgen
            if (!gen.exists()) {
                gen.createNewFile();
            } else {
                String result;
                BufferedReader br = new BufferedReader(new FileReader(gen));
                result = br.readLine();
                if (GuiAltManager.Api == null) {
                    GuiAltManager.Api = result;
                }

            }

            // Module
            if (!moduleFile.exists()) {
                moduleFile.createNewFile();
            } else {
                String line;
                BufferedReader br = new BufferedReader(new FileReader(moduleFile));
                while ((line = br.readLine()) != null) {
                    if (!line.contains(":"))
                        continue;
                    String[] split = line.split(":");
                    Mod m = ModManager.getModule(split[0]);
                    boolean state = Boolean.parseBoolean(split[1]);
                    if (m == null)
                        continue;
                    try {
                        if (!m.getName().equals("Fly") && !m.getName().equals("Blink")
                                && !m.getName().equals("Scaffold")) {
                            m.setState(state, false);
                        } else {
                            m.setState(false, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // HANABI_VERIFY
            // HANABI_VERIFY
            /*
             * try { if
             * (!Hanabi.AES_UTILS.decrypt(Hanabi.HWID_VERIFY).contains(Wrapper.getHWID())) {
             * FMLCommonHandler.instance().exitJava(0, true); Client.sleep = true; } } catch
             * (Exception e) { FMLCommonHandler.instance().exitJava(0, true); Client.sleep =
             * true; }
             *
             */

            // Value
            if (!valueFile.exists()) {
                valueFile.createNewFile();
            } else {
                String line;
                BufferedReader br = new BufferedReader(new FileReader(valueFile));
                while ((line = br.readLine()) != null) {
                    if (!line.contains(":"))
                        continue;
                    String[] split = line.split(":");
                    for (Value value : Value.list) {
                        if (!split[0].equalsIgnoreCase(value.getValueName()))
                            continue;
                        if (value.isValueBoolean && split[1].equalsIgnoreCase("b")) {
                            value.setValueState(Boolean.parseBoolean(split[2]));
                            continue;
                        }
                        if (value.isValueDouble && split[1].equalsIgnoreCase("d")) {
                            value.setValueState(Double.parseDouble(split[2]));
                            continue;
                        }
                        if (!value.isValueMode || !split[1].equalsIgnoreCase("s")
                                || !split[2].equalsIgnoreCase(value.getModeTitle()))
                            continue;
                        value.setCurrentMode(Integer.parseInt(split[3]));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadWindows() {
        try {
            String line;

            final FileReader fileReader = new FileReader(this.fileDir + "/windows.txt");
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                String[] splited = line.split(":");
                if(splited.length >= 3) {
                    HudWindow w = HudWindowManager.getWindowByID(splited[0]);
                    if(w != null) {
                        w.x = Float.parseFloat(splited[1]);
                        w.y = Float.parseFloat(splited[2]);

                        if(w.resizeable && splited.length == 5) {
                            w.width = Float.parseFloat(splited[3]);
                            w.height = Float.parseFloat(splited[4]);
                        }
                    }
                }
            }
            bufferedReader.close();
        } catch (Exception ignored) {

        }
    }

    public void saveWindows() {
        try {
            final FileWriter fileWriter = new FileWriter(this.fileDir + "/windows.txt");
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (HudWindow w : HudWindowManager.windows) {
                bufferedWriter.write(w.windowID + ":" + Math.floor(w.x) + ":" + Math.floor(w.y) + (w.resizeable ? ":" + Math.floor(w.width) + ":" + Math.floor(w.height) : ""));
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (Exception ignored) {
        }
    }

    public void loadColors() {
        try {
            String line;

            final FileReader fileReader = new FileReader(this.fileDir + "/colors.txt");
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                String[] splited = line.split(":");
                if(splited.length == 8) {
                    ColorValue cv = ColorValue.getColorValueByName(splited[0]);
                    if(cv != null) {
                        cv.hue = Float.parseFloat(splited[1]);
                        cv.saturation = Float.parseFloat(splited[2]);
                        cv.brightness = Float.parseFloat(splited[3]);
                        cv.alpha = Float.parseFloat(splited[4]);
                        cv.hasAlpha = Boolean.parseBoolean(splited[5]);
                        cv.rainbow = Boolean.parseBoolean(splited[6]);
                        cv.rainbowSpeed = Float.parseFloat(splited[7]);
                    }
                }
            }
            bufferedReader.close();
        } catch (Exception ignored) {

        }
    }

    public void saveColors() {
        try {
            final FileWriter fileWriter = new FileWriter(this.fileDir + "/colors.txt");
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (ColorValue v : ColorValue.colorValues) {
                bufferedWriter.write(v.name + ":" + v.hue + ":" + v.saturation + ":" + v.brightness + ":" + v.alpha + ":" + v.hasAlpha + ":" + v.rainbow + ":" + v.rainbowSpeed);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (Exception ignored) {
        }
    }
}