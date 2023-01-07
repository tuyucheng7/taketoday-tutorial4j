package cn.tuyucheng.taketoday.accessmodifiers.another;

import cn.tuyucheng.taketoday.accessmodifiers.SuperPublic;

public class AnotherPublic {
	public AnotherPublic() {
		SuperPublic.publicMethod(); // Available everywhere.
	}
}
