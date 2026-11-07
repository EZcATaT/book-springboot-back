# 图书借阅管理系统 · 后端

基于 **Spring Boot 3 + MyBatis + MySQL** 的图书借阅管理系统后端，供单人课程设计使用。

前端仓库：`book-vue-front`  
接口风格：RESTful JSON API  
默认端口：`8080`  
数据库：`book_borrow`

---

## 1. 技术栈

| 分类 | 技术 |
| --- | --- |
| 开发语言 | Java 17 |
| 后端框架 | Spring Boot 3.2.5 |
| 持久层 | MyBatis 3.0.3 |
| 数据库 | MySQL 5.6+ |
| 数据库驱动 | mysql-connector-j 8.3.0 |
| 构建工具 | Maven |
| 开发工具 | IntelliJ IDEA |
| 简化代码 | Lombok |

---

## 2. 功能模块

### 2.1 用户模块

- 用户注册（自助注册，默认普通用户）
- 用户登录（返回登录令牌和用户信息）
- 用户登出（令牌失效）
- 用户列表、按 id 查询
- 新增用户、修改用户、删除用户（仅管理员）

### 2.2 图书模块

- 图书列表、按 id 查询（登录用户）
- 新增图书、修改图书、删除图书（仅管理员）
- ISBN 唯一校验
- 库存不能为负

### 2.3 借阅模块

- 全部借阅记录（仅管理员）
- 指定用户的借阅记录（管理员或本人）
- 按 id 查询单条记录（管理员或本人）
- 新增借阅（借书）
- 修改借阅记录（归还图书，写入归还时间）
- 删除借阅记录（仅管理员）

### 2.4 借还书库存联动

- 借书成功：库存 `stock = stock - 1`
- 归还成功：库存 `stock = stock + 1`
- 库存为 0 时禁止借阅
- 同一用户不能重复借阅同一本尚未归还的书
- 删除未归还的借阅记录时自动补回库存
- 借书、还书、删除记录均使用 `@Transactional` 保证事务一致性

---

## 3. 角色与权限

项目按原需求只建立 `id、username、password、name` 四个用户字段，因此角色不单独存表：

| 账号 | 角色 | 说明 |
| --- | --- | --- |
| `admin` | 管理员 | 系统保留账号，可管理用户、图书和全部借阅记录 |
| 其他账号 | 普通用户 | 可浏览图书、借书、归还自己的书、查看自己的记录 |

后端通过登录令牌识别当前用户，并在 Controller 层进行权限判断：

- 普通用户访问用户管理接口 → 拒绝
- 普通用户新增/修改/删除图书 → 拒绝
- 普通用户查看他人的借阅记录 → 拒绝
- 普通用户删除借阅记录 → 拒绝
- 管理员拥有全部权限

> 说明：这是课程项目的简化权限实现。令牌保存在内存中，服务重启后需要重新登录。

---

## 4. 项目结构

```text
book-springboot-back
├── pom.xml
├── sql/
│   └── book_borrow.sql          # 建库、建表、演示数据
├── docs/
│   ├── API.md                   # 接口文档
│   └── 测试记录.md              # 测试用例与结果
└── src/main/
    ├── java/com/book/
    │   ├── BookBackApplication.java
    │   ├── auth/                # 令牌、登录上下文、权限工具
    │   ├── common/              # 统一返回、异常处理
    │   ├── config/              # CORS、拦截器配置
    │   ├── controller/          # RESTful 接口
    │   ├── dto/                 # 登录请求/响应
    │   ├── entity/              # User / Book / Borrow
    │   ├── mapper/              # MyBatis Mapper 接口
    │   └── service/             # 业务接口与实现
    └── resources/
        ├── application.yml
        └── mapper/              # MyBatis XML
```

---

## 5. 数据库设计

数据库名：`book_borrow`

### user 表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | int | 主键，自增 |
| username | varchar(50) | 登录账号，唯一 |
| password | varchar(100) | 登录密码 |
| name | varchar(50) | 姓名 |

### book 表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | int | 主键，自增 |
| book_name | varchar(100) | 书名 |
| author | varchar(50) | 作者 |
| isbn | varchar(20) | ISBN，唯一 |
| stock | int | 库存，不能为负 |

