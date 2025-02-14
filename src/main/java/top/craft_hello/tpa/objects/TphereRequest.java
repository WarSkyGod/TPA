package top.craft_hello.tpa.objects;

import cn.handyplus.lib.adapter.PlayerSchedulerUtil;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.utils.SendMessageUtil;
import top.craft_hello.tpa.abstracts.PlayerToPlayerRequest;
import top.craft_hello.tpa.enums.CommandType;
import top.craft_hello.tpa.enums.TimerType;
import top.craft_hello.tpa.exceptions.ErrorTargetOfflineException;

import static java.util.Objects.isNull;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;


public class TphereRequest extends PlayerToPlayerRequest {
    public TphereRequest(CommandSender requestObject, String[] args)  {
        super(requestObject, args, CommandType.TP_HERE);
        setTimer((delay < 0L ? 30000L : delay * 1000L), TimerType.DENY);
        REQUEST_QUEUE.put(targetPlayer, this);
        SendMessageUtil.requestTargetTeleportToHere(requestPlayer, targetPlayer, String.valueOf((delay < 0L ? 30L : delay)));
    }

    @Override
    protected void teleport()  {
        super.teleport();
        checkError();
        if (getConfig().isEnableTitleMessage()) {
            SendMessageUtil.titleCountdownOverMessage(targetPlayer, requestPlayerName);
            if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(targetPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }
        SendMessageUtil.youTeleportedToMessage(targetPlayer, requestPlayerName);
        teleport(targetPlayer, requestPlayer.getLocation());
        if (getConfig().isEnableCommandDelay(requestPlayer)) setCommandTimer(requestPlayer, getConfig().getCommandDelay(requestPlayer));
    }

    public void checkError()  {
        if (isNull(requestPlayer) || !requestPlayer.isOnline()) throw new ErrorTargetOfflineException(requestPlayer, "null");
        if (isNull(targetPlayer) || !targetPlayer.isOnline()) throw new ErrorTargetOfflineException(requestPlayer, targetPlayerName);
    }

    @Override
    public void tpaccept()  {
        super.tpaccept();
        checkError();
        SendMessageUtil.acceptMessage(targetPlayer, requestPlayer);
        if (!getConfig().isEnableTeleportDelay(requestPlayer)) {
            teleport();
            return;
        }
        setCheckMoveTimer(targetPlayer.getLocation(), targetPlayer, requestPlayer);
        setCountdownMessageTimer(targetPlayer, requestPlayerName);
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
