package cn.tuyucheng.taketoday.sample.pets.boundary;

import cn.tuyucheng.taketoday.sample.pets.domain.Pet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PetDtoMapper {

    PetDto map(Pet source);

    Pet map(PetDto source);

}
