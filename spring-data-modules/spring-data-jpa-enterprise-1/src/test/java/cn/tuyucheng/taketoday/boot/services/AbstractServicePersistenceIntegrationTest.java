package cn.tuyucheng.taketoday.boot.services;

import cn.tuyucheng.taketoday.boot.domain.Foo;
import cn.tuyucheng.taketoday.util.IDUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractServicePersistenceIntegrationTest<T extends Serializable> {

    @Test
    final void givenResourceDoesNotExist_whenResourceIsRetrieved_thenNoResourceIsReceived() {
        // When
        final Foo createdResource = getApi().findOne(IDUtil.randomPositiveLong());

        // Then
        assertNull(createdResource);
    }

    @Test
    void givenResourceExists_whenResourceIsRetrieved_thenNoExceptions() {
        final Foo existingResource = persistNewEntity();
        getApi().findOne(existingResource.getId());
    }

    @Test
    void givenResourceDoesNotExist_whenResourceIsRetrieved_thenNoExceptions() {
        getApi().findOne(IDUtil.randomPositiveLong());
    }

    @Test
    void givenResourceExists_whenResourceIsRetrieved_thenTheResultIsNotNull() {
        final Foo existingResource = persistNewEntity();
        final Foo retrievedResource = getApi().findOne(existingResource.getId());
        assertNotNull(retrievedResource);
    }

    @Test
    void givenResourceExists_whenResourceIsRetrieved_thenResourceIsRetrievedCorrectly() {
        final Foo existingResource = persistNewEntity();
        final Foo retrievedResource = getApi().findOne(existingResource.getId());
        assertEquals(existingResource, retrievedResource);
    }

    // find - one - by name

    // find - all

    @Test
    void whenAllResourcesAreRetrieved_thenNoExceptions() {
        getApi().findAll();
    }

    @Test
    void whenAllResourcesAreRetrieved_thenTheResultIsNotNull() {
        final List<Foo> resources = getApi().findAll();

        assertNotNull(resources);
    }

    @Test
    void givenAtLeastOneResourceExists_whenAllResourcesAreRetrieved_thenRetrievedResourcesAreNotEmpty() {
        persistNewEntity();

        // When
        final List<Foo> allResources = getApi().findAll();

        // Then
        assertThat(allResources, not(Matchers.<Foo>empty()));
    }

    @Test
    void givenAnResourceExists_whenAllResourcesAreRetrieved_thenTheExistingResourceIsIndeedAmongThem() {
        final Foo existingResource = persistNewEntity();

        final List<Foo> resources = getApi().findAll();

        assertThat(resources, hasItem(existingResource));
    }

    @Test
    void whenAllResourcesAreRetrieved_thenResourcesHaveIds() {
        persistNewEntity();

        // When
        final List<Foo> allResources = getApi().findAll();

        // Then
        for (final Foo resource : allResources) {
            assertNotNull(resource.getId());
        }
    }

    @Test
    void whenNullResourceIsCreated_thenException() {
        assertThrows(RuntimeException.class, () -> getApi().create(null));
    }

    @Test
    void whenResourceIsCreated_thenNoExceptions() {
        persistNewEntity();
    }

    @Test
    void whenResourceIsCreated_thenResourceIsRetrievable() {
        final Foo existingResource = persistNewEntity();

        assertNotNull(getApi().findOne(existingResource.getId()));
    }

    @Test
    void whenResourceIsCreated_thenSavedResourceIsEqualToOriginalResource() {
        final Foo originalResource = createNewEntity();
        final Foo savedResource = getApi().create(originalResource);

        assertEquals(originalResource, savedResource);
    }

    // @Test(expected = RuntimeException.class)
    // void whenResourceWithFailedConstraintsIsCreated_thenException() {
    // 	final Foo invalidResource = createNewEntity();
    // 	invalidate(invalidResource);
    //
    // 	getApi().create(invalidResource);
    // }

    /**
     * -- specific to the persistence engine
     */
    // @Test(expected = DataAccessException.class)
    // @Disabled("Hibernate simply ignores the id silently and still saved (tracking this)")
    // void whenResourceWithIdIsCreated_thenDataAccessException() {
    // 	final Foo resourceWithId = createNewEntity();
    // 	resourceWithId.setId(IDUtil.randomPositiveLong());
    //
    // 	getApi().create(resourceWithId);
    // }

    // update

    // @Test(expected = RuntimeException.class)
    // void whenNullResourceIsUpdated_thenException() {
    // 	getApi().update(null);
    // }
    @Test
    void givenResourceExists_whenResourceIsUpdated_thenNoExceptions() {
        // Given
        final Foo existingResource = persistNewEntity();

        // When
        getApi().update(existingResource);
    }

    /**
     * - can also be the ConstraintViolationException which now occurs on the update operation will not be translated; as a consequence, it will be a TransactionSystemException
     */
    // @Test(expected = RuntimeException.class)
    // void whenResourceIsUpdatedWithFailedConstraints_thenException() {
    //     final Foo existingResource = persistNewEntity();
    //     invalidate(existingResource);
    //
    //     getApi().update(existingResource);
    // }

    @Test
    void givenResourceExists_whenResourceIsUpdated_thenUpdatesArePersisted() {
        // Given
        final Foo existingResource = persistNewEntity();

        // When
        change(existingResource);
        getApi().update(existingResource);

        final Foo updatedResource = getApi().findOne(existingResource.getId());

        // Then
        assertEquals(existingResource, updatedResource);
    }


    // @Test(expected = RuntimeException.class)
    // void givenResourceDoesNotExists_whenResourceIsDeleted_thenException() {
    //     // When
    //     getApi().delete(IDUtil.randomPositiveLong());
    // }
    //
    // @Test(expected = RuntimeException.class)
    // void whenResourceIsDeletedByNegativeId_thenException() {
    //     // When
    //     getApi().delete(IDUtil.randomNegativeLong());
    // }
    //
    // @Test
    // void givenResourceExists_whenResourceIsDeleted_thenNoExceptions() {
    //     // Given
    //     final Foo existingResource = persistNewEntity();
    //
    //     // When
    //     getApi().delete(existingResource.getId());
    // }

    // @Test
    // final void givenResourceExists_whenResourceIsDeleted_thenResourceNoLongerExists() {
    //     // Given
    //     final Foo existingResource = persistNewEntity();
    //
    //     // When
    //     getApi().delete(existingResource.getId());
    //
    //     // Then
    //     assertNull(getApi().findOne(existingResource.getId()));
    // }

    // template method

    protected Foo createNewEntity() {
        return new Foo(randomAlphabetic(6));
    }

    protected abstract Operations<Foo> getApi();

    private final void invalidate(final Foo entity) {
        entity.setName(null);
    }

    private final void change(final Foo entity) {
        entity.setName(randomAlphabetic(6));
    }

    protected Foo persistNewEntity() {
        return getApi().create(createNewEntity());
    }
}