import os
import re

ADMIN_DIR = r"E:\workspace\navatation\navatation-admin"
BUSINESS_DTO_DIR = os.path.join(ADMIN_DIR, "navatation-business", "src", "main", "java", "com", "navatation", "business", "dto")

rename_map = {
    "BatchCreateItemVO": ("BatchCreateItemRespDTO", "resp", "com.navatation.business.dto.resp"),
    "BatchRecommendSiteSaveRequest": ("BatchRecommendSiteSaveReqDTO", "req", "com.navatation.business.dto.req"),
    "CreateShortcutItem": ("CreateShortcutItemDTO", "req", "com.navatation.business.dto.req"),
    "RecommendSiteItem": ("RecommendSiteItemDTO", "req", "com.navatation.business.dto.req"),
    "RecommendWidgetRequest": ("RecommendWidgetReqDTO", "req", "com.navatation.business.dto.req"),
}

for old_name, (new_name, new_dir, new_pkg) in rename_map.items():
    old_file = os.path.join(BUSINESS_DTO_DIR, f"{old_name}.java")
    if not os.path.exists(old_file):
        continue
    
    new_file = os.path.join(BUSINESS_DTO_DIR, new_dir, f"{new_name}.java")
    with open(old_file, "r", encoding="utf-8", errors="ignore") as f:
        content = f.read()
    
    content = re.sub(r"package com\.navatation\.business\.dto;", f"package {new_pkg};", content)
    content = re.sub(fr"\bpublic\s+class\s+{old_name}\b", f"public class {new_name}", content)
    
    for o, (n, _, _) in rename_map.items():
        content = re.sub(fr"\b{o}\b", n, content)
        
    with open(new_file, "w", encoding="utf-8") as f:
        f.write(content)
        
    os.remove(old_file)

# Now fix wildcard imports and string replacements in all files
for search_dir in [ADMIN_DIR, r"E:\workspace\navatation\doc"]:
    for root, dirs, files in os.walk(search_dir):
        for file in files:
            if file.endswith(".java") or file.endswith(".md"):
                filepath = os.path.join(root, file)
                with open(filepath, "r", encoding="utf-8", errors="ignore") as f:
                    content = f.read()
                
                orig_content = content
                
                # Replace wildcard imports
                wildcard_import = "import com.navatation.business.dto.*;"
                replacement_imports = "import com.navatation.business.dto.req.*;\nimport com.navatation.business.dto.resp.*;\nimport com.navatation.common.dto.resp.*;"
                content = content.replace(wildcard_import, replacement_imports)
                
                for old_name, (new_name, new_dir, new_pkg) in rename_map.items():
                    content = re.sub(fr"import com\.navatation\.business\.dto\.{old_name};", f"import {new_pkg}.{new_name};", content)
                    
                for old_name, (new_name, _, _) in rename_map.items():
                    content = re.sub(fr"\b{old_name}\b", new_name, content)
                    
                if content != orig_content:
                    with open(filepath, "w", encoding="utf-8") as f:
                        f.write(content)

print("Remaining DTOs renamed and wildcards fixed.")
