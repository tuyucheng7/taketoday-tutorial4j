package cn.tuyucheng.taketoday.embeddable;

import cn.tuyucheng.taketoday.Application;
import cn.tuyucheng.taketoday.embeddable.model.Company;
import cn.tuyucheng.taketoday.embeddable.model.ContactPerson;
import cn.tuyucheng.taketoday.embeddable.repositories.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
class EmbeddableIntegrationTest {

	@Autowired
	private CompanyRepository companyRepository;

	@Test
	@Transactional
	void whenInsertingCompany_thenEmbeddedContactPersonDetailsAreMapped() {
		ContactPerson contactPerson = new ContactPerson();
		contactPerson.setFirstName("First");
		contactPerson.setLastName("Last");
		contactPerson.setPhone("123-456-789");

		Company company = new Company();
		company.setName("Company");
		company.setAddress("1st street");
		company.setPhone("987-654-321");
		company.setContactPerson(contactPerson);

		companyRepository.save(company);

		Company result = companyRepository.getOne(company.getId());

		assertEquals("Company", result.getName());
		assertEquals("1st street", result.getAddress());
		assertEquals("987-654-321", result.getPhone());
		assertEquals("First", result.getContactPerson().getFirstName());
		assertEquals("Last", result.getContactPerson().getLastName());
		assertEquals("123-456-789", result.getContactPerson().getPhone());
	}

	@Test
	@Transactional
	void whenFindingCompanyByContactPersonAttribute_thenCompanyIsReturnedProperly() {
		ContactPerson contactPerson = new ContactPerson();
		contactPerson.setFirstName("Name");
		contactPerson.setLastName("Last");
		contactPerson.setPhone("123-456-789");

		Company company = new Company();
		company.setName("Company");
		company.setAddress("1st street");
		company.setPhone("987-654-321");
		company.setContactPerson(contactPerson);

		companyRepository.save(company);

		List<Company> result = companyRepository.findByContactPersonFirstName("Name");

		assertEquals(1, result.size());

		result = companyRepository.findByContactPersonFirstName("FirstName");

		assertEquals(0, result.size());
	}

	@Test
	@Transactional
	void whenFindingCompanyByContactPersonAttributeWithJPQL_thenCompanyIsReturnedProperly() {
		ContactPerson contactPerson = new ContactPerson();
		contactPerson.setFirstName("@QueryName");
		contactPerson.setLastName("Last");
		contactPerson.setPhone("123-456-789");

		Company company = new Company();
		company.setName("Company");
		company.setAddress("1st street");
		company.setPhone("987-654-321");
		company.setContactPerson(contactPerson);

		companyRepository.save(company);

		List<Company> result = companyRepository.findByContactPersonFirstNameWithJPQL("@QueryName");

		assertEquals(1, result.size());

		result = companyRepository.findByContactPersonFirstNameWithJPQL("FirstName");

		assertEquals(0, result.size());
	}

	@Test
	@Transactional
	void whenFindingCompanyByContactPersonAttributeWithNativeQuery_thenCompanyIsReturnedProperly() {
		ContactPerson contactPerson = new ContactPerson();
		contactPerson.setFirstName("NativeQueryName");
		contactPerson.setLastName("Last");
		contactPerson.setPhone("123-456-789");

		Company company = new Company();
		company.setName("Company");
		company.setAddress("1st street");
		company.setPhone("987-654-321");
		company.setContactPerson(contactPerson);

		companyRepository.save(company);

		List<Company> result = companyRepository.findByContactPersonFirstNameWithNativeQuery("NativeQueryName");

		assertEquals(1, result.size());

		result = companyRepository.findByContactPersonFirstNameWithNativeQuery("FirstName");

		assertEquals(0, result.size());
	}
}