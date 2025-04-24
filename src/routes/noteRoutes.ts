import express from "express";
import { authenticate } from "../utils/auth";
import {
  getNotes,
  createNote,
  getNote,
  updateNote,
  deleteNote,
  searchNotes,
} from "../controllers/noteController";

const router = express.Router();

// 所有路由都需要认证
router.use(authenticate);

// 获取所有Note
router.get("/", getNotes);

// 创建Note
router.post("/", createNote);

// 获取单个Note
router.get("/:id", getNote);

// 更新Note
router.put("/:id", updateNote);

// 删除Note
router.delete("/:id", deleteNote);

// 搜索Note
router.get("/search", searchNotes);

export default router;
