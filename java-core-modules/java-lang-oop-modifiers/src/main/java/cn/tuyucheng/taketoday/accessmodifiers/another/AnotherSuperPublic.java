package cn.tuyucheng.taketoday.accessmodifiers.another;

import cn.tuyucheng.taketoday.accessmodifiers.SuperPublic;

public class AnotherSuperPublic {
	public AnotherSuperPublic() {
		SuperPublic.publicMethod(); // Available everywhere. Let's note different package.
	}
}
