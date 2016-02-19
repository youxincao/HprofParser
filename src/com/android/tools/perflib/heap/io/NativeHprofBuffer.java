package com.android.tools.perflib.heap.io;

import java.io.File;
import java.io.IOException;

/**
 * Created by weilun on 16/2/17.
 */
public class NativeHprofBuffer implements HprofBuffer {

    private long mCurPosition;
    private long mLength;

    static {
        System.loadLibrary("nativeHprofBuffer");
    }

    public NativeHprofBuffer(File file) throws IOException {

        String filePath = file.getAbsolutePath();

        mLength = mapFileToMemory(filePath);
        if (mLength == -1) {
            return;
        }
    }

    /**
     * 将文件读到内存中
     *
     * @param filePath 需要的文件句柄
     * @return 文件的大小, 如果出错返回-1
     * @implNote 也许使用文件路径会比较合适, 但是为了兼容LeakCananry的用法使用文件句柄
     */
    native long mapFileToMemory(String filePath) throws IOException;

    /**
     * 释放native内存
     */
    native int releaseNativeMemory() throws IOException;

    /**
     * 从指定的位置读取一个byte
     *
     * @param pos 指定的位置
     * @return 读取到的值
     */
    native byte nativeReadByte(long pos);

    /**
     * 从指定的位置读取一个char
     *
     * @param pos 指定的位置
     * @return 读取到的值
     */
    native char nativeReadChar(long pos);

    /**
     * 从指定的位置读取一个short
     *
     * @param pos 指定的位置
     * @return 读取到的值
     */
    native short nativeReadShort(long pos);

    /**
     * 从指定的位置读取一个int
     *
     * @param pos 指定的位置
     * @return 读取到的值
     */
    native int nativeReadInt(long pos);

    /**
     * 从指定的位置读取一个long
     *
     * @param pos 指定的位置
     * @return 读取到的值
     */
    native long nativeReadLong(long pos);

    /**
     * 从指定的位置读取一个float
     *
     * @param pos 指定的位置
     * @return 读取到的值
     */
    native float nativeReadFloat(long pos);

    /**
     * 从指定的位置读取一个double
     *
     * @param pos 指定的位置
     * @return 读取到的值
     */
    native double nativeReadDouble(long pos);

    native byte[] readByteArray(int start, int length);

    @Override
    public byte readByte() {
        byte result = nativeReadByte(mCurPosition);
        mCurPosition += 1;
        return result;
    }

    @Override
    public void read(byte[] b) {
        readSubSequence(b, (int)mCurPosition, b.length);
    }

    @Override
    public void readSubSequence(byte[] b, int sourceStart, int length) {
        byte [] arr = readByteArray(sourceStart,length);
        if( arr != null ){
            // data return is more than needed
            if( arr.length > b.length )
                return;
            System.arraycopy(arr, 0, b, 0, b.length);
            mCurPosition += arr.length;
        }
    }

    @Override
    public char readChar() {
        char result = nativeReadChar(mCurPosition);
        mCurPosition += 2;
        return result;
    }

    @Override
    public short readShort() {
        short result = nativeReadShort(mCurPosition);
        mCurPosition += 2;
        return result;
    }

    @Override
    public int readInt() {
        int result = nativeReadInt(mCurPosition);
        mCurPosition += 4;
        return result;
    }

    @Override
    public long readLong() {
        long result = nativeReadLong(mCurPosition);
        mCurPosition += 8;
        return result;
    }

    @Override
    public float readFloat() {
        float result = nativeReadFloat(mCurPosition);
        mCurPosition += 4;
        return result;
    }

    @Override
    public double readDouble() {
        Double result = nativeReadDouble(mCurPosition);
        mCurPosition += 8;
        return result;
    }

    @Override
    public void setPosition(long position) {
        mCurPosition = position;
    }

    @Override
    public long position() {
        return mCurPosition;
    }

    @Override
    public boolean hasRemaining() {
        return mCurPosition < mLength;
    }

    @Override
    public long remaining() {
        return mLength - mCurPosition;
    }
}
