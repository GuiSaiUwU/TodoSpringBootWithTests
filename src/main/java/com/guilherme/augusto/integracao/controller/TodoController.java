package com.guilherme.augusto.integracao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.guilherme.augusto.integracao.entidades.Todo;
import com.guilherme.augusto.integracao.repository.TodoRepository;

@RestController
@CrossOrigin(origins = "*")
public class TodoController {
    @Autowired
    private TodoRepository todoRepository;

    @GetMapping(path = "/todos")
    public List<Todo> getTodos() {
        return todoRepository.findAll();
    }

    @PostMapping(path = "/todos")
    public ResponseEntity<Object> criar(@RequestBody Todo todo) {
        return ResponseEntity.ok(todoRepository.save(todo));
    }

    @PutMapping(path = "/todos/{id}")
    public ResponseEntity<Object> editar(@PathVariable(value = "id") Long id, @RequestBody Todo dadosAtualizados) {
        var todoOptional = todoRepository.findById(id);
        if (todoOptional.isPresent()) {
            Todo todo = todoOptional.get();
            todo.setTitulo(dadosAtualizados.getTitulo());
            todo.setDescricao(dadosAtualizados.getDescricao());
            return ResponseEntity.ok(todoRepository.save(todo));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping(path = "/todos/{id}/concluir")
    public ResponseEntity<Object> completar(@PathVariable(value = "id") Long id) {
        var todoOptional = todoRepository.findById(id);
        if (todoOptional.isPresent()) {
            Todo todo = todoOptional.get();
            todo.setConcluida(true); // Altera o status usando o atributo da classe Todo
            return ResponseEntity.ok(todoRepository.save(todo));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/todos/{id}")
    public ResponseEntity<Object> deletar(@PathVariable(value = "id") Long id) {
        var todo = todoRepository.findById(id);
        if (todo.isPresent()) {
            todoRepository.delete(todo.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
