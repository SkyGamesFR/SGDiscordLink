package fr.skygames.sgdiscordlink.listeners.bungee;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.skygames.sgdiscordlink.managers.Managers;
import fr.skygames.sgdiscordlink.utils.FileManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DiscordLinkCommand extends Command {

    private final FileManager fileManager;
    private final OkHttpClient client = new OkHttpClient();

    public DiscordLinkCommand(FileManager fileManager) {
        super("discordlink");
        this.fileManager = fileManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if(args.length == 0) {
            player.sendMessage(new TextComponent("§a§lDiscordLink §7» Pour obtenir ton code utilise la commande /discordlink generate!"));
        } else {
            if (args[0].equalsIgnoreCase("generate")) {
                String textToCopy = getToken(player);
                ((ProxiedPlayer) sender).getServer().sendData("SGDiscordLink:discordToken", textToCopy.getBytes());
                player.sendMessage(new TextComponent("§a§lDiscordLink §7» Ton code est: " + getToken(player)));
            }
        }
    }

    private String getToken(ProxiedPlayer player) {
        try {
            String uuid = player.getUniqueId().toString();

            Request request = new Request.Builder()
                    .url(fileManager.getConfig("apiconfig").getString("url") + "discord/" + uuid + "/token")
                    .addHeader("Authorization", "Bearer " + fileManager.getConfig("apiconfig").getString("api-key"))
                    .get()
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);


            if (response.isSuccessful()) {
                assert response.body() != null;
                return jsonObject.get("token").getAsString();
            } else {
                return "null";
            }
        } catch (Exception e) {
            player.sendMessage(new TextComponent("§a§lDiscordLink §7» Une erreur est survenue !"));
        }

        return null;
    }
}
