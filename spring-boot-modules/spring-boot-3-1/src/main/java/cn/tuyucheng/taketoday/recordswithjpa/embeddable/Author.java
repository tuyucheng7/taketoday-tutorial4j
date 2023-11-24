package cn.tuyucheng.taketoday.recordswithjpa.embeddable;

import jakarta.persistence.Embeddable;

@Embeddable
public record Author(
      String firstName,
      String lastName
) {
}