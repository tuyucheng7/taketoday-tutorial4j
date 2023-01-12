package cn.tuyucheng.taketoday.unmappedproperties.mapper;

import cn.tuyucheng.taketoday.unmappedproperties.dto.DocumentDTO;
import cn.tuyucheng.taketoday.unmappedproperties.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface DocumentMapperWithConfig {

	DocumentMapperWithConfig INSTANCE = Mappers.getMapper(DocumentMapperWithConfig.class);

	DocumentDTO documentToDocumentDTO(Document entity);

	Document documentDTOToDocument(DocumentDTO dto);
}