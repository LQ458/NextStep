# NextStep

一个极简主义的任务管理和笔记应用。

## 功能特性

- 任务管理
  - 创建、编辑、删除任务
  - 设置优先级和截止日期
  - 添加标签
  - 搜索和过滤
- 笔记功能

  - 创建、编辑、删除笔记
  - Markdown 支持
  - 添加标签
  - 搜索和过滤

- 通用功能
  - 用户认证
  - 数据同步
  - 标签管理
  - 主题切换
  - 离线支持

## 技术栈

- 后端: Node.js + Express + TypeScript
- 数据库: MongoDB
- 前端:
  - Android: Kotlin + Material Design
  - macOS: Swift + Material Design

## 开发环境设置

1. 克隆仓库

```bash
git clone https://github.com/yourusername/NextStep.git
cd NextStep
```

2. 安装依赖

```bash
pnpm install
```

3. 配置环境变量

```bash
cp src/.env.example src/.env
# 编辑 .env 文件设置你的配置
```

4. 启动开发服务器

```bash
pnpm dev
```

## 项目结构

```
NextStep/
├── backend/                # 后端代码
│   ├── src/
│   │   ├── controllers/    # 控制器
│   │   ├── models/        # 数据模型
│   │   ├── routes/        # 路由
│   │   ├── services/      # 业务逻辑
│   │   └── utils/         # 工具函数
│   └── package.json
├── android/               # Android应用
├── macos/                # macOS应用
└── README.md
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情
