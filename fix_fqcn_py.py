import os
import re

ADMIN_DIR = r"E:\workspace\navatation\navatation-admin"
BUSINESS_DIR = os.path.join(ADMIN_DIR, "navatation-business", "src", "main", "java", "com", "navatation", "business")
COMMON_DIR = os.path.join(ADMIN_DIR, "navatation-common", "src", "main", "java", "com", "navatation", "common")

# Fix FQCNs in specific files
files_to_fix = [
    (os.path.join(BUSINESS_DIR, "service", "TodoService.java"), [
        (r"com\.navatation\.business\.entity\.root\.RootTodoItem", "RootTodoItem"),
        (r"import com\.navatation\.business\.entity\.nav\.TodoItem;", "import com.navatation.business.entity.nav.TodoItem;\nimport com.navatation.business.entity.root.RootTodoItem;")
    ]),
    (os.path.join(BUSINESS_DIR, "service", "WidgetService.java"), [
        (r"com\.navatation\.business\.entity\.root\.RootWidget", "RootWidget"),
        (r"com\.navatation\.business\.mapper\.UserMapper", "UserMapper"),
        (r"import com\.navatation\.business\.entity\.nav\.Widget;", "import com.navatation.business.entity.nav.Widget;\nimport com.navatation.business.entity.root.RootWidget;")
    ]),
    (os.path.join(BUSINESS_DIR, "service", "SettingsService.java"), [
        (r"com\.navatation\.business\.entity\.root\.RootConfig", "RootConfig"),
        (r"java\.io\.File\.separator", "File.separator"),
        (r"com\.navatation\.common\.FileUploadUtil", "FileUploadUtil"),
        (r"java\.util\.concurrent\.ThreadLocalRandom", "ThreadLocalRandom"),
        (r"import com\.navatation\.business\.entity\.nav\.UserConfig;", "import com.navatation.business.entity.nav.UserConfig;\nimport java.io.File;\nimport com.navatation.common.FileUploadUtil;\nimport java.util.concurrent.ThreadLocalRandom;\nimport com.navatation.business.entity.root.RootConfig;")
    ]),
    (os.path.join(BUSINESS_DIR, "..", "..", "..", "..", "test", "java", "com", "navatation", "business", "AuthServiceIntegrationTest.java"), [
        (r"com\.navatation\.business\.mapper\.RootUserMapper", "RootUserMapper"),
        (r"com\.navatation\.business\.entity\.root\.RootUser", "RootUser"),
        (r"import com\.navatation\.business\.entity\.nav\.User;", "import com.navatation.business.entity.nav.User;\nimport com.navatation.business.entity.root.RootUser;\nimport com.navatation.business.mapper.RootUserMapper;")
    ]),
    (os.path.join(COMMON_DIR, "IdUtils.java"), [
        (r"java\.security\.SecureRandom", "SecureRandom"),
        (r"import java\.util\.UUID;", "import java.util.UUID;\nimport java.security.SecureRandom;")
    ])
]

for filepath, replacements in files_to_fix:
    if os.path.exists(filepath):
        with open(filepath, "r", encoding="utf-8") as f:
            content = f.read()
        for old, new in replacements:
            content = re.sub(old, new, content)
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)

print("FQCN fix complete.")
