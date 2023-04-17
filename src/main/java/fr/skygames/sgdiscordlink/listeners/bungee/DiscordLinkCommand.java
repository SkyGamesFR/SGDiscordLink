package fr.skygames.sgdiscordlink.listeners.bungee;

import fr.skygames.sgdiscordlink.utils.FileManager;
import fr.skygames.sgdiscordlink.utils.HttpUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import okhttp3.OkHttpClient;

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
                String textToCopy = HttpUtils.getToken(player);
                assert textToCopy != null;
                ((ProxiedPlayer) sender).getServer().sendData("SGDiscordLink:discordToken", textToCopy.getBytes());
                player.sendMessage(new TextComponent("§a§lDiscordLink §7» Ton code est: " + HttpUtils.getToken(player)));
            }
        }
    }
}
