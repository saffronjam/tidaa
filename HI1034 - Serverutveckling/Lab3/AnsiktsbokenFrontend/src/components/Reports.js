import {useEffect, useState} from "react";
import {Container} from "react-bootstrap";
import Chart from "./Chart";
import {fetch} from "whatwg-fetch";
import {VERTX_MS_IP} from "../Constants";

const fetchReports = (reportId, setReports) => {
    fetch(`${VERTX_MS_IP}/reportSet/${reportId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => {
        if (response.ok) {
            return response.json()
        }
        throw response
    }).then(reponseJson => {
        setReports(reponseJson)
    }).catch(_ => [])
}

const fetchUserLogins = (userId) => {
    return fetch(`${VERTX_MS_IP}/report/${userId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(res => {
            if (res.ok) {
                return res.json()
            }
            throw res
        })
}

const Reports = ({reportsId}) => {
    const [reports, setReports] = useState([])

    useEffect(() => {
        fetchReports(reportsId, setReports)
    }, [reportsId])

    return (
        <Container>
            <Chart reports={reports}/>
        </Container>
    )
}

export {Reports, fetchUserLogins}