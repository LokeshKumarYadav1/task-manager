import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

function SignupForm() {

    const[name, setName] = useState("")
    const[username, setUsername] = useState("")
    const[email, setEmail] = useState("")
    const[password, setPassword] = useState("");
    const[error, setError] = useState("")
    const[loading, setLoading] = useState(false)
    const navigate = useNavigate()

    async function handleSignup(event) {

        event.preventDefault();

        setError("");

        if (name.trim() === "") {
            setError("Name is required");
            return;
        }

        if (!email.includes("@")) {
            setError("Enter a valid email");
            return;
        }

        if (username.length < 5 || username.length > 10) {
            setError("Username must be between 5 and 10 characters");
            return;
        }

        if (password.length < 6 || password.length > 15) {
            setError("Password must be between 6 and 15 characters");
            return;
        }

        setLoading(true)

        try{

            const response = await axios.post('http://localhost:8080/user/signup',

                {

                    name: name,
                    username: username,
                    email: email,
                    password: password

                }

            )

            console.log("Signup successful: ", response.data)

            navigate("/login");

        } catch(error) {

            console.log("Error during signup: ", error)

            setError(error.response?.data?.Message || "Something went wrong")

        } finally {

            setLoading(false)

        }


    }

    return(

        <div className="min-h-screen bg-gray-100 flex items-center justify-center px-4">

            <div className="bg-white w-full max-w-md rounded-2xl shadow-lg p-8">

                <div>

                    <h1 className="text-3xl font-bold text-gray-800">Task Manager</h1>

                    <p className="text-gray-500 mt-2"> Create your account and start managing your tasks. </p>

                </div>

                <form onSubmit={handleSignup} className="flex flex-col gap-4">

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>

                        <input type="text" 
                        placeholder="Enter name"
                        value={name}
                        onChange={(event) => setName(event.target.value)}
                        className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"/>

                    </div>

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                       
                        <input type="email" 
                        placeholder="Enter email"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                        className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"/>  

                    </div>

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>

                        <input type="text" 
                        placeholder="Enter username"
                        value={username}
                        onChange={(event) => setUsername(event.target.value)}
                        className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"/>

                    </div>

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                        
                        <input type="password" 
                        placeholder="Enter password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"/>

                    </div>

                    {error && (<p className="text-red-600 text-sm">{error}</p>)}

                    <button type="submit" 
                    disabled={loading}
                    className="bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 transition cursor-pointer mt-3">{loading ? "Signing in..." : "Signup"}</button>

                </form>

                <div className="text-center mt-6">

                    <p className="text-sm text-gray-500">
                        Already have an account?
                    </p>

                    <button type="button" onClick={() => navigate("/login")} className="text-blue-600 font-medium hover:underline cursor-pointer mt-1">Login now</button>

                </div>

            </div>

        </div>
    )

}

export default SignupForm;