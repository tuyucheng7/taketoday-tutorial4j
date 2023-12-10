package cn.tuyucheng.taketoday.entitydtodifferences.mapper;

import cn.tuyucheng.taketoday.entitydtodifferences.dto.BookDto;
import cn.tuyucheng.taketoday.entitydtodifferences.dto.UserCreationDto;
import cn.tuyucheng.taketoday.entitydtodifferences.dto.UserResponseDto;
import cn.tuyucheng.taketoday.entitydtodifferences.entity.Book;
import cn.tuyucheng.taketoday.entitydtodifferences.entity.User;

import java.util.stream.Collectors;

public class UserMapper {

   public static UserResponseDto toDto(User entity) {
      return new UserResponseDto(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getBooks()
            .stream()
            .map(UserMapper::toDto)
            .collect(Collectors.toList()));
   }

   public static User toEntity(UserCreationDto dto) {
      return new User(dto.getFirstName(), dto.getLastName(), dto.getAddress(), dto.getBooks()
            .stream()
            .map(UserMapper::toEntity)
            .collect(Collectors.toList()));
   }

   public static BookDto toDto(Book entity) {
      return new BookDto(entity.getName(), entity.getAuthor());
   }

   public static Book toEntity(BookDto dto) {
      return new Book(dto.getName(), dto.getAuthor());
   }
}