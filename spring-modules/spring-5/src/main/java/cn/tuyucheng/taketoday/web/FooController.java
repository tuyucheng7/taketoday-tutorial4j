package cn.tuyucheng.taketoday.web;

import cn.tuyucheng.taketoday.persistence.FooRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController("/foos")
public class FooController {

	@PostConstruct
	public void init() {
		System.out.println("test");
	}

	@Autowired
	private FooRepository repo;

	@GetMapping("/foos/{id}")
	@ResponseBody
	@Validated
	public Foo findById(@PathVariable @Min(0) final long id) {
		return repo.findById(id)
			.orElse(null);
	}

	@GetMapping
	@ResponseBody
	public List<Foo> findAll() {
		return repo.findAll();
	}

	@GetMapping(params = {"page", "size"})
	@ResponseBody
	@Validated
	public List<Foo> findPaginated(@RequestParam("page") @Min(0) final int page, @Max(100) @RequestParam("size") final int size) {
		return repo.findAll(PageRequest.of(page, size))
			.getContent();
	}

	@PutMapping("/foos/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Foo update(@PathVariable("id") final String id, @RequestBody final Foo foo) {
		return foo;
	}
}