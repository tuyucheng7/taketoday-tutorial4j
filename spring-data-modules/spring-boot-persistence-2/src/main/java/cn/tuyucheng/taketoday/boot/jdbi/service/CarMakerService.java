package cn.tuyucheng.taketoday.boot.jdbi.service;

import cn.tuyucheng.taketoday.boot.jdbi.dao.CarMakerDao;
import cn.tuyucheng.taketoday.boot.jdbi.dao.CarModelDao;
import cn.tuyucheng.taketoday.boot.jdbi.domain.CarMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarMakerService {

	private CarMakerDao carMakerDao;
	private CarModelDao carModelDao;

	@Autowired
	public CarMakerService(CarMakerDao carMakerDao, CarModelDao carModelDao) {
		this.carMakerDao = carMakerDao;
		this.carModelDao = carModelDao;
	}

	@Transactional
	public int bulkInsert(CarMaker carMaker) {
		Long carMakerId;
		if (carMaker.getId() == null) {
			carMakerId = carMakerDao.insert(carMaker);
			carMaker.setId(carMakerId);
		}

		// Make sure all models belong to the same maker
		carMaker.getModels().forEach(m -> {
			m.setMakerId(carMaker.getId());
			carModelDao.insert(m);
		});

		return carMaker.getModels().size();
	}
}