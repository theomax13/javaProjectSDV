package com.example.todo_list.controller;

import com.example.todo_list.model.Task;
import com.example.todo_list.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskRepository repository;

    // Récupérer toutes les tâches
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = repository.findAll();
        if (tasks.isEmpty()) {
            return ResponseEntity.notFound().build(); // Renvoie 404 Not Found si la liste est vide
        } else {
            return ResponseEntity.ok(tasks); // Renvoie 200 OK avec la liste des tâches
        }
    }

    @GetMapping("{completed}")
    public ResponseEntity<List<Task>> getCompletedTasks(@PathVariable Boolean completed) {
        List<Task> tasks =  repository.findByCompleted(completed);
        return ResponseEntity.ok(tasks); // Renvoie 200 OK avec la liste des tâches
    }

    // Ajouter une nouvelle tâche
    @PostMapping
    public ResponseEntity<Task> addTask(@RequestBody Task task) {
        Task savedTask = repository.save(task);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED); // Renvoie un statut 201 CREATED
    }

    // Mettre à jour une tâche
    @PutMapping("{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return repository.findById(id).map(task -> {
            task.setTitle(taskDetails.getTitle());
            task.setDescription(taskDetails.getDescription());
            task.setCompleted(taskDetails.isCompleted());
            return ResponseEntity.ok(repository.save(task));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Supprimer une tâche
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        return repository.findById(id).map(task -> {
            repository.deleteById(id);
            return ResponseEntity.ok().build(); // Renvoie 200 OK si la suppression est réussie
        }).orElse(ResponseEntity.notFound().build()); // Renvoie 404 Not Found si l'ID n'existe pas
    }
}
