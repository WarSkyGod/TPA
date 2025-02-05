package top.craft_hello.tpa.objects;

import cn.handyplus.lib.adapter.PlayerSchedulerUtil;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.utils.SendMessageUtil;
import top.craft_hello.tpa.abstracts.PlayerToPlayerRequest;
import top.craft_hello.tpa.enums.CommandType;
import top.craft_hello.tpa.enums.TimerType;
import top.craft_hello.tpa.exceptions.OfflineOrNullErrorException;

import static java.util.Objects.isNull;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;


public class TpaRequest extends PlayerToPlayerRequest {
    public TpaRequest(CommandSender requestObject, String[] args) {
        super(requestObject, args, CommandType.TPA);
        setTimer((delay < 0L ? 30000L : delay * 1000L), TimerType.DENY);
        REQUEST_QUEUE.put(targetPlayer, this);
        SendMessageUtil.requestTeleportToTarget(requestPlayer, targetPlayer, String.valueOf((delay < 0L ? 30L : delay)));
    }

    @Override
    protected void teleport() {
        super.teleport();
        checkError();
        if (getConfig().isEnableTitleMessage()) {
            SendMessageUtil.titleCountdownOverMessage(requestPlayer, targetPlayerName);
            if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(requestPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }
        SendMessageUtil.youTeleportedToMessage(requestPlayer, targetPlayerName);
        teleport(requestPlayer, targetPlayer.getLocation());
        if (getConfig().isEnableCommandDelay(requestPlayer)) setCommandTimer(requestPlayer, getConfig().getCommandDelay(requestPlayer));
    }

    public void checkError() {
        if (isNull(requestPlayer) || !requestPlayer.isOnline()) throw new OfflineOrNullErrorException(requestPlayer);
        if (isNull(targetPlayer) || !targetPlayer.isOnline()) throw new OfflineOrNullErrorException(requestPlayer);
    }

    @Override
    public void tpaccept() {
        super.tpaccept();
        checkError();
        SendMessageUtil.acceptMessage(requestPlayer, targetPlayer);
        if (!getConfig().isEnableTeleportDelay(requestPlayer)) {
            teleport();
            return;
        }
        setCheckMoveTimer(requestPlayer.getLocation(), requestPlayer, targetPlayer);
        setCountdownMessageTimer(requestPlayer, targetPlayerName);
        setTimer((delay < 0L ? 3000L : delay * 1000L), TimerType.TELEPORT);
    }

    @Override
    public void tpdeny(){
        super.tpdeny();
        SendMessageUtil.denyMessage(requestPlayer, targetPlayer);
    }

    @Override
    protected void timeOverDeny(){
        super.timeOverDeny();
        SendMessageUtil.timeOverDeny(requestPlayer, targetPlayer);
    }
}
