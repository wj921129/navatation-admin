import os
import re

ADMIN_DIR = r"E:\workspace\navatation\navatation-admin"
BUSINESS_DTO_DIR = os.path.join(ADMIN_DIR, "navatation-business", "src", "main", "java", "com", "navatation", "business", "dto")
COMMON_RESP_DIR = os.path.join(ADMIN_DIR, "navatation-common", "src", "main", "java", "com", "navatation", "common", "dto", "resp")

os.makedirs(os.path.join(BUSINESS_DTO_DIR, "req"), exist_ok=True)
os.makedirs(os.path.join(BUSINESS_DTO_DIR, "resp"), exist_ok=True)
os.makedirs(COMMON_RESP_DIR, exist_ok=True)

rename_map = {
    "BatchCreateItem": ("BatchCreateItemDTO", "req", "com.navatation.business.dto.req"),
    "BatchCreateRequest": ("BatchCreateReqDTO", "req", "com.navatation.business.dto.req"),
    "BatchFaviconRequest": ("BatchFaviconReqDTO", "req", "com.navatation.business.dto.req"),
    "BatchRecommendSiteRequest": ("BatchRecommendSiteReqDTO", "req", "com.navatation.business.dto.req"),
    "CategoryRequest": ("CategoryReqDTO", "req", "com.navatation.business.dto.req"),
    "CategoryVO": ("CategoryRespDTO", "resp", "com.navatation.business.dto.resp"),
    "ChangePasswordRequest": ("ChangePasswordReqDTO", "req", "com.navatation.business.dto.req"),
    "CreateShortcutRequest": ("CreateShortcutReqDTO", "req", "com.navatation.business.dto.req"),
    "DeleteCountVO": ("DeleteCountRespDTO", COMMON_RESP_DIR, "com.navatation.common.dto.resp"),
    "EncryptedChangePasswordRequest": ("EncryptedChangePasswordReqDTO", "req", "com.navatation.business.dto.req"),
    "EncryptedLoginRequest": ("EncryptedLoginReqDTO", "req", "com.navatation.business.dto.req"),
    "EncryptedRegisterRequest": ("EncryptedRegisterReqDTO", "req", "com.navatation.business.dto.req"),
    "FaviconRequest": ("FaviconReqDTO", "req", "com.navatation.business.dto.req"),
    "FaviconVO": ("FaviconRespDTO", "resp", "com.navatation.business.dto.resp"),
    "GuestConfigVO": ("GuestConfigRespDTO", "resp", "com.navatation.business.dto.resp"),
    "IconUploadVO": ("IconUploadRespDTO", "resp", "com.navatation.business.dto.resp"),
    "LoginRequest": ("LoginReqDTO", "req", "com.navatation.business.dto.req"),
    "LoginVO": ("LoginRespDTO", "resp", "com.navatation.business.dto.resp"),
    "RecommendCategoryRequest": ("RecommendCategoryReqDTO", "req", "com.navatation.business.dto.req"),
    "RecommendCategoryVO": ("RecommendCategoryRespDTO", "resp", "com.navatation.business.dto.resp"),
    "RecommendSiteRequest": ("RecommendSiteReqDTO", "req", "com.navatation.business.dto.req"),
    "RecommendSiteVO": ("RecommendSiteRespDTO", "resp", "com.navatation.business.dto.resp"),
    "RecommendWidgetVO": ("RecommendWidgetRespDTO", "resp", "com.navatation.business.dto.resp"),
    "RefreshTokenRequest": ("RefreshTokenReqDTO", "req", "com.navatation.business.dto.req"),
    "RegisterRequest": ("RegisterReqDTO", "req", "com.navatation.business.dto.req"),
    "ResetPasswordRequest": ("ResetPasswordReqDTO", "req", "com.navatation.business.dto.req"),
    "SettingsRequest": ("SettingsReqDTO", "req", "com.navatation.business.dto.req"),
    "SettingsVO": ("SettingsRespDTO", "resp", "com.navatation.business.dto.resp"),
    "ShortcutVO": ("ShortcutRespDTO", "resp", "com.navatation.business.dto.resp"),
    "SortItem": ("SortItemDTO", "req", "com.navatation.business.dto.req"),
    "SortRequest": ("SortReqDTO", "req", "com.navatation.business.dto.req"),
    "TodoCreateRequest": ("TodoCreateReqDTO", "req", "com.navatation.business.dto.req"),
    "TodoSortItem": ("TodoSortItemDTO", "req", "com.navatation.business.dto.req"),
    "TodoSortRequest": ("TodoSortReqDTO", "req", "com.navatation.business.dto.req"),
    "TodoUpdateRequest": ("TodoUpdateReqDTO", "req", "com.navatation.business.dto.req"),
    "TodoVO": ("TodoRespDTO", "resp", "com.navatation.business.dto.resp"),
    "ToggleVO": ("ToggleRespDTO", "resp", "com.navatation.business.dto.resp"),
    "UpdateShortcutRequest": ("UpdateShortcutReqDTO", "req", "com.navatation.business.dto.req"),
    "UserVO": ("UserRespDTO", "resp", "com.navatation.business.dto.resp"),
    "WallpaperVO": ("WallpaperRespDTO", "resp", "com.navatation.business.dto.resp"),
    "WidgetRequest": ("WidgetReqDTO", "req", "com.navatation.business.dto.req"),
    "WidgetVO": ("WidgetRespDTO", "resp", "com.navatation.business.dto.resp"),
}

for old_name, (new_name, new_dir, new_pkg) in rename_map.items():
    old_file = os.path.join(BUSINESS_DTO_DIR, f"{old_name}.java")
    if not os.path.exists(old_file):
        continue
    
    if new_dir in ["req", "resp"]:
        new_file_dir = os.path.join(BUSINESS_DTO_DIR, new_dir)
    else:
        new_file_dir = new_dir
        
    new_file = os.path.join(new_file_dir, f"{new_name}.java")
    
    with open(old_file, "r", encoding="utf-8", errors="ignore") as f:
        content = f.read()
    
    content = re.sub(r"package com\.navatation\.business\.dto;", f"package {new_pkg};", content)
    content = re.sub(fr"\bpublic\s+class\s+{old_name}\b", f"public class {new_name}", content)
    
    for o, (n, _, _) in rename_map.items():
        content = re.sub(fr"\b{o}\b", n, content)
        
    with open(new_file, "w", encoding="utf-8") as f:
        f.write(content)
        
    os.remove(old_file)

for search_dir in [ADMIN_DIR, r"E:\workspace\navatation\doc"]:
    for root, dirs, files in os.walk(search_dir):
        for file in files:
            if file.endswith(".java") or file.endswith(".md"):
                filepath = os.path.join(root, file)
                with open(filepath, "r", encoding="utf-8", errors="ignore") as f:
                    content = f.read()
                
                orig_content = content
                
                for old_name, (new_name, new_dir, new_pkg) in rename_map.items():
                    content = re.sub(fr"import com\.navatation\.business\.dto\.{old_name};", f"import {new_pkg}.{new_name};", content)
                    
                for old_name, (new_name, _, _) in rename_map.items():
                    content = re.sub(fr"\b{old_name}\b", new_name, content)
                    
                if content != orig_content:
                    with open(filepath, "w", encoding="utf-8") as f:
                        f.write(content)

print("Rename complete.")
