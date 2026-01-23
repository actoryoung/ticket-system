# 工单系统 - 部署和运行指南

## 项目概述

这是一个完整的工单/问题反馈管理系统，采用前后端分离架构。

**技术栈**：
- 前端: Vue 3 + Element Plus + TailwindCSS + Vite + ECharts
- 后端: Spring Boot 2.5 + MyBatis-Plus + MySQL 8.0 + Redis + JWT

## 功能特性

### 核心功能
1. **工单提交**: 用户提交问题/工单，支持分类选择、优先级设置、附件上传
2. **状态流转**: 待处理 → 处理中 → 已完成 → 已关闭，完整的状态机设计
3. **工单处理**: 处理人员分配、处理记录填写、处理时间统计
4. **权限控制**: 三种角色（普通用户、处理人员、管理员）
5. **统计分析**: 工单数量统计、处理时长分析、按分类统计

### 角色权限
| 角色 | 权限 |
|------|------|
| USER | 提交工单、查看自己的工单、添加评论 |
| HANDLER | 查看所有工单、分配工单、更新工单状态、添加评论 |
| ADMIN | 全部权限 + 查看统计分析 |

## 快速开始

### 前置要求

- JDK 1.8+
- Node.js 16+
- MySQL 8.0+
- Redis（可选，用于缓存）
- Maven 3.6+

### 1. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source backend/src/main/resources/db/init.sql
```

或者使用 MySQL 客户端工具执行 `backend/src/main/resources/db/init.sql` 文件。

**测试账户**（密码均为 `123456`）：
- 管理员: `admin`
- 处理人: `handler1`, `handler2`
- 普通用户: `user1`, `user2`

### 2. 后端启动

```bash
# 进入后端目录
cd backend

# 修改配置文件
# 编辑 src/main/resources/application.yml
# 配置数据库连接信息（username, password）

# 启动后端服务
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080/api` 启动。

### 3. 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:5173` 启动。

## 目录结构

```
ticket-system/
├── backend/                           # 后端项目
│   ├── src/main/java/com/ticketsystem/
│   │   ├── config/                   # 配置类
│   │   ├── controller/               # 控制器
│   │   ├── dto/                      # 数据传输对象
│   │   ├── entity/                   # 实体类
│   │   ├── enums/                    # 枚举类
│   │   ├── exception/                # 异常处理
│   │   ├── filter/                   # 过滤器
│   │   ├── mapper/                   # MyBatis Mapper
│   │   ├── service/                  # 业务层
│   │   ├── util/                     # 工具类
│   │   └── vo/                       # 视图对象
│   ├── src/main/resources/
│   │   ├── db/init.sql              # 数据库初始化脚本
│   │   ├── mapper/                  # MyBatis XML映射
│   │   └── application.yml          # 配置文件
│   └── pom.xml                      # Maven依赖
│
├── frontend/                         # 前端项目
│   ├── src/
│   │   ├── api/                     # API接口
│   │   ├── components/              # 组件
│   │   ├── router/                  # 路由配置
│   │   ├── stores/                  # Pinia状态管理
│   │   ├── utils/                   # 工具函数
│   │   └── views/                   # 页面组件
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── tailwind.config.js
│
└── README.md                         # 项目说明
```

## API 文档

### 认证接口

#### 登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}

Response:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "role": "ADMIN"
    }
  }
}
```

#### 获取当前用户信息
```
GET /api/auth/user-info
Authorization: Bearer {token}
```

#### 退出登录
```
POST /api/auth/logout
Authorization: Bearer {token}
```

### 工单接口

#### 获取工单列表
```
GET /api/ticket/list?current=1&size=10&status=PENDING
Authorization: Bearer {token}
```

#### 获取工单详情
```
GET /api/ticket/{id}
Authorization: Bearer {token}
```

#### 创建工单
```
POST /api/ticket/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "工单标题",
  "description": "详细描述",
  "category": "BUG",
  "priority": "HIGH",
  "attachmentUrls": []
}
```

#### 分配工单
```
PUT /api/ticket/{id}/assign
Authorization: Bearer {token}
Content-Type: application/json

{
  "handlerId": 2
}
```

#### 更新工单状态
```
PUT /api/ticket/{id}/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": "PROCESSING",
  "remark": "开始处理"
}
```

#### 添加评论
```
POST /api/ticket/{id}/comment
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "评论内容",
  "isInternal": 0
}
```

### 文件上传

```
POST /api/upload
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: [文件]
```

## 数据库表结构

### users (用户表)
- `id`: 用户ID
- `username`: 用户名
- `password`: 密码（BCrypt加密）
- `real_name`: 真实姓名
- `role`: 角色（USER/HANDLER/ADMIN）
- `status`: 状态（0-禁用，1-启用）

### tickets (工单表)
- `id`: 工单ID
- `ticket_no`: 工单编号（自动生成）
- `title`: 工单标题
- `description`: 工单描述
- `category`: 分类（BUG/FEATURE/SUPPORT/OTHER）
- `priority`: 优先级（LOW/MEDIUM/HIGH/URGENT）
- `status`: 状态（PENDING/PROCESSING/RESOLVED/CLOSED）
- `creator_id`: 创建人ID
- `handler_id`: 处理人ID
- `attachment_urls`: 附件URL列表（JSON）
- `resolution`: 解决方案

### ticket_status_history (状态历史表)
- `id`: 记录ID
- `ticket_id`: 工单ID
- `old_status`: 原状态
- `new_status`: 新状态
- `operator_id`: 操作人ID
- `operator_name`: 操作人姓名
- `remark`: 备注说明

### ticket_comments (评论表)
- `id`: 评论ID
- `ticket_id`: 工单ID
- `user_id`: 评论人ID
- `user_name`: 评论人姓名
- `content`: 评论内容
- `is_internal`: 是否内部评论（0-否，1-是）

## 开发说明

### 后端开发

**统一响应格式**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1234567890
}
```

**异常处理**：
- `BusinessException`: 业务异常
- `GlobalExceptionHandler`: 全局异常处理器

**权限控制**：
- 使用 Spring Security + JWT
- 通过 `SecurityUtil` 获取当前登录用户信息
- Controller 层进行权限验证

### 前端开发

**状态管理**：
- 使用 Pinia 进行状态管理
- `useUserStore`: 用户状态

**API 调用**：
- 统一使用 `src/utils/request.js` 封装的 axios 实例
- 自动添加 JWT Token 到请求头
- 统一错误处理

**路由守卫**：
- 登录验证：检查 token 是否存在
- 权限验证：检查用户角色

## 常见问题

### 1. 后端启动失败
- 检查 MySQL 是否启动
- 检查数据库连接配置是否正确
- 检查端口 8080 是否被占用

### 2. 前端无法连接后端
- 检查后端服务是否正常启动
- 检查 Vite 代理配置
- 浏览器 F12 查看网络请求错误

### 3. 登录失败
- 确认数据库中存在测试用户
- 确认密码为 `123456`
- 检查用户状态是否为启用（status=1）

## 生产部署

### 后端打包

```bash
cd backend
mvn clean package -DskipTests

# 运行 jar 包
java -jar target/ticket-system-backend-1.0.0.jar
```

### 前端打包

```bash
cd frontend
npm run build

# 产物在 dist 目录
# 部署到 Nginx 或其他静态文件服务器
```

## 许可证

MIT License

## 联系方式

如有问题，请提交 Issue 或联系开发团队。
