package cn.tuyucheng.taketoday.kubernetes.admission.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AdmissionStatus {

	int code;
	String message;
}