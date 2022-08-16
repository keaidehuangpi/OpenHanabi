/*
 * Decompiled with CFR 0_132.
 */
package cn.hanabi.gui.font.noway.ttfr;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;

public class FontLoaders {

    public HFontRenderer comfortaa10;
    public HFontRenderer comfortaa11;
    public HFontRenderer comfortaa12;
    public HFontRenderer comfortaa13;
    public HFontRenderer comfortaa15;
    public HFontRenderer comfortaa16;
    public HFontRenderer comfortaa17;
    public HFontRenderer comfortaa18;
    public HFontRenderer comfortaa20;
    public HFontRenderer comfortaa22;
    public HFontRenderer comfortaa25;
    public HFontRenderer comfortaa28;
    public HFontRenderer comfortaa30;
    public HFontRenderer comfortaa35;
    public HFontRenderer comfortaa40;
    public HFontRenderer comfortaa45;
    public HFontRenderer comfortaa50;
    public HFontRenderer comfortaa70;
    public HFontRenderer comfortaa150;

    public HFontRenderer raleway10;
    public HFontRenderer raleway11;
    public HFontRenderer raleway12;
    public HFontRenderer raleway13;
    public HFontRenderer raleway15;
    public HFontRenderer raleway16;
    public HFontRenderer raleway17;
    public HFontRenderer raleway18;
    public HFontRenderer raleway20;
    public HFontRenderer raleway25;
    public HFontRenderer raleway30;
    public HFontRenderer raleway35;
    public HFontRenderer raleway40;
    public HFontRenderer raleway45;
    public HFontRenderer raleway50;
    public HFontRenderer raleway70;

    public HFontRenderer usans10;
    public HFontRenderer usans11;
    public HFontRenderer usans12;
    public HFontRenderer usans13;
    public HFontRenderer usans14;
    public HFontRenderer usans15;
    public HFontRenderer usans16;
    public HFontRenderer usans17;
    public HFontRenderer usans18;
    public HFontRenderer usans19;
    public HFontRenderer usans20;
    public HFontRenderer usans21;
    public HFontRenderer usans22;
    public HFontRenderer usans23;
    public HFontRenderer usans24;
    public HFontRenderer usans25;
    public HFontRenderer usans28;
    public HFontRenderer usans30;
    public HFontRenderer usans35;
    public HFontRenderer usans40;
    public HFontRenderer usans45;
    public HFontRenderer usans50;
    public HFontRenderer usans70;
    public HFontRenderer usans150;

    public HFontRenderer icon10;
    public HFontRenderer icon11;
    public HFontRenderer icon12;
    public HFontRenderer icon13;
    public HFontRenderer icon14;
    public HFontRenderer icon15;
    public HFontRenderer icon16;
    public HFontRenderer icon17;
    public HFontRenderer icon18;
    public HFontRenderer icon19;
    public HFontRenderer icon20;
    public HFontRenderer icon21;
    public HFontRenderer icon22;
    public HFontRenderer icon23;
    public HFontRenderer icon24;
    public HFontRenderer icon25;
    public HFontRenderer icon30;
    public HFontRenderer icon35;
    public HFontRenderer icon40;
    public HFontRenderer icon45;
    public HFontRenderer icon50;
    public HFontRenderer icon70;
    public HFontRenderer icon100;
    public HFontRenderer icon130;


    public HFontRenderer sessionInfoIcon10;
    public HFontRenderer sessionInfoIcon11;
    public HFontRenderer sessionInfoIcon12;
    public HFontRenderer sessionInfoIcon13;
    public HFontRenderer sessionInfoIcon14;
    public HFontRenderer sessionInfoIcon15;
    public HFontRenderer sessionInfoIcon16;
    public HFontRenderer sessionInfoIcon17;
    public HFontRenderer sessionInfoIcon18;
    public HFontRenderer sessionInfoIcon19;
    public HFontRenderer sessionInfoIcon20;
    public HFontRenderer sessionInfoIcon21;
    public HFontRenderer sessionInfoIcon22;
    public HFontRenderer sessionInfoIcon23;
    public HFontRenderer sessionInfoIcon24;
    public HFontRenderer sessionInfoIcon25;
    public HFontRenderer sessionInfoIcon30;
    public HFontRenderer sessionInfoIcon35;

    public HFontRenderer altManagerIcon10;
    public HFontRenderer altManagerIcon11;
    public HFontRenderer altManagerIcon12;
    public HFontRenderer altManagerIcon13;
    public HFontRenderer altManagerIcon14;
    public HFontRenderer altManagerIcon15;
    public HFontRenderer altManagerIcon16;
    public HFontRenderer altManagerIcon17;
    public HFontRenderer altManagerIcon18;
    public HFontRenderer altManagerIcon19;
    public HFontRenderer altManagerIcon20;
    public HFontRenderer altManagerIcon21;
    public HFontRenderer altManagerIcon22;
    public HFontRenderer altManagerIcon23;
    public HFontRenderer altManagerIcon24;
    public HFontRenderer altManagerIcon25;
    public HFontRenderer altManagerIcon30;
    public HFontRenderer altManagerIcon35;
    public HFontRenderer altManagerIcon40;
    public HFontRenderer altManagerIcon45;
    public HFontRenderer altManagerIcon50;
    public HFontRenderer altManagerIcon70;


