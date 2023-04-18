package cn.tuyucheng.taketoday.springbootreact.controller;

import cn.tuyucheng.taketoday.springbootreact.domain.Client;
import cn.tuyucheng.taketoday.springbootreact.repository.ClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientsController {

	private final ClientRepository clientRepository;

	public ClientsController(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	@GetMapping
	public List<Client> getClients() {
		return clientRepository.findAll();
	}

	@GetMapping("/{id}")
	public Client getClient(@PathVariable Long id) {
		return clientRepository.findById(id).orElseThrow(RuntimeException::new);
	}

	@PostMapping
	public ResponseEntity createClient(@RequestBody Client client) throws URISyntaxException {
		Client savedClient = clientRepository.save(client);
		return ResponseEntity.created(new URI("/clients/" + savedClient.getId())).body(savedClient);
	}

	@PutMapping("/{id}")
	public ResponseEntity updateClient(@PathVariable Long id, @RequestBody Client client) {
		Client currentClient = clientRepository.findById(id).orElseThrow(RuntimeException::new);
		currentClient.setName(client.getName());
		currentClient.setEmail(client.getEmail());
		currentClient = clientRepository.save(client);

		return ResponseEntity.ok(currentClient);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity deleteClient(@PathVariable Long id) {
		clientRepository.deleteById(id);
		return ResponseEntity.ok().build();
	}
}