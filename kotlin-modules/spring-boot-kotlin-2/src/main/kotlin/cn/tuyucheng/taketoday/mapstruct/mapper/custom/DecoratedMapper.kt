package cn.tuyucheng.taketoday.mapstruct.mapper.custom

import org.mapstruct.DecoratedWith
import org.mapstruct.Mapper

@Mapper
@DecoratedWith(UserMapperDecorator::class)
abstract class DecoratedMapper : cn.tuyucheng.taketoday.mapstruct.mapper.UserMapper {
}

