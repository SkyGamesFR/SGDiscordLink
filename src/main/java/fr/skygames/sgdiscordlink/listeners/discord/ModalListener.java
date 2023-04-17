package fr.skygames.sgdiscordlink.listeners.discord;

import com.google.gson.Gson;
import fr.skygames.sgdiscordlink.utils.HttpUtils;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ModalListener extends ListenerAdapter {

    private final OkHttpClient client = new OkHttpClient();
    private static boolean isModalOpen = false;
    private final Gson gson = new Gson();

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(event.getModalId().equalsIgnoreCase("token-modal")) {
            try {
                isModalOpen = false;
                String token = Objects.requireNonNull(event.getValue("token")).getAsString();

                HttpUtils.verifyToken(token, event);

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

    public static boolean isModalOpen() {
        return isModalOpen;
    }
}

