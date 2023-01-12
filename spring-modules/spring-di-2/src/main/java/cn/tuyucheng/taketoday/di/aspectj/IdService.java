package cn.tuyucheng.taketoday.di.aspectj;

import org.springframework.stereotype.Service;

@Service
public class IdService {
	private static int count;

	int generateId() {
		return ++count;
	}
}
