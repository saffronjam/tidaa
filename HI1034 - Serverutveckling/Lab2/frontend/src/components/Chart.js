import {LineChart, Line, YAxis, XAxis} from 'recharts'
import {useEffect, useState} from "react";

const Chart = ({reports}) => {
    const [data, setData] = useState([])

    useEffect(() => {
        setData(reports.map(i => ({logins: i})))
    }, [reports])

    return (
        <LineChart width={400} height={300} data={data}>
            <Line type="monotone" dataKey="logins" stroke="#8884d8"/>
            <XAxis dataKey="name"/>
            <YAxis/>
        </LineChart>
    );
}

export default Chart;