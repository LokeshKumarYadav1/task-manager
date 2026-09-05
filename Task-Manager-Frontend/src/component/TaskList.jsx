import { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";

function TaskList({ refresh }) {

    const [tasks, setTasks] = useState([])
    const [editingTaskId, setEditingTaskId] = useState(null)
    const [deleteLoadingId, setDeleteLoadingId] = useState(null)
    const [saveTaskLoadingId, setSaveTaskLoadingId] = useState(null)
    const [editTitle, setEditTitle] = useState("")
    const [editDescription, setEditDescription] = useState("")
    const [taskLoading, setTaskLoading] = useState(false)
    const [taskError, setTaskError] = useState("");
    const [taskDeletionErrorId, setTaskDeletionErrorId] = useState(null)
    const [taskCompletionErrorId, setTaskCompletionErrorId] = useState(null)
    const [taskEditErrorId, setTaskEditErrorId] = useState(null)

    function handleEditing(id) {

        const task = tasks.find((task) => task.id === id)

        if (task.completed) {

            alert("Completed task can't be edited")
            return

        }

        setEditingTaskId(id);

        setEditTitle(task.title)

        setEditDescription(task.description);

    }

    async function saveTask(id) {

        setTaskEditErrorId(null)

        if (editTitle.trim() === "") {
            setTaskEditErrorId(id)
            return
        }

        if (editDescription.trim() === "") {
            setTaskEditErrorId(id)
            return
        }

        setSaveTaskLoadingId(id)

        const task = tasks.find((task) => task.id === id);

        try {

            const response = await axiosInstance.put(
                `/task/${id}`,
                {

                    title: editTitle,
                    description: editDescription,
                    completed: task.completed

                }

            )

            setTasks((currentTasks) =>
                currentTasks.map((task) =>
                    task.id === id ? response.data : task
                )
            );

            setEditingTaskId(null)
            setEditTitle("")
            setEditDescription("")

        } catch (error) {

            console.log("Error updating task: ", error)
            setTaskEditErrorId(id)

        } finally {

            setSaveTaskLoadingId(null)

        }

    }

    async function handleTaskCompletion(task) {

        setTaskCompletionErrorId(null)

        try {

            const response = await axiosInstance.patch(

                `/task/${task.id}`,
                {
                    completed: !task.completed
                }

            )

            setTasks((currentTasks) => currentTasks.map((currentTask) => currentTask.id === task.id ? response.data : currentTask))

        } catch(error) {

            console.log("Error in task completion", error)
            setTaskCompletionErrorId(task.id)

        }
        
    }


    async function handleTaskDeletion(id) {

        const task = tasks.find((task) => task.id === id)
 
        if (!task.completed) {

            alert("Can't delete uncomplete task")
            return

        }

        const confirmed = window.confirm("Are you sure you want to delete this task?");

        if (!confirmed) {
            return;
        }

        setDeleteLoadingId(id)
        setTaskDeletionErrorId(null)

        try {

            await axiosInstance.delete(`/task/${id}`)

            console.log("Task deleted successfully")

            setTasks((currentTasks) => currentTasks.filter((task) => task.id !== id))

        } catch (error) {

            console.log("Error in task deletion: ", error);
            setTaskDeletionErrorId(id);

        } finally {
            setDeleteLoadingId(null)
        }

    }

    useEffect(() => {

        async function fetchTask() {

            setTaskLoading(true)
            setTaskError("")

            try {

                const response = await axiosInstance.get("/task")

                setTasks(response.data)

            } catch (error) {

                console.log("Error fetching tasks: ", error);
                
                setTaskError("Couldn't load tasks")

            } finally {

                setTaskLoading(false)

            }

        }

        fetchTask();

    }, [refresh])


    return (
        <div className="mt-8">

            <h2 className="text-2xl font-semibold text-gray-800 mb-4">My Tasks</h2>

            {
                taskLoading ? (<p>Loading tasks...</p>) : taskError ? <p>Couldn't load tasks</p> : tasks.length === 0 ? (<p>No tasks yet...</p>) : (tasks.map((task) => (

                    <div key={task.id}
                    className="bg-white rounded-xl shadow-sm border p-5 mb-4 flex flex-col sm:flex-row sm:items-center gap-4">

                        <input
                            type="checkbox"
                            checked={task.completed}
                            onChange={() => handleTaskCompletion(task)}
                            className="w-5 h-5"
                        />

                        <h3 className="text-lg font-semibold text-gray-800">{task.title}</h3>

                        <p className="text-gray-600 mt-2">{task.description}</p>

                        <p className={`inline-block mt-4 px-3 py-y rounded-full text-sm font-medium ${task.completed ? "bg-green-100 textgreen-700" : "bg-yellow-100 text-yellow-700"} `}>Status: {task.completed ? "Completed" : "Pending"}</p>

                        <div className="flex flex-wrap gap-3 mt-5"> 

                            <button onClick={() => { handleEditing(task.id) }}
                            className="cursor-pointer px-4 py-2 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-100 transition">Edit</button>

                            <button onClick={() => { handleTaskDeletion(task.id) }}
                            disabled={deleteLoadingId === task.id}
                            className="cursor-pointer px-4 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700 transition">{deleteLoadingId === task.id ? "Deleting..." : "Delete"}</button>

                        </div>

                       

                        {
                            editingTaskId === task.id && (

                                <div className="mt-2">

                                    <input
                                        value={editTitle}
                                        placeholder="New title"
                                        onChange={(event) => setEditTitle(event.target.value)}
                                        className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    />
                                    <textarea
                                        value={editDescription}
                                        placeholder="New description"
                                        onChange={(event) => setEditDescription(event.target.value)} 
                                        className="w-full border border-gray-300 rounded-lg px-4 py-2 mt-3 min-h-32 resize-y focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    />
                                    
                                    <div className="flex gap-3 mt-4">

                                        {task.id === taskEditErrorId && (
                                            <p className="text-red-600 text-sm">
                                                Both fields required
                                            </p>
                                        )}

                                        <button onClick={() => {saveTask(task.id)}}
                                            disabled={saveTaskLoadingId === task.id || task.completed}
                                            className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition cursor-pointer">{saveTaskLoadingId === task.id ? "Saving..." : "Save"}
                                        </button>

                                        <button onClick={() => {
                                                setEditingTaskId(null)
                                                setEditTitle("")
                                                setEditDescription("")
                                                setTaskEditErrorId(null)
                                            }}
                                            className="px-4 py-2 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-100 transition cursor-pointer">Cancel
                                        </button>

                                    </div>
                                    
                                </div>

                            )
                        }

                        {task.id === taskDeletionErrorId && (<p className="text-red-600 text-sm">Couldn't delete task</p>)} 
                        {task.id === taskCompletionErrorId && (<p className="text-red-600 text-sm">Action couldn't be completed</p>)}

                    </div>

                )))
            }

        </div>
    )
}

export default TaskList;