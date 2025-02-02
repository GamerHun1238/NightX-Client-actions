package net.aspw.client.util;

import com.google.gson.JsonObject;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

/**
 * The type Client utils.
 */
public final class ClientUtils extends MinecraftInstance {

    private static final Logger logger = LogManager.getLogger("Client");

    private static Field fastRenderField;

    static {
        try {
            fastRenderField = GameSettings.class.getDeclaredField("ofFastRender");

            if (!fastRenderField.isAccessible())
                fastRenderField.setAccessible(true);
        } catch (final NoSuchFieldException ignored) {
        }
    }

    /**
     * Gets logger.
     *
     * @return the logger
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Disable fast render.
     */
    public static void disableFastRender() {
        try {
            if (fastRenderField != null) {
                if (!fastRenderField.isAccessible())
                    fastRenderField.setAccessible(true);

                fastRenderField.setBoolean(mc.gameSettings, false);
            }
        } catch (final IllegalAccessException ignored) {
        }
    }

    /**
     * Display chat message.
     *
     * @param message the message
     */
    public static void displayChatMessage(final String message) {
        if (mc.thePlayer == null) {
            getLogger().info("(MCChat)" + message);
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", message);

        mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()));
    }
}