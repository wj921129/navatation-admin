package com.navatation.business.constant;

/**
 * @Description 用户设置相关的默认常量
 */
public class SettingsConstants {
    
    // 默认搜索引擎
    public static final String DEFAULT_SEARCH_ENGINE = "google";
    
    // 默认兜底壁纸
    public static final String DEFAULT_WALLPAPER = "https://images.unsplash.com/photo-1598439473183-42c9301db5dc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=2400";
    
    // 默认UI布局设置
    public static final Integer DEFAULT_SEARCH_BOX_WIDTH = 50;         // 搜索框宽度占比，单位百分比 (%)
    public static final Integer DEFAULT_SEARCH_BOX_HEIGHT = 64;        // 搜索框高度，单位像素 (px)
    public static final Integer DEFAULT_SEARCH_BOX_MARGIN_TOP = 192;   // 搜索框距顶部间距，单位像素 (px)
    public static final Integer DEFAULT_ICON_SIZE = 64;                // 图标外框尺寸，单位像素 (px)
    public static final Integer DEFAULT_ICON_RADIUS = 50;              // 图标圆角大小，单位百分比 (%)
    public static final Integer DEFAULT_ICON_SPACING_X = 32;           // 图标水平间距，单位像素 (px)
    public static final Integer DEFAULT_ICON_SPACING_Y = 48;           // 图标垂直间距，单位像素 (px)
    public static final Integer DEFAULT_ICON_TEXT_GAP = 12;            // 图标与文字的间距，单位像素 (px)
    public static final Integer DEFAULT_TEXT_SIZE = 14;                // 捷径文字大小，单位像素 (px)
    public static final Integer DEFAULT_ICONS_MARGIN_TOP = 64;         // 搜索框与下方图标区的间距（作为搜索框下间距使用），单位像素 (px)
    public static final Integer DEFAULT_ICONS_MARGIN_X = 10;           // 图标区左右边距，控制网格水平收缩，单位百分比 (%)
    
    // 默认主题和背景类型
    public static final String DEFAULT_THEME = "dark";
    public static final String DEFAULT_BACKGROUND_TYPE = "URL";

    private SettingsConstants() {
        // 私有构造函数，防止实例化
    }
}
