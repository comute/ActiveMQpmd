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
package org.apache.activemq.transport.stomp2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.command.Command;
import org.apache.activemq.command.Endpoint;
import org.apache.activemq.command.Response;
import org.apache.activemq.state.CommandVisitor;

/**
 * Represents all the data in a STOMP frame.
 * 
 * @author <a href="http://hiramchirino.com">chirino</a> 
 */
public class StompCommand implements Command {

    private static final byte[] NO_DATA = new byte[]{};

	private String action;
	private Map headers = Collections.EMPTY_MAP;
	private byte[] content = NO_DATA;

	public StompCommand(String command, HashMap headers, byte[] data) {
		this.action = command;
		this.headers = headers;
		this.content = data;
	}

	public StompCommand() {
	}

	public String getAction() {
		return action;
	}

	public void setAction(String command) {
		this.action = command;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] data) {
		this.content = data;
	}

	public Map getHeaders() {
		return headers;
	}

	public void setHeaders(Map headers) {
		this.headers = headers;
	}

	//
	// Methods in the Command interface
	//
	public int getCommandId() {
		return 0;
	}

	public Endpoint getFrom() {
		return null;
	}

	public Endpoint getTo() {
		return null;
	}

	public boolean isBrokerInfo() {
		return false;
	}

	public boolean isMessage() {
		return false;
	}

	public boolean isMessageAck() {
		return false;
	}

	public boolean isMessageDispatch() {
		return false;
	}

	public boolean isMessageDispatchNotification() {
		return false;
	}

	public boolean isResponse() {
		return false;
	}

	public boolean isResponseRequired() {
		return false;
	}

	public boolean isShutdownInfo() {
		return false;
	}

	public boolean isWireFormatInfo() {
		return false;
	}

	public void setCommandId(int value) {
	}

	public void setFrom(Endpoint from) {
	}

	public void setResponseRequired(boolean responseRequired) {
	}

	public void setTo(Endpoint to) {
	}

	public Response visit(CommandVisitor visitor) throws Exception {
		return null;
	}

	public byte getDataStructureType() {
		return 0;
	}

	public boolean isMarshallAware() {
		return false;
	}

}
