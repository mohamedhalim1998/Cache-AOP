package com.mohamed.halim.cacheaop.mapper;

import com.mohamed.halim.cacheaop.dto.BookDto;
import com.mohamed.halim.cacheaop.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);
    Book fromDto(BookDto dto);
}
