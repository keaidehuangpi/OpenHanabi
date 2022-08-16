package cn.hanabi.altmanager;

import cn.hanabi.Hanabi;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import me.ratsiel.auth.model.mojang.MinecraftAuthenticator;
import me.ratsiel.auth.model.mojang.MinecraftToken;
import me.ratsiel.auth.model.mojang.profile.MinecraftProfile;
import net.minecraft.util.EnumChatFormatting;

import java.net.Proxy;

public class AddAltThread extends Thread {
    private final String password;
    private final String username;

    public AddAltThread(final String username, final String password) {
        this.username = username;
        this.password = password;
        GuiAltManager.setStatus(EnumChatFormatting.GRAY + "Idle...");
    }

    private void checkAndAddAlt(final String username, final String password, final boolean mslogin) {
        final YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service
                .createUserAuthentication(Agent.MINECRAFT);
        final MinecraftAuthenticator minecraftAuthenticator = new MinecraftAuthenticator();
        auth.setUsername(username);
        auth.setPassword(password);

        String name;
        try {
            if (mslogin){
                final MinecraftToken minecraftToken = minecraftAuthenticator.loginWithXbox(username, password);
                final MinecraftProfile minecraftProfile = minecraftAuthenticator.checkOwnership(minecraftToken);
                name = minecraftProfile.getUsername();
            } else {
                auth.logIn();
                name = auth.getSelectedProfile().getName();
            }
            AltManager.registry.add(new Alt(username, password, name));
            try {
                Hanabi.INSTANCE.altFileMgr.getFile(Alts.class).saveFile();
            } catch (Exception ignored) {
            }
            GuiAltManager.setStatus("Alt added. (" + username + ")");
        } catch (AuthenticationException e) {
            GuiAltManager.setStatus(EnumChatFormatting.RED + "Alt failed!");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (this.password.equals("")) {
            AltManager.registry.add(new Alt(this.username, ""));
            GuiAltManager.setStatus(
                    EnumChatFormatting.GREEN + "Alt added. (" + this.username + " - offline name)");
            return;
        }
        GuiAltManager.setStatus(EnumChatFormatting.AQUA + "Trying alt...");
        this.checkAndAddAlt(this.username, this.password , Hanabi.INSTANCE.mslogin);
    }
}
