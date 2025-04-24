import { Request, Response } from 'express';
import { Todo, ITodo } from '../models/Todo';

// 获取所有Todo
export const getTodos = async (req: Request, res: Response) => {
  try {
    const todos = await Todo.find({ userId: req.user?.id })
      .sort({ createdAt: -1 });
    res.json(todos);
  } catch (error) {
    res.status(500).json({ message: 'Error getting todos' });
  }
};

// 创建Todo
export const createTodo = async (req: Request, res: Response) => {
  try {
    const { title, description, dueDate, priority, tags } = req.body;
    
    const todo = new Todo({
      title,
      description,
      dueDate,
      priority,
      tags,
      userId: req.user?.id
    });

    await todo.save();
    res.status(201).json(todo);
  } catch (error) {
    res.status(500).json({ message: 'Error creating todo' });
  }
};

// 获取单个Todo
export const getTodo = async (req: Request, res: Response) => {
  try {
    const todo = await Todo.findOne({
      _id: req.params.id,
      userId: req.user?.id
    });

    if (!todo) {
      return res.status(404).json({ message: 'Todo not found' });
    }

    res.json(todo);
  } catch (error) {
    res.status(500).json({ message: 'Error getting todo' });
  }
};

// 更新Todo
export const updateTodo = async (req: Request, res: Response) => {
  try {
    const { title, description, completed, dueDate, priority, tags } = req.body;
    
    const todo = await Todo.findOneAndUpdate(
      { _id: req.params.id, userId: req.user?.id },
      { title, description, completed, dueDate, priority, tags },
      { new: true }
    );

    if (!todo) {
      return res.status(404).json({ message: 'Todo not found' });
    }

    res.json(todo);
  } catch (error) {
    res.status(500).json({ message: 'Error updating todo' });
  }
};

// 删除Todo
export const deleteTodo = async (req: Request, res: Response) => {
  try {
    const todo = await Todo.findOneAndDelete({
      _id: req.params.id,
      userId: req.user?.id
    });

    if (!todo) {
      return res.status(404).json({ message: 'Todo not found' });
    }

    res.json({ message: 'Todo deleted' });
  } catch (error) {
    res.status(500).json({ message: 'Error deleting todo' });
  }
};

// 搜索Todo
export const searchTodos = async (req: Request, res: Response) => {
  try {
    const { query } = req.query;
    
    const todos = await Todo.find({
      userId: req.user?.id,
      $or: [
        { title: { $regex: query, $options: 'i' } },
        { description: { $regex: query, $options: 'i' } },
        { tags: { $regex: query, $options: 'i' } }
      ]
    }).sort({ createdAt: -1 });

    res.json(todos);
  } catch (error) {
    res.status(500).json({ message: 'Error searching todos' });
  }
}; 