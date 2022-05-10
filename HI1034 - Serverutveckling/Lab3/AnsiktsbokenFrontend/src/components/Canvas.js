import React, {useRef, useEffect} from 'react';
import '../canvas.css';
import {w3cwebsocket as W3CWebSocket} from "websocket";
import {Card, Container} from "react-bootstrap";
import {CANVAS_MS_WS_IP} from "../Constants";

let client = null
let opened = false

const getMousePos = (canvas, evt) => {
    const rect = canvas.getBoundingClientRect()
    const scaleX = canvas.width / rect.width
    const scaleY = canvas.height / rect.height

    return {
        x: (evt.clientX - rect.left) * scaleX,
        y: (evt.clientY - rect.top) * scaleY
    }
}

const Canvas = ({chatId, style}) => {
    const canvasRef = useRef(null);
    const colorsRef = useRef(null);

    useEffect(() => {
        client = new W3CWebSocket(`${CANVAS_MS_WS_IP}/${chatId}`)

        const canvas = canvasRef.current;
        const context = canvas.getContext('2d');

        const colors = document.getElementsByClassName('color');
        const current = {
            color: 'black',
        };

        const onColorUpdate = (e) => {
            current.color = e.target.className.split(' ')[1];
        };
        for (let i = 0; i < colors.length; i++) {
            colors[i].addEventListener('click', onColorUpdate, false);
        }
        let drawing = false;

        const drawLine = (x0, y0, x1, y1, color, sendToServer) => {
            context.beginPath();
            context.moveTo(x0, y0);
            context.lineTo(x1, y1);
            context.strokeStyle = color;
            context.lineWidth = 2;
            context.stroke();
            context.closePath();

            if (!sendToServer) {
                return;
            }

            // Prepare coordinates and send through websocket
            const w = canvas.width;
            const h = canvas.height;

            const firstCoordNorm = {x: x0 / w, y: y0 / h}
            const secondCoordNorm = {x: x1 / w, y: y1 / h}

            const data = JSON.stringify({
                p0: firstCoordNorm,
                p1: secondCoordNorm,
                color,
            })

            if (opened) {
                client.send(data);
            }
        };

        const onMouseDown = (e) => {
            drawing = true;
            const mousePos = getMousePos(canvas, e)
            current.x = mousePos.x;
            current.y = mousePos.y;
        };

        const onMouseMove = (e) => {
            if (!drawing) {
                return;
            }
            const mousePos = getMousePos(canvas, e)
            drawLine(current.x, current.y, mousePos.x, mousePos.y, current.color, true);
            current.x = mousePos.x;
            current.y = mousePos.y;
        };

        const onMouseUp = (e) => {
            if (!drawing) {
                return;
            }
            drawing = false;
            const mousePos = getMousePos(canvas, e)
            drawLine(current.x, current.y, mousePos.x, mousePos.y, current.color, true);
        };

        const limitUpdate = (callback, delay) => {
            let last = new Date().getTime();
            return function () {
                const time = new Date().getTime();

                if ((time - last) >= delay) {
                    last = time;
                    callback.apply(null, arguments);
                }
            };
        };

        const onResize = () => {
            const data = context.getImageData(0, 0, canvas.width, canvas.height)
            canvas.width = window.innerWidth;
            canvas.height = window.innerHeight;
            context.putImageData(data, 0, 0)
        };

        canvas.addEventListener('mousedown', onMouseDown, false);
        canvas.addEventListener('mouseup', onMouseUp, false);
        canvas.addEventListener('mouseout', onMouseUp, false);
        canvas.addEventListener('mousemove', limitUpdate(onMouseMove, 10), false);
        window.addEventListener('resize', onResize, false);

        onResize();

        client.onmessage = (event) => {
            const incomingData = JSON.parse(event.data);
            if (incomingData.status === 'success') {
                const data = incomingData.data
                const w = canvas.width;
                const h = canvas.height;
                drawLine(data.p0.x * w, data.p0.y * h, data.p1.x * w, data.p1.y * h, data.color);
            }
        }

        client.onopen = () => {
            if (client !== null) {
                client.send(JSON.stringify({msg: 'REQUESTALL'}))
                opened = true
            }
        }

        client.onclose = () => {
            opened = false
        }

        return () => {
            client = null
        }

    }, [chatId]);

    return (
        <Card style={{
            marginTop: '10px',
            marginBottom: '10px',
            position: 'relative'
        }}>
            <canvas ref={canvasRef} className="whiteboard" style={{
                left: '0px',
                right: '0px'
            }}/>

            <Container ref={colorsRef} className="colors" style={{
                position: 'absolute',
                left: '0px',
                right: '0px'
            }}>
                <Container className="color black"/>
                <Container className="color red"/>
                <Container className="color green"/>
                <Container className="color blue"/>
                <Container className="color yellow"/>
            </Container>
        </Card>
    );
};

export default Canvas;