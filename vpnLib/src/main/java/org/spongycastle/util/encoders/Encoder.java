

package org.spongycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;


public interface Encoder
{
    int encode(byte[] data, int off, int length, OutputStream out) throws IOException;
    
    void decode(byte[] data, int off, int length, OutputStream out) throws IOException;

    int decode(String data, OutputStream out) throws IOException;
}
