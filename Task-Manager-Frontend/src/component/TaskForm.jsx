import axiosInstance from "../axiosInstance";
import { useState } from "react";

function TaskForm({ onTaskCreation }) {

    const [title, setTitle] = useState("")
    const [description, setDescription] = useState("")
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState("")

    async function handleSubmit(event) {

        event.preventDefault()

        setLoading(true)
        setError("")

            if (title.trim() === "") {
                setError("Task title is required");
                setLoading(false)
                return;
            }

            if (description.trim() === "") {
                setError("Task description is required");
                setLoading(false)
                return;
            }

        try {

            const response = await axiosInstance.post(

                `/task`,
                {
                    title: title,
                    description: description
                }

            )

            console.log("Task Created: ", response.data)

            onTaskCreation();
            setTitle("");
            setDescription("");

        } catch (error) {

            console.log("Error in creating task")

        } finally {

            setLoading(false)

        }

        console.log("Title: ", { title })
        console.log("Description: ", { description })

    }

    return (

        <div className="bg-white rounded-xl shadow-sm border p-6">

            <h2 className="text-xl font-semibold text-shadow-gray-800 mb-6">Create Task</h2>

            <form onSubmit={handleSubmit}>

                <input type="text"
                    placeholder="Enter Task Title"
                    value={title}
                    onChange={(event) => { setTitle(event.target.value) }} 
                    className="border border-gray-300 rounded-lg px-4 py-2 mb-4 focus:outline-none focus:ring-2 focus:ring-blue-500 w-full"
                />

                <br />
                <br />

                <textarea
                    value={description}
                    placeholder="Type task description here..."
                    onChange={(event) => setDescription(event.target.value)}
                    className="w-full border border-gray-300 rounded-lg px-4 py-2 mb-4 min-h-32 resize-y focus:outline-none focus:ring-2 focus:ring-blue-500">
                    
                </textarea>

                <br />

                <button type="submit"
                disabled={loading}
                className="cursor-pointer bg-blue-600 text-white px-5 py-2 rounded-lg font-medium hover:bg-blue-700 transition">{loading ? "Creating Task..." : "Create Task"}</button>

                {error && <p className="text-red-600 text-sm text-center">{error}</p>}

            </form>

        </div>

    )

}

export default TaskForm;