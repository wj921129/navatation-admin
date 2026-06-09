package com.navatation.business.dto.resp.settings;

import lombok.Data;
import java.util.List;
import com.navatation.business.dto.resp.nav.CategoryRespDTO;
import com.navatation.business.dto.resp.settings.SettingsRespDTO;
import com.navatation.business.dto.resp.widget.WidgetRespDTO;
import com.navatation.business.dto.resp.settings.GuestConfigRespDTO;
import com.navatation.business.dto.resp.nav.ShortcutRespDTO;

@Data
public class GuestConfigRespDTO {
    private SettingsRespDTO settings;
    private List<WidgetRespDTO> widgets;
    private List<CategoryRespDTO> categories;
    private List<ShortcutRespDTO> shortcuts;
}
