package cn.tuyucheng.taketoday.boot.collection.name.service;

import cn.tuyucheng.taketoday.boot.collection.name.SpringBootCollectionNameApplication;
import cn.tuyucheng.taketoday.boot.collection.name.data.Compilation;
import cn.tuyucheng.taketoday.boot.collection.name.data.MusicAlbum;
import cn.tuyucheng.taketoday.boot.collection.name.data.MusicTrack;
import cn.tuyucheng.taketoday.boot.collection.name.data.Store;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SpringBootCollectionNameApplication.class)
@DirtiesContext
@ExtendWith(SpringExtension.class)
@TestPropertySource("/embedded.properties")
class MusicStoreServiceIntegrationTest {
    @Autowired
    private MusicStoreService service;

    @Autowired
    private MongoTemplate mongoDb;

    @Test
    void givenAnnotation_whenSearchingByCollectionName_thenFound() {
        List<Compilation> list = service.getCompilationList();
        int sizeBefore = list.size();

        service.add(new Compilation("Spring Hits"));

        list = mongoDb.findAll(Compilation.class, "compilation");
        int sizeAfter = list.size();

        assertThat(sizeAfter - sizeBefore).isEqualTo(1);
    }

    @Test
    void givenAnnotationWithValue_whenSearchingByCollectionName_thenFound() {
        List<MusicAlbum> list = service.getAlbumList();
        int sizeBefore = list.size();

        service.add(new MusicAlbum("Album 1", "Artist A"));

        list = mongoDb.findAll(MusicAlbum.class, "albums");
        int sizeAfter = list.size();

        assertThat(sizeAfter - sizeBefore).isEqualTo(1);
    }

    @Test
    void givenAnnotationWithSpELEnvironment_whenSearchingByCollectionName_thenFound() {
        List<Store> list = service.getStoreList();
        int sizeBefore = list.size();

        service.add(new Store("Store A"));

        list = mongoDb.findAll(Store.class, "store-db");
        int sizeAfter = list.size();

        assertThat(sizeAfter - sizeBefore).isEqualTo(1);
    }

    @Test
    void givenAnnotationWithSpELBean_whenSearchingByCollectionName_thenFound() {
        List<MusicTrack> list = service.getTrackList();
        int sizeBefore = list.size();

        service.add(new MusicTrack("Track 1"));

        list = mongoDb.findAll(MusicTrack.class, "music_track");
        int sizeAfter = list.size();

        assertThat(sizeAfter - sizeBefore).isEqualTo(1);
    }
}