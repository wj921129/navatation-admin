package com.navatation.business.dto.resp;

import lombok.Data;
import java.util.List;

@Data
public class GuestConfigRespDTO {
    private SettingsRespDTO settings;
    private List<WidgetRespDTO> widgets;
    private List<CategoryRespDTO> categories;
    private List<ShortcutRespDTO> shortcuts;
}
