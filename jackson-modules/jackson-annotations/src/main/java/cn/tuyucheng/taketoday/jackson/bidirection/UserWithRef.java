package cn.tuyucheng.taketoday.jackson.bidirection;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.List;

public class UserWithRef {
	public int id;
	public String name;

	@JsonManagedReference
	public List<ItemWithRef> userItems;

	public UserWithRef() {
		super();
	}

	public UserWithRef(final int id, final String name) {
		this.id = id;
		this.name = name;
		userItems = new ArrayList<>();
	}

	public void addItem(final ItemWithRef item) {
		userItems.add(item);
	}
}
