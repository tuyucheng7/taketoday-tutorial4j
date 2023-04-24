package cn.tuyucheng.taketoday.entity;

import org.springframework.data.annotation.Id;

public record Pet(@Id String id, String name) {
}