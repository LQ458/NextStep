import { Request, Response } from "express";
import { User, IUser } from "../models/User";
import { generateToken } from "../utils/auth";

// 注册新用户
export const register = async (req: Request, res: Response) => {
  try {
    const { email, password, name } = req.body;

    // 检查邮箱是否已存在
    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(400).json({ message: "Email already exists" });
    }

    // 创建新用户
    const user = new User({
      email,
      password,
      name,
    });

    await user.save();

    // 生成JWT令牌
    const token = generateToken(user._id.toString());

    res.status(201).json({
      token,
      user: {
        id: user._id,
        email: user.email,
        name: user.name,
      },
    });
  } catch (error) {
    res.status(500).json({ message: "Error creating user" });
  }
};

// 用户登录
export const login = async (req: Request, res: Response) => {
  try {
    const { email, password } = req.body;

    // 查找用户
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(401).json({ message: "Invalid email or password" });
    }

    // 验证密码
    const isMatch = await user.comparePassword(password);
    if (!isMatch) {
      return res.status(401).json({ message: "Invalid email or password" });
    }

    // 生成JWT令牌
    const token = generateToken(user._id.toString());

    res.json({
      token,
      user: {
        id: user._id,
        email: user.email,
        name: user.name,
      },
    });
  } catch (error) {
    res.status(500).json({ message: "Error logging in" });
  }
};

// 获取当前用户信息
export const getCurrentUser = async (req: Request, res: Response) => {
  try {
    const user = await User.findById(req.user?.id).select("-password");
    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }
    res.json(user);
  } catch (error) {
    res.status(500).json({ message: "Error getting user" });
  }
};
