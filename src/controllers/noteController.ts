import { Request, Response } from "express";
import { Note, INote } from "../models/Note";

// 获取所有Note
export const getNotes = async (req: Request, res: Response) => {
  try {
    const notes = await Note.find({ userId: req.user?.id }).sort({
      updatedAt: -1,
    });
    res.json(notes);
  } catch (error) {
    res.status(500).json({ message: "Error getting notes" });
  }
};

// 创建Note
export const createNote = async (req: Request, res: Response) => {
  try {
    const { title, content, tags } = req.body;

    const note = new Note({
      title,
      content,
      tags,
      userId: req.user?.id,
    });

    await note.save();
    res.status(201).json(note);
  } catch (error) {
    res.status(500).json({ message: "Error creating note" });
  }
};

// 获取单个Note
export const getNote = async (req: Request, res: Response) => {
  try {
    const note = await Note.findOne({
      _id: req.params.id,
      userId: req.user?.id,
    });

    if (!note) {
      return res.status(404).json({ message: "Note not found" });
    }

    res.json(note);
  } catch (error) {
    res.status(500).json({ message: "Error getting note" });
  }
};

// 更新Note
export const updateNote = async (req: Request, res: Response) => {
  try {
    const { title, content, tags } = req.body;

    const note = await Note.findOneAndUpdate(
      { _id: req.params.id, userId: req.user?.id },
      { title, content, tags },
      { new: true }
    );

    if (!note) {
      return res.status(404).json({ message: "Note not found" });
    }

    res.json(note);
  } catch (error) {
    res.status(500).json({ message: "Error updating note" });
  }
};

// 删除Note
export const deleteNote = async (req: Request, res: Response) => {
  try {
    const note = await Note.findOneAndDelete({
      _id: req.params.id,
      userId: req.user?.id,
    });

    if (!note) {
      return res.status(404).json({ message: "Note not found" });
    }

    res.json({ message: "Note deleted" });
  } catch (error) {
    res.status(500).json({ message: "Error deleting note" });
  }
};

// 搜索Note
export const searchNotes = async (req: Request, res: Response) => {
  try {
    const { query } = req.query;

    const notes = await Note.find({
      userId: req.user?.id,
      $text: { $search: query as string },
    }).sort({ score: { $meta: "textScore" } });

    res.json(notes);
  } catch (error) {
    res.status(500).json({ message: "Error searching notes" });
  }
};
