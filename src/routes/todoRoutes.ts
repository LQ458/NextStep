import express from "express";
import { authenticate } from "../utils/auth";
import {
  getTodos,
  createTodo,
  getTodo,
  updateTodo,
  deleteTodo,
  searchTodos,
} from "../controllers/todoController";

const router = express.Router();

// 所有路由都需要认证
router.use(authenticate);

// 获取所有Todo
router.get("/", getTodos);

// 创建Todo
router.post("/", createTodo);

// 获取单个Todo
router.get("/:id", getTodo);

// 更新Todo
router.put("/:id", updateTodo);

// 删除Todo
router.delete("/:id", deleteTodo);

// 搜索Todo
router.get("/search", searchTodos);

export default router;
