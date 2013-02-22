/*******************************************************************************
 * Copyright 2010 Cees De Groot, Alex Boisvert, Jan Kotek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.tmcdb.utils.jdbm;

import org.tmcdb.utils.jdbm.helper.LongPacker;
import org.tmcdb.utils.jdbm.helper.Serialization;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Input for Serializer
 *
 * @author Jan Kotek
 */
public class SerializerInput extends DataInputStream {


    public SerializerInput(InputStream in) {
        super(in);
    }

    @SuppressWarnings("unchecked")
    public <V> V readObject() throws ClassNotFoundException, IOException {
        return (V) Serialization.readObject(this);
    }

    public long readPackedLong() throws IOException {
        return LongPacker.unpackLong(this);
    }

    public int readPackedInt() throws IOException {
        return LongPacker.unpackInt(this);
    }
}
