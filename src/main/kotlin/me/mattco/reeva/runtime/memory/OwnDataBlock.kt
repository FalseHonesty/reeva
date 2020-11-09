package me.mattco.reeva.runtime.memory

import java.nio.ByteBuffer

@OptIn(ExperimentalUnsignedTypes::class)
class OwnDataBlock(size: Int) : DataBlock {
    private val wrapped = ByteBuffer.wrap(ByteArray(size))

    override fun getInt8(index: Int) = wrapped.get(index)

    override fun getInt16(index: Int) = wrapped.getShort(index)

    override fun getInt32(index: Int) = wrapped.getInt(index)

    override fun setInt8(index: Int, value: Byte) {
        wrapped.put(index, value)
    }

    override fun setInt16(index: Int, value: Short) {
        wrapped.putShort(index, value)
    }

    override fun setInt32(index: Int, value: Int) {
        wrapped.putInt(index, value)
    }
}
