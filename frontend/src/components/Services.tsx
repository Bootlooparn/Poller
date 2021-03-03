import Paper from '@material-ui/core/Paper'
import Table from '@material-ui/core/Table'
import TableBody from '@material-ui/core/TableBody'
import TableCell from '@material-ui/core/TableCell'
import TableContainer from '@material-ui/core/TableContainer'
import TableHead from '@material-ui/core/TableHead'
import TableRow from '@material-ui/core/TableRow'
import React, { useEffect, useState } from 'react'
import axios from 'axios'

import '../stylesheets/services'
import Service from './Service'

interface IServices {
    currentUser: string
}

interface service {
    name: string,
    url: string,
    added: string,
    changed: string,
    status: string
}

const Services: React.FC<IServices> = ({ currentUser }) => {
    const [services, setServices] = useState<[service]>([{name: "", url: "", added: "", changed: "", status: ""}])

    useEffect(() => {
        axios.get("http://localhost:3000/services/" + currentUser)
        .then(response => {
            setServices(response.data)
        }).catch(e => console.log(e));
    })

    return (
        <div id="services-container">
            <Service user={currentUser} />
            <TableContainer id="services-table" component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell align="center">Name</TableCell>
                            <TableCell align="center">Url</TableCell>
                            <TableCell align="center">Added</TableCell>
                            <TableCell align="center">Changed</TableCell>
                            <TableCell align="center">Status</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {
                            services.map(row => {
                                return <TableRow key={row.name}>
                                    <TableCell align="center">{row.name}</TableCell>
                                    <TableCell align="center">{row.url}</TableCell>
                                    <TableCell align="center">{row.added}</TableCell>
                                    <TableCell align="center">{row.changed}</TableCell>
                                    <TableCell align="center">{row.status}</TableCell>
                                </TableRow>
                            })
                        }
                    </TableBody>
                </Table>
            </TableContainer>
        </div>
    )
}

export default Services