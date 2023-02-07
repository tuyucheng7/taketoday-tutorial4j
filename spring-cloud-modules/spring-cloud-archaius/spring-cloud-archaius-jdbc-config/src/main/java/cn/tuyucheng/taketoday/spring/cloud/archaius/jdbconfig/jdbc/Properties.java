package cn.tuyucheng.taketoday.spring.cloud.archaius.jdbconfig.jdbc;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Properties {

	@Id
	private String key;

	@SuppressWarnings("unused")
	private String value;
}