### borrow 表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | int | 主键，自增 |
| user_id | int | 用户 id |
| book_id | int | 图书 id |
| borrow_time | datetime | 借阅时间 |
| return_time | datetime | 归还时间，NULL 表示未归还 |

---

## 6. 本地运行

### 6.1 初始化数据库

确保 MySQL 已启动，然后在命令行执行：

```bash
mysql -uroot -p < sql/book_borrow.sql
```

或者在 Navicat / IDEA Database 中打开 `sql/book_borrow.sql` 并执行。

脚本会：

1. 创建数据库 `book_borrow`
2. 重建 `user`、`book`、`borrow` 三张表
3. 写入 3 个演示用户、8 本图书、5 条借阅记录

> 脚本包含 `DROP TABLE`，重复执行会清空原来的三张表，请勿在已有正式数据上直接执行。

### 6.2 修改数据库连接

打开 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/book_borrow?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 123456
```

本机密码不是 `123456` 时修改 `password`，也可以通过环境变量覆盖：

```bash
DB_USERNAME=root
DB_PASSWORD=你的密码
```

### 6.3 启动后端

**方式一：IDEA（推荐）**

1. 用 IDEA 打开 `book-springboot-back` 目录
2. 等待 Maven 依赖下载完成
3. 运行 `com.book.BookBackApplication`
4. 控制台出现 `Tomcat started on port 8080` 即启动成功

**方式二：命令行**

```bash
mvn clean package -DskipTests
java -jar target/book-springboot-back.jar
```

启动成功后访问：

```text
http://localhost:8080/api/health
```

返回：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "book-springboot-back is running"
}
```

---

## 7. 演示账号

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| `admin` | `123456` | 管理员 |
| `reader` | `123456` | 普通用户 |
| `lisi` | `123456` | 普通用户 |

---

## 8. 接口说明

完整接口、参数和返回示例见 [`docs/API.md`](docs/API.md)。

核心接口前缀：`/api`

| 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- |
| POST | `/api/auth/register` | 注册 | 公开 |
| POST | `/api/auth/login` | 登录 | 公开 |
| POST | `/api/auth/logout` | 登出 | 登录 |
| GET | `/api/users` | 用户列表 | 管理员 |
| POST | `/api/users` | 新增用户 | 管理员 |
| PUT | `/api/users/{id}` | 修改用户 | 管理员 |
| DELETE | `/api/users/{id}` | 删除用户 | 管理员 |
| GET | `/api/books` | 图书列表 | 登录 |
| POST | `/api/books` | 新增图书 | 管理员 |
| PUT | `/api/books/{id}` | 修改图书 | 管理员 |
| DELETE | `/api/books/{id}` | 删除图书 | 管理员 |
| GET | `/api/borrows` | 全部借阅记录 | 管理员 |
| GET | `/api/users/{userId}/borrows` | 指定用户记录 | 管理员/本人 |
| POST | `/api/borrows` | 借书 | 登录 |
| PUT | `/api/borrows/{id}` | 归还 | 管理员/本人 |
| DELETE | `/api/borrows/{id}` | 删除记录 | 管理员 |

除登录、注册外，请求需要携带：

```http
Authorization: Bearer <登录返回的 token>
```

> 兼容说明：最初的接口清单使用 `/user/list`、`/user/add`、`/book/list` 等路径，
> 项目仍保留这些旧路径，前端统一使用上面的 RESTful 路径。

---

## 9. 跨域说明

`config/CorsConfig.java` 已允许前后端分离开发时的跨域请求：

```java
registry.addMapping("/**")
        .allowedOriginPatterns("*")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*");
```

前端开发地址通常是 `http://127.0.0.1:5173`，后端是 `http://127.0.0.1:8080`，可以直接访问。

---

## 10. 注意事项

- 密码当前按课程要求使用明文存储，仅用于演示，不要用于真实生产环境。
- 登录令牌保存在内存中，重启后端后前端需要重新登录。
- 用户表和图书表存在历史借阅记录时，后端会阻止删除，避免出现孤立记录。
- 生产环境建议增加：BCrypt 密码加密、JWT 或 Redis 会话、参数校验、分页、日志审计。

---

## 11. Git

远程仓库：

```text
https://github.com/EZcATaT/book-springboot-back.git
```

提交时建议使用清晰的提交信息，例如：

```text
feat: 完成用户登录与权限控制
feat: 完成图书和借阅管理
docs: 补充数据库与接口说明
```
