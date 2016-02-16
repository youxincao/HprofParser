/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tools.perflib.heap;


import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.*;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TLongObjectHashMap;
import gnu.trove.TObjectProcedure;

public class Heap {

    private final int mId;


    private final String mName;

    //  List of individual stack frames

    TLongObjectHashMap<StackFrame> mFrames = new TLongObjectHashMap<StackFrame>();

    //  List stack traces, which are lists of stack frames

    TIntObjectHashMap<StackTrace> mTraces = new TIntObjectHashMap<StackTrace>();

    //  Root objects such as interned strings, jni locals, etc

    ArrayList<RootObj> mRoots = new ArrayList<RootObj>();

    //  List of threads

    TIntObjectHashMap<ThreadObj> mThreads = new TIntObjectHashMap<ThreadObj>();

    //  Class definitions

    TLongObjectHashMap<ClassObj> mClassesById = new TLongObjectHashMap<ClassObj>();

    Multimap<String, ClassObj> mClassesByName = ArrayListMultimap.create();

    //  List of instances of above class definitions
    private final TLongObjectHashMap<Instance> mInstances = new TLongObjectHashMap<Instance>();

    //  The snapshot that this heap is part of
    public Snapshot mSnapshot;

    public Heap(int id, String name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }


    public String getName() {
        return mName;
    }

    public final void addStackFrame(StackFrame theFrame) {
        mFrames.put(theFrame.mId, theFrame);
    }

    public final StackFrame getStackFrame(long id) {
        return mFrames.get(id);
    }

    public final void addStackTrace(StackTrace theTrace) {
        mTraces.put(theTrace.mSerialNumber, theTrace);
    }

    public final StackTrace getStackTrace(int traceSerialNumber) {
        return mTraces.get(traceSerialNumber);
    }

    public final StackTrace getStackTraceAtDepth(int traceSerialNumber,
            int depth) {
        StackTrace trace = mTraces.get(traceSerialNumber);

        if (trace != null) {
            trace = trace.fromDepth(depth);
        }

        return trace;
    }

    public final void addRoot(RootObj root) {
        root.mIndex = mRoots.size();
        mRoots.add(root);
    }

    public final void addThread(ThreadObj thread, int serialNumber) {
        mThreads.put(serialNumber, thread);
    }

    public final ThreadObj getThread(int serialNumber) {
        return mThreads.get(serialNumber);
    }

    public final void addInstance(long id, Instance instance) {
        mInstances.put(id, instance);
    }

    public final Instance getInstance(long id) {
        return mInstances.get(id);
    }

    public final void addClass(long id, ClassObj theClass) {
        mClassesById.put(id, theClass);
        mClassesByName.put(theClass.mClassName, theClass);
    }

    public final ClassObj getClass(long id) {
        return mClassesById.get(id);
    }

    public final ClassObj getClass(String name) {
        Collection<ClassObj> classes = mClassesByName.get(name);
        if (classes.size() == 1) {
            return classes.iterator().next();
        }
        return null;
    }

    public final Collection<ClassObj> getClasses(String name) {
        return mClassesByName.get(name);
    }

    public final void dumpInstanceCounts() {
        for (Object value : mClassesById.getValues()) {
            ClassObj theClass = (ClassObj) value;
            int count = theClass.getInstanceCount();

            if (count > 0) {
                System.out.println(theClass + ": " + count);
            }
        }
    }

    public final void dumpSubclasses() {
        for (Object value : mClassesById.getValues()) {
            ClassObj theClass = (ClassObj) value;
            int count = theClass.mSubclasses.size();

            if (count > 0) {
                System.out.println(theClass);
                theClass.dumpSubclasses();
            }
        }
    }

    public final void dumpSizes() {
        for (Object value : mClassesById.getValues()) {
            ClassObj theClass = (ClassObj) value;

            int size = 0;

            for (Instance instance : theClass.getHeapInstances(getId())) {
                size += instance.getCompositeSize();
            }

            if (size > 0) {
                System.out.println(theClass + ": base " + theClass.getSize()
                        + ", composite " + size);
            }
        }
    }


    public Collection<ClassObj> getClasses() {
        return mClassesByName.values();
    }


    public Collection<Instance> getInstances() {
        final ArrayList<Instance> result = new ArrayList<Instance>(mInstances.size());
        mInstances.forEachValue(new TObjectProcedure<Instance>() {
            @Override
            public boolean execute(Instance instance) {
                result.add(instance);
                return true;
            }
        });
        return result;
    }

    public int getInstancesCount() {
        return mInstances.size();
    }
}
