/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnes.httpclient;

import fr.cnes.httpclient.HttpClientFactory.Type;
import fr.cnes.httpclient.configuration.ProxyConfiguration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.config.Registry;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The client makes HTTP requests via a proxy without authentication and configured by the
 * {@link fr.cnes.httpclient.configuration.ProxyConfiguration API}
 *
 * @author Jean-Christophe Malapert
 */
public class ProxyHttpClientWithoutAuth extends AbstractProxyHttpClient {

    /**
     * Get actual class name to be printed on.
     */
    private static final Logger LOG = LogManager.getLogger(ProxyHttpClientWithoutAuth.class.
            getName());

    /**
     * Creates a HTTP client using a proxy with no authentication.
     */
    public ProxyHttpClientWithoutAuth() {
        this(false);
    }

    /**
     * Creates a HTTP client using a proxy with no authentication.
     *
     * @param isDisabledSSL True when the SSL certificate check is disabled otherwise False.
     */
    public ProxyHttpClientWithoutAuth(final boolean isDisabledSSL) {
        this(isDisabledSSL, new HashMap(), Type.PROXY_BASIC);
    }
    
    /**
     * Creates a HTTP client using a proxy with no authentication and options for Http client.
     *
     * @param isDisabledSSL True when the SSL certificate check is disabled otherwise False.
     * @param config options for Http client
     */
    public ProxyHttpClientWithoutAuth(final boolean isDisabledSSL, final Map<String, String> config) {
        this(isDisabledSSL, config, Type.PROXY_BASIC);
    }    

    /**
     * Creates a HTTP client using a proxy with no authentication and options for Http client.
     *
     * @param isDisabledSSL True when the SSL certificate check is disabled otherwise False.
     * @param config options for Http client
     * @param type type of http client
     */
    protected ProxyHttpClientWithoutAuth(final boolean isDisabledSSL, final Map<String, String> config, final Type type) {
        super(isDisabledSSL, config, type);
    }

    /**
     * No credentials.
     *
     * @param proxy proxy
     * @return {@code null}
     */
    @Override
    protected CredentialsProvider createCredsProvider(final HttpHost proxy) {
        LOG.debug("Does not provide credentials");
        return null;
    }

    /**
     * No register authentication scheme.
     *
     * @return {@code null}
     */
    @Override
    protected Registry<AuthSchemeProvider> registerAuthSchemeProvider() {
        LOG.debug("Does not provide authentication scheme to register");
        return null;
    }

    /**
     * Creates proxy builder without authentication.
     *
     * @param builder builder
     * @return builder
     * @throws IllegalArgumentException when a validation error happens in ProxyConfiguration
     */
    @Override
    protected HttpClientBuilder createBuilderProxy(final HttpClientBuilder builder) {
        LOG.traceEntry("builder : {}", builder);
        final StringBuilder error = new StringBuilder();
        final boolean isValid = ProxyConfiguration.isValid(error);
        if (!isValid) {
            LOG.error("Error validation : {}", error);
            throw LOG.throwing(new IllegalArgumentException(error.toString()));
        }
        final HttpHost proxy = stringToProxy(ProxyConfiguration.HTTP_PROXY.getValue());
        final List<String> excludedHosts = new ArrayList<>();
        Collections.addAll(excludedHosts, ProxyConfiguration.NO_PROXY.getValue().split("\\s*,\\s*"));
        return LOG.traceExit(this.createBuilder(builder, proxy, excludedHosts));
    }

}