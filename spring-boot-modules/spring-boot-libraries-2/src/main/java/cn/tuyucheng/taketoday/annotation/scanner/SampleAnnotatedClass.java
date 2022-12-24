package cn.tuyucheng.taketoday.annotation.scanner;

@SampleAnnotation(name = "SampleAnnotatedClass")
public class SampleAnnotatedClass {

	@SampleAnnotation(name = "annotatedMethod")
	public void annotatedMethod() {
		// Do something
	}

	public void notAnnotatedMethod() {
		// Do something
	}
}