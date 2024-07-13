package com.mohamed.halim.cacheaop.service.impl;

import com.mohamed.halim.cacheaop.aop.Cached;
import com.mohamed.halim.cacheaop.aop.CachedEvict;
import com.mohamed.halim.cacheaop.dto.BookDto;
import com.mohamed.halim.cacheaop.mapper.BookMapper;
import com.mohamed.halim.cacheaop.repo.BookRepo;
import com.mohamed.halim.cacheaop.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService implements CrudService<BookDto> {
    private final BookRepo repo;
    private final BookMapper mapper;
    @Override
    public BookDto save(BookDto e) {
        var entity = mapper.fromDto(e);
        return mapper.toDto(repo.save(entity));
    }

    @Override
    @Cached(prefix = "book", key = "id")
    public BookDto read(Long id) {
        return mapper.toDto(repo.findById(id).get());
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    @CachedEvict(prefix = "book", key = "id")
    public BookDto update(Long id, BookDto e) {
        var entity = mapper.fromDto(e);
        entity.setId(id);
        return mapper.toDto(repo.save(entity));
    }
}
