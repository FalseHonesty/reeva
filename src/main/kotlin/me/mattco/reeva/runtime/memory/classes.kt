package me.mattco.reeva.runtime.memory

import me.mattco.reeva.core.Agent
import me.mattco.reeva.runtime.annotations.ECMAImpl
import me.mattco.reeva.utils.expect

data class CandidateExecution(
    val eventRecords: List<AgentEventRecord>,
    val chosenValues: List<ChosenValueRecord>,
) {
    @ECMAImpl("28.5.1")
    val eventSet: List<SharedDataBlockEvent>
        get() = eventRecords.flatMap { it.eventList }

    @ECMAImpl("28.5.2")
    val sharedDataBlockEventSet: List<SharedDataBlockEvent>
        get() = eventSet

    @ECMAImpl("28.5.3")
    val hostEventSet: List<Event>
        get() = emptyList()
}

data class AgentEventRecord(
    val agent: Agent,
    val eventList: List<SharedDataBlockEvent>,
    val agentSynchronizesWith: List<SynchronizeEvent>,
)

class SynchronizeEvent

data class ChosenValueRecord(
    val event: SharedDataBlockEvent,
    val chosenValue: List<Byte>,
)

abstract class Event

sealed class SharedDataBlockEvent(
    val order: Order,
    val noTear: Boolean,
    val block: SharedDataBlock,
    val byteIndex: Int,
    val elementSize: Int,
) : Event() {
    enum class Order {
        SeqCst,
        Unordered,
        Init,
    }
}

class ReadSharedMemoryEvent(
    order: Order,
    noTear: Boolean,
    block: SharedDataBlock,
    byteIndex: Int,
    elementSize: Int,
) : SharedDataBlockEvent(order, noTear, block, byteIndex, elementSize) {
    init {
        expect(order != Order.Init)
    }
}

class WriteSharedMemoryEvent(
    order: Order,
    noTear: Boolean,
    block: SharedDataBlock,
    byteIndex: Int,
    elementSize: Int,
) : SharedDataBlockEvent(order, noTear, block, byteIndex, elementSize) {
    lateinit var payload: ByteArray
}

class ReadModifyWriteSharedMemory(
    order: SharedDataBlockEvent.Order,
    noTear: Boolean,
    block: SharedDataBlock,
    byteIndex: Int,
    elementSize: Int,
    val modifyOp: () -> Unit,
) {
    lateinit var payload: ByteArray
}
