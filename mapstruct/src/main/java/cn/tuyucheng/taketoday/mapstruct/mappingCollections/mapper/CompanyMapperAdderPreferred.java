package cn.tuyucheng.taketoday.mapstruct.mappingCollections.mapper;

import cn.tuyucheng.taketoday.mapstruct.mappingCollections.dto.CompanyDTO;
import cn.tuyucheng.taketoday.mapstruct.mappingCollections.model.Company;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
	uses = EmployeeMapper.class)
public interface CompanyMapperAdderPreferred {

	CompanyDTO map(Company company);
}
