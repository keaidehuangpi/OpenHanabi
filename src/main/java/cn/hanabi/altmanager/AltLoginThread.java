package cn.hanabi.altmanager;

import cn.hanabi.Hanabi;
import cn.hanabi.injection.interfaces.IMinecraft;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import me.ratsiel.auth.model.mojang.MinecraftAuthenticator;
import me.ratsiel.auth.model.mojang.MinecraftToken;
import me.ratsiel.auth.model.mojang.profile.MinecraftProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

import java.net.Proxy;


public final class AltLoginThread extends Thread {
    private final Alt alt;
    private String status;

    public AltLoginThread(Alt alt) {
        super("Alt Login Thread");
        Minecraft mc = Minecraft.getMinecraft();
        this.alt = alt;
        this.status = EnumChatFormatting.GRAY + "Waiting...";
    }

    private Session createSession(final String username, final String password, final boolean mslogin) {
        final YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
        final MinecraftAuthenticator minecraftAuthenticator = new MinecraftAuthenticator();

        auth.setUsername(username);
        auth.setPassword(password);

        if (mslogin) {
            try {
                final MinecraftToken minecraftToken = minecraftAuthenticator.loginWithXbox(username, password);
                final MinecraftProfile minecraftProfile = minecraftAuthenticator.checkOwnership(minecraftToken);
                return new Session(minecraftProfile.getUsername(), minecraftProfile.getUuid().toString(), minecraftToken.getAccessToken(), "mojang");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            auth.logIn();
            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        } catch (AuthenticationException localAuthenticationException) {
            localAuthenticationException.printStackTrace();
            return null;
        }
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    @Override
    public void run() {
        if (alt.getPassword().equals("")) {

            Session sess = new Session(alt.getUsername(), "", "", "mojang");

            ((IMinecraft) Minecraft.getMinecraft()).setSession(sess);

            this.status = EnumChatFormatting.GREEN + "Logged in. (" + alt.getUsername() + " - offline name)";
            return;
        }
        this.status = EnumChatFormatting.AQUA + "Logging in...";
        final Session auth = this.createSession(alt.getUsername(), alt.getPassword(), (Hanabi.INSTANCE.mslogin));
        if (auth == null) {
            this.status = EnumChatFormatting.RED + "Login failed!";
        } else {
            AltManager.lastAlt = new Alt(alt.getUsername(), alt.getPassword());
            this.status = EnumChatFormatting.GREEN + "Logged in. (" + auth.getUsername() + ")";
            alt.setMask(auth.getUsername());

            ((IMinecraft) Minecraft.getMinecraft()).setSession(auth);

            try {
                Hanabi.INSTANCE.altFileMgr.getFile(Alts.class).saveFile();
            } catch (Exception ignored) {
            }
        }
    }
}
