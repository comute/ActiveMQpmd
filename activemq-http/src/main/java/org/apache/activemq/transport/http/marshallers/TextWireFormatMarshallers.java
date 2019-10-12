/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.activemq.transport.http.marshallers;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.activemq.transport.util.TextWireFormat;
import org.apache.activemq.wireformat.WireFormat;

/**
 * A factory for marshallers {@link HttpTransportMarshaller} that maintain compatibility with the original
 * ActiveMQ code that used {@link TextWireFormat#marshalText(Object)} and {@link TextWireFormat#marshal(Object)} depending
 * on the context.
 * All text handling is done using UTF-8.
 */
public class TextWireFormatMarshallers {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * The returned marshaller uses {@link TextWireFormat#marshal(Object)} and {@link TextWireFormat#unmarshalText(Reader)}.
     */
    public static HttpTransportMarshaller newServletMarshaller(final WireFormat wireFormat) {
        return new MarshalPlainUnmarshalTextMarshaller((TextWireFormat)wireFormat);
    }

    /**
     * The returned marshaller uses {@link TextWireFormat#marshalText(Object)} and {@link TextWireFormat#unmarshal(DataInput)}
     */
    public static HttpTransportMarshaller newTransportMarshaller(final TextWireFormat textWireFormat) {
        return new MarshalTextUnmarshalPlainMarshaller(textWireFormat);
    }

    private static class MarshalTextUnmarshalPlainMarshaller implements HttpTransportMarshaller {
        private final TextWireFormat wireFormat;

        private MarshalTextUnmarshalPlainMarshaller(final TextWireFormat wireFormat) {
            this.wireFormat = wireFormat;
        }

        @Override
        public void marshal(final Object command, final OutputStream outputStream) throws IOException {
            final String s = wireFormat.marshalText(command);
            outputStream.write(s.getBytes(CHARSET));
        }

        @Override
        public Object unmarshal(final InputStream stream) throws IOException {
            return wireFormat.unmarshal(new DataInputStream(stream));
        }

        @Override
        public WireFormat getWireFormat() {
            return wireFormat;
        }
    }

    private static class MarshalPlainUnmarshalTextMarshaller implements HttpTransportMarshaller  {
        private final TextWireFormat wireFormat;

        private MarshalPlainUnmarshalTextMarshaller(final TextWireFormat wireFormat) {
            this.wireFormat = wireFormat;
        }

        @Override
        public void marshal(final Object command, final OutputStream outputStream) throws IOException {
            wireFormat.marshal(command, new DataOutputStream(outputStream));
        }

        @Override
        public Object unmarshal(final InputStream stream) throws IOException {
            return wireFormat.unmarshalText(new InputStreamReader(stream, CHARSET));
        }

        @Override
        public WireFormat getWireFormat() {
            return wireFormat;
        }
    }
}