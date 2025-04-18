package org.powernukkitx.vanillagen.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import org.powernukkitx.vanillagen.packet.objects.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.*;

public class HandleByteBuf extends ByteBuf {

    protected final ByteBuf buf;

    public static HandleByteBuf of(ByteBuf buf) {
        return new HandleByteBuf(buf);
    }

    protected HandleByteBuf(ByteBuf buf) {
        this.buf = ObjectUtil.checkNotNull(buf, "buf");
    }

    @Override
    public final boolean hasMemoryAddress() {
        return buf.hasMemoryAddress();
    }

    @Override
    public boolean isContiguous() {
        return buf.isContiguous();
    }

    @Override
    public final long memoryAddress() {
        return buf.memoryAddress();
    }

    @Override
    public final int capacity() {
        return buf.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        buf.capacity(newCapacity);
        return this;
    }

    @Override
    public final int maxCapacity() {
        return buf.maxCapacity();
    }

    @Override
    public final ByteBufAllocator alloc() {
        return buf.alloc();
    }

    @Override
    public final ByteOrder order() {
        return buf.order();
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        return buf.order(endianness);
    }

    @Override
    public final ByteBuf unwrap() {
        return buf;
    }

    @Override
    public ByteBuf asReadOnly() {
        return buf.asReadOnly();
    }

    @Override
    public boolean isReadOnly() {
        return buf.isReadOnly();
    }

    @Override
    public final boolean isDirect() {
        return buf.isDirect();
    }

    @Override
    public final int readerIndex() {
        return buf.readerIndex();
    }

    @Override
    public final ByteBuf readerIndex(int readerIndex) {
        buf.readerIndex(readerIndex);
        return this;
    }

    @Override
    public final int writerIndex() {
        return buf.writerIndex();
    }

    @Override
    public final ByteBuf writerIndex(int writerIndex) {
        buf.writerIndex(writerIndex);
        return this;
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        buf.setIndex(readerIndex, writerIndex);
        return this;
    }

    @Override
    public final int readableBytes() {
        return buf.readableBytes();
    }

    @Override
    public final int writableBytes() {
        return buf.writableBytes();
    }

    @Override
    public final int maxWritableBytes() {
        return buf.maxWritableBytes();
    }

    @Override
    public int maxFastWritableBytes() {
        return buf.maxFastWritableBytes();
    }

    @Override
    public final boolean isReadable() {
        return buf.isReadable();
    }

    @Override
    public final boolean isWritable() {
        return buf.isWritable();
    }

    @Override
    public final ByteBuf clear() {
        buf.clear();
        return this;
    }

    @Override
    public final ByteBuf markReaderIndex() {
        buf.markReaderIndex();
        return this;
    }

    @Override
    public final ByteBuf resetReaderIndex() {
        buf.resetReaderIndex();
        return this;
    }

    @Override
    public final ByteBuf markWriterIndex() {
        buf.markWriterIndex();
        return this;
    }

    @Override
    public final ByteBuf resetWriterIndex() {
        buf.resetWriterIndex();
        return this;
    }

    @Override
    public ByteBuf discardReadBytes() {
        buf.discardReadBytes();
        return this;
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        buf.discardSomeReadBytes();
        return this;
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        buf.ensureWritable(minWritableBytes);
        return this;
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return buf.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        return buf.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return buf.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return buf.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return buf.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return buf.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return buf.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return buf.getUnsignedShortLE(index);
    }

    @Override
    public int getMedium(int index) {
        return buf.getMedium(index);
    }

