/**
 *
 * Copyright 2005-2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.command;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.activeio.packet.ByteSequence;

/**
 * @version $Revision$
 */
public class ActiveMQTextMessageTest extends TestCase {

    public static void main(String[] args) {
        TestRunner.run(ActiveMQTextMessageTest.class);
    }

    public void testGetDataStructureType() {
        ActiveMQTextMessage msg = new ActiveMQTextMessage();
        assertEquals(msg.getDataStructureType(), CommandTypes.ACTIVEMQ_TEXT_MESSAGE);
    }

    public void testShallowCopy() throws JMSException {
        ActiveMQTextMessage msg = new ActiveMQTextMessage();
        String string = "str";
        msg.setText(string);
        Message copy = msg.copy();
        assertTrue(msg.getText() == ((ActiveMQTextMessage) copy).getText());
    }

    public void testSetText() {
        ActiveMQTextMessage msg = new ActiveMQTextMessage();
        String str = "testText";
        try {
            msg.setText(str);
            assertEquals(msg.getText(), str);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void testGetBytes() throws JMSException, IOException {
        ActiveMQTextMessage msg = new ActiveMQTextMessage();
        String str = "testText";
        msg.setText(str);
        msg.beforeMarshall(null);
        
        ByteSequence bytes = msg.getContent();
        msg = new ActiveMQTextMessage();
        msg.setContent(bytes);
        
        assertEquals(msg.getText(), str);
    }

    public void testClearBody() throws JMSException, IOException {
        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText("string");
        textMessage.clearBody();
        assertFalse(textMessage.isReadOnlyBody());
        assertNull(textMessage.getText());
        try {
            textMessage.setText("String");
            textMessage.getText();
        } catch (MessageNotWriteableException mnwe) {
            fail("should be writeable");
        } catch (MessageNotReadableException mnre) {
            fail("should be readable");
        }
    }

    public void testReadOnlyBody() throws JMSException {
        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText("test");
        textMessage.setReadOnlyBody(true);
        try {
            textMessage.getText();
        } catch (MessageNotReadableException e) {
            fail("should be readable");
        }
        try {
            textMessage.setText("test");
            fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
    }

    public void testWriteOnlyBody() throws JMSException { // should always be readable
        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setReadOnlyBody(false);
        try {
            textMessage.setText("test");
            textMessage.getText();
        } catch (MessageNotReadableException e) {
            fail("should be readable");
        }
        textMessage.setReadOnlyBody(true);
        try {
            textMessage.getText();
            textMessage.setText("test");
            fail("should throw exception");
        } catch (MessageNotReadableException e) {
            fail("should be readable");
        } catch (MessageNotWriteableException mnwe) {
        }
    }
}
