package net.aspw.client.features.module.impl.movement.speeds.spectre;

import net.aspw.client.Client;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.features.module.impl.player.Scaffold;
import net.aspw.client.util.MovementUtils;

/**
 * The type Spectre b hop.
 */
public class SpectreBHop extends SpeedMode {

    /**
     * Instantiates a new Spectre b hop.
     */
    public SpectreBHop() {
        super("SpectreBHop");
    }

    @Override
    public void onMotion() {
        if (!MovementUtils.isMoving() || mc.thePlayer.movementInput.jump)
            return;

        if (mc.thePlayer.onGround) {
            MovementUtils.strafe(1.1F);
            mc.thePlayer.motionY = 0.44D;
            return;
        }

        MovementUtils.strafe();
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onDisable() {
        final Scaffold scaffold = Client.moduleManager.getModule(Scaffold.class);

        if (!mc.thePlayer.isSneaking() && !scaffold.getState()) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
