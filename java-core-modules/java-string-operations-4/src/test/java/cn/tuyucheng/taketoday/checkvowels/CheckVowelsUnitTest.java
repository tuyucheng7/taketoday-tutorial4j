package cn.tuyucheng.taketoday.checkvowels;

import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.checkvowels.CheckVowels.isInVowelsString;
import static cn.tuyucheng.taketoday.checkvowels.CheckVowels.isVowelByRegex;
import static cn.tuyucheng.taketoday.checkvowels.CheckVowels.isVowelBySwitch;
import static org.assertj.core.api.Assertions.assertThat;

class CheckVowelsUnitTest {

	@Test
	void givenAVowelCharacter_thenInVowelString() {
		assertThat(isInVowelsString('e')).isTrue();
	}

	@Test
	void givenAConsonantCharacter_thenNotInVowelString() {
		assertThat(isInVowelsString('z')).isFalse();
	}

	@Test
	void givenAVowelString_thenInVowelString() {
		assertThat(isInVowelsString("e")).isTrue();
	}

	@Test
	void givenAConsonantString_thenNotInVowelString() {
		assertThat(isInVowelsString("z")).isFalse();
	}

	@Test
	void givenAVowelCharacter_thenInVowelSwitch() {
		assertThat(isVowelBySwitch('e')).isTrue();
	}

	@Test
	void givenAConsonantCharacter_thenNotInVowelSwitch() {
		assertThat(isVowelBySwitch('z')).isFalse();
	}

	@Test
	void givenAVowelString_thenInVowelPattern() {
		assertThat(isVowelByRegex("e")).isTrue();
		assertThat(isVowelByRegex("E")).isTrue();
	}

	@Test
	void givenAVowelCharacter_thenInVowelPattern() {
		assertThat(isVowelByRegex(Character.toString('e'))).isTrue();
		assertThat(isVowelByRegex("E")).isTrue();
	}
}
