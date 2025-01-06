package cn.meowdream.meowBattle.battleWorld;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class BattleWorld {
    private World world;
    private double baseY;
    private WorldBorder worldBorder;
    private List<borderChangeTask> borderChangeTasks, originTasks;
    private List<Integer> taskNum = new ArrayList<>();
    private int originSize = 0;
    private double currentDamage;
    BlockDisplay wall1, wall2, wall3, wall4;
    BukkitTask renderTask, soundManagerTask;
    private borderChangeState state = borderChangeState.NOT_STARTED;

    private boolean isRunning = true;

    public void createParticleWall(double x1, double z1, double x2, double z2) {
        if(state == borderChangeState.FINAL) {
            wall1.remove();
            wall2.remove();
            wall3.remove();
            wall4.remove();
            return;
        }

//        Particle particle = Particle.BLOCK_MARKER;
//        for(double y = baseY; y < world.getMaxHeight(); y += step) {
//            for(double x = x1; x <= x2; x += step) {
//                Location loc = new Location(world, x, y, z1);
//                world.spawnParticle(particle, loc, 1, step / 2, step / 2, 0, 0, Material.BARRIER.createBlockData(), true);
//                loc = new Location(world, x, y, z2);
//                world.spawnParticle(particle, loc, 1, step / 2, step / 2, 0, 0, Material.BARRIER.createBlockData(), true);
//            }
//            for(double z = z1; z <= z2; z += step) {
//                Location loc = new Location(world, x1, y, z);
//                world.spawnParticle(particle, loc, 1, 0, step / 2, step / 2, 0, Material.BARRIER.createBlockData(), true);
//                loc = new Location(world, x2, y, z);
//                world.spawnParticle(particle, loc, 1, 0, step / 2, step / 2, 0, Material.BARRIER.createBlockData(), true);
//            }
//        }

//        Bukkit.broadcastMessage("Moving walls...");

        BlockData data = Material.RED_STAINED_GLASS.createBlockData();

        Location bLocation = worldBorder.getCenter();

//        Bukkit.broadcastMessage("bLocation: " + bLocation);
//
//        Bukkit.broadcastMessage("Wall1 Pos BEFORE: " + wall1.getLocation().getX() + " " + wall1.getLocation().getY() + " " + wall1.getLocation().getZ());
//        Bukkit.broadcastMessage("Trying to tp to: " + new Location(world, bLocation.getX() - worldBorder.getSize() / 2, world.getMaxHeight(), bLocation.getZ() - worldBorder.getSize() / 2));
        wall1.setViewRange(Float.MAX_VALUE);
        wall1.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f((float) worldBorder.getSize(), -(float) (world.getMaxHeight() - baseY), 0), new AxisAngle4f()));
        wall1.teleport(new Location(world, bLocation.getX() - worldBorder.getSize() / 2, world.getMaxHeight(), bLocation.getZ() - worldBorder.getSize() / 2));
        wall1.setBlock(data);

//        Bukkit.broadcastMessage("Wall1 Pos AFTER: " + wall1.getLocation().getX() + " " + wall1.getLocation().getY() + " " + wall1.getLocation().getZ());

        wall2.setViewRange(Float.MAX_VALUE);
        wall2.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f((float) worldBorder.getSize(), -(float) (world.getMaxHeight() - baseY), 0), new AxisAngle4f()));
        wall2.teleport(new Location(world, bLocation.getX() - worldBorder.getSize() / 2, world.getMaxHeight(), bLocation.getZ() + worldBorder.getSize() / 2));
        wall2.setBlock(data);

        wall3.setViewRange(Float.MAX_VALUE);
        wall3.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(0, -(float) (world.getMaxHeight() - baseY), (float) worldBorder.getSize()), new AxisAngle4f()));
        wall3.teleport(new Location(world, bLocation.getX() - worldBorder.getSize() / 2, world.getMaxHeight(), bLocation.getZ() - worldBorder.getSize() / 2));
        wall3.setBlock(data);

        wall4.setViewRange(Float.MAX_VALUE);
        wall4.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(0, -(float) (world.getMaxHeight() - baseY), (float) worldBorder.getSize()), new AxisAngle4f()));
        wall4.teleport(new Location(world, bLocation.getX() + worldBorder.getSize() / 2, world.getMaxHeight(), bLocation.getZ() - worldBorder.getSize() / 2));
        wall4.setBlock(data);

