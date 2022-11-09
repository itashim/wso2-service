package kz.bsbnb.portal.wso2service.wso2config;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.net.ssl.SSLContext;
import java.io.File;


//@Configuration
public class Wso2SOAPClientConfig {
    @Value("${ssl.trust-store}")
    private String trustStore;
    @Value("${ssl.trust-store-password}")
    private String trustStorePassword;
    @Value("${server.wso2.url}")
    private String wso2Url;
    @Value("${server.wso2.username}")
    private String wso2Username;
    @Value("${server.wso2.password}")
    private String wso2Password;

    private String contextPath;
    private String url;
    private String username;
    private String password;


    public SSLContext sslContext() throws Exception {
        File file = new File(trustStore);
        return SSLContextBuilder.create()
                .loadTrustMaterial(file, trustStorePassword.toCharArray()).build();
    }

    public SSLConnectionSocketFactory sslConnectionSocketFactory() throws Exception {
        // NoopHostnameVerifier essentially turns hostname verification off as otherwise following error
        // is thrown: java.security.cert.CertificateException: No name matching localhost found
        return new SSLConnectionSocketFactory(sslContext(), NoopHostnameVerifier.INSTANCE);
    }


    //@Bean
    //@Scope(value = "prototype")
    public WebServiceTemplate wso2WebServiceTemplate() throws Exception {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setContextPath(contextPath);

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if (username != null && password != null)
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        HttpClient httpClient = HttpClientBuilder.create()
                .setSSLSocketFactory(sslConnectionSocketFactory())
                .addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();

        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
        httpComponentsMessageSender.setReadTimeout(3000000);
        httpComponentsMessageSender.setConnectionTimeout(3000000);
        httpComponentsMessageSender.setHttpClient(httpClient);

        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(jaxb2Marshaller);
        webServiceTemplate.setUnmarshaller(jaxb2Marshaller);
        webServiceTemplate.setDefaultUri(url);
        webServiceTemplate.setMessageSender(httpComponentsMessageSender);
        ClientInterceptor[] interceptors =
                new ClientInterceptor[]{new LogClientInterceptor()};
        webServiceTemplate.setInterceptors(interceptors);
        return webServiceTemplate;
    }
}
