package cn.tuyucheng.taketoday.unmappedproperties.mapper;

import cn.tuyucheng.taketoday.unmappedproperties.dto.DocumentDTO;
import cn.tuyucheng.taketoday.unmappedproperties.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DocumentMapperMappingIgnore {

	DocumentMapperMappingIgnore INSTANCE = Mappers.getMapper(DocumentMapperMappingIgnore.class);

	@Mapping(target = "comments", ignore = true)
	DocumentDTO documentToDocumentDTO(Document entity);

	@Mapping(target = "modificationTime", ignore = true)
	Document documentDTOToDocument(DocumentDTO dto);

}