//        for(Player player : world.getPlayers()) {
//            Particle particle = Particle.FLAME;
//            for(double y = player.getLocation().getY() - 20; y < player.getLocation().getY() + 20; y += step) {
//                for(double x = x1; x <= x2; x += step) {
//                    Location loc = new Location(world, x, y, z1);
//                    player.spawnParticle(particle, loc, 6, step / 4, step / 2, 0, 0, null, true);
//                    loc = new Location(world, x, y, z2);
//                    player.spawnParticle(particle, loc, 6, step / 4, step / 2, 0, 0, null, true);
//                }
//                for(double z = z1; z <= z2; z += step) {
//                    Location loc = new Location(world, x1, y, z);
//                    player.spawnParticle(particle, loc, 6, 0, step / 2, step / 4, 0, null, true);
//                    loc = new Location(world, x2, y, z);
//                    player.spawnParticle(particle, loc, 6, 0, step / 2, step / 4, 0, null, true);
//                }
//            }
//        }
    }

    private final Map<Player, BukkitTask> damageTasks = new HashMap<>();

    public void applyDamageOverTime(Player player, int duration, double damage) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }

                // 扣除0.1生命值
                player.damage(damage);
            }
        };

        BukkitTask t = task.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("MeowBattle")), 0L, 20L); // 每秒运行一次任务
        damageTasks.put(player, t);
    }

    public void cancelDamageOverTime(Player player) {
        BukkitTask task = damageTasks.remove(player); // 从映射中获取并移除任务
        if (task != null) {
            task.cancel(); // 取消任务
        }
    }

    private final Map<Player, Boolean> lastInRing = new HashMap<>();

    BukkitRunnable renderer = new BukkitRunnable() {
        @Override
        public void run() {
            if(!isRunning) {
                return;
            }
            if(world != null) {
                createParticleWall(worldBorder.getCenter().getX() - worldBorder.getSize() / 2, worldBorder.getCenter().getZ() - worldBorder.getSize() / 2, worldBorder.getCenter().getX() + worldBorder.getSize() / 2, worldBorder.getCenter().getZ() + worldBorder.getSize() / 2);
//                Bukkit.broadcastMessage("Wall1 Pos: " + wall1.getLocation().getX() + " " + wall1.getLocation().getY() + " " + wall1.getLocation().getZ());
            }
        }
    };

    BukkitRunnable soundManager = new BukkitRunnable() {
        @Override
        public void run() {
            if(!isRunning) {
                return;
            }
            if(world != null) {
                Sound sound = Sound.ENTITY_BLAZE_BURN;
                for(Player player : world.getPlayers()) {
                    if(!worldBorder.isInside(player.getLocation()) || !isRunning) {
                        if(!lastInRing.containsKey(player) || lastInRing.get(player)) {
//                            player.stopSound(sound);
//                            Bukkit.broadcastMessage("Stopping sound");
                            applyDamageOverTime(player, 1, currentDamage);
                            lastInRing.put(player, false);
                        }
                        player.playSound(player.getLocation(), sound, 0.7f, 0);

                        Particle particle = Particle.FLAME;
                        player.spawnParticle(particle, new Location(world, player.getLocation().getX(), player.getLocation().getY() + 1, player.getLocation().getZ()), 4, 0.5, 0.5, 0.5, 0, null, true);

//                        Bukkit.broadcastMessage("Playing OUT ring sound");
                    } else {
                        // calculate the distance to the border
                        Location pLocation = player.getLocation(), bLocation = worldBorder.getCenter();
                        double wbSize = worldBorder.getSize() / 2;
                        double distance = Math.min(Math.min(Math.abs(pLocation.getX() - bLocation.getX() + wbSize), Math.abs(pLocation.getX() - bLocation.getX() - wbSize)), Math.min(Math.abs(pLocation.getZ() - bLocation.getZ() + wbSize), Math.abs(pLocation.getZ() - bLocation.getZ() - wbSize)));
//                        Bukkit.broadcastMessage("Distance: " + distance + ";");
                        if(!lastInRing.containsKey(player) || !lastInRing.get(player)) {
                            lastInRing.put(player, true);
                            cancelDamageOverTime(player);
//                            player.stopSound(sound);
//                            Bukkit.broadcastMessage("Stopping sound");
                        }
                        if(distance < 14) {
                            player.playSound(new Location(world, pLocation.getX(), pLocation.getY() - distance, pLocation.getZ()), sound, Math.max((float) (0.7 - distance / 14 * 0.7), 0), 0);
//                            Bukkit.broadcastMessage("Playing IN ring sound");
                        } else {
                            player.stopSound(sound);
//                            Bukkit.broadcastMessage("Stopping sound");
                        }
                    }
                }
            }
        }
    };

    public BattleWorld(World world, Location corner1, Location corner2, double baseY, List<borderChangeTask> borderChangeTasks) {
        this.world = world;
        this.baseY = baseY;
        worldBorder = Bukkit.createWorldBorder();
//        worldBorder = world.getWorldBorder();
        worldBorder.setCenter((corner1.getX() + corner2.getX()) / 2, (corner1.getZ() + corner2.getZ()) / 2);
        originSize = (int) Math.min(Math.abs(corner1.getX() - corner2.getX()), Math.abs(corner1.getZ() - corner2.getZ())) / 2 + 1;
        worldBorder.setSize(originSize * 2);
        worldBorder.setDamageAmount(0f);
        worldBorder.setWarningDistance(0);
        worldBorder.setDamageBuffer(0);

        for(Player player : world.getPlayers()) {
            player.setResourcePack("http://ys-n.ysepan.com/wap/meowlynxsea/A2BcPlIE3EGQ6JeAeQ/kDN3lgB,AB52M5yRa48aV7EQ3yge8/MeowBattle.zip");
        }

        //borderChangeTasks中最后一个任务的size必须为0
        if(!borderChangeTasks.isEmpty()) {
            borderChangeTasks.getLast().size = 0;
        } else {
            throw new IllegalArgumentException("borderChangeTasks is empty");
        }

        this.originTasks = borderChangeTasks;
        this.borderChangeTasks = new ArrayList<>();
        // 将所有的task分割成0.1秒一个
        for (int i = 0; i < borderChangeTasks.size(); i++) {
            int splitTaskNum = 0;
            for(double j = 0; j < borderChangeTasks.get(i).duration; j += 0.1) {
                if(i == 0) {
                    this.borderChangeTasks.add(new borderChangeTask(borderChangeTasks.get(i).startTime + j, 0.1, borderChangeTasks.get(i).size * (j / borderChangeTasks.get(i).duration) + 1 * (1 - j / borderChangeTasks.get(i).duration), borderChangeTasks.get(i).damagePerSecond));
                } else {
                    this.borderChangeTasks.add(new borderChangeTask(borderChangeTasks.get(i).startTime + j, 0.1, borderChangeTasks.get(i).size * (j / borderChangeTasks.get(i).duration) + borderChangeTasks.get(i - 1).size * (1 - j / borderChangeTasks.get(i).duration), borderChangeTasks.get(i).damagePerSecond));
                }
                splitTaskNum++;
            }
            taskNum.add(splitTaskNum);
        }
    }

    private void startBorderChangeTask(int taskId, Location targetCenter) {
        if(!isRunning) {
            return;
        }

        currentDamage = borderChangeTasks.get(taskId).damagePerSecond;

        // get current size
        double size = worldBorder.getSize() / 2;
        // get current center
        Location center = worldBorder.getCenter();
        // calculate new size
        double newSize = Math.max(originSize * borderChangeTasks.get(taskId).size, 1D);

        // 保证新的边界的四边都在老边界之内
        while(targetCenter.getX() - newSize < center.getX() - size) {
            targetCenter.setX(targetCenter.getX() + 0.1);
        }
        while(targetCenter.getX() + newSize > center.getX() + size) {
            targetCenter.setX(targetCenter.getX() - 0.1);
        }
        while(targetCenter.getZ() - newSize < center.getZ() - size) {
            targetCenter.setZ(targetCenter.getZ() + 0.1);
        }
        while(targetCenter.getZ() + newSize > center.getZ() + size) {
            targetCenter.setZ(targetCenter.getZ() - 0.1);
        }

        double duration = borderChangeTasks.get(taskId).duration;

        worldBorder.setSize(newSize * 2, TimeUnit.MILLISECONDS, (long) (duration * 1000));
        worldBorder.setCenter(targetCenter);
    }

    public void startBorderChange() {
        wall1 = (BlockDisplay) world.spawnEntity(new Location(world, 0, 0, 0), EntityType.BLOCK_DISPLAY);
        wall2 = (BlockDisplay) world.spawnEntity(new Location(world, 0, 0, 0), EntityType.BLOCK_DISPLAY);
        wall3 = (BlockDisplay) world.spawnEntity(new Location(world, 0, 0, 0), EntityType.BLOCK_DISPLAY);
        wall4 = (BlockDisplay) world.spawnEntity(new Location(world, 0, 0, 0), EntityType.BLOCK_DISPLAY);

        renderTask = renderer.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("MeowBattle")), 0, 2);
        soundManagerTask = soundManager.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("MeowBattle")), 0, 7);

        List<Location> targetLocations = new ArrayList<>();

        double simulatedSize = worldBorder.getSize() / 2;
        Location simulationCenter = worldBorder.getCenter();

        for (int i = 0; i < borderChangeTasks.size(); i++) {
            int finalI = i;
            int currentTaskID = 0;
            int j = i - taskNum.getFirst();
            while (j > 0) {
                currentTaskID++;
                j -= taskNum.get(currentTaskID);
            }
            j += taskNum.get(currentTaskID);

            if(targetLocations.size() <= currentTaskID) {
                // calculate new size
                double newSize = originSize * originTasks.get(currentTaskID).size;

                // calculate new center
                Location tCenter = new Location(world, simulationCenter.getX() + (simulatedSize - newSize) * 2 * (Math.random() - 0.5), baseY, simulationCenter.getZ() + (simulatedSize - newSize) * 2 * (Math.random() - 0.5));

                targetLocations.add(tCenter);
            }

            Location tCenter = targetLocations.get(currentTaskID);
            Location newCenter = new Location(
                    world,
                    tCenter.getX() * ((double) j / taskNum.get(currentTaskID)) + simulationCenter.getX() * (1 - (double) j / taskNum.get(currentTaskID)),
                    baseY,
                    tCenter.getZ() * ((double) j / taskNum.get(currentTaskID)) + simulationCenter.getZ() * (1 - (double) j / taskNum.get(currentTaskID))
            );

            simulationCenter = newCenter;
            simulatedSize = Math.max(originSize * borderChangeTasks.get(finalI).size, 1D);

            int finalJ = j;
            int finalCurrentTaskID = currentTaskID;
            Bukkit.getScheduler().runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("MeowBattle")), new Runnable() {
                @Override
                public void run() {
                    if(!isRunning) return;
                    if(finalJ == 1) {
                        Bukkit.broadcastMessage("Start ring closing...");
                        state = borderChangeState.CLOSING;
                    } else if(finalJ == taskNum.get(finalCurrentTaskID) - 1) {
                        Bukkit.broadcastMessage("Ring closed!");
                        state = borderChangeState.CLOSED;
                    }
                    if(finalCurrentTaskID == originTasks.size() - 1 && finalJ == taskNum.get(finalCurrentTaskID) - 1) {
                        Bukkit.broadcastMessage("Final ring closed!");
                        state = borderChangeState.FINAL;
                    }
                    startBorderChangeTask(finalI, newCenter);
                }
            }, (long) ((borderChangeTasks.get(finalI).startTime) * 20));
        }
    }

    public void stopAllActivities() {
        for(Player player : world.getPlayers()) {
            cancelDamageOverTime(player);
        }
        renderTask.cancel();
        soundManagerTask.cancel();
        isRunning = false;
        state = borderChangeState.FINAL;
    }

    public void destroy() {
        stopAllActivities();
        worldBorder.reset();
        worldBorder.setDamageAmount(0);
        worldBorder.setDamageBuffer(0);
        worldBorder.setWarningDistance(0);
        worldBorder.setWarningTime(0);
        worldBorder.setDamageAmount(0);
        wall1.remove();
        wall2.remove();
        wall3.remove();
        wall4.remove();
    }

    public borderChangeState getState() {
        return state;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public double getBaseY() {
        return baseY;
    }

    public void setBaseY(double baseY) {
        this.baseY = baseY;
    }


}
