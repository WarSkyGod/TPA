package top.craft_hello.tpa.objects;


import cn.handyplus.lib.adapter.HandyRunnable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.enums.RequestType;

public class Request {
    private final RequestType REQUEST_TYPE;
    private final Player requestPlayer;
    private String target;
    private HandyRunnable timer;

    public Request(@NotNull RequestType REQUEST_TYPE, @NotNull Player requestPlayer, @NotNull String target, @NotNull HandyRunnable timer) {
        this.REQUEST_TYPE = REQUEST_TYPE;
        this.requestPlayer = requestPlayer;
        this.target = target;
        this.timer = timer;
    }

    public RequestType getREQUEST_TYPE() {
        return REQUEST_TYPE;
    }

    public Player getRequestPlayer() {
        return requestPlayer;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public HandyRunnable getTimer() {
        return timer;
    }

    public void setTimer(HandyRunnable timer) {
        this.timer = timer;
    }
}
