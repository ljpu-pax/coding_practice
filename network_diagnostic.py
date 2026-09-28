#!/usr/bin/env python3
"""
网络诊断工具 - 测试路由器是否限速或有设备上限
"""

import subprocess
import time
import json
from datetime import datetime

def run_command(cmd):
    """运行系统命令并返回结果"""
    try:
        result = subprocess.run(cmd, shell=True, capture_output=True, text=True, timeout=30)
        return result.stdout.strip()
    except Exception as e:
        return f"错误: {str(e)}"

def get_wifi_info():
    """获取WiFi连接信息（macOS）"""
    print("=== WiFi 连接信息 ===")

    # 获取当前WiFi网络
    ssid = run_command("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I | grep ' SSID' | awk '{print $2}'")
    print(f"当前网络: {ssid}")

    # 获取信号强度
    rssi = run_command("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I | grep 'agrCtlRSSI' | awk '{print $2}'")
    print(f"信号强度 (RSSI): {rssi} dBm")

    # 获取噪声水平
    noise = run_command("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I | grep 'agrCtlNoise' | awk '{print $2}'")
    print(f"噪声水平: {noise} dBm")

    # 获取传输速率
    tx_rate = run_command("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I | grep 'lastTxRate' | awk '{print $2}'")
    print(f"传输速率: {tx_rate} Mbps")

    # 获取频段
    channel = run_command("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I | grep ' channel' | awk '{print $2}'")
    print(f"频道: {channel}")

    print()

def ping_test(host="8.8.8.8", count=10):
    """Ping测试 - 检测延迟和丢包"""
    print(f"=== Ping测试 ({host}) ===")
    result = run_command(f"ping -c {count} {host}")
    print(result)
    print()

def speed_test_simple():
    """简单的下载速度测试"""
    print("=== 下载速度测试 ===")
    print("正在测试下载速度...")

    # 使用curl测试下载速度
    result = run_command("curl -o /dev/null -w 'Speed: %{speed_download} bytes/sec\\nTime: %{time_total}s\\n' https://speed.cloudflare.com/__down?bytes=10000000 2>&1")
    print(result)
    print()

def check_router_gateway():
    """检查路由器网关"""
    print("=== 路由器网关信息 ===")
    gateway = run_command("netstat -nr | grep default | grep -v ':' | awk '{print $2}' | head -1")
    print(f"路由器IP: {gateway}")

    # Ping路由器
    print(f"\nPing路由器 ({gateway}):")
    result = run_command(f"ping -c 5 {gateway}")
    print(result)
    print()

def check_network_interfaces():
    """检查网络接口"""
    print("=== 网络接口信息 ===")
    result = run_command("ifconfig | grep -A 3 'en0'")
    print(result)
    print()

def continuous_speed_monitor(duration_minutes=5, interval_seconds=30):
    """持续监控网速变化"""
    print(f"=== 持续监控 ({duration_minutes}分钟，每{interval_seconds}秒测试一次) ===")

    end_time = time.time() + (duration_minutes * 60)
    results = []

    while time.time() < end_time:
        timestamp = datetime.now().strftime("%H:%M:%S")
        print(f"\n[{timestamp}] 测试中...")

        # Ping测试
        ping_result = run_command("ping -c 3 8.8.8.8 | tail -1 | awk -F'/' '{print $5}'")

        # WiFi信号
        rssi = run_command("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I | grep 'agrCtlRSSI' | awk '{print $2}'")

        result = {
            'time': timestamp,
            'ping_avg': ping_result,
            'rssi': rssi
        }
        results.append(result)

        print(f"  延迟: {ping_result} ms, 信号: {rssi} dBm")

        time.sleep(interval_seconds)

    print("\n=== 监控总结 ===")
    for r in results:
        print(f"{r['time']}: 延迟={r['ping_avg']}ms, 信号={r['rssi']}dBm")
    print()

def main():
    print("=" * 60)
    print("网络诊断工具")
    print("=" * 60)
    print()

    # 基本信息收集
    get_wifi_info()
    check_router_gateway()
    check_network_interfaces()

    # 网络测试
    ping_test("8.8.8.8", 20)  # Google DNS
    ping_test("1.1.1.1", 20)  # Cloudflare DNS

    # 速度测试
    speed_test_simple()

    # 询问是否进行持续监控
    print("\n" + "=" * 60)
    response = input("是否进行5分钟持续监控？(y/n): ")
    if response.lower() == 'y':
        continuous_speed_monitor(5, 30)

    print("\n诊断完成！")
    print("\n建议检查项：")
    print("1. 如果信号强度 < -70 dBm，可能是距离太远或有障碍物")
    print("2. 如果延迟波动很大，可能是带宽被占用或QoS设置问题")
    print("3. 如果到路由器的ping也很慢，可能是路由器性能问题")
    print("4. 检查路由器管理界面的连接设备数量")
    print("5. 尝试切换WiFi频段（2.4GHz ↔ 5GHz）")

if __name__ == "__main__":
    main()
