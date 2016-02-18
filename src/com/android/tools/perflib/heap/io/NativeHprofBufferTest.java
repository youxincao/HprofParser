package com.android.tools.perflib.heap.io;

import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by weilun on 16/2/17.
 */
public class NativeHprofBufferTest {

    private static final String mDumpFile = "/Users/weilun/Desktop/2015-10-13_16-32-50_766_unkown.hprof";
    private static final int TEST_COUNT = 1000;

    private File mTestFile = null;
    NativeHprofBuffer mBuffer = null;
    MemoryMappedFileBuffer mOriginBuffer = null;


    @Before
    public void runBefore(){
        mTestFile = new File(mDumpFile);
        try {
            mBuffer = new NativeHprofBuffer(mTestFile);
            mOriginBuffer = new MemoryMappedFileBuffer(mTestFile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @After
    public void runAfter(){
        try {
            mBuffer.releaseNativeMemory();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mOriginBuffer.dispose();
    }

    @org.junit.Test
    public void testReadByte() throws Exception{
        Random r = new Random();
        int count = 0 ;
        long pos;

        while( count < TEST_COUNT ) {

            pos = r.nextInt(1024);
            mOriginBuffer.setPosition(pos);
            mBuffer.setPosition(pos);

            byte expertByte = mOriginBuffer.readByte();
            byte actualByte = mBuffer.readByte();
            assertEquals(expertByte, actualByte);

            count ++;
        }
    }

    @org.junit.Test
    public void testRead() throws Exception {

    }

    @org.junit.Test
    public void testReadSubSequence() throws Exception {

    }

    @org.junit.Test
    public void testReadChar() throws Exception {
        Random r = new Random();
        int count = 0 ;
        long pos;

        while( count < TEST_COUNT ) {

            pos = r.nextInt(1024);
            mOriginBuffer.setPosition(pos);
            mBuffer.setPosition(pos);

            char expertChar = mOriginBuffer.readChar();
            char actualChar = mBuffer.readChar();
            assertEquals(expertChar, actualChar);

            count ++;
        }
    }

    @org.junit.Test
    public void testReadShort() throws Exception {

    }

    @org.junit.Test
    public void testReadInt() throws Exception {
        Random r = new Random();
        int count = 0 ;
        long pos;

        while( count < TEST_COUNT ) {

            pos = r.nextInt(1024);
            mOriginBuffer.setPosition(pos);
            mBuffer.setPosition(pos);

            int expertByte = mOriginBuffer.readInt();
            int actualByte = mBuffer.readInt();
            assertEquals(expertByte, actualByte);

            count ++;
        }
    }

    @org.junit.Test
    public void testReadLong() throws Exception {

    }

    @org.junit.Test
    public void testReadFloat() throws Exception {

    }

    @org.junit.Test
    public void testReadDouble() throws Exception {

    }
}