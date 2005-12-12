/**
 *
 * Copyright 2004 Hiram Chirino
 * Copyright 2005 LogicBlaze Inc.
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
 *
 **/
package org.activemq.ra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ServerSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;

import org.activemq.ActiveMQQueueSession;
import org.activemq.ActiveMQSession;
import org.activemq.ActiveMQTopicSession;
import org.activemq.command.MessageDispatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @version $Revision$ $Date$
 */
public class ServerSessionPoolImpl implements ServerSessionPool {
    
    private static final Log log = LogFactory.getLog(ServerSessionPoolImpl.class);

    private final ActiveMQEndpointWorker activeMQAsfEndpointWorker;
    private final int maxSessions;

    private ArrayList idleSessions = new ArrayList();
    private LinkedList activeSessions = new LinkedList();
    private boolean closing = false;

    public ServerSessionPoolImpl(ActiveMQEndpointWorker activeMQAsfEndpointWorker, int maxSessions) {
        this.activeMQAsfEndpointWorker = activeMQAsfEndpointWorker;
        this.maxSessions=maxSessions;
    }

    private ServerSessionImpl createServerSessionImpl() throws JMSException {
        ActiveMQActivationSpec activationSpec = activeMQAsfEndpointWorker.endpointActivationKey.getActivationSpec();
        int acknowledge = (activeMQAsfEndpointWorker.transacted) ? Session.SESSION_TRANSACTED : activationSpec.getAcknowledgeModeForSession();
        final ActiveMQSession session = (ActiveMQSession) activeMQAsfEndpointWorker.connection.createSession(activeMQAsfEndpointWorker.transacted,acknowledge);            
        MessageEndpoint endpoint;
        try {                
            int batchSize = 0;
            if (activationSpec.getEnableBatchBooleanValue()) {
                batchSize = activationSpec.getMaxMessagesPerBatchIntValue();
            }
            if( activationSpec.isUseRAManagedTransactionEnabled() ) {
                // The RA will manage the transaction commit.
                endpoint = createEndpoint(null);   
                return new ServerSessionImpl(this, (ActiveMQSession)session, activeMQAsfEndpointWorker.workManager, endpoint, true, batchSize);
            } else {
                // Give the container an object to manage to transaction with.
                endpoint = createEndpoint(new LocalAndXATransaction(session.getTransactionContext()));                
                return new ServerSessionImpl(this, (ActiveMQSession)session, activeMQAsfEndpointWorker.workManager, endpoint, false, batchSize);
            }
        } catch (UnavailableException e) {
            // The container could be limiting us on the number of endpoints
            // that are being created.
            session.close();
            return null;
        }
    }

    private MessageEndpoint createEndpoint(LocalAndXATransaction txResourceProxy) throws UnavailableException {
        MessageEndpoint endpoint;
        endpoint = activeMQAsfEndpointWorker.endpointFactory.createEndpoint(txResourceProxy);
        MessageEndpointProxy endpointProxy = new MessageEndpointProxy(endpoint);
        return endpointProxy;
    }

    /**
     */
    synchronized public ServerSession getServerSession() throws JMSException {
        log.debug("ServerSession requested.");
        if (closing) {
            throw new JMSException("Session Pool Shutting Down.");
        }

        if (idleSessions.size() > 0) {
            ServerSessionImpl ss = (ServerSessionImpl) idleSessions.remove(idleSessions.size() - 1);
            activeSessions.addLast(ss);
            log.debug("Using idle session: " + ss);
            return ss;
        } else {
            // Are we at the upper limit?
            if (activeSessions.size() >= maxSessions) {
                // then reuse the allready created sessions..
                // This is going to queue up messages into a session for
                // processing.
                return getExistingServerSession();
            }
            ServerSessionImpl ss = createServerSessionImpl();
            // We may not be able to create a session due to the conatiner
            // restricting us.
            if (ss == null) {
                return getExistingServerSession();
            }
            activeSessions.addLast(ss);
            log.debug("Created a new session: " + ss);
            return ss;
        }
    }

    /**
     * @param message
     * @throws JMSException
     */
    private void dispatchToSession(MessageDispatch messageDispatch) throws JMSException {

        ServerSession serverSession = getServerSession();
        Session s = serverSession.getSession();
        ActiveMQSession session = null;
        if( s instanceof ActiveMQSession ) {
            session = (ActiveMQSession) s;
        } else if(s instanceof ActiveMQQueueSession) {
            session = (ActiveMQSession) s;
        } else if(s instanceof ActiveMQTopicSession) {
            session = (ActiveMQSession) s;
        } else {
        	activeMQAsfEndpointWorker.connection.onAsyncException(new JMSException("Session pool provided an invalid session type: "+s.getClass()));
        }
        session.dispatch(messageDispatch);
        serverSession.start();
    }

    
    /**
     * @return
     */
    private ServerSession getExistingServerSession() {
        ServerSessionImpl ss = (ServerSessionImpl) activeSessions.removeFirst();
        activeSessions.addLast(ss);
        log.debug("Reusing an active session: " + ss);
        return ss;
    }

    synchronized public void returnToPool(ServerSessionImpl ss) {
        log.debug("Session returned to pool: " + ss);
        activeSessions.remove(ss);
        idleSessions.add(ss);
        notify();
    }

    synchronized public void removeFromPool(ServerSessionImpl ss) {
        activeSessions.remove(ss);
        try {
            ActiveMQSession session = (ActiveMQSession) ss.getSession();
            List l = session.getUnconsumedMessages();
            for (Iterator i = l.iterator(); i.hasNext();) {
                dispatchToSession((MessageDispatch) i.next());        			
            }
        } catch (Throwable t) {
            log.error("Error redispatching unconsumed messages from stale session", t);    	
        }
        ss.close();
        notify();
    }

    public void close() {
        synchronized (this) {
            closing = true;
            closeIdleSessions();
            while( activeSessions.size() > 0 ) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                closeIdleSessions();
            }
        }
    }

    private void closeIdleSessions() {
        for (Iterator iter = idleSessions.iterator(); iter.hasNext();) {
            ServerSessionImpl ss = (ServerSessionImpl) iter.next();
            ss.close();
        }
    }

}
