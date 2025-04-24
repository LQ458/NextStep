import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import mongoose from "mongoose";
import authRoutes from "./routes/authRoutes";
import todoRoutes from "./routes/todoRoutes";
import noteRoutes from "./routes/noteRoutes";

// 加载环境变量
dotenv.config();

const app = express();

// 中间件
app.use(cors());
app.use(express.json());

// 数据库连接
mongoose
  .connect(process.env.MONGODB_URI || "mongodb://localhost:27017/nextstep")
  .then(() => console.log("Connected to MongoDB"))
  .catch((err) => console.error("MongoDB connection error:", err));

// 路由
app.use("/api/auth", authRoutes);
app.use("/api/todos", todoRoutes);
app.use("/api/notes", noteRoutes);

app.get("/", (req, res) => {
  res.json({ message: "Welcome to NextStep API" });
});

// 错误处理
app.use(
  (
    err: Error,
    req: express.Request,
    res: express.Response,
    next: express.NextFunction
  ) => {
    console.error(err.stack);
    res.status(500).json({ message: "Something went wrong!" });
  }
);

// 启动服务器
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
