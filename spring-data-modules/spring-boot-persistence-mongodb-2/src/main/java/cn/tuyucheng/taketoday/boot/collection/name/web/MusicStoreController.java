package cn.tuyucheng.taketoday.boot.collection.name.web;

import cn.tuyucheng.taketoday.boot.collection.name.data.Compilation;
import cn.tuyucheng.taketoday.boot.collection.name.data.MusicAlbum;
import cn.tuyucheng.taketoday.boot.collection.name.data.MusicTrack;
import cn.tuyucheng.taketoday.boot.collection.name.data.Store;
import cn.tuyucheng.taketoday.boot.collection.name.service.MusicStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/music")
public class MusicStoreController {
	@Autowired
	private MusicStoreService service;

	@PostMapping("/album")
	public MusicAlbum post(@RequestBody MusicAlbum item) {
		return service.add(item);
	}

	@GetMapping("/album")
	public List<MusicAlbum> getAlbumList() {
		return service.getAlbumList();
	}

	@PostMapping("/compilation")
	public Compilation post(@RequestBody Compilation item) {
		return service.add(item);
	}

	@GetMapping("/compilation")
	public List<Compilation> getCompilationList() {
		return service.getCompilationList();
	}

	@PostMapping("/store")
	public Store post(@RequestBody Store item) {
		return service.add(item);
	}

	@GetMapping("/store")
	public List<Store> getStoreList() {
		return service.getStoreList();
	}

	@PostMapping("/track")
	public MusicTrack post(@RequestBody MusicTrack item) {
		return service.add(item);
	}

	@GetMapping("/track")
	public List<MusicTrack> getTrackList() {
		return service.getTrackList();
	}
}