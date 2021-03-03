import Button from '@material-ui/core/Button'
import TextField from '@material-ui/core/TextField'
import axios from 'axios'
import React, { useState } from 'react'

interface IUsers {
    currentUser: React.Dispatch<string>
}

const addUser = (text: string, func: React.Dispatch<string>) => {
    func("")
    axios({
        method: "POST",
        url: "http://localhost:3000/users/add",
        data: {
            user: text
        }
    })
}

const Users: React.FC<IUsers> = ({ currentUser }) => {
    const [input, setInput] = useState<string>("")

    return(
        <div id="users-container">
            <div id="user-container">
                <TextField label="user" variant="outlined" value={input} onChange={(evt) => setInput(evt.target.value)}/>
                <Button onClick={() => addUser(input, setInput)}>Add</Button>
                <Button onClick={() => currentUser(input)}>Select</Button>
            </div>
        </div>
    )
}

export default Users