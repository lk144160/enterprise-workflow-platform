# RenovationOps

装修业务运营平台，覆盖客户管理、报价、合同与收款、设计交底、施工进度、库存、报销、审批流和经营数据看板。

## 技术栈

- 后端：Java 17、Spring Boot、Spring Security、MyBatis、Redis、MySQL
- 前端：Vue 3、Element Plus、Vite、Pinia
- 工程：Maven 多模块；前端位于 `web-ui/`

## 本地运行

1. 创建 MySQL 数据库 `renovation_ops`。
2. 依次导入 `sql/ry_20260417.sql`、`sql/quartz.sql`、`sql/business_schema.sql`、`sql/business_seed.sql`。
3. 设置必需环境变量 `JWT_SECRET`，并按需设置 `.env.example` 中的数据库、Redis 和上传目录变量。
4. 运行后端：`mvn clean package -DskipTests`，然后启动 `ruoyi-admin` 模块。
5. 运行前端：进入 `web-ui`，执行 `npm install` 和 `npm run dev`。

不要把生产数据库、上传文件、日志、`.env`、FRP 实际配置或打包产物提交到仓库。示例部署配置位于 `deploy/`，使用前必须复制并填写自己的环境值。

## 开源来源与许可

本项目包含基于 [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue) 的修改代码。RuoYi-Vue 使用 MIT License；依许可证要求，原始版权与许可声明完整保留在 [LICENSE](LICENSE) 中，修改说明见 [NOTICE](NOTICE)。

`RenovationOps` 是本仓库使用的独立项目名，并非 RuoYi 官方发行版或官方背书。仓库改名不能消除第三方版权义务；使用者仍应遵守所有依赖和素材各自的许可证。

## 发布前检查

提交前运行：

```bash
git status --short
git grep -n -I -E "(BEGIN .*PRIVATE KEY|password[[:space:]]*[:=][[:space:]]*[^${]|secret[[:space:]]*[:=][[:space:]]*[^${])"
```

项目不包含真实客户、员工、公司、服务器或账号数据。初始化脚本中的账号与联系方式只应使用明确的虚构占位值。
