package cn.tuyucheng.taketoday.boot.naming;

import cn.tuyucheng.taketoday.boot.naming.NamingConfig.Config;
import cn.tuyucheng.taketoday.boot.naming.entity.Preference;
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

import java.util.Collection;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {
	"spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl",
	"spring.jpa.hibernate.naming.implicit-strategy=org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyHbmImpl",
})
@Import(Config.class)
class StrategyLegacyHbmImplIntegrationTest extends NamingConfig {

	@Test
	void givenLegacyHbmImplNamingNamingStrategy_whenCreateDatabase_thenGetStrategyNames() {
		Metadata metadata = MetadataExtractorIntegrator.INSTANCE.getMetadata();
		String entity = Preference.class.getCanonicalName();
		PersistentClass persistentClass = metadata.getEntityBinding(entity);
		Collection<Table> tables = metadata
			.getDatabase()
			.getDefaultNamespace()
			.getTables();
		Table preferenceTable = persistentClass.getTable();
		String tableNameExpected = "Account_preferences";
		Table accountPreferencesTable = tables
			.stream()
			.filter(table -> table
				.getName()
				.equals(tableNameExpected))
			.findFirst()
			.get();
		String implicitNameExpected = "account";

		String implicitNameCreated = preferenceTable.getColumn(3).getName();
		String tableNameCreated = accountPreferencesTable.getName();

		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(implicitNameCreated).isEqualTo(implicitNameExpected);
			softly.assertThat(tableNameCreated).isEqualTo(tableNameExpected);
		});
	}
}