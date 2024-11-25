package ubb.scs.map.demosocialnetwork.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private final Dotenv dotenv;

    private Config() {
        boolean isTest = Boolean.getBoolean("test.mode");

        dotenv = isTest ? Dotenv.configure().filename(".env.test").load() :
                Dotenv.configure().filename(".env").load();
    }

    private static Config instance = null;

    public static Config getInstance() {
        if (instance == null)
            instance = new Config();
        return instance;
    }

    public String getDatabaseUrl() {
        return dotenv.get("DB_URL");
    }

    public String getDatabaseUser() {
        return dotenv.get("DB_USER");
    }

    public String getDatabasePassword() {
        return dotenv.get("DB_PASSWORD");
    }

}
