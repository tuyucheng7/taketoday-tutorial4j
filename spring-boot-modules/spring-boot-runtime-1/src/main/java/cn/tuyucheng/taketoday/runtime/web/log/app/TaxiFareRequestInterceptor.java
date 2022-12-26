package cn.tuyucheng.taketoday.runtime.web.log.app;

import cn.tuyucheng.taketoday.runtime.web.log.util.RequestLoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TaxiFareRequestInterceptor extends HandlerInterceptorAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(TaxiFareRequestInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String postData;
		HttpServletRequest requestCacheWrapperObject = null;
		try {
			// Uncomment to produce the stream closed issue
			// postData = RequestLoggingUtil.getStringFromInputStream(request.getInputStream());

			// To overcome request stream closed issue
			requestCacheWrapperObject = new ContentCachingRequestWrapper(request);
			requestCacheWrapperObject.getParameterMap();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			postData = RequestLoggingUtil.readPayload(requestCacheWrapperObject);
			LOGGER.info("REQUEST DATA: " + postData);
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		LOGGER.info("RESPONSE: " + response.getStatus());
	}
}