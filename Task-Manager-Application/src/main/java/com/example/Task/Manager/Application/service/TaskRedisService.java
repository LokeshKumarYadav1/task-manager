package com.example.Task.Manager.Application.service;

import com.example.Task.Manager.Application.dto.TaskResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskRedisService {

    private final ObjectMapper objectMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TASK_KEY_PREFIX = "task:";

    private String getTaskKey(String username, long id){

        return TASK_KEY_PREFIX + username + ":" + id;

    }

    public void saveTask(TaskResponseDTO task, String username) {

        try {

            String key = getTaskKey(username, task.getId());

            redisTemplate.opsForValue().set(key, task);

        } catch (Exception e) {

            System.out.println("Redis unavailable: " + e.getMessage());

        }

    }

    public TaskResponseDTO getTask(Long id, String username) {

        try {

            String key = getTaskKey(username, id);

            Object value = redisTemplate.opsForValue().get(key);

            if (value == null) {
                return null;
            }

            return objectMapper.convertValue(value, TaskResponseDTO.class);

        } catch (Exception e) {

            System.out.println("Redis unavailable: " + e.getMessage());

            return null;
        }
    }

    public void deleteTask(String username, long id) {

        try {

            String key = getTaskKey(username, id);

            redisTemplate.delete(key);

        } catch (Exception e) {

            System.out.println("Redis unavailable: " + e.getMessage());

        }

    }

}