package com.mohamed.halim.cacheaop.controller;

import com.mohamed.halim.cacheaop.service.CrudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
public abstract class CrudController<T> {
    private final CrudService<T> service;

    @PostMapping()
    public T save(@Valid @RequestBody T t) {
        return service.save(t);
    }

    @GetMapping("{id}")
    public T get(@PathVariable Long id) {
        return service.read(id);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("{id}")
    public T update(@PathVariable Long id, @Valid @RequestBody T t) {
        return service.update(id, t);
    }
}
