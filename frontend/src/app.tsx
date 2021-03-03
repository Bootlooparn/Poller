import Typography from '@material-ui/core/Typography'
import React, { useState } from 'react'
import ReactDOM from 'react-dom'
import Services from './components/Services'
import Users from './components/Users'

import './stylesheets/app.scss'

export interface IUser {
   user: string
}

const Template = () => {
   const [currentUser, setCurrentUser] = useState<string>("")

   return(
      <div id="container">
         <Typography variant="h3" align="center">Polling service</Typography>
         <Users currentUser={setCurrentUser}/>
         <Services currentUser={currentUser}/>
      </div>
   )
}

ReactDOM.render(<Template/>, document.getElementById("app"))