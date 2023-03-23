package cn.tuyucheng.taketoday.partevent.web;

import cn.tuyucheng.taketoday.partevent.model.FileUploadCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FilePartEvent;
import org.springframework.http.codec.multipart.FormPartEvent;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Slf4j
@SuppressWarnings("unused")
public class FileUploadController {

	@PostMapping(value = "simple-form-upload-mvc", consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, String>> handleFileUploadForm(@RequestPart("file") MultipartFile file) throws IOException {
		LOGGER.info("handling request parts: {}", file);
		try {
			File f = new ClassPathResource("").getFile();
			final Path path = Paths.get(f.getAbsolutePath() + File.separator + "static" + File.separator + "image");

			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}
			Path filePath = path.resolve(file.getOriginalFilename());
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/image/")
				.path(file.getOriginalFilename())
				.toUriString();

			var result = Map.of(
				"filename", file.getOriginalFilename(),
				"fileUri", fileUri
			);
			return ok().body(result);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "simple-form-upload", consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, String>> handleFileUploadForm(FileUploadCommand form) {
		LOGGER.info("uploading form data: {}", form);

		var result = Map.of(
			"name", form.getName(),
			"filename", form.getFile().filename()
		);
		return ok().body(result);
	}

	@PostMapping(value = "upload-with-request-parts", consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, String>> handleRequestParts(@RequestPart("name") String name, @RequestPart("file") FilePart file) {
		LOGGER.info("handling request parts: {}, {}", name, file);
		var result = Map.of(
			"name", name,
			"filename", file.filename()
		);
		return ok().body(result);
	}

	@PostMapping(value = "upload-with-multi-value-map", consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Mono<List<String>>> handleMultiValueMap(@RequestBody Mono<MultiValueMap<String, Part>> parts) {
		LOGGER.debug("handling multi values: {}", parts);
		var partNames = parts.map
				(p -> p.keySet().stream().map(key -> p.getFirst(key).name()).toList())
			.log();
		return ok().body(partNames);
	}

	@PostMapping("upload-with-part-events")
	public ResponseEntity<Flux<String>> handlePartsEvents(@RequestBody Flux<PartEvent> allPartsEvents) {
		var result = allPartsEvents.windowUntil(PartEvent::isLast)
			.concatMap(p -> p.switchOnFirst((signal, partEvents) -> {
						if (signal.hasValue()) {
							PartEvent event = signal.get();
							if (event instanceof FormPartEvent formEvent) {
								// handle form field
								String value = formEvent.value();
								LOGGER.info("form value : {}", value);
								return Mono.just(value + "\n");
							} else if (event instanceof FilePartEvent fileEvent) {
								// handle file upload
								String filename = fileEvent.filename();
								LOGGER.info("upload file name : {}", filename);
								Flux<DataBuffer> contents = partEvents.map(PartEvent::content);
								var fileBytes = DataBufferUtils.join(contents)
									.map(dataBuffer -> {
										byte[] bytes = new byte[dataBuffer.readableByteCount()];
										dataBuffer.read(bytes);
										DataBufferUtils.release(dataBuffer);
										return bytes;
									});

								return Mono.just(filename);
							}

							// no signal value
							return Mono.error(new RuntimeException("Unexpected event: " + event));
						}

						LOGGER.info("return default flux");
						// return result;
						return Flux.empty(); // either complete or error signal
					}
				)
			);
		return ok().body(result);
	}
}