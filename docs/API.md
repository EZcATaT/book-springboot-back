# 接口文档

## 1. 基本约定

- 基础地址：`http://localhost:8080/api`
- 请求格式：`application/json`
- 登录后请求头：`Authorization: Bearer <token>`
- 统一返回结构：

```json
{
  "code": 200,
  "msg": "提示信息",
  "data": {}
}
```

`code = 200` 表示成功，`code = 500` 表示失败。  
登录、注册、健康检查接口不需要携带令牌。

---

## 2. 认证接口

### 2.1 注册

`POST /api/auth/register`

请求体：

```json
{
  "username": "student01",
  "password": "123456",
  "name": "张三"
}
```

成功响应：

```json
{
  "code": 200,
  "msg": "注册成功，请登录",
  "data": null
}
```

说明：注册账号固定为普通用户，`admin` 是系统保留账号，不能注册。

### 2.2 登录

`POST /api/auth/login`

请求体：

```json
{
  "username": "admin",
  "password": "123456"
}
```

成功响应：

```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "3f8c9d2a4b7e4f1c8a6d5e3b2c1f0a9d",
    "user": {
      "id": 1,
      "username": "admin",
      "name": "管理员",
      "role": "admin"
    }
  }
}
```

失败响应：

```json
{
  "code": 500,
  "msg": "用户名或密码错误",
  "data": null
}
```

### 2.3 登出

`POST /api/auth/logout`

请求头：

```http
Authorization: Bearer <token>
```

成功响应：

```json
{
  "code": 200,
  "msg": "已退出登录",
  "data": null
}
```

---

## 3. 用户接口（管理员）

### 3.1 用户列表

`GET /api/users`

成功响应：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "username": "admin",
      "name": "管理员",
      "role": "admin"
    },
    {
      "id": 2,
      "username": "reader",
      "name": "读者小王",
      "role": "user"
    }
  ]
}
```

### 3.2 查询单个用户

`GET /api/users/{id}`

### 3.3 新增用户

`POST /api/users`

```json
{
  "username": "wangwu",
  "password": "123456",
  "name": "王五"
}
```

### 3.4 修改用户

`PUT /api/users/{id}`

```json
{
  "username": "wangwu",
  "password": "",
  "name": "王五（新）"
}
```

密码留空表示不修改原密码；`admin` 的用户名不可修改。

### 3.5 删除用户

`DELETE /api/users/{id}`

用户存在借阅记录时删除会失败，需要先删除相关借阅记录。

---

## 4. 图书接口

### 4.1 图书列表

`GET /api/books`

成功响应：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "bookName": "三体",
      "author": "刘慈欣",
      "isbn": "9787536692930",
      "stock": 4
    }
  ]
}
```

### 4.2 查询单本图书

`GET /api/books/{id}`

### 4.3 新增图书（管理员）

`POST /api/books`

```json
{
  "bookName": "深入理解计算机系统",
  "author": "Randal E. Bryant",
  "isbn": "9787111544937",
  "stock": 3
}
```

### 4.4 修改图书（管理员）

`PUT /api/books/{id}`

```json
{
  "bookName": "深入理解计算机系统（第3版）",
  "author": "Randal E. Bryant",
  "isbn": "9787111544937",
  "stock": 5
}
```

### 4.5 删除图书（管理员）

`DELETE /api/books/{id}`

存在借阅记录时删除会失败。

---

## 5. 借阅接口

### 5.1 全部借阅记录（管理员）

`GET /api/borrows`

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 3,
      "userId": 3,
      "bookId": 3,
      "borrowTime": "2026-11-05 22:10:00",
      "returnTime": null,
      "userName": "李四",
      "bookName": "Spring Boot实战"
    }
  ]
}
```

### 5.2 指定用户的借阅记录

`GET /api/users/{userId}/borrows`

- 管理员：可以查看任意用户
- 普通用户：只能查看自己的 `userId`

### 5.3 查询单条借阅记录

`GET /api/borrows/{id}`

### 5.4 新增借阅（借书）

`POST /api/borrows`

普通用户请求：

```json
{
  "bookId": 4
}
```

管理员为读者登记借阅：

```json
{
  "userId": 2,
  "bookId": 4
}
```

成功响应：

```json
{
  "code": 200,
  "msg": "借阅成功",
  "data": null
}
```

失败情况：

- 图书不存在
- 库存不足
- 同一用户已借阅该书且未归还

### 5.5 归还图书

`PUT /api/borrows/{id}`

请求体可以为空对象：

```json
{}
```

后端自动写入当前时间作为 `returnTime`，并恢复库存。

成功响应：

```json
{
  "code": 200,
  "msg": "归还成功",
  "data": null
}
```

### 5.6 删除借阅记录（管理员）

`DELETE /api/borrows/{id}`

如果记录尚未归还，删除时会自动恢复库存。

---

## 6. 兼容的旧接口

最初的接口清单使用以下路径，项目仍保留，逻辑与 RESTful 接口一致：

| 旧路径 | 方法 | 对应 RESTful 路径 |
| --- | --- | --- |
| `/user/list` | GET | `/api/users` |
| `/user/{id}` | GET | `/api/users/{id}` |
| `/user/add` | POST | `/api/users` |
| `/user/update` | PUT | `/api/users/{id}` |
| `/user/{id}` | DELETE | `/api/users/{id}` |
| `/user/login` | POST | `/api/auth/login` |
| `/book/list` | GET | `/api/books` |
| `/book/{id}` | GET | `/api/books/{id}` |
| `/book/add` | POST | `/api/books` |
| `/book/update` | PUT | `/api/books/{id}` |
| `/book/{id}` | DELETE | `/api/books/{id}` |
| `/borrow/list` | GET | `/api/borrows` |
| `/borrow/user/{userId}` | GET | `/api/users/{userId}/borrows` |
| `/borrow/{id}` | GET | `/api/borrows/{id}` |
| `/borrow/add` | POST | `/api/borrows` |
| `/borrow/update` | PUT | `/api/borrows/{id}` |
| `/borrow/{id}` | DELETE | `/api/borrows/{id}` |

---

## 7. curl 示例

### 登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 查看图书

```bash
curl http://localhost:8080/api/books \
  -H "Authorization: Bearer <token>"
```

### 借书

```bash
curl -X POST http://localhost:8080/api/borrows \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"bookId":4}'
```

### 归还

```bash
curl -X PUT http://localhost:8080/api/borrows/6 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{}'
```
