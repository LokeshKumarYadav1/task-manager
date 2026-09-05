import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

function LoginForm() {

    const[username, setUsername] = useState("")
    const[password, setPassword] = useState("")
    const[error, setError] = useState("")
    const[loading, setLoading] = useState(false)
    const navigate = useNavigate()

    async function handleSubmit(event) {

        event.preventDefault();
        setError("")

        if (username.trim() === "") {

            setError("Username is required");
            return;

        } 

        if (password.trim() === "") {

            setError("Password is required");
            return;

        }

        setLoading(true)

        try{

            const response = await axios.post("http://localhost:8080/user/login",
                {

                    username: username,
                    password: password

                }
            )

            localStorage.setItem("token", response.data.token)
            localStorage.setItem("username", response.data.username)
            
            navigate("/tasks")

            console.log("Logged in successfully ", response.data)

        } catch(error) {

            setError(error.response?.data?.Message || "Something went wrong")

        } finally {

            setLoading(false)

        }

    }

    return(
        <div className="min-h-screen bg-gray-100 flex items-center justify-center px-4">

            <div className="bg-white w-full max-w-md rounded-2xl shadow-lg p-8">

                <div>

                    <h1 className="text-3xl font-bold text-gray-800"> Task Manager </h1>

                    <p className="text-gray-500 mt-2"> Welcome back! Login to continue. </p>

                </div>

                <form onSubmit={handleSubmit} className="flex flex-col">

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>

                        <input type="text" 
                        placeholder="Enter your username"
                        value={username}
                        onChange={(event) => setUsername(event.target.value)}
                        className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"/>

                    </div>

                    <div>
                        
                        <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>

                        <input type="password" 
                        placeholder="Password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"/>

                    </div>

                    {error && (<p className="text-red-600 text-sm text-center"> {error} </p> )}

                    <button type="submit" 
                    disabled={loading}
                    className="bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 transition cursor-pointer mt-3">{loading ? "Logging in..." : "Login"}</button>

                </form>

                <div className="text-center mt-6">
                    <p className="text-sm text-gray-500"> Don't have an account? </p>

                    <button
                    type="button"
                    onClick={() => navigate("/signup")}
                    className="text-blue-600 font-medium hover:underline cursor-pointer mt-1"> Create an account </button>

                </div>

            </div>

        </div>
    )

}

export default LoginForm;