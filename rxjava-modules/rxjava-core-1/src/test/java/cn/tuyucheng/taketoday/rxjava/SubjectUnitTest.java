package cn.tuyucheng.taketoday.rxjava;

import org.junit.jupiter.api.Test;
import rx.subjects.PublishSubject;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubjectUnitTest {

	@Test
	void givenSubjectAndTwoSubscribers_whenSubscribeOnSubject_thenSubscriberBeginsToAdd() {
		PublishSubject<Integer> subject = PublishSubject.create();

		subject.subscribe(SubjectImpl.getFirstObserver());
		subject.onNext(1);
		subject.onNext(2);
		subject.onNext(3);

		subject.subscribe(SubjectImpl.getSecondObserver());
		subject.onNext(4);
		subject.onCompleted();

		assertEquals(14, SubjectImpl.subscriber1 + SubjectImpl.subscriber2);
	}
}