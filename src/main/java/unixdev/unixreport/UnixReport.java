package unixdev.unixreport;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class UnixReport extends JavaPlugin {
    String configFolder = "plugins/UnixReport/config.yml";

    @Override
    public void onEnable() {
        this.getServer().getPluginCommand("unixreport").setExecutor(new CommandReport());
        checkConfig();
    }

    public String getString(String string) {
        File file = new File(configFolder);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config = YamlConfiguration.loadConfiguration(file);
        return config.getString(string);
    }

    public void checkConfig() {
        File file = new File(configFolder);
        if(!file.exists()) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config = YamlConfiguration.loadConfiguration(file);

        if(config.getString("access_token") == null || config.getString("access_token").equals("ваш токен здесь")) {
            Bukkit.getLogger().info("§cОШИБКА§f: Токен ВК не указан! > Плагин отключен");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
