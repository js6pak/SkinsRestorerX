package skinsrestorer.sponge.listeners;

import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent.Auth;
import org.spongepowered.api.profile.GameProfile;
import skinsrestorer.shared.storage.Config;
import skinsrestorer.shared.storage.SkinStorage;
import skinsrestorer.shared.utils.C;
import skinsrestorer.sponge.SkinsRestorer;
import skinsrestorer.shared.utils.MojangAPI.SkinRequestException;


public class LoginListener implements EventListener<ClientConnectionEvent.Auth> {
    private SkinsRestorer plugin;

    public LoginListener(SkinsRestorer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(Auth e) {
        if (e.isCancelled())
            return;

        if (Config.DISABLE_ONJOIN_SKINS)
            return;

        GameProfile profile = e.getProfile();

        profile.getName().ifPresent(name -> {
            try {
                // Don't change skin if player has no custom skin-name set and his username is invalid
                if (SkinStorage.getPlayerSkin(name) == null && !C.validUsername(name)) {
                    System.out.println("[SkinsRestorer] Not applying skin to " + name + " (invalid username).");
                    return;
                }

                String skin = SkinStorage.getDefaultSkinNameIfEnabled(name);
                plugin.getSkinApplier().updateProfileSkin(profile, skin);
            } catch (SkinRequestException ignored) {
            }
        });
    }
}