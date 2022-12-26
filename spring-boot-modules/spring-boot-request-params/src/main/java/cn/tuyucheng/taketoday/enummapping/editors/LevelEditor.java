package cn.tuyucheng.taketoday.enummapping.editors;

import cn.tuyucheng.taketoday.enummapping.enums.Level;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;

public class LevelEditor extends PropertyEditorSupport {

	@Override
	public void setAsText(String text) {
		if (StringUtils.isBlank(text)) {
			setValue(null);
		} else {
			setValue(EnumUtils.getEnum(Level.class, text.toUpperCase()));
		}
	}
}