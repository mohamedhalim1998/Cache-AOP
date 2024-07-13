package com.mohamed.halim.cacheaop.controller;

import com.mohamed.halim.cacheaop.dto.BookDto;
import com.mohamed.halim.cacheaop.service.impl.BookService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController extends CrudController<BookDto> {
    public BookController(BookService service) {
        super(service);
    }
}
