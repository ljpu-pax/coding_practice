#!/usr/bin/env python3
"""
快速网络测试 - 检查当前连接状态
"""

import subprocess
import re

def run_cmd(cmd):
    try:
        result = subprocess.run(cmd, shell=True, capture_output=True, text=True, timeout=10)
        return result.stdout.strip()
    except:
        return ""

print("=" * 60)
print("快速网络诊断")
print("=" * 60)

# 1. 当前连接的WiFi
print("\n【1. 当前WiFi网络】")
ssid = run_cmd("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I | grep ' SSID' | awk '{print $2}'")
print(f"网络名称: {ssid}")

# 2. 连接频段
print("\n【2. 频段信息】")
channel_info = run_cmd("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I | grep channel")
print(channel_info)

# 提取频道号
channel_match = re.search(r'channel:\s+(\d+)', channel_info)
if channel_match:
    channel = int(channel_match.group(1))
    if channel <= 14:
        band = "2.4 GHz (较慢但覆盖范围大)"
    else:
        band = "5 GHz (较快但覆盖范围小)"
    print(f"→ 你当前连接的是: {band}")

# 3. 信号质量
print("\n【3. 信号质量】")
rssi = run_cmd("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I | grep 'agrCtlRSSI' | awk '{print $2}'")
noise = run_cmd("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I | grep 'agrCtlNoise' | awk '{print $2}'")
tx_rate = run_cmd("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I | grep 'lastTxRate' | awk '{print $2}'")

try:
    rssi_val = int(rssi)
    if rssi_val > -50:
        quality = "优秀"
    elif rssi_val > -60:
        quality = "良好"
    elif rssi_val > -70:
        quality = "一般"
    else:
        quality = "较差"
    print(f"信号强度: {rssi} dBm ({quality})")
except:
    print(f"信号强度: {rssi} dBm")

print(f"噪声水平: {noise} dBm")
print(f"连接速率: {tx_rate} Mbps")

# 4. 路由器IP和延迟
print("\n【4. 路由器连接】")
gateway = run_cmd("netstat -nr | grep default | grep -v ':' | awk '{print $2}' | head -1")
print(f"路由器IP: {gateway}")

ping_result = run_cmd(f"ping -c 5 {gateway} | tail -1")
print(f"到路由器延迟: {ping_result}")

# 5. 互联网延迟
print("\n【5. 互联网连接】")
internet_ping = run_cmd("ping -c 5 8.8.8.8 | tail -1")
print(f"到互联网延迟: {internet_ping}")

# 6. DNS查询速度
print("\n【6. DNS查询】")
dns_test = run_cmd("time dig google.com +short 2>&1 | grep real")
print(f"DNS查询时间: {dns_test}")

print("\n" + "=" * 60)
print("诊断建议：")
print("=" * 60)
print("1. 如果你在慢的电脑上看到 '2.4 GHz'，请尝试连接 5GHz 网络")
print("2. 如果信号强度 < -70 dBm，说明距离太远或有障碍物")
print("3. 如果到路由器的延迟 > 10ms，可能是WiFi拥挤或干扰")
print("4. 对比两台电脑的结果，看看差异在哪里")
print("\n请在另一台慢的电脑上也运行此脚本进行对比！")
