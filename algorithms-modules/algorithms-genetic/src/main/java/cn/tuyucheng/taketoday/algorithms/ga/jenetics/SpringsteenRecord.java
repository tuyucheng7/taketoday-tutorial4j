package cn.tuyucheng.taketoday.algorithms.ga.jenetics;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jenetics.util.ISeq;

import static java.util.Objects.requireNonNull;

@Data
@NoArgsConstructor
public class SpringsteenRecord {

	String name;
	double price;
	ISeq<String> songs;

	public SpringsteenRecord(String name, double price, ISeq<String> songs) {
		this.name = requireNonNull(name);
		this.price = price;
		this.songs = requireNonNull(songs);
	}

}
