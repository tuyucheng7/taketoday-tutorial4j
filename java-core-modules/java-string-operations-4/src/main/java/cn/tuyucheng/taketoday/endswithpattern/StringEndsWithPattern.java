package cn.tuyucheng.taketoday.endswithpattern;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class StringEndsWithPattern {

	public static boolean usingStringEndsWithMethod(String text, String suffix) {
		if (text == null || suffix == null) {
			return false;
		}
		return text.endsWith(suffix);
	}

	public static boolean usingStringMatchesMethod(String text, String suffix) {
		if (text == null || suffix == null) {
			return false;
		}
		String regex = ".*" + suffix + "$";
		return text.matches(regex);
	}

	public static boolean usingStringRegionMatchesMethod(String text, String suffix) {
		if (text == null || suffix == null) {
			return false;
		}
		int toffset = text.length() - suffix.length();
		return text.regionMatches(toffset, suffix, 0, suffix.length());
	}

	public static boolean usingPatternClass(String text, String suffix) {
		if (text == null || suffix == null) {
			return false;
		}
		Pattern pattern = Pattern.compile(".*" + suffix + "$");
		return pattern.matcher(text)
			.find();
	}

	public static boolean usingApacheCommonsLang(String text, String suffix) {
		return StringUtils.endsWith(text, suffix);
	}

}
