package kz.bsbnb.portal.wso2service.wso2config;

import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;

public class LogClientInterceptor implements ClientInterceptor {

    @Override
    public void afterCompletion(MessageContext arg0, Exception arg1)
            throws WebServiceClientException {
        //HttpLoggingUtils.showTheMessageINeed("Some text!!!");
    }

    @Override
    public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
        HttpLoggingUtils.getMessage(messageContext.getResponse());
        return true;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        HttpLoggingUtils.getMessage(messageContext.getRequest());

        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
        HttpLoggingUtils.getMessage(messageContext.getResponse());

        return true;
    }
}

