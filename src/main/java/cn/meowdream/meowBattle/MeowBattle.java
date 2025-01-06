package cn.meowdream.meowBattle;

import cn.meowdream.meowBattle.battleWorld.BattleWorld;
import cn.meowdream.meowBattle.battleWorld.borderChangeState;
import cn.meowdream.meowBattle.battleWorld.borderChangeTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class MeowBattle extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("battle").setExecutor(new CommandBattle());
        this.getCommand("battle").setTabCompleter(new TabCompleterBattle());
    }

    public class CommandBattle implements CommandExecutor {
        BattleWorld battleWorld;

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                if(args.length == 1) {
                    if (Objects.equals(args[0], "init")) {
                        List<borderChangeTask> borderChangeTasks = new ArrayList<>();
                        borderChangeTasks.add(new borderChangeTask(10, 8, 0.75, 0.5));
                        borderChangeTasks.add(new borderChangeTask(30, 8, 0.45, 1));
                        borderChangeTasks.add(new borderChangeTask(50, 10, 0.15, 1.5));
                        borderChangeTasks.add(new borderChangeTask(70, 15, 0, 2.5));
                        World world = Objects.requireNonNull(Bukkit.getWorld("world"));
                        battleWorld = new BattleWorld(world, new Location(world,495, 0, 210), new Location(world, 595, 0, 310), 0, borderChangeTasks);
//                        battleWorld.startBorderChange();
                    } else if(Objects.equals(args[0], "start")) {
                        battleWorld.startBorderChange();
                    } else if (Objects.equals(args[0], "stop")) {
                        battleWorld.stopAllActivities();
                    } else if (Objects.equals(args[0], "state")) {
                        borderChangeState state = battleWorld.getState();
                        if(state == borderChangeState.NOT_STARTED) {
                            sender.sendMessage("The border change is not started.");
                        } else if(state == borderChangeState.CLOSING) {
                            sender.sendMessage("The border is closing.");
                        } else if(state == borderChangeState.CLOSED) {
                            sender.sendMessage("The border is closed.");
                        } else if(state == borderChangeState.FINAL) {
                            sender.sendMessage("The border is final.");
                        }
                    } else if (Objects.equals(args[0], "destroy")) {
                        battleWorld.destroy();
                    }
                }
            }
            return true;
        }
    }

    public class TabCompleterBattle implements TabCompleter {
        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                List<String> commands = new ArrayList<>(Arrays.asList("start", "stop", "state", "destroy"));
                commands.removeIf(s -> !s.startsWith(args[0]));
                return commands;
            }
            return null;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
