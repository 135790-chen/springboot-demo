#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""一键导入 Nacos 配置 —— 把 nacos-configs/ 下的 yaml 文件发到 Nacos"""

import os, sys, io
from urllib.request import Request, urlopen
from urllib.parse import urlencode
from pathlib import Path

# 修复 Windows GBK 终端编码问题
if sys.platform == "win32":
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")

NACOS_URL = os.environ.get("NACOS_URL", "http://localhost:8848")
GROUP = "DEFAULT_GROUP"

CONFIGS = [
    "shared-config.yaml",
    "auth-service.yaml",
    "student-service.yaml",
    "message-service.yaml",
    "gateway.yaml",
]

configs_dir = Path(__file__).parent / "nacos-configs"

print(f"Nacos: {NACOS_URL}")
print(f"Group: {GROUP}")
print("-" * 40)

for name in CONFIGS:
    data_id = name
    filepath = configs_dir / name
    if not filepath.exists():
        print(f"[SKIP] {name} — file not found")
        continue

    content = filepath.read_text(encoding="utf-8")
    body = urlencode({
        "dataId": data_id,
        "group": GROUP,
        "content": content,
    }).encode("utf-8")

    url = f"{NACOS_URL}/nacos/v1/cs/configs"
    req = Request(url, data=body, method="POST")
    req.add_header("Content-Type", "application/x-www-form-urlencoded")

    try:
        resp = urlopen(req, timeout=10)
        result = resp.read().decode()
        ok = "OK" if resp.status == 200 else "FAIL"
        print(f"[{ok}] {name} -> {result}")
    except Exception as e:
        print(f"[FAIL] {name} -> {e}")

print("-" * 40)
print("Done! Check Nacos console: http://localhost:8848/nacos")
