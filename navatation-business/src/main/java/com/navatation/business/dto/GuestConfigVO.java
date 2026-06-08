package com.navatation.business.dto;

import lombok.Data;
import java.util.List;

@Data
public class GuestConfigVO {
    private SettingsVO settings;
    private List<WidgetVO> widgets;
    private List<CategoryVO> categories;
    private List<ShortcutVO> shortcuts;
}
