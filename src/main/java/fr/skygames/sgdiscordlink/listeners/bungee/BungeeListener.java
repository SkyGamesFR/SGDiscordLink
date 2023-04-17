package fr.skygames.sgdiscordlink.listeners.bungee;

import com.google.gson.JsonObject;
import fr.skygames.sgdiscordlink.managers.Managers;
import fr.skygames.sgdiscordlink.utils.FileManager;
import fr.skygames.sgdiscordlink.utils.GenerateDiscordToken;
import fr.skygames.sgdiscordlink.utils.HttpUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import okhttp3.*;

import java.io.IOException;

public class BungeeListener implements Listener {

    private final OkHttpClient client = new OkHttpClient();

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        ProxiedPlayer player = e.getPlayer();

        try {
            if(!HttpUtils.checkIfTokenExist(player.getUniqueId().toString())) {
                HttpUtils.registerToken(player.getUniqueId().toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }



}

