package cn.tuyucheng.taketoday.mapstruct.mappingCollections.mapper;

import cn.tuyucheng.taketoday.mapstruct.mappingCollections.dto.CompanyDTO;
import cn.tuyucheng.taketoday.mapstruct.mappingCollections.model.Company;
import org.mapstruct.Mapper;

@Mapper(uses = EmployeeMapper.class)
public interface CompanyMapper {

	CompanyDTO map(Company company);
}
