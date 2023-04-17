package fr.skygames.sgdiscordlink.listeners.discord;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.skygames.sgdiscordlink.managers.Managers;
import fr.skygames.sgdiscordlink.utils.FileManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ModalListener extends ListenerAdapter {

    private final FileManager fileManager;
    private final OkHttpClient client = new OkHttpClient();
    private boolean isModalOpen = false;
    private final Gson gson = new Gson();

    public ModalListener(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(event.getModalId().equalsIgnoreCase("token-modal")) {
            try {
                isModalOpen = false;
                String token = Objects.requireNonNull(event.getValue("token")).getAsString();

                verifyToken(token, event);

                if (isModalOpen) {
                    event.deferEdit().queue();
                } else {
                    event.deferEdit().queue();
                }

            } catch (Exception e) {
                event.reply("Une erreur est survenue, veuillez réessayer.").setEphemeral(true).queue();
                e.printStackTrace();
            }
        }

    }

    private void verifyToken(String token, ModalInteractionEvent event) {
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

    private void updateDiscordID(String discordID, String token, ModalInteractionEvent event) {
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
                isModalOpen = true;
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

