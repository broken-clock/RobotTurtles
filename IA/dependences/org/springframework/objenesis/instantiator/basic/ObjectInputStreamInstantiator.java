// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.basic;

import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import org.springframework.objenesis.ObjenesisException;
import java.io.NotSerializableException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.ObjectInputStream;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class ObjectInputStreamInstantiator<T> implements ObjectInstantiator<T>
{
    private ObjectInputStream inputStream;
    
    public ObjectInputStreamInstantiator(final Class<T> clazz) {
        if (Serializable.class.isAssignableFrom(clazz)) {
            try {
                this.inputStream = new ObjectInputStream(new MockStream(clazz));
                return;
            }
            catch (IOException e) {
                throw new Error("IOException: " + e.getMessage());
            }
            throw new ObjenesisException(new NotSerializableException(clazz + " not serializable"));
        }
        throw new ObjenesisException(new NotSerializableException(clazz + " not serializable"));
    }
    
    public T newInstance() {
        try {
            return (T)this.inputStream.readObject();
        }
        catch (ClassNotFoundException e) {
            throw new Error("ClassNotFoundException: " + e.getMessage());
        }
        catch (Exception e2) {
            throw new ObjenesisException(e2);
        }
    }
    
    private static class MockStream extends InputStream
    {
        private int pointer;
        private byte[] data;
        private int sequence;
        private static final int[] NEXT;
        private byte[][] buffers;
        private final byte[] FIRST_DATA;
        private static byte[] HEADER;
        private static byte[] REPEATING_DATA;
        
        private static void initialize() {
            try {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                DataOutputStream dout = new DataOutputStream(byteOut);
                dout.writeShort(-21267);
                dout.writeShort(5);
                MockStream.HEADER = byteOut.toByteArray();
                byteOut = new ByteArrayOutputStream();
                dout = new DataOutputStream(byteOut);
                dout.writeByte(115);
                dout.writeByte(113);
                dout.writeInt(8257536);
                MockStream.REPEATING_DATA = byteOut.toByteArray();
            }
            catch (IOException e) {
                throw new Error("IOException: " + e.getMessage());
            }
        }
        
        public MockStream(final Class<?> clazz) {
            this.pointer = 0;
            this.sequence = 0;
            this.data = MockStream.HEADER;
            final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            final DataOutputStream dout = new DataOutputStream(byteOut);
            try {
                dout.writeByte(115);
                dout.writeByte(114);
                dout.writeUTF(clazz.getName());
                dout.writeLong(ObjectStreamClass.lookup(clazz).getSerialVersionUID());
                dout.writeByte(2);
                dout.writeShort(0);
                dout.writeByte(120);
                dout.writeByte(112);
            }
            catch (IOException e) {
                throw new Error("IOException: " + e.getMessage());
            }
            this.FIRST_DATA = byteOut.toByteArray();
            this.buffers = new byte[][] { MockStream.HEADER, this.FIRST_DATA, MockStream.REPEATING_DATA };
        }
        
        private void advanceBuffer() {
            this.pointer = 0;
            this.sequence = MockStream.NEXT[this.sequence];
            this.data = this.buffers[this.sequence];
        }
        
        @Override
        public int read() throws IOException {
            final int result = this.data[this.pointer++];
            if (this.pointer >= this.data.length) {
                this.advanceBuffer();
            }
            return result;
        }
        
        @Override
        public int available() throws IOException {
            return Integer.MAX_VALUE;
        }
        
        @Override
        public int read(final byte[] b, int off, final int len) throws IOException {
            int left = len;
            for (int remaining = this.data.length - this.pointer; remaining <= left; remaining = this.data.length - this.pointer) {
                System.arraycopy(this.data, this.pointer, b, off, remaining);
                off += remaining;
                left -= remaining;
                this.advanceBuffer();
            }
            if (left > 0) {
                System.arraycopy(this.data, this.pointer, b, off, left);
                this.pointer += left;
            }
            return len;
        }
        
        static {
            NEXT = new int[] { 1, 2, 2 };
            initialize();
        }
    }
}
