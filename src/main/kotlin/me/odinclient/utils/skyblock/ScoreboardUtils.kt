package me.odinclient.utils.skyblock

import me.odinclient.OdinClient.Companion.mc
import me.odinclient.utils.Utils.noControlCodes
import net.minecraft.scoreboard.ScorePlayerTeam

object ScoreboardUtils {

    fun cleanSB(scoreboard: String?): String {
        return scoreboard.noControlCodes.toCharArray().filter { it.code in 21..126 }.joinToString("")
    }

    val sidebarLines: List<String>
        get() {
            val scoreboard = mc.theWorld?.scoreboard ?: return emptyList()
            val objective = scoreboard.getObjectiveInDisplaySlot(1) ?: return emptyList()

            return scoreboard.getSortedScores(objective)
                .filter { it?.playerName?.startsWith("#") == false }
                .let { if (it.size > 15) it.drop(15) else it }
                .map { ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(it.playerName), it.playerName) }
        }
}