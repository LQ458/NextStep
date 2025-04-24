import mongoose from "mongoose";

export interface ITodo extends mongoose.Document {
  title: string;
  description?: string;
  completed: boolean;
  dueDate?: Date;
  priority: "low" | "medium" | "high";
  tags: string[];
  userId: mongoose.Types.ObjectId;
  createdAt: Date;
  updatedAt: Date;
}

const todoSchema = new mongoose.Schema(
  {
    title: {
      type: String,
      required: true,
      trim: true,
    },
    description: {
      type: String,
      trim: true,
    },
    completed: {
      type: Boolean,
      default: false,
    },
    dueDate: {
      type: Date,
    },
    priority: {
      type: String,
      enum: ["low", "medium", "high"],
      default: "medium",
    },
    tags: [
      {
        type: String,
        trim: true,
      },
    ],
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
  },
  {
    timestamps: true,
  }
);

// 索引
todoSchema.index({ userId: 1, completed: 1 });
todoSchema.index({ userId: 1, dueDate: 1 });
todoSchema.index({ userId: 1, tags: 1 });

export const Todo = mongoose.model<ITodo>("Todo", todoSchema);
