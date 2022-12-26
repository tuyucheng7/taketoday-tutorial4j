package cn.tuyucheng.taketoday.enummapping.editors;

import cn.tuyucheng.taketoday.enummapping.enums.Level;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LevelEditorIntegrationTest {

	private final LevelEditor levelEditor = new LevelEditor();

	@Test
	void whenConvertStringToLevelEnumUsingCustomPropertyEditor_thenSuccess() {
		levelEditor.setAsText("lOw");

		assertThat(levelEditor.getValue()).isEqualTo(Level.LOW);
	}

	@Test
	void whenStringIsEmpty_thenReturnNull() {
		levelEditor.setAsText("");

		assertThat(levelEditor.getValue()).isNull();
	}

	@Test
	void whenStringIsNull_thenReturnNull() {
		levelEditor.setAsText(null);

		assertThat(levelEditor.getValue()).isNull();
	}
}