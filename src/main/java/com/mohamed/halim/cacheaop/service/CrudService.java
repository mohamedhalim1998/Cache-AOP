package com.mohamed.halim.cacheaop.service;

public interface CrudService<T> {
    T save(T e);

    T read(Long id);

    void delete(Long id);

    T update(Long id, T e);
}
