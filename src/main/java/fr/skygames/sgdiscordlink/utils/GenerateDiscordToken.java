package fr.skygames.sgdiscordlink.utils;

public class GenerateDiscordToken {

    public static String generateToken() {
        String token = "";
        for(int i = 0; i < 10; i++) {
            token += (char) (Math.random() * 26 + 97);
        }
        return token;
    }
}
