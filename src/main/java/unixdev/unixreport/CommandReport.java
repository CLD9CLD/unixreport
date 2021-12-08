package unixdev.unixreport;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CommandReport implements CommandExecutor {

    HashMap<String, Long> delay_long  = new HashMap<>();

    String permission_player = "unixreport.use";
    String permission_immunity = "unixreport.immunity";

    File file = new File("plugins/UnixReport/config.yml");
    FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;

            if(!p.hasPermission("unixreport.use")) {
                p.sendMessage(config.getString("messages.prefix") + " " + config.getString("messages.not_enough_perm"));
                return false;
            }

            if(args.length < 2) {
                p.sendMessage(config.getString("messages.prefix") + " " + config.getString("messages.error"));
                return false;
            }

            Player imposter = Bukkit.getPlayerExact(args[0]);

            if(imposter == null) {
                p.sendMessage(config.getString("messages.prefix") + " " + config.getString("messages.not_online").replace("%player%",args[0]));
                return false;
            }

            if(imposter.getName().equals(p.getName())) {
                p.sendMessage(config.getString("messages.prefix") + " " + config.getString("messages.player_you"));
                return false;
            }

            if(imposter.hasPermission(permission_immunity)) {
                p.sendMessage(config.getString("messages.prefix") + " " + config.getString("messages.player_immunity"));
                return false;
            }

            args[0] = "";
            String reason = Arrays.stream(args).collect(Collectors.joining(" "));

            if(reason.length() < config.getInt("reason_min_symbols")) {
                p.sendMessage(config.getString("messages.prefix") + " " + config.getString("messages.error_min_symbols").replace("%min%",config.getString("reason_min_symbols")));
                return false;
            }

            long time = System.currentTimeMillis()/1000l;
            if(delay_long.get(p.getName()) != null) {
                if(delay_long.get(p.getName()) <= time) {
                    delay_long.remove(p.getName());
                }
                else {
                    p.sendMessage(config.getString("messages.prefix") + " " + config.getString("messages.error_delay").replace("%sec%",String.valueOf(delay_long.get(p.getName())-time)));
                    return false;
                }
            }

            delay_long.put(p.getName(), time + config.getInt("report_delay_sec"));
            reason = config.getString("messages.report_chat").replace("%player_1%",p.getName()).replace("%player_2%",imposter.getName()).replace("%reason%",reason).replace("%server_name%",config.getString("server_name"));
            sendMessage(reason);
            p.sendMessage(config.getString("messages.prefix") + " " + config.getString("messages.report_send").replace("%player%",imposter.getName()));
        }
        else {
            if(args.length == 1 && args[0].equals("reload")) {
                file = new File("plugins/UnixReport/config.yml");
                config = YamlConfiguration.loadConfiguration(file);
                sender.sendMessage(config.getString("messages.prefix") + " Конфигурация перезагружена!");
                return false;
            }
            sender.sendMessage(config.getString("messages.prefix") + " Команду нельзя выполнить в консоли!");
        }
        return true;
    }

    public void sendMessage(String msg) {
        if(config.getInt("report_mode") == 1) {
            HTTP.post("https://api.vk.com/method/messages.send", "access_token=" + config.getString("access_token") + "&peer_id=" + config.getString("peer_id") + "&message=" + msg + "&v=5.81");
        }
        else if(config.getInt("report_mode") == 2) {
            HTTP.post("https://api.vk.com/method/messages.send", "access_token=" + config.getString("access_token") + "&user_id=" + config.getString("user_id") + "&message=" + msg + "&v=5.81");
        }
    }
}