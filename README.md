# knowledge-hub-api

企业知识库与 AI 应用平台后端项目。

## 环境要求

- JDK 17
- Maven 3.9+
- PostgreSQL 16+
- Navicat

## 构建

```bash
mvn clean test
```

## 启动

```bash
mvn spring-boot:run
```

## 接口调试

启动项目后，在 Apifox 中可以调用以下接口：

```text
POST   /api/users
GET    /api/users/{id}
GET    /api/users?page=0&size=10
PUT    /api/users/{id}
DELETE /api/users/{id}

POST   /api/knowledge-bases
GET    /api/knowledge-bases/{id}
GET    /api/knowledge-bases?page=0&size=10
PUT    /api/knowledge-bases/{id}
DELETE /api/knowledge-bases/{id}

POST   /api/categories
GET    /api/categories/{id}
GET    /api/categories?knowledgeBaseId=1
PUT    /api/categories/{id}
DELETE /api/categories/{id}

POST   /api/tags
GET    /api/tags/{id}
GET    /api/tags?knowledgeBaseId=1
PUT    /api/tags/{id}
DELETE /api/tags/{id}

POST   /api/documents
GET    /api/documents/{id}
GET    /api/documents?knowledgeBaseId=1&page=0&size=10
PUT    /api/documents/{id}
DELETE /api/documents/{id}
```

创建知识库请求体示例：

```json
{
  "code": "tech_docs",
  "name": "技术知识库",
  "description": "开发规范与故障记录",
  "creatorId": 1
}
```

当前正在开发第 2 章，已经完成 JPA 数据源配置、实体关系映射、基础 Repository，以及用户、知识库、分类、标签和文档 CRUD。数据库模型按照章节文档在 Navicat 中创建，Hibernate 使用 `ddl-auto: validate` 校验表结构，不自动建表。
