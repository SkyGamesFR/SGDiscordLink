package fr.skygames.sgdiscordlink.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HttpUtils {

    private static FileManager fileManager;
    private static final OkHttpClient client = new OkHttpClient();

    public static void init(FileManager fileManager) {
        HttpUtils.fileManager = fileManager;
    }

    public static boolean checkIfTokenExist(String uuid) throws IOException {
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

    public static void registerToken(String uuid) throws IOException {
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

    public static String getToken(ProxiedPlayer player) {
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

    public static void verifyToken(String token, ModalInteractionEvent event) {
        try {
            Request request = new Request.Builder()
                    .url(fileManager.getConfig("apiconfig").getString("url") + "discord/" + token + "/verify")
                    .addHeader("Authorization", "Bearer " + fileManager.getConfig("apiconfig").getString("api-key"))
                    .get()
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .build();

            Response response = client.newCall(request).execute();
            response.close();
            if (response.isSuccessful()) {
                updateDiscordID(Objects.requireNonNull(event.getInteraction().getMember()).getId(), token, event);
            } else {
                Message message = event.getInteraction().getMessageChannel().sendMessage(event.getInteraction().getUser().getAsMention() + " Le token est invalide ou une erreur est survenue !").complete();
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(() -> {
                    message.delete().queue();
                }, 15, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateDiscordID(String discordID, String token, ModalInteractionEvent event) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("discord_id", discordID);

        try {
            Request request = new Request.Builder()
                    .url(fileManager.getConfig("apiconfig").getString("url") + "discord/" + token + "/id")
                    .addHeader("Authorization", "Bearer " + fileManager.getConfig("apiconfig").getString("api-key"))
                    .patch(RequestBody.create(jsonObject.toString(), MediaType.parse("application/json")))
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .build();

            Response response = client.newCall(request).execute();
            response.close();

            if (response.isSuccessful()) {
                Message message = event.getInteraction().getMessageChannel().sendMessage(event.getInteraction().getUser().getAsMention() + " Ton compte à bien été lié à Discord !").complete();
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(() -> {
                    message.delete().queue();
                }, 15, TimeUnit.SECONDS);
            } else {
                Message message = event.getInteraction().getMessageChannel().sendMessage(event.getInteraction().getUser().getAsMention() + " Une erreur est survenue !").complete();
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(() -> {
                    message.delete().queue();
                }, 15, TimeUnit.SECONDS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Request request = new Request.Builder()
                    .url(fileManager.getConfig("apiconfig").getString("url") + "players/" + discordID + "/update")
                    .addHeader("Authorization", "Bearer " + fileManager.getConfig("apiconfig").getString("api-key"))
                    .get()
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .build();

            Response response = client.newCall(request).execute();
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
