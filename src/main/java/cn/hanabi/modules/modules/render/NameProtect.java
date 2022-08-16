package cn.hanabi.modules.modules.render;

import cn.hanabi.Client;
import cn.hanabi.events.EventText;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.apache.commons.lang3.StringUtils;

public class NameProtect extends Mod {


    public Value<Boolean> allPlayersValue = new Value<>("NameProtect", "AllPlayer", false);


    public NameProtect() {
        super("NameProtect", Category.RENDER);
        // TODO 自动生成的构造函数存根
    }

    @EventTarget
    public void onText(final EventText event) {
        if(mc.thePlayer == null)
            return;

        if(!getState())
            return;

        event.setText(StringUtils.replace(event.getText(), mc.thePlayer.getName(), Client.username + "\247f"));

        if(allPlayersValue.getValue())
            for(final NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap())
                event.setText(StringUtils.replace(event.getText(), playerInfo.getGameProfile().getName(), "PROTECTION"));
    }
}
