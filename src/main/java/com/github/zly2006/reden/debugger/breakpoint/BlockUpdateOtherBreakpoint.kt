package com.github.zly2006.reden.debugger.breakpoint

import com.github.zly2006.reden.Reden
import com.github.zly2006.reden.debugger.stages.block.AbstractBlockUpdateStage
import net.minecraft.text.Text

class BlockUpdateOtherBreakpoint(id: Int) : BlockUpdateEvent(id, Companion) {
    companion object: BreakPointType {
        override val id = Reden.identifier("block_update_other")
        override val description = Text.literal("BlockUpdateOther")
        override fun create(id: Int) = BlockUpdateOtherBreakpoint(id)
    }

    override fun call(event: Any) {
        if ((event as AbstractBlockUpdateStage<*>).sourcePos == pos) {
            super.call(event)
        }
    }
}
