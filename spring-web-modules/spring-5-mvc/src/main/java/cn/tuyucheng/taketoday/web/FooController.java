package cn.tuyucheng.taketoday.web;

import cn.tuyucheng.taketoday.model.Foo;
import cn.tuyucheng.taketoday.persistence.FooRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
public class FooController {

	@Autowired
	private FooRepository repo;

	@GetMapping("/foos/{id}")
	@Validated
	public Foo findById(@PathVariable @Min(0) final long id) {
		return repo.findById(id).orElse(null);
	}

	@GetMapping("/foos")
	public List<Foo> findAll() {
		return repo.findAll();
	}

	@GetMapping(value = "/foos", params = {"page", "size"})
	@Validated
	public List<Foo> findPaginated(@RequestParam("page") @Min(0) final int page, @Max(100) @RequestParam("size") final int size) {
		return repo.findAll(PageRequest.of(page, size)).getContent();
	}

	@PutMapping("/foos/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Foo update(@PathVariable("id") final String id, @RequestBody final Foo foo) {
		return foo;
	}

	@PostMapping("/foos")
	@ResponseStatus(HttpStatus.CREATED)
	public void create(@RequestBody final Foo foo) {
		if (null == foo || null == foo.getName()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, " 'name' is required");
		}
		repo.save(foo);
	}
}