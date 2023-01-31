package cn.tuyucheng.taketoday.pointcutadvice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

@Component
@Aspect
public class LoggingAspect {

	private static final Logger logger = Logger.getLogger(LoggingAspect.class.getName());

	private final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> new SimpleDateFormat("[yyyy-MM-dd hh:mm:ss:SSS]"));

	@Pointcut("within(cn.tuyucheng.taketoday..*) && execution(* cn.tuyucheng.taketoday.pointcutadvice.dao.FooDao.*(..))")
	public void repositoryMethods() {
	}

	@Pointcut("within(cn.tuyucheng.taketoday..*) && @annotation(cn.tuyucheng.taketoday.pointcutadvice.annotations.Loggable)")
	public void loggableMethods() {
	}

	@Pointcut("within(cn.tuyucheng.taketoday..*) && @args(cn.tuyucheng.taketoday.pointcutadvice.annotations.Entity)")
	public void methodsAcceptingEntities() {
	}

	@Before("repositoryMethods()")
	public void logMethodCall(JoinPoint jp) {
		String methodName = jp.getSignature().getName();
		logger.info(sdf.get().format(new Date()) + methodName);
	}

	@Before("loggableMethods()")
	public void logMethod(JoinPoint jp) {
		String methodName = jp.getSignature().getName();
		logger.info("Executing method: " + methodName);
	}

	@Before("methodsAcceptingEntities()")
	public void logMethodAcceptionEntityAnnotatedBean(JoinPoint jp) {
		logger.info("Accepting beans with @Entity annotation: " + jp.getArgs()[0]);
	}
}