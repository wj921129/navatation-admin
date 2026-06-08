package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.navatation.business.dto.DeleteCountVO;
import com.navatation.business.dto.TodoCreateRequest;
import com.navatation.business.dto.TodoSortItem;
import com.navatation.business.dto.TodoSortRequest;
import com.navatation.business.dto.TodoUpdateRequest;
import com.navatation.business.dto.TodoVO;
import com.navatation.business.dto.ToggleVO;
import com.navatation.business.entity.nav.TodoItem;
import com.navatation.business.mapper.RootTodoItemMapper;
import com.navatation.business.mapper.TodoItemMapper;
import com.navatation.business.mapper.UserMapper;
import com.navatation.common.BizException;
import com.navatation.common.ResultCode;
import com.navatation.common.IdUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author admin
 * @CreateTime 2026-05-15
 * @Description 待办事项服务，处理待办项的CRUD、完成状态切换、排序及批量清除已完成项
 */
@Service
@RequiredArgsConstructor
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class);

    private final TodoItemMapper todoItemMapper;
    private final RootTodoItemMapper rootTodoItemMapper;
    private final UserMapper userMapper;

    private boolean isAdmin(String userId) {
        com.navatation.business.entity.user.User user = userMapper.selectById(userId);
        return user != null && "ADMIN".equals(user.getRole());
    }

    /**
     * 查询待办列表，可按状态筛选
     * @param userId 用户ID
     * @param status 状态筛选（active/completed/null）
     * @return 待办列表
     */
    public List<TodoVO> getList(String userId, String status) {
        if (isAdmin(userId)) {
            LambdaQueryWrapper<com.navatation.business.entity.root.RootTodoItem> wrapper = new LambdaQueryWrapper<com.navatation.business.entity.root.RootTodoItem>()
                    .eq(com.navatation.business.entity.root.RootTodoItem::getUserId, userId)
                    .orderByAsc(com.navatation.business.entity.root.RootTodoItem::getSortOrder);

            if ("active".equals(status)) {
                wrapper.eq(com.navatation.business.entity.root.RootTodoItem::getCompleted, false);
            } else if ("completed".equals(status)) {
                wrapper.eq(com.navatation.business.entity.root.RootTodoItem::getCompleted, true);
            }

            return rootTodoItemMapper.selectList(wrapper).stream()
                    .map(this::toVO)
                    .collect(Collectors.toList());
        }

        LambdaQueryWrapper<TodoItem> wrapper = new LambdaQueryWrapper<TodoItem>()
                .eq(TodoItem::getUserId, userId)
                .orderByAsc(TodoItem::getSortOrder);

        if ("active".equals(status)) {
            wrapper.eq(TodoItem::getCompleted, false);
        } else if ("completed".equals(status)) {
            wrapper.eq(TodoItem::getCompleted, true);
        }

        return todoItemMapper.selectList(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 创建待办项
     * @param userId 用户ID
     * @param req 创建请求
     * @return 创建的待办项
     */
    public TodoVO create(String userId, TodoCreateRequest req) {
        if (isAdmin(userId)) {
            double maxSort = rootTodoItemMapper.selectList(
                    new LambdaQueryWrapper<com.navatation.business.entity.root.RootTodoItem>().eq(com.navatation.business.entity.root.RootTodoItem::getUserId, userId))
                    .stream().mapToDouble(item -> item.getSortOrder() != null ? item.getSortOrder() : 0.0).max().orElse(0.0);

            com.navatation.business.entity.root.RootTodoItem item = new com.navatation.business.entity.root.RootTodoItem();
            item.setTodoId(IdUtils.genTodoId());
            item.setUserId(userId);
            item.setContent(req.getContent());
            item.setCompleted(false);
            item.setSortOrder(maxSort + 1.0);
            rootTodoItemMapper.insert(item);
            log.info("创建管理员待办成功 userId={} todoId={}", userId, item.getTodoId());
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
     * @param userId 用户ID
     * @param todoId 待办ID
     * @param req 更新请求
     */
    public void update(String userId, String todoId, TodoUpdateRequest req) {
        if (isAdmin(userId)) {
            com.navatation.business.entity.root.RootTodoItem item = rootTodoItemMapper.selectById(todoId);
            if (item == null || !item.getUserId().equals(userId)) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            item.setContent(req.getContent());
            rootTodoItemMapper.updateById(item);
            log.info("更新管理员待办成功 userId={} todoId={}", userId, todoId);
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
     * @param userId 用户ID
     * @param todoId 待办ID
     * @return 切换后的状态
     */
    public ToggleVO toggle(String userId, String todoId) {
        if (isAdmin(userId)) {
            com.navatation.business.entity.root.RootTodoItem item = rootTodoItemMapper.selectById(todoId);
            if (item == null || !item.getUserId().equals(userId)) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            item.setCompleted(!Boolean.TRUE.equals(item.getCompleted()));
            item.setCompletedAt(item.getCompleted() ? LocalDateTime.now() : null);
            rootTodoItemMapper.updateById(item);

            ToggleVO vo = new ToggleVO();
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

        ToggleVO vo = new ToggleVO();
        vo.setTodoId(item.getTodoId());
        vo.setCompleted(item.getCompleted());
        vo.setCompletedAt(item.getCompletedAt() != null ? item.getCompletedAt().toString() : null);
        return vo;
    }

    /**
     * 删除待办项
     * @param userId 用户ID
     * @param todoId 待办ID
     */
    public void delete(String userId, String todoId) {
        if (isAdmin(userId)) {
            com.navatation.business.entity.root.RootTodoItem item = rootTodoItemMapper.selectById(todoId);
            if (item == null || !item.getUserId().equals(userId)) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            rootTodoItemMapper.deleteById(todoId);
            log.info("删除管理员待办成功 userId={} todoId={}", userId, todoId);
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
     * @param userId 用户ID
     * @param req 排序请求
     */
    @Transactional
    public void sort(String userId, TodoSortRequest req) {
        List<String> ids = req.getItems().stream().map(TodoSortItem::getTodoId).collect(Collectors.toList());

        if (isAdmin(userId)) {
            Map<String, com.navatation.business.entity.root.RootTodoItem> itemMap = rootTodoItemMapper.selectBatchIds(ids).stream()
                    .filter(i -> i.getUserId().equals(userId))
                    .collect(Collectors.toMap(com.navatation.business.entity.root.RootTodoItem::getTodoId, Function.identity()));

            for (TodoSortItem si : req.getItems()) {
                com.navatation.business.entity.root.RootTodoItem item = itemMap.get(si.getTodoId());
                if (item != null) {
                    item.setSortOrder(si.getSortOrder());
                    rootTodoItemMapper.updateById(item);
                }
            }
            return;
        }

        // 批量查询所有待排序的待办项
        Map<String, TodoItem> itemMap = todoItemMapper.selectBatchIds(ids).stream()
                .filter(i -> i.getUserId().equals(userId))
                .collect(Collectors.toMap(TodoItem::getTodoId, Function.identity()));

        for (TodoSortItem si : req.getItems()) {
            TodoItem item = itemMap.get(si.getTodoId());
            if (item != null) {
                item.setSortOrder(si.getSortOrder());
                todoItemMapper.updateById(item);
            }
        }
    }

    /**
     * 批量清除已完成待办项
     * @param userId 用户ID
     * @return 删除数量
     */
    public DeleteCountVO clearCompleted(String userId) {
        if (isAdmin(userId)) {
            List<com.navatation.business.entity.root.RootTodoItem> completed = rootTodoItemMapper.selectList(
                    new LambdaQueryWrapper<com.navatation.business.entity.root.RootTodoItem>()
                            .eq(com.navatation.business.entity.root.RootTodoItem::getUserId, userId)
                            .eq(com.navatation.business.entity.root.RootTodoItem::getCompleted, true));
            List<String> ids = completed.stream().map(com.navatation.business.entity.root.RootTodoItem::getTodoId).collect(Collectors.toList());
            if (!ids.isEmpty()) {
                rootTodoItemMapper.deleteBatchIds(ids);
                log.info("清除管理员已完成待办成功 userId={} count={}", userId, ids.size());
            }
            DeleteCountVO vo = new DeleteCountVO();
            vo.setDeletedCount(completed.size());
            return vo;
        }

        List<TodoItem> completed = todoItemMapper.selectList(
                new LambdaQueryWrapper<TodoItem>()
                        .eq(TodoItem::getUserId, userId)
                        .eq(TodoItem::getCompleted, true));
        // 批量删除已完成待办项
        List<String> ids = completed.stream().map(TodoItem::getTodoId).collect(Collectors.toList());
        if (!ids.isEmpty()) {
            todoItemMapper.deleteBatchIds(ids);
            log.info("清除已完成待办成功 userId={} count={}", userId, ids.size());
        }
        DeleteCountVO vo = new DeleteCountVO();
        vo.setDeletedCount(completed.size());
        return vo;
    }

    /** 实体转VO */
    private TodoVO toVO(TodoItem item) {
        TodoVO vo = new TodoVO();
        vo.setTodoId(item.getTodoId());
        vo.setContent(item.getContent());
        vo.setCompleted(item.getCompleted());
        vo.setSortOrder(item.getSortOrder());
        vo.setCreatedAt(item.getCreatedAt() != null ? item.getCreatedAt().toString() : null);
        vo.setCompletedAt(item.getCompletedAt() != null ? item.getCompletedAt().toString() : null);
        return vo;
    }

    private TodoVO toVO(com.navatation.business.entity.root.RootTodoItem item) {
        TodoVO vo = new TodoVO();
        vo.setTodoId(item.getTodoId());
        vo.setContent(item.getContent());
        vo.setCompleted(item.getCompleted());
        vo.setSortOrder(item.getSortOrder());
        vo.setCreatedAt(item.getCreatedAt() != null ? item.getCreatedAt().toString() : null);
        vo.setCompletedAt(item.getCompletedAt() != null ? item.getCompletedAt().toString() : null);
        return vo;
    }
}
