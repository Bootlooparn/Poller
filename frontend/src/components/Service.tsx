import TextField from '@material-ui/core/TextField'
import Button from '@material-ui/core/Button'
import validator from 'validator'
import axios from 'axios'
import React, { useState } from 'react'

import '../stylesheets/service'

interface IService {
    user: string
}

const Service: React.FC<IService> = ({user}) => {
    const [name, setName] = useState<string>("")
    const [url, setUrl] = useState<string>("")

    const addService = () => {

        if(validator.isURL(url)) {
            axios({
                method: "POST",
                baseURL:"http://localhost:3000",
                url:"/services/add",
                data: {
                    name: name,
                    url: url,
                    user: user
                }
            }).then(response => {
                setName("")
                setUrl("")
            })
        } else {
            setName("")
            setUrl("")
            console.log("not a valid url")
        }
    }
    
    const removeService = () => {
        axios({
            method: "DELETE",
            url: "http://localhost:3000/services/delete",
            data: {
                name: name,
                url: url,
                user: user
            }
        }).then(response => {
            setName("")
            setUrl("")
        })
    }

    return(
        <div id="service-container">
            <TextField value={name} onChange={(evt) => setName(evt.target.value)} label="name" variant="outlined"/>
            <TextField value={url} onChange={(evt) => setUrl(evt.target.value)} label="url" variant="outlined"/>
            <Button onClick={() => addService()}>Add</Button>
            <Button onClick={() => removeService()}>Remove</Button>
        </div>
    )
}

export default Service