# Demo 2: 工单/问题反馈系统

> 基于 AipexBase 的工作流管理演示项目

## 项目概述

这是一个展示工作流设计和状态管理的演示项目，包含问题提交、状态流转、优先级处理和权限控制功能。

## 技术栈

**前端：**
- Vue 3
- Element Plus
- TailwindCSS
- Vite

**后端：**
- Spring Boot 2.5
- MyBatis-Plus
- MySQL 8.0
- Redis

## 功能模块

### 1. 工单提交
- 用户提交问题/工单
- 工单分类选择
- 优先级设置
- 附件上传

### 2. 状态流转
- 待处理 → 处理中 → 已完成
- 状态机设计
- 状态变更记录

### 3. 工单处理
- 处理人员分配
- 处理记录填写
- 处理时间统计

### 4. 权限控制
- 普通用户：提交和查看自己的工单
- 处理人员：查看和分配工单
- 管理员：全部权限

### 5. 统计分析
- 工单数量统计
- 处理时长分析
- 按分类统计

## 项目结构

```
ticket-system/
├── frontend/           # Vue 3 前端
│   ├── src/
│   │   ├── views/     # 页面组件
│   │   ├── components/# 通用组件
│   │   ├── api/       # API 接口
│   │   └── router/    # 路由配置
│   └── package.json
├── backend/           # Spring Boot 后端
│   ├── src/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── entity/
│   │   └── mapper/
│   └── pom.xml
├── .claude/           # AI 辅助开发配置
└── README.md
```

## 快速开始

### 前端启动
```bash
cd frontend
npm install
npm run dev
```

### 后端启动
```bash
cd backend
mvn spring-boot:run
```

## 开发进度

- [x] 项目初始化
- [x] 数据库设计
- [x] 后端 API 开发
- [x] 前端页面开发
- [ ] 功能测试
- [ ] 在线部署

## 快速开始

详细部署指南请查看 [SETUP.md](./SETUP.md)

### 1. 数据库初始化
```bash
mysql -u root -p < backend/src/main/resources/db/init.sql
```

### 2. 启动后端
```bash
cd backend
# 修改 application.yml 中的数据库配置
mvn spring-boot:run
```

### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
```

### 4. 访问系统
- 前端地址: http://localhost:5173
- 后端地址: http://localhost:8080/api

**测试账户**（密码: 123456）:
- 管理员: admin
- 处理人: handler1, handler2
- 普通用户: user1, user2

## 测试

详细的测试计划请查看 [TEST_PLAN.md](./TEST_PLAN.md)

## 项目结构

```
ticket-system/
├── backend/              # Spring Boot 后端
│   ├── src/main/java/
│   │   └── com/ticketsystem/
│   │       ├── config/          # 配置类
│   │       ├── controller/      # REST 控制器
│   │       ├── dto/             # 数据传输对象
│   │       ├── entity/          # 实体类
│   │       ├── enums/           # 枚举类
│   │       ├── exception/       # 异常处理
│   │       ├── filter/          # JWT 过滤器
│   │       ├── mapper/          # MyBatis Mapper
│   │       ├── service/         # 业务层
│   │       ├── util/            # 工具类
│   │       └── vo/              # 视图对象
│   ├── src/main/resources/
│   │   ├── db/init.sql         # 数据库初始化脚本
│   │   ├── mapper/             # MyBatis XML
│   │   └── application.yml     # 配置文件
│   └── pom.xml
│
├── frontend/             # Vue 3 前端
│   ├── src/
│   │   ├── api/                # API 接口
│   │   ├── components/         # 组件
│   │   ├── router/             # 路由
│   │   ├── stores/             # Pinia 状态
│   │   ├── utils/              # 工具
│   │   └── views/              # 页面
│   ├── package.json
│   └── vite.config.js
│
├── SETUP.md              # 部署指南
├── TEST_PLAN.md          # 测试计划
└── README.md             # 项目说明
```

## 对应客户需求

此项目可复用于：
- 客服工单系统
- 问题反馈系统
- 审批流程系统
- 任务管理系统
- 售后服务系统

---

> 下一步：生成详细开发计划
