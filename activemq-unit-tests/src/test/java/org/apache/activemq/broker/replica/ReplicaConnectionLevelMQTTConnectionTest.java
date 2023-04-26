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

package org.apache.activemq.broker.replica;

import org.apache.activemq.TestSupport;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import static org.apache.activemq.broker.replica.ReplicaPluginTestSupport.SHORT_TIMEOUT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class ReplicaConnectionLevelMQTTConnectionTest extends TestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ReplicaConnectionLevelMQTTConnectionTest.class);
    public static final String KEYSTORE_TYPE = "jks";
    public static final String PASSWORD = "password";
    public static final String SERVER_KEYSTORE = "src/test/resources/server.keystore";
    public static final String TRUST_KEYSTORE = "src/test/resources/client.keystore";
    public static final String PRIMARY_BROKER_CONFIG = "org/apache/activemq/broker/replica/transport-protocol-test-primary.xml";
    public static final String REPLICA_BROKER_CONFIG = "org/apache/activemq/broker/replica/transport-protocol-test-replica.xml";
    private static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.S");
    private final String protocol;
    protected BrokerService firstBroker;
    protected BrokerService secondBroker;
    protected Topic destination;

    @Before
    public void setUp() throws Exception {
        firstBroker =  setUpBrokerService(PRIMARY_BROKER_CONFIG);
        secondBroker =  setUpBrokerService(REPLICA_BROKER_CONFIG);

        firstBroker.start();
        secondBroker.start();
        firstBroker.waitUntilStarted();
        secondBroker.waitUntilStarted();

        destination = new Topic(getDestinationString(), QoS.AT_LEAST_ONCE);
    }

    @After
    public void tearDown() throws Exception {
        if (firstBroker != null) {
            try {
                firstBroker.stop();
                firstBroker.waitUntilStopped();
            } catch (Exception e) {
            }
        }
        if (secondBroker != null) {
            try {
                secondBroker.stop();
                secondBroker.waitUntilStopped();
            } catch (Exception e) {
            }
        }
    }

    @Parameterized.Parameters(name="protocol={0}")
    public static Collection<String[]> getTestParameters() {
        return Arrays.asList(new String[][] {
            {"mqtt"}, {"mqtt+ssl"}, {"mqtt+nio+ssl"}, {"mqtt+nio"}
        });
    }

    static {
        System.setProperty("javax.net.ssl.trustStore", TRUST_KEYSTORE);
        System.setProperty("javax.net.ssl.trustStorePassword", PASSWORD);
        System.setProperty("javax.net.ssl.trustStoreType", KEYSTORE_TYPE);
        System.setProperty("javax.net.ssl.keyStore", SERVER_KEYSTORE);
        System.setProperty("javax.net.ssl.keyStorePassword", PASSWORD);
        System.setProperty("javax.net.ssl.keyStoreType", KEYSTORE_TYPE);
    }

    public ReplicaConnectionLevelMQTTConnectionTest(String protocol) {
        this.protocol = protocol;
    }

    @Test
    public void testConnectWithMqttProtocol() throws Exception {
        MqttConnectOptions firstBrokerOptions = new MqttConnectOptions();
        firstBrokerOptions.setCleanSession(false);
        firstBrokerOptions.setAutomaticReconnect(true);
        String firstBrokerConnectionUri = getMQTTClientUri(firstBroker.getTransportConnectorByScheme(protocol));
        MqttClient firstBrokerClient = new MqttClient(firstBrokerConnectionUri, UUID.randomUUID().toString(), new MemoryPersistence());
        firstBrokerClient.connect(firstBrokerOptions);
        String payloadMessage = "testConnectWithMqttProtocol payload";

        MqttCallback mqttCallback = new MqttCallback() {
            public void connectionLost(Throwable cause) {
            }

            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println(String.format("%s - Receiver: received '%s'", df.format(new Date()), new String(message.getPayload())));
                assertEquals(payloadMessage, new String(message.getPayload()));
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        };


        MqttCallback callbackSpy = spy(mqttCallback);
        firstBrokerClient.setCallback(callbackSpy);

        LOG.info(String.format("mqtt client successfully connected to %s", firstBrokerClient.getServerURI()));
        firstBrokerClient.subscribe(destination.toString());
        firstBrokerClient.publish(destination.toString(), payloadMessage.getBytes(StandardCharsets.UTF_8), 1, false);
        Thread.sleep(SHORT_TIMEOUT);

        ArgumentCaptor<MqttMessage> mqttMessageArgumentCaptor = ArgumentCaptor.forClass(MqttMessage.class);
        verify(callbackSpy).messageArrived(anyString(), mqttMessageArgumentCaptor.capture());
        MqttMessage messageReceived = mqttMessageArgumentCaptor.getValue();
        assertEquals(payloadMessage, new String(messageReceived.getPayload()));
        verify(callbackSpy, never()).connectionLost(any());
        verify(callbackSpy, atMostOnce()).deliveryComplete(any());

        firstBrokerClient.disconnect();
    }

    @Test
    public void testReplicaReceiveMessage() throws Exception {
        MqttConnectOptions firstBrokerOptions = new MqttConnectOptions();
        firstBrokerOptions.setCleanSession(false);
        firstBrokerOptions.setAutomaticReconnect(true);
        String firstBrokerConnectionUri = getMQTTClientUri(firstBroker.getTransportConnectorByScheme(protocol));
        MqttClient firstBrokerClient = new MqttClient(firstBrokerConnectionUri, UUID.randomUUID().toString(), new MemoryPersistence());
        firstBrokerClient.connect(firstBrokerOptions);

        MqttConnectOptions secondBrokerOptions = new MqttConnectOptions();
        secondBrokerOptions.setCleanSession(false);
        secondBrokerOptions.setAutomaticReconnect(true);
        String secondBrokerConnectionUri = getMQTTClientUri(secondBroker.getTransportConnectorByScheme(protocol));
        MqttClient secondBrokerClient = new MqttClient(secondBrokerConnectionUri, UUID.randomUUID().toString(), new MemoryPersistence());
        secondBrokerClient.connect(secondBrokerOptions);
        String payloadMessage = "testConnectWithMqttProtocol payload";

        MqttCallback mqttCallback = new MqttCallback() {
            public void connectionLost(Throwable cause) {
            }

            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println(String.format("%s - Receiver: received '%s'", df.format(new Date()), new String(message.getPayload())));
                assertEquals(payloadMessage, new String(message.getPayload()));
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        };


        MqttCallback callbackSpy = spy(mqttCallback);
        secondBrokerClient.setCallback(callbackSpy);

        LOG.info(String.format("mqtt client successfully connected to %s", firstBrokerClient.getServerURI()));
        secondBrokerClient.subscribe(destination.toString());
        firstBrokerClient.publish(destination.toString(), payloadMessage.getBytes(StandardCharsets.UTF_8), 1, false);
        Thread.sleep(SHORT_TIMEOUT);

        ArgumentCaptor<MqttMessage> mqttMessageArgumentCaptor = ArgumentCaptor.forClass(MqttMessage.class);
        verify(callbackSpy).messageArrived(anyString(), mqttMessageArgumentCaptor.capture());
        MqttMessage messageReceived = mqttMessageArgumentCaptor.getValue();
        assertEquals(payloadMessage, new String(messageReceived.getPayload()));
        verify(callbackSpy, never()).connectionLost(any());
        verify(callbackSpy, atMostOnce()).deliveryComplete(any());

        firstBrokerClient.disconnect();
        secondBrokerClient.disconnect();
    }


    protected BrokerService setUpBrokerService(String configurationUri) throws Exception {
        BrokerService broker = createBroker(configurationUri);
        broker.setPersistent(false);
        return broker;
    }

    protected BrokerService createBroker(String uri) throws Exception {
        LOG.info("Loading broker configuration from the classpath with URI: " + uri);
        return BrokerFactory.createBroker(new URI("xbean:" + uri));
    }

    private String getMQTTClientUri(TransportConnector mqttConnector) throws IOException, URISyntaxException {
        if (protocol.contains("ssl")) {
            return "ssl://localhost:" + mqttConnector.getConnectUri().getPort();
        } else {
            return "tcp://localhost:" + mqttConnector.getConnectUri().getPort();
        }
    }

    protected String getDestinationString() {
        return getClass().getName() + "." + getName();
    }
}