package cn.tuyucheng.taketoday.resttemplate.web.handler;

import cn.tuyucheng.taketoday.resttemplate.web.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class RestTemplateResponseErrorHandler
      implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse)
          throws IOException {

        return (httpResponse
              .getStatusCode()
              .series() == HttpStatus.Series.CLIENT_ERROR || httpResponse
              .getStatusCode()
              .series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse)
          throws IOException {

        if (httpResponse
              .getStatusCode()
              .series() == HttpStatus.Series.SERVER_ERROR) {
            // Handle SERVER_ERROR
            throw new HttpClientErrorException(httpResponse.getStatusCode());
        } else if (httpResponse
              .getStatusCode()
              .series() == HttpStatus.Series.CLIENT_ERROR) {
            // Handle CLIENT_ERROR
            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException();
            }
        }
    }
}