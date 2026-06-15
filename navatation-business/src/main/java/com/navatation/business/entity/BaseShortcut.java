package com.navatation.business.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface BaseShortcut {
    String getShortcutId();
    void setShortcutId(String shortcutId);
    
    String getCategoryId();
    void setCategoryId(String categoryId);
    
    String getUserId();
    void setUserId(String userId);
    
    String getName();
    void setName(String name);
    
    String getUrl();
    void setUrl(String url);
    
    String getIconType();
    void setIconType(String iconType);
    
    String getIconValue();
    void setIconValue(String iconValue);
    
    String getIconColor();
    void setIconColor(String iconColor);
    
    BigDecimal getSortOrder();
    void setSortOrder(BigDecimal sortOrder);
    
    Long getClickCount();
    void setClickCount(Long clickCount);

    LocalDateTime getCreatedAt();
}
