package com.navatation.business.service;

import com.navatation.business.dto.req.nav.HomeShortcutReqDTO;
import com.navatation.business.dto.resp.nav.HomeShortcutRespDTO;
import com.navatation.business.entity.nav.NavHomeShortcut;
import com.navatation.business.entity.recommend.RecommendHomeShortcut;
import com.navatation.business.entity.user.User;
import com.navatation.business.helper.FaviconFetcherHelper;
import com.navatation.business.mapper.NavHomeShortcutMapper;
import com.navatation.business.mapper.RecommendHomeShortcutMapper;
import com.navatation.business.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HomeShortcutServiceTest {

    @Mock
    private NavHomeShortcutMapper navHomeShortcutMapper;

    @Mock
    private RecommendHomeShortcutMapper recommendHomeShortcutMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private FaviconFetcherHelper faviconFetcherHelper;

    @Mock
    private java.util.concurrent.Executor iconDownloadExecutor;

    @InjectMocks
    private HomeShortcutService homeShortcutService;

    @BeforeEach
    public void setUp() {
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        lenient().doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(iconDownloadExecutor).execute(any(Runnable.class));
    }

    @Test
    public void testAddHomeShortcut_Admin_IconLocalization() {
        User admin = new User();
        admin.setUserId("1");
        admin.setRole("ADMIN");
        when(userMapper.selectById("1")).thenReturn(admin);

        when(faviconFetcherHelper.downloadToLocal("https://github.com/favicon.ico", "github.com"))
                .thenReturn("/uploads/icon/sys/github.com_123.png");

        HomeShortcutReqDTO req = new HomeShortcutReqDTO();
        req.setName("GitHub");
        req.setUrl("https://github.com");
        req.setIconType("FAVICON");
        req.setIconValue("https://github.com/favicon.ico");
        req.setSortOrder(BigDecimal.ZERO);

        HomeShortcutRespDTO resp = homeShortcutService.addHomeShortcut("1", req);

        assertNotNull(resp);
        assertEquals("/uploads/icon/sys/github.com_123.png", resp.getIconValue());
        verify(recommendHomeShortcutMapper, times(1)).insert(any(RecommendHomeShortcut.class));
        verify(hashOperations, times(1)).delete("navatation:guest_config", "home_shortcuts");
    }

    @Test
    public void testAddHomeShortcut_User_IconLocalization() {
        User user = new User();
        user.setUserId("2");
        user.setRole("USER");
        when(userMapper.selectById("2")).thenReturn(user);

        when(faviconFetcherHelper.downloadToLocal("https://baidu.com/favicon.ico", "baidu.com"))
                .thenReturn("/uploads/icon/sys/baidu.com_456.png");

        HomeShortcutReqDTO req = new HomeShortcutReqDTO();
        req.setName("Baidu");
        req.setUrl("https://baidu.com");
        req.setIconType("FAVICON");
        req.setIconValue("https://baidu.com/favicon.ico");
        req.setSortOrder(BigDecimal.ZERO);

        HomeShortcutRespDTO resp = homeShortcutService.addHomeShortcut("2", req);

        assertNotNull(resp);
        assertEquals("/uploads/icon/sys/baidu.com_456.png", resp.getIconValue());
        verify(navHomeShortcutMapper, times(1)).insert(any(NavHomeShortcut.class));
    }

    @Test
    public void testGetHomeShortcuts_SelfHealing() {
        User user = new User();
        user.setUserId("2");
        user.setRole("USER");
        when(userMapper.selectById("2")).thenReturn(user);

        NavHomeShortcut hs = new NavHomeShortcut();
        hs.setShortcutId("shortcut-1");
        hs.setUserId("2");
        hs.setName("Baidu");
        hs.setUrl("https://baidu.com");
        hs.setIconType("FAVICON");
        hs.setIconValue("https://baidu.com/favicon.ico");
        hs.setSortOrder(BigDecimal.ZERO);

        when(navHomeShortcutMapper.selectList(any())).thenReturn(Collections.singletonList(hs));

        when(faviconFetcherHelper.downloadToLocal("https://baidu.com/favicon.ico", "baidu.com"))
                .thenReturn("/uploads/icon/sys/baidu.com_456.png");

        List<HomeShortcutRespDTO> list = homeShortcutService.getHomeShortcuts("2");

        assertEquals(1, list.size());
        // 异步非阻塞：第一次获取应该依然是旧值
        assertEquals("https://baidu.com/favicon.ico", list.get(0).getIconValue());
        // 验证确实调用了异步更新
        verify(navHomeShortcutMapper, times(1)).updateById(hs);
        // 检查 hs 的 iconValue 是否已更新为本地化路径，为下一次查询做准备
        assertEquals("/uploads/icon/sys/baidu.com_456.png", hs.getIconValue());
    }

    @Test
    public void testGetHomeShortcuts_SelfHealing_RetryCooldown() {
        User user = new User();
        user.setUserId("2");
        user.setRole("USER");
        when(userMapper.selectById("2")).thenReturn(user);

        NavHomeShortcut hs = new NavHomeShortcut();
        hs.setShortcutId("shortcut-1");
        hs.setUserId("2");
        hs.setName("Baidu");
        hs.setUrl("https://baidu.com");
        hs.setIconType("FAVICON");
        hs.setIconValue("https://baidu.com/favicon.ico");
        hs.setSortOrder(BigDecimal.ZERO);

        when(navHomeShortcutMapper.selectList(any())).thenReturn(Collections.singletonList(hs));

        // 第一次下载失败，返回原来的 url
        when(faviconFetcherHelper.downloadToLocal("https://baidu.com/favicon.ico", "baidu.com"))
                .thenReturn("https://baidu.com/favicon.ico");

        // 第一次调用，应该触发下载但因为下载返回原值导致失败，进入 30 分钟冷却
        List<HomeShortcutRespDTO> list1 = homeShortcutService.getHomeShortcuts("2");
        assertEquals("https://baidu.com/favicon.ico", list1.get(0).getIconValue());
        verify(faviconFetcherHelper, times(1)).downloadToLocal("https://baidu.com/favicon.ico", "baidu.com");

        // 第二次调用，应该直接跳过下载（在冷却中）
        reset(faviconFetcherHelper); // 重置 mock
        List<HomeShortcutRespDTO> list2 = homeShortcutService.getHomeShortcuts("2");
        assertEquals("https://baidu.com/favicon.ico", list2.get(0).getIconValue());
        verify(faviconFetcherHelper, never()).downloadToLocal(anyString(), anyString());
    }
}
