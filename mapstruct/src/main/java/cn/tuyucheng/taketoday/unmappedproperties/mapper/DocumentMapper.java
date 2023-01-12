package cn.tuyucheng.taketoday.unmappedproperties.mapper;

import cn.tuyucheng.taketoday.unmappedproperties.dto.DocumentDTO;
import cn.tuyucheng.taketoday.unmappedproperties.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DocumentMapper {

	DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

	DocumentDTO documentToDocumentDTO(Document entity);

	Document documentDTOToDocument(DocumentDTO dto);
}