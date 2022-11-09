package kz.bsbnb.portal.wso2service;

import kz.bsbnb.portal.wso2service.wso2config.LogClientInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.net.ssl.SSLContext;
import java.io.File;

@SpringBootApplication
@Slf4j
public class Wso2ServiceApplication {
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(Wso2ServiceApplication.class, args);
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate webServiceTemplate = null;
        String wso2Url = environment.getProperty("server.wso2.url");
        String wso2Username = environment.getProperty("server.wso2.username");
        String wso2Password = environment.getProperty("server.wso2.password");
        String trustStorePassword = environment.getProperty("ssl.wso2.trust-store-password");
        String trustStore = environment.getProperty("ssl.wso2.trust-store");

        try {
            Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
            jaxb2Marshaller.setContextPath("org.wso2.carbon.um.ws.service");

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(wso2Username, wso2Password));

            HttpClient httpClient = HttpClientBuilder.create()
                    .setSSLSocketFactory(sslConnectionSocketFactory(trustStore, trustStorePassword))
                    .addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .build();

            HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
            httpComponentsMessageSender.setReadTimeout(3000000);
            httpComponentsMessageSender.setConnectionTimeout(3000000);
            httpComponentsMessageSender.setHttpClient(httpClient);

            webServiceTemplate = new WebServiceTemplate();
            webServiceTemplate.setMarshaller(jaxb2Marshaller);
            webServiceTemplate.setUnmarshaller(jaxb2Marshaller);
            webServiceTemplate.setDefaultUri(wso2Url);
            webServiceTemplate.setMessageSender(httpComponentsMessageSender);
            ClientInterceptor[] interceptors =
                    new ClientInterceptor[]{new LogClientInterceptor()};
            webServiceTemplate.setInterceptors(interceptors);
        } catch (Exception e) {
            log.error("Error while creating WSO webServiceTemplate", e);
        }
        return webServiceTemplate;
    }

    public SSLContext sslContext(String trustStore, String trustStorePassword) throws Exception {
        File file = new File(trustStore);
        return SSLContextBuilder.create()
                .loadTrustMaterial(file, trustStorePassword.toCharArray()).build();
    }

    public SSLConnectionSocketFactory sslConnectionSocketFactory(String trustStore, String trustStorePassword) throws Exception {
        // NoopHostnameVerifier essentially turns hostname verification off as otherwise following error
        // is thrown: java.security.cert.CertificateException: No name matching localhost found
        return new SSLConnectionSocketFactory(sslContext(trustStore, trustStorePassword), NoopHostnameVerifier.INSTANCE);
    }
}