    public HFontRenderer tahoma_15;
    public HFontRenderer tahoma_16;
    public HFontRenderer tahoma_17;
    public HFontRenderer tahoma_18;
    public HFontRenderer tahoma_19;
    public HFontRenderer tahoma_20;

    public HFontRenderer tahomabd_12;
    public HFontRenderer tahomabd_13;
    public HFontRenderer tahomabd_14;
    public HFontRenderer tahomabd_15;

    public HFontRenderer tahomabd_16;
    public HFontRenderer tahomabd_17;
    public HFontRenderer tahomabd_18;
    public HFontRenderer tahomabd_19;
    public HFontRenderer tahomabd_20;

    public HFontRenderer micon15;
    public HFontRenderer micon30;

    public HFontRenderer wqy13;
    public HFontRenderer wqy16;
    public HFontRenderer wqy18;

    public FontLoaders() {
        comfortaa10 = getFont("comfortaa", 10);
        comfortaa11 = getFont("comfortaa", 11);
        comfortaa12 = getFont("comfortaa", 12);
        comfortaa13 = getFont("comfortaa", 13);
        comfortaa15 = getFont("comfortaa", 15);
        comfortaa16 = getFont("comfortaa", 16);
        comfortaa17 = getFont("comfortaa", 17);
        comfortaa18 = getFont("comfortaa", 18);
        comfortaa20 = getFont("comfortaa", 20);
        comfortaa25 = getFont("comfortaa", 25);
        comfortaa30 = getFont("comfortaa", 30);
        comfortaa35 = getFont("comfortaa", 35);
        comfortaa40 = getFont("comfortaa", 40);
        comfortaa45 = getFont("comfortaa", 45);
        comfortaa50 = getFont("comfortaa", 50);
        comfortaa70 = getFont("comfortaa", 70);
        comfortaa150 = getFont("comfortaa", 140);

        raleway10 = getFont("raleway", 10);
        raleway11 = getFont("raleway", 11);
        raleway12 = getFont("raleway", 12);
        raleway13 = getFont("raleway", 13);
        raleway15 = getFont("raleway", 15);
        raleway16 = getFont("raleway", 16);
        raleway17 = getFont("raleway", 17);
        raleway18 = getFont("raleway", 18);
        raleway20 = getFont("raleway", 20);
        raleway25 = getFont("raleway", 25);
        raleway30 = getFont("raleway", 30);
        raleway35 = getFont("raleway", 35);
        raleway40 = getFont("raleway", 40);
        raleway45 = getFont("raleway", 45);
        raleway50 = getFont("raleway", 50);
        raleway70 = getFont("raleway", 70);

        usans10 = getFont("usans", 10, "otf");
        usans11 = getFont("usans", 11, "otf");
        usans12 = getFont("usans", 12, "otf");
        usans13 = getFont("usans", 13, "otf");
        usans14 = getFont("usans", 14, "otf");
        usans15 = getFont("usans", 15, "otf");
        usans16 = getFont("usans", 16, "otf");
        usans17 = getFont("usans", 17, "otf");
        usans18 = getFont("usans", 18, "otf");
        usans19 = getFont("usans", 19, "otf");
        usans20 = getFont("usans", 20, "otf");
        usans21 = getFont("usans", 21, "otf");
        usans22 = getFont("usans", 22, "otf");
        usans23 = getFont("usans", 23, "otf");
        usans24 = getFont("usans", 24, "otf");
        usans25 = getFont("usans", 25, "otf");
        usans28 = getFont("usans", 28, "otf");
        usans30 = getFont("usans", 30, "otf");
        usans35 = getFont("usans", 35, "otf");
        usans40 = getFont("usans", 40, "otf");
        usans45 = getFont("usans", 45, "otf");
        usans50 = getFont("usans", 50, "otf");
        usans70 = getFont("usans", 70, "otf");
        usans150 = getFont("usans", 150, "otf");

        icon10 = getFont("icon", 10);
        icon11 = getFont("icon", 11);
        icon12 = getFont("icon", 12);
        icon13 = getFont("icon", 13);
        icon14 = getFont("icon", 14);
        icon15 = getFont("icon", 15);
        icon16 = getFont("icon", 16);
        icon17 = getFont("icon", 17);
        icon18 = getFont("icon", 18);
        icon19 = getFont("icon", 19);
        icon20 = getFont("icon", 20);
        icon21 = getFont("icon", 21);
        icon22 = getFont("icon", 22);
        icon23 = getFont("icon", 23);
        icon24 = getFont("icon", 24);
        icon25 = getFont("icon", 25);
        icon30 = getFont("icon", 30);
        icon35 = getFont("icon", 35);
        icon40 = getFont("icon", 40);
        icon45 = getFont("icon", 45);
        icon50 = getFont("icon", 50);
        icon70 = getFont("icon", 70);
        icon100 = getFont("icon", 100);
        icon130 = getFont("icon", 130);

        sessionInfoIcon10 = getFont("SessIcon", 10);
        sessionInfoIcon11 = getFont("SessIcon", 11);
        sessionInfoIcon12 = getFont("SessIcon", 12);
        sessionInfoIcon13 = getFont("SessIcon", 13);
        sessionInfoIcon14 = getFont("SessIcon", 14);
        sessionInfoIcon15 = getFont("SessIcon", 15);
        sessionInfoIcon16 = getFont("SessIcon", 16);
        sessionInfoIcon17 = getFont("SessIcon", 17);
        sessionInfoIcon18 = getFont("SessIcon", 18);
        sessionInfoIcon19 = getFont("SessIcon", 19);
        sessionInfoIcon20 = getFont("SessIcon", 20);
        sessionInfoIcon21 = getFont("SessIcon", 21);
        sessionInfoIcon22 = getFont("SessIcon", 22);
        sessionInfoIcon23 = getFont("SessIcon", 23);
        sessionInfoIcon24 = getFont("SessIcon", 24);
        sessionInfoIcon25 = getFont("SessIcon", 25);
        sessionInfoIcon30 = getFont("SessIcon", 30);
        sessionInfoIcon35 = getFont("SessIcon", 35);

        altManagerIcon10 = getFont("altmanager", 10);
        altManagerIcon11 = getFont("altmanager", 11);
        altManagerIcon12 = getFont("altmanager", 12);
        altManagerIcon13 = getFont("altmanager", 13);
        altManagerIcon14 = getFont("altmanager", 14);
        altManagerIcon15 = getFont("altmanager", 15);
        altManagerIcon16 = getFont("altmanager", 16);
        altManagerIcon17 = getFont("altmanager", 17);
        altManagerIcon18 = getFont("altmanager", 18);
        altManagerIcon19 = getFont("altmanager", 19);
        altManagerIcon20 = getFont("altmanager", 20);
        altManagerIcon21 = getFont("altmanager", 21);
        altManagerIcon22 = getFont("altmanager", 22);
        altManagerIcon23 = getFont("altmanager", 23);
        altManagerIcon24 = getFont("altmanager", 24);
        altManagerIcon25 = getFont("altmanager", 25);
        altManagerIcon30 = getFont("altmanager", 30);
        altManagerIcon35 = getFont("altmanager", 35);
        altManagerIcon40 = getFont("altmanager", 40);
        altManagerIcon45 = getFont("altmanager", 45);
        altManagerIcon50 = getFont("altmanager", 50);
        altManagerIcon70 = getFont("altmanager", 70);

        micon15 = getFont("micon", 15);
        micon30 = getFont("micon", 30);

        wqy13 = getFont("wqy_microhei", 13);
        wqy16 = getFont("wqy_microhei", 16);
        wqy18 = getFont("wqy_microhei", 18);

        tahoma_15 = getFont("tahoma", 15);
        tahoma_16 = getFont("tahoma", 16);
        tahoma_17 = getFont("tahoma", 17);
        tahoma_18 = getFont("tahoma", 18);
        tahoma_19 = getFont("tahoma", 19);
        tahoma_20 = getFont("tahoma", 20);

        tahomabd_12 = getFont("tahomabd", 12);
        tahomabd_13 = getFont("tahomabd", 13);
        tahomabd_14 = getFont("tahomabd", 14);
        tahomabd_15 = getFont("tahomabd", 15);
        tahomabd_16 = getFont("tahomabd", 16);
        tahomabd_17 = getFont("tahomabd", 17);
        tahomabd_18 = getFont("tahomabd", 18);
        tahomabd_19 = getFont("tahomabd", 19);
        tahomabd_20 = getFont("tahomabd", 20);
    }

    private HFontRenderer getFont(String name, int size) {
        Font font;
        try {
            InputStream is = getClass()
                    .getResourceAsStream("/assets/minecraft/Client/fonts/" + name+".ttf");
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }

        return new HFontRenderer(font, size, true);
    }


    private HFontRenderer getFont(String name, int size,String suffix) {
        Font font;
        try {
            InputStream is = getClass()
                    .getResourceAsStream("/assets/minecraft/Client/fonts/" + name+"."+suffix);
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }

        return new HFontRenderer(font, size, true);
    }
}

