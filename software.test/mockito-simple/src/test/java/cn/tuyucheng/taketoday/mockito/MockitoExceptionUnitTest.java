package cn.tuyucheng.taketoday.mockito;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MockitoExceptionUnitTest {

	@Test
	void whenConfigNonVoidReturnMethodToThrowEx_thenExIsThrown() {
		MyDictionary dictMock = mock(MyDictionary.class);
		when(dictMock.getMeaning(anyString())).thenThrow(NullPointerException.class);
		assertThrows(NullPointerException.class, () -> dictMock.getMeaning("word"));

	}

	@Test
	void whenConfigVoidReturnMethodToThrowEx_thenExIsThrown() {
		MyDictionary dictMock = mock(MyDictionary.class);
		doThrow(IllegalStateException.class).when(dictMock)
			.add(anyString(), anyString());
		assertThrows(IllegalStateException.class, () -> dictMock.add("word", "meaning"));

	}

	@Test
	void whenConfigNonVoidReturnMethodToThrowExWithNewExObj_thenExIsThrown() {
		MyDictionary dictMock = mock(MyDictionary.class);
		when(dictMock.getMeaning(anyString())).thenThrow(new NullPointerException("Error occurred"));
		assertThrows(NullPointerException.class, () -> dictMock.getMeaning("word"));

	}

	@Test
	void whenConfigVoidReturnMethodToThrowExWithNewExObj_thenExIsThrown() {
		MyDictionary dictMock = mock(MyDictionary.class);
		doThrow(new IllegalStateException("Error occurred")).when(dictMock)
			.add(anyString(), anyString());
		assertThrows(IllegalStateException.class, () -> dictMock.add("word", "meaning"));

	}

	// =====

	@Test
	void givenSpy_whenConfigNonVoidReturnMethodToThrowEx_thenExIsThrown() {
		MyDictionary dict = new MyDictionary();
		MyDictionary spy = Mockito.spy(dict);
		when(spy.getMeaning(anyString())).thenThrow(NullPointerException.class);
		assertThrows(NullPointerException.class, () -> spy.getMeaning("word"));
	}
}