text.Notepad {textPane,actions} noflow UndoTask
	where UndoTask = {c | c.message.contains("Undo")}