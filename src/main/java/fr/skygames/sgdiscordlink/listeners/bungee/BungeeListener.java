package fr.skygames.sgdiscordlink.listeners.bungee;

import com.google.gson.JsonObject;
import fr.skygames.sgdiscordlink.managers.Managers;
import fr.skygames.sgdiscordlink.utils.FileManager;
import fr.skygames.sgdiscordlink.utils.GenerateDiscordToken;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import okhttp3.*;

import java.io.IOException;

public class BungeeListener implements Listener {

    private final FileManager fileManager;
    private final OkHttpClient client = new OkHttpClient();

    public BungeeListener(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        ProxiedPlayer player = e.getPlayer();

        try {
            if(!checkIfTokenExist(player.getUniqueId().toString())) {
                registerToken(player.getUniqueId().toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private boolean checkIfTokenExist(String uuid) throws IOException {
        Request request = new Request.Builder()
                .url(fileManager.getConfig("apiconfig").getString("url") + "discord/" + uuid + "/token")
                .addHeader("Authorization", "Bearer " + fileManager.getConfig("apiconfig").getString("api-key"))
                .get()
                .addHeader("User-Agent", "Mozilla/5.0")
                .build();

        Response response = client.newCall(request).execute();
        response.close();
        return response.isSuccessful();
    }

    private void registerToken(String uuid) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", uuid);
        json.addProperty("token", GenerateDiscordToken.generateToken());

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(fileManager.getConfig("apiconfig").getString("url") + "discord/" + uuid + "/token")
                .addHeader("Authorization", "Bearer " + fileManager.getConfig("apiconfig").getString("api-key"))
                .post(body)
                .addHeader("User-Agent", "Mozilla/5.0")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            System.out.println("Token generated for " + uuid);
        } else {
            System.out.println("Error while generating token for " + uuid);
        }
    }

}

