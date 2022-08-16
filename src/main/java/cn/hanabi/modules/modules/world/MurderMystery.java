package cn.hanabi.modules.modules.world;

import cn.hanabi.events.EventRender;
import cn.hanabi.events.EventTick;
import cn.hanabi.events.EventWorldChange;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.render.ESP;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.WorldUtil;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmorStand;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemMap;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;



public class MurderMystery extends Mod {


    private static EntityPlayer murder;
    private final List<String> alartedPlayers = new ArrayList<>();

    public MurderMystery() {
        super("MurderMystery", Category.WORLD);
    }

    public static boolean isMurder(EntityPlayer player) {
        if (player == null || murder == null)
            return false;

        if (player.isDead || player.isInvisible())
            return false;

        return player.equals(murder);
    }

    @Override
    public void onDisable() {
        if (this.alartedPlayers != null) {
            this.alartedPlayers.clear();
        }
        super.onDisable();
    }

    @EventTarget
    public void onRespawn(EventWorldChange event) {
        if (this.alartedPlayers != null) {
            this.alartedPlayers.clear();
        }
    }

    @EventTarget
    public void onRender(EventRender event) {
        if (isMurder(murder))
            (ModManager.getModule(ESP.class)).renderBox(murder, (murder.hurtTime > 1) ? 0.8f : 0.0f,
                    (murder.hurtTime > 1) ? 0.0f : 0.4f, (murder.hurtTime > 1) ? 0.0f : 1.0f);
    }

    @EventTarget
    public void onTick(EventTick event) {
        if (mc.theWorld == null || this.alartedPlayers == null)
            return;

            try {
            for (EntityPlayer player : WorldUtil.getLivingPlayers()) {
                if (this.alartedPlayers.contains(player.getName()))
                    continue;

                if (player.getCurrentEquippedItem() != null) {
                    if (CheckItem(player.getCurrentEquippedItem().getItem())) {
                        PlayerUtil.tellPlayer(EnumChatFormatting.GOLD + player.getName() + EnumChatFormatting.RESET
                                + " is the murderer!!!");
                        this.alartedPlayers.add(player.getName());
                        murder = player;

                    }
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean CheckItem(Item item) {
        return !(item instanceof ItemMap) && !(item instanceof ItemArmorStand) && !item.getUnlocalizedName().equalsIgnoreCase("item.ingotGold") &&
                !(item instanceof ItemBow) && !item.getUnlocalizedName().equalsIgnoreCase("item.arrow") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.potion") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.paper") &&
                !item.getUnlocalizedName().equalsIgnoreCase("tile.tnt") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.web") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.bed") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.compass") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.comparator") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.shovelWood");
    }


}
