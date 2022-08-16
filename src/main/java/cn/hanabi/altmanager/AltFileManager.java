package cn.hanabi.altmanager;

import cn.hanabi.Hanabi;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class AltFileManager {
    private static final File directory = new File(Minecraft.getMinecraft().mcDataDir.toString() + "/" + Hanabi.CLIENT_NAME); // '\' dont working on linux
    public static ArrayList<CustomFile> Files = new ArrayList();

    public AltFileManager() {
        this.makeDirectories();
        Files.add(new Alts("alts", false, true));
    }

    public void loadFiles() {
        for (CustomFile f : Files) {
            try {
                if (!f.loadOnStart()) continue;
                f.loadFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFiles() {
        for (CustomFile f : Files) {
            try {
                f.saveFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CustomFile getFile(Class<? extends CustomFile> clazz) {
        for (CustomFile file : Files) {
            if (file.getClass() != clazz) continue;
            return file;
        }
        return null;
    }

    public void makeDirectories() {
        try {
            //TODO: VERSION CHECK
            if (!directory.exists()) {
                if (directory.mkdir()) {
                    Hanabi.INSTANCE.println("Directory is created!");
                } else {
                    Hanabi.INSTANCE.println("Failed to create directory!");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    public static abstract class CustomFile {
        private final File file;
        private final String name;
        private final boolean load;

        public CustomFile(String name, boolean Module2, boolean loadOnStart) {
            this.name = name;
            this.load = loadOnStart;
            this.file = new File(directory, name + ".txt");
            if (!this.file.exists()) {
                try {
                    this.saveFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public final File getFile() {
            return this.file;
        }

        private boolean loadOnStart() {
            return this.load;
        }

        public final String getName() {
            return this.name;
        }

        public abstract void loadFile() throws IOException;

        public abstract void saveFile() throws IOException;
    }

}