    @Override
    public int getMediumLE(int index) {
        return buf.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return buf.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return buf.getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(int index) {
        return buf.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return buf.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return buf.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return buf.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int index) {
        return buf.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return buf.getLongLE(index);
    }

    @Override
    public char getChar(int index) {
        return buf.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return buf.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return buf.getDouble(index);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        buf.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        buf.getBytes(index, dst, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        buf.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        buf.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        buf.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        buf.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        buf.getBytes(index, out, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return buf.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return buf.getBytes(index, out, position, length);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return buf.getCharSequence(index, length, charset);
    }

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        buf.setBoolean(index, value);
        return this;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        buf.setByte(index, value);
        return this;
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        buf.setShort(index, value);
        return this;
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        buf.setShortLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        buf.setMedium(index, value);
        return this;
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        buf.setMediumLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        buf.setInt(index, value);
        return this;
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        buf.setIntLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        buf.setLong(index, value);
        return this;
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        buf.setLongLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        buf.setChar(index, value);
        return this;
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        buf.setFloat(index, value);
        return this;
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        buf.setDouble(index, value);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        buf.setBytes(index, src);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        buf.setBytes(index, src, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        buf.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        buf.setBytes(index, src);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        buf.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        buf.setBytes(index, src);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return buf.setBytes(index, in, position, length);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        buf.setZero(index, length);
        return this;
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return buf.setCharSequence(index, sequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return buf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return buf.readShort();
    }

    @Override
    public short readShortLE() {
        return buf.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return buf.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return buf.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return buf.readMedium();
    }

    @Override
    public int readMediumLE() {
        return buf.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return buf.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return buf.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return buf.readInt();
    }

    @Override
    public int readIntLE() {
        return buf.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return buf.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return buf.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return buf.readLong();
    }

    @Override
    public long readLongLE() {
        return buf.readLongLE();
    }

    @Override
    public char readChar() {
        return buf.readChar();
    }

    @Override
    public float readFloat() {
        return buf.readFloat();
    }

    @Override
    public double readDouble() {
        return buf.readDouble();
    }

    @Override
    public ByteBuf readBytes(int length) {
        return buf.readBytes(length);
    }

    @Override
    public ByteBuf readSlice(int length) {
        return buf.readSlice(length);
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return buf.readRetainedSlice(length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        buf.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        buf.readBytes(dst, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        buf.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        buf.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        buf.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        buf.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        buf.readBytes(out, length);
        return this;
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return buf.readBytes(out, length);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return buf.readBytes(out, position, length);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return buf.readCharSequence(length, charset);
    }

    @Override
    public ByteBuf skipBytes(int length) {
        buf.skipBytes(length);
        return this;
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        buf.writeBoolean(value);
        return this;
    }

    @Override
    public ByteBuf writeByte(int value) {
        buf.writeByte(value);
        return this;
    }

    @Override
    public ByteBuf writeShort(int value) {
        buf.writeShort(value);
        return this;
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        buf.writeShortLE(value);
        return this;
    }

    @Override
    public ByteBuf writeMedium(int value) {
        buf.writeMedium(value);
        return this;
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        buf.writeMediumLE(value);
        return this;
    }

    @Override
    public ByteBuf writeInt(int value) {
        buf.writeInt(value);
        return this;
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        buf.writeIntLE(value);
        return this;
    }

    @Override
    public ByteBuf writeLong(long value) {
        buf.writeLong(value);
        return this;
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        buf.writeLongLE(value);
        return this;
    }

    @Override
    public ByteBuf writeChar(int value) {
        buf.writeChar(value);
        return this;
    }

    @Override
    public ByteBuf writeFloat(float value) {
        buf.writeFloat(value);
        return this;
    }

    @Override
    public ByteBuf writeDouble(double value) {
        buf.writeDouble(value);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        buf.writeBytes(src);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        buf.writeBytes(src, length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        buf.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        buf.writeBytes(src);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        buf.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        buf.writeBytes(src);
        return this;
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return buf.writeBytes(in, position, length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        buf.writeZero(length);
        return this;
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return buf.writeCharSequence(sequence, charset);
    }

    public String readString() {
        byte[] bytes = new byte[this.readUnsignedVarInt()];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public ByteBuf writeString(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        this.writeUnsignedVarInt(bytes.length);
        return buf.writeBytes(bytes);
    }

    public <T> void writeNotNull(T data, Consumer<T> consumer) {
        boolean present = data != null;
        writeBoolean(present);
        if (present) {
            consumer.accept(data);
        }
    }

    public <T> void writeOptional(Optional<T> data, Consumer<T> consumer) {
        boolean present = data.isPresent();
        writeBoolean(present);
        if (present) {
            consumer.accept(data.get());
        }
    }

    public <T> T readOptional(T nonPresentValue, Supplier<T> valueReader) {
        boolean isPresent = this.readBoolean();
        if (isPresent) {
            return valueReader.get();
        }
        return nonPresentValue;
    }

    public byte[] readByteArray() {
        byte[] bytes = new byte[readUnsignedVarInt()];
        readBytes(bytes);
        return bytes;
    }

    public void writeByteArray(byte[] bytes) {
        writeUnsignedVarInt(bytes.length);
        this.writeBytes(bytes);
    }

    public void writeUnsignedVarInt(int value) {
        ByteBufVarInt.writeUnsignedInt(this, value);
    }

    public int readUnsignedVarInt() {
        return ByteBufVarInt.readUnsignedInt(this);
    }

    public void writeVarInt(int value) {
        ByteBufVarInt.writeInt(this, value);
    }

    public int readVarInt() {
        return ByteBufVarInt.readInt(this);
    }

    public void writeUnsignedVarLong(long value) {
        ByteBufVarInt.writeUnsignedLong(this, value);
    }

    public long readUnsignedVarLong() {
        return ByteBufVarInt.readUnsignedLong(this);
    }

    public void writeUnsignedBigVarInt(BigInteger value) {
        ByteBufVarInt.writeUnsignedBigVarInt(this, value);
    }

    public BigInteger readUnsignedBigVarInt(int length) {
        return ByteBufVarInt.readUnsignedBigVarInt(this, length);
    }

    public void writeVarLong(long value) {
        ByteBufVarInt.writeLong(this, value);
    }

    public long readVarLong() {
        return ByteBufVarInt.readLong(this);
    }

    public <T> void writeArray(Collection<T> collection, Consumer<T> writer) {
        if (collection == null || collection.isEmpty()) {
            writeUnsignedVarInt(0);
            return;
        }
        writeUnsignedVarInt(collection.size());
        collection.forEach(writer);
    }

    public <T> void writeArray(T[] collection, Consumer<T> writer) {
        if (collection == null || collection.length == 0) {
            writeUnsignedVarInt(0);
            return;
        }
        writeUnsignedVarInt(collection.length);
        for (T t : collection) {
            writer.accept(t);
        }
    }

    public <T> void writeArray(Collection<T> array, BiConsumer<HandleByteBuf, T> biConsumer) {
        this.writeUnsignedVarInt(array.size());
        for (T val : array) {
            biConsumer.accept(this, val);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T[] readArray(Class<T> clazz, Function<HandleByteBuf, T> function) {
        ArrayDeque<T> deque = new ArrayDeque<>();
        int count = readUnsignedVarInt();
        for (int i = 0; i < count; i++) {
            deque.add(function.apply(this));
        }
        return deque.toArray((T[]) Array.newInstance(clazz, 0));
    }

    public BlockData readBlockData() {
        BlockData data = new BlockData();
        data.decode(this);
        return data;
    }

    public ChunkData readChunkData() {
        ChunkData data = new ChunkData();
        data.decode(this);
        return data;
    }

    public ChunkInfo readChunkVector() {
        ChunkInfo data = new ChunkInfo();
        data.decode(this);
        return data;
    }

    public LevelPlayerPosition readLevelPlayerPosition() {
        LevelPlayerPosition data = new LevelPlayerPosition();
        data.decode(this);
        return data;
    }

    public EntityData readEntityData() {
        EntityData data = new EntityData();
        data.decode(this);
        return data;
    }

    public BlockEntityData readBlockEntityData() {
        BlockEntityData data = new BlockEntityData();
        data.decode(this);
        return data;
    }

    public EnchantmentData readEnchantmentData() {
        EnchantmentData data = new EnchantmentData();
        data.decode(this);
        return data;
    }

    public ItemData readItemData() {
        ItemData data = new ItemData();
        data.decode(this);
        return data;
    }

    public <T> void readArray(Collection<T> array, Function<HandleByteBuf, T> function) {
        readArray(array, HandleByteBuf::readUnsignedVarInt, function);
    }

    public <T> void readArray(Collection<T> array, ToLongFunction<HandleByteBuf> lengthReader, Function<HandleByteBuf, T> function) {
        long length = lengthReader.applyAsLong(this);
        for (int i = 0; i < length; i++) {
            array.add(function.apply(this));
        }
    }


    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return buf.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return buf.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return buf.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return buf.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return buf.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return buf.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return buf.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return buf.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuf copy() {
        return buf.copy();
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return buf.copy(index, length);
    }

    @Override
    public ByteBuf slice() {
        return buf.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return buf.retainedSlice();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return buf.slice(index, length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return buf.retainedSlice(index, length);
    }

    @Override
    public ByteBuf duplicate() {
        return buf.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return buf.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return buf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return buf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return buf.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return buf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return buf.nioBuffers(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return buf.internalNioBuffer(index, length);
    }

    @Override
    public boolean hasArray() {
        return buf.hasArray();
    }

    @Override
    public byte[] array() {
        return buf.array();
    }

    @Override
    public int arrayOffset() {
        return buf.arrayOffset();
    }

    @Override
    public String toString(Charset charset) {
        return buf.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return buf.toString(index, length, charset);
    }

    @Override
    public int hashCode() {
        return buf.hashCode();
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return buf.equals(obj);
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return buf.compareTo(buffer);
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '(' + buf.toString() + ')';
    }

    @Override
    public ByteBuf retain(int increment) {
        buf.retain(increment);
        return this;
    }

    @Override
    public ByteBuf retain() {
        buf.retain();
        return this;
    }

    @Override
    public ByteBuf touch() {
        buf.touch();
        return this;
    }

    @Override
    public ByteBuf touch(Object hint) {
        buf.touch(hint);
        return this;
    }

    @Override
    public final boolean isReadable(int size) {
        return buf.isReadable(size);
    }

    @Override
    public final boolean isWritable(int size) {
        return buf.isWritable(size);
    }

    @Override
    public final int refCnt() {
        return buf.refCnt();
    }

    @Override
    public boolean release() {
        return buf.release();
    }

    @Override
    public boolean release(int decrement) {
        return buf.release(decrement);
    }
}
