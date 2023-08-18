package com.github.zly2006.reden.rvc

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

fun select(world: World, pos: BlockPos) {

}

class Detector {
    class KeyPoint(
        var mode: Mode,
        var pos: BlockPos
    ) {
        enum class Mode(
            val manhattanDistance: Int,
            val shouldSame: Boolean,
        ) {
            Self(0, false),
            Same(1, true),
            Connected(1, false),
            QC(2, false),
            Update(3, false)
        }
    }
    val trackingPos: MutableSet<KeyPoint> = hashSetOf()
    val ignoredPose: MutableSet<KeyPoint> = hashSetOf()

    fun isTracking() {

    }
}
