package cn.tuyucheng.taketoday.pointcutadvice;

import cn.tuyucheng.taketoday.pointcutadvice.events.FooCreationEvent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class PublishingAspect {

	private ApplicationEventPublisher eventPublisher;

	@Autowired
	public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@Pointcut("within(cn.tuyucheng.taketoday..*) && execution(* cn.tuyucheng.taketoday.pointcutadvice.dao.FooDao.*(..))")
	public void repositoryMethods() {
	}

	@Pointcut("within(cn.tuyucheng.taketoday..*) && execution(* cn.tuyucheng.taketoday.pointcutadvice.dao.FooDao.create*(Long,..))")
	public void firstLongParamMethods() {
	}

	@Pointcut("repositoryMethods() && firstLongParamMethods()")
	public void entityCreationMethods() {
	}

	@AfterReturning(value = "entityCreationMethods()", returning = "entity")
	public void logMethodCall(JoinPoint jp, Object entity) throws Throwable {
		eventPublisher.publishEvent(new FooCreationEvent(entity));
	}
}