package net.aspw.client.features.module.impl.movement.speeds.ncp;

import net.aspw.client.Client;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.features.module.impl.player.Scaffold;
import net.aspw.client.util.MovementUtils;

/**
 * The type Ncp hop.
 */
public class NCPHop extends SpeedMode {

    /**
     * Instantiates a new Ncp hop.
     */
    public NCPHop() {
        super("NCPHop");
    }

    @Override
    public void onEnable() {
        mc.timer.timerSpeed = 1.0865F;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
        super.onDisable();

        final Scaffold scaffold = Client.moduleManager.getModule(Scaffold.class);

        if (!mc.thePlayer.isSneaking() && !scaffold.getState()) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }
    }

    @Override
    public void onMotion() {
    }

    @Override
    public void onUpdate() {
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                mc.thePlayer.jumpMovementFactor = 0.0223F;
            }

            MovementUtils.strafe();
        }
    }

    @Override
    public void onMove(MoveEvent event) {

    }
}