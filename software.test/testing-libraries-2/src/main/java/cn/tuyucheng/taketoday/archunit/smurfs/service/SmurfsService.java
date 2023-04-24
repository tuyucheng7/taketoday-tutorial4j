package cn.tuyucheng.taketoday.archunit.smurfs.service;

import cn.tuyucheng.taketoday.archunit.smurfs.persistence.SmurfsRepository;
import cn.tuyucheng.taketoday.archunit.smurfs.persistence.domain.Smurf;
import cn.tuyucheng.taketoday.archunit.smurfs.service.dto.SmurfDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SmurfsService {

	private SmurfsRepository repository;

	public SmurfsService(SmurfsRepository repository) {
		this.repository = repository;
	}

	public List<SmurfDTO> findAll() {

		return repository.findAll()
			.stream()
			.map(SmurfsService::toDTO)
			.collect(Collectors.toList());
	}


	public static SmurfDTO toDTO(Smurf smurf) {
		return new SmurfDTO(smurf.getName(), smurf.isComic(), smurf.isCartoon());
	}

}
