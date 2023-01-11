package cn.tuyucheng.taketoday.spring.data.reactive.redis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Employee implements Serializable {
	private static final long serialVersionUID = 1603714798906422731L;
	private String id;
	private String name;
	private String department;
}
