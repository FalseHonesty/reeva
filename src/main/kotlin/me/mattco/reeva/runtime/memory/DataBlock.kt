package me.mattco.reeva.runtime.memory

@OptIn(ExperimentalUnsignedTypes::class)
interface DataBlock {
    fun getInt8(index: Int): Byte

    fun getUint8(index: Int): UByte = getInt8(index).toUByte()

    fun getInt16(index: Int): Short

    fun getUint16(index: Int): UShort = getInt16(index).toUShort()

    fun getInt32(index: Int): Int

    fun getUint32(index: Int): UInt = getInt32(index).toUInt()

    operator fun get(index: Int) = getInt8(index)

    fun setInt8(index: Int, value: Byte)

    fun setUint8(index: Int, value: UByte) = setInt8(index, value.toByte())

    fun setInt16(index: Int, value: Short)

    fun setUint16(index: Int, value: UShort) = setInt16(index, value.toShort())

    fun setInt32(index: Int, value: Int)

    fun setUint16(index: Int, value: UInt) = setInt32(index, value.toInt())

    operator fun set(index: Int, value: Byte) = setInt8(index, value)
}
