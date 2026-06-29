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

    @InjectMocks
    private HomeShortcutService homeShortcutService;

    @BeforeEach
    public void setUp() {
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
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
        assertEquals("/uploads/icon/sys/baidu.com_456.png", list.get(0).getIconValue());
        verify(navHomeShortcutMapper, times(1)).updateById(hs);
    }
}
