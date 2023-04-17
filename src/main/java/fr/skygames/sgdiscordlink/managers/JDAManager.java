package fr.skygames.sgdiscordlink.managers;

import fr.skygames.sgdiscordlink.Main;
import fr.skygames.sgdiscordlink.listeners.discord.DiscordListener;
import fr.skygames.sgdiscordlink.listeners.discord.ModalListener;
import fr.skygames.sgdiscordlink.utils.CustomLogger;
import fr.skygames.sgdiscordlink.utils.FileManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class JDAManager {

    private final Main plugin;
    private final CustomLogger logger;
    private final FileManager fileManager;
    private JDA jda;

    public JDAManager(Main main, CustomLogger logger, FileManager fileManager) {
        this.logger = logger;
        this.plugin = main;
        this.fileManager = fileManager;
    }

    public void load() {
        try {
            jda = JDABuilder.createDefault(fileManager.getConfig("config").getString("discordToken"))
                    .setAutoReconnect(true)
                    .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                    .addEventListeners(new DiscordListener(plugin, logger, fileManager), new ModalListener(fileManager))
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES).build();
        } catch (Exception e) {
            logger.severe("Error while loading JDA");
        }
    }

    public void shutdown() {
        jda.shutdown();
    }
}
