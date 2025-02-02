package net.aspw.client.script.api

import jdk.nashorn.api.scripting.JSObject
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.ClientUtils
import net.aspw.client.value.Value

@Suppress("unused")
@ModuleInfo(name = "ScriptModule", description = "Empty", category = ModuleCategory.OTHER)
class ScriptModule(private val moduleObject: JSObject) : Module() {

    private val events = HashMap<String, JSObject>()
    private val _values = LinkedHashMap<String, Value<*>>()
    private var _tag: String? = null

    /**
     * Allows the user to access values by typing module.settings.<valuename>
     */
    val settings by lazy { _values }

    init {
        name = moduleObject.getMember("name") as String
        description = moduleObject.getMember("description") as String

        if (moduleObject.hasMember("spacedName"))
            spacedName = moduleObject.getMember("spacedName") as String
        else
            spacedName = name

        val categoryString = moduleObject.getMember("category") as String
        for (category in ModuleCategory.values())
            if (categoryString.equals(category.displayName, true))
                this.category = category

        if (moduleObject.hasMember("settings")) {
            val settings = moduleObject.getMember("settings") as JSObject

            for (settingName in settings.keySet())
                _values[settingName] = settings.getMember(settingName) as Value<*>
        }

        if (moduleObject.hasMember("tag"))
            _tag = moduleObject.getMember("tag") as String
    }

    override val values: List<Value<*>>
        get() {
            return _values.values.toList()
        }

    override var tag: String?
        get() = _tag
        set(value) {
            _tag = value
        }

    /**
     * Called from inside the script to register a new event handler.
     * @param eventName Name of the event.
     * @param handler JavaScript function used to handle the event.
     */
    fun on(eventName: String, handler: JSObject) {
        events[eventName] = handler
    }

    override fun onEnable() = callEvent("enable")

    override fun onDisable() = callEvent("disable")

    @EventTarget
    fun onUpdate(updateEvent: UpdateEvent) = callEvent("update")

    @EventTarget
    fun onMotion(motionEvent: MotionEvent) = callEvent("motion", motionEvent)

    @EventTarget
    fun onRender2D(render2DEvent: Render2DEvent) = callEvent("render2D", render2DEvent)

    @EventTarget
    fun onRender3D(render3DEvent: Render3DEvent) = callEvent("render3D", render3DEvent)

    @EventTarget
    fun onPacket(packetEvent: PacketEvent) = callEvent("packet", packetEvent)

    @EventTarget
    fun onJump(jumpEvent: JumpEvent) = callEvent("jump", jumpEvent)

    @EventTarget
    fun onAttack(attackEvent: AttackEvent) = callEvent("attack", attackEvent)

    @EventTarget
    fun onKey(keyEvent: KeyEvent) = callEvent("key", keyEvent)

    @EventTarget
    fun onMove(moveEvent: MoveEvent) = callEvent("move", moveEvent)

    @EventTarget
    fun onStep(stepEvent: StepEvent) = callEvent("step", stepEvent)

    @EventTarget
    fun onStepConfirm(stepConfirmEvent: StepConfirmEvent) = callEvent("stepConfirm")

    @EventTarget
    fun onWorld(worldEvent: WorldEvent) = callEvent("world", worldEvent)

    @EventTarget
    fun onSession(sessionEvent: SessionEvent) = callEvent("session")

    @EventTarget
    fun onClickBlock(clickBlockEvent: ClickBlockEvent) = callEvent("clickBlock", clickBlockEvent)

    @EventTarget
    fun onStrafe(strafeEvent: StrafeEvent) = callEvent("strafe", strafeEvent)

    @EventTarget
    fun onSlowDown(slowDownEvent: SlowDownEvent) = callEvent("slowDown", slowDownEvent)

    /**
     * Calls the handler of a registered event.
     * @param eventName Name of the event to be called.
     * @param payload Event data passed to the handler function.
     */
    private fun callEvent(eventName: String, payload: Any? = null) {
        try {
            events[eventName]?.call(moduleObject, payload)
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("[ScriptAPI] Exception in module '$name'!", throwable)
        }
    }
}