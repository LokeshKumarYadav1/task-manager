import { useNavigate } from "react-router-dom"

function Navbar() {

    const name = localStorage.getItem("username")

    const navigate = useNavigate()

    function handleLogout() {

        localStorage.removeItem("token")
        localStorage.removeItem("username")
        
        navigate("/login")

    }

    return(
        <nav className="bg-white shadow-sm border-b">

            <div className="px-4 py-4 flex items-center justify-between">

                <h1 className="text-2xl font-bold text-gray-800">
                    Task Manager
                </h1>

                <p className="text-gray-600">
                    Welcome back, <span className="font-semibold">{name}</span>
                </p>
                
                <button onClick={handleLogout} 
                className="bg-gray-800 text-white px-4 py-2 rounded-lg hover:bg-gray-700 cursor-pointer transition">
                    Logout
                </button>

            </div>

        </nav>
    )
    
}

export default Navbar