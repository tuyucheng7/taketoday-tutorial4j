package cn.tuyucheng.taketoday.performancetests.model.destination;

import cn.tuyucheng.taketoday.performancetests.model.source.Address;
import com.googlecode.jmapper.annotations.JGlobalMap;

import java.util.List;

@JGlobalMap
public class Shop {

	private String shopName;
	private cn.tuyucheng.taketoday.performancetests.model.source.Address shopAddres;
	private String shopUrl;
	private List<Review> reviews;

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public cn.tuyucheng.taketoday.performancetests.model.source.Address getShopAddres() {
		return shopAddres;
	}

	public void setShopAddres(cn.tuyucheng.taketoday.performancetests.model.source.Address shopAddres) {
		this.shopAddres = shopAddres;
	}

	public String getShopUrl() {
		return shopUrl;
	}

	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}

	public Shop() {
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public Shop(String shopName, Address shopAddres, String shopUrl, List<Review> reviews) {

		this.shopName = shopName;
		this.shopAddres = shopAddres;
		this.shopUrl = shopUrl;
		this.reviews = reviews;
	}
}