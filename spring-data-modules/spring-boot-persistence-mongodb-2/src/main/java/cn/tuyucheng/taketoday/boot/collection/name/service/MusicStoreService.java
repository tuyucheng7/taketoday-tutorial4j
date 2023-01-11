package cn.tuyucheng.taketoday.boot.collection.name.service;

import cn.tuyucheng.taketoday.boot.collection.name.dao.CompilationRepository;
import cn.tuyucheng.taketoday.boot.collection.name.dao.MusicAlbumRepository;
import cn.tuyucheng.taketoday.boot.collection.name.dao.MusicTrackRepository;
import cn.tuyucheng.taketoday.boot.collection.name.dao.StoreRepository;
import cn.tuyucheng.taketoday.boot.collection.name.data.Compilation;
import cn.tuyucheng.taketoday.boot.collection.name.data.MusicAlbum;
import cn.tuyucheng.taketoday.boot.collection.name.data.MusicTrack;
import cn.tuyucheng.taketoday.boot.collection.name.data.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicStoreService {
    @Autowired
    private MusicAlbumRepository albumRepository;

    @Autowired
    private CompilationRepository compilationRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MusicTrackRepository trackRepository;

    public MusicAlbum add(MusicAlbum item) {
        return albumRepository.save(item);
    }

    public List<MusicAlbum> getAlbumList() {
        return albumRepository.findAll();
    }

    public Compilation add(Compilation item) {
        return compilationRepository.save(item);
    }

    public List<Compilation> getCompilationList() {
        return compilationRepository.findAll();
    }

    public Store add(Store item) {
        return storeRepository.save(item);
    }

    public List<Store> getStoreList() {
        return storeRepository.findAll();
    }

    public MusicTrack add(MusicTrack item) {
        return trackRepository.save(item);
    }

    public List<MusicTrack> getTrackList() {
        return trackRepository.findAll();
    }
}