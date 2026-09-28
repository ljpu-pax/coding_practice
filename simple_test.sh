#!/bin/bash
# 简单的网络测试脚本 - 适用于所有系统

echo "======================================"
echo "网络诊断测试"
echo "======================================"

echo ""
echo "【1. 本机IP地址】"
ifconfig | grep "inet " | grep -v 127.0.0.1

echo ""
echo "【2. 路由器地址】"
netstat -rn | grep default | head -1

echo ""
echo "【3. Ping路由器 (10次)】"
ping -c 10 10.0.0.1

echo ""
echo "【4. Ping互联网 (10次)】"
ping -c 10 8.8.8.8

echo ""
echo "【5. 持续监控 (30秒，每3秒ping一次)】"
for i in {1..10}; do
    timestamp=$(date +"%H:%M:%S")
    result=$(ping -c 1 10.0.0.1 | grep "time=" | awk -F'time=' '{print $2}' | awk '{print $1}')
    echo "[$timestamp] 延迟: $result"
    sleep 3
done

echo ""
echo "测试完成！"
