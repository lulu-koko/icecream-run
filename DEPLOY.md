# IceCream Run 运行说明

本项目已改造为 Spring Boot Web 项目。网页游戏文件由 Spring Boot 提供，成绩保存到本地 H2 文件数据库。

## 本地运行

```powershell
mvn spring-boot:run
```

浏览器打开：

```text
http://localhost:8080
```

## 打包 jar

```powershell
mvn clean package
java -jar target\icecream-run-1.0.0.jar
```

运行 jar 后打开：

```text
http://localhost:8080
```

成绩数据库会保存在项目运行目录下的 `data/` 文件夹。

## 接口

提交成绩：

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/scores -ContentType "application/json" -Body '{"userId":"student001","completed":true,"durationMs":28500,"iceCreamCount":6,"bananaHits":1,"maxBrainFreeze":64,"finalDistance":1000}'
```

查询排行榜：

```powershell
Invoke-RestMethod http://localhost:8080/api/leaderboard?limit=20
```

健康检查：

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

## 简单压力测试

```powershell
1..100 | ForEach-Object -Parallel {
  $body = @{
    userId = "user-$($_)"
    completed = ($_ % 3 -ne 0)
    durationMs = 20000 + ($_ * 137)
    iceCreamCount = $_ % 12
    bananaHits = $_ % 3
    maxBrainFreeze = $_ % 100
    finalDistance = if ($_ % 3 -ne 0) { 1000 } else { 300 + $_ }
  } | ConvertTo-Json
  Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/scores -ContentType "application/json" -Body $body
} -ThrottleLimit 20

Invoke-RestMethod http://localhost:8080/api/leaderboard?limit=20
```
