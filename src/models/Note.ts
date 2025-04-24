import mongoose from "mongoose";

export interface INote extends mongoose.Document {
  title: string;
  content: string;
  tags: string[];
  userId: mongoose.Types.ObjectId;
  createdAt: Date;
  updatedAt: Date;
}

const noteSchema = new mongoose.Schema(
  {
    title: {
      type: String,
      required: true,
      trim: true,
    },
    content: {
      type: String,
      required: true,
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
noteSchema.index({ userId: 1, tags: 1 });
noteSchema.index({ userId: 1, title: "text", content: "text" });

export const Note = mongoose.model<INote>("Note", noteSchema);
