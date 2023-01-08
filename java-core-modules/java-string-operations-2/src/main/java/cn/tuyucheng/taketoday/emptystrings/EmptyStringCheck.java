package cn.tuyucheng.taketoday.emptystrings;

class EmptyStringCheck {

	boolean isEmptyString(String string) {
		return string == null || string.isEmpty();
	}
}
