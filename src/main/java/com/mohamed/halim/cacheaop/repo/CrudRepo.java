package com.mohamed.halim.cacheaop.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CrudRepo<E> extends JpaRepository<E, Long> {
}
