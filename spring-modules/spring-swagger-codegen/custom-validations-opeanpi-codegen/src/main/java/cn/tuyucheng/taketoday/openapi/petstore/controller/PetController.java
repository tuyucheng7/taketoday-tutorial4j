package cn.tuyucheng.taketoday.openapi.petstore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;

@Controller
public class PetController implements cn.tuyucheng.taketoday.openapi.api.PetsApi {

	public ResponseEntity<List<cn.tuyucheng.taketoday.openapi.model.Pet>> findPetsByName(String name) {
		return ResponseEntity.ok(Arrays.asList(new cn.tuyucheng.taketoday.openapi.model.Pet().id(1L).name(name)));
	}
}