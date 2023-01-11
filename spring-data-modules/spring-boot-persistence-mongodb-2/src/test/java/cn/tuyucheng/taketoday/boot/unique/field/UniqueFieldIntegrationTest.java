package cn.tuyucheng.taketoday.boot.unique.field;

import cn.tuyucheng.taketoday.boot.unique.field.dao.AssetRepository;
import cn.tuyucheng.taketoday.boot.unique.field.dao.CompanyRepository;
import cn.tuyucheng.taketoday.boot.unique.field.dao.CustomerRepository;
import cn.tuyucheng.taketoday.boot.unique.field.dao.SaleRepository;
import cn.tuyucheng.taketoday.boot.unique.field.data.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertThrows;

@SpringBootTest(classes = SpringBootUniqueFieldApplication.class)
@DirtiesContext
@ExtendWith(SpringExtension.class)
@TestPropertySource("/embedded.properties")
class UniqueFieldIntegrationTest {
    @Autowired
    private SaleRepository saleRepo;

    @Autowired
    private CompanyRepository companyRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AssetRepository assetRepo;

    @Test
    void givenMultipleIndexes_whenAnyFieldDupe_thenExceptionIsThrown() {
        Asset a = new Asset();
        a.setName("Name");
        a.setNumber(1);

        assetRepo.insert(a);

        Asset b = new Asset();
        b.setName("Name");
        b.setNumber(2);
        assertThrows(DuplicateKeyException.class, () -> assetRepo.insert(b));

        Asset c = new Asset();
        c.setName("Other");
        c.setNumber(1);
        assertThrows(DuplicateKeyException.class, () -> assetRepo.insert(c));
    }

    @Test
    void givenUniqueIndex_whenInsertingDupe_thenExceptionIsThrown() {
        Company a = new Company();
        a.setName("Name");
        a.setEmail("a@mail.com");

        companyRepo.insert(a);

        Company b = new Company();
        b.setName("Other");
        b.setEmail("a@mail.com");
        assertThrows(DuplicateKeyException.class, () -> companyRepo.insert(b));
    }

    @Test
    void givenCompoundIndex_whenDupeInsert_thenExceptionIsThrown() {
        Customer customerA = new Customer("Name A");
        customerA.setNumber(1L);
        customerA.setStoreId(2L);

        Customer customerB = new Customer("Name B");
        customerB.setNumber(1L);
        customerB.setStoreId(2L);

        customerRepo.insert(customerA);

        assertThrows(DuplicateKeyException.class, () -> customerRepo.insert(customerB));
    }

    @Test
    void givenCustomTypeIndex_whenInsertingDupe_thenExceptionIsThrown() {
        SaleId id = new SaleId();
        id.setDate("2022-06-15");
        id.setItem(1L);

        Sale a = new Sale(id);
        a.setValue(53.94);

        saleRepo.insert(a);

        Sale b = new Sale(id);
        b.setValue(100.00);
        assertThrows(DuplicateKeyException.class, () -> saleRepo.insert(b));
    }
}