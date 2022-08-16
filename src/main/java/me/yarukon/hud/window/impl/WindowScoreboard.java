package me.yarukon.hud.window.impl;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Hanabi;
import cn.hanabi.modules.modules.render.HUD;
import cn.hanabi.utils.Colors;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import me.yarukon.YRenderUtil;
import me.yarukon.hud.window.HudWindow;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@ObfuscationClass

public class WindowScoreboard extends HudWindow {
    public WindowScoreboard() {
        super("Scoreboard", 5, 200, 200, 300, "Scoreboard", "", 12, 0, 1f);
    }

    @Override
    public void draw() {
        Hanabi.INSTANCE.customScoreboard = !hide;

        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        ScoreObjective scoreObjective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.mc.thePlayer.getName());

        if (scoreplayerteam != null) {
            int i1 = scoreplayerteam.getChatFormat().getColorIndex();

            if (i1 >= 0) {
                scoreObjective = scoreboard.getObjectiveInDisplaySlot(3 + i1);
            }
        }

        ScoreObjective scoreObjective1 = scoreObjective != null ? scoreObjective : scoreboard.getObjectiveInDisplaySlot(1);

        if (scoreObjective1 != null) {
            super.draw();
            Collection<Score> collection = scoreboard.getSortedScores(scoreObjective1);
            List<Score> arraylist = collection.stream().filter(p_apply_1_ -> p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#")).collect(Collectors.toList());
            List<Score> arraylist1;

            if (arraylist.size() > 15) {
                arraylist1 = Lists.newArrayList(Iterables.skip(arraylist, collection.size() - 15));
            } else {
                arraylist1 = arraylist;
            }

            int height = 0;
            float width = iconOffX + mc.fontRendererObj.getStringWidth(title) + 10;
            boolean isClassic = HUD.hudMode.isCurrentMode("Classic");

            String s3 = scoreObjective1.getDisplayName();
            YRenderUtil.drawRectNormal(x, y + draggableHeight, x + this.width, y + draggableHeight + 14, isClassic ? 0x44000000 : Colors.getColor(166, 173, 176, 190));
            this.drawCenteredString(s3, (int) x + (int) (this.width / 2), (int) y + (int) draggableHeight + 3, 0xffffffff);
            height += 18;

            for (int i = 0; i < arraylist1.size(); i++) {
                Score score1 = arraylist1.get(arraylist1.size() - i - 1);
                ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
                String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
                width = Math.max(mc.fontRendererObj.getStringWidth(s1), width);
                s1 = s1.replaceAll("\247l" , "");
                mc.fontRendererObj.drawStringWithShadow(s1, (int) (x + 4f), (int) y + (height += 10), 0xffffffff);
            }

            width = Math.max(mc.fontRendererObj.getStringWidth(s3), width);

            this.width = width + 8;
            this.height = height - 2;
        }
    }

    public void drawCenteredString(String text, int x, int y, int col) {
        mc.fontRendererObj.drawStringWithShadow(text, x - (mc.fontRendererObj.getStringWidth(text) / 2f), y, col);
    }

    @Override
    public void show() {
        Hanabi.INSTANCE.customScoreboard = true;
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
        Hanabi.INSTANCE.customScoreboard = false;
    }
}
