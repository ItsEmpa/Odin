package me.odinclient.features.impl.skyblock

import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.settings.Setting.Companion.withDependency
import me.odinmain.features.settings.impl.BooleanSetting
import me.odinmain.features.settings.impl.NumberSetting
import me.odinmain.utils.ServerUtils.getPing
import me.odinmain.utils.skyblock.dungeon.DungeonUtils
import me.odinmain.utils.skyblock.dungeon.M7Phases
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object HidePlayers : Module(
    name = "Hide Players",
    description = "Hides players in your vicinity.",
    category = Category.SKYBLOCK
) {
    private val hideAll: Boolean by BooleanSetting("Hide all", default = false, false, "Hides all players, regardless of distance.")
    private val distance: Double by NumberSetting("distance", 3.0, 0.0, 32.0, .5, false, "The number of blocks away to hide players.").withDependency { !hideAll }
    private val clickThrough: Boolean by BooleanSetting("Click Through", default = false, false, "Allows clicking through players.")
    private val onlyDevs: Boolean by BooleanSetting("only at Devs", default = false, false, "Only hides players when standing at ss or fourth device.")

    @SubscribeEvent
    fun onRenderEntity(event: RenderPlayerEvent.Pre) {
        if (mc.isSingleplayer) return
        val atDevs = (mc.thePlayer.getDistance(108.63, 120.0, 94.0) <= 1.8 || mc.thePlayer.getDistance(63.5, 127.0, 35.5) <= 1.8) && DungeonUtils.getF7Phase() == M7Phases.P3
        if (event.entity.getPing() != 1 || clickThrough || event.entity == mc.thePlayer || (!atDevs && onlyDevs)) return
        if (event.entity.getDistanceToEntity(mc.thePlayer) <= distance || hideAll) event.isCanceled = true
    }

    @SubscribeEvent
    fun onPosUpdate(event: LivingEvent.LivingUpdateEvent) {
        if (mc.isSingleplayer) return
        val atDevs = (mc.thePlayer.getDistance(108.63, 120.0, 94.0) <= 1.8 || mc.thePlayer.getDistance(63.5, 127.0, 35.5) <= 1.8) && DungeonUtils.getF7Phase() == M7Phases.P3
        if (event.entity.getPing() != 1 || !clickThrough || event.entity == mc.thePlayer || (!atDevs && onlyDevs)) return
        if (event.entity.getDistanceToEntity(mc.thePlayer) <= distance || hideAll) {
            event.entity.posX = 9999999.0
            event.isCanceled = true
        }
    }
}