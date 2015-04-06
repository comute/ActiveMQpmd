/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.transport.tcp;

import junit.framework.Test;
import junit.textui.TestRunner;
import org.apache.activemq.broker.TransportConnector;
import org.apache.directory.api.ldap.model.constants.SupportedSaslMechanisms;
import org.apache.directory.server.annotations.CreateKdcServer;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.annotations.SaslMechanism;
import org.apache.directory.server.core.annotations.ContextEntry;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreateIndex;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.kerberos.KeyDerivationInterceptor;
import org.apache.directory.server.ldap.handlers.sasl.cramMD5.CramMd5MechanismHandler;
import org.apache.directory.server.ldap.handlers.sasl.digestMD5.DigestMd5MechanismHandler;
import org.apache.directory.server.ldap.handlers.sasl.gssapi.GssapiMechanismHandler;
import org.apache.directory.server.ldap.handlers.sasl.ntlm.NtlmMechanismHandler;
import org.apache.directory.server.ldap.handlers.sasl.plain.PlainMechanismHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.PrivilegedAction;

@CreateDS(name = "SaslGssapiBindITest-class",
        partitions =
                {
                        @CreatePartition(
                                name = "example",
                                suffix = "dc=example,dc=com",
                                contextEntry = @ContextEntry(
                                        entryLdif =
                                                "dn: dc=example,dc=com\n" +
                                                        "dc: example\n" +
                                                        "objectClass: top\n" +
                                                        "objectClass: domain\n\n"),
                                indexes =
                                        {
                                                @CreateIndex(attribute = "objectClass"),
                                                @CreateIndex(attribute = "dc"),
                                                @CreateIndex(attribute = "ou")
                                        })
                },
        additionalInterceptors =
                {
                        KeyDerivationInterceptor.class
                })
@CreateLdapServer(
        transports =
                {
                        @CreateTransport(protocol = "LDAP")
                },
        saslHost = "localhost",
        saslPrincipal = "ldap/localhost@EXAMPLE.COM",
        saslMechanisms =
                {
                        @SaslMechanism(name = SupportedSaslMechanisms.PLAIN, implClass = PlainMechanismHandler.class),
                        @SaslMechanism(name = SupportedSaslMechanisms.CRAM_MD5, implClass = CramMd5MechanismHandler.class),
                        @SaslMechanism(name = SupportedSaslMechanisms.DIGEST_MD5, implClass = DigestMd5MechanismHandler.class),
                        @SaslMechanism(name = SupportedSaslMechanisms.GSSAPI, implClass = GssapiMechanismHandler.class),
                        @SaslMechanism(name = SupportedSaslMechanisms.NTLM, implClass = NtlmMechanismHandler.class),
                        @SaslMechanism(name = SupportedSaslMechanisms.GSS_SPNEGO, implClass = NtlmMechanismHandler.class)
                })
@CreateKdcServer(
        transports =
                {
                        @CreateTransport(protocol = "UDP", port = 6088),
                        @CreateTransport(protocol = "TCP", port = 6088)
                })
public class Krb5BrokerServiceTest extends Krb5BrokerTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(Krb5BrokerServiceTest.class);

    protected String getBindLocation() {
        return "krb5://localhost:0?transport.soWriteTimeout=20000&krb5ConfigName=Broker";
    }

    @Override
    protected URI getBindURI() throws URISyntaxException {
        return new URI("krb5://localhost:0?soWriteTimeout=20000&krb5ConfigName=User1");
    }

    public void testPositive() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        makeSSLConnection(context, "User1", connector);
    }

    private void makeSSLConnection(final SSLContext context, String krb5ConfigName, final TransportConnector connector) throws Exception, SocketException {

        LoginContext loginCtx = new LoginContext(krb5ConfigName);
        loginCtx.login();
        Subject subject = loginCtx.getSubject();

        Subject.doAs(subject, new PrivilegedAction<Void>() {
            public Void run() {
                try {
                    SSLSocket sslSocket = (SSLSocket) context.getSocketFactory().createSocket("localhost", connector.getUri().getPort());

                    sslSocket.setEnabledCipherSuites(new String[]{Krb5OverSslTransport.KRB5_CIPHER});

                    sslSocket.setSoTimeout(5000);

                    SSLSession session = sslSocket.getSession();
                    sslSocket.startHandshake();
                    sslSocket.startHandshake();
                    LOG.info("CipherSuite: " + session.getCipherSuite());
                    LOG.info("PeerPort: " + session.getPeerPort());
                    LOG.info("LocalPrincipal: " + session.getLocalPrincipal());
                    LOG.info("PeerPrincipal: " + session.getPeerPrincipal());

                    return null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    protected void setUp() throws Exception {
        maxWait = 10000;
        super.setUp();
    }

    public static Test suite() {
        return suite(Krb5BrokerServiceTest.class);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }
}
