package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.navatation.business.dto.req.todo.TodoCreateReqDTO;
import com.navatation.business.dto.req.todo.TodoSortItemDTO;
import com.navatation.business.dto.req.todo.TodoSortReqDTO;
import com.navatation.business.dto.req.todo.TodoUpdateReqDTO;
import com.navatation.business.dto.resp.common.DeleteCountRespDTO;
import com.navatation.business.dto.resp.todo.TodoRespDTO;
import com.navatation.business.dto.resp.todo.ToggleRespDTO;
import com.navatation.business.entity.nav.TodoItem;
import com.navatation.business.entity.recommend.RecommendTodoItem;
import com.navatation.business.entity.user.User;
import com.navatation.business.mapper.RecommendTodoItemMapper;
import com.navatation.business.mapper.TodoItemMapper;
import com.navatation.business.mapper.UserMapper;
import com.navatation.common.BizException;
import com.navatation.common.IdUtils;
import com.navatation.common.ResultCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 待办事项服务，处理待办项的CRUD、完成状态切换、排序及批量清除已完成项
 */
@Service
@RequiredArgsConstructor
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class);
    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_COMPLETED = "completed";

    private final TodoItemMapper todoItemMapper;
    private final RecommendTodoItemMapper recommendTodoItemMapper;
    private final UserMapper userMapper;

    private boolean isAdmin(String userId) {
        User user = userMapper.selectById(userId);
        return user != null && "ADMIN".equals(user.getRole());
    }

    /**
     * 查询待办列表，可按状态筛选
     */
    public List<TodoRespDTO> getList(String userId, String status) {
        if (isAdmin(userId)) {
            LambdaQueryWrapper<RecommendTodoItem> wrapper = new LambdaQueryWrapper<RecommendTodoItem>()
                    .orderByAsc(RecommendTodoItem::getSortOrder);

            if (STATUS_ACTIVE.equals(status)) {
                wrapper.eq(RecommendTodoItem::getCompleted, false);
            } else if (STATUS_COMPLETED.equals(status)) {
                wrapper.eq(RecommendTodoItem::getCompleted, true);
            }

            return recommendTodoItemMapper.selectList(wrapper).stream()
                    .map(this::toVO)
                    .collect(Collectors.toList());
        }

        LambdaQueryWrapper<TodoItem> wrapper = new LambdaQueryWrapper<TodoItem>()
                .eq(TodoItem::getUserId, userId)
                .orderByAsc(TodoItem::getSortOrder);

        if (STATUS_ACTIVE.equals(status)) {
            wrapper.eq(TodoItem::getCompleted, false);
        } else if (STATUS_COMPLETED.equals(status)) {
            wrapper.eq(TodoItem::getCompleted, true);
        }

        return todoItemMapper.selectList(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 创建待办项
     */
    public TodoRespDTO create(String userId, TodoCreateReqDTO req) {
        if (isAdmin(userId)) {
            double maxSort = recommendTodoItemMapper.selectList(new LambdaQueryWrapper<>())
                    .stream().mapToDouble(item -> item.getSortOrder() != null ? item.getSortOrder() : 0.0).max().orElse(0.0);

            RecommendTodoItem item = new RecommendTodoItem();
            item.setTodoId(IdUtils.genTodoId());
            item.setContent(req.getContent());
            item.setCompleted(false);
            item.setSortOrder(maxSort + 1.0);
            recommendTodoItemMapper.insert(item);
            log.info("创建管理员推荐待办成功 userId={} todoId={}", userId, item.getTodoId());
            return toVO(item);
        }

        double maxSort = todoItemMapper.selectList(
                new LambdaQueryWrapper<TodoItem>().eq(TodoItem::getUserId, userId))
                .stream().mapToDouble(item -> item.getSortOrder() != null ? item.getSortOrder() : 0.0).max().orElse(0.0);

        TodoItem item = new TodoItem();
        item.setTodoId(IdUtils.genTodoId());
        item.setUserId(userId);
        item.setContent(req.getContent());
        item.setCompleted(false);
        item.setSortOrder(maxSort + 1.0);
        todoItemMapper.insert(item);
        log.info("创建待办成功 userId={} todoId={}", userId, item.getTodoId());
        return toVO(item);
    }

    /**
     * 更新待办内容
     */
    public void update(String userId, String todoId, TodoUpdateReqDTO req) {
        if (isAdmin(userId)) {
            RecommendTodoItem item = recommendTodoItemMapper.selectById(todoId);
            if (item == null) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            item.setContent(req.getContent());
            recommendTodoItemMapper.updateById(item);
            log.info("更新管理员推荐待办成功 userId={} todoId={}", userId, todoId);
            return;
        }

        TodoItem item = todoItemMapper.selectById(todoId);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        item.setContent(req.getContent());
        todoItemMapper.updateById(item);
        log.info("更新待办成功 userId={} todoId={}", userId, todoId);
    }

    /**
     * 切换待办完成状态
     */
    public ToggleRespDTO toggle(String userId, String todoId) {
        if (isAdmin(userId)) {
            RecommendTodoItem item = recommendTodoItemMapper.selectById(todoId);
            if (item == null) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            item.setCompleted(!Boolean.TRUE.equals(item.getCompleted()));
            item.setCompletedAt(item.getCompleted() ? LocalDateTime.now() : null);
            recommendTodoItemMapper.updateById(item);

            ToggleRespDTO vo = new ToggleRespDTO();
            vo.setTodoId(item.getTodoId());
            vo.setCompleted(item.getCompleted());
            vo.setCompletedAt(item.getCompletedAt() != null ? item.getCompletedAt().toString() : null);
            return vo;
        }

        TodoItem item = todoItemMapper.selectById(todoId);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        item.setCompleted(!Boolean.TRUE.equals(item.getCompleted()));
        item.setCompletedAt(item.getCompleted() ? LocalDateTime.now() : null);
        todoItemMapper.updateById(item);

        ToggleRespDTO vo = new ToggleRespDTO();
        vo.setTodoId(item.getTodoId());
        vo.setCompleted(item.getCompleted());
        vo.setCompletedAt(item.getCompletedAt() != null ? item.getCompletedAt().toString() : null);
        return vo;
    }

    /**
     * 删除待办项
     */
    public void delete(String userId, String todoId) {
        if (isAdmin(userId)) {
            RecommendTodoItem item = recommendTodoItemMapper.selectById(todoId);
            if (item == null) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            recommendTodoItemMapper.deleteById(todoId);
            log.info("删除管理员推荐待办成功 userId={} todoId={}", userId, todoId);
            return;
        }

        TodoItem item = todoItemMapper.selectById(todoId);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        todoItemMapper.deleteById(todoId);
        log.info("删除待办成功 userId={} todoId={}", userId, todoId);
    }

    /**
     * 批量更新待办排序
     */
    @Transactional
    public void sort(String userId, TodoSortReqDTO req) {
        List<String> ids = req.getItems().stream().map(TodoSortItemDTO::getTodoId).collect(Collectors.toList());

        if (isAdmin(userId)) {
            Map<String, RecommendTodoItem> itemMap = recommendTodoItemMapper.selectBatchIds(ids).stream()
                    .collect(Collectors.toMap(RecommendTodoItem::getTodoId, Function.identity()));

            List<RecommendTodoItem> updates = new java.util.ArrayList<>();
            for (TodoSortItemDTO si : req.getItems()) {
                RecommendTodoItem item = itemMap.get(si.getTodoId());
                if (item != null) {
                    item.setSortOrder(si.getSortOrder());
                    updates.add(item);
                }
            }
            if (!CollectionUtils.isEmpty(updates)) {
                Db.updateBatchById(updates);
            }
            return;
        }

        Map<String, TodoItem> itemMap = todoItemMapper.selectBatchIds(ids).stream()
                .filter(i -> i.getUserId().equals(userId))
                .collect(Collectors.toMap(TodoItem::getTodoId, Function.identity()));

        List<TodoItem> updates = new java.util.ArrayList<>();
        for (TodoSortItemDTO si : req.getItems()) {
            TodoItem item = itemMap.get(si.getTodoId());
            if (item != null) {
                item.setSortOrder(si.getSortOrder());
                updates.add(item);
            }
        }
        if (!CollectionUtils.isEmpty(updates)) {
            Db.updateBatchById(updates);
        }
    }

    /**
     * 批量清除已完成待办项
     */
    public DeleteCountRespDTO clearCompleted(String userId) {
        if (isAdmin(userId)) {
            List<RecommendTodoItem> completed = recommendTodoItemMapper.selectList(
                    new LambdaQueryWrapper<RecommendTodoItem>()
                            .eq(RecommendTodoItem::getCompleted, true));
            List<String> ids = completed.stream().map(RecommendTodoItem::getTodoId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(ids)) {
                recommendTodoItemMapper.deleteBatchIds(ids);
                log.info("清除管理员推荐已完成待办成功 userId={} count={}", userId, ids.size());
            }
            DeleteCountRespDTO vo = new DeleteCountRespDTO();
            vo.setDeletedCount(completed.size());
            return vo;
        }

        List<TodoItem> completed = todoItemMapper.selectList(
                new LambdaQueryWrapper<TodoItem>()
                        .eq(TodoItem::getUserId, userId)
                        .eq(TodoItem::getCompleted, true));
        List<String> ids = completed.stream().map(TodoItem::getTodoId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(ids)) {
            todoItemMapper.deleteBatchIds(ids);
            log.info("清除已完成待办成功 userId={} count={}", userId, ids.size());
        }
        DeleteCountRespDTO vo = new DeleteCountRespDTO();
        vo.setDeletedCount(completed.size());
        return vo;
    }

    private TodoRespDTO toVO(TodoItem item) {
        TodoRespDTO vo = new TodoRespDTO();
        vo.setTodoId(item.getTodoId());
        vo.setContent(item.getContent());
        vo.setCompleted(item.getCompleted());
        vo.setSortOrder(item.getSortOrder());
        vo.setCreatedAt(item.getCreatedAt() != null ? item.getCreatedAt().toString() : null);
        vo.setCompletedAt(item.getCompletedAt() != null ? item.getCompletedAt().toString() : null);
        return vo;
    }

    private TodoRespDTO toVO(RecommendTodoItem item) {
        TodoRespDTO vo = new TodoRespDTO();
        vo.setTodoId(item.getTodoId());
        vo.setContent(item.getContent());
        vo.setCompleted(item.getCompleted());
        vo.setSortOrder(item.getSortOrder());
        vo.setCreatedAt(item.getCreatedAt() != null ? item.getCreatedAt().toString() : null);
        vo.setCompletedAt(item.getCompletedAt() != null ? item.getCompletedAt().toString() : null);
        return vo;
    }
}
