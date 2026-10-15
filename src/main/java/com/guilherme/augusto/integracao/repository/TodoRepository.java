package com.guilherme.augusto.integracao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guilherme.augusto.integracao.entidades.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long>{
    
}
