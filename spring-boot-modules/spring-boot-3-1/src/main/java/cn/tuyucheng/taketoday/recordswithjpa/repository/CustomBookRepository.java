package cn.tuyucheng.taketoday.recordswithjpa.repository;

import cn.tuyucheng.taketoday.recordswithjpa.records.CustomBookRecord;

import java.util.List;

public interface CustomBookRepository {
	List<CustomBookRecord> findAllBooks();
}