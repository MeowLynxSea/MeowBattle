package cn.meowdream.meowBattle.battleWorld;

public class borderChangeTask {
    public double startTime;
    public double duration; // in seconds
    public double size; // in percentage
    public double damagePerSecond;

    public borderChangeTask(double startTime, double duration, double size, double damagePerSecond) {
        this.startTime = startTime;
        this.duration = duration;
        this.size = size;
        this.damagePerSecond = damagePerSecond;
    }
}
