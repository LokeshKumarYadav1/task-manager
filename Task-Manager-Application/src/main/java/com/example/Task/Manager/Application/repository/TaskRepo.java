package com.example.Task.Manager.Application.repository;

import com.example.Task.Manager.Application.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepo extends JpaRepository<Task, Long> {

      //@Query(value = "select * from user where id =:id", nativeQuery = true)
      //public Task findTaskWithId(Long id);

        Optional<Task> findByIdAndUserUsername(Long id, String username);

        List<Task> findByUserUsername(String username);

}