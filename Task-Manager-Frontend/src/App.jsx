import './App.css'
import Navbar from './component/Navbar'
import TaskForm from './component/TaskForm'
import TaskList from './component/TaskList'
import LoginForm from './component/LoginForm'
import SignupForm from './component/signupForm'
import { Routes, Route } from 'react-router-dom'
import ProtectedRoute from './component/ProtectedRoute'
import { useState } from 'react'

function App() {

  const[refresh, setRefresh] = useState(0);

  function taskCreated() {

    console.log("Task Created Succesfully");

    setRefresh(prev => prev + 1)

  }

  return (

    <Routes>

      <Route path = '' element = {<LoginForm/>}/>

      <Route path = '/login' element = {<LoginForm/>}/>

      <Route path='/signup' element = {<SignupForm/>}/>

      <Route
        path="/tasks"
        element={
        <ProtectedRoute>
        
          <Navbar/>
          <main className="max-w-4xl mx-auto px-4 py-8">
            <TaskForm onTaskCreation={taskCreated}/>
            <TaskList refresh = {refresh}/>
          </main>

        </ProtectedRoute>
        }
      />

    </Routes>

  )
}

export default App;