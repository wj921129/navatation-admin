package com.navatation.business.controller;

import com.navatation.business.dto.resp.common.DeleteCountRespDTO;
import com.navatation.business.dto.req.todo.TodoCreateReqDTO;
import com.navatation.business.dto.req.todo.TodoSortReqDTO;
import com.navatation.business.dto.req.todo.TodoUpdateReqDTO;
import com.navatation.business.dto.resp.todo.TodoRespDTO;
import com.navatation.business.dto.resp.todo.ToggleRespDTO;
import com.navatation.business.service.TodoService;
import com.navatation.common.Result;
import com.navatation.framework.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author admin
 * @CreateTime 2026-05-15
 * @Description 待办事项控制器，处理待办项的CRUD、完成状态切换、排序及清除已完成项
 */
@RestController
@RequestMapping("/api/v1/todo")
@RequiredArgsConstructor
public class TodoController {

    private static final Logger log = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public Result<List<TodoRespDTO>> getList(@RequestHeader("Authorization") String auth,
                                         @RequestParam(required = false) String status) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("获取待办列表 入参:userId={},status={}", userId, status);
        List<TodoRespDTO> result = todoService.getList(userId, status);
        log.info("获取待办列表 出参:count={}", result.size());
        return Result.success(result);
    }

    @PostMapping
    public Result<TodoRespDTO> create(@RequestHeader("Authorization") String auth,
                                  @Valid @RequestBody TodoCreateReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("创建待办 入参:userId={},content={}", userId, req.getContent());
        TodoRespDTO result = todoService.create(userId, req);
        log.info("创建待办 出参:todoId={}", result.getTodoId());
        return Result.success("创建成功", result);
    }

    @PutMapping("/{todoId}")
    public Result<?> update(@RequestHeader("Authorization") String auth,
                             @PathVariable String todoId,
                             @Valid @RequestBody TodoUpdateReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("更新待办 入参:userId={},todoId={}", userId, todoId);
        todoService.update(userId, todoId, req);
        log.info("更新待办 出参:success=true");
        return Result.success("更新成功", null);
    }

    @PatchMapping("/{todoId}/toggle")
    public Result<ToggleRespDTO> toggle(@RequestHeader("Authorization") String auth,
                                    @PathVariable String todoId) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("切换待办状态 入参:userId={},todoId={}", userId, todoId);
        ToggleRespDTO result = todoService.toggle(userId, todoId);
        log.info("切换待办状态 出参:completed={}", result.getCompleted());
        return Result.success(result);
    }

    @DeleteMapping("/{todoId}")
    public Result<?> delete(@RequestHeader("Authorization") String auth,
                             @PathVariable String todoId) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("删除待办 入参:userId={},todoId={}", userId, todoId);
        todoService.delete(userId, todoId);
        log.info("删除待办 出参:success=true");
        return Result.success("删除成功", null);
    }

    @PutMapping("/sort")
    public Result<?> sort(@RequestHeader("Authorization") String auth,
                           @RequestBody TodoSortReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("待办排序 入参:userId={},count={}", userId, req.getItems().size());
        todoService.sort(userId, req);
        log.info("待办排序 出参:success=true");
        return Result.success("排序更新成功", null);
    }

    @DeleteMapping("/completed")
    public Result<DeleteCountRespDTO> clearCompleted(@RequestHeader("Authorization") String auth) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("清除已完成待办 入参:userId={}", userId);
        DeleteCountRespDTO result = todoService.clearCompleted(userId);
        log.info("清除已完成待办 出参:deletedCount={}", result.getDeletedCount());
        return Result.success("已清空 " + result.getDeletedCount() + " 条已完成待办", result);
    }
}
