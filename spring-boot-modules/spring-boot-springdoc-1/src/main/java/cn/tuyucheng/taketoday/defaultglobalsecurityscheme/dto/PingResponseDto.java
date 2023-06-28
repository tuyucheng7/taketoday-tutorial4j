package cn.tuyucheng.taketoday.defaultglobalsecurityscheme.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * SecuredPingResponseDto
 */
@JsonTypeName("PingResponse")
public class PingResponseDto {

	@JsonProperty("pong")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private OffsetDateTime pong;

	public PingResponseDto pong(OffsetDateTime pong) {
		this.pong = pong;
		return this;
	}

	/**
	 * Get pong
	 *
	 * @return pong
	 */
	@Valid
	@Schema(name = "pong", required = false)
	public OffsetDateTime getPong() {
		return pong;
	}

	public void setPong(OffsetDateTime pong) {
		this.pong = pong;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PingResponseDto securedPingResponse = (PingResponseDto) o;
		return Objects.equals(this.pong, securedPingResponse.pong);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pong);
	}

	@Override
	public String toString() {
		return "class PingResponseDto {\n" +
			"    pong: " +
			toIndentedString(pong) +
			"\n" +
			"}";
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}