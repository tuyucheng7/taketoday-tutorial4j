package cn.tuyucheng.taketoday.boot.naming;

import cn.tuyucheng.taketoday.boot.naming.NamingConfig.Config;
import cn.tuyucheng.taketoday.boot.naming.entity.Account;
import org.assertj.core.api.SoftAssertions;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {
	"spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl",
	"spring.jpa.hibernate.naming.implicit-strategy=org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl"
})
@Import(Config.class)
class LegacyJpaImplNamingIntegrationTest extends NamingConfig {

	@Test
	void givenLegacyJpaImplNamingStrategy_whenCreateDatabase_thenGetStrategyNames() {
		Metadata metadata = MetadataExtractorIntegrator.INSTANCE.getMetadata();
		String entity = Account.class.getCanonicalName();
		PersistentClass persistentClass = metadata.getEntityBinding(entity);
		Table table = persistentClass.getTable();
		String physicalNameExpected = "Secondary_Email";
		String implicitNameExpected = "defaultEmail";
		String tableNameExpected = "Account";

		String tableNameCreated = table.getName();
		boolean columnNameIsQuoted = table.getColumn(3).isQuoted();
		String physicalNameCreated = table.getColumn(3).getName();
		String implicitNameCreated = table.getColumn(2).getName();

		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(columnNameIsQuoted).isTrue();
			softly.assertThat(tableNameCreated).isEqualTo(tableNameExpected);
			softly.assertThat(physicalNameCreated).isEqualTo(physicalNameExpected);
			softly.assertThat(implicitNameCreated).isEqualTo(implicitNameExpected);
		});
	}
}