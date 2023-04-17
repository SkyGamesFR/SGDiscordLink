package fr.skygames.sgdiscordlink.listeners.discord;

import fr.skygames.sgdiscordlink.Main;
import fr.skygames.sgdiscordlink.utils.CustomLogger;
import fr.skygames.sgdiscordlink.utils.FileManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordListener extends ListenerAdapter {

    private final FileManager fileManager;
    private final CustomLogger logger;
    private final Main plugin;

    public DiscordListener(Main plugin, CustomLogger logger, FileManager fileManager) {
        this.logger = logger;
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        event.getJDA().getPresence().setActivity(Activity.watching(plugin.getDescription().getName() + " - v" + plugin.getDescription().getVersion()));

        TextChannel channel = event.getJDA().getTextChannelById(fileManager.getConfig("config").getLong("discordChannelId"));
        assert channel != null;

        List<Message> messages = channel.getHistory().retrievePast(10).complete();
        messages.forEach(message -> message.delete().queue());

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(":link: **Lie ton compte SkyGames a Discord !**")
                .addField("Pour obtenir le code pour lier ton compte SkyGames a Discord, il te suffit de faire la commande suivante sur le serveur :", "`/discordlink generate`", false)
                .setImage("https://s3.gaming-cdn.com/img/products/2369/pcover/2369.jpg?v=1659006015")
                .setFooter("SkyGames", event.getJDA().getSelfUser().getAvatarUrl());

        ActionRow actionRow = ActionRow.of(Button.secondary("discordlink", "\uD83D\uDD17 Lier mon compte"));

        MessageAction messageAction = channel.sendMessageEmbeds(embed.build()).setActionRows(actionRow);
        messageAction.queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;

        /*if (event.isFromType(ChannelType.PRIVATE)) {
            logger.info("Received a message from " + event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay());

            System.out.printf("[PM] %s: %s\n",
                    event.getAuthor().getName(),
                    event.getMessage().getContentDisplay()
            );
        } else {
            logger.info("Received a message from " + event.getAuthor().getName() + " in " + event.getGuild().getName() + ": " + event.getMessage().getContentDisplay());

            System.out.printf("[%s][%s] %s: %s\n",
                    event.getGuild().getName(),
                    event.getTextChannel().getName(),
                    Objects.requireNonNull(event.getMember()).getEffectiveName(),
                    event.getMessage().getContentDisplay()
            );
        }*/
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().equals("discordlink")) {
            event.deferEdit().queue();
            try {
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("📡: **Lisaison SkyGames**")
                        .addField("Pour lier ton compte SkyGames a Discord, il te suffit de faire la commande suivante sur le serveur :", "`/discordlink generate`", false)
                        .addField("Ensuite, tu recevras un code a entrer dans le champs ci-dessous :", " ", false)
                        .addField("ATTENTION CE MESSAGE SE SUPPRIMERA DANS 1 MINUTE !", " ", false)
                        .setFooter("SkyGames", event.getJDA().getSelfUser().getAvatarUrl());

                ActionRow actionRow = ActionRow.of(Button.secondary("discordlink-verify", "\uD83D\uDD17 Lier mon compte"));

                Message message = event.getChannel().sendMessageEmbeds(embed.build()).setActionRows(actionRow).complete();

                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(() -> {
                    message.delete().queue();
                }, 30, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(event.getComponentId().equalsIgnoreCase("discordlink-verify")) {
            TextInput textInput = TextInput.create("token", "Token", TextInputStyle.SHORT)
                    .setMinLength(10)
                    .setMaxLength(10)
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("token-modal", "\uD83D\uDD17 Liaison de ton compte SkyGames")
                    .addActionRows(ActionRow.of(textInput))
                    .build();

            event.replyModal(modal).queue();
        }
    }
}

