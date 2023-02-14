package com.baeldung.propertyeditor.exotictype.editor;

import com.baeldung.propertyeditor.exotictype.model.ExoticType;

import java.beans.PropertyEditorSupport;

public class CustomExoticTypeEditor extends PropertyEditorSupport {

	@Override
	public String getAsText() {
		ExoticType exoticType = (ExoticType) getValue();

		return exoticType == null ? "" : exoticType.getName();
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		ExoticType exoticType = new ExoticType();
		exoticType.setName(text.toUpperCase());

		setValue(exoticType);
	}
}
