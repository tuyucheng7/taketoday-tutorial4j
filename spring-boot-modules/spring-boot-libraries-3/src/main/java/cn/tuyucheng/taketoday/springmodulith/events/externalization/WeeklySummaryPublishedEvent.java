package cn.tuyucheng.taketoday.springmodulith.events.externalization;

import org.springframework.modulith.events.Externalized;

@Externalized
record WeeklySummaryPublishedEvent(String handle, String heading) {
}