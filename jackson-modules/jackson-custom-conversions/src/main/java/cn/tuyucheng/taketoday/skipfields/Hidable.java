package cn.tuyucheng.taketoday.skipfields;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("hidden")
public interface Hidable {
	boolean isHidden();
